/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeNotFoundException;
import util.exception.FailedLoginException;

/**
 *
 * @author 65912
 */
@Remote
public interface EmployeeSessionBeanRemote {
    public Employee doLogin(String username, String password) throws FailedLoginException, EmployeeNotFoundException;

    public List<Employee> retrieveListOfEmployees();

    public Employee findEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public Employee findEmployeeById(Long employeeId) throws EmployeeNotFoundException;

    public Long createEmployee(Employee e);
}
