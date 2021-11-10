/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemjavaseclient;

import java.util.Date;
import java.util.Scanner;
import ws.client.FailedLoginException;
import ws.client.FailedLoginException_Exception;
import ws.client.HotelReservationWebService_Service;
import ws.client.Partner;
import ws.client.PartnerNotFoundException;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.PartnerReservation;
import ws.client.ReservationLineItem;
import ws.client.ReservationLineItemNotFoundException;
import ws.client.ReservationLineItemNotFoundException_Exception;
import ws.client.RoomType;

/**
 *
 * @author 65912
 */
public class MainApp {

    //Partner currentPartner;
    Scanner scanner = new Scanner(System.in);
    Partner currentPartner;

    public MainApp() {

    }

    public void runApp() {
        HotelReservationWebService_Service service = new HotelReservationWebService_Service();
        Integer response = 0;

        while (true) {
            System.out.println("\n*** Welcome to Hotel Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Search Room");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        menuMain();

                    } catch (FailedLoginException_Exception ex) {
                        System.out.println("");
                        System.out.println("Invalid login credential \n");
                    } catch (PartnerNotFoundException_Exception ex) {
                        System.out.println("");
                        System.out.println("Partner not found \n");
                    }
                    //} else if (response == 2) {
                    // registerAsGuest();
                    // menuMain();

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

    public void doLogin() throws FailedLoginException_Exception, PartnerNotFoundException_Exception {
        HotelReservationWebService_Service service = new HotelReservationWebService_Service();
        String username;
        String password;

        System.out.println("\n*** Holiday Reservation System :: Partner Login ***\n");

        System.out.println("Enter username> ");
        username = scanner.next().trim();

        System.out.println("Enter password> ");
        password = scanner.next().trim();

        if (username.length() > 0 && password.length() > 0) {

            currentPartner = partnerLogin(username, password);
            System.out.println("Partner Login successful !\n");

        }
    }

    public void menuMain() {
        Integer response = 0;

        while (true) {
            System.out.println("\n*** Welcome to Hotel Reservation System ***\n");
            System.out.println("1: Search Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {

                } else if (response == 2) {
                    try {
                        viewMyReservationDetails();
                    } catch (ReservationLineItemNotFoundException_Exception ex) {
                        System.out.println("Enter a valid reservation Id!");
                    }

                } else if (response == 3) {
                    try {
                        viewAllMyReservations();
                    } catch (PartnerNotFoundException_Exception ex) {
                        System.out.println("");
                        System.out.println("Partner not found \n");
                    }

                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again! ");
                }
            }
            if (response == 4) {
                break;
            }
        }
    }

    private void viewMyReservationDetails() throws ReservationLineItemNotFoundException_Exception {
        System.out.println("\n *** Hotel Reservation System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Integer rId = scanner.nextInt();
        Long reservationId = rId.longValue();

        ReservationLineItem lineItem = findReservationLineItemById(reservationId);
        System.out.println("--------------------------------------------");
        System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
        System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
        System.out.println("Amount: " + lineItem.getAmount().toString());
        System.out.println("--------------------------------------------");
    }

    private void viewAllMyReservations() throws PartnerNotFoundException_Exception {
        Partner p;
        int i = 1;
        p = findPartnerById(currentPartner.getPartnerId());
        if (!p.getPartnerReservations().isEmpty()) {
            for (PartnerReservation pr : p.getPartnerReservations()) {
                for (ReservationLineItem lineItem : pr.getReservationLineItems()) {
                    System.out.println("--------------------------------------------");
                    System.out.println("Reservation " + i + ": ");
                    System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                    System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                    System.out.println("Amount: " + lineItem.getAmount().toString());
                    System.out.println("--------------------------------------------");
                    i++;
                }
            }
        } else {
            System.out.println("You have no Reservations \n");
        }

    }

    private Partner partnerLogin(String username, String password) throws FailedLoginException_Exception, PartnerNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.partnerLogin(username, password);

    }

    private ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.findReservationLineItemById(reservationLineItemId);
    }

    private Partner findPartnerById(Long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.findPartnerById(partnerId);
    }
    
    private Integer walkInSearchRoom(RoomType roomType, javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.walkInSearchRoom(roomType, arg0, arg1);
    }
}