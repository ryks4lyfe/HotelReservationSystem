/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author 65912
 */
public class Main {

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    

    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(partnerSessionBeanRemote, employeeSessionBeanRemote,guestSessionBeanRemote);
        mainApp.runApp();
        
        //Instantiate Timer
    }
    
}
