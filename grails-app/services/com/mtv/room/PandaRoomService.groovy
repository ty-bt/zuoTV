package com.mtv.room

import com.alibaba.fastjson.JSON
import com.mtv.DateUtils
import com.mtv.Room
import grails.transaction.Transactional
import org.jsoup.Jsoup
import org.springframework.util.Assert


@Transactional
class PandaRoomService extends SupportLoadRoom {

    def typeContrastService

    /**
     *
     * @param platformFlag 平台标识
     */
    PandaRoomService() {
        super("panda")
    }

    /**
     * 刷新平台房间数据
     */
    @Override
    public List<List<Map>> loadData() {

        def tempObj = this.getPageObj(1, 1)
        Assert.notNull(tempObj?.data?.total, "${platformFlag}获取总条数异常")

        // 总条数
        int total = tempObj.data.total
        int pageSize = 120
        // 总页数
        int pageCount = Math.ceil(total / pageSize.doubleValue()).intValue()
        log.info("${platformFlag}总条数:${total},总页数${pageCount}")

        List pageList = []
        for(int a = 1; a <= pageCount; a++){
//            println "${platformFlag}正在获取第${a}页数据."
            Object pageObj = this.getPageObj(a, pageSize)
            List<Map> list = pageObj?.data?.items

            Assert.notNull(list, "${platformFlag}第${a}页获取异常...")
            pageList << list

        }
        return pageList
    }

    @Override
    void saveRoom(Object obj) {
        List<List<Map>> pageList = (List<List<Map>>)obj
        Date lastUpdated = DateUtils.getDateNoMSEL()
        int i = 1
        // 解析并保存数据
        pageList.each {
//            print("${platform.name}: ${pageList.size()}/${i++}")
            List<Map> list = it
            list.each{
                // 查看如果是老数据则覆盖,新数据则新建
                String roomId = it.id
                Room room = Room.findByPlatformAndFlag(platform, roomId)
                if(!room){
                    room = new Room(platform: platform, flag: roomId)
                }
                room.isOnLine = true
                room.name = it.name
                room.img = it.pictures.img
                room.tag = typeContrastService.getTypeName(it.classification.cname)
                room.adNum = Long.parseLong(it.person_num)
                room.anchor = it.userinfo.nickName
                room.url = "http://www.panda.tv/" + it.id
                room.lastUpdated = lastUpdated
                room.save()
            }
        }

        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = false where r.platform = ? and r.lastUpdated < ?", [this.platform, lastUpdated])
    }

    public Object getPageObj(int pageIndex, int pageSize){
        String body = Jsoup.connect("http://www.panda.tv/live_lists?status=2&order=person_num&pageno=${pageIndex}&pagenum=${pageSize}")
                .timeout(60000)
                .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .ignoreContentType(true).execute().body()
        return JSON.parse(body)
    }




}
