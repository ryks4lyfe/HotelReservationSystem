/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author 65912
 */
@Entity
public class WalkInReservation extends ReservationRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne(optional = true)
    @JoinColumn(nullable=true)
    private Employee employee;
    
    public WalkInReservation() {
        super();
    }

    public WalkInReservation(Employee employee, Date reservationDate) {
        super(reservationDate);
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WalkInReservation)) {
            return false;
        }
        WalkInReservation other = (WalkInReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.WalkInReservation[ id=" + reservationId + " ]";
    }
    
}
