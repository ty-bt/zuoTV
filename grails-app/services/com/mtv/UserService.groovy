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
        if(!name || !password){
            throw new IllegalArgumentException("用户名和密码不能为空")
        }

        if(User.findByName(name)){
            throw new IllegalArgumentException("用户名已存在")
        }
        if(email && User.findByEmail(email)){
            throw new IllegalArgumentException("邮箱已被使用")
        }

        User user = new User()
        user.name = name
        user.password = EncryptUtils.pwd(password)
        user.email = email
        user.save()
        return this.login(name, password)
    }

    /**
     * 登录
     * @param name 用户名或邮箱
     * @param password 密码
     * @return
     */
    public User login(String name, String password){
        if(!name || !password){
            throw new IllegalArgumentException("用户名和密码不能为空")
        }

        User user = User.findByName(name)
        if(!user || user.password != EncryptUtils.pwd(password)){
            throw new IllegalArgumentException("用户名或密码错误")
        }
        HttpServletRequest request = RequestUtils.getRequest()
        request.session.putAt("user", user)
        loginLogService.addLog(user)
        return user
    }

    public void logout(){
        HttpServletRequest request = RequestUtils.getRequest()
        request.session.invalidate()
    }


    /**
     * 修改密码
     * @param userId
     * @param newPwd
     * @param oldPwd 老密码不传或为null则不校验老密码是否正确
     */
    public void updatePwd(Long userId, String newPwd, String oldPwd = null){
        Assert.notNull(userId, "用户ID不能为空")
        if(!newPwd){
            throw new IllegalArgumentException("新密码不能为空")
        }
        User user = User.get(userId)
        if(oldPwd && user.password != EncryptUtils.pwd(oldPwd)){
            throw new IllegalArgumentException("密码错误")
        }
        user.password = EncryptUtils.pwd(newPwd)
        user.save()
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public User getCurrentUser(){
        HttpServletRequest request = RequestUtils.getRequest()
        return request.session.getAttribute("user")
    }


}
