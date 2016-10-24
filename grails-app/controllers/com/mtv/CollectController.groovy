package com.mtv

import grails.converters.JSON

class CollectController {

    def list(){
        def collects = Collect.createCriteria().list(ParamUtils.limit()){
            room{
                eq('isOnLine', true)
                order('adNum', "desc")
            }
        }
        render([collects: collects, total: collects.totalCount] as JSON)
    }
}
