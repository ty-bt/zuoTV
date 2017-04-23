package com.mtv.jobs

import com.mtv.Room
import grails.util.Environment


class RoomJob {

    def roomService
    def typeService
    def platformService
    def recommendService

    // 禁止同时执行两个 上一个还未执行完则等下一次
    def concurrent = false

    static triggers = {
        // 4分钟刷新一次
        simple repeatInterval: 60000L * 5, startDelay: 10000 // execute job once in 5 seconds
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
