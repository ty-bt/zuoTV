package com.mtv.room

import com.alibaba.fastjson.JSON
import com.mtv.DateUtils
import com.mtv.Room
import grails.transaction.Transactional
import org.jsoup.Jsoup

@Transactional
class LongZhuRoomService extends SupportLoadRoom {

    def typeContrastService

    def roomLogService
    /**
     *
     * @param platformFlag 平台标识
     */
    LongZhuRoomService() {
        super("longZhu")
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    public List<List<Map>> loadData() {

        List pageList = []
        int pageCount = 1
        for(int a = 0; a <= pageCount; a++){
            def page = this.getPageObj(a)
            if(!a){
                pageCount = Math.ceil(page.data.totalItems / 18d)
                log.info("${platformFlag}总条数:${page.data.totalItems},总页数${pageCount}")
            }
            pageList << page.data.items
        }

        return pageList
    }

    @Override
    void saveRoom(Object obj) {
        List<List<Map>> pageList = (List<List<Map>>)obj
        Date lastUpdated = DateUtils.getDateNoMSEL()
        // 解析并保存数据
        pageList.each {
            List<Map> list = it
            list.each{
                // 查看如果是老数据则覆盖,新数据则新建
                String roomId = it.channel.id
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                Boolean oldOLStatus = room.isOnLine
                room.name = it.channel.status
                room.img = it.preview
                room.tag = typeContrastService.getTypeName(it.game ? it.game[0].name : "")
                room.reSetAdNum(Long.parseLong(it.viewers))
                room.anchor = it.channel.name
                room.url = it.channel.url
                room.lastUpdated = lastUpdated
                room.isOnLine = true
                room.save()
                // 记录日志 必须保存完在调用
                if(room.isLog){
                    roomLogService.log(room, !oldOLStatus)
                }
            }
        }

        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = false where r.platform = ? and r.lastUpdated < ?", [this.platform, lastUpdated])
    }

    /**
     *
     * @param pageIndex 从0开始
     * @return
     */
    public Object getPageObj(int pageIndex){
        // 返回jsonp格式
        String callbackText = '_callbacks_._36bx66'
        String body = Jsoup.connect("http://api.plu.cn/tga/streams?max-results=18&start-index=${pageIndex * 18}&sort-by=views&filter=0&game=0&callback=${callbackText}")
                .timeout(60000)
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        body = body.substring(callbackText.length() + 1, body.length() - 1)
        return JSON.parse(body)
    }




}
