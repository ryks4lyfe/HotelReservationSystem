/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException {
        ReservationLineItem r = em.find(ReservationLineItem.class, reservationLineItemId);
        if (r != null) {
            return r;
        } else {
            throw new ReservationLineItemNotFoundException("Error, Guest " + reservationLineItemId + " does not exist.");
        }
    }

    @Override
    public Integer walkInSearchRoom(RoomType roomType, Date checkIn, Date checkOut) {
        Integer numOfRooms = roomType.getRoomRecords().size();
        Date currentDate = new Date();

        for (ReservationLineItem lineItem : roomType.getLineItems()) {
            if (!availableForBooking(lineItem.getCheckInDate(), lineItem.getCheckOutDate(), checkIn, checkOut)) {
                numOfRooms--;
            }
        }

        if (currentDate == checkIn) {
            for (RoomRecord room : roomType.getRoomRecords()) {
                if (!room.getRoomStatus().equalsIgnoreCase("available")) {
                    numOfRooms--;
                }
            }
        }
        return numOfRooms;
    }

    @Override
    public BigDecimal walkInPrice(RoomType roomType, Date checkInDate, Date checkOutDate) {
        Long amount = new Long(0);
        RoomType rt = em.find(RoomType.class, roomType);
        for (RoomRate rr : rt.getRoomRates()) {
            if (rr.getRoomRateType().equals(PUBLISHED)) {
                BigDecimal price = rr.getRatePerNight();
                if (checkOutDate.getTime() != checkInDate.getTime()) {
                    Long daysBetween = checkOutDate.getTime() - checkInDate.getTime();
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
        RoomType rt = em.find(RoomType.class, roomType);
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
            
            for(RoomRate peak : peakRates) {
                if(current.after(peak.getStartRateDate()) && current.before(peak.getEndRateDate())
                        || current.equals((peak.getEndRateDate())) 
                        || current.after(peak.getStartRateDate())) {
                    ratePerDay = peak.getRatePerNight().longValue();
                }
            }
            
            for(RoomRate promo : promoRates) {
                if(current.after(promo.getStartRateDate()) && current.before(promo.getEndRateDate())
                        || current.equals((promo.getEndRateDate())) 
                        || current.after(promo.getStartRateDate())) {
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

}
