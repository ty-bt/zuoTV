package com.mtv.room

import com.mtv.SupportBeanDefine

/**
 * 平台房间重新加载接口
 * Created by 谭勇 on 2016/7/13 0013.
 */
interface SupportLoadRoom extends SupportBeanDefine<String>{
    /**
     * 重新获取平台房间数据
     */
    public void loadRoom()
}
