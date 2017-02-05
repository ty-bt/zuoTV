package com.mtv.room

import com.mtv.OnLineRoom
import com.mtv.Platform
import com.mtv.Recommend
import com.mtv.utils.BeanLookup
import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation
import org.springframework.util.Assert

@Transactional
class RoomService {

    def typeContrastService

    /**
     * 重新加载指定平台数据
     * @param platform
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    def loadAll(){
        Platform.findAll().each {
            loadRoom(it)
        }
    }

    /**
     * 保存一个平台的数据, 使用单独事务执行
     * @param supportLoadRoom 平台数据加载服务类
     * @param data 服务类中loadData加载到的数据
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRoom(SupportLoadRoom supportLoadRoom, Object data){
        supportLoadRoom.saveRoom(data)
    }

    /**
     * 重新加载所有平台数据(多线程)
     * @return
     */
    def loadAllT(){
        // 服务类map
        Map serverMap = [:]
        // 抓取到的数据
        Map dataMap = [:]
        // 线程
        Map<String, Thread> threadList = [:]
        // 刷新类型对照表
        typeContrastService.reload()

        Platform.createCriteria().list({
            order('onLineRoom')
        }).each {
            Platform platform = it
            // 获取平台指定服务类
            Object support = BeanLookup.support(SupportLoadRoom.class, platform.flag)
            Assert.notNull(support, "没有找到对应服务类${platform.flag}")

            SupportLoadRoom supportLoadRoom = (SupportLoadRoom)support
            serverMap.put(platform.flag, supportLoadRoom)
            supportLoadRoom.init()
            // 多线程中没有事务 只负责抓取数据
            threadList.put(platform.flag, Thread.start {
                try{
                    log.info("抓取${platform.name}开始")
                    Long startDate = System.currentTimeMillis()
                    dataMap[platform.flag] = supportLoadRoom.loadData()
                    log.info("抓取${platform.name}完成, 用时${System.currentTimeMillis() - startDate}ms")
                }catch (e){
                    log.error("${platform.name}抓取数据出错")
                    log.error(e)
                    e.printStackTrace()
                }
            })
        }
        // 线程执行完毕之后 执行保存方法 插入数据库
        threadList.each {k, v ->
            v.join()
            log.info("保存${k}开始")
            Long startDate = System.currentTimeMillis()
            if(!dataMap[k]){
                return
            }
            this.saveRoom(serverMap[k], dataMap[k])
            log.info("保存${k}完成, 用时${System.currentTimeMillis() - startDate}ms")
        }
        Long olDate = System.currentTimeMillis()
        log.info("在线房间表更新开始")
        // 清空在线房间数据库 重新插入
        Recommend.executeUpdate("delete Recommend ")
        OnLineRoom.executeUpdate("delete OnLineRoom")
        log.info("清空完成, 用时${System.currentTimeMillis() - olDate}ms")
        OnLineRoom.executeUpdate("""insert into
            OnLineRoom(id, platform, isOnLine, flag, name, img, tag, adNum, anchor, url, quoteUrl)
            select r.id, r.platform, r.isOnLine, r.flag, r.name, r.img, r.tag, r.adNum, r.anchor, r.url, r.quoteUrl from Room r where r.isOnLine = true order by r.adNum desc""");
        log.info("在线房间表更新完成, 用时${System.currentTimeMillis() - olDate}ms")
    }
}
