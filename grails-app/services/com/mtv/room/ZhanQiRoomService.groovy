package com.mtv.room

import com.alibaba.fastjson.JSON
import com.mtv.DateUtils
import com.mtv.Room
import grails.transaction.Transactional
import org.jsoup.Jsoup

@Transactional
class ZhanQiRoomService extends SupportLoadRoom {

    def typeContrastService

    /**
     *
     * @param platformFlag 平台标识
     */
    ZhanQiRoomService() {
        super("zhanQi")
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    public List<List<Map>> loadData() {

        List pageList = []
        int pageCount = 1
        for(int a = 1; a <= pageCount; a++){
            def page = this.getPageObj(a)
            if(a == 1){
                pageCount = Math.ceil(page.data.cnt / 30d)
                log.info("${platformFlag}总条数:${page.data.cnt},总页数${pageCount}")
            }
            pageList << page.data.rooms
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
                // http://www.quanmin.tv/task/share?from=_39beabe71c3e2bc143558bee32c315f2_1849855
                String roomId = it.id
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                room.name = it.title
                room.img = it.bpic
                room.tag = typeContrastService.getTypeName(it.gameName)
                room.reSetAdNum(Long.parseLong(it.online))

                room.anchor = it.nickname
                room.url = "https://www.zhanqi.tv" + it.url
                room.lastUpdated = lastUpdated
                // 在线状态最后改
                room.isOnLine = true
                room.save()
            }
        }

        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = false where r.platform = ? and r.lastUpdated < ?", [this.platform, lastUpdated])
    }


    public Object getPageObj(int pageIndex){
        String body = Jsoup.connect("https://www.zhanqi.tv/api/static/v2.1/live/list/30/${pageIndex}.json")
                .timeout(60000)
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        return JSON.parse(body)
    }




}
