/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Employee;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static util.enumeration.EmployeeAccessRight.GUEST_RELATION_OFFICER;
import static util.enumeration.EmployeeAccessRight.OPERATION_MANAGER;
import static util.enumeration.EmployeeAccessRight.SALES_MANAGER;
import static util.enumeration.EmployeeAccessRight.SYSTEM_ADMINISTRATOR;
import util.enumeration.RoomRateTypeEnum;
import static util.enumeration.RoomRateTypeEnum.NORMAL;
import static util.enumeration.RoomRateTypeEnum.PUBLISHED;

/**
 *
 * @author 65912
 */
@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {

        if (em.find(Employee.class, 1l) == null) {
            initializeEmployees();
        }
        if (em.find(RoomType.class, 1l) == null && em.find(RoomRate.class, 1l) == null && em.find(RoomRecord.class, 1l) == null) {
            initializeRoomTypeRateRecord();
        }
        /*if (em.find(RoomRate.class, 1l) == null) {
            initializeRoomRate();
        }
        if (em.find(RoomRecord.class, 1l) == null) {
            initializeRoomRecord();
        }*/
    }
    
    

    public void initializeEmployees() {
        em.persist(new Employee("sysadmin", "password", SYSTEM_ADMINISTRATOR));
        em.persist(new Employee("opmanager", "password", OPERATION_MANAGER));
        em.persist(new Employee("salesmanager", "password", SALES_MANAGER));
        em.persist(new Employee("guestrelo", "password", GUEST_RELATION_OFFICER));
        em.flush();
    }
    
    public void initializeRoomTypeRateRecord() {
        RoomType deluxeRoom = new RoomType("Deluxe Room", "Deluxe", "5", "5", "5", "5", "available", "5"); 
        em.persist(deluxeRoom);
        RoomType premierRoom = new RoomType("Premier Room", "Premier", "5", "5", "5", "5", "available", "4"); 
        em.persist(premierRoom);
        RoomType familyRoom = new RoomType("Family Room", "Family", "5", "5", "5", "5", "available", "3"); 
        em.persist(familyRoom);
        RoomType juniorSuite = new RoomType("Junior Suite", "Junior", "5", "5", "5", "5", "available", "2"); 
        em.persist(juniorSuite);
        RoomType grandSuite = new RoomType("Grand Suite", "Grand", "5", "5", "5", "5", "available", "1"); 
        em.persist(grandSuite);
        
        
        em.persist(new RoomRate("Deluxe Room Published", BigDecimal.valueOf(100), "available", new Date(), new Date(), PUBLISHED, deluxeRoom));
        em.persist(new RoomRate("Deluxe Room Normal", BigDecimal.valueOf(50), "available", new Date(), new Date(), NORMAL, deluxeRoom));

        em.persist(new RoomRate("Premier Room Published", BigDecimal.valueOf(200), "available", new Date(), new Date(), PUBLISHED, premierRoom));
        em.persist(new RoomRate("Premier Room Normal", BigDecimal.valueOf(100), "available", new Date(), new Date(), NORMAL, premierRoom));

        em.persist(new RoomRate("Family Room Published", BigDecimal.valueOf(300), "available", new Date(), new Date(), PUBLISHED, familyRoom));
        em.persist(new RoomRate("Family Room Normal", BigDecimal.valueOf(150), "available", new Date(), new Date(), NORMAL, familyRoom));

        em.persist(new RoomRate("Junior Suite Published", BigDecimal.valueOf(400), "available", new Date(), new Date(), PUBLISHED, juniorSuite));
        em.persist(new RoomRate("Junior Suite Normal", BigDecimal.valueOf(200), "available", new Date(), new Date(), NORMAL, juniorSuite));

        em.persist(new RoomRate("Grand Suite Published", BigDecimal.valueOf(500), "available", new Date(), new Date(), PUBLISHED, grandSuite));
        em.persist(new RoomRate("Grand Suite Normal", BigDecimal.valueOf(250), "available", new Date(), new Date(), NORMAL, grandSuite));
        
        em.persist(new RoomRecord(0101, "available", deluxeRoom));
        em.persist(new RoomRecord(0201, "available", deluxeRoom));
        em.persist(new RoomRecord(0301, "available", deluxeRoom));
        em.persist(new RoomRecord(0401, "available", deluxeRoom));
        em.persist(new RoomRecord(0501, "available", deluxeRoom));

        em.persist(new RoomRecord(0102, "available", familyRoom));
        em.persist(new RoomRecord(0202, "available", familyRoom));
        em.persist(new RoomRecord(0302, "available", familyRoom));
        em.persist(new RoomRecord(0402, "available", familyRoom));
        em.persist(new RoomRecord(0502, "available", familyRoom));

        em.persist(new RoomRecord(0103, "available", familyRoom));
        em.persist(new RoomRecord(0203, "available", familyRoom));
        em.persist(new RoomRecord(0303, "available", familyRoom));
        em.persist(new RoomRecord(0403, "available", familyRoom));
        em.persist(new RoomRecord(0503, "available", familyRoom));

        em.persist(new RoomRecord(0104, "available", juniorSuite));
        em.persist(new RoomRecord(0204, "available", juniorSuite));
        em.persist(new RoomRecord(0304, "available", juniorSuite));
        em.persist(new RoomRecord(0404, "available", juniorSuite));
        em.persist(new RoomRecord(0504, "available", juniorSuite));

        em.persist(new RoomRecord(0105, "available", grandSuite));
        em.persist(new RoomRecord(0205, "available", grandSuite));
        em.persist(new RoomRecord(0305, "available", grandSuite));
        em.persist(new RoomRecord(0405, "available", grandSuite));
        em.persist(new RoomRecord(0505, "available", grandSuite));
        
        em.flush();
        
    }
    
    /*public void initializeRoomRate() {
        RoomType deluxeRoom = em.find(RoomType.class, 1l);
        RoomType premierRoom = em.find(RoomType.class, 2l);
        RoomType familyRoom = em.find(RoomType.class, 3l);
        RoomType juniorSuite = em.find(RoomType.class, 4l);
        RoomType grandSuite = em.find(RoomType.class, 5l);

        

        em.flush();
    }
    
    public void initializeRoomRecord() {
        RoomType deluxeRoom = em.find(RoomType.class, 1l);
        RoomType premierRoom = em.find(RoomType.class, 2l);
        RoomType familyRoom = em.find(RoomType.class, 3l);
        RoomType juniorSuite = em.find(RoomType.class, 4l);
        RoomType grandSuite = em.find(RoomType.class, 5l);

        em.persist(new RoomRecord(0101, "available", deluxeRoom));
        em.persist(new RoomRecord(0201, "available", deluxeRoom));
        em.persist(new RoomRecord(0301, "available", deluxeRoom));
        em.persist(new RoomRecord(0401, "available", deluxeRoom));
        em.persist(new RoomRecord(0501, "available", deluxeRoom));

        em.persist(new RoomRecord(0102, "available", familyRoom));
        em.persist(new RoomRecord(0202, "available", familyRoom));
        em.persist(new RoomRecord(0302, "available", familyRoom));
        em.persist(new RoomRecord(0402, "available", familyRoom));
        em.persist(new RoomRecord(0502, "available", familyRoom));

        em.persist(new RoomRecord(0103, "available", familyRoom));
        em.persist(new RoomRecord(0203, "available", familyRoom));
        em.persist(new RoomRecord(0303, "available", familyRoom));
        em.persist(new RoomRecord(0403, "available", familyRoom));
        em.persist(new RoomRecord(0503, "available", familyRoom));

        em.persist(new RoomRecord(0104, "available", juniorSuite));
        em.persist(new RoomRecord(0204, "available", juniorSuite));
        em.persist(new RoomRecord(0304, "available", juniorSuite));
        em.persist(new RoomRecord(0404, "available", juniorSuite));
        em.persist(new RoomRecord(0504, "available", juniorSuite));

        em.persist(new RoomRecord(0105, "available", grandSuite));
        em.persist(new RoomRecord(0205, "available", grandSuite));
        em.persist(new RoomRecord(0305, "available", grandSuite));
        em.persist(new RoomRecord(0405, "available", grandSuite));
        em.persist(new RoomRecord(0505, "available", grandSuite));
        em.flush();
    }*/

}

