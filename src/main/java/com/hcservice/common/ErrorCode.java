package com.hcservice.common;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    //通用错误类型1000
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),
    PHONE_FORMAT_ERROR(10003,"手机号格式错误"),
    PERMISSION_DENIED(10004, "权限不足"),


    //20000开头为用户信息相关错误定义
    ACCOUNT_NOT_EXIST(20001,"用户不存在"),
    ACCOUNT_LOGIN_FAIL(20002,"手机号或密码不正确"),
    ACCOUNT_NOT_LOGIN(20003,"用户还未登录"),
    ACCOUNT_DISABLED(20004,"账户被禁用，请联系管理员!"),
    ACCOUNT_EXPIRED(20005,"账号过期，请联系管理员!"),
    ACCOUNT_NAME_EXISTS(20006,"该用户名已被注册"),
    PASSWORD_TOO_SIMPLE(20007,"密码强度太低"),
    OTP_CODE_ERROR(20008,"验证码错误"),
    GET_OTP_CODE_FREQUENTLY(20009,"获取验证码太频繁"),
    PHONE_NUMBER_UNREGISTERED(20010,"该手机号未注册"),
    PHONE_NUMBER_HAS_BEEN_REGISTERED(20011,"该手机号已经被注册过了"),
    IMAGE_CAN_NOT_BE_NULL(20012, "图片不能为空"),
    IMAGE_FORMAT_ERROR(20013, "图片格式错误"),
    IMAGE_UPDATE_ERROR(20014, "图片上传发生错误"),



    //30000开头为功能服务错误定义
    ROOM_NUM_NOT_EXIST(30001,"该房间号不存在！"),
    ORDER_INFO_NOT_EXIST(30002, "订单信息不存在！"),
    ORDER_INFO_ALREADY_PAID(30003, "订单已经完成！"),
    ORDER_NOT_ACTIONABLE(30004,"订单不可操作！"),
    SERVICE_COST_ALREADY_PAID(30005,"该月物业费已经缴清！")
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
