package com.mtv

import grails.converters.JSON

class RoomController {

    def page(){
        def rooms = Room.createCriteria().list(ParamUtils.limit()){
            eq('isOnLine', true)
            order('adNum', "desc")
        }
        render([rooms: rooms] as JSON)
    }
}
