/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import entity.RoomRecord;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ReservationLineItemNotFoundException;

/**
 *
 * @author ajayan
 */
@Remote
public interface ReservationSessionBeanRemote {

    public ReservationLineItem findReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException;
    
    public Integer walkInSearchRoom(RoomType roomType, Date checkIn, Date checkOut);

    public BigDecimal walkInPrice(RoomType roomType, Date checkInDate, Date checkOutDate);
    
    public BigDecimal reservationPrice(RoomType roomType, Date checkInDate, Date checkOutDate);

    public boolean availableForBooking(Date startDate, Date endDate, Date checkIn, Date checkOut);

    public List<ExceptionReport> viewAllExceptionReports();

    public ExceptionReport findExceptionReport(Long exceptionReportId);

    public ExceptionReport updateExceptionReport(Long exceptionReportId, String report);

    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport);

    public List<ReservationLineItem> findAllReservationLineItems();

    public List<ReservationLineItem> findListOfReservationLineItemsByCheckOutDate(Date checkOutDate) throws ReservationLineItemNotFoundException;

    public List<ReservationLineItem> findListOfReservationLineItemsByCheckInDate(Date checkInDate) throws ReservationLineItemNotFoundException;

    public void roomAllocationsForToday() throws ReservationLineItemNotFoundException;


    public void doCheckout(Partner partner, Integer totalLineItems, BigDecimal totalAmount, List<ReservationLineItem> lineItems);
    
    public ReservationLineItem createLineItem(Date checkInDate, Date checkOutDate, BigDecimal amount, RoomType roomType);

    public ReservationLineItem findReservationLineItemOfPartner(Long reservationLineItemId, Long partnerId) throws ReservationLineItemNotFoundException;

    public Integer searchRoom(Date checkIn, Date checkOut);


}
