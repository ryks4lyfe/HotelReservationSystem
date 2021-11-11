package ejb.session.stateless;

import classes.RankComparator;
import classes.RankComparatorRooms;
import entity.ExceptionReport;
import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static util.enumeration.RoomRateTypeEnum.NORMAL;
import static util.enumeration.RoomRateTypeEnum.PEAK;
import static util.enumeration.RoomRateTypeEnum.PROMOTION;
import static util.enumeration.RoomRateTypeEnum.PUBLISHED;
import util.exception.ReservationLineItemNotFoundException;

/**
 *
 * @author ajayan
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote;

    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    public ReservationSessionBean() {
    }
    
    @Override
    public ReservationLineItem createLineItem(Date checkInDate, Date checkOutDate, BigDecimal amount, RoomType roomType) {
        return new ReservationLineItem(checkInDate, checkOutDate, amount, roomType);
    }

    @Override
    public PartnerReservation doCheckout(Partner partner, Integer totalLineItems, BigDecimal totalAmount, List<ReservationLineItem> lineItems) {
        PartnerReservation p = new PartnerReservation(partner, new Date(), totalLineItems, totalAmount);
        p.setReservationLineItems(lineItems);
        em.persist(p);
        Partner p1 = em.find(Partner.class, partner.getPartnerId());

        p1.getPartnerReservations().add(p);
        p.setPartner(p1);
        em.flush();

        return p;
    }

    @Override
    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException {
        ReservationLineItem r = em.find(ReservationLineItem.class, reservationLineItemId);
        if (r != null) {
            return r;
        } else {
            throw new ReservationLineItemNotFoundException("Error, Guest " + reservationLineItemId + " does not exist.");
        }
    }
    
    @Override
    public List<ReservationLineItem> findListOfReservationLineItemsByCheckInDate(Date checkInDate) throws ReservationLineItemNotFoundException 
    {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r WHERE r.checkInDate = :inCheckInDate");
        query.setParameter("inCheckInDate", checkInDate);
        
        if (query.getResultList() != null) 
        {
            return query.getResultList();
        }
        else throw new ReservationLineItemNotFoundException("Reservation Line Item with Check In Date " + checkInDate + " cannot be found!");  
    }
    
    @Override
    public List<ReservationLineItem> findListOfReservationLineItemsByCheckOutDate(Date checkOutDate) throws ReservationLineItemNotFoundException 
    {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r WHERE r.checkOutDate = :inCheckOutDate");
        query.setParameter("inCheckOutDate", checkOutDate);
        
        if (query.getResultList() != null) 
        {
            return query.getResultList();
        }
        else throw new ReservationLineItemNotFoundException("Reservation Line Item with Check In Date " + checkOutDate + " cannot be found!");  
    }
    
    @Override
    public List<ReservationLineItem> findAllReservationLineItems() 
    {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r"); 
        return query.getResultList(); 
    }

    @Override
    public Integer walkInSearchRoom(RoomType roomType, Date checkIn, Date checkOut) {
        Integer numOfRooms = roomType.getRoomRecords().size();
        Date currentDate = new Date();

        for (ReservationLineItem lineItem : roomType.getLineItems()) {
            if (!(checkIn.after(lineItem.getCheckOutDate()) || checkOut.before(lineItem.getCheckInDate()))) {
                numOfRooms--;
            }
        }

        if (currentDate == checkIn) {
            for (RoomRecord room : roomType.getRoomRecords()) {
                if (!room.getRoomStatus().equalsIgnoreCase("not in use")) {
                    numOfRooms--;
                }
            }
        }
        return numOfRooms;
    }

    @Override
    public BigDecimal walkInPrice(RoomType roomType, Date checkInDate, Date checkOutDate) {
        Long amount = new Long(0);
        RoomType rt = em.find(RoomType.class,
                roomType.getRoomTypeId());
        for (RoomRate rr : rt.getRoomRates()) {
            if (rr.getRoomRateType().equals(PUBLISHED)) {
                BigDecimal price = rr.getRatePerNight();
                if (checkOutDate.getTime() != checkInDate.getTime()) {
                    Long daysBetween = (checkOutDate.getTime() - checkInDate.getTime()) / 86400000;
                    amount = price.longValue() * daysBetween;
                } else {
                    amount = price.longValue();
                }
            }
        }
        return BigDecimal.valueOf(amount);
    }

    @Override
    public BigDecimal reservationPrice(RoomType roomType, Date checkInDate, Date checkOutDate) {
        Long amount = new Long(0);
        RoomType rt = em.find(RoomType.class,
                roomType.getRoomTypeId());
        RoomRate normalRate = new RoomRate();
        List<RoomRate> promoRates = new ArrayList<>();
        List<RoomRate> peakRates = new ArrayList<>();

        for (RoomRate rr : rt.getRoomRates()) {
            if (rr.getRoomRateType().equals(NORMAL)) {
                normalRate = rr;
            } else if (rr.getRoomRateType().equals(PEAK)) {
                peakRates.add(rr);
            } else if (rr.getRoomRateType().equals(PROMOTION)) {
                promoRates.add(rr);
            }
        }

        Date current = checkInDate;

        while (current.before(checkOutDate)) {
            Long ratePerDay = normalRate.getRatePerNight().longValue();

            for (RoomRate peak : peakRates) {
                //
                if (!(current.after(peak.getEndRateDate()) || current.before(peak.getStartRateDate()))) {
                    ratePerDay = peak.getRatePerNight().longValue();
                }
            }

            for (RoomRate promo : promoRates) {
                if (!(current.after(promo.getEndRateDate()) || current.before(promo.getStartRateDate()))) {
                    ratePerDay = promo.getRatePerNight().longValue();
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();

            amount += ratePerDay;
        }

        return BigDecimal.valueOf(amount);
    }

    @Override
    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut) 
    {
        return !(startDate.after(checkIn) || endDate.before(checkOut));
    }
    
    @Override
    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport) {
        em.persist(exceptionReport); 
        em.flush();
        return exceptionReport; 
    } 
    
    @Override
    public ExceptionReport updateExceptionReport (Long exceptionReportId, String report) {
        ExceptionReport exceptionReport = em.find(ExceptionReport.class, exceptionReportId); 
        
        if (exceptionReport != null) {
            List<String> reports = exceptionReport.getReports(); 
            reports.add(report) ; 
            exceptionReport.setReports(reports); 
        }
        
        return exceptionReport; 
    }
    
    @Override
    public ExceptionReport findExceptionReport(Long exceptionReportId) {
        ExceptionReport exceptionReport = em.find(ExceptionReport.class, exceptionReportId);
        
        return exceptionReport; 
    }
    
    @Override
    public List<ExceptionReport> viewAllExceptionReports() {
        Query query = em.createQuery("SELECT er FROM ExceptionReport er"); 
        return query.getResultList(); 
    }
    
  /*  @Schedule(hour = "13")
    @Override
    public void updateRoomStatusForReservations(RoomRecord roomToCheckOut) {
        
        
        if (roomToCheckOut.getRoomStatus().equals("unavailable1")) {
        roomToCheckOut.setRoomStatus("available");
        } else if (roomToCheckOut.getRoomStatus().equals("unavailable2")) {
            roomToCheckOut.setRoomStatus("reserved and ready");
        }
    }*/
    
    @Override
    //@Schedule(persistent = false, hour = "2")
    public void roomAllocationsForToday() throws ReservationLineItemNotFoundException 
    {
       Date todaysDate = new Date(); 
       List<RoomRecord> newlyReservedRoomRecords  = new ArrayList<>(); 
       List<RoomRecord> roomsAvailableForToday = roomRecordSessionBeanRemote.findAllAvailableRoomRecords(); 
       ExceptionReport exceptionReport = new ExceptionReport(); 
       
       //blls
       List<ReservationLineItem> reservationLineItemsCheckInToday = findListOfReservationLineItemsByCheckInDate(todaysDate); 
       
       List<ReservationLineItem> reservationLineItemsCheckOutToday = findListOfReservationLineItemsByCheckOutDate(todaysDate); 
       
       for (ReservationLineItem reservationLineItem : reservationLineItemsCheckOutToday) 
       {
           RoomRecord occupiedButAvailRoomRecord = reservationLineItem.getRoom(); 
           occupiedButAvailRoomRecord.setRoomStatus("occupied but available");
           roomsAvailableForToday.add(occupiedButAvailRoomRecord); 
       }
       
       //first one to match all the exact room types of available rooms to reservation line items for today check in
       //subsequently remove the available room from available list and add this room to newlyReservedRooms
       for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday)
       {
           for (RoomRecord availableRoom : roomsAvailableForToday) 
           {
               if (reservationLineItemCheckIn.getRoomType().getTypeName().equals(availableRoom.getRoomType().getTypeName()))
               {
                   if (availableRoom.getRoomStatus().equals("available")){
                   availableRoom.setRoomStatus("reserved and ready");
                   } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                       availableRoom.setRoomStatus("reserved and not ready");
                   }
                   reservationLineItemCheckIn.setRoom(availableRoom);
                   //dk whether set the room to the reservationLineItem
                   newlyReservedRoomRecords.add(availableRoom); 
                   
                   roomsAvailableForToday.remove(availableRoom);
                   reservationLineItemsCheckInToday.remove(reservationLineItemCheckIn); 
                  
               }
           }
       }
       
       reservationLineItemsCheckInToday.sort(new RankComparator()); 
       roomsAvailableForToday.sort(new RankComparatorRooms());
       
       for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday)
       {
           for (RoomRecord availableRoom : roomsAvailableForToday)
           {
               if (Integer.parseInt(reservationLineItemCheckIn.getRoomType().getRankRoom()) > Integer.parseInt(availableRoom.getRoomType().getRankRoom()))
               {
                   if (availableRoom.getRoomStatus().equals("available")){
                   availableRoom.setRoomStatus("reserved and ready");
                   } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                       availableRoom.setRoomStatus("reserved and not ready");
                   }
                   reservationLineItemCheckIn.setRoom(availableRoom);
                   newlyReservedRoomRecords.add(availableRoom); 
                   roomsAvailableForToday.remove(availableRoom); 
                   
                   String reportDescription = "There was no available room for room type reserved for Room Reservation with Id " + reservationLineItemCheckIn.getReservationLineItemId() + 
                                              ". Hence upgraded to the next highest room type; room allocated is Room " + availableRoom.getRoomNum(); 
                   
                   updateExceptionReport(exceptionReport.getExceptionReportId(), reportDescription); 
               }
           }
       }
       
       for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday)
       {
           String reportDescription = "There was no available room for room type reserved,  and no upgrade available for Room Reservation with ID " + reservationLineItemCheckIn.getReservationLineItemId() + ". No room was allocated"; 
                   
           updateExceptionReport(exceptionReport.getExceptionReportId(), reportDescription); 
       }
       
       
      // return newlyReservedRoomRecords; 
       
    }

}
