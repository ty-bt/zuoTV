package com.mtv.room

import com.mtv.Platform
import com.mtv.Room
import com.mtv.utils.SupportBeanDefine
import org.springframework.util.Assert

/**
 * 平台房间重新加载 抽象类
 * Created by 谭勇 on 2016/7/13 0013.
 */
abstract class SupportLoadRoom implements SupportBeanDefine<String>{

    // 当前平台标识
    abstract String platformFlag

    // 当前平台
    abstract Platform platform

    /**
     *
     * @param platformFlag 平台标识
     */
    public SupportLoadRoom(String platformFlag){
        this.platformFlag = platformFlag
    }

    /**
     * 如果支持返回true 不支持返回false
     * 自动按照platformFlag判断
     * @param object 判断依据
     * @return
     */
    public boolean support(String platformFlag){
        println platformFlag + "=" + this.platformFlag + "-" + (this.platformFlag == platformFlag)
        return this.platformFlag == platformFlag
    }

    /**
     * 重新获取平台房间数据
     */
    public abstract void loadRoom()

    /**
     * 初始化方法
     */
    public void init(){
        // 获取平台对象
        Platform platform = Platform.findByFlag(this.platformFlag)
        Assert.notNull(platform, "没有找到平台数据${this.platformFlag}")
        this.platform = platform
        // 将平台下所有房间置为离线
        Room.executeUpdate("update Room r set r.isOnLine = true where r.platform = ?", [platform])
    }

    /**
     * 主方法，顺序执行init， loadRoom
     */
    public void load(){
        Long start = System.currentTimeMillis()
        log.info("${platformFlag}开始获取数据.")
        this.init()
        this.loadRoom()
        log.info("${platformFlag}获取数据完成.用时 ${System.currentTimeMillis() - start}ms")
    }

}
