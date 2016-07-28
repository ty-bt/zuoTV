package com.mtv.utils

/**
 * 基础服务提供接口
 */
interface SupportBeanDefine<T> {

    /**
     * 如果支持返回true 不支持返回false
     * @param object 判断依据
     * @return
     */
    public boolean support(T object)
}
