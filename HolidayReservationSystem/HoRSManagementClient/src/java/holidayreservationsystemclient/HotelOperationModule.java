/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomRecordSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.persistence.PersistenceException;
import util.enumeration.RoomRateTypeEnum;
import static util.enumeration.RoomRateTypeEnum.NORMAL;
import static util.enumeration.RoomRateTypeEnum.PEAK;
import static util.enumeration.RoomRateTypeEnum.PROMOTION;
import static util.enumeration.RoomRateTypeEnum.PUBLISHED;
import util.exception.DeleteRoomRateException;
import util.exception.DeleteRoomRecordException;
import util.exception.DeleteRoomTypeException;
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
public class HotelOperationModule {
    

private PartnerSessionBeanRemote partnerSessionBeanRemote;
private EmployeeSessionBeanRemote employeeSessionBeanRemote; 
private RoomRateSessionBeanRemote roomRateSessionBeanRemote; 
private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote; 
private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote; 
private ReservationSessionBeanRemote reservationSessionBeanRemote;
private Employee employee;

    public HotelOperationModule() {
    }



    public HotelOperationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, RoomRecordSessionBeanRemote roomRecordSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee employee) {
        this(); 
        this.employeeSessionBeanRemote = employeeSessionBeanRemote; 
        this.partnerSessionBeanRemote = partnerSessionBeanRemote; 
        this.roomRecordSessionBeanRemote = roomRecordSessionBeanRemote; 
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote; 
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote; 
        this.reservationSessionBeanRemote = reservationSessionBeanRemote; 
        this.employee= employee; 
    }

    public void menuHotelOperation() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS :: Hotel Operation ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("-----------------------");
            System.out.println("11: Create New Room Rate");
            System.out.println("12: View Room Rate Details");
            System.out.println("13: Update Room Rate");
            System.out.println("14: Delete Room Rate");
            System.out.println("15: View All Room Rates");
            System.out.println("16: Allocate room");
            System.out.println("-----------------------");
            System.out.println("17: Back\n");
            response = 0;
            
            OUTER:
            while (response < 1 || response > 16) {
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();
                switch (response) {
                    case 1:
                        createRoomType();
                        break;
                    case 2:
                        viewRoomTypeDetails();
                        break;
                    case 3:
                        {
                            System.out.print("Enter Room Type Name> ");
                            String name = scanner.nextLine().trim();
                            try{
                            updateRoomType(roomTypeSessionBeanRemote.findRoomTypeByName(name));
                            } catch (RoomTypeNotFoundException ex) {       
                            }
                            
                            break;
                        }
                    case 4:
                        {
                            System.out.print("Enter Room Type Name> ");
                            String name = scanner.nextLine().trim();
                            try {
                                deleteRoomType(roomTypeSessionBeanRemote.findRoomTypeByName(name));
                            } catch (RoomTypeNotFoundException ex) {
                                System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
                            }       break;
                        }
                    case 5:
                        viewAllRoomTypes();
                        break;
                    case 6:
                        createRoom();
                        break;
                    case 7:
                        updateRoom();
                        break;
                    case 8:
                        deleteRoom();
                        break;
                    case 9:
                        viewAllRooms();
                        break;
                    case 10:
                        viewRoomAllocationExceptionReport();
                        break;
                    case 11:
                        createRoomRate();
                        break;
                    case 12:
                        viewRoomRateDetails();
                        break;
                    case 13:
                        {
                            System.out.print("Enter Room Rate Id> ");
                            Long rateId = scanner.nextLong();
                            try {
                                updateRoomRate(roomRateSessionBeanRemote.findRoomRateById(rateId));
                            } catch (RoomRateNotFoundException ex) {
                                System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
                            }       break;
                        }
                    case 14:
                        {
                            System.out.print("Enter Room Rate Id> ");
                            Long rateId = scanner.nextLong();
                            scanner.nextLine();
                            try {
                                deleteRoomRate(roomRateSessionBeanRemote.findRoomRateById(rateId));
                            } catch (RoomRateNotFoundException ex) {
                                System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
                            }       break;
                        }
                    case 15:
                        viewAllRoomRates();
                        break;
                    case 16:
                        allocateRoom();
                        break;
                    case 17:
                        break OUTER;
                    default:
                        System.out.println("Invalid option, please try again!\n");
                        break;                
                }
            }
            
            if(response == 17)
            {
                break;
            }
        }
    }

    public void createRoomType()  
    {
        
        Scanner scanner = new Scanner(System.in);
        RoomType newRoomType = new RoomType();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Room Type ***\n");
        System.out.print("Enter Name> ");
        newRoomType.setTypeName(scanner.nextLine().trim());
        System.out.print("Enter Description> ");
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter Room Size (square meters)> ");
        newRoomType.setSize(scanner.nextLine().trim());
        System.out.print("Enter Bed> ");
        newRoomType.setBed(scanner.nextLine().trim());
        System.out.print("Enter Capacity> ");
        newRoomType.setCapacity(scanner.nextLine().trim());
        System.out.print("Enter Amenities> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        newRoomType.setTypeStatus("enabled");
        
        System.out.println(newRoomType.getSize()); 
        
        try {
        newRoomType = roomTypeSessionBeanRemote.createRoomType(newRoomType);
        }
        catch (RoomTypeNameExistsException | UnknownPersistenceException ex){
            
        }
        System.out.println("New room type created successfully!: " + newRoomType.getTypeName()+ "\n");
        System.out.print("Press any key to continue...> ");

    }

    public void viewRoomTypeDetails() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS :: Hotel Management System :: View Room Type Details ***\n");
        
        System.out.print("Enter Room Type Name> ");
        String name = scanner.nextLine().trim();
        
        try 
        {
            RoomType roomType = roomTypeSessionBeanRemote.findRoomTypeByName(name); 
            
            System.out.println("\n" + "Room Type name: " + roomType.getTypeName());
            System.out.println("Room Type description: " + roomType.getDescription());
            System.out.println("Room Type size: " + roomType.getSize());
            System.out.println("Room Type bed: " + roomType.getBed());
            System.out.println("Room Type capacity: " + roomType.getCapacity());
            System.out.println("Room Type amenities: " + roomType.getAmenities());
            System.out.println("Room Type status: " + roomType.getTypeStatus());
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                updateRoomType(roomType);
            }
            else if(response == 2)
            {
                deleteRoomType(roomType);
            }
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
            System.out.print("Press any key to continue...> ");
        }
    }
    
    public void updateRoomType(RoomType roomType)  
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Update Room Type ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setTypeName(input);
        }
        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setDescription(input);
        }
        System.out.print("Enter Size - in square meters (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            while(true){
                try
                {
                    roomType.setSize((input));
                    break;
                } 
                catch (NumberFormatException e) 
                {
                    System.out.println("Please enter a number");
                }
            }
        }
        System.out.print("Enter Bed (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setBed(input);
        }
        System.out.print("Enter Capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            while(true){
                try
                {
                    roomType.setCapacity(input);
                    break;
                } 
                catch (NumberFormatException e) 
                {
                    System.out.println("Please enter a number");
                }
            }
        }
        System.out.print("Enter Amenities (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setAmenities(input);
        }
        
        System.out.println(roomType.getBed()); 
        
        try 
        {
            roomTypeSessionBeanRemote.updateRoomType(roomType);
            System.out.println("Room Type updated successfully!\n");
        } 
        catch (RoomTypeNotFoundException | UpdateRoomTypeException ex) 
        {
            System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomType(RoomType roomType) 
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Delete Room Type ***\n");
        System.out.println("Confirm Delete Room Type %s (Room Type ID: %d) (Enter 'Y' to Delete)> ");
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type deleted successfully!\n");
            }
            catch (RoomTypeNotFoundException | DeleteRoomTypeException ex) 
            {
                System.out.println("An error has occurred while deleting Room Type: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    public void viewAllRoomTypes()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Room Type ***\n");
        
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
        System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", "Room Type Id", "Name", "Description", "Size", "Bed", "Capacity", "Amenities", "Status");

        for(RoomType roomType:roomTypes)
        {
            System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", roomType.getRoomTypeId().toString(), roomType.getTypeName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities(), roomType.getTypeStatus());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void createRoom()   
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
            RoomRecord newRoom = new RoomRecord();
            
            System.out.println("*** HoRS :: Hotel Management System :: Create New Room ***\n");
            System.out.print("Enter Room Number (room floor + room number)> ");
            newRoom.setRoomNum(scanner.nextInt());
            scanner.nextLine();
            System.out.print("Enter Room Type Name> ");
            String roomTypeName = scanner.nextLine().trim();
            RoomType roomType = roomTypeSessionBeanRemote.findRoomTypeByName(roomTypeName);
            newRoom.setRoomType(roomType);
            System.out.println("Open room for room type: " + roomType.getTypeName()+ "\n");
            newRoom.setRoomStatus("not in use");
            try {
            newRoom = roomRecordSessionBeanRemote.createRoomRecord(newRoom, roomTypeName);
            System.out.println(newRoom.getRoomNum()); 
            System.out.println("New room created successfully!: " + newRoom.getRoomNum()+ "\n");
            }
            catch(RoomTypeNotFoundException ex)
            {
                System.out.println("ffs..."); 
                System.out.println(ex.getMessage() + "\n");
            }
            
            
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println(ex.getMessage() + "!\n");
        } 
    }

    public void updateRoom() 
    {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS :: Hotel Management System :: Update Room ***\n");
            System.out.print("Enter Room Number> ");
            Long roomRecordId = scanner.nextLong();
            scanner.nextLine();
            RoomRecord room = roomRecordSessionBeanRemote.findRoomRecordById(roomRecordId);
            System.out.print("Enter Room Type name (blank if no change)> ");
            input = scanner.nextLine();
            String roomTypeName = "";
            if(input.length() > 0)
            {
                roomTypeName = input;
            }
            while(true)
            {
                System.out.print("Choose Room Status (1. available, 2.occupied)> ");
                Integer roomStatusInt = scanner.nextInt();
                
                if(roomStatusInt >= 1 && roomStatusInt <= 2)
                {
                    if(roomStatusInt == 1)
                    {
                        room.setRoomStatus("available");
                        Long resLineItemId = null;
                        try {
                        roomRecordSessionBeanRemote.updateRoomRecord(room);
                        RoomRecord roomUpdated = roomRecordSessionBeanRemote.findRoomRecordById(room.getRoomRecordId()); 
                        roomRecordSessionBeanRemote.updateRoomRecordListInRoomType(roomUpdated.getRoomRecordId());
                        }
                        catch (RoomTypeNotFoundException | RoomRecordNotFoundException ex){
                        }
                        System.out.println("Room Type updated successfully!\n");
                        break;
                    }
                    else
                    { 
                        scanner.nextLine();
                        System.out.print("Enter Reservation Line Item Id> ");
                        Long resLineItemId = scanner.nextLong();
                        if(resLineItemId != null)
                        {
                            room.setRoomStatus("in use");
                            try {
                            roomRecordSessionBeanRemote.updateRoomRecord(room); 
                            RoomRecord roomUpdated = roomRecordSessionBeanRemote.findRoomRecordById(room.getRoomRecordId());
                            roomRecordSessionBeanRemote.updateRoomRecordListInRoomType(roomUpdated.getRoomRecordId());
                            } 
                            catch (RoomRecordNotFoundException | RoomTypeNotFoundException ex){
                                System.out.println("RoomNumber is Invalid");
                            }
                            System.out.println("Room Type updated successfully!\n");
                        }
                        else
                        {
                            System.out.println("Please enter a valid reservation line item Id.");
                        }
                        break;
                    }
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        } catch (RoomRecordNotFoundException ex) {
                System.out.println("An error has occurred while updating room : " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoom() 
    {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS :: Hotel Management System :: Delete Room ***\n");
            System.out.print("Enter Room Number> ");
            Long roomRecordId = scanner.nextLong();
            scanner.nextLine(); 
            try {
            RoomRecord room = roomRecordSessionBeanRemote.findRoomRecordById(roomRecordId);
            System.out.printf("Confirm Delete Room Number %d (Enter 'Y' to Delete)> ", room.getRoomNum());
            input = scanner.nextLine().trim();
            
            if(input.equals("Y")) 
            {
                try {
                roomRecordSessionBeanRemote.deleteRoomRecord(room.getRoomRecordId());
                } 
                catch(DeleteRoomRecordException| RoomRecordNotFoundException ex) {
                    
                }
                System.out.println("Room deleted successfully!\n");
            }
            else 
            {
                System.out.println("Room NOT deleted!\n");
            }
            }
            
            catch(RoomRecordNotFoundException ex){
            }
            
    }

    public void viewAllRooms() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Rooms ***\n");
        
        List<RoomRecord> rooms = roomRecordSessionBeanRemote.findAllRoomRecords();
        System.out.printf("%12s%12s%20s%20s\n", "Room Number", "Room Status", "Room Type", "Room Reservation Id");
        try{
            for(RoomRecord roomRecord:rooms)
            {
                if(roomRecord.getReservationLineItem() != null){
                    System.out.printf("%12s%12s%20s%20s\n", roomRecord.getRoomNum().toString(), roomRecord.getRoomStatus(),
                            roomRecord.getRoomType().getTypeName(), roomRecord.getReservationLineItem().get(0).toString());
                }
                else
                {
                    System.out.printf("%12s%12s%20s%20s\n", roomRecord.getRoomNum().toString(), roomRecord.getRoomStatus(),
                            roomRecord.getRoomType().getTypeName(), "none");
                }
            }
        }catch(NullPointerException e) 
        { 
            System.out.print("NullPointerException Caught"); 
        } 
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void viewRoomAllocationExceptionReport() //incomplete
    {
      
    }

    public void createRoomRate() 
    {
            Scanner scanner = new Scanner(System.in);
            RoomRate newRoomRate;
            
            System.out.println("*** HoRS :: Hotel Management System :: Create New Room Rate ***\n");
            while(true)
            {
                System.out.print("Select Room Rate Type (1. Normal Rate, 2. Published Rate, 3. Promotion Rate, 4. Peak Rate)> ");
                Integer roomRateTypeInt = scanner.nextInt();
                if(roomRateTypeInt >= 1 && roomRateTypeInt <= 4){
                    newRoomRate = new RoomRate(); 
                    if(roomRateTypeInt == 1)
                    {
                        newRoomRate.setRoomRateType(RoomRateTypeEnum.NORMAL);
                        break;
                    }else if(roomRateTypeInt == 2)
                    {
                        newRoomRate.setRoomRateType(RoomRateTypeEnum.PUBLISHED);
                        break;
                    }else if(roomRateTypeInt == 3)
                    {
                        newRoomRate.setRoomRateType(RoomRateTypeEnum.PROMOTION);
                        break;
                    }else if(roomRateTypeInt == 4)
                    {
                        newRoomRate.setRoomRateType(RoomRateTypeEnum.PEAK);
                        break;
                    }
                    else
                    {
                        System.out.println("Sorry, this room rate type is currently not available. Please try again!\n");
                    }
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            scanner.nextLine();
            
            System.out.print("Enter Room Rate name> ");
            newRoomRate.setRateName(scanner.nextLine());
            
            System.out.print("Enter Room Rate per night> ");
            newRoomRate.setRatePerNight(scanner.nextBigDecimal());
            
            System.out.print("Enter Room Type Id> ");
            Long roomTypeId = scanner.nextLong();
            try {
            RoomType roomType = roomTypeSessionBeanRemote.findRoomTypeById(roomTypeId);
            scanner.nextLine();
            
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
            if(newRoomRate.getRoomRateType().equals(PROMOTION)){
                System.out.print("Enter Promotion Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setStartRateDate(date);
                System.out.print("Enter Promotion Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setEndRateDate(date);
            }
            else if(newRoomRate.getRoomRateType().equals(PEAK)){
                System.out.print("Enter Peak Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setStartRateDate(date);
                System.out.print("Enter Peak Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setEndRateDate(date);
            }
            else if(newRoomRate.getRoomRateType().equals(NORMAL)){
                System.out.print("Enter Normal Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setStartRateDate(date);
                System.out.print("Enter Normal Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setEndRateDate(date);
            }
            if(newRoomRate.getRoomRateType().equals(PUBLISHED)){
                System.out.print("Enter Published Rate validity date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                newRoomRate.setStartRateDate(date);
            }
            }
            catch (ParseException ex) {
            }
            System.out.println("Open room rate for room type: " + roomType.getTypeName()+ "\n");
            newRoomRate.setRoomRateStatus("enabled");
            } catch (RoomTypeNotFoundException ex) {
            }
            try{
            newRoomRate = roomRateSessionBeanRemote.createRoomRate(newRoomRate, roomTypeId);
            System.out.println("New room rate created successfully!: " + newRoomRate.getRoomRateId()+ "\n");
        } 
        catch (RoomTypeNotFoundException | RoomNameExistsException | UnknownPersistenceException | RoomRateExistsException ex) {
            
        }
    }

    public void viewRoomRateDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS :: Hotel Management System :: View Room Rate Details ***\n");
        
        System.out.print("Enter Room Rate Id> ");
        Long roomRateId = scanner.nextLong();
        scanner.nextLine();
        
        try 
        {
            RoomRate roomRate = roomRateSessionBeanRemote.findRoomRateById(roomRateId); 
            
            System.out.println("\n" + "Room rate name: " + roomRate.getRateName());
            System.out.println("Room rate per night: " + roomRate.getRatePerNight().toString());
            System.out.println("Room rate type : " + roomRate.getRoomType().getTypeName());
            System.out.println("Room rate status: " + roomRate.getRoomRateStatus());
            if(roomRate.getRoomRateType().equals(NORMAL)){
                System.out.println("Room rate start date: " + roomRate.getStartRateDate().toString());
                System.out.println("Room rate end date: " + roomRate.getEndRateDate().toString());
            }
            if(roomRate.getRoomRateType().equals(PUBLISHED)){
                System.out.println("Room rate validity date: " + roomRate.getStartRateDate().toString());
            }
            if(roomRate.getRoomRateType().equals(PROMOTION)){
            {
                System.out.println("Room rate start date: " + roomRate.getStartRateDate().toString());
                System.out.println("Room rate end date: " + roomRate.getEndRateDate().toString());
            }
            if(roomRate.getRoomRateType().equals(PEAK)){
            {
                System.out.println("Room rate start date: " + roomRate.getStartRateDate().toString());
                System.out.println("Room rate end date: " +  roomRate.getEndRateDate().toString());
            }
            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                try{
                    roomRateSessionBeanRemote.updateRoomRate(roomRate);
                } 
                catch(RoomRateNotFoundException| RoomTypeNotFoundException ex) {
                }
            }
            else if(response == 2)
            {
                try{
                     roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                }
                catch(DeleteRoomRateException|RoomRateNotFoundException ex) {
                }
               
                } 
            }
          }
        }
        catch (RoomRateNotFoundException ex) { 
        }
    }
    public void updateRoomRate(RoomRate roomRate) 
    {
        try {
            Scanner scanner = new Scanner(System.in);        
            String input;

            System.out.println("*** HoRS :: Hotel Management System :: Update Room Rate ***\n");
            System.out.print("Enter Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                roomRate.setRateName(input);
                }
            System.out.print("Enter Rate per night (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                roomRate.setRatePerNight(new BigDecimal(input));
            }
            System.out.print("Enter Rate's Room type name (blank if no change)> ");
            input = scanner.nextLine();
            String roomTypeName = "";
            if(input.length() > 0)
            {
                roomTypeName = input;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(roomRate.getRoomRateType().equals(NORMAL)){
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    roomRate.setStartRateDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    roomRate.setEndRateDate(sdf.parse(input));
                }
            }
            else if(roomRate.getRoomRateType().equals(PUBLISHED))
            {
                System.out.print("Enter Rate Validity date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    roomRate.setStartRateDate(sdf.parse(input));
                }
            }
            else if (roomRate.getRoomRateType().equals(PROMOTION))
            {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    (roomRate).setStartRateDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    (roomRate).setEndRateDate(sdf.parse(input));
                }
            }
            else if (roomRate.getRoomRateType().equals(PEAK))
            {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    (roomRate).setStartRateDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    (roomRate).setEndRateDate(sdf.parse(input));
                }
            }
            if(roomRate.getRoomRateStatus().equals("disabled")){
                System.out.print("Room rate is currently disabled, enable it? (Enter 'Y' to Enable)> ");
                input = scanner.nextLine().trim();
                if(input.equals("Y")){
                    roomRate.setRoomRateStatus("enabled");
                }
            }
            
            try {
                roomRateSessionBeanRemote.updateRoomRate(roomRate);
            }
            catch(RoomRateNotFoundException ex){
                System.out.println("Exception caught"); 
            }
            
            RoomRate roomRateUpdated = roomRateSessionBeanRemote.findRoomRateById(roomRate.getRoomRateId()); 
            
            roomRateSessionBeanRemote.updateRoomRateListInRoomType(roomRateUpdated.getRoomRateId());
            System.out.println("Room Rate updated successfully!\n");
            
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        } catch (RoomTypeNotFoundException | RoomRateNotFoundException ex) {
                System.out.println("An error has occurred while updating room rate: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomRate(RoomRate roomRate) 
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getRateName(), roomRate.getRoomRateId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            }
            catch (RoomRateNotFoundException | DeleteRoomRateException ex) 
            {
                System.out.println("An error has occurred while deleting Room Rate: " + ex.getMessage() + "\n");
            } 
        }
        else
        {
            System.out.println("Room Rate NOT deleted!\n");
        }
    }

    private void allocateRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void viewAllRoomRates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
}
