package com.mtv

class Recommend {

    OnLineRoom onLineRoom

    // 推荐等级 越高排名越前
    Long level

    // 等级一样时候的排序
    Integer sort

    static constraints = {

    }

    static mapping = {
        cache usage: 'read-only',include: 'all'
    }
}
