package com.mtv

/**
 * 字符串工具类
 * Created by 谭勇 on 2016/7/13 0013.
 */
class StringUtils {
    /**
     * 将单位为万的字符串转换为 数字
     * @param numStr '1.2万'
     * @return
     */
    public static Long parseNum(String numStr){
        String text = numStr.trim()
        if(!text){
            return 0l
        }
        def pattern = ~/(\d*\.?\d*)万/
        if(text ==~ pattern){
            String wStr = (text =~ pattern)[0][1]
            if(!wStr.isDouble()){
                throw new Exception("${numStr}不是一个数字")
            }
            return (wStr.toDouble() * 10000).longValue()
        }else{
            if(!text.isLong()){
                throw new Exception("${numStr}不是一个数字")
            }
            return text.toLong()
        }
    }
}
