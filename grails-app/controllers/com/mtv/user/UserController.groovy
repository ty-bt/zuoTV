package com.mtv.user

import com.mtv.Response
import com.mtv.User

class UserController {

    static namespace = "center"

    def userService

    def updatePwd(){
        if(!params.oldPwd){
            throw new IllegalArgumentException("老密码不能为空")
        }
        userService.updatePwd(userService.getCurrentUser().id, params.password, params.oldPwd)
        render Response.success().toJSON()

    }

    def setVip(){
        Long userId = userService.getCurrentUser().id
        userService.setVip(userId)
        request.session.setAttribute("user", User.get(userId))
        render Response.success().toJSON()

    }
}
