/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRecord;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomRecordNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ajayan
 */
@Local
public interface RoomRecordSessionBeanLocal {

    public List<RoomRecord> findAllRoomRecordsForRoomType(String roomTypeName) throws RoomTypeNotFoundException;

    public List<RoomRecord> findAllAvailableRoomRecordsForRoomType(String roomTypeName) throws RoomTypeNotFoundException;

    public List<RoomRecord> findAllAvailableRoomRecords();

    public RoomRecord findRoomRecordByNum(Integer roomRecordNum) throws RoomRecordNotFoundException;

    public RoomRecord findRoomRecordById(Long roomRecordId) throws RoomRecordNotFoundException;

    public List<RoomRecord> findAllRoomRecords();
    
}
