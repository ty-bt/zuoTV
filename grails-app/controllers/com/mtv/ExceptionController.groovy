package com.mtv

import grails.converters.JSON
import grails.validation.ValidationException

import javax.security.auth.login.LoginException

class ExceptionController {


    def index() {
        try {
            Exception exception = (Exception) request?.exception // 判断是否是ajax请求
            Exception realEx = getRealException(exception)
            if (/*request.xhr &&*/ realEx) {
                response.status = 200
                render(convertExceptionToResponse(exception) as JSON)
            } else {
                render(view: '/error')
            }
        } catch (e) {
            e.printStackTrace()
        }
    }

    def notFound() {
        render(view: '404')
    }


    private Response convertExceptionToResponse(Exception ex) {
        ex = ex?.cause ? ex.cause : ex
        Response responseBody = Response.failure(null, "服务器正忙，请稍后再试")
        if (ex instanceof ValidationException) {
            ValidationException exception = (ValidationException) ex
            responseBody = Response.failure(exception.errors, "字段验证不通过，请检查")
        } else if(ex instanceof IllegalArgumentException){
            responseBody = Response.failure('', ex.message)
        }else if(ex instanceof LoginException){
            responseBody = Response.failure('login', ex.message)
        }else {
            responseBody = Response.failure(null, "服务器正忙，请稍后再试")
        }
        return responseBody
    }


    private Exception getRealException(Exception ex) {
        return ex?.cause ? ex.cause : ex
    }

}
