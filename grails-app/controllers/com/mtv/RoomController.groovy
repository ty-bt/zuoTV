package com.mtv

import grails.converters.JSON
import org.apache.commons.lang.StringEscapeUtils
import org.springframework.util.Assert

class RoomController {

    /**
     * 喂给搜索引擎
     * @return
     */
    def list(){
        [rooms: getRoom(ParamUtils.limit())]
    }

    /**
     * 喂给搜索引擎
     * @return
     */
    def detail(){
        [room: Room.get(params.getLong("id"))]
    }

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
//            eq('isOnLine', true)
//            order('adNum', "desc")

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
        render([rooms: rooms, total: rooms.totalCount] as JSON)
    }

    def one(){
        def roomId = params.getLong("id")
        Assert.notNull(roomId, "ID不能为空")

        def room = Room.findById(roomId)
        render([room: room] as JSON)
    }
}
