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
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkInDate;
            Date checkOutDate;
            String roomType;

            GregorianCalendar gcal = new GregorianCalendar();
            XMLGregorianCalendar xgcal1;
            XMLGregorianCalendar xgcal2;

            System.out.println("\n*** HoRS System :: Search Hotel Room ***\n");
            System.out.print("Enter check in date (dd/mm/yyyy)> ");
            checkInDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter check out Date (dd/mm/yyyy)> ");
            checkOutDate = inputDateFormat.parse(scanner.nextLine().trim());

            gcal.setTime(checkInDate);
            xgcal1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            gcal.setTime(checkOutDate);
            xgcal2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            List<Integer> numOfRooms = new ArrayList<>();
            List<BigDecimal> availableRates = new ArrayList<>();
            List<RoomType> enabledRooms = new ArrayList<>();

            for (RoomType r : retrieveAllRoomTypes()) {
                
                //Check each room type for number of available rooms 
                if (walkInSearchRoom(r, xgcal1, xgcal2) > 0) {
                    enabledRooms.add(r);
                    numOfRooms.add(walkInSearchRoom(r, xgcal1, xgcal2));
                    availableRates.add(reservationPrice(r, xgcal1, xgcal2));
                } else {
                    System.out.println("");
                    System.out.println("Room Type : " + r.getTypeName() + " has no rooms left");
                    System.out.println("-------------------------------------------");
                }
            }

            if (!enabledRooms.isEmpty()) {

                //For each avaialble roomType, display the room record and rate details
                for (int i = 0; i < enabledRooms.size(); i++) {
                    RoomType rt = enabledRooms.get(i);
                    BigDecimal price = availableRates.get(i);
                    System.out.println("");
                    int i2 = i + 1;
                    System.out.println("Option " + i2);
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

            Integer totalLineItems = 0;
            Long totalAmount = Long.valueOf(0);
            List<ReservationLineItem> lineItems = new ArrayList<>();

            boolean continueReservation = true;
            boolean emptyRooms = false;

            Scanner scanner = new Scanner(System.in);

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkInDate;
            Date checkOutDate;
            String roomType;

            GregorianCalendar gcal = new GregorianCalendar();
            XMLGregorianCalendar xgcal1;
            XMLGregorianCalendar xgcal2;

            System.out.println("\n*** HoRS System :: Search Hotel Room ***\n");
            System.out.print("Enter check in date (dd/mm/yyyy)> ");
            checkInDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter check out Date (dd/mm/yyyy)> ");
            checkOutDate = inputDateFormat.parse(scanner.nextLine().trim());

            gcal.setTime(checkInDate);
            xgcal1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            gcal.setTime(checkOutDate);
            xgcal2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            while (continueReservation == true) {
                List<Integer> numOfRooms = new ArrayList<>();
                List<BigDecimal> availableRates = new ArrayList<>();
                List<RoomType> enabledRooms = new ArrayList<>();

                for (RoomType r : retrieveAllRoomTypes()) {
                    
                    //Check each room type for number of available rooms 
                    if (walkInSearchRoom(r, xgcal1, xgcal2) > 0) {
                        enabledRooms.add(r);
                        numOfRooms.add(walkInSearchRoom(r, xgcal1, xgcal2));
                        availableRates.add(reservationPrice(r, xgcal1, xgcal2));
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
                        System.out.println("");
                        int i2 = i + 1;
                        System.out.println("Option " + i2);
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
                        
                        totalAmount += availableRates.get(option - 1).longValue();
                        ReservationLineItem item = createLineItem(xgcal1, xgcal2,
                                availableRates.get(option - 1),
                                enabledRooms.get(option - 1));
                        
                        item = addItem(item, enabledRooms.get(option - 1).getRoomTypeId());
                        lineItems.add(item);
                        
                        System.out.println("Cart Cost: $" + totalAmount.toString());
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
                            doCheckout(currentPartner, totalLineItems, BigDecimal.valueOf(totalAmount), lineItems);
                            continueReservation = false;
                            //IF SAME DAY ALLOCATE
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
                            doCheckout(currentPartner, totalLineItems, BigDecimal.valueOf(totalAmount), lineItems);
                            continueReservation = false;
                            //IF SAME DAY ALLOCATE
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
        try {
            ReservationLineItem lineItem = findReservationLineItemOfPartner(reservationId, currentPartner.getPartnerId());
            System.out.println("");
                System.out.println("Reservation " + rId + ": ");
                System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                System.out.println("Amount: " + lineItem.getAmount().toString());
                
                System.out.println("--------------------------------------------");
        } catch(ReservationLineItemNotFoundException_Exception ex) {
            System.out.println("Partner has doesnt have the entered reservation!");
        }

        
            
    }

    private void viewAllMyReservations() throws PartnerNotFoundException_Exception {
        HotelReservationWebService_Service service = new HotelReservationWebService_Service();
        Partner p;
        int i = 1;
        List<ReservationLineItem> lineItems = retrieveAllPartnerReservations(currentPartner.getPartnerId());
        
        if (!lineItems.isEmpty()) {
            for (ReservationLineItem lineItem : lineItems) {
                System.out.println("");
                System.out.println("Reservation " + i + ": ");
                System.out.println("Check In Date: " + lineItem.getCheckInDate().toString());
                System.out.println("Check Out Date: " + lineItem.getCheckOutDate().toString());
                System.out.println("Amount: " + lineItem.getAmount().toString());
                
                System.out.println("--------------------------------------------");
                i++;
            }
        } else {
            System.out.println("Partner has no reservations!");
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
        return port.retrieveAllRoomTypesForWebservice();
    }

    private java.math.BigDecimal reservationPrice(ws.client.RoomType roomType, javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.reservationPrice(roomType, arg0, arg1);
    }

    private ws.client.ReservationLineItem addItem(ws.client.ReservationLineItem lineItem, Long rId) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.addItem(lineItem, rId);
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

    private void doCheckout(ws.client.Partner partner, java.lang.Integer totalLineItems, java.math.BigDecimal totalAmount,
            java.util.List<ws.client.ReservationLineItem> lineItems) {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        port.doCheckout(partner, totalLineItems, totalAmount, lineItems);
    }

    public java.util.List<ws.client.ReservationLineItem> retrieveAllPartnerReservations(Long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.retrieveAllPartnerReservations(partnerId);
    }
    
    public ws.client.ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.findReservationLineItemById(reservationLineItemId);
    }
    
    public ReservationLineItem findReservationLineItemOfPartner(Long reservationLineItemId, Long partnerId) throws ReservationLineItemNotFoundException_Exception {
        ws.client.HotelReservationWebService_Service service = new ws.client.HotelReservationWebService_Service();
        ws.client.HotelReservationWebService port = service.getHotelReservationWebServicePort();
        return port.findReservationLineItemOfPartner(reservationLineItemId, partnerId);
    }
}
