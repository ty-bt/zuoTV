package filter

import grails.converters.JSON


class CenterFilters {

    def userService


    def filters = {
        all(namespace:'center', controller:'*', action:'*') {
            before = {
                def user = userService.currentUser
                if(!user){
                    render(text: [success: false, isLogin: false] as JSON)
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
