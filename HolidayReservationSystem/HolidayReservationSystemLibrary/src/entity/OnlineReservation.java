/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class OnlineReservation extends ReservationRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne(optional = true)
    @JoinColumn(nullable=true)
    private Guest guest;
    
    public OnlineReservation() {
        super();
    }

    public OnlineReservation(Guest guest, Date reservationDate,Integer totalLineItem, BigDecimal totalAmount) {
        super(reservationDate, totalLineItem, totalAmount);
        this.guest = guest;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
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
        if (!(object instanceof OnlineReservation)) {
            return false;
        }
        OnlineReservation other = (OnlineReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OnlineReservation[ id=" + reservationId + " ]";
    }
    
}