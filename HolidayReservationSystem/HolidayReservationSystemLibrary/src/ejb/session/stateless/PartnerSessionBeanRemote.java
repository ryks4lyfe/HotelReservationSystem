/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import entity.PartnerReservation;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FailedLoginException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author 65912
 */
@Remote
public interface PartnerSessionBeanRemote {
    public Partner doLogin(String username, String password) throws FailedLoginException, PartnerNotFoundException;

    public Partner findPartnerByUsername(String username) throws PartnerNotFoundException;

    public Partner findPartnerById(Long guestId) throws PartnerNotFoundException;

    public Long createPartner(Partner p);
    
    public List<Partner> retrieveListOfPartners() throws PartnerNotFoundException;

    public List<PartnerReservation> retrieveAllPartnerReservations(Long partnerId);
}
