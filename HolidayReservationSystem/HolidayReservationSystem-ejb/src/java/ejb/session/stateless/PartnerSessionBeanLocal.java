/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;
import util.exception.FailedLoginException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author 65912
 */
@Local
public interface PartnerSessionBeanLocal {

    public Partner doLogin(String username, String password) throws FailedLoginException, PartnerNotFoundException;

    public Partner findPartnerByUsername(String username) throws PartnerNotFoundException;

    public Partner findPartnerById(Long guestId) throws PartnerNotFoundException;

    public Long createPartner(Partner p);

    public List<Partner> retrieveListOfPartners();
    
}
