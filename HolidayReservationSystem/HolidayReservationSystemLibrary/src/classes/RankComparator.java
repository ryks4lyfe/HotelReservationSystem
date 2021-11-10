/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import entity.ReservationLineItem;
import entity.RoomRecord;
import java.util.Comparator;

/**
 *
 * @author ajayan
 */
public class RankComparator implements Comparator<ReservationLineItem>{

    @Override
    public int compare(ReservationLineItem o1, ReservationLineItem o2) {
        return o2.getRoomType().getRankRoom().compareTo(o1.getRoomType().getRankRoom()); 
    }
    
}
