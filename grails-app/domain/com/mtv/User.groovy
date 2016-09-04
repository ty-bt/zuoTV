package com.mtv

class User {

    String name

    String password

    String email

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
