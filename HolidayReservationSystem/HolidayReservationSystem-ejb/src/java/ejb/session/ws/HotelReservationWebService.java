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
import java.util.ArrayList;
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
        Partner p = partnerSessionBeanLocal.doLogin(username, password);
        em.detach(p);
        
        
        for(PartnerReservation pr: p.getPartnerReservations()) {
            em.detach(pr);
            pr.setPartner(null);
        }
        p.setPartnerReservations(null);
        return p;
    }

    @WebMethod
    public List<ReservationLineItem> retrieveAllPartnerReservations(Long partnerId) throws PartnerNotFoundException {
        Partner p = partnerSessionBeanLocal.findPartnerById(partnerId);
        List<ReservationLineItem> r = new ArrayList<>();
        for(PartnerReservation pr: p.getPartnerReservations()) {
            for(ReservationLineItem item : pr.getReservationLineItems()) {
                r.add(item);
            }
        }
        
        for(ReservationLineItem i : r) {
            em.detach(i);
            RoomType rt = i.getRoomType();
            if(rt != null) {
            em.detach(rt);
            }
            rt.getLineItems().remove(i);
            i.setRoomType(null);
        }
        
        
        
        return r;
       
    }

    @WebMethod
    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException {
        ReservationLineItem r = reservationSessionBeanLocal.findReservationLineItemById(reservationLineItemId);
        
        
        em.detach(r.getRoomType());
        
        return r;
        
    }
    
    @WebMethod
    public ReservationLineItem findReservationLineItemOfPartner(Long reservationLineItemId, Long partnerId) throws ReservationLineItemNotFoundException {
        ReservationLineItem r = reservationSessionBeanLocal.findReservationLineItemOfPartner(reservationLineItemId, partnerId);
        em.detach(r);
        
        RoomType rt = r.getRoomType();
        em.detach(rt);
        
        rt.getLineItems().remove(r);
        r.setRoomType(null);
        
        return r;
    }

    @WebMethod
    public Integer walkInSearchRoom(RoomType roomType, Date checkIn, Date checkOut) {
        return reservationSessionBeanLocal.walkInSearchRoom(roomType, checkIn, checkOut);
    }
    
    @WebMethod
    public Integer searchRoom(Date checkIn, Date checkOut) {
        return reservationSessionBeanLocal.searchRoom(checkIn, checkOut);
    }

    @WebMethod
    public BigDecimal reservationPrice(RoomType roomType, Date checkInDate, Date checkOutDate) {
        return reservationSessionBeanLocal.reservationPrice(roomType, checkInDate, checkOutDate);
    }

    @WebMethod
    public Partner findPartnerById(Long partnerId) throws PartnerNotFoundException {
        Partner p = partnerSessionBeanLocal.findPartnerById(partnerId);
        em.detach(p);
        p.setPartnerReservations(null);
        
        for(PartnerReservation pr: p.getPartnerReservations()) {
            em.detach(pr);
            pr.setPartner(null);
            
        }
        
        return p;
    }

    @WebMethod
    public ReservationLineItem addItem(ReservationLineItem lineItem, Long rId) {
        ReservationLineItem r = partnerSessionBeanLocal.addItem(lineItem, rId);
        em.detach(r);
        r.setRoomType(null);
        
        
        //RoomType rt = r.getRoomType();
        //em.detach(rt);
        //rt.getLineItems().remove(r);
        
        
        return r;
        
    }

    @WebMethod
    public void removeAllItemsFromCart(List<ReservationLineItem> lineItems) {
        List<ReservationLineItem> lineItems2  = new ArrayList<>();
        for(ReservationLineItem r : lineItems) {
            lineItems2.add(em.find(ReservationLineItem.class, r.getReservationLineItemId()));
            
        }
        partnerSessionBeanLocal.removeAllItemsFromCart(lineItems2);
    }

    @WebMethod
    public ReservationLineItem createLineItem(Date checkInDate, Date checkOutDate, BigDecimal amount, RoomType roomType) {
        return reservationSessionBeanLocal.createLineItem(checkInDate, checkOutDate, amount, roomType);
    }

    @WebMethod
    public void doCheckout(Partner partner, Integer totalLineItems, BigDecimal totalAmount, List<ReservationLineItem> lineItems) {
        reservationSessionBeanLocal.doCheckout(partner, totalLineItems, totalAmount, lineItems);
    }

    @WebMethod
    public List<RoomType> retrieveAllRoomTypesForWebservice() {
        List<RoomType> roomTypes = roomTypeSessionBeanLocal.retrieveAllRoomTypesForWebservice();
        for (RoomType rt : roomTypes) {
            em.detach(rt);
            
            for (RoomRate rr : rt.getRoomRates()) {
                em.detach(rr);
                rr.setRoomType(null);
                
            }

            for (RoomRecord r : rt.getRoomRecords()) {
                em.detach(r);
                r.setRoomType(null);
                
            }
            
            for (ReservationLineItem item : rt.getLineItems()) {
                em.detach(item);
                item.setRoomType(null);
               
            }
            rt.getLineItems().clear();
            rt.getRoomRates().clear();
            rt.getRoomRecords().clear();

        }
        return roomTypes;
    }
}
