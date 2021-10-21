/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import javax.ejb.EJB;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;

/**
 *
 * @author 65912
 */
public class Main {

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    

    
    public static void main(String[] args) throws FailedLoginException, GuestNotFoundException {
       MainApp mainApp = new MainApp(guestSessionBeanRemote);
       mainApp.runApp();
    }
    
}
