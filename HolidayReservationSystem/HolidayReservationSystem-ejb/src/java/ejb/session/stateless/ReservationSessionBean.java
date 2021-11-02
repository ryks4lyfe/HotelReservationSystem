/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import entity.RoomRecord;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public List<ReservationLineItem> findReservationLineItemByRoomType(Long roomTypeId) {
        return new ArrayList<ReservationLineItem>();
    }

    
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
    
    
    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut) {
        return !(startDate.after(checkIn) || endDate.before(checkOut));
    }
}
