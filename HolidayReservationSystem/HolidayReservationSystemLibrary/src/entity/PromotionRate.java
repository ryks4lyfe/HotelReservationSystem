/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;*/

/**
 *
 * @author ajayan
 */
/*@Entity
public class PromotionRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date startDate;
    private Date endDate; 
    
    public PromotionRate() {
        super(); 
    }

    public PromotionRate(Date startDate, Date endDate, Long roomRateId, String rateName, BigDecimal ratePerNight, String roomStatus, RoomType roomType) {
        super(roomRateId, rateName, ratePerNight, roomStatus,roomType);
        this.startDate = startDate;
        this.endDate = endDate;
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
        if (!(object instanceof PromotionRate)) {
            return false;
        }
        PromotionRate other = (PromotionRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PromotionRate[ id=" + roomRateId + " ]";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
}*/
