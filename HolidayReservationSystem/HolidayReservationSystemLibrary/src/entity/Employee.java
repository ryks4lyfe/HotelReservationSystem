/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import util.enumeration.EmployeeAccessRight;

/**
 *
 * @author 65912
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    @Column(nullable = false, length = 32)
    private String password;
    @Enumerated(EnumType.STRING)
    private EmployeeAccessRight Enum;
    
    @OneToMany(mappedBy = "employee", cascade = {}, fetch = FetchType.EAGER)
    private List<WalkInReservation> walkInReservations;
    
    public Employee() {
        walkInReservations = new ArrayList<>();
    }

    public Employee(String username, String password, EmployeeAccessRight Enum) {
        this();
        this.username = username;
        this.password = password;
        this.Enum = Enum;
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeAccessRight getEnum() {
        return Enum;
    }

    public void setEnum(EmployeeAccessRight Enum) {
        this.Enum = Enum;
    }

    public List<WalkInReservation> getWalkInReservations() {
        return walkInReservations;
    }

    public void setWalkInReservations(List<WalkInReservation> walkInReservations) {
        this.walkInReservations = walkInReservations;
    }
    
    
    
    

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ id=" + employeeId + " ]";
    }
    
}
