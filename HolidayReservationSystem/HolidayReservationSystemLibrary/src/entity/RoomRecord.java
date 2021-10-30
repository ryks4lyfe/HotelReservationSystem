/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author ajayan
 */
@Entity
public class RoomRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRecordId;
    private Integer roomNum; 
    private String roomStatus; 
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private ReservationLineItem reservationLineItem;
    @ManyToOne
    @JoinColumn(nullable = false)
    private RoomType roomType; 
    

    public RoomRecord() {
    }

    public RoomRecord(Long roomRecordId, Integer roomNum, String roomStatus, ReservationLineItem reservationLineItem, RoomType roomType) {
        this.roomRecordId = roomRecordId;
        this.roomNum = roomNum;
        this.roomStatus = roomStatus;
        this.reservationLineItem = reservationLineItem;
        this.roomType = roomType;
    }
    

    public Long getRoomRecordId() {
        return roomRecordId;
    }

    public void setRoomRecordId(Long roomRecordId) {
        this.roomRecordId = roomRecordId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRecordId != null ? roomRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRecordId fields are not set
        if (!(object instanceof RoomRecord)) {
            return false;
        }
        RoomRecord other = (RoomRecord) object;
        if ((this.roomRecordId == null && other.roomRecordId != null) || (this.roomRecordId != null && !this.roomRecordId.equals(other.roomRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRecord[ id=" + roomRecordId + " ]";
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public ReservationLineItem getReservationLineItem() {
        return reservationLineItem;
    }

    public void setReservationLineItem(ReservationLineItem reservationLineItem) {
        this.reservationLineItem = reservationLineItem;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
}
