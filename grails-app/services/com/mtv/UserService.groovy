package com.mtv

import grails.transaction.Transactional
import org.springframework.util.Assert

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

@Transactional
class UserService {

    def loginLogService

    def userTokenService

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

        if(name.length() > 36 || password.length() >36){
            throw new IllegalArgumentException("用户名,密码也别太长了")
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
        return this.login(name, password, true)
    }

    /**
     * 登录
     * @param name 用户名或邮箱
     * @param password 密码
     * @param keep 是否保持登录
     * @return
     */
    public User login(String name, String password, Boolean keep = false){
        if(!name || !password){
            throw new IllegalArgumentException("用户名和密码不能为空")
        }

        User user = User.findByName(name)
        if(!user || user.password != EncryptUtils.pwd(password)){
            throw new IllegalArgumentException("用户名或密码错误")
        }
        return login(user, keep)
    }

    public User login(User user, Boolean keep = false){
        HttpServletRequest request = RequestUtils.getRequest()
        request.session.putAt("user", user)
        loginLogService.addLog(user)
        if(keep){
            UserToken userToken = userTokenService.add(user.id)
            RequestUtils.getResponse().addCookie(userToken.createCookie())
        }
        return user
    }

    public void logout(){
        HttpServletRequest request = RequestUtils.getRequest()
        request.session.invalidate()
        // 判断是否有保持登录cookie
        Cookie cookie = request.cookies.find{
            return it.name == UserToken.COOK_NAME
        }
        if(cookie){
            userTokenService.delete(cookie.value)
        }
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
        // 清除所有用户所有token
        userTokenService.delete(userId)
    }

    /**
     * 获取当前登录用户  尝试cookie登录
     * @return
     */
    public User getCurrentUser(){
        HttpServletRequest request = RequestUtils.getRequest()
        // 尝试cookie登录
        if(!request.session.getAttribute("user")){
            // 判断是否有保持登录cookie
            Cookie cookie = request.cookies.find{
                return it.name == UserToken.COOK_NAME
            }
            if(cookie){
                UserToken userToken = userTokenService.check(cookie.value)
                if(userToken){
                    // 登录
                    this.login(userToken.user)
                    // 刷新有效期
                    RequestUtils.getResponse().addCookie(userToken.createCookie())
                }else{
                    // 删除token cookie
                    Cookie delCookie = cookie.clone()
                    delCookie.setMaxAge(0)
                    RequestUtils.getResponse().addCookie(delCookie)
                }

            }
        }
        return request.session.getAttribute("user")
    }


}
