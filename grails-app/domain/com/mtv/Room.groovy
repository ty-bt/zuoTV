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

    /* 分数 */
    Double mark = 0D

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

        // 分值计算
        if(this.isOnLine && this.adNum){
//            // Math.abs(log.n / contentData[i - 1].n - 1)
//            // 观众波动百分比
//            Double wave = Math.abs(adNum / this.adNum - 1)
//            Double change = 0;
//            if(this.adNum > 2000){
//                if(wave <= 0.05){
//                    change = 0.1;
//                }else if(wave <= 0.1){
//                    change = -0.5
//                }else if(wave <= 0.15){
//                    change = -1
//                }else{
//                    change = -1
//                }
//                if(change <= 0 && adNum > this.adNum){
//                    change = 0.15;
//
//                }else if(change <= 0 ){
//                    this.mark = this.mark > 0 ? (this.mark - 1).longValue() : this.mark.longValue();
//                }
//                change *= this.adNum / 10000;
//            }
//            this.mark += change
//            this.mark = this.mark >= 1000000 ? 1000000 : this.mark
            this.sort = adNum - this.adNum
        }else{
            this.sort = adNum
        }
        this.adNum = adNum
    }

}
