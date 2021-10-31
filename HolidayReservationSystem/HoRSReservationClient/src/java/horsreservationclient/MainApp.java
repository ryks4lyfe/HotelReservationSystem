/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author 65912
 */
public class MainApp {

    private GuestSessionBeanRemote guestSessionBeanRemote; 
    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote; 
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote; 
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote; 
    private ReservationSessionBeanRemote reservationSessionBeanRemote; 

    Guest currentGuest;
    Scanner scanner=new Scanner(System.in);

    public MainApp() {

    }

   
    public MainApp(Guest currentGuest) {
        this.currentGuest = currentGuest;
    }

    MainApp(GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, RoomRecordSessionBeanRemote roomRecordSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean) 
    {
       this.guestSessionBeanRemote = guestSessionBeanRemote; 
       this.reservationSessionBeanRemote = reservationSessionBeanRemote; 
       this.roomRateSessionBeanRemote = roomRateSessionBeanRemote; 
       this.roomRecordSessionBeanRemote = roomRecordSessionBeanRemote; 
       this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote; 
    }
    
    
    
    public void runApp() throws FailedLoginException, GuestNotFoundException,  RoomTypeNotFoundException {
        
        Integer response = 0;

        while (true) {
            System.out.println("\n*** Welcome to HoRS Hotel Reservation System ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        menuMain();

                    } catch (FailedLoginException ex) {
                        System.out.println("");
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    } catch (GuestNotFoundException ex) {
                        System.out.println("");
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    registerAsGuest();
                    menuMain();

                } else if (response == 3) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again! ");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void doLogin() throws FailedLoginException, GuestNotFoundException {
       
        String email;
        String password;

        System.out.println("\n*** HoRS System :: Guest Login ***\n");

        System.out.println("Enter email> ");
        email = scanner.next().trim();

        System.out.println("Enter password> ");
        password = scanner.next().trim();

        if (email.length() > 0 && password.length() > 0) {
            try {
                currentGuest = guestSessionBeanRemote.doLogin(email, password);
                System.out.println("Guest Login successful !\n");
            } catch (FailedLoginException ex) {
                throw new FailedLoginException("Error, login credentials are incorret!");
            } catch (GuestNotFoundException ex) {
                throw new GuestNotFoundException("Error, Guest with the email '" + email + "' does not exist.");
            }
        }
    }
    
    private void registerAsGuest() {
        Guest newGuest = new Guest();
        
        System.out.println("\n*** HoRS System :: Register As Guest ***\n");
        
        System.out.println("Enter name >");
        newGuest.setName(scanner.next().trim());
        
        System.out.println("Enter email > ");
        newGuest.setEmail(scanner.next().trim());
        
        System.out.println("Enter password > ");
        newGuest.setPassword(scanner.next().trim());
        
        System.out.println("Enter Phone Number > ");
        newGuest.setPhoneNum(scanner.next().trim());
        
        System.out.println("Enter Passport Number > ");
        newGuest.setPassportNum(scanner.next().trim());
        
        Long guestId = guestSessionBeanRemote.createGuest(newGuest);
        
        System.out.println("Visitor registered as guest "+guestId+" successfully! ");
    }

    
    public void menuMain() {
        
    }

}