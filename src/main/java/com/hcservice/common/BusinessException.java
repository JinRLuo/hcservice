package com.hcservice.common;

public class BusinessException extends Exception {

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode,String errMsg){
        super();
        this.errorCode = errorCode;
        this.errorCode.setErrMsg(errMsg);
    }

    public int getErrCode() {
        return this.errorCode.getErrCode();
    }

    public String getErrMsg() {
        return this.errorCode.getErrMsg();
    }

}
