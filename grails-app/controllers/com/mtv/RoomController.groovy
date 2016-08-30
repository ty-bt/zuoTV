package com.mtv

import grails.converters.JSON

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
}
