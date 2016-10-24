package com.mtv

import grails.converters.JSON
import grails.converters.XML
import org.springframework.validation.Errors

/**
 * 返回的消息和数据
 *
 */
class Response {

    public boolean success = true

    public String message

    public Errors errors

    public Object data

    public String link

    public static Response success(Object data = null, String message = null) {
        return new Response(success: true, data: data, message: message)
    }

    public static Response message(boolean success = true, String message) {
        return new Response(success: true, data: null, message: message)
    }

    public static Response failure(Errors errors, String message = null) {
        return new Response(success: false, errors: errors, message: message)
    }

    public static Response failure(String link, String message) {
        return new Response(success: false, message: message, link: link)
    }

    public def toJSON() {
        return this as JSON
    }

    public def toXML() {
        return this as XML
    }
}
