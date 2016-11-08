package com.mtv.jobs



class RoomJob {

    def roomService
    def typeService
    def platformService

    static triggers = {
        // 10分钟刷新一次
        simple repeatInterval: 600000l, startDelay: 10000 // execute job once in 5 seconds
    }

    def execute() {
        Long start = System.currentTimeMillis()
        println "定时任务执行 " + start
        roomService.loadAllT()
        typeService.reloadAll()
        platformService.statistics()
        println "定时任务执行完成,用时" + (System.currentTimeMillis() - start) + "ms"

    }
}
