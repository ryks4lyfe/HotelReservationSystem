/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.FailedLoginException;

/**
 *
 * @author 65912
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    public EmployeeSessionBean() {
        
    }
    
    //Create and persist Employee, no need to add Reservation
    @Override
    public Long createEmployee(Employee e) {
        em.persist(e);
        em.flush();
        
        return e.getEmployeeId();
    }
    
    //Search via Id
    @Override
    public Employee findEmployeeById(Long employeeId) throws EmployeeNotFoundException {
        Employee e = em.find(Employee.class, employeeId);
        
        if(e!= null) {
            return e;
        } else {
            throw new EmployeeNotFoundException("Error, Employee " + employeeId + " does not exist.");
        }
    }
    
    //Search via username
    @Override
    public Employee findEmployeeByUsername(String username) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (Employee)query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Error, Employee with username " + username + " does not exist.");
        }
    }
    
    
    //Tries to find e with username, if doesnt exists throw an error. Continue to check if password same,
    //if wrong throw an error, else, return the e.
    @Override
    public Employee doLogin(String username, String password) throws FailedLoginException, EmployeeNotFoundException {
        try {
            Employee e = findEmployeeByUsername(username);
            if(e.getPassword().equals(password)) {
                e.getWalkInReservations().size();
                return e;
            } else {
                throw new FailedLoginException("Error, please try logging in again with a different username or password!");
            }
        } catch(EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException("Error, Employee with username" + username + " does not exist.");
        }
    }
    
    @Override
    public List<Employee> retrieveListOfEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        return query.getResultList();
    }

    
}


   
