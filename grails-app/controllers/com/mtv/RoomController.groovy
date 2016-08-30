package com.mtv

import grails.converters.JSON
import org.springframework.util.Assert

class RoomController {

    def detail(){
        [room: Room.get(params.getLong("id"))]
    }

    def page(){
        def rooms = Room.createCriteria().list(ParamUtils.limit()){
            eq('isOnLine', true)
            order('adNum', "desc")
        }
        render([rooms: rooms, total: rooms.totalCount] as JSON)
    }

    def one(){
        def roomId = params.getLong("id")
        Assert.notNull(roomId, "ID不能为空")

        def room = Room.findById(roomId)
        render([room: room] as JSON)
    }
}
