package com.mtv

import grails.transaction.Transactional
import org.apache.commons.lang.math.RandomUtils
import org.springframework.util.Assert

@Transactional
class UserTokenService {

    public UserToken add(Long userId){
        Assert.notNull(userId, "用户ID不能为空")
        User user = User.get(userId)
        Assert.notNull(user, "用户不存在")

        UserToken userToken = new UserToken()
        userToken.user = user
        userToken.token = getToken(user)
        userToken.validDate = DateUtils.amount(new Date(), Calendar.MONTH, 1)
        userToken.save()
    }

    public String getToken(User user){
        return (user.id + "-_" + System.currentTimeMillis() + "-" + RandomUtils.nextLong() + EncryptUtils.SALT_PWD).encodeAsMD5()
    }

    /**
     * 校验token
     * @param token
     * @return 返回null失败
     */
    public UserToken check(String token){
        UserToken userToken = UserToken.findByToken(token)
        if(!userToken){
            return null
        }
        // 过期删除
        if(userToken.validDate < new Date()){
            userToken.delete()
            return null
        }
        // 增加一个月有效期
        userToken.validDate = DateUtils.amount(new Date(), Calendar.MONTH, 1)
        return userToken
    }

    /**
     * 删除用户下面所有token
     * @param userId
     */
    public void delete(Long userId){
        Assert.notNull(userId, "用户ID不能为空")
        User user = User.get(userId)
        Assert.notNull(user, "用户不存在")
        UserToken.executeUpdate("delete UserToken t where t.user = ?", [user])
    }

    /**
     * 删除指定token
     * @param userId
     */
    public void delete(String token){
        UserToken.executeUpdate("delete UserToken t where t.token = ?", [token])
    }
}
