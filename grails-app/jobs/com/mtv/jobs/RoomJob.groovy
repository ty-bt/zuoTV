package com.mtv.jobs



class RoomJob {

    def roomService

    static triggers = {
        // 10分钟刷新一次
        simple repeatInterval: 600000l, startDelay: 30000 // execute job once in 5 seconds
    }

    def execute() {
        println "定时任务执行 " + System.currentTimeMillis()
        roomService.loadAllT()
    }
}
