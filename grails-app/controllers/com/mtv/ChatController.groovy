package com.mtv

import com.alibaba.fastjson.JSON
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.util.Assert

import java.security.Principal

/**
 * 聊天
 */
class ChatController {

    def chatService

    def userService

    def sessionTrackerService

    SimpMessagingTemplate brokerMessagingTemplate

    def send(){
        User user = userService.getCurrentUser()
        Assert.notNull(user, "登录才能玩")
        chatService.send(params.msg, user)
        render Response.success() as grails.converters.JSON
    }

    def sendNotice(){
        User user = userService.getCurrentUser()
        if(user?.id != 1l){
            throw new Exception("BOOM")
        }
        chatService.sendNotice(params.msg)
        render Response.success() as grails.converters.JSON
    }

    def latelyLog(){
        render Response.success(chatService.latelyLog(20)) as grails.converters.JSON
    }
    def saveLog(){
        chatService.saveLog()
        render Response.success() as grails.converters.JSON
    }


    /**
     * websocket 发送消息, 推送消息地址 已废弃(20170618)
     * @param headerAccessor
     * @param text
     * @return
     */
//    @MessageMapping("chatMsg")
//    @SendToUser("/topic/chatMsg1")
//    @SendTo
//    protected String chat(SimpMessageHeaderAccessor headerAccessor, Map obj){
//        if(!headerAccessor.getUser()){
//            headerAccessor.setUser(new Principal() {
//                @Override
//                public String getName() {
//                    return ""
//                }
//            })
//        }
//        if(headerAccessor.sessionAttributes.httpSessionID){
//            User user = sessionTrackerService.getSessionById(headerAccessor.sessionAttributes.httpSessionID)?.user
//            if(obj?.c && user){
//                brokerMessagingTemplate.convertAndSend("/topic/chatMsg", JSON.toJSONString([u: [n: user.name, i: user.id], msg: obj?.c]))
//                return JSON.toJSONString([success: true])
//            }
//        }
//        return JSON.toJSONString([success: false])
//
//    }

//        @MessageMapping("hello")
//        @SendTo("/topic/hello")
//        protected String hello(SimpMessageHeaderAccessor headerAccessor, String text){
//            User user = null
//            if(headerAccessor.sessionAttributes.httpSessionID){
//                user = sessionTrackerService.getSessionById(headerAccessor.sessionAttributes.httpSessionID)?.user
//                return "${user?.name}: ${text}"
//            }else{
//                return null
//            }
//        }
}
