package com.mtv

import com.alibaba.fastjson.JSONArray

class RoomLog {

    static{
        grails.converters.JSON.registerObjectMarshaller(RoomLog){
            return it.properties.findAll{k,v->
                !(k in ['class'])
            } + [id:it.id]
        }
    }

    Room room

    /* 日志JSON [{d: "时间",n:"观看人数"}]*/
    String content

    /*创建时间*/
    Date dateCreated = new Date()

    /*最后修改时间*/
    Date lastUpdated = new Date()

    static constraints = {
    }

    void addLog(Room r){
        if(!this.content){
            this.content = "[]"
        }
        JSONArray array = JSONArray.parse(this.content)
        array.push([t: new Date().getTime(), n: r.adNum])
        this.content = array.toJSONString()
    }
}
