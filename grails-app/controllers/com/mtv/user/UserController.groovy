package com.mtv.user

import com.mtv.Response
import com.mtv.User

class UserController {

    def userService

    def index() {

    }

    def login(){
        User user = userService.login(params.name, params.password)
        render Response.success(user).toJSON()
    }

    def register(){
        User user = userService.register(params.name, params.password, params.email)
        render Response.success(user).toJSON()
    }

    def getCurrentUser(){
        render Response.success(userService.getCurrentUser()).toJSON()
    }
}
