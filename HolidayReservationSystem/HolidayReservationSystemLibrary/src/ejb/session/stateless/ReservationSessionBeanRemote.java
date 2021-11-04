/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import entity.RoomRecord;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ReservationLineItemNotFoundException;

/**
 *
 * @author ajayan
 */
@Remote
public interface ReservationSessionBeanRemote {
   public List<ReservationLineItem> findReservationLineItemByRoomType(Long roomTypeId);

    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException;
    
    public RoomRecord walkInSearch(RoomType roomType, Date checkIn, Date checkOut);

    public BigDecimal walkInPrice(RoomType roomType, Date checkInDate, Date checkOutDate);

    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut);

    public WalkInReservation createWalkInReservation(WalkInReservation w, Long eId);
}
