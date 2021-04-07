package com.hcservice.common.utils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class StringUtil {

    public static boolean verifyPhoneNum(String phoneNum) {
        return phoneNum.matches("^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$");
    }

    public static boolean verifyPassword(String password) {
        if(password == null || password.length() < 8) {
            return false;
        }
        return true;
    }

    public static String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newStr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }

}
