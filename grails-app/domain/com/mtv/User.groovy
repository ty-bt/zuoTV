package com.mtv

class User {

    static{
        grails.converters.JSON.registerObjectMarshaller(User){
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'lastUpdated', 'password'])
            }
        }
    }

    String name

    String password

    String email

    Boolean isVip = false

    /* 是否启用 */
    Boolean enable = true

    /*创建时间*/
    Date dateCreated

    /*最后修改时间*/
    Date lastUpdated

    static constraints = {
        email nullable: true
        name unique: true
    }

}
