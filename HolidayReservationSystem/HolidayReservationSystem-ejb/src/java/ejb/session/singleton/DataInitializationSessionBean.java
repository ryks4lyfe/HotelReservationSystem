package ejb.session.singleton;

import entity.Employee;
import entity.RoomRate;
import entity.RoomRecord;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.ejb.TimerService;
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
        
    }

    public void initializeEmployees() {
        em.persist(new Employee("sysadmin", "password", SYSTEM_ADMINISTRATOR));
        em.persist(new Employee("opmanager", "password", OPERATION_MANAGER));
        em.persist(new Employee("salesmanager", "password", SALES_MANAGER));
        em.persist(new Employee("guestrelo", "password", GUEST_RELATION_OFFICER));
        em.flush();
    }

    public void initializeRoomTypeRateRecord() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

        try {
            RoomRate d1 = new RoomRate("Deluxe Room Published", BigDecimal.valueOf(100), "available", new Date(), sdf.parse("2999-01-01"), PUBLISHED, deluxeRoom);
            deluxeRoom.getRoomRates().add(d1);
            em.persist(d1);

            RoomRate d2 = new RoomRate("Deluxe Room Normal", BigDecimal.valueOf(50), "available", new Date(), sdf.parse("2999-01-01"), NORMAL, deluxeRoom);
            deluxeRoom.getRoomRates().add(d2);
            em.persist(d2);

            RoomRate p1 = new RoomRate("Premier Room Published", BigDecimal.valueOf(200), "available", new Date(), sdf.parse("2999-01-01"), PUBLISHED, premierRoom);
            premierRoom.getRoomRates().add(p1);
            em.persist(p1);

            RoomRate p2 = new RoomRate("Premier Room Normal", BigDecimal.valueOf(100), "available", new Date(), sdf.parse("2999-01-01"), NORMAL, premierRoom);
            premierRoom.getRoomRates().add(p2);
            em.persist(p2);

            RoomRate f1 = new RoomRate("Family Room Published", BigDecimal.valueOf(300), "available", new Date(), sdf.parse("2999-01-01"), PUBLISHED, familyRoom);
            familyRoom.getRoomRates().add(f1);
            em.persist(f1);

            RoomRate f2 = new RoomRate("Family Room Normal", BigDecimal.valueOf(150), "available", new Date(), sdf.parse("2999-01-01"), NORMAL, familyRoom);
            familyRoom.getRoomRates().add(f2);
            em.persist(f2);

            RoomRate j1 = new RoomRate("Junior Suite Published", BigDecimal.valueOf(400), "available", new Date(), sdf.parse("2999-01-01"), PUBLISHED, juniorSuite);
            juniorSuite.getRoomRates().add(j1);
            em.persist(j1);

            RoomRate j2 = new RoomRate("Junior Suite Normal", BigDecimal.valueOf(200), "available", new Date(), sdf.parse("2999-01-01"), NORMAL, juniorSuite);
            juniorSuite.getRoomRates().add(j2);
            em.persist(j2);

            RoomRate g1 = new RoomRate("Grand Suite Published", BigDecimal.valueOf(500), "available", new Date(), sdf.parse("2999-01-01"), PUBLISHED, grandSuite);
            grandSuite.getRoomRates().add(g1);
            em.persist(g1);

            RoomRate g2 = new RoomRate("Grand Suite Normal", BigDecimal.valueOf(250), "available", new Date(), sdf.parse("2999-01-01"), NORMAL, grandSuite);
            grandSuite.getRoomRates().add(g2);
            em.persist(g2);
        } catch (ParseException ex) {
            System.out.println("Time wrong");
        }

        List<RoomRecord> deluxeRooms = new ArrayList<>();
        deluxeRooms.add(new RoomRecord("0101", "available", deluxeRoom));
        deluxeRooms.add(new RoomRecord("0201", "available", deluxeRoom));
        deluxeRooms.add(new RoomRecord("0301", "available", deluxeRoom));
        deluxeRooms.add(new RoomRecord("0401", "available", deluxeRoom));
        deluxeRooms.add(new RoomRecord("0501", "available", deluxeRoom));

        for (RoomRecord r : deluxeRooms) {
            em.persist(r);
            deluxeRoom.getRoomRecords().add(r);
        }

        List<RoomRecord> premierRooms = new ArrayList<>();
        premierRooms.add(new RoomRecord("0102", "available", premierRoom));
        premierRooms.add(new RoomRecord("0202", "available", premierRoom));
        premierRooms.add(new RoomRecord("0302", "available", premierRoom));
        premierRooms.add(new RoomRecord("0402", "available", premierRoom));
        premierRooms.add(new RoomRecord("0502", "available", premierRoom));

        for (RoomRecord r : premierRooms) {
            em.persist(r);
            premierRoom.getRoomRecords().add(r);
        }

        List<RoomRecord> familyRooms = new ArrayList<>();
        familyRooms.add(new RoomRecord("0103", "available", familyRoom));
        familyRooms.add(new RoomRecord("0203", "available", familyRoom));
        familyRooms.add(new RoomRecord("0303", "available", familyRoom));
        familyRooms.add(new RoomRecord("0403", "available", familyRoom));
        familyRooms.add(new RoomRecord("0503", "available", familyRoom));

        for (RoomRecord r : familyRooms) {
            em.persist(r);
            familyRoom.getRoomRecords().add(r);
        }

        List<RoomRecord> juniorRooms = new ArrayList<>();
        juniorRooms.add(new RoomRecord("0104", "available", juniorSuite));
        juniorRooms.add(new RoomRecord("0204", "available", juniorSuite));
        juniorRooms.add(new RoomRecord("0304", "available", juniorSuite));
        juniorRooms.add(new RoomRecord("0404", "available", juniorSuite));
        juniorRooms.add(new RoomRecord("0504", "available", juniorSuite));

        for (RoomRecord r : juniorRooms) {
            em.persist(r);
            juniorSuite.getRoomRecords().add(r);
        }

        List<RoomRecord> grandRooms = new ArrayList<>();
        grandRooms.add(new RoomRecord("0105", "available", grandSuite));
        grandRooms.add(new RoomRecord("0205", "available", grandSuite));
        grandRooms.add(new RoomRecord("0305", "available", grandSuite));
        grandRooms.add(new RoomRecord("0405", "available", grandSuite));
        grandRooms.add(new RoomRecord("0505", "available", grandSuite));

        for (RoomRecord r : grandRooms) {
            em.persist(r);
            grandSuite.getRoomRecords().add(r);
        }

        em.flush();

    }

}
