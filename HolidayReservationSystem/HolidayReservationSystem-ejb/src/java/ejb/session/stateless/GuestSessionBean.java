/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.RoomRecord;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;
import util.exception.UnallowedCheckInException;
import util.exception.UnallowedCheckOutException;

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

        if (g != null) {
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
            return (Guest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Error, Guest with " + email + " does not exist.");
        }
    }

    //Tries to find g with email, if doesnt exists throw an error. Continue to check if password same,
    //if wrong throw an error, else, return the g.
    @Override
    public Guest doLogin(String email, String password) throws FailedLoginException, GuestNotFoundException {
        try {
            Guest g = findGuestByEmail(email);
            if (g.getPassword().equals(password)) {
                g.getOnlineReservations().size();
                return g;
            } else {
                throw new FailedLoginException("Error, please try logging in again with a different email or password!");
            }
        } catch (GuestNotFoundException ex) {
            throw new GuestNotFoundException("Error, Guest with " + email + " does not exist.");
        }
    }

    //Retrieve reservations
    public List<OnlineReservation> retrieveOnlineReservationListOfGuest(Long guestId) throws GuestNotFoundException {
        Guest g = em.find(Guest.class, guestId);

        if (g != null) {
            return g.getOnlineReservations();
        } else {
            throw new GuestNotFoundException("Error, Guest " + guestId + " does not exist.");
        }
    }

    //Change to fit the client
    @Override
    public List<RoomRecord> checkInGuest(ReservationLineItem r) throws GuestNotFoundException, UnallowedCheckInException {
        /*try {
            Guest g = findGuestById(guestId);
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

            List<RoomRecord> roomsCheckedIn = new ArrayList<>();

            //Get Reservations from Guest
            List<OnlineReservation> reservationList = retrieveOnlineReservationListOfGuest(guestId);

            //Loop through each reservation record of guest, and get all the line items
            for (OnlineReservation oR : reservationList) {
                for (ReservationLineItem r : oR.getReservationLineItems()) {
                    try {
                        //Check for lineItems with the same check in date as currentDate
                        if (sdf.format(currentDate).equals(sdf.format(r.getCheckInDate()))) {*/

        //Nothing to worry if after 2pm
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        List<RoomRecord> roomsCheckedIn = new ArrayList<>();
        Date currentDate = new Date();
        //System.out.println(r.getCheckInDate().getTime());

        try {
            if (dateFormat.parse(dateFormat.format(currentDate)).after(dateFormat.parse("14:00"))) {
                RoomRecord roomToCheckIn = r.getRoom();
                System.out.println(roomToCheckIn.getRoomStatus());
                roomToCheckIn.setRoomStatus("occupied");
                //roomToCheckIn.setReservationLineItem(r);
                roomsCheckedIn.add(roomToCheckIn);
            } else {
                //If before 2pm, check if reserved
                RoomRecord roomToCheckIn = r.getRoom();
                if (roomToCheckIn.getRoomStatus().equals("reserved and ready")) {
                    roomToCheckIn.setRoomStatus("occupied");
                    //roomToCheckIn.setReservationLineItem(r);
                    roomsCheckedIn.add(roomToCheckIn);
                } else if (roomToCheckIn.getRoomStatus().equals("reserved and not ready") || (roomToCheckIn.getRoomStatus().equals("unavailable"))) {
                    throw new UnallowedCheckInException("Guest Check in at 2pm on the day of arrival only allowed if a room is available before then.");
                }
            }
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        }
        return roomsCheckedIn;
    }

    //Change to fit the client
    public List<RoomRecord> checkOutGuest(ReservationLineItem r) throws GuestNotFoundException {

        /* Guest g = findGuestById(guestId);
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

            List<RoomRecord> roomsCheckedOut = new ArrayList<>();
            
            List<OnlineReservation> reservationList = retrieveOnlineReservationListOfGuest(guestId);
            
            for (OnlineReservation oR : reservationList) {
                for (ReservationLineItem r : oR.getReservationLineItems()) {
                    //Check for lineItems with the same check in date as currentDate
                    if (sdf.format(currentDate).equals(sdf.format(r.getCheckOutDate()))) {*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        List<RoomRecord> roomsCheckedOut = new ArrayList<>();

        RoomRecord roomToCheckOut = r.getRoom();
        roomsCheckedOut.add(roomToCheckOut);
        if (roomToCheckOut.getRoomStatus().equals("occupied but available") || roomToCheckOut.getRoomStatus().equals("occupied")) {
            roomToCheckOut.setRoomStatus("unavailable");
            //after 1.5 hours for cleaning, make it available
            roomToCheckOut.setRoomStatus("available");
        } else if (roomToCheckOut.getRoomStatus().equals("reserved and not ready")) {
            roomToCheckOut.setRoomStatus("unavailable");
            //after 1.5hours for cleaning, make it reserved and ready;
            roomToCheckOut.setRoomStatus("reserved and ready");

        }
        return roomsCheckedOut;
    }

}
