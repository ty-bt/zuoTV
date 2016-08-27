package com.mtv.room

import com.mtv.Platform
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
        return this.platformFlag == platformFlag
    }

    /**
     * 通过网络获取平台房间数据
     * 此方法可能会通过多线程调用
     * 不能有数据库操作,不然可能造成死锁
     */
    public abstract Object loadData()

    /**
     * 对抓取到的数据进行处理并保存
     * @param obj loadRoom方法抓取到的数据
     */
    public abstract void saveRoom(Object obj)

    /**
     * 初始化方法
     */
    public void init(){
        // 获取平台对象
        Platform platform = Platform.findByFlag(this.platformFlag)
        Assert.notNull(platform, "没有找到平台数据${this.platformFlag}")
        this.platform = platform
    }

    /**
     * 主方法，顺序执行init， loadData
     * 多线程调用不会使用该方法
     */
    public void load(){
        Long start = System.currentTimeMillis()
        log.info("${platformFlag}开始抓取数据...")
        this.init()
        // 抓取页数据
        def pages = this.loadData()
        Long dataTime = System.currentTimeMillis()
        log.info("${platformFlag}抓取数据完成,开始处理...")
        this.saveRoom(pages)
        log.info("${platformFlag}重新加载完成, 抓取数据用时: ${System.currentTimeMillis() - dataTime}ms, 总用时 ${System.currentTimeMillis() - start}ms")
    }

}
