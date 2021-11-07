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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
    
    
    @OneToMany(mappedBy = "room", cascade = {}, fetch = FetchType.EAGER)
    private List<ReservationLineItem> reservationLineItem;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private RoomType roomType; 
    

    public RoomRecord() {
        reservationLineItem = new ArrayList<>();
    }

    public RoomRecord(Integer roomNum, String roomStatus, RoomType roomType) {
        this();
        this.roomNum = roomNum;
        this.roomStatus = roomStatus;
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

    public List<ReservationLineItem> getReservationLineItem() {
        return reservationLineItem;
    }

    public void setReservationLineItem(List<ReservationLineItem> reservationLineItem) {
        this.reservationLineItem = reservationLineItem;
    }


    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
}
