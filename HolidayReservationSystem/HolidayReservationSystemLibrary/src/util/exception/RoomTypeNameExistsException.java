/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author ajayan
 */
public class RoomTypeNameExistsException extends Exception {

    public RoomTypeNameExistsException() {
    }

    public RoomTypeNameExistsException(String msg) {
        super(msg);
    }
    
}
