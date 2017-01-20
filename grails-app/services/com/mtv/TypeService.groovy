package com.mtv

import grails.transaction.Transactional

@Transactional
class TypeService {

    /**
     * 重新统计所有类型
     */
    public void reloadAll(){
        def startDate = System.currentTimeMillis()
        log.info("平台类型数据统计开始")
        List list = Room.executeQuery("select r.tag, sum(r.adNum) as adSum, count(r.tag) as roomCount from OnLineRoom as r where r.tag != '' group by r.tag order by adSum desc")
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
        log.info("平台类型数据统计完成, 用时${System.currentTimeMillis() - startDate}ms")

    }
}
