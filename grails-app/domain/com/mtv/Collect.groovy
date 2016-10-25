package com.mtv

/**
 * 收藏
 */
class Collect {

    static{
        grails.converters.JSON.registerObjectMarshaller(Collect){
            it.room.getName()
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'lastUpdated'])
            } + [id:it.id]
        }
    }

    /**分组 暂时不用*/
    CollectGroup collectGroup

    User user

    Room room

    /*创建时间*/
    Date dateCreated


    static constraints = {
        collectGroup nullable: true
    }
}
