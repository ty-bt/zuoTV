package com.mtv.room

import com.alibaba.fastjson.JSON
import com.mtv.DateUtils
import com.mtv.Room
import grails.transaction.Transactional
import org.jsoup.Jsoup
import org.springframework.util.Assert


@Transactional
class QuanMinRoomService extends SupportLoadRoom {

    def typeContrastService

    def roomLogService
    /**
     *
     * @param platformFlag 平台标识
     */
    QuanMinRoomService() {
        super("quanMin")
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    public List<List<Map>> loadData() {

        List pageList = []
        int pageCount = 1
        for(int a = 0; a < pageCount; a++){
            def page = this.getPageObj(a)
            if(!a){
                pageCount = page.pageCount
                log.info("${platformFlag}总条数:${page.total},总页数${page.pageCount}")
            }
            pageList << page.data
        }

        return pageList
    }

    @Override
    void saveRoom(Object obj) {
        List<List<Map>> pageList = (List<List<Map>>)obj
        Date lastUpdated = this.loadTime
        // 解析并保存数据
        pageList.each {
            List<Map> list = it
            list.each{
                Long adNum = Long.parseLong(it.view)
                // 观众小于等于10的不收录
                if(adNum < 10){
                    return
                }
                // 查看如果是老数据则覆盖,新数据则新建
                // http://www.quanmin.tv/task/share?from=_39beabe71c3e2bc143558bee32c315f2_1849855
                String roomId = it.uid
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                Boolean oldOLStatus = room.isOnLine
                room.name = it.title
                room.img = it.thumb
                room.tag = typeContrastService.getTypeName(it.category_name)
                room.reSetAdNum(adNum)
                room.anchor = it.nick
                room.url = "http://www.quanmin.tv/v/" + roomId
                room.lastUpdated = lastUpdated
                room.isOnLine = true
                room.save()
                // 记录日志 必须保存完在调用
                roomLogService.log(room, !oldOLStatus, lastUpdated)
            }
        }

        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = false where r.platform = ? and r.lastUpdated < ?", [this.platform, lastUpdated])
    }

    public Object getPageObj(int pageIndex){
        String body = Jsoup.connect("http://www.quanmin.tv/json/play/list${pageIndex ? "_" + pageIndex : ""}.json")
                .timeout(60000)
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        return JSON.parse(body)
    }




}
