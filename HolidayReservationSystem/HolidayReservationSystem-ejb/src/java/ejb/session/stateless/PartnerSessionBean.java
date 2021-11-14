/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FailedLoginException;
import util.exception.PartnerNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author 65912
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @EJB(name = "RoomTypeSessionBeanRemote")
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    public PartnerSessionBean() {

    }

    //Create and persist Partner, no need to add Reservation
    @Override
    public Long createPartner(Partner p) {
        em.persist(p);
        em.flush();

        return p.getPartnerId();
    }

    //Search via Id
    @Override
    public Partner findPartnerById(Long partnerId) throws PartnerNotFoundException {
        Partner p = em.find(Partner.class, partnerId);

        if (p != null) {
            return p;
        } else {
            throw new PartnerNotFoundException("Error, Partner " + partnerId + " does not exist.");
        }
    }

    //Search via username
    @Override
    public Partner findPartnerByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (Partner) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("Error, Partner with Username " + username + " does not exist.");
        }
    }

    //Tries to find p with username, if doesnt exists throw an error. Continue to check if password same,
    //if wrong throw an error, else, return the p.
    @Override
    public Partner doLogin(String username, String password) throws FailedLoginException, PartnerNotFoundException {
        try {
            Partner p = findPartnerByUsername(username);
            if (p.getPassword().equals(password)) {
                p.getPartnerReservations().size();
                return p;
            } else {
                throw new FailedLoginException("Error, please try logging in again with a different username or password!");
            }
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Error, Partner with Username: " + username + " does not exist.");
        }
    }

    @Override
    public List<Partner> retrieveListOfPartners() throws PartnerNotFoundException {
        try {
            Query query = em.createQuery("SELECT p FROM Partner p");
            return query.getResultList();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("No Partner data in database!");
        }
    }

    @Override
    public List<ReservationLineItem> retrieveAllPartnerReservations(Long partnerId) {
        Partner p = em.find(Partner.class, partnerId);
        List<ReservationLineItem> r = new ArrayList<>();
        for (PartnerReservation pr : p.getPartnerReservations()) {
            for (ReservationLineItem item : pr.getReservationLineItems()) {
                r.add(item);
                System.out.println("add");
            }
        }
        return r;
    }

    @Override
    public ReservationLineItem addItem(ReservationLineItem lineItem, Long rId) {
        RoomType rt = em.find(RoomType.class, rId);
        lineItem.setRoomType(rt);
        em.persist(lineItem);

        rt.getLineItems().add(lineItem);
        em.flush();
        return lineItem;

    }

    @Override
    public void removeAllItemsFromCart(List<ReservationLineItem> lineItems) {

        for (ReservationLineItem r : lineItems) {
            ReservationLineItem r1 = em.find(ReservationLineItem.class, r.getReservationLineItemId());

            r1.setRoom(null);
            r1.setRoomType(null);
            em.remove(r1);
        }
        em.flush();

    }

}
