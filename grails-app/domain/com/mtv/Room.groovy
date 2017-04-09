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
    Boolean isOnLine = false

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

    /* 是否记录日志 */
    Boolean isLog = false

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
            // Math.abs(log.n / contentData[i - 1].n - 1)
            // 观众波动百分比
            Double wave = Math.abs(adNum / this.adNum - 1)
            // 以前的分值
            Double curRatio = this.sort / this.adNum
            // 分值变化比例
            Double change = 1;
            if(wave <= 0.05){           // 波动5%以内不管增加还是减少 都加分
                change = 1.04;
            }else if(wave <= 0.1){      // 波动超过10%则减少分数
                change = 0.9
            }else if(wave <= 0.15){
                change = 0.85
            }else{
                change = 0.8
            }
            // 如果是上升 则增加1%分数
            if(change < 1 && adNum > this.adNum){
                change = 1.01
            }
            curRatio *= change
            // 最大分值限定
            curRatio = curRatio > 10000 ? 10000 : curRatio
            this.sort = (adNum * curRatio).longValue()
            this.adNum = adNum
        }else{
            this.sort = adNum
            this.adNum = adNum
        }
    }

}
