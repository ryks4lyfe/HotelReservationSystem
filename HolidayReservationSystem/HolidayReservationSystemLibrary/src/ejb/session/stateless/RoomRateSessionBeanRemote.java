/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomRateException;
import util.exception.RoomNameExistsException;
import util.exception.RoomRateExistsException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ajayan
 */
@Remote
public interface RoomRateSessionBeanRemote {
    public RoomRate createRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomTypeNotFoundException, RoomNameExistsException, UnknownPersistenceException, RoomRateExistsException;

    public List<RoomRate> findAllRoomRates();

    public RoomRate findRoomRateById(Long roomRateId) throws RoomRateNotFoundException;

    public RoomType findRoomRateByName(String roomRateName) throws RoomTypeNotFoundException, RoomRateNotFoundException;

    public List<RoomRate> findRoomRateForRoomType(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomRateListInRoomType(Long roomRateId) throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, RoomTypeNotFoundException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException;
}
