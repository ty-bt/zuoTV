package com.mtv.room

import com.alibaba.fastjson.JSON
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
class PandaRoomService implements SupportLoadRoom {

    // 当前平台标识
    final static String FLAG = "panda"

    @Override
    public boolean support(String object) {
        return object == FLAG
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    public void loadRoom() {
        log.info("开始获取${FLAG}数据...")
        Platform platform = Platform.findByFlag(FLAG)
        Assert.notNull(platform, "没有找到平台数据${FLAG}")

        def tempObj = getPageObj(1, 1)
        Assert.notNull(tempObj?.data?.total, "${FLAG}获取总条数异常")

        // 总条数
        int total = tempObj.data.total
        int pageSize = 120
        // 总页数
        int pageCount = Math.ceil(total / pageSize.doubleValue()).intValue()
        log.info("${FLAG}总条数:${total},总页数${pageCount}")

        for(int a = 1; a <= pageCount; a++){
            println "正在获取第${a}页数据."
            Object pageObj = getPageObj(a, pageSize)
            List<Map> list = pageObj?.data?.items

            Assert.notNull(list, "${FLAG}第${a}页获取异常...")

            list.each{
                // 查看如果是老数据则覆盖,新数据则新建
                String roomId = it.id
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                room.name = it.name
                room.img = it.pictures.img
                room.tag = it.classification.cname
                room.adNum = Long.parseLong(it.person_num)
                room.anchor = it.userinfo.nickName
                room.url = "http://www.panda.tv/" + it.id
                room.save()
            }
        }
        log.info("${FLAG}数据获取完成...")

    }

    public Object getPageObj(int pageIndex, int pageSize){
        String body = Jsoup.connect("http://www.panda.tv/live_lists?status=2&order=person_num&pageno=${pageIndex}&pagenum=${pageSize}")
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        return JSON.parse(body)
    }




}
