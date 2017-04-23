package com.mtv.user

import com.mtv.Response
import com.mtv.Room

class AdminController {

    def userService

    def roomLogService

    def openRoomLog(){
        if(userService.getCurrentUser().id != 1l){
            return
        }

        def room = Room.get(params.long('roomId'))
        if(!room){
            render Response.failure("", "不存在的房间").toJSON()
            return
        }
        room.isLog = true
        room.save(flush: true)
        render Response.success(null, "ok").toJSON()

    }

    def splitLog(){
        if(userService.getCurrentUser().id != 1l){
            return
        }
        roomLogService.splitTable(params.getDate('date', "yyyy-MM-dd"))
        render Response.success(null, "ok").toJSON()
    }
}
