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
                List<RoomRecord> availableRooms = new ArrayList<>();
                List<BigDecimal> availableRates = new ArrayList<>();
                List<RoomType> enabledRooms = roomTypeSessionBeanRemote.retrieveAllRoomTypes();

                for (RoomType r : enabledRooms) {
                    RoomRecord room = reservationSessionBeanRemote.walkInSearch(r, checkInDate, checkOutDate);
                    if (room != null) {
                        availableRooms.add(room);
                        availableRates.add(reservationSessionBeanRemote.walkInPrice(r, checkInDate, checkOutDate));
                    }
                }

                if (availableRooms.size() != 0) {

                    for (int i = 0; i < availableRooms.size() - 1; i++) {
                        RoomType rt = availableRooms.get(i).getRoomType();
                        BigDecimal price = availableRates.get(i);
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

                    System.out.println("Enter option for reservation : ");
                    Integer option = scanner.nextInt();
                    while (option < 1 || option > availableRooms.size()) {
                        if (option < 1 || option > availableRooms.size()) {
                            System.out.println("Please input a proper option");
                        } else {
                            
                        }
                    }

                } else {
                    continueReservation = false;
                    System.out.println("No more rooms left on the given dates.");
                }

            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    public void reserveRoom(Date checkInDate, Date checkOutDate, RoomRecord room, BigDecimal amount) {

        WalkInReservation walkInReservation = new WalkInReservation();
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        walkInReservation = reservationSessionBeanRemote.createWalkInReservation(walkInReservation, employee.getEmployeeId());
        //ReservationLineItem reservation = 
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
