package com.mtv

import grails.transaction.Transactional

@Transactional
class PlatformService {

    def serviceMethod() {

    }

    public void statistics(){
        List list = Platform.executeQuery("select p.id, count(r.id), sum(r.adNum) from Room r join r.platform p where r.isOnLine = true group by p.id")
        // 插入
        list.each {
            Platform platform = Platform.get(it[0])
            platform.onLineRoom = it[1]
            platform.onLineAd = it[2]
            platform.save()
        }
    }
}
