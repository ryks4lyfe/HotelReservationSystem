/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.OnlineReservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;

/**
 *
 * @author 65912
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    public GuestSessionBean() {
        
    }
    
    //Create and persist Guest, no need to add Online Reservation
    @Override
    public Long createGuest(Guest g) {
        em.persist(g);
        em.flush();
        
        return g.getGuestId();
    }
    
    //Search via Id
    @Override
    public Guest findGuestById(Long guestId) throws GuestNotFoundException {
        Guest g = em.find(Guest.class, guestId);
        
        if(g!= null) {
            return g;
        } else {
            throw new GuestNotFoundException("Error, Guest " + guestId + " does not exist.");
        }
    }
    
    //Search vie email
    @Override
    public Guest findGuestByEmail(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (Guest)query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Error, Guest with " + email + " does not exist.");
        }
    }
    
    
    //Tries to find g with email, if doesnt exists throw an error. Continue to check if password same,
    //if wrong throw an error, else, return the g.
    @Override
    public Guest doLogin(String email, String password) throws FailedLoginException, GuestNotFoundException {
        try {
            Guest g = findGuestByEmail(email);
            if(g.getPassword().equals(password)) {
                g.getOnlineReservations().size();
                return g;
            } else {
                throw new FailedLoginException("Error, please try logging in again with a different email or password!");
            }
        } catch(GuestNotFoundException ex) {
            throw new GuestNotFoundException("Error, Guest with " + email + " does not exist.");
        }
    }

    
    //Retrieve reservations
    public List<OnlineReservation> retrieveOnlineReservationListOfGuest(Long guestId) throws GuestNotFoundException {
        Guest g = em.find(Guest.class, guestId);
        
        if(g!= null) {
            return g.getOnlineReservations();
        } else {
            throw new GuestNotFoundException("Error, Guest " + guestId + " does not exist.");
        }
    }
    
    //Check In 
    
    //Check out
        
}