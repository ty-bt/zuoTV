package com.mtv

import grails.transaction.Transactional
import org.springframework.util.Assert
import org.springframework.web.context.request.RequestScope

import javax.servlet.http.HttpServletRequest

@Transactional
class LoginLogService {

    /**
     * 保存登陆日志
     * @param user
     * @return
     */
    public LoginLog addLog(User user){
        Assert.notNull(user, "用户不能为空")
        LoginLog loginLog = new LoginLog()
        loginLog.user = user
        loginLog.ip = RequestUtils.getRealIp()
        return loginLog.save()
    }
}
