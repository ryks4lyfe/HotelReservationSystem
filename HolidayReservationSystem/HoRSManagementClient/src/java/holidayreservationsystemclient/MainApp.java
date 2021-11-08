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
import java.text.ParseException;
import java.util.Scanner;
import util.exception.DeleteRoomRateException;
import util.exception.DeleteRoomTypeException;
import util.exception.EmployeeNotFoundException;
import util.exception.FailedLoginException;
import util.exception.RoomNameExistsException;
import util.exception.RoomRateExistsException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomRecordNotFoundException;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author 65912
 */
public class MainApp {

    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    private WalkInReservationSessionBeanRemote walkInReservationBeanRemote;

    private SystemAdministrationModule systemAdminModule;
    private HotelOperationModule hotelOpModule;
    private FrontOfficeModule frontOfficeModule;

    private Employee employee;

    public MainApp(PartnerSessionBeanRemote partnerSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote,
            GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBean,
            RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomRecordSessionBeanRemote roomRecordSessionBeanRemote,
            RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, WalkInReservationSessionBeanRemote walkInReservationBeanRemote) {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBean;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomRecordSessionBeanRemote = roomRecordSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.walkInReservationBeanRemote = walkInReservationBeanRemote;

    }

    public void runApp() {

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to HoRS :: Hotel Management System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");

            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        menuMain();
                    } catch (FailedLoginException | EmployeeNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
        System.out.println("Finish!\n");
    }

    private void doLogin() throws FailedLoginException, EmployeeNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String email;
        String password;

        System.out.println("*** HoRS System :: Employee Login ***\n");
        System.out.print("Enter Username> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            try {
                employee = employeeSessionBeanRemote.doLogin(email, password);
            } catch (FailedLoginException ex) {
                throw new FailedLoginException("Error, login credentials are incorret!");
            } catch (EmployeeNotFoundException ex) {
                throw new EmployeeNotFoundException("Error, Employee with " + email + " does not exist.");
            }
        }
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Management (HoRS) System ***\n");
            System.out.println("You are logged in as " + employee.getUsername() + " with " + employee.getEnum().toString() + " rights\n");

            System.out.println("1: Proceed ");
            System.out.println("2: Logout\n");

            while (response != 1 || response != 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {

                    if (employee.getEnum().toString().equals("SYSTEM_ADMINISTRATOR")) {
                        SystemAdministrationModule systemAdminModule = new SystemAdministrationModule(partnerSessionBeanRemote, employeeSessionBeanRemote, employee);
                        systemAdminModule.menuSystemAdministration();

                    } else if (employee.getEnum().toString().equals("OPERATION_MANAGER") || employee.getEnum().toString().equals("SALES_MANAGER")) {
                        HotelOperationModule hotelOpModule = new HotelOperationModule(employeeSessionBeanRemote, partnerSessionBeanRemote, roomRecordSessionBeanRemote, roomTypeSessionBeanRemote, roomRateSessionBeanRemote, reservationSessionBeanRemote, employee);
                        hotelOpModule.menuHotelOperation();

                    } else if (employee.getEnum().toString().equals("GUEST_RELATION_OFFICER")) {
                        frontOfficeModule = new FrontOfficeModule(employeeSessionBeanRemote, guestSessionBeanRemote, partnerSessionBeanRemote,
                                roomRecordSessionBeanRemote, roomTypeSessionBeanRemote,
                                roomRateSessionBeanRemote, reservationSessionBeanRemote, walkInReservationBeanRemote, employee);
                        frontOfficeModule.menuFrontOffice();
                    }

                } else if (response == 2) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
    }
}
