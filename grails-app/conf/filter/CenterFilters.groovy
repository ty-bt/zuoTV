package filter

import grails.converters.JSON

import javax.security.auth.login.LoginException

class CenterFilters {

    def userService

    // 未登录返回的数据
    static final LOGIN_ERROR_JSON = [success: false, isLogin: false] as JSON

    def filters = {
        all(namespace:'center', controller:'*', action:'*') {
            before = {
                def user = userService.currentUser
                if(!user){
                    render this.LOGIN_ERROR_JSON
                    return false
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
