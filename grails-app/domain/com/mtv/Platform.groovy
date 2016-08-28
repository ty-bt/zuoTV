package com.mtv

/**
 * 平台列表
 */
class Platform {

    static{
        grails.converters.JSON.registerObjectMarshaller(Platform){
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'lastUpdated'])
            } + [id:it.id]
        }
    }

    /* 名称*/
    String name

    /* 标识*/
    String flag

    /* 平台地址*/
    String url

    /* 排序 越大越往前*/
    Long sort = 0l

    /*创建时间*/
    Date dateCreated

    /*最后修改时间*/
    Date lastUpdated

    static constraints = {
    }
}
