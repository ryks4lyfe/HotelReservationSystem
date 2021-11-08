/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.EmployeeAccessRight;
import util.exception.EmployeeNotFoundException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author 65912
 */
public class SystemAdministrationModule {

    private PartnerSessionBeanRemote partnerSessionBeanRemote;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    private Employee employee;

    public SystemAdministrationModule() {

    }

    public SystemAdministrationModule(PartnerSessionBeanRemote partnerSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, Employee employee) {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.employee = employee;
    }

    public void menuSystemAdministration() {

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partner");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createEmployee();
                } else if (response == 2) {
                    viewAllEmployees();
                } else if (response == 3) {
                    createPartner();
                } else if (response == 4) {
                    viewAllPartners();
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

    public void createEmployee() {
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Employee ***\n");
        System.out.print("Enter Username> ");
        newEmployee.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newEmployee.setPassword(scanner.nextLine().trim());
        System.out.print("Select Employee Access Right (1: System Admin, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
        Integer employeeRoleInt = scanner.nextInt();

        OUTER:
        while (true) {
            if (employeeRoleInt >= 1 && employeeRoleInt <= 4) {
                switch (employeeRoleInt) {
                    case 1:
                        newEmployee.setEnum(EmployeeAccessRight.SYSTEM_ADMINISTRATOR);
                        break OUTER;
                    case 2:
                        newEmployee.setEnum(EmployeeAccessRight.OPERATION_MANAGER);
                        break OUTER;
                    case 3:
                        newEmployee.setEnum(EmployeeAccessRight.SALES_MANAGER);
                        break OUTER;
                    case 4:
                        newEmployee.setEnum(EmployeeAccessRight.GUEST_RELATION_OFFICER);
                        break OUTER;
                    default:
                        System.out.println("Sorry, this employee access right is currently not available. Please try again!\n");
                        break;
                }
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        Long eId = employeeSessionBeanRemote.createEmployee(newEmployee);
        System.out.println("New employee created successfully!: " + eId + "\n");

    }

    public void viewAllEmployees() {

        System.out.println("*** HoRS :: Hotel Management System :: View All Employees ***\n");

        List<Employee> employees;
        try {
            employees = employeeSessionBeanRemote.retrieveListOfEmployees();
            for (Employee employee : employees) {
                System.out.println("Employee ID :" + employee.getEmployeeId());
                System.out.println("Employee Username :" + employee.getUsername());
                System.out.println("Employee Password :" + employee.getPassword());
                System.out.println("Employee AccessRight :" + employee.getEnum().toString());
                System.out.println("--------------------------------------------------------------------------------------");

            }
            System.out.println("");
        } catch (EmployeeNotFoundException ex) {
            System.out.println("No Employee record in database!");
        }

    }

    public void createPartner() {
        Scanner scanner = new Scanner(System.in);
        Partner newPartner = new Partner();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Partner ***\n");
        System.out.print("Enter Partner Name> ");
        newPartner.setPartnerName(scanner.nextLine().trim());
        System.out.print("Enter Username> ");
        newPartner.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        Long pId = partnerSessionBeanRemote.createPartner(newPartner);
        System.out.println("New partner created successfully!: " + pId + "\n");

    }

    public void viewAllPartners() {

        System.out.println("*** HoRS :: Hotel Management System :: View All Partners ***\n");

        List<Partner> partners;
        try {
            partners = partnerSessionBeanRemote.retrieveListOfPartners();
            for (Partner partner : partners) {
                System.out.println("Partner ID : " + partner.getPartnerId());
                System.out.println("Partner Name : " + partner.getPartnerName());
                System.out.println("Partner Username : " + partner.getUsername());
                System.out.println("Partner Password : " + partner.getPassword());
                System.out.println("----------------------------------------------------------------");
            }

            System.out.println("");
        } catch (PartnerNotFoundException ex) {
            System.out.println("No Partner records in database!");
        }

    }
}
