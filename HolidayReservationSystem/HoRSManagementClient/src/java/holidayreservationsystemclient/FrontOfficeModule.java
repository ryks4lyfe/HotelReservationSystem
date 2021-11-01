/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.RoomRecord;
import entity.WalkInReservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private Employee employee;

    public FrontOfficeModule() {

    }

    public FrontOfficeModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, RoomRecordSessionBeanRemote roomRecordSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee employee) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomRecordSessionBeanRemote = roomRecordSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
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
                } else if (response == 2) {
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

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out Date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine());

          
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    public void reserveRoom() {

        WalkInReservation walkInReservation = new WalkInReservation();
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate;
        Date checkOutDate;

        System.out.println("*** HoRS :: Hotel Management System :: Walk-in Reserve Room ***\n");
        try {
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter Room Type> ");
            String roomType = scanner.nextLine().trim();
            System.out.print("Enter Number of Room> ");
            Integer numberOfRooms = scanner.nextInt();

            
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        //} catch (RoomTypeNotFoundException | EmployeeNotFoundException ex) {
            System.out.println("An error ocurred when reserving room: " + ex.getMessage() + "\n");
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
            for(RoomRecord room:roomsCheckedIn)
            {
                System.out.println("Room Number: " + room.getRoomNum());
            }
            
            System.out.println("");
            
            
        } catch (GuestNotFoundException | UnallowedCheckInException ex) {
            System.out.println("An error has occurred while checking in guest: " + guestId.toString() + ex.getMessage() + "\n");
        }
    }
}

    



