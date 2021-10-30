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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author ajayan
 */
@Entity
public class ReservationLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationLineItemId;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date checkInDate; 
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date checkOutDate; 
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private RoomType roomType;
    @OneToMany(mappedBy = "reservationLineItem")
    @JoinColumn(nullable = false)
    private List<RoomRecord> roomRecords; 
    @OneToMany
    @JoinColumn(nullable = false)
    private List<RoomRate> roomRates; 


    public ReservationLineItem() {
        roomRecords = new ArrayList<>(); 
        roomRates = new ArrayList<>(); 
    }

    public ReservationLineItem(Long reservationLineItemId, Date checkInDate, Date checkOutDate, RoomType roomType, List<RoomRecord> roomRecords, List<RoomRate> roomRates) {
        this.reservationLineItemId = reservationLineItemId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.roomRecords = roomRecords;
        this.roomRates = roomRates;
    }

    

    public Long getReservationLineItemId() {
        return reservationLineItemId;
    }

    public void setReservationLineItemId(Long reservationLineItemId) {
        this.reservationLineItemId = reservationLineItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationLineItemId != null ? reservationLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationLineItemId fields are not set
        if (!(object instanceof ReservationLineItem)) {
            return false;
        }
        ReservationLineItem other = (ReservationLineItem) object;
        if ((this.reservationLineItemId == null && other.reservationLineItemId != null) || (this.reservationLineItemId != null && !this.reservationLineItemId.equals(other.reservationLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationLineItem[ id=" + reservationLineItemId + " ]";
    }

     public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }    
    
    public List<RoomRecord> getRoomRates() {
        return roomRecords;
    }

    public void setRoomRates(List<RoomRecord> roomRates) {
        this.roomRecords = roomRecords;
    }
}
