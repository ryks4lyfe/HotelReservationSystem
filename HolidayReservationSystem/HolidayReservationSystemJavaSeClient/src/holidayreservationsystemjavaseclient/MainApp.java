/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemjavaseclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
                } else if (response == 2) {
                    try {
                        searchHotel();
                    } catch (DatatypeConfigurationException ex) {
                        System.out.println("Invalid Date format!");
                    }

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
                    try {
                        searchAndReserveHotel();
                    } catch (DatatypeConfigurationException ex) {
                        System.out.println("Invalid Date format!");
                    }

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

    public void searchHotel() throws DatatypeConfigurationException {
        try {
            for (RoomType r : retrieveAllRoomTypes()) {
                System.out.println(r.getRoomRecords().size());
            }
            Scanner scanner = new Scanner(System.in);

            Date checkIn;
            Date checkOut;
            Date checkIn1;
            Date checkOut1;

            GregorianCalendar gcal = null;
            XMLGregorianCalendar checkInDate;
            XMLGregorianCalendar checkOutDate;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyyhh:mm a");

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkIn = sdf.parse(scanner.nextLine().trim());
            checkIn1 = inputDateFormat.parse(checkIn.toString());
            System.out.print("Enter checkout Date (yyyy-MM-dd)> ");
            checkOut = sdf.parse(scanner.nextLine().trim());
            checkOut1 = inputDateFormat.parse(checkOut.toString());

            gcal.setTime(checkIn1);
            checkInDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            gcal.setTime(checkOut1);
            checkOutDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            List<Integer> numOfRooms = new ArrayList<>();
            List<BigDecimal> availableRates = new ArrayList<>();
            List<RoomType> enabledRooms = new ArrayList<>();

            for (RoomType r : retrieveAllRoomTypes()) {
                System.out.println(r.getRoomRecords().size());
                System.out.println(r.getLineItems().size());
                System.out.println(walkInSearchRoom(r, checkInDate, checkOutDate));
                //Check each room type for number of available rooms 
                if (walkInSearchRoom(r, checkInDate, checkOutDate) > 0) {
                    enabledRooms.add(r);
                    numOfRooms.add(walkInSearchRoom(r, checkInDate, checkOutDate));
                    availableRates.add(reservationPrice(r, checkInDate, checkOutDate));
                } else {
                    System.out.println("-------------------------------------------");
                    System.out.println("Room Type : " + r.getTypeName() + " has no rooms left");
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

            } else {
                System.out.println("No more rooms left on the given dates.");
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    public void searchAndReserveHotel() throws DatatypeConfigurationException {
        try {
            for (RoomType r : retrieveAllRoomTypes()) {
                System.out.println(r.getRoomRecords().size());
            }
            Scanner scanner = new Scanner(System.in);

            Date checkIn;
            Date checkOut;
            Date checkIn1;
            Date checkOut1;

            GregorianCalendar gcal = null;
            XMLGregorianCalendar checkInDate;
            XMLGregorianCalendar checkOutDate;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyyhh:mm a");

            Integer totalLineItems = 0;
            BigDecimal totalAmount = BigDecimal.valueOf(0);
            List<ReservationLineItem> lineItems = new ArrayList<>();

            boolean continueReservation = true;
            boolean emptyRooms = false;

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkIn = sdf.parse(scanner.nextLine().trim());
            checkIn1 = inputDateFormat.parse(checkIn.toString());
            System.out.print("Enter checkout Date (yyyy-MM-dd)> ");
            checkOut = sdf.parse(scanner.nextLine().trim());
            checkOut1 = inputDateFormat.parse(checkOut.toString());

            gcal.setTime(checkIn1);
            checkInDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            gcal.setTime(checkOut1);
            checkOutDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            while (continueReservation == true) {
                List<Integer> numOfRooms = new ArrayList<>();
                List<BigDecimal> availableRates = new ArrayList<>();
                List<RoomType> enabledRooms = new ArrayList<>();

                for (RoomType r : retrieveAllRoomTypes()) {
                    System.out.println(r.getRoomRecords().size());
                    System.out.println(r.getLineItems().size());
                    System.out.println(walkInSearchRoom(r, checkInDate, checkOutDate));
                    //Check each room type for number of available rooms 
                    if (walkInSearchRoom(r, checkInDate, checkOutDate) > 0) {
                        enabledRooms.add(r);
                        numOfRooms.add(walkInSearchRoom(r, checkInDate, checkOutDate));
                        availableRates.add(reservationPrice(r, checkInDate, checkOutDate));
                    } else {
                        System.out.println("-------------------------------------------");
                        System.out.println("Room Type : " + r.getTypeName() + " has no rooms left");
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

                        totalAmount = addItem(createLineItem(checkInDate, checkOutDate,
                                availableRates.get(option - 1),
                                enabledRooms.get(option - 1)));
                        System.out.println("Cart Cost: $" + totalAmount);
                        totalLineItems++;
                        System.out.println("Cart Items: " + totalLineItems);
                    }

                } else {

                    System.out.println("No more rooms left on the given dates.");
                    emptyRooms = true;
                }

                if (emptyRooms != true) {
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
                        if (totalLineItems != 0) {
                            doCheckout(currentPartner, totalLineItems, totalAmount, lineItems);
                            continueReservation = false;
                            //walkInReservationSessionBeanRemote.resetCart();
                            System.out.println("CheckOut Completed");
                        } else {
                            //Cart empty, continue while loop to add more items
                            System.out.println("Cart is Empty, please add items");
                            continueReservation = true;
                        }
                    } else if (response == 3) {
                        //Quit, remove all item from cart and disassociate
                        continueReservation = false;
                        removeAllItemsFromCart(lineItems);
                    }

                } else {
                    System.out.println("--------------------------------------------");
                    System.out.println("1: Checkout");
                    System.out.println("2: Quit");
                    Integer response = scanner.nextInt();

                    if (response == 1) {
                        if (totalLineItems != 0) {
                            doCheckout(currentPartner, totalLineItems, totalAmount, lineItems);
                            continueReservation = false;
                            //walkInReservationSessionBeanRemote.resetCart();
                            System.out.println("CheckOut Completed");
                        } else {
                            //Cart empty, continue while loop to add more items
                            System.out.println("Cart is Empty, please add items");
                            continueReservation = true;
                        }
                    } else if (response == 2) {
                        //Quit, remove all item from cart and disassociate
                        continueReservation = false;
                        removeAllItemsFromCart(lineItems);
                    }

                }

            }

        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }

    private void viewMyReservationDetails() throws ReservationLineItemNotFoundException_Exception {
        System.out.println("\n *** Hotel Reservation System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Integer rId = scanner.nextInt();
        Long reservationId = rId.longValue();

        boolean contains = false;

        try {
            Partner p = findPartnerById(currentPartner.getPartnerId());
            if (!p.getPartnerReservations().isEmpty()) {
                for (PartnerReservation or : p.getPartnerReservations()) {
                    for (ReservationLineItem lineItem : or.getReservationLineItems()) {
                        if (lineItem.getReservationLineItemId().equals(reservationId)) {
                            contains = true;
                            System.out.println("--------------------------------------------");
                            System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                            System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                            System.out.println("Amount: " + lineItem.getAmount().toString());
                            System.out.println("--------------------------------------------");
                        }
                    }
                }
            }

            if (contains == false) {
                System.out.println("Guest does not have this line item!");
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Guest does not exist!");
        }
    }
    

    private void viewAllMyReservations() throws PartnerNotFoundException_Exception {
        HotelReservationWebService_Service service = new HotelReservationWebService_Service();
        Partner p;
        int i = 1;
        try {
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
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("You have no Reservations \n");
        }

    }

    private ws.client.Partner partnerLogin(java.lang.String username, java.lang.String password) throws FailedLoginException_Exception, PartnerNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.partnerLogin(username, password);

    }


    private ws.client.Partner findPartnerById(java.lang.Long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.findPartnerById(partnerId);

    }

    private java.lang.Integer walkInSearchRoom(ws.client.RoomType roomType, javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.walkInSearchRoom(roomType, arg0, arg1);
    }

    private java.util.List<ws.client.RoomType> retrieveAllRoomTypes() {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.retrieveAllRoomTypes();
    }

    private java.math.BigDecimal reservationPrice(ws.client.RoomType roomType, javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.reservationPrice(roomType, arg0, arg1);
    }

    private java.math.BigDecimal addItem(ws.client.ReservationLineItem lineItem) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.addItem(lineItem);
    }

    private void removeAllItemsFromCart(java.util.List<ws.client.ReservationLineItem> lineItems) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        port.removeAllItemsFromCart(lineItems);
    }

    private ws.client.ReservationLineItem createLineItem(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1, java.math.BigDecimal amount, ws.client.RoomType roomType) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.createLineItem(arg0, arg1, amount, roomType);
    }

    private ws.client.PartnerReservation doCheckout(ws.client.Partner partner, java.lang.Integer totalLineItems, java.math.BigDecimal totalAmount,
            java.util.List<ws.client.ReservationLineItem> lineItems) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.doCheckout(partner, totalLineItems, totalAmount, lineItems);
    }
}
