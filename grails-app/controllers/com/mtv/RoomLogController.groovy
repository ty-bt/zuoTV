package com.mtv

import org.springframework.util.Assert

class RoomLogController {

    def one(){
        Long roomId = params.long("roomId")
        Assert.notNull(roomId, "房间ID不能为空")
        RoomLog roomLog = RoomLog.createCriteria().get{
            eq('room.id', roomId)
            ge('dateStart', DateUtils.formatDayMin(new Date()))
        }
        Assert.notNull(roomLog, "没有找到日志，观众数小于1000不记录日志")
        render Response.success(roomLog).toJSON()
    }

}
