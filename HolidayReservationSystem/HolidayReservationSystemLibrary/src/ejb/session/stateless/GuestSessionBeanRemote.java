/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;
import util.exception.FailedLoginException;
import util.exception.GuestNotFoundException;

/**
 *
 * @author 65912
 */
@Remote
public interface GuestSessionBeanRemote {
    public Guest doLogin(String email, String password) throws FailedLoginException, GuestNotFoundException;

    public Guest findGuestByEmail(String email) throws GuestNotFoundException;

    public Guest findGuestById(Long guestId) throws GuestNotFoundException;

    public Long createGuest(Guest g);
}
