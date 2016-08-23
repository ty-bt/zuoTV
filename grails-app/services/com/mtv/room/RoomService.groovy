package com.mtv.room

import com.mtv.Platform
import com.mtv.utils.BeanLookup
import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation
import org.springframework.util.Assert

@Transactional
class RoomService {

    def serviceMethod() {

    }

    /**
     * 重新加载指定平台数据
     * @param platform
     */
    def loadRoom(Platform platform){
        Assert.notNull(platform, "平台不能为空")

        // 获取平台指定服务类
        Object support = BeanLookup.support(SupportLoadRoom.class, platform.flag)
        Assert.notNull(support, "没有找到对应服务类${platform.flag}")

        SupportLoadRoom supportLoadRoom = (SupportLoadRoom)support
        supportLoadRoom.load()
    }

    /**
     * 重新加载所有平台数据 无事务控制
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    def loadAll(){
        Platform.findAll().each {
            try{
                loadRoom(it)
            }catch (e){
                log.error(e)
            }
        }
    }

    /**
     * 重新加载所有平台数据(多线程) 无事务控制
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    def loadAllT(){
        Platform.findAll().each {
            try{
                Platform platform = it
                Thread.start {
                    loadRoom(platform)
                }
            }catch (e){
                log.error(e)
            }
        }
    }
}
