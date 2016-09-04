package com.mtv

import grails.transaction.Transactional
import org.springframework.util.Assert

import javax.servlet.http.HttpServletRequest

@Transactional
class UserService {

    def loginLogService

    /**
     * 注册
     * @param name 用户名
     * @param password 密码
     * @param email 邮箱
     */
    public User register(String name, String password, String email = null){
        Assert.notEmpty(name, "用户名不能为空")

        if(User.findByName(name)){
            throw new Exception("用户名已存在")
        }
        if(email && User.findByEmail(email)){
            throw new Exception("邮箱已被使用")
        }

        User user = new User()
        user.name = name
        user.password = EncryptUtils.pwd(password)
        user.email = email
        return user.save()
    }

    /**
     * 登录
     * @param name 用户名或邮箱
     * @param password 密码
     * @return
     */
    public User login(String name, String password){
        Assert.notEmpty(name, "用户名不能为空")
        User user = User.findByNameOrEmail(name)
        if(!user || user.password != EncryptUtils.pwd(password)){
            throw new Exception("用户名或密码错误")
        }
        HttpServletRequest request = RequestUtils.getRequest()
        request.session.putAt("user", user)
        loginLogService.addLog(user)
        return user
    }


}
