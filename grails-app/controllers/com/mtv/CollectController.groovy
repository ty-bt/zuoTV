package com.mtv

import grails.converters.JSON

class CollectController {

    static namespace = "center"

    def userService

    def collectService

    def list1(){
//        def start = System.currentTimeMillis()

        User user = userService.getCurrentUser()
        List<Collect> collects = Collect.createCriteria().list(ParamUtils.limit()){
            room{
                order('isOnLine', "desc")
                order('adNum', "desc")
            }
            eq('user', user)
        }
//        render("${System.currentTimeMillis() - start}-${collects.size()}")

        render Response.success([collects: collects*.room, total: collects.totalCount]).toJSON()
    }

    def list(){
//        def start = System.currentTimeMillis()
        User user = userService.getCurrentUser()
        def collects = Collect.findAllByUser(user)
        def rooms = []
        if(collects){
            rooms = OnLineRoom.findAllByIdInList(collects*.roomId)
        }
//        render("${System.currentTimeMillis() - start}-${collects.size()}")
        render Response.success([collects: rooms, total: collects.size()]).toJSON()
    }

    def add(){
        User user = userService.getCurrentUser()
        collectService.add(user.id, params.long('roomId'))
        render Response.success().toJSON()
    }

    def delete(){
        User user = userService.getCurrentUser()
        collectService.delete(params.long('roomId'), user.id)
        render Response.success().toJSON()
    }
}
