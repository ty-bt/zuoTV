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
        if(this.isOnLine && this.adNum && this.adNum > 1000){
            // 观察期 7-14分钟 不调整sort
            if(this.adNum == this.sort){
                this.sort = adNum + 1
                this.adNum = adNum
                return
            }
            // 观众升降比例
            Double curRatio = (this.sort / this.adNum) * (adNum / this.adNum)
            // 调整上限 最大100倍
            Double max = this.adNum / 1000
            max = max > 100 ? 100 : max
            // 调整下限 最小减少50倍
            Double min =  1 / max
            min = min < 0.02 ? 0.02 : min
            curRatio = curRatio > max ? max : curRatio
            curRatio = curRatio < min ? min : curRatio
            this.sort = curRatio * adNum
            this.adNum = adNum
        }else{
            this.sort = adNum
            this.adNum = adNum
        }
    }

}
