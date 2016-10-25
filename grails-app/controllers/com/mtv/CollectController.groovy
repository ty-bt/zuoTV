package com.mtv

import grails.converters.JSON

class CollectController {

    static namespace = "center"

    def userService

    def collectService

    def list(){
        User user = userService.getCurrentUser()
        def collects = Collect.createCriteria().list(ParamUtils.limit()){
            room{
                order('isOnLine', "desc")
                order('adNum', "desc")
            }
            eq('user', user)
        }
        render Response.success([collects: collects, total: collects.totalCount]).toJSON()
    }

    def add(){
        User user = userService.getCurrentUser()
        collectService.add(user.id, params.long('roomId'))
        render Response.success().toJSON()
    }

    def delete(){
        User user = userService.getCurrentUser()
        collectService.delete(params.long('id'), user.id)
        render Response.success().toJSON()
    }
}
