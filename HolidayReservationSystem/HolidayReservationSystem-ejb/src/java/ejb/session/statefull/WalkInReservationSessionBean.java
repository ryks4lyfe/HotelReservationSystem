/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.statefull;

import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Employee;
import entity.ReservationLineItem;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author 65912
 */
@Stateful
public class WalkInReservationSessionBean implements WalkInReservationSessionBeanRemote, WalkInReservationSessionBeanLocal {

    @EJB(name = "ReservationSessionBeanRemote")
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    
    

    private Date reservationDate;
    private Integer totalLineItems;
    private BigDecimal totalAmount;
    private List<ReservationLineItem> lineItems;
    
    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    
    
    
    public WalkInReservationSessionBean() {
        initialiseState();
    }
    
    
    
    private void initialiseState() {
        reservationDate = new Date();
        totalLineItems = 0;
        totalAmount = new BigDecimal("0.00");
        lineItems = new ArrayList<>();
        
    }
    
    @Override
    public BigDecimal addItem(ReservationLineItem lineItem) {
        totalLineItems++;
        totalAmount.add(lineItem.getAmount());
        lineItems.add(lineItem);
        return totalAmount;
    }
    
    @Override
    public WalkInReservation doCheckout(Employee e) {
        WalkInReservation r = new WalkInReservation(e, reservationDate, totalLineItems, totalAmount);
        for(ReservationLineItem items : lineItems) {
            r.getReservationLineItems().add(items);
        }
        return r;
    }
    
    
    @Override
    public void resetCart() {
        initialiseState();
    }

    @Override
    public Date getReservationDate() {
        return reservationDate;
    }

    @Override
    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    @Override
    public Integer getTotalLineItems() {
        return totalLineItems;
    }

    @Override
    public void setTotalLineItems(Integer totalLineItems) {
        this.totalLineItems = totalLineItems;
    }

    @Override
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public List<ReservationLineItem> getLineItems() {
        return lineItems;
    }

    @Override
    public void setLineItems(List<ReservationLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    
    
    
    
}
