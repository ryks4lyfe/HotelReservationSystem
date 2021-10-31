package ejb.session.stateless;

import entity.ReservationLineItem;
import entity.RoomRecord;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomRecordException;
import util.exception.RoomNameExistsException;
import util.exception.RoomRecordNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ajayan
 */
@Stateless
public class RoomRecordSessionBean implements RoomRecordSessionBeanLocal, RoomRecordSessionBeanRemote {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal; 
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal; 

    public RoomRecordSessionBean() {
    }

    @Override
    public RoomRecord createRoomRecord (RoomRecord newRoomRecord, Long roomTypeId) throws RoomNameExistsException, UnknownPersistenceException, RoomTypeNotFoundException 
    {
       
        try 
        {
            RoomType roomType = roomTypeSessionBeanLocal.findRoomTypeById(roomTypeId);
            
            if (!roomType.getTypeStatus().equals("disabled")) {
            em.persist(newRoomRecord); 
            newRoomRecord.setRoomType(roomType);
            roomType.getRoomRecords().add(newRoomRecord);
            
            em.flush();
            
            return newRoomRecord;
            } 
            else 
            { 
                throw new RoomTypeNotFoundException("Room type for Room Type ID " + roomTypeId + " does not exist!"); 
            }
            
        }
        
        catch(PersistenceException ex) 
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
               if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new RoomNameExistsException("Room already exists!");
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }    
    }  
    
    @Override
    public List<RoomRecord> findAllRoomRecords() 
             
    {
        Query query = em.createNamedQuery("SELECT rr FROM RoomRecord rr"); 
        return query.getResultList(); 
    }
    
    @Override
    public RoomRecord findRoomRecordById (Long roomRecordId) throws RoomRecordNotFoundException
    {
        RoomRecord roomRecord = em.find(RoomRecord.class, roomRecordId); 
        
        if (roomRecord != null)
        {
            return roomRecord; 
        }
        else 
        {
            throw new RoomRecordNotFoundException("Room Record ID " + roomRecordId + "does not exist!");
        }
    }
    
    public RoomRecord findRoomRecordByNum (Integer roomRecordNum) throws RoomRecordNotFoundException
    {
        Query query = em.createQuery("SELECT rr FROM RoomRecord rr WHERE r.roomRecordNum = :inRoomRecordNum");
        query.setParameter("inRoomRecordNum", roomRecordNum); 
        
        try 
        {
            return (RoomRecord) query.getSingleResult(); 
        }
        catch(NoResultException | NonUniqueResultException ex)
            
        {
            throw new RoomRecordNotFoundException("Room Record Name " + roomRecordNum + "does not exist!"); 
        }
    }
    
    @Override
    public List<RoomRecord> findAllAvailableRoomRecords() {
        Query query = em.createNamedQuery("SELECT rr FROM RoomRecord rr WHERE rr.roomStatus  :inRoomStatus"); 
        query.setParameter("inRoomStatus", "in use"); 
        return query.getResultList(); 
    }
    
    @Override
    public List<RoomRecord> findAllAvailableRoomRecordsForRoomType (String roomTypeName) throws RoomTypeNotFoundException {
        
        Query query = em.createQuery("SELECT r FROM RoomRecod r WHERE r.roomType = :inRoomType AND r.roomStatus = :inRoomStatus"); 
        query.setParameter("inRoomType", roomTypeSessionBeanLocal.findRoomTypeByName(roomTypeName)); 
        query.setParameter("inRoomStatus", "in use");
         
        return query.getResultList(); 
        
    }
    
    @Override
    public List<RoomRecord> findAllRoomRecordsForRoomType (String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT r FROM RoomRecord r WHERE r.roomType = :inRoomType"); 
        query.setParameter("inRoomType", roomTypeSessionBeanLocal.findRoomTypeByName(roomTypeName));
        
        return query.getResultList();
    }
    
    @Override
     public void updateRoomRecordListInRoomType(Long roomRecordId) throws RoomRecordNotFoundException
    {
        RoomRecord roomRecord = findRoomRecordById(roomRecordId);
        roomRecord.getRoomType().getRoomRecords().add(roomRecord);
    }
    
    @Override
    public void updateRoomRecord(RoomRecord roomRecord) throws RoomRecordNotFoundException, RoomTypeNotFoundException 
    { 
        if (roomRecord != null && roomRecord.getRoomRecordId()!= null) 
        {
            RoomRecord roomRecordToUpdate = findRoomRecordById(roomRecord.getRoomRecordId()); 
            
            if(roomRecordToUpdate.getRoomNum().equals(roomRecord.getRoomNum()))
            {
                roomRecordToUpdate.setRoomStatus(roomRecord.getRoomStatus());
                roomRecordToUpdate.setRoomNum(roomRecord.getRoomNum());
                if (roomRecord.getRoomType().getRoomTypeId() != null) {
                    RoomType roomType = roomTypeSessionBeanLocal.findRoomTypeById(roomRecord.getRoomType().getRoomTypeId());
                    List<RoomRecord> outdatedRoomTypeRoomRecordList = roomRecordToUpdate.getRoomType().getRoomRecords(); 
                    outdatedRoomTypeRoomRecordList.remove(roomRecordToUpdate); //a.getBs().remove(b)
                    roomRecordToUpdate.setRoomType(roomType);
                }
                if(roomRecord.getReservationLineItem() != null) {
                    ReservationLineItem reservationLineItem = reservationSessionBeanLocal.findReservationLineItemById (roomRecord.getReservationLineItem().getReservationLineItemId()); 
                    roomRecordToUpdate.setReservationLineItem(reservationLineItem);
                }
            }
                else
                {
                    throw new RoomRecordNotFoundException("Room number is invalid!"); 
                }
                           
            }
        else {
            throw new RoomRecordNotFoundException("Room Record ID is not valid or Room Record does not exist"); 
            
        }
    }
    
    @Override
    public void deleteRoomRecord(Long roomRecordId) throws RoomRecordNotFoundException, DeleteRoomRecordException
    {
        RoomRecord roomToRemove = findRoomRecordById(roomRecordId);
        
        if(roomToRemove.getRoomStatus().equals("not in use"))
        {
            List<RoomRecord> removeRoomRecordFromRoomType = roomToRemove.getRoomType().getRoomRecords();
            removeRoomRecordFromRoomType.remove(roomToRemove);
            em.remove(roomToRemove); 
        }
        else if(roomToRemove.getRoomStatus().equals("in use"))
        {
            roomToRemove.setRoomStatus("disabled");
        }
        else
        {
            throw new DeleteRoomRecordException("Room Record ID " + roomRecordId + " cannot be deleted as it is being used!");
        }
    }
    

    
    

    }