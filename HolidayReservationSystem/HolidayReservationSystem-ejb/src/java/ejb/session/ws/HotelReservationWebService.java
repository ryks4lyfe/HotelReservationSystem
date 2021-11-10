/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import entity.RoomType;
import entity.RoomRecord;
import entity.RoomRate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FailedLoginException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationLineItemNotFoundException;

/**
 *
 * @author 65912
 */
@WebService(serviceName = "HotelReservationWebService")
@Stateless()
public class HotelReservationWebService {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @WebMethod(operationName = "partnerLogin")
    public Partner doLogin(String username, String password) throws FailedLoginException, PartnerNotFoundException {
        return partnerSessionBeanLocal.doLogin(username, password);
    }

    @WebMethod
    public List<PartnerReservation> retrieveAllPartnerReservations(Long partnerId) {
        return partnerSessionBeanLocal.retrieveAllPartnerReservations(partnerId);
    }

    @WebMethod
    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException {
        return reservationSessionBeanLocal.findReservationLineItemById(reservationLineItemId);
    }

    @WebMethod
    public Integer walkInSearchRoom(RoomType roomType, Date checkIn, Date checkOut) {
        return reservationSessionBeanLocal.walkInSearchRoom(roomType, checkIn, checkOut);
    }

    @WebMethod
    public BigDecimal reservationPrice(RoomType roomType, Date checkInDate, Date checkOutDate) {
        return reservationSessionBeanLocal.reservationPrice(roomType, checkInDate, checkOutDate);
    }

    @WebMethod
    public List<RoomType> retrieveAllRoomTypes() {
        return roomTypeSessionBeanLocal.retrieveAllRoomTypes();
    }

    @WebMethod
    public Partner findPartnerById(Long partnerId) throws PartnerNotFoundException {
        return partnerSessionBeanLocal.findPartnerById(partnerId);
    }

    @WebMethod
    public BigDecimal addItem(ReservationLineItem lineItem) {
        return partnerSessionBeanLocal.addItem(lineItem);
    }

    @WebMethod
    public void removeAllItemsFromCart(List<ReservationLineItem> lineItems) {
        partnerSessionBeanLocal.removeAllItemsFromCart(lineItems);
    }

    @WebMethod
    public ReservationLineItem createLineItem(Date checkInDate, Date checkOutDate, BigDecimal amount, RoomType roomType) {
        return reservationSessionBeanLocal.createLineItem(checkInDate, checkOutDate, amount, roomType);
    }

    @WebMethod
    public PartnerReservation doCheckout(Partner partner, Integer totalLineItems, BigDecimal totalAmount, List<ReservationLineItem> lineItems) {
        return reservationSessionBeanLocal.doCheckout(partner, totalLineItems, totalAmount, lineItems);
    }
}
