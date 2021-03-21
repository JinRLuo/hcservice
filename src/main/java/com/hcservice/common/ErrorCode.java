package com.hcservice.common;

public enum ErrorCode {

    //通用错误类型1000
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),

    //20000开头为用户信息相关错误定义
    ACCOUNT_NOT_EXIST(20001,"用户不存在"),
    ACCOUNT_LOGIN_FAIL(20002,"用户手机号或密码不正确"),
    ACCOUNT_NOT_LOGIN(20003,"用户还未登录"),
    ACCOUNT_DISABLED(20004,"账户被禁用，请联系管理员!"),
    ACCOUNT_EXPIRED(20005,"账号过期，请联系管理员!"),



    //30000开头为交易信息错误定义
    STOCK_NOT_ENOUGH(30001,"库存不足")
    ;

    private int errCode;
    private String errMsg;

    ErrorCode(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
