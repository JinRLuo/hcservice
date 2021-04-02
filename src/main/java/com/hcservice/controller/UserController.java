package com.hcservice.controller;

import com.hcservice.common.StringUtil;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.response.BaseResult;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


@RestController
@RequestMapping("/hc")
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/getOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    public BaseResult getOtp(@RequestParam(name = "phoneNum") String phoneNum) {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        return userService.getOtp(phoneNum);
    }

    @RequestMapping(value = "/user/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    public BaseResult register(@RequestParam(name = "phoneNum") String phoneNum,
                               @RequestParam(name = "otpCode") String otpCode,
                               @RequestParam(name = "userName") String userName,
                               @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        if(!userService.verityOtp(phoneNum, otpCode)) {//校验验证码
            return BaseResult.create(ErrorCode.OTP_CODE_ERROR,"fail");
        }
        if(!StringUtil.verifyPassword(password)) {
            return BaseResult.create(ErrorCode.PASSWORD_TOO_SIMPLE,"fail");
        }
        return userService.register(phoneNum, userName, StringUtil.EncodeByMd5(password));
    }

    @RequestMapping(value = "/user/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    public BaseResult login(@RequestParam(name = "phoneNum") String phoneNum,
                            @RequestParam(name = "otpCode") String otpCode) {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        if(!userService.verityOtp(phoneNum, otpCode)) {//校验验证码
            return BaseResult.create(ErrorCode.OTP_CODE_ERROR,"fail");
        }
        return userService.login(phoneNum);
    }



}
