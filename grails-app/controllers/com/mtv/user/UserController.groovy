package com.mtv.user

import com.mtv.Response

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
}
