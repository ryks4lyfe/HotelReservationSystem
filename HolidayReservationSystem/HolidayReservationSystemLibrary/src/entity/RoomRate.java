/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import util.enumeration.RoomRateTypeEnum;

/**
 *
 * @author ajayan
 */
@Entity
public class RoomRate implements Serializable {

    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    protected Long roomRateId;
    @Column(nullable = false)
    protected String rateName; 
    @Column(nullable = false)
    protected BigDecimal ratePerNight; 
    @Column(nullable = false)
    private String roomRateStatus; 
    @Column(nullable = false)
    private Date startRateDate; 
    @Column(nullable = false)
    private Date endRateDate; 
    @Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    @NotNull
    private RoomRateTypeEnum roomRateType; 
    
    
    @ManyToOne 
    @JoinColumn(nullable = false)
    private RoomType roomType; 

    public RoomRate() {
    }

    public RoomRate(Long roomRateId, String rateName, BigDecimal ratePerNight, String roomRateStatus, Date startRateDate, Date endRateDate, RoomRateTypeEnum roomRateType, RoomType roomType) {
        this.roomRateId = roomRateId;
        this.rateName = rateName;
        this.ratePerNight = ratePerNight;
        this.roomRateStatus = roomRateStatus;
        this.startRateDate = startRateDate;
        this.endRateDate = endRateDate;
        this.roomRateType = roomRateType;
        this.roomType = roomType;
    }

    
   
    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomRateTypeEnum getRoomRateType() {
        return roomRateType;
    }

    public void setRoomRateType(RoomRateTypeEnum roomRateType) {
        this.roomRateType = roomRateType;
    }
    
    public Date getStartRateDate() {
        return startRateDate;
    }

    public void setStartRateDate(Date startRateDate) {
        this.startRateDate = startRateDate;
    }

    public Date getEndRateDate() {
        return endRateDate;
    }

    public void setEndRateDate(Date endRateDate) {
        this.endRateDate = endRateDate;
    }

    public String getRoomRateStatus() {
        return roomRateStatus;
    }

    public void setRoomRateStatus(String roomRateStatus) {
        this.roomRateStatus = roomRateStatus;
    }

    
}
