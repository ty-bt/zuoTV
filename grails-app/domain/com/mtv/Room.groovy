package com.mtv

class Room {

    static{
        grails.converters.JSON.registerObjectMarshaller(Room){
            return it.properties.findAll{k,v->
                !(k in ['class', 'dateCreated', 'lastUpdated', 'sort'])
            } + [id:it.id]
        }
    }

    Platform platform

    /* 是否在线 */
    Boolean isOnLine

    /* 平台中的唯一标识 房间号 */
    String flag

    /* 房间标题*/
    String name

    /* 图片 */
    String img

    /* 标签 类型*/
    String tag

    /* 人数 */
    Long adNum = 0L

    /* zb */
    String anchor

    /* 房间url*/
    String url

    /* 外部引用url */
    String quoteUrl

    /* 排序 越大越往前*/
    Long sort = 0L

    /*创建时间*/
    Date dateCreated = new Date()

    /*最后修改时间*/
    Date lastUpdated = new Date()

    static mapping = {
        // 更新的时候会自动更新lastUpdated 所以这个不用自动更新时间
        autoTimestamp false
        cache true
    }

    static constraints = {
        quoteUrl nullable: true
    }

    void reSetAdNum(Long adNum){

        // 差值
        if(this.isOnLine && this.adNum){
            // 观察期 开播7-14分钟 不调整sort
            if(this.adNum == this.sort){
                this.sort = adNum + 1
                this.adNum = adNum
                return
            }
            // 观众升降比例
            Double curRatio = (this.sort / this.adNum) * (adNum / this.adNum)
            // 最大100倍
            curRatio = curRatio > 100 ? 100 : curRatio
            // 最小减少20倍
            curRatio = curRatio < 0.05 ? 0.05 : curRatio
            this.sort = curRatio * adNum
            this.adNum = adNum
        }else{
            this.sort = adNum
            this.adNum = adNum
        }
    }

}
