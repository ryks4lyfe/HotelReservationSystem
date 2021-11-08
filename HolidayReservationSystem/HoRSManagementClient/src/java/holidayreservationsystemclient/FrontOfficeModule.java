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
import util.exception.EmployeeNotFoundException;
import util.exception.GuestNotFoundException;
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
            System.out.println("2: Walk-in Reserve Room");
            System.out.println("-----------------------");
            System.out.println("3: Check-in Guest");
            System.out.println("4: Check-out Guest");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    walkInSearchRoom();
                    //} else if (response == 2) {
                    //walkInreserveRoom();
                } else if (response == 3) {
                    checkInGuest();
                } else if (response == 4) {
                    //checkOutGuest();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
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

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out Date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine());

            while (continueReservation == true) {
                List<Integer> numOfRooms = new ArrayList<>();
                List<BigDecimal> availableRates = new ArrayList<>();
                List<RoomType> enabledRooms = new ArrayList<>();

                for (RoomType r : roomTypeSessionBeanRemote.retrieveAllRoomTypes()) {
                    System.out.println(r.getRoomRecords().size());
                     System.out.println(reservationSessionBeanRemote.walkInSearchRoom(r, checkInDate, checkOutDate));
                    //Check each room type for number of available rooms 
                    if (reservationSessionBeanRemote.walkInSearchRoom(r, checkInDate, checkOutDate) != 0) {
                        enabledRooms.add(r);
                        numOfRooms.add(reservationSessionBeanRemote.walkInSearchRoom(r, checkInDate, checkOutDate));
                        availableRates.add(reservationSessionBeanRemote.walkInPrice(r, checkInDate, checkOutDate));
                    } else {
                        System.out.println("-------------------------------------------");
                        System.out.println("Room Type " + r.getTypeName() + " has no rooms left");
                        System.out.println("-------------------------------------------");
                    }
                }

                if (!enabledRooms.isEmpty()) {

                    //For each avaialble roomType, display the room record and rate details
                    for (int i = 0; i < enabledRooms.size(); i++) {
                        RoomType rt = enabledRooms.get(i);
                        BigDecimal price = availableRates.get(i);
                        System.out.println("-------------------------------------------");
                        System.out.println("Option " + i + 1);
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
                        System.out.println(availableRates.get(option - 1));
                        System.out.println(walkInReservationBeanRemote.print());
                        System.out.println("Cart Cost: " + walkInReservationBeanRemote.addItem(new ReservationLineItem(checkInDate, checkOutDate
                                , availableRates.get(option - 1),
                                enabledRooms.get(option - 1))));
                        System.out.println("Cart Items: " + walkInReservationBeanRemote.getTotalLineItems());
                    }
                } else {

                    System.out.println("No more rooms left on the given dates.");
                }
                System.out.println("--------------------------------------------");
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
                        walkInReservationBeanRemote.doCheckout(employee);
                        continueReservation = false;
                        walkInReservationBeanRemote.resetCart();
                        System.out.println("CheckOut Completed");
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

            }

        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    public void checkInGuest() {
        Long guestId = null;
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS :: Hotel Management System :: Check-in Guest ***\n");
            System.out.print("Enter Guest id>");
            guestId = scanner.nextLong();

            List<RoomRecord> roomsCheckedIn = guestSessionBeanRemote.checkInGuest(guestId);
            System.out.println("Guest " + guestId.toString() + " checked in successfully to the following rooms: ");
            for (RoomRecord room : roomsCheckedIn) {
                System.out.println("Room Number: " + room.getRoomNum());
            }

            System.out.println("");

        } catch (GuestNotFoundException | UnallowedCheckInException ex) {
            System.out.println("An error has occurred while checking in guest: " + guestId.toString() + ex.getMessage() + "\n");
        }
    }
}
