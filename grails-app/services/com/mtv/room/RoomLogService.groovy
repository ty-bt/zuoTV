package com.mtv.room

import com.mtv.Room
import com.mtv.RoomLog
import grails.transaction.Transactional

@Transactional
class RoomLogService {

    def serviceMethod() {

    }

    /**
     * 记录日志
     * @param room 房间号
     * @param newOnLine 是否新上线
     * @return
     */
    public RoomLog log(Room room, Boolean newOnLine, Date date){
        // 先不用
        return null
//        if(!newOnLine){
//            List<RoomLog> logList = RoomLog.createCriteria().list {
//                eq('room.id', room.id)
//                setMaxResults(1)
//                order('dateCreated', "desc")
//            }
//            if(logList){
//                logList[0].addLog(room)
//                return logList[0].save()
//            }
//        }
//        RoomLog roomLog = new RoomLog()
//        roomLog.room = room
//        roomLog.addLog(room)
//        return roomLog.save()

    }
}
