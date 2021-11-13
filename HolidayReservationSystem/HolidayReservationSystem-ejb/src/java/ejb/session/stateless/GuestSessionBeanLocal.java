/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.ReservationLineItem;
import entity.RoomRecord;
import java.util.List;
import javax.ejb.Local;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;
import util.exception.UnallowedCheckInException;

/**
 *
 * @author 65912
 */
@Local
public interface GuestSessionBeanLocal {

    public Guest doLogin(String email, String password) throws FailedLoginException, GuestNotFoundException;

    public Guest findGuestByEmail(String email) throws GuestNotFoundException;

    public Guest findGuestById(Long guestId) throws GuestNotFoundException;

    public Long createGuest(Guest g);

    public List<RoomRecord> checkInGuest(ReservationLineItem r) throws GuestNotFoundException, UnallowedCheckInException;

    public List<RoomRecord> checkOutGuest(ReservationLineItem r) throws GuestNotFoundException;

    
}

