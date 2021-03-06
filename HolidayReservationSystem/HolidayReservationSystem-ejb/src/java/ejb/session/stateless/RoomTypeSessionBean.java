	package ejb.session.stateless;

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
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author ajayan
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanLocal, RoomTypeSessionBeanRemote {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomRecordSessionBeanRemote roomRecordSessionBeanRemote; 
    
    @EJB
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote; 
    
    @EJB
    private ReservationSessionBeanRemote reservationSessionBeanRemote; 

    public RoomTypeSessionBean() {
    }
    
    

    @Override
    public RoomType createRoomType(RoomType newRoomType) throws RoomTypeNameExistsException, UnknownPersistenceException 
    { 
        try{
         RoomType rt = findRoomTypeByName(newRoomType.getTypeName()); 
           
        } catch 
                (RoomTypeNotFoundException ex) {
           em.persist(newRoomType); 
           em.flush();
           System.out.println("An error occured"); 
        }
           
           return newRoomType; 
    }
           

    @Override
    public List<RoomType> retrieveAllRoomTypes() 
    {
        
        Query query = em.createQuery("SELECT r FROM RoomType r"); 
        
        return query.getResultList(); 

    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypesForWebservice() 
    {
        
        Query query = em.createQuery("SELECT r FROM RoomType r"); 
        
        return query.getResultList(); 

    }
    
    @Override
    public RoomType findRoomTypeById (Long roomTypeId) throws RoomTypeNotFoundException
    {
        RoomType roomType = em.find(RoomType.class, roomTypeId); 
        
        if(roomType != null) 
        {
            return roomType; 
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type ID" + roomTypeId + "does not exist!"); 
        }
    }
    
    @Override
    public RoomType findRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.typeName = :inTypeName");
        query.setParameter("inTypeName", roomTypeName); 
        
        try 
        {
            return (RoomType) query.getSingleResult(); 
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomTypeNotFoundException("Room Type Name " + roomTypeName + " does not exist!"); 
        }
    }
    
    @Override
    public void updateRoomType (RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException
    {
        if (roomType != null && roomType.getRoomTypeId()!= null)
        {
            RoomType roomTypeToUpdate = findRoomTypeById(roomType.getRoomTypeId()); 
            
            if (roomTypeToUpdate.getRoomTypeId().equals(roomType.getRoomTypeId()))
            {
                roomTypeToUpdate.setTypeName(roomType.getTypeName());
                roomTypeToUpdate.setAmenities(roomType.getAmenities());
                roomTypeToUpdate.setBed(roomType.getBed());
                roomTypeToUpdate.setDescription(roomType.getDescription());
                roomTypeToUpdate.setSize(roomType.getSize());
                roomTypeToUpdate.setCapacity(roomType.getCapacity());
                roomTypeToUpdate.setRankRoom(roomType.getRankRoom());
            }
            else 
            {
                throw new UpdateRoomTypeException("Name of room type to be updated, " + roomType.getTypeName() + ", does not match existing records"); 
            }
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type ID not provided for the room type to be updated");
        }
        
    }
    
    @Override
    public void deleteRoomType (Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException 
    {
         RoomType roomTypeToRemove = findRoomTypeById(roomTypeId);
         
         String roomTypeName = roomTypeToRemove.getTypeName(); 
        
        if(roomTypeToRemove.getTypeStatus().equals("available") && roomRateSessionBeanRemote.findRoomRateForRoomType(roomTypeName).isEmpty() 
                && roomRecordSessionBeanRemote.findAllAvailableRoomRecordsForRoomType(roomTypeName).isEmpty())        
        {
            em.remove(roomTypeToRemove); //enabled and not in use = delete
        }
        else if(roomTypeToRemove.getTypeStatus().equals("available") && !roomRecordSessionBeanRemote.findAllRoomRecordsForRoomType(roomTypeName).isEmpty()
                && !roomRateSessionBeanRemote.findRoomRateForRoomType(roomTypeName).isEmpty())
                 
        {
            roomTypeToRemove.setTypeStatus("disabled");
        }
        else
        {
            throw new DeleteRoomTypeException("Room Type ID " + roomTypeId + " cannot be deleted as it is being used!");
        }
    }
        
        
}