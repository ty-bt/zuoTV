package com.mtv

/**
 * 时间工具类
 * Created by 谭勇 on 2016/7/13 0013.
 */
class DateUtils {

    /**
     * 获取当前时间 精确到秒 置空毫秒 mysql只能精确到秒 防止查询出错
     * @return
     */
    public static Date getDateNoMSEL(){
        return new Date((System.currentTimeMillis() / 1000).longValue() * 1000)
    }

}
