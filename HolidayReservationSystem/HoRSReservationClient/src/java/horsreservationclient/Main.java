/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.statefull.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.ReservationLineItem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author 65912
 */
public class Main {

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBean;
    
    @EJB
    private static WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;

    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;

    @EJB
    private static RoomRecordSessionBeanRemote roomRecordSessionBean;

    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBean;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    public static void main(String[] args) throws FailedLoginException, GuestNotFoundException, RoomTypeNotFoundException {

        try {
            List<ReservationLineItem> r = new ArrayList<>();
            r.add(reservationSessionBean.findReservationLineItemById(new Long(15)));
            r.add(reservationSessionBean.findReservationLineItemById(new Long(17)));
            partnerSessionBean.removeAllItemsFromCart(r);
            
            MainApp mainApp = new MainApp(guestSessionBeanRemote, reservationSessionBean,
                    roomRateSessionBean, roomRecordSessionBean, roomTypeSessionBean, walkInReservationSessionBeanRemote);
            mainApp.runApp();
        } catch (ReservationLineItemNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
