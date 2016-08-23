package com.mtv

class Room {

    Platform platform

    /* 是否在线 */
    Boolean isOnLine

    /* 平台中的唯一标识 房间号 */
    String flag

    /* 房间标题*/
    String name

    /* 图片 */
    String img

    /* 标签*/
    String tag

    /* 人数 */
    Long adNum

    /* zb */
    String anchor

    /* 房间url*/
    String url

    /* 外部引用url */
    String quoteUrl


    static constraints = {
        quoteUrl nullable: true
    }

}
