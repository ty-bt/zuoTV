package com.mtv

import grails.transaction.Transactional

@Transactional
class TypeService {

    def serviceMethod() {

    }

    /**
     * 重新统计所有类型
     */
    public void reloadAll(){
        List list = Room.executeQuery("select r.tag, sum(r.adNum) as adSum, count(r.tag) as roomCount from Room as r where r.isOnLine = true and r.tag != '' group by r.tag order by adSum desc")
        // 删除所有
        Type.executeUpdate("delete Type t")
        // 插入
        list.each {
            Type type = new Type()
            type.name = it[0]
            type.adSum = it[1]
            type.roomCount = it[2]
            type.save()
        }
    }
}
