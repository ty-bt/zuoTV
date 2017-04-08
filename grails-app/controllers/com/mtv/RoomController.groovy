package com.mtv

import grails.converters.JSON
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.math.RandomUtils
import org.springframework.util.Assert

class RoomController {

//    /**
//     * 喂给搜索引擎
//     * @return
//     */
//    def list(){
//        [rooms: getRoom(ParamUtils.limit())]
//    }
//
//    /**
//     * 喂给搜索引擎
//     * @return
//     */
//    def detail(){
//        [room: Room.get(params.getLong("id"))]
//    }

    public List<OnLineRoom> getRoom(Object limit){
        return OnLineRoom.createCriteria().list(limit){
            if(params.kw){
                or{
                    like('name', "%${StringUtils.escapeSql(params.kw)}%")
                    like('anchor', "%${StringUtils.escapeSql(params.kw)}%")
                }

            }
            if(params.tag){
                eq('tag', params.tag)
            }
            if(params.platformName){
                Platform platform = Platform.findByName(params.platformName)
                Assert.notNull(platform, "找不到对应的平台")
                eq('platform', platform)
            }
        }
    }

//    def getRecommend(){
//
//        def recommendList = Collect.createCriteria().list(ParamUtils.limit()){
//            groupProperty('room')
//            count("room", "collectCount")
//            order("collectCount", "desc")
//            room{
//                eq('isOnLine', true)
//                order('adNum', "desc")
//            }
//
//        }
//        def recommends = recommendList.collect {
//            return it[0]
//        }
//        render([recommends: recommends, total: recommendList.totalCount] as JSON)
//    }

    def getRecommend(){


        def recommendList = Recommend.createCriteria().list(ParamUtils.limit()){
            order("level", "desc")
            order("sort", "desc")
        }
        def recommends = recommendList.collect {
            return it.onLineRoom
        }
        render([recommends: recommends, total: recommendList.totalCount] as JSON)
    }

    def page(){
        def rooms = getRoom(ParamUtils.limit())
        // 增加5个推荐
        def recommends = []
        if(!params.kw && !params.tag){
            int rCount = Recommend.count()
            RandomUtils.nextInt(rCount)
            recommends = Recommend.createCriteria().list(max: 5, offset: RandomUtils.nextInt(rCount - 6)){
                setMaxResults(5)
                order("sort", "desc")
            }.collect {
                return it.onLineRoom
            }
        }

        render([rooms: rooms, total: rooms.totalCount, recommends: recommends] as JSON)
    }

    def split(){
//        def start = System.currentTimeMillis()

        List ids = JSON.parse(params.ids)
        if(ids.size() > 4){
            render(text: [success: false, message: "超出上限"])
            return
        }
        def result = []
        ids.each {
            result.push(Room.get(it))
        }
//        render(text: "用时${System.currentTimeMillis() - start}")

        render(text: [success: true, rooms: result] as JSON)

    }

    def splitTest(){
        def start = System.currentTimeMillis()
        List ids = JSON.parse(params.ids)
        if(ids.size() > 4){
            render(text: [success: false, message: "超出上限"])
            return
        }
        def result = []
        ids.each {
            result.push(Room.get(it))
        }
        render(text: "用时${System.currentTimeMillis() - start}")
//        render(text: [success: true, rooms: result] as JSON)

    }

    def splitTest1(){
                def start = System.currentTimeMillis()
        List<Integer> ids = JSON.parse(params.ids)
        ids = ids.collect {
            return it.longValue()
        }
        if(ids.size() > 4){
            render(text: [success: false, message: "超出上限"])
            return
        }
        def result = Room.createCriteria().list {
            'in'('id', ids)
        }
                render(text: "用时${System.currentTimeMillis() - start}")

    }

    def one(){
        def roomId = params.getLong("id")
        Assert.notNull(roomId, "ID不能为空")

        def room = Room.findById(roomId)
        render([room: room] as JSON)
    }

    /**
     * 获取观众数波动日志
     * @return
     */
    def getRoomLog(){
        def roomLogs = RoomLog.createCriteria().list(ParamUtils.limit()){
            if(params.roomId){
                eq('room.id', params.long('roomId'))
            }
            if(params.order){
                order(params.order, params.orderType ?: "desc")
            }
        }
        render Response.success([roomLogs: roomLogs, total: roomLogs.totalCount]).toJSON()
    }


}
