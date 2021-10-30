/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class RoomTypeSessionBean implements RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomRecordSessionBeanLocal roomRecordSessionBeanLocal; 
    
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal; 
    
    private ReservationSessionBeanLocal reservationSessionBeanLocal; 

    public RoomTypeSessionBean() {
    }
    

    public RoomType createRoomType(RoomType newRoomType) throws RoomTypeNameExistsException, UnknownPersistenceException 
    {
        
        try 
        {
           em.persist(newRoomType); 
           em.flush();
           
           return newRoomType; 
           
        }
        
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
               if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new RoomTypeNameExistsException();
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

    public List<RoomType> retrieveAllRoomTypes() 
    {
        
        Query query = em.createQuery("SELECT r FROM RoomType r"); 
        
        return query.getResultList(); 

    }
    
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
    
    public RoomType findRoomTypeByName (String roomTypeName) throws RoomTypeNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.typeName = :inTypeName");
        query.setParameter("inTypeName", roomTypeName); 
        
        try 
        {
            return (RoomType) query.getSingleResult(); 
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomTypeNotFoundException("Room Type Name " + roomTypeName + "does not exist!"); 
        }
    }
    
    public void updateRoomType (RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException
    {
        if (roomType != null && roomType.getRoomTypeId()!= null)
        {
            RoomType roomTypeToUpdate = findRoomTypeById(roomType.getRoomTypeId()); 
            
            if (roomTypeToUpdate.getTypeName().equals(roomType.getTypeName()))
            {
                roomTypeToUpdate.setTypeName(roomType.getTypeName());
                roomTypeToUpdate.setAmenities(roomType.getAmenities());
                roomTypeToUpdate.setBed(roomType.getBed());
                roomTypeToUpdate.setDescription(roomType.getDescription());
                roomTypeToUpdate.setSize(roomType.getSize());
                roomTypeToUpdate.setCapacity(roomType.getCapacity());
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
    
    public void deleteRoomType (Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException 
    {
         RoomType roomTypeToRemove = findRoomTypeById(roomTypeId);
         
         String roomTypeName = roomTypeToRemove.getTypeName(); 
        
        if(roomRecordSessionBeanLocal.findAllAvailableRoomRecordsForRoomType(roomTypeName).isEmpty() && roomTypeToRemove.getTypeStatus().equals("enabled") 
                && roomRateSessionBeanLocal.findRoomRateForRoomType(roomTypeName).isEmpty() 
                && reservationSessionBeanLocal.findReservationLineItemByRoomType(roomTypeId).isEmpty())
        {
            em.remove(roomTypeToRemove); //enabled and not in use = delete
        }
        else if(roomTypeToRemove.getTypeStatus().equals("enabled") && !roomRecordSessionBeanLocal.findAllRoomRecordsForRoomType(roomTypeName).isEmpty()
                && !roomRateSessionBeanLocal.findRoomRateForRoomType(roomTypeName).isEmpty()
                && !reservationSessionBeanLocal.findReservationLineItemByRoomType(roomTypeId).isEmpty()) //enabled and in use = disabled
        {
            roomTypeToRemove.setTypeStatus("disabled");
        }
        else
        {
            throw new DeleteRoomTypeException("Room Type ID " + roomTypeId + " cannot be used as it is being used!");
        }
    }
        
        
}

    
   