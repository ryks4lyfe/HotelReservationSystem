/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.exception.EmployeeNotFoundException;
import util.exception.FailedLoginException;

/**
 *
 * @author 65912
 */
public class MainApp {

    private PartnerSessionBeanRemote partnerSessionBeanRemote;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    private GuestSessionBeanRemote guestSessionBeanRemote;

    private Employee employee;

    public MainApp() {

    }

    public MainApp(PartnerSessionBeanRemote partnerSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote) {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
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

                        //systemAdminModule = new SystemAdministrationModule(employeeSessionBeanRemote, partnerSessionBeanRemote, employee);
                        //hotelOpModule = new HotelOperationModule(employeeSessionBeanRemote, partnerSessionBeanRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote, reservationControllerRemote, employee);
                        //frontOfficeModule = new FrontOfficeModule(employeeSessionBeanRemote, guestSessionBeanRemote, partnerSessionBeanRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote, walkInReservationSessionBeanRemote, reservationControllerRemote, employee);
                        menuMain();
                    } catch (FailedLoginException ex) {
                        System.out.println(ex.getMessage());
                    } catch (EmployeeNotFoundException ex) {
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
    }

    private void doLogin() throws FailedLoginException, EmployeeNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String email;
        String password;

        System.out.println("*** HoRS System :: Employee Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
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
                        //hotelOpModule = new HotelOperationModule(employeeSessionBeanRemote, partnerSessionBeanRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote, reservationControllerRemote, employee);
                        //hotelOpModule.menuHotelOperation();

                    } else if (employee.getEnum().toString().equals("GUEST_RELATION_OFFICER")) {
                        //frontOfficeModule = new FrontOfficeModule(employeeSessionBeanRemote, guestSessionBeanRemote, partnerSessionBeanRemote,
                        //roomControllerRemote, roomTypeControllerRemote,
                        //roomRateControllerRemote, walkInReservationSessionBeanRemote, reservationControllerRemote, employee);
                        //frontOfficeModule.menuFrontOffice();   
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
