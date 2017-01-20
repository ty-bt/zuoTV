package com.mtv

/**
 * 在线房间表  总表数据量太大
 */
class OnLineRoom {

    static{
        grails.converters.JSON.registerObjectMarshaller(Room){
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'lastUpdated'])
            } + [id:it.id]
        }
    }

    Platform platform

    /* 是否在线 */
    Boolean isOnLine

    /* 平台中的唯一标识 房间号 */
    String flag

    /* 房间标题*/
    String name

    /* 图片 */
    String img

    /* 标签 类型*/
    String tag

    /* 人数 */
    Long adNum

    /* zb */
    String anchor

    /* 房间url*/
    String url

    /* 外部引用url */
    String quoteUrl


    static mapping = {
        generator:'assigned'
    }

    static constraints = {
        quoteUrl nullable: true
    }

}
