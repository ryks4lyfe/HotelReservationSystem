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
import entity.Employee;
import entity.Guest;
import entity.OnlineReservation;
import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import entity.RoomRecord;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnallowedCheckInException;

/**
 *
 * @author 65912
 */
public class FrontOfficeModule {

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    private WalkInReservationSessionBeanRemote walkInReservationBeanRemote;

    private Employee employee;

    public FrontOfficeModule() {

    }

    public FrontOfficeModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote,
            PartnerSessionBeanRemote partnerSessionBeanRemote, RoomRecordSessionBeanRemote roomRecordSessionBeanRemote,
            RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote,
            ReservationSessionBeanRemote reservationSessionBeanRemote, WalkInReservationSessionBeanRemote walkInReservationBeanRemote, Employee employee) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomRecordSessionBeanRemote = roomRecordSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.walkInReservationBeanRemote = walkInReservationBeanRemote;
        this.employee = employee;
    }

    public void menuFrontOffice() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("-----------------------");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("-----------------------");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    walkInSearchRoom();

                } else if (response == 2) {
                    checkInGuest();
                } else if (response == 3) {
                    checkOutGuest();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void walkInSearchRoom() {
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

                            for (ReservationLineItem lineItem : walkInReservationBeanRemote.getLineItems()) {
                                System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                                System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                                System.out.println("Amount: " + lineItem.getAmount().toString());
                                System.out.println("RoomType: " + lineItem.getRoomType().getTypeName());
                            }
                            System.out.println("Cart Cost: " + walkInReservationBeanRemote.addItem(new ReservationLineItem(checkInDate, checkOutDate,
                                    availableRates.get(option - 1),
                                    enabledRooms.get(option - 1))));
                            System.out.println("Cart Items: " + walkInReservationBeanRemote.getTotalLineItems());
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
                            if (!walkInReservationBeanRemote.getLineItems().isEmpty()) {
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
                                walkInReservationBeanRemote.doCheckout(employee);
                                continueReservation = false;
                                walkInReservationBeanRemote.resetCart();
                                System.out.println("Checkout Completed");
                            } else {
                                //Cart empty, continue while loop to add more items
                                System.out.println("Cart is Empty, please add items");
                                continueReservation = true;
                            }
                        } else if (response == 3) {
                            //Quit, remove all item from cart and disassociate
                            continueReservation = false;
                            walkInReservationBeanRemote.removeAllItemsFromCart();
                        }

                    } else {
                        System.out.println("--------------------------------------------");
                        System.out.println("No more rooms left to book");
                        System.out.println("1: Checkout");
                        System.out.println("2: Quit");
                        Integer response = scanner.nextInt();

                        if (response == 1) {
                            if (!walkInReservationBeanRemote.getLineItems().isEmpty()) {
                                try {
                                    reservationSessionBeanRemote.roomAllocationsForToday();
                                } catch (ReservationLineItemNotFoundException ex) {
                                    System.out.println("reservation was not found!");
                                }
                                walkInReservationBeanRemote.doCheckout(employee);
                                continueReservation = false;
                                walkInReservationBeanRemote.resetCart();
                                System.out.println("CheckOut Completed");
                            } else {
                                //Cart empty, continue while loop to add more items
                                System.out.println("Cart is Empty, please add items");
                                continueReservation = true;
                            }
                        } else if (response == 2) {
                            //Quit, remove all item from cart and disassociate
                            continueReservation = false;
                            walkInReservationBeanRemote.removeAllItemsFromCart();
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

    public void checkInGuest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Hotel Management System :: Check-In Guest ***\n");
        System.out.println("Enter booking type : 1: Walk-in , 2: Guest,  3: Partner\n");
        Integer choice = scanner.nextInt();

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if (choice == 1) {
            System.out.println("Enter Employee Id: \n");
            Long eId = scanner.nextLong();
            System.out.println("Enter Reservation Id: \n");
            Long rId = scanner.nextLong();
            try {
                Employee e = employeeSessionBeanRemote.findEmployeeById(eId);
                for (WalkInReservation wr : e.getWalkInReservations()) {
                    for (ReservationLineItem r : wr.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckInDate())) && r.getReservationLineItemId().equals(rId)) {
                            try {
                                guestSessionBeanRemote.checkInGuest(r);
                                System.out.println("Reservation " + rId + " checked in!");
                            } catch (GuestNotFoundException | UnallowedCheckInException ex) {
                                System.out.println("An error occurred " + ex.getMessage());
                            }
                        }
                    }
                }
                //checkIn(r);

            } catch (EmployeeNotFoundException ex) {
                System.out.println("No such user!");
            }
        } else if (choice == 2) {
            System.out.println("Enter Guest Id: \n");
            Long gId = scanner.nextLong();
            System.out.println("Enter Reservation Id: \n");
            Long rId = scanner.nextLong();
            try {
                Guest g = guestSessionBeanRemote.findGuestById(gId);
                for (OnlineReservation or : g.getOnlineReservations()) {
                    for (ReservationLineItem r : or.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckInDate())) && r.getReservationLineItemId().equals(rId)) {

                            try {
                                guestSessionBeanRemote.checkInGuest(r);
                                System.out.println("Reservation " + rId + " checked in!");
                            } catch (GuestNotFoundException | UnallowedCheckInException ex) {
                                System.out.println("An error occurred " + ex.getMessage());
                            }
                        }
                    }
                }
                //checkIn(r);
            } catch (GuestNotFoundException ex) {
                System.out.println("No such user!");
            }

        } else if (choice == 3) {
            System.out.println("Enter Partner Id: \n");
            Long pId = scanner.nextLong();
            System.out.println("Enter Reservation Id: \n");
            Long rId = scanner.nextLong();
            try {
                Partner p = partnerSessionBeanRemote.findPartnerById(pId);
                for (PartnerReservation pr : p.getPartnerReservations()) {
                    for (ReservationLineItem r : pr.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckInDate())) && r.getReservationLineItemId().equals(rId)) {

                            try {
                                guestSessionBeanRemote.checkInGuest(r);
                                System.out.println("Reservation " + rId + " checked in!");
                            } catch (GuestNotFoundException | UnallowedCheckInException ex) {
                                System.out.println("An error occurred " + ex.getMessage());
                            }
                        }
                    }
                }
                //checkIn(r);
            } catch (PartnerNotFoundException ex) {
                System.out.println("No such user!");
            }
        } else {
            System.out.println("No such choice!");
        }

    }

    private void checkOutGuest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Hotel Management System :: Check-Out Guest ***\n");
        System.out.println("Enter booking type : 1: Walk-in , 2: Guest,  3: Partner\n");
        Integer choice = scanner.nextInt();
        ReservationLineItem lineItem;
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if (choice == 1) {
            System.out.println("Enter Employee Id: \n");
            Long id = scanner.nextLong();
            try {
                Employee e = employeeSessionBeanRemote.findEmployeeById(id);
                for (WalkInReservation wr : e.getWalkInReservations()) {
                    for (ReservationLineItem r : wr.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckOutDate()))) {
                            lineItem = r;
                            try {
                                guestSessionBeanRemote.checkOutGuest(r);
                            } catch (GuestNotFoundException ex) {
                                System.out.println("User not found!");
                            }
                        }
                    }
                }
                //checkOut(r);

            } catch (EmployeeNotFoundException ex) {
                System.out.println("No such user!");
            }
        } else if (choice == 2) {
            System.out.println("Enter Guest Id: \n");
            Long id = scanner.nextLong();
            try {
                Guest g = guestSessionBeanRemote.findGuestById(id);
                for (OnlineReservation or : g.getOnlineReservations()) {
                    for (ReservationLineItem r : or.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckOutDate()))) {
                            lineItem = r;
                            try {
                                guestSessionBeanRemote.checkOutGuest(r);
                            } catch (GuestNotFoundException ex) {
                                System.out.println("User not found!");
                            }
                        }
                    }
                }
                //checkOut(r);
            } catch (GuestNotFoundException ex) {
                System.out.println("No such user!");
            }

        } else if (choice == 3) {
            System.out.println("Enter Partner Id: \n");
            Long id = scanner.nextLong();
            try {
                Partner p = partnerSessionBeanRemote.findPartnerById(id);
                for (PartnerReservation pr : p.getPartnerReservations()) {
                    for (ReservationLineItem r : pr.getReservationLineItems()) {
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckOutDate()))) {
                            lineItem = r;
                            try {
                                guestSessionBeanRemote.checkOutGuest(r);
                            } catch (GuestNotFoundException ex) {
                                System.out.println("User not found!");
                            }
                        }
                    }
                }
                //checkOut(r);
            } catch (PartnerNotFoundException ex) {
                System.out.println("No such user!");
            }
        } else {
            System.out.println("No such choice!");
        }

    }
}
