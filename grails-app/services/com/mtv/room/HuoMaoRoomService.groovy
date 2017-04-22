package com.mtv.room

import com.alibaba.fastjson.JSON
import com.mtv.DateUtils
import com.mtv.Room
import com.mtv.StringUtils
import grails.transaction.Transactional
import org.jsoup.Jsoup

@Transactional
class HuoMaoRoomService extends SupportLoadRoom {

    def typeContrastService

    def roomLogService
    /**
     *
     * @param platformFlag 平台标识
     */
    HuoMaoRoomService() {
        super("huoMao")
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
                pageCount = Math.ceil(page.data.allCount / 120d)
                log.info("${platformFlag}总条数:${page.data.allCount},总页数${pageCount}，火猫在线不在线的都在这里面")
            }
            pageList << page.data.channelList
            // 发现不在线的则结束
            if(page.data.channelList[page.data.channelList.size() - 1].is_live != '1'){
                break
            }
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

                // 发现不在线的则结束
                if(it.is_live != '1'){
                    return
                }

                Long adNum = StringUtils.parseNum(it.views)
                // 观众小于等于10的不收录
                if(adNum < 10){
                    return
                }

                // 查看如果是老数据则覆盖,新数据则新建
                String roomId = it.room_number
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                Boolean oldOLStatus = room.isOnLine
                room.name = it.channel
                room.img = it.image
                room.tag = room.tag = typeContrastService.getTypeName(it.gameCname)
                room.reSetAdNum(adNum)
                room.anchor = it.nickname
                room.url = "https://www.huomao.com/" + it.room_number
                room.lastUpdated = lastUpdated
                room.isOnLine = true
                room.save()
                // 记录日志 必须保存完在调用
                if(room.isLog){
                    roomLogService.log(room, !oldOLStatus, lastUpdated)
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
        String body = Jsoup.connect("https://www.huomao.com/channels/channel.json?page=${pageIndex}&page_size=120&game_url_rule=all")
                .timeout(60000)
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        return JSON.parse(body)
    }




}
