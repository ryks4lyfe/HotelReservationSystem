/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ajayan
 */
@Remote
public interface RoomTypeSessionBeanRemote {
    public List<RoomType> retrieveAllRoomTypes();

    public RoomType createRoomType(RoomType newRoomType) throws RoomTypeNameExistsException, UnknownPersistenceException;

    public RoomType findRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType findRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException;

    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;

    //public List<RoomType> retrieveAllRoomTypesWS();
    
}
