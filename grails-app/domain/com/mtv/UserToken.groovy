package com.mtv

import com.alibaba.fastjson.JSON

import javax.servlet.http.Cookie

/**
 * 用户token保持登录凭证
 */
class UserToken {

    // 存放在cookie中的名称
    public static final String COOK_NAME = "userToken"

    User user

    // token字符串
    String token

    // 有效时间
    Date validDate

    /*创建时间*/
    Date dateCreated

    static constraints = {
    }

    /**
     * 根据token对象生成cookie
     * @return
     */
    public Cookie createCookie(){
        Cookie cookie = new Cookie(UserToken.COOK_NAME, this.token)
        cookie.setPath("/")
        cookie.setHttpOnly(true)
        cookie.setMaxAge(((this.validDate.getTime() - System.currentTimeMillis()) / 1000).intValue())
        return cookie
    }

}
