package com.mtv.room

import com.mtv.DateUtils
import com.mtv.Room
import com.mtv.RoomLog
import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional
class RoomLogService {

    def serviceMethod() {

    }

    /**
     * 记录日志
     * @param room 房间号
     * @param newOnLine 是否新上线
     * @param date 抓取时间
     * @return
     */
    public RoomLog log(Room room, Boolean newOnLine, Date date){
        // 观看人数1000以下不记录日志
        if(room.adNum < 1000){
            return
        }
        RoomLog roomLog = RoomLog.createCriteria().get{
            eq('room.id', room.id)
            ge('dateStart', DateUtils.formatDayMin(date))
        }
        if(!roomLog){
            roomLog = new RoomLog()
            roomLog.room = room
            roomLog.dateStart = date
        }else{
            // 有时候会重复设置
            if(roomLog.dateStart >= date){
                return
            }
        }

        roomLog.addLog(room, date, newOnLine)
        return roomLog.save()

    }

    def dataSource

    /**
     * 分表保存数据 将room_log某一天前的数据分散到多个表保存
     * @param overDate
     */
    public void splitTable(Date overDate = new Date()){
        def start = System.currentTimeMillis()
        log.info("overDate: ${overDate} 分表保存开始...")
        Sql sql
        try{
            sql = new Sql(dataSource)
            // 分割的表数量
            def tableSize = 15
            for(i in 0..(tableSize - 1)){
                // 表不存在则创建表
                String tableName = "v_room_log_${i}"
                sql.executeUpdate("""
                Create Table If Not Exists """ + tableName + """(
                  `id` bigint(20) NOT NULL auto_increment,
                  `version` bigint(20) NOT NULL,
                  `content` text collate utf8_unicode_ci NOT NULL,
                  `last_updated` datetime NOT NULL,
                  `room_id` bigint(20) NOT NULL,
                  `date_start` datetime NOT NULL,
                  PRIMARY KEY  (`id`),
                  KEY `room_id` (`room_id`),
                  KEY `date_start` (`date_start`)
                ) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
            """)
                // 将符合规则的数据插入 通过取余数来拆分
                sql.executeUpdate("""
                    INSERT INTO ${tableName}(
                      version, content, last_updated, room_id, date_start
                    )
                    SELECT version, content, last_updated, room_id, date_start
                    FROM room_log where date_start < ? and room_id % ? = ?
                """, [overDate, tableSize, i])
                def rowSize = RoomLog.executeUpdate("delete RoomLog r where r.dateStart < ? and MOD(r.id, ?) = ?", [overDate, tableSize, i])
                log.info("分表${i}完成 - 影响行：${rowSize} - ${System.currentTimeMillis() - start}ms")
            }
        }catch (e) {
            e.printStackTrace()
            log.info("分表保存异常 - ${System.currentTimeMillis() - start}ms")
        }finally{
            sql?.close()
            log.info("分表保存完成 - ${System.currentTimeMillis() - start}ms")
        }

    }
}
