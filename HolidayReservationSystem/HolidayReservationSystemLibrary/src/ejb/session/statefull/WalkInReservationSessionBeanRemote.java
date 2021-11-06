/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.statefull;

import entity.Employee;
import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author 65912
 */
@Remote
public interface WalkInReservationSessionBeanRemote {

   

    public BigDecimal addItem(ReservationLineItem lineItem);

    public WalkInReservation doCheckout(Employee e);

    public void resetCart();

    public Date getReservationDate();

    public void setReservationDate(Date reservationDate);

    public Integer getTotalLineItems();

    public void setTotalLineItems(Integer totalLineItems);

    public BigDecimal getTotalAmount();

    public void setTotalAmount(BigDecimal totalAmount);

    public List<ReservationLineItem> getLineItems();

    public void setLineItems(List<ReservationLineItem> lineItems);

    public void removeAllItemsFromCart();

    public OnlineReservation doCheckout(Guest g);
    
}
