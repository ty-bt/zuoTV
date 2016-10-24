package com.mtv

/**
 * 房间类型
 */
class Type {

    // 分类名称
    String name

    // 房间总数
    Long roomCount

    // 观众总数
    Long adSum

    // 自定义排序字段
    Long sort = 0

    /*创建时间*/
    Date dateCreated

    static constraints = {
    }
}
