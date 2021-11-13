/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.statefull.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;
import util.exception.ReservationLineItemNotFoundException;
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
    private WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;

    Guest currentGuest;
    Scanner scanner = new Scanner(System.in);

    public MainApp() {

    }

    public MainApp(Guest currentGuest) {
        this.currentGuest = currentGuest;
    }

    MainApp(GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBean,
            RoomRateSessionBeanRemote roomRateSessionBean, RoomRecordSessionBeanRemote roomRecordSessionBean,
            RoomTypeSessionBeanRemote roomTypeSessionBean, WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBean;
        this.roomRateSessionBeanRemote = roomRateSessionBean;
        this.roomRecordSessionBeanRemote = roomRecordSessionBean;
        this.roomTypeSessionBeanRemote = roomTypeSessionBean;
        this.walkInReservationSessionBeanRemote = walkInReservationSessionBeanRemote;
    }

    public void runApp() throws FailedLoginException, GuestNotFoundException, RoomTypeNotFoundException {

        List<RoomType> rooms = roomTypeSessionBeanRemote.retrieveAllRoomTypes();

        Integer response = 0;

        while (true) {
            System.out.println("\n*** Welcome to HoRS Hotel Reservation System ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
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
        currentGuest = newGuest;

        System.out.println("Visitor registered as guest " + guestId + " successfully! ");
    }

    public void menuMain() {
        Integer response = 0;

        while (true) {
            System.out.println("\n*** Welcome Guest: " + currentGuest.getName() + "***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservations Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    searchHotel();

                } else if (response == 2) {
                    viewMyReservationDetails();

                } else if (response == 3) {
                    viewAllMyReservations();

                } else if (response == 4) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again! ");
                }

            }

        }
    }

    public void searchHotel() {
        try {
            Scanner scanner = new Scanner(System.in);
            Date checkInDate;
            Date checkOutDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            boolean continueReservation = true;
            boolean emptyRooms = false;

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out Date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine());

            while (continueReservation == true) {
                Integer numOfRooms = 0;
                List<BigDecimal> availableRates = new ArrayList<>();
                List<RoomType> enabledRooms = new ArrayList<>();

                //Check each room type for number of available rooms 
                if (reservationSessionBeanRemote.searchRoom(checkInDate, checkOutDate) > 0) {

                    numOfRooms = reservationSessionBeanRemote.searchRoom(checkInDate, checkOutDate);
                    if (numOfRooms - 1 != 0) {
                        for (RoomType rt : roomTypeSessionBeanRemote.retrieveAllRoomTypes()) {
                            availableRates.add(reservationSessionBeanRemote.walkInPrice(rt, checkInDate, checkOutDate));
                            enabledRooms.add(rt);
                        }

                        //For each avaialble roomType, display the room record and rate details
                        for (int i = 0; i < enabledRooms.size(); i++) {
                            RoomType rt = enabledRooms.get(i);
                            BigDecimal price = availableRates.get(i);
                            System.out.println("");
                            int i2 = i + 1;
                            System.out.println("Option " + i2);
                            //System.out.println("Rooms Left: " + numOfRooms.get(i));
                            System.out.println("Room Type: " + rt.getTypeName());
                            System.out.println("Room Size: " + rt.getSize());
                            System.out.println("Bed Number: " + rt.getBed());
                            System.out.println("Amenities: " + rt.getAmenities());
                            System.out.println("Capacity: " + rt.getCapacity());
                            System.out.println("");
                            System.out.println("Room Cost: " + price);
                            System.out.println("-------------------------------------------");
                            System.out.println("");

                        }

                        //The chosen room will be instantiated as a line item and added into the list
                        System.out.println("Enter option for reservation : ");
                        Integer option = scanner.nextInt();

                        if (option < 1 || option > enabledRooms.size()) {
                            System.out.println("Please input a proper option");
                        } else {

                            for (ReservationLineItem lineItem : walkInReservationSessionBeanRemote.getLineItems()) {
                                System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                                System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                                System.out.println("Amount: " + lineItem.getAmount().toString());
                                System.out.println("RoomType: " + lineItem.getRoomType().getTypeName());
                            }
                            System.out.println("Cart Cost: " + walkInReservationSessionBeanRemote.addItem(new ReservationLineItem(checkInDate, checkOutDate,
                                    availableRates.get(option - 1),
                                    enabledRooms.get(option - 1))));
                            System.out.println("Cart Items: " + walkInReservationSessionBeanRemote.getTotalLineItems());
                        }

                        System.out.println("--------------------------------------------");
                        System.out.println(numOfRooms + " Rooms Left");
                        System.out.println("1: Add more items");
                        System.out.println("2: Checkout");
                        System.out.println("3: Quit");
                        Integer response = scanner.nextInt();

                        if (response == 1) {
                            //Continue while loop to add more item
                            continueReservation = true;
                        } else if (response == 2) {
                            //If cart not empty, proceed to checkout and end loop
                            if (!walkInReservationSessionBeanRemote.getLineItems().isEmpty()) {
                                try {
                                    reservationSessionBeanRemote.roomAllocationsForToday();
                                } catch (ReservationLineItemNotFoundException ex) {
                                    System.out.println("reservation was not found!");
                                }
                                /*List<ReservationLineItem> lineItems = walkInReservationBeanRemote.getLineItems(); 
                            for (ReservationLineItem lineItem : lineItems)
                            {
                                if (lineItem.getCheckInDate().equals(new Date())) 
                                {
                                    
                                }
                            }*/
                                walkInReservationSessionBeanRemote.doCheckout(currentGuest);
                                continueReservation = false;
                                walkInReservationSessionBeanRemote.resetCart();
                                System.out.println("Checkout Completed");
                            } else {
                                //Cart empty, continue while loop to add more items
                                System.out.println("Cart is Empty, please add items");
                                continueReservation = true;
                            }
                        } else if (response == 3) {
                            //Quit, remove all item from cart and disassociate
                            continueReservation = false;
                            walkInReservationSessionBeanRemote.removeAllItemsFromCart();
                        }

                    } else {
                        System.out.println("--------------------------------------------");
                        System.out.println("No more rooms left to book");
                        System.out.println("1: Checkout");
                        System.out.println("2: Quit");
                        Integer response = scanner.nextInt();

                        if (response == 1) {
                            if (!walkInReservationSessionBeanRemote.getLineItems().isEmpty()) {
                                try {
                                    reservationSessionBeanRemote.roomAllocationsForToday();
                                } catch (ReservationLineItemNotFoundException ex) {
                                    System.out.println("reservation was not found!");
                                }
                                walkInReservationSessionBeanRemote.doCheckout(currentGuest);
                                continueReservation = false;
                                walkInReservationSessionBeanRemote.resetCart();
                                System.out.println("CheckOut Completed");
                            } else {
                                //Cart empty, continue while loop to add more items
                                System.out.println("Cart is Empty, please add items");
                                continueReservation = true;
                            }
                        } else if (response == 2) {
                            //Quit, remove all item from cart and disassociate
                            continueReservation = false;
                            walkInReservationSessionBeanRemote.removeAllItemsFromCart();
                        }

                    }

                } else {
                    System.out.println("");
                    System.out.println("No more rooms left");
                    System.out.println("-------------------------------------------");
                }

            }

        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    private void viewMyReservationDetails() {
        System.out.println("\n *** HoRS System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Integer rId = scanner.nextInt();
        Long reservationId = rId.longValue();

        boolean contains = false;

        try {
            Guest g = guestSessionBeanRemote.findGuestById(currentGuest.getGuestId());
            if (!g.getOnlineReservations().isEmpty()) {
                for (OnlineReservation or : g.getOnlineReservations()) {
                    for (ReservationLineItem lineItem : or.getReservationLineItems()) {
                        if (lineItem.getReservationLineItemId().equals(reservationId)) {
                            contains = true;
                            System.out.println("");
                            System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                            System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                            System.out.println("Amount: " + lineItem.getAmount().toString());
                            System.out.println("RoomType: " + lineItem.getRoomType().getTypeName());
                            System.out.println("--------------------------------------------");
                        }
                    }
                }
            }

            if (contains == false) {
                System.out.println("Guest does not have this line item!");
            }
        } catch (GuestNotFoundException ex) {
            System.out.println("Guest does not exist!");
        }
    }

    private void viewAllMyReservations() {
        Guest g;
        int i = 1;
        try {
            g = guestSessionBeanRemote.findGuestById(currentGuest.getGuestId());
            if (!g.getOnlineReservations().isEmpty()) {
                for (OnlineReservation or : g.getOnlineReservations()) {
                    for (ReservationLineItem lineItem : or.getReservationLineItems()) {
                        System.out.println("");
                        System.out.println("Reservation " + i + ": ");
                        System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                        System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                        System.out.println("Amount: " + lineItem.getAmount().toString());
                        System.out.println("RoomType: " + lineItem.getRoomType().getTypeName());
                        System.out.println("--------------------------------------------");
                        i++;
                    }
                }
            } else {
                System.out.println("You have no Reservations \n");
            }
        } catch (GuestNotFoundException ex) {
            System.out.println("Guest does not exist!");
        }

    }
}
