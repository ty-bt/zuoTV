package com.mtv

/**
 * 房间类型
 */
class Type {

    static{
        grails.converters.JSON.registerObjectMarshaller(Type){
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'sort'])
            }
        }
    }

    // 分类名称
    String name

    // 房间总数
    Long roomCount

    // 观众总数
    Long adSum

    // 自定义排序字段
    Long sort = 0

    /*创建时间*/
    Date dateCreated

    static constraints = {
    }
}
