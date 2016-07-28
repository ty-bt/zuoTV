package com.mtv

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TestController {

    def roomService

    def index() {
        println "进入"
        Document document = Jsoup.parse(new URL("http://www.douyu.com/directory/all?page=2&isAjax=0"), 60000)
        println "总页数:" + document.select(".classify_li a").get(0).attr("data-pagecount")
//        Elements eles = document.select("li")
////        http://staticlive.douyutv.com/common/share/play.swf?room_id=14163
//        eles.each {
//            Element ele = it
//            render "url: " + ele.select("a").attr("href") + "title: " + ele.select("h3.ellipsis").text() + "<br/>"
//        }
    }

    def start(){
        roomService.loadRoom(Platform.findByFlag("douyu"))
        render "success"
    }

    def start1(){
        roomService.loadRoom(Platform.findByFlag("panda"))
        render "success"
    }

    def loadAll(){
        render(new Date())
        roomService.loadAll()
        render(new Date())
        render "success"
    }
}
