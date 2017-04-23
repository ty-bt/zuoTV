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

    /* http抓取时间*/
    Date dateStart

    /*最后修改时间*/
    Date lastUpdated

    static constraints = {
    }

    static mapping = {

        cache true
    }

    void addLog(Room r, Date date, Boolean newOnLine){
        if(!this.content){
            this.content = "[]"
        }
        JSONArray array = JSONArray.parse(this.content)
        def logObj = [t: date.getTime(), n: r.adNum]
        // 是否新上线
        if(newOnLine){
            logObj.put('n', 1)
        }
        array.push(logObj)
        this.content = array.toJSONString()
    }
}
