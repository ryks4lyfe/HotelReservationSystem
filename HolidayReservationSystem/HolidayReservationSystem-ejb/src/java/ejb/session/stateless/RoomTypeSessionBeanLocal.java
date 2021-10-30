/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ajayan
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomType createRoomType(RoomType newRoomType) throws RoomTypeNameExistsException, UnknownPersistenceException;

    public List<RoomType> retrieveAllRoomTypes();

    public RoomType findRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType findRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException;
    
}