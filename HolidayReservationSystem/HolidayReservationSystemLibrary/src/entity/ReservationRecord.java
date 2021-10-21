/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author 65912
 */
@Entity
public abstract class ReservationRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    protected Long reservationId;
    @Column(nullable=false)
    protected Date reservationDate;
    
    
    //protected List<ReservationLineItem> reservationLineItems;
    
    public ReservationRecord() {
        //reservationLineItems = new ArrayList<>();
    }
    
    public ReservationRecord(Date reservationDate) {
        //this();
        this.reservationDate = reservationDate;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

   // public List<ReservationLineItem> getReservationLineItems() {
      //  return reservationLineItems;
   // }

   // public void setReservationLineItems(List<ReservationLineItem> reservationLineItems) {
    //    this.reservationLineItems = reservationLineItems;
   // }
    
    

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof ReservationRecord)) {
            return false;
        }
        ReservationRecord other = (ReservationRecord) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationRecord[ id=" + reservationId + " ]";
    }
    
}
