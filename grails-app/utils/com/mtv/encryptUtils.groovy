package com.mtv

/**
 * 字符串工具类
 * Created by 谭勇 on 2016/7/13 0013.
 */
class EncryptUtils {

    /** 密码盐值*/
    public static final String SALT_PWD = "MTTV-PWD@nozuonodie.cn"

    /**
     * 密码加密
     * @param text
     * @return
     */
    public static String pwd(String text){
        return (text.encodeAsMD5() + this.SALT_PWD).encodeAsSHA256()
    }
}
