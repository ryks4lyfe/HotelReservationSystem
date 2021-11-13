/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.statefull.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomRecord;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import javax.ejb.EJB;

/**
 *
 * @author 65912
 */
public class Main {

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
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
   
    public static void main(String[] args)  {
        List<RoomRecord> r =  roomRecordSessionBean.findAllAvailableRoomRecords();
        for(RoomRecord rr : r) {
            System.out.println(rr.getRoomRecordId());
        }
        try {
             //allocate room to guests reservations at 2am daily
            Date currentDate = new Date();
            String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
            
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter.parse(modifiedDate + " 02:00:00");
            
            Timer timer = new Timer();
           
        } catch (ParseException ex) {
            System.out.println("invalid date format");
        }
        MainApp mainApp = new MainApp(partnerSessionBeanRemote, employeeSessionBeanRemote,
                guestSessionBeanRemote, reservationSessionBean, roomRateSessionBean,
                roomRecordSessionBean, roomTypeSessionBean, walkInReservationSessionBeanRemote);
        mainApp.runApp();
        System.exit(0);
  
         
    }
    
}
