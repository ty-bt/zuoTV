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

    /**
     * 时间增减工具
     * @param date 起始时间
     * @param field 单位 Calendar中的常量
     * @param amount 数量
     * @return
     */
    public static Date amount(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.add(field, amount)
        return calendar.getTime()
    }

    static Date formatDayMax(Date date) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.getTime()
    }

    static Date formatDayMin(Date date) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.getTime()
    }
    static Date addDays(Date date, int amount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }

}
