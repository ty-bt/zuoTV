package com.mtv

import com.alibaba.fastjson.JSON
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.util.Assert

@Transactional
class ChatService {

    // 日志集合
    private synchronized static List logs = []

    SimpMessagingTemplate brokerMessagingTemplate

    public void send(String msg, User user) {
        Assert.notNull(user, "用户不能为空")
        if(!msg?.trim()){
            throw new Exception("发送内容不能为空")
        }
        if(msg.length() > 500){
            throw new Exception("内容过长")
        }
        def sendMap = [u: [n: user.name, i: user.id], msg: msg, d: new Date()];
        brokerMessagingTemplate.convertAndSend("/topic/chatMsg", JSON.toJSONString(sendMap))
        this.log(sendMap)
    }

    /**
     * 发送公告
     * @param msg
     * @param type
     */
    public void sendNotice(String msg, Integer type = 1) {
        Assert.notNull(msg, "内容不能为空")
        Assert.notNull(type, "类型不能为空")

        def sendMap = [msg: msg, type: type, d: new Date()];
        brokerMessagingTemplate.convertAndSend("/topic/chatMsg", JSON.toJSONString(sendMap))
        this.log(sendMap)
    }


    /**
     * 获取最近的日志
     * @param offset
     * @return
     */
    public List latelyLog(int offset){
        if(!offset || offset > logs.size()){
            offset = logs.size()
        }
        return logs.subList(logs.size() - offset, logs.size())
    }

    /**
     * 记录日志  只是记录在内存中 达到数量自动记录到数据库
     * @param obj
     */
    public synchronized void log(obj){
        this.logs.push(obj)
        if(this.logs.size() >= 520){
            saveLog(20)
        }
    }

    /**
     * 保存日志到数据库
     * @param splitSize 留下的条数 0 为全部保存
     */
    public void saveLog(Integer splitSize = 0){
        int offset = logs.size() - splitSize
        if(offset > 0){
            List saveData = logs.subList(0, offset)
            ChatLog chatLog = new ChatLog()
            chatLog.content = JSON.toJSONString(saveData)
            chatLog.startDate = saveData[0].d
            chatLog.endDate = saveData[-1].d
            chatLog.save()
        }



    }
}
