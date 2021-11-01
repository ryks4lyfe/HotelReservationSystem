/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRecord;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomRecordException;
import util.exception.RoomNameExistsException;
import util.exception.RoomRecordNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ajayan
 */
@Remote
public interface RoomRecordSessionBeanRemote {
    public RoomRecord createRoomRecord(RoomRecord newRoomRecord, String roomTypeName) throws RoomNameExistsException, UnknownPersistenceException, RoomTypeNotFoundException;

    public List<RoomRecord> findAllRoomRecords();

    public RoomRecord findRoomRecordById(Long roomRecordId) throws RoomRecordNotFoundException;

    public RoomRecord findRoomRecordByNum(Integer roomRecordNum) throws RoomRecordNotFoundException;

    public List<RoomRecord> findAllAvailableRoomRecords();

    public List<RoomRecord> findAllAvailableRoomRecordsForRoomType(String roomTypeName) throws RoomTypeNotFoundException;

    public List<RoomRecord> findAllRoomRecordsForRoomType(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomRecordListInRoomType(Long roomRecordId) throws RoomRecordNotFoundException;

    public void updateRoomRecord(RoomRecord roomRecord) throws RoomRecordNotFoundException, RoomTypeNotFoundException;

    public void deleteRoomRecord(Long roomRecordId) throws RoomRecordNotFoundException, DeleteRoomRecordException;

}
