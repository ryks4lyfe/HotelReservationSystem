/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ajayan
 */
@Local
public interface ReservationSessionBeanLocal {

    public List<ReservationLineItem> findReservationLineItemByRoomType(Long roomTypeId);

    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId);
    
}
