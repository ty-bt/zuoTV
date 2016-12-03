package com.mtv.user

import com.mtv.Response
import com.mtv.User

class AuthController {

    def userService

    def index() {

    }

    def login(){
        User user = userService.login(params.name, params.password, params.boolean('keep', false))
        render Response.success(user).toJSON()
    }

    def register(){
        User user = userService.register(params.name, params.password, params.email)
        render Response.success(user).toJSON()
    }

    def getCurrentUser(){
        render Response.success(userService.getCurrentUser()).toJSON()
    }

    def logout(){
        User user = userService.logout()
        render Response.success(user).toJSON()
    }
}
