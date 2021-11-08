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
    }

    public void initializeEmployees() {
        em.persist(new Employee("sysadmin", "password", SYSTEM_ADMINISTRATOR));
        em.persist(new Employee("opmanager", "password", OPERATION_MANAGER));
        em.persist(new Employee("salesmanager", "password", SALES_MANAGER));
        em.persist(new Employee("guestrelo", "password", GUEST_RELATION_OFFICER));
        em.flush();
    }

}

