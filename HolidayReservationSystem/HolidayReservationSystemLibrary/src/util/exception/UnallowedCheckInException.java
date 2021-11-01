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
public class UnallowedCheckInException extends Exception {
    
    public UnallowedCheckInException() {
        
    }
    
    public UnallowedCheckInException(String msg) {
        super(msg);
    }
}
