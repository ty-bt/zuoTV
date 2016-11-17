package com.mtv

class Room {

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

    /* 标签*/
    String tag

    /* 人数 */
    Long adNum

    /* zb */
    String anchor

    /* 房间url*/
    String url

    /* 外部引用url */
    String quoteUrl

    /*创建时间*/
    Date dateCreated = new Date()

    /*最后修改时间*/
    Date lastUpdated = new Date()

    static mapping = {
        // 更新的时候会自动更新lastUpdated 所以这个不用自动更新时间
        autoTimestamp false
    }

    static constraints = {
        quoteUrl nullable: true
    }

}
