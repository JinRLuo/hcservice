package com.hcservice.controller;

import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.BaseResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public static final String CONTENT_TYPE_URLENCODED ="application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_FROM_DATA = "multipart/form-data";
    public static final String CONTENT_TYPE_JSON = "application/json";

    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException)ex;
            responseData.put("errCode",businessException.getErrCode());
            responseData.put("errMsg",businessException.getErrMsg());
        }else{
            responseData.put("errCode", ErrorCode.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",ErrorCode.UNKNOWN_ERROR.getErrMsg());
        }

        return BaseResult.create(responseData,"fail");
    }

}
