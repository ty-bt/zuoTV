package com.mtv

class IndexController {

    def typeService

    def recommendService

    def index() {

    }

    def getIndexData(){
        List platforms = Platform.createCriteria().list {
            order('onLineAd', 'desc')
        }
        List types = Type.findAll()
        render Response.success([platforms: platforms, types: types]).toJSON()
    }

//    def reloadType(){
//        typeService.reloadAll()
//    }

    def reloadRecommon(){
        recommendService.reload()
    }
}
