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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author ajayan
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(nullable = false)
    private String typeName; 
    @Column(nullable = false)
    private String description; 
    @Column(nullable = false)
    private String size; 
    @Column(nullable = false)
    private String bed; 
    @Column(nullable = false)
    private String amenities; 
    @Column(nullable = false)
    private String capacity; 
    @Column(nullable = false)
    private String typeStatus; 
    @Column(nullable = false) 
    private String rankRoom; 
    
    
    @OneToMany (mappedBy = "roomType", cascade = {}, fetch = FetchType.EAGER)
    private List<RoomRecord> roomRecords; 
    @OneToMany (mappedBy = "roomType", cascade = {}, fetch = FetchType.EAGER)
    private List<RoomRate> roomRates;    
    @OneToMany(mappedBy = "roomType", cascade = {}, fetch = FetchType.EAGER)
    private List<ReservationLineItem> lineItems;
    
    
    public RoomType() {
        
        roomRecords = new ArrayList<>();
        roomRates = new ArrayList<>();   
        lineItems = new ArrayList<>();
    }

    public RoomType(String typeName, String description, String size, String bed, String amenities, String capacity, String typeStatus, String rankRoom) {
        this();
        this.typeName = typeName;
        this.description = description;
        this.size = size;
        this.bed = bed;
        this.amenities = amenities;
        this.capacity = capacity;
        this.typeStatus = typeStatus;
        this.rankRoom = rankRoom; 
    }

    public List<ReservationLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<ReservationLineItem> lineItems) {
        this.lineItems = lineItems;
    }

  
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(String typeStatus) {
        this.typeStatus = typeStatus;
    }

    public List<RoomRecord> getRoomRecords() {
        return roomRecords;
    }

    public void setRoomRecords(List<RoomRecord> roomRecords) {
        this.roomRecords = roomRecords;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getRankRoom() {
        return rankRoom;
    }

    public void setRankRoom(String rankRoom) {
        this.rankRoom = rankRoom;
    }


    
}
