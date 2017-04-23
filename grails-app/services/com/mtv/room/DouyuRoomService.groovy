package com.mtv.room

import com.mtv.DateUtils
import com.mtv.Room
import com.mtv.StringUtils
import grails.transaction.Transactional
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


@Transactional
class DouyuRoomService extends SupportLoadRoom {

    def typeContrastService

    def roomLogService

    public DouyuRoomService() {
        super("douyu")
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    Object loadData() {

        // 获取总页数
        Document pageCountDoc = Jsoup.parse(new URL("http://www.douyu.com/directory/all?page=2&isAjax=0"), 60000)
        String pageCountStr = pageCountDoc.select(".classify_li a").get(0).attr("data-pagecount")
        if(!pageCountStr || !pageCountStr.isInteger()){
            throw new Exception("总页数获取失败")
        }
        int pageCount = pageCountStr.toInteger()
        log.info("${platformFlag}总页数${pageCount}.")

        List<Map> roomList = []
        // 循环获取每一页的数据
        for(int a = 0; a < pageCount; a++){
//            println "${platformFlag}正在获取第${a+1}页数据."
            Document pageDoc = Jsoup.parse(new URL("http://www.douyu.com/directory/all?page=${a+1}&isAjax=1"), 60000)
            Elements eles = pageDoc.select("li")
            // 从html中读取数据
            eles.each {
                Element ele = it
                def flag = ele.attr("data-rid")
                // 先存在map中
                Map room = [platform: platform, flag: flag, quoteUrl: "http://staticlive.douyutv.com/common/share/play.swf?room_id=${flag}"]
                room.isOnLine = true
                room.name = ele.select("h3.ellipsis").text()
                // 斗鱼可能有两个图片 取最后面那张
                Elements imgs = ele.select(".imgbox img")
                room.img = imgs.get(imgs.size() - 1).attr("data-original")
                room.tag = typeContrastService.getTypeName(ele.select(".tag").text())
                room.adNum = StringUtils.parseNum(ele.select(".dy-num").text())
                room.anchor = ele.select(".dy-name").text()
                room.url = "http://www.douyu.com" + ele.select("a").attr("href")
                roomList << room
            }
        }
        return roomList
    }

    @Override
    void saveRoom(Object obj) {
        List<Map> roomList = (List<Map>)obj
        // 最后修改时间
        Date lastUpdated = this.loadTime
        // 解析并保存数据
        int i = 1
        roomList.each {
            Map rooMap = it
            // 观众小于等于10的不收录
            if(rooMap.adNum < 10){
                return
            }
            // 如果有数据则替换原来的数据 没有则新建
            Room room = Room.findByPlatformAndFlag(this.platform, rooMap.flag)
            if(!room){
                room = new Room(platform: platform, flag: rooMap.flag, quoteUrl: rooMap.quoteUrl)
            }
            Boolean oldOLStatus = room.isOnLine
            room.name = rooMap.name
            room.img = rooMap.img
            room.tag = typeContrastService.getTypeName(rooMap.tag)
            room.reSetAdNum(rooMap.adNum)
            room.anchor = rooMap.anchor
            room.url = rooMap.url
            room.lastUpdated = lastUpdated
            room.isOnLine = true
            room.save()
            // 记录日志 必须保存完在调用
            roomLogService.log(room, !oldOLStatus, lastUpdated)
//            print("${platform.name}: ${roomList.size()}/${i++}")
        }
        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = false where r.platform = ? and r.lastUpdated < ?", [this.platform, lastUpdated])

    }
}
