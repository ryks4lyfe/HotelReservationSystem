/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import entity.RoomRecord;
import java.util.Comparator;

/**
 *
 * @author ajayan
 */
public class RankComparatorRooms implements Comparator<RoomRecord> 
{

    @Override
    public int compare(RoomRecord o1, RoomRecord o2) {
        return o2.getRoomType().getRankRoom().compareTo(o1.getRoomType().getRankRoom()); 
    }
    
}
