/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
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
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal; 

    public RoomRateSessionBean() {
    }
    
    public RoomRate createRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomTypeNotFoundException, RoomNameExistsException, UnknownPersistenceException, RoomRateExistsException 
    {
         try 
        {
            RoomType roomType = roomTypeSessionBeanLocal.findRoomTypeById(roomTypeId);
            
            if (!roomType.getTypeStatus().equals("disabled")) {
                em.persist(newRoomRate); 
                newRoomRate.setRoomType(roomType);
                roomType.getRoomRates().add(newRoomRate); 
                
                em.flush();
                
                return newRoomRate; 
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
                    throw new RoomRateExistsException("Room Rate already exists");
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
    
    public List<RoomRate> findAllRoomRates() 
    {
        Query query = em.createNamedQuery("SELECT rr FROM RoomRate rr"); 
        return query.getResultList(); 
    }
    
    public RoomRate findRoomRateById (Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate roomRecord = em.find(RoomRate.class, roomRateId); 
        
        if (roomRecord != null)
        {
            return roomRecord; 
        }
        else 
        {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + "does not exist!");
        }
    }
    
    public RoomType findRoomRateByName (String roomRateName) throws RoomTypeNotFoundException, RoomRateNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.rateName = :inRateName");
        query.setParameter("inRateName", roomRateName); 
        
        try 
        {
            return (RoomType) query.getSingleResult(); 
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomRateNotFoundException("Room Rate Name " + roomRateName + "does not exist!"); 
        }
    }
    
    
    public List<RoomRate> findRoomRateForRoomType (String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType = :inRoomType"); 
        query.setParameter("inRoomType", roomTypeSessionBeanLocal.findRoomTypeByName(roomTypeName));
        
        return query.getResultList();
    }
    
     public void updateRoomRateListInRoomType(Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate roomRate = findRoomRateById(roomRateId);
        roomRate.getRoomType().getRoomRates().add(roomRate);
    }
    
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, RoomTypeNotFoundException 
    {
        if (roomRate != null && roomRate.getRoomRateId()!= null) 
        {
            RoomRate roomRateToUpdate = findRoomRateById(roomRate.getRoomRateId()); 
            
            if(roomRateToUpdate.getRoomRateId().equals(roomRate.getRoomRateId()))
            {
                if (roomRate.getRoomType().getRoomTypeId() != null) {
                    RoomType roomType = roomTypeSessionBeanLocal.findRoomTypeById(roomRate.getRoomType().getRoomTypeId());
                    List<RoomRate> outdatedRoomTypeRoomRateList = roomRateToUpdate.getRoomType().getRoomRates(); 
                    outdatedRoomTypeRoomRateList.remove(roomRateToUpdate); //a.getBs().remove(b)
                    roomRateToUpdate.setRoomType(roomType);
                }
                
                roomRateToUpdate.setStartRateDate(roomRate.getStartRateDate());
                roomRateToUpdate.setEndRateDate(roomRate.getEndRateDate());
                roomRateToUpdate.setRateName(roomRate.getRateName());
                roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                roomRateToUpdate.setRoomRateType(roomRate.getRoomRateType());
            }
            else 
            {
                throw new RoomRateNotFoundException("Room Rate is not valid!"); 
            }
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate does not exist!"); 
        }
    }
    
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException
    {
        RoomRate roomRateToRemove = findRoomRateById(roomRateId);
        
        if(roomRateToRemove.getRoomRateStatus().equals("not in use")) 
        {
            List<RoomRate> removeRoomRateFromRoomType = roomRateToRemove.getRoomType().getRoomRates();
            removeRoomRateFromRoomType.remove(roomRateToRemove);
            em.remove(roomRateToRemove); 
        }
        else if(roomRateToRemove.getRoomRateStatus().equals("in use"))
        {
            roomRateToRemove.setRoomRateStatus("disabled"); 
        }
        else
        {
            throw new DeleteRoomRateException("Room Rate " + roomRateId + " cannot be deleted as it is being used");
        }
    }
    

}
