/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.ReservationLineItem;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    public List<ReservationLineItem> findReservationLineItemByRoomType(Long roomTypeId) {
        return new ArrayList<ReservationLineItem>();
    }

    
    @Override
    public RoomRecord walkInSearch(RoomType roomType, Date checkIn, Date checkOut) {
        for(RoomRecord r : roomType.getRoomRecords()) {
            if(r.getRoomStatus().equals("available")) {
                if(availableForBooking(r.getReservationLineItem().getCheckInDate(), r.getReservationLineItem().getCheckOutDate(), 
                        checkIn, checkOut)) {
                    return r;
                }
            }
        }
        return null;
    }
    
    @Override
    public BigDecimal walkInPrice(RoomType roomType, Date checkInDate,Date checkOutDate) {
        Long amount = new Long(0);
        RoomType rt = em.find(RoomType.class, roomType);
        for(RoomRate rr : rt.getRoomRates()) {
            if(rr.getRoomRateType().equals(PUBLISHED)) {
                BigDecimal price = rr.getRatePerNight();
                if(checkOutDate.getTime()!= checkInDate.getTime()) {
                Long daysBetween = checkOutDate.getTime() - checkInDate.getTime();
                amount = price.longValue() * daysBetween;
                } else {
                    amount = price.longValue();
                }
            }
        }
        return BigDecimal.valueOf(amount.longValue());
    }
    
    
    @Override
    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut) {
        return !(startDate.after(checkIn) || endDate.before(checkOut));
    }
    
    @Override
    public WalkInReservation createWalkInReservation(WalkInReservation w, Long eId) {
        
    } 
}
