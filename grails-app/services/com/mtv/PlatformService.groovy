package com.mtv

import grails.transaction.Transactional

@Transactional
class PlatformService {

    def serviceMethod() {

    }

    public void statistics(){
        def startDate = System.currentTimeMillis()
        log.info("平台在线数据统计开始")
        List list = Platform.executeQuery("select p.id, count(r.id), sum(r.adNum) from OnLineRoom r join r.platform p group by p.id")
        // 插入
        list.each {
            Platform platform = Platform.get(it[0])
            platform.onLineRoom = it[1]
            platform.onLineAd = it[2]
            platform.save()
        }
        log.info("平台在线数据统计, 用时${System.currentTimeMillis() - startDate}ms")

    }
}
