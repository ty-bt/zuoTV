package com.mtv

/**
 * 收藏
 */
class Collect {

    /**分组 暂时不用*/
    CollectGroup collectGroup

    User user

    Room room

    /*创建时间*/
    Date dateCreated


    static constraints = {
        collectGroup nullable: true
    }
}
