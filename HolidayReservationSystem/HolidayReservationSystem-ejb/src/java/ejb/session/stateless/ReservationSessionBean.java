/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ajayan
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal; 
    
    private RoomRecordSessionBeanLocal roomRecordSessionBeanLocal; 
    
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal; 
    
    

    public ReservationSessionBean() {
    }
    
    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) {
        return new ReservationLineItem(); 
    }
    

    public List<ReservationLineItem> findReservationLineItemByRoomType(Long roomTypeId) {
        return new ArrayList<ReservationLineItem>(); 
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
