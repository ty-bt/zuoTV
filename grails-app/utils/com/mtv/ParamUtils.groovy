package com.mtv
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
/**
 * 获取参数简化类，简化一些方法
 *
 */
class ParamUtils {

    /**
     * 获取分页参数
     *
     * @return
     */
    public static Map limit() {
        return limit(params)

    }
    /**
     * 获取分页参数
     *
     * @return
     */
    public static Map limit(def params) {
        Integer max = params.int('max', 120)

        Integer offset = params.int('offset', 0)

        if(params.page){
            Integer page = params.int('page', 1)
            offset = (page - 1) * max
        }
        return [offset: offset, max: max]
    }

    public static GrailsParameterMap getParams() {
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes();
        return webRequest.getParams();
    }

}
