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
public class PublishedRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date startDate;
    private Date endDate; 

    public PublishedRate() {
        super();
    }

    public PublishedRate(Date startDate, Date endDate, Long roomRateId, String rateName, BigDecimal ratePerNight, String roomStatus, RoomType roomType) {
        super(roomRateId, rateName, ratePerNight, roomStatus, roomType);
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (startDate != null ? startDate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the startDate fields are not set
        if (!(object instanceof PublishedRate)) {
            return false;
        }
        PublishedRate other = (PublishedRate) object;
        if ((this.startDate == null && other.startDate != null) || (this.startDate != null && !this.startDate.equals(other.startDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PublishedRate[ id=" + startDate + " ]";
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
}*/

