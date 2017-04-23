package com.mtv.jobs

import com.mtv.DateUtils


class LogSplitJob {
    static triggers = {
        // 每天凌晨两点执行
        cron name: "房间观众波动日志分表存储", cronExpression: '0 0 2 * * ?'
    }

    def roomLogService

    def execute() {
        // 分割今天之前的观众波动日志
        roomLogService.splitTable(DateUtils.formatDayMin(new Date()))
    }
}
