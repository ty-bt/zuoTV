package com.mtv.room

import com.mtv.Platform
import com.mtv.Room
import com.mtv.StringUtils
import grails.transaction.Transactional
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.util.Assert


@Transactional
class DouyuRoomService implements SupportLoadRoom {

    final static String FLAG = "douyu"

    @Override
    boolean support(String object) {
        return object == FLAG
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    void loadRoom() {
        Platform platform = Platform.findByFlag(FLAG)
        Assert.notNull(platform, "没有找到平台数据${FLAG}")

        // 获取总页数
        Document pageCountDoc = Jsoup.parse(new URL("http://www.douyu.com/directory/all?page=2&isAjax=0"), 60000)
        String pageCountStr = pageCountDoc.select(".classify_li a").get(0).attr("data-pagecount")
        if(!pageCountStr || !pageCountStr.isInteger()){
            throw new Exception("总页数获取失败")
        }
        int pageCount = pageCountStr.toInteger()

        // 循环获取每一页的数据
        for(int a = 0; a < pageCount; a++){
            println "正在获取第${a+1}页数据."
            Document pageDoc = Jsoup.parse(new URL("http://www.douyu.com/directory/all?page=${a+1}&isAjax=1"), 60000)
            Elements eles = pageDoc.select("li")
            eles.each {
                Element ele = it
                def flag = ele.attr("data-rid")
                // 如果有数据则替换原来的数据 没有则新建
                Room room = Room.findByPlatformAndFlag(platform, flag)
                if(!room){
                    room = new Room(platform: platform, flag: flag, quoteUrl: "http://staticlive.douyutv.com/common/share/play.swf?room_id=${flag}")
                }
                room.name = ele.select("h3.ellipsis").text()
                room.img = ele.select(".imgbox img").attr("data-original")
                room.tag = ele.select(".tag").text()
                room.adNum = StringUtils.parseNum(ele.select(".dy-num").text())
                room.anchor = ele.select(".dy-name").text()
                room.url = "http://www.douyu.com" + ele.select("a").attr("href")
                room.save()
            }
        }
    }


}
