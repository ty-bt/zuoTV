package com.mtv.jobs



class RoomJob {

    def roomService

    static triggers = {
        // 10分钟刷新一次
        simple repeatInterval: 600000l, startDelay: 600000l // execute job once in 5 seconds
    }

    def execute() {
        println "执行" + System.currentTimeMillis()
//        roomService.loadAll()
    }
}
