/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author 65912
 */
public class ReservationLineItemNotFoundException extends Exception {
    
    public ReservationLineItemNotFoundException () {
        
    }
    
    public ReservationLineItemNotFoundException(String msg) {
        super(msg);
    }
    
}
