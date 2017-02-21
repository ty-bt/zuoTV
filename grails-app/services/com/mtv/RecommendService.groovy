package com.mtv

import grails.transaction.Transactional
import org.apache.commons.lang.math.RandomUtils

@Transactional
class RecommendService {

    public void reload(){
        Recommend.executeUpdate("delete Recommend ")
        List result = Collect.executeQuery("select c.room.id, count(c) from Collect c group by c.room.id")
        Map reMap = [:]
        result.each {
            OnLineRoom onLineRoom = OnLineRoom.get(it[0])
            if(onLineRoom){
                reMap.put(it[0], [level: it[1], onLineRoom: onLineRoom])
            }
        }

        // 分类前20个各取出1个房间
        List<Type> types = Type.createCriteria().list{
            setMaxResults(25)
            order("adSum", "desc")
        }
        types.each {
            String tagName = it.name
            List<OnLineRoom> list = OnLineRoom.createCriteria().list {
                setMaxResults(2)
                eq('tag', tagName)
            }
            list.each {
                if(reMap.containsKey(it.id)){
                    reMap[it.id].level++
                }else{
                    reMap.put(it.id, [level: 1, onLineRoom: it])
                }
            }

        }
        reMap.each {k, v->
            Recommend recommend = new Recommend()
            recommend.onLineRoom = v.onLineRoom
            recommend.level = v.level
            // 等级一样按照这个随机排序
            recommend.sort = RandomUtils.nextInt(100)
            recommend.save()
        }

    }
}
