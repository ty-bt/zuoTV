package com.mtv.utils

import grails.util.Holders
import org.springframework.context.ApplicationContext
import org.springframework.util.Assert

/**
 *
 * 查找基础服务工具
 */
class BeanLookup {

    /**
     * 获取一个服务工具
     * @param clazz Class 指定基类
     * @param support 操作标识对象
     * @return 一个支持的bean定义 或者 null
     */
    public static Object support(Class clazz, Object support) {
        Assert.notNull(clazz, "clazz 不能为空")
        Assert.notNull(support, "support 不能为空")
        String[] beanNames = applicationContext.getBeanNamesForType(clazz)
        for (String it : beanNames) {
            SupportBeanDefine beanDefine = (SupportBeanDefine) applicationContext.getBean(it)
            if (beanDefine.support(support)) {
                return beanDefine
            }
        }
        return null
    }

    private static ApplicationContext getApplicationContext() {
        return Holders.getApplicationContext()
    }

}
