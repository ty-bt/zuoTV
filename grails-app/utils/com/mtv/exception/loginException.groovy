package com.mtv.exception

/**
 * Created by 谭勇 on 2016/10/25 0025.
 */
class loginException extends Exception {
    public loginException(String msg = '您还没有登录,请登录'){
        super(msg)
    }
}
