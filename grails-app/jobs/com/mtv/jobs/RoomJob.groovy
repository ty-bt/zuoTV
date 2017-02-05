package com.mtv.jobs

import grails.util.Environment


class RoomJob {

    def roomService
    def typeService
    def platformService
    def recommendService

    static triggers = {
        // 10分钟刷新一次
        simple repeatInterval: 600000l, startDelay: 10000 // execute job once in 5 seconds
    }

    def execute() {
        Long start = System.currentTimeMillis()
        println "定时任务执行 " + new Date()
        // 只有生产环境开启
        if(Environment.current == Environment.PRODUCTION){
            roomService.loadAllT()
            typeService.reloadAll()
            platformService.statistics()
            recommendService.reload()
        }
        println "定时任务执行完成,用时" + (System.currentTimeMillis() - start) + "ms"

    }
}
