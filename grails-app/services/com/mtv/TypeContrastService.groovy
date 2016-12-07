package com.mtv

import com.alibaba.fastjson.JSON
import grails.transaction.Transactional

@Transactional
class TypeContrastService {

    // 对照表Map  key:其他平台分类 value: 统一分类名称
    private static Map contrastMap = [:]

    /**
     * 重新刷新对照表
     * @return
     */
    public Map reload(){
        Map result = [:]
        List typeContrastAll = TypeContrast.findAll()
        typeContrastAll.each {
            TypeContrast contrast = it
            List<String> list = JSON.parse(contrast.typesText)
            list.each {
                result.put(it, contrast.name)
            }
        }
        this.contrastMap = result
    }

    /**
     * 获取统一类型名称
     * @param oldTypeName
     * @return
     */
    public String getTypeName(String oldTypeName){
        if(contrastMap.containsKey(oldTypeName)){
            return contrastMap[oldTypeName]
        }
        return oldTypeName
    }
}
