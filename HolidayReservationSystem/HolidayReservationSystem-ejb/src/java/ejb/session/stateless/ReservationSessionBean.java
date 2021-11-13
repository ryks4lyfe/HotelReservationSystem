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
import util.exception.NoAvailableRoomException;
import util.exception.PartnerNotFoundException;
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

    @EJB
    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote;

    @EJB
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    public ReservationSessionBean() {
    }

    @Override
    public ReservationLineItem createLineItem(Date checkInDate, Date checkOutDate, BigDecimal amount, RoomType roomType) {
        return new ReservationLineItem(checkInDate, checkOutDate, amount, roomType);
    }

    @Override
    public void doCheckout(Partner partner, Integer totalLineItems, BigDecimal totalAmount, List<ReservationLineItem> lineItems) {
        List<ReservationLineItem> items = new ArrayList<>();
        for (ReservationLineItem i : lineItems) {
            items.add(em.find(ReservationLineItem.class, i.getReservationLineItemId()));
        }
        PartnerReservation p = new PartnerReservation(partner, new Date(), totalLineItems, totalAmount, items);

        em.persist(p);
        Partner p1 = em.find(Partner.class, partner.getPartnerId());

        p1.getPartnerReservations().add(p);
        p.setPartner(p1);
        em.flush();
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
    public ReservationLineItem findReservationLineItemOfPartner(Long reservationLineItemId, Long partnerId) throws ReservationLineItemNotFoundException {
        Partner p = em.find(Partner.class, partnerId);
        for (PartnerReservation pr : p.getPartnerReservations()) {
            for (ReservationLineItem items : pr.getReservationLineItems()) {
                if (items.getReservationLineItemId().equals(reservationLineItemId)) {
                    return items;
                }
            }
        }
        throw new ReservationLineItemNotFoundException("Error, Reservation Id: " + reservationLineItemId + " does not exist.");
    }

    @Override
    public List<ReservationLineItem> findListOfReservationLineItemsByCheckInDate(Date checkInDate) throws ReservationLineItemNotFoundException {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r WHERE r.checkInDate = :inCheckInDate");
        query.setParameter("inCheckInDate", checkInDate);

        if (query.getResultList() != null) {
            return query.getResultList();
        } else {
            throw new ReservationLineItemNotFoundException("Reservation Line Item with Check In Date " + checkInDate + " cannot be found!");
        }
    }

    @Override
    public List<ReservationLineItem> findListOfReservationLineItemsByCheckOutDate(Date checkOutDate) throws ReservationLineItemNotFoundException {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r WHERE r.checkOutDate = :inCheckOutDate");
        query.setParameter("inCheckOutDate", checkOutDate);

        if (query.getResultList() != null) {
            return query.getResultList();
        } else {
            throw new ReservationLineItemNotFoundException("Reservation Line Item with Check In Date " + checkOutDate + " cannot be found!");
        }
    }

    @Override
    public List<ReservationLineItem> findAllReservationLineItems() {
        Query query = em.createQuery("SELECT r FROM ReservationLineItem r");
        return query.getResultList();
    }

    @Override
    public Integer walkInSearchRoom(RoomType room, Date checkIn, Date checkOut) {
        RoomType roomType = em.find(RoomType.class, room.getRoomTypeId());
        Integer numOfRooms = roomType.getRoomRecords().size();
        Date currentDate = new Date();

        for (ReservationLineItem lineItem : roomType.getLineItems()) {
            if (!(checkIn.after(lineItem.getCheckOutDate()) || checkOut.before(lineItem.getCheckInDate()))) {
                numOfRooms--;
            }
        }

        if (currentDate == checkIn) {
            for (RoomRecord r : roomType.getRoomRecords()) {
                //add status
                if (!r.getRoomStatus().equalsIgnoreCase("available")
                        || !r.getRoomStatus().equalsIgnoreCase("occupied but available")) {
                    numOfRooms--;
                }
            }
        }
        return numOfRooms;
    }

    @Override
    public Integer searchRoom(Date checkIn, Date checkOut) {
        Integer numOfRooms = roomRecordSessionBeanRemote.findAllRoomRecords().size();

        List<RoomType> roomtypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();

        Date currentDate = new Date();

        for (RoomType rt : roomtypes) {
            for (ReservationLineItem lineItem : rt.getLineItems()) {
                if (!(checkIn.after(lineItem.getCheckOutDate()) || checkOut.before(lineItem.getCheckInDate()))) {
                    numOfRooms--;
                }
            }
        }

        if (currentDate == checkIn) {
            for (RoomType rt : roomtypes) {
                for (RoomRecord room : rt.getRoomRecords()) {
                    if (!room.getRoomStatus().equalsIgnoreCase("available")
                            || !room.getRoomStatus().equalsIgnoreCase("occupied but available")) {
                        numOfRooms--;
                    }
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
    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut) {
        return !(startDate.after(checkIn) || endDate.before(checkOut));
    }

    @Override
    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport) {
        em.persist(exceptionReport);
        em.flush();
        return exceptionReport;
    }

    @Override
    public ExceptionReport updateExceptionReport(Long exceptionReportId, String report) {
        ExceptionReport exceptionReport = em.find(ExceptionReport.class, exceptionReportId);

        if (exceptionReport != null) {
            List<String> reports = exceptionReport.getReports();
            reports.add(report);
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

    @Override
    public void roomAllocationsForToday(Date todaysDate) throws ReservationLineItemNotFoundException {

        List<RoomRecord> newlyReservedRoomRecords = new ArrayList<>();

        List<RoomRecord> roomsAvailableForToday = roomRecordSessionBeanRemote.findAllAvailableRoomRecords();

        ExceptionReport exceptionReport = new ExceptionReport();

        List<ReservationLineItem> reservationLineItemsCheckInToday = findListOfReservationLineItemsByCheckInDate(todaysDate);

        List<ReservationLineItem> reservationLineItemsCheckOutToday = findListOfReservationLineItemsByCheckOutDate(todaysDate);

        List<ReservationLineItem> lineItemsToRemove = new ArrayList<>();

        List<ReservationLineItem> reservationLineItemsReserved = new ArrayList<>();

        Long roomId = new Long(0);

        for (ReservationLineItem reservationLineItem : reservationLineItemsCheckOutToday) {
            if (reservationLineItem.getRoom() != null) {
                RoomRecord occupiedButAvailRoomRecord = reservationLineItem.getRoom();
                occupiedButAvailRoomRecord.setRoomStatus("occupied but available");
                roomsAvailableForToday.add(occupiedButAvailRoomRecord);
            }
        }

        for (ReservationLineItem reservationLineItemReserved : reservationLineItemsCheckInToday) {
            if (reservationLineItemReserved.getRoom() != null) {
                if (reservationLineItemReserved.getRoom().getRoomStatus().equals("reserved and ready")
                        || reservationLineItemReserved.getRoom().getRoomStatus().equals("reserved and not ready")) {
                    reservationLineItemsReserved.add(reservationLineItemReserved);
                }
            }

        }

        reservationLineItemsCheckInToday.removeAll(reservationLineItemsReserved);

        for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
            for (RoomRecord availableRoom : roomsAvailableForToday) {
                if (reservationLineItemCheckIn.getRoomType().getRoomTypeId().equals(availableRoom.getRoomType().getRoomTypeId())
                        && reservationLineItemCheckIn.getRoom() == null) {

                    if (availableRoom.getRoomStatus().equals("available")) {
                        RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                        r1.setRoomStatus("reserved and ready");
                        availableRoom.setRoomStatus("reserved and ready");
                        roomId = r1.getRoomRecordId();

                        System.out.println(r1.getRoomStatus());
                        System.out.println("balls");

                        reservationLineItemCheckIn.setRoom(r1);
                        lineItemsToRemove.add(reservationLineItemCheckIn);

                    } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                        RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                        r1.setRoomStatus("reserved and ready");
                        availableRoom.setRoomStatus("reserved and not ready");

                        reservationLineItemCheckIn.setRoom(availableRoom);
                        lineItemsToRemove.add(reservationLineItemCheckIn);

                    }
                }
            }
        }

        reservationLineItemsCheckInToday.removeAll(lineItemsToRemove);
        lineItemsToRemove.clear();

        if (!reservationLineItemsCheckInToday.isEmpty()) {
            //ranking the rooms remaining in terms of rank, such that as much as possible rooms higher but of the closest rank will be allocated first
            reservationLineItemsCheckInToday.sort(new RankComparator());
            roomsAvailableForToday.sort(new RankComparatorRooms());
            for (RoomRecord roomTypeNum : roomsAvailableForToday) {
                System.out.println(roomTypeNum.getRoomType());
            }
            for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
                for (RoomRecord availableRoom : roomsAvailableForToday) {
                    if (reservationLineItemCheckIn.getRoom() == null) {
                        if (Integer.parseInt(reservationLineItemCheckIn.getRoomType().getRankRoom()) > Integer.parseInt(availableRoom.getRoomType().getRankRoom())) {
                            if (availableRoom.getRoomStatus().equals("available")) {
                                RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                                r1.setRoomStatus("reserved and ready");
                                availableRoom.setRoomStatus("reserved and ready");
                                reservationLineItemCheckIn.setRoom(r1);
                                reservationLineItemCheckIn.setRoomType(r1.getRoomType());
                                lineItemsToRemove.add(reservationLineItemCheckIn);

                            } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                                RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                                r1.setRoomStatus("reserved and ready");
                                availableRoom.setRoomStatus("reserved and not ready");
                                reservationLineItemCheckIn.setRoom(availableRoom);
                                lineItemsToRemove.add(reservationLineItemCheckIn);

                            }

                            String reportDescription = " Type 1: There was no available room for room type reserved for Room Reservation with Id " + reservationLineItemCheckIn.getReservationLineItemId()
                                    + ". Hence upgraded to the next highest room type; room allocated is Room " + availableRoom.getRoomNum();

                            System.out.println(reportDescription);

                            em.persist(exceptionReport);
                            em.flush();
                            ExceptionReport exceptionReport1 = em.find(ExceptionReport.class, exceptionReport.getExceptionReportId());

                            updateExceptionReport(exceptionReport1.getExceptionReportId(), reportDescription);
                        }
                    }
                }
            }
        }

        reservationLineItemsCheckInToday.removeAll(lineItemsToRemove);
        lineItemsToRemove.clear();

        if (!reservationLineItemsCheckInToday.isEmpty()) {
            for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
                String reportDescription = "Type 2: There was no available room for room type reserved,  and no upgrade available for Room Reservation with ID " + reservationLineItemCheckIn.getReservationLineItemId() + ". No room was allocated";
                em.persist(exceptionReport);
                em.flush();
                updateExceptionReport(exceptionReport.getExceptionReportId(), reportDescription);
            }

        } // return newlyReservedRoomRecords; 
    }

    @Override
    //@Schedule(persistent = false, hour = "2")
    public void roomAllocationsForToday() throws ReservationLineItemNotFoundException {
        Date todaysDate = new Date();

        List<RoomRecord> newlyReservedRoomRecords = new ArrayList<>();

        List<RoomRecord> roomsAvailableForToday = roomRecordSessionBeanRemote.findAllAvailableRoomRecords();

        ExceptionReport exceptionReport = new ExceptionReport();

        List<ReservationLineItem> reservationLineItemsCheckInToday = findListOfReservationLineItemsByCheckInDate(todaysDate);

        List<ReservationLineItem> reservationLineItemsCheckOutToday = findListOfReservationLineItemsByCheckOutDate(todaysDate);

        List<ReservationLineItem> lineItemsToRemove = new ArrayList<>();

        List<ReservationLineItem> reservationLineItemsReserved = new ArrayList<>();

        Long roomId = new Long(0);

        for (ReservationLineItem reservationLineItem : reservationLineItemsCheckOutToday) {
            if (reservationLineItem.getRoom() != null) {
                RoomRecord occupiedButAvailRoomRecord = reservationLineItem.getRoom();
                occupiedButAvailRoomRecord.setRoomStatus("occupied but available");
                roomsAvailableForToday.add(occupiedButAvailRoomRecord);
            }
        }

        for (ReservationLineItem reservationLineItemReserved : reservationLineItemsCheckInToday) {
            if (reservationLineItemReserved.getRoom() != null) {
                if (reservationLineItemReserved.getRoom().getRoomStatus().equals("reserved and ready")
                        || reservationLineItemReserved.getRoom().getRoomStatus().equals("reserved and not ready")) {
                    reservationLineItemsReserved.add(reservationLineItemReserved);
                }
            }

        }

        reservationLineItemsCheckInToday.removeAll(reservationLineItemsReserved);

        for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
            for (RoomRecord availableRoom : roomsAvailableForToday) {
                if (reservationLineItemCheckIn.getRoomType().getRoomTypeId().equals(availableRoom.getRoomType().getRoomTypeId())
                        && reservationLineItemCheckIn.getRoom() == null) {

                    if (availableRoom.getRoomStatus().equals("available")) {
                        RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                        r1.setRoomStatus("reserved and ready");
                        availableRoom.setRoomStatus("reserved and ready");
                        roomId = r1.getRoomRecordId();

                        System.out.println(r1.getRoomStatus());
                        System.out.println("balls");

                        reservationLineItemCheckIn.setRoom(r1);
                        lineItemsToRemove.add(reservationLineItemCheckIn);

                    } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                        RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                        r1.setRoomStatus("reserved and ready");
                        availableRoom.setRoomStatus("reserved and not ready");

                        reservationLineItemCheckIn.setRoom(availableRoom);
                        lineItemsToRemove.add(reservationLineItemCheckIn);

                    }
                }
            }
        }

        reservationLineItemsCheckInToday.removeAll(lineItemsToRemove);
        lineItemsToRemove.clear();

        if (!reservationLineItemsCheckInToday.isEmpty()) {
            //ranking the rooms remaining in terms of rank, such that as much as possible rooms higher but of the closest rank will be allocated first
            reservationLineItemsCheckInToday.sort(new RankComparator());
            roomsAvailableForToday.sort(new RankComparatorRooms());
            for (RoomRecord roomTypeNum : roomsAvailableForToday) {
                System.out.println(roomTypeNum.getRoomType());
            }
            for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
                for (RoomRecord availableRoom : roomsAvailableForToday) {
                    if (reservationLineItemCheckIn.getRoom() == null) {
                        if (Integer.parseInt(reservationLineItemCheckIn.getRoomType().getRankRoom()) > Integer.parseInt(availableRoom.getRoomType().getRankRoom())) {
                            if (availableRoom.getRoomStatus().equals("available")) {
                                RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                                r1.setRoomStatus("reserved and ready");
                                availableRoom.setRoomStatus("reserved and ready");
                                reservationLineItemCheckIn.setRoom(r1);
                                reservationLineItemCheckIn.setRoomType(r1.getRoomType());
                                lineItemsToRemove.add(reservationLineItemCheckIn);

                            } else if (availableRoom.getRoomStatus().equals("occupied but available")) {
                                RoomRecord r1 = em.find(RoomRecord.class, availableRoom.getRoomRecordId());
                                r1.setRoomStatus("reserved and ready");
                                availableRoom.setRoomStatus("reserved and not ready");
                                reservationLineItemCheckIn.setRoom(availableRoom);
                                lineItemsToRemove.add(reservationLineItemCheckIn);

                            }

                            String reportDescription = " Type 1: There was no available room for room type reserved for Room Reservation with Id " + reservationLineItemCheckIn.getReservationLineItemId()
                                    + ". Hence upgraded to the next highest room type; room allocated is Room " + availableRoom.getRoomNum();

                            System.out.println(reportDescription);

                            em.persist(exceptionReport);
                            em.flush();
                            ExceptionReport exceptionReport1 = em.find(ExceptionReport.class, exceptionReport.getExceptionReportId());

                            updateExceptionReport(exceptionReport1.getExceptionReportId(), reportDescription);
                        }
                    }
                }
            }
        }

        reservationLineItemsCheckInToday.removeAll(lineItemsToRemove);
        lineItemsToRemove.clear();

        if (!reservationLineItemsCheckInToday.isEmpty()) {
            for (ReservationLineItem reservationLineItemCheckIn : reservationLineItemsCheckInToday) {
                String reportDescription = "Type 2: There was no available room for room type reserved,  and no upgrade available for Room Reservation with ID " + reservationLineItemCheckIn.getReservationLineItemId() + ". No room was allocated";
                em.persist(exceptionReport);
                em.flush();
                updateExceptionReport(exceptionReport.getExceptionReportId(), reportDescription);
            }

        } // return newlyReservedRoomRecords; 
    }

}
