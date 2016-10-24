package com.mtv

class IndexController {

    def typeService

    def index() {

    }

    def getIndexData(){
        List platforms = Platform.findAll()
        List types = Type.findAll()
        render Response.success([platforms: platforms, types: types]).toJSON()
    }

//    def reloadType(){
//        typeService.reloadAll()
//    }
}
