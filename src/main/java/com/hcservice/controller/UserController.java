package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.*;
import com.hcservice.common.utils.DateUtil;
import com.hcservice.common.utils.JwtUtil;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.domain.VO.UserVO;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.service.UserService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/hc")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Value("${img.location}")
    private String systemImagePath;

    @RequestMapping(value = "/user/getOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult getOtp(@RequestParam(name = "phoneNum") String phoneNum) {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        return userService.getOtp(phoneNum);
    }

    @RequestMapping(value = "/user/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult register(@RequestParam(name = "phoneNum") String phoneNum,
                               @RequestParam(name = "otpCode") String otpCode,
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
        return userService.register(phoneNum, StringUtil.EncodeByMd5(password));
    }

    @RequestMapping(value = "/user/loginByOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> loginByOtp(@RequestParam(name = "phoneNum") String phoneNum,
                                         @RequestParam(name = "otpCode") String otpCode) throws BusinessException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        if(!userService.verityOtp(phoneNum, otpCode)) {//校验验证码
            return BaseResult.create(ErrorCode.OTP_CODE_ERROR,"fail");
        }
        User user = userService.loginByPhoneNum(phoneNum);
        String token = JwtUtil.getToken(user);
        return BaseResult.create(token);
    }

    @RequestMapping(value = "/user/loginByPwd",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> loginByPwd(@RequestParam(name = "phoneNum") String phoneNum,
                            @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        User user = userService.loginByPwd(phoneNum, StringUtil.EncodeByMd5(password));
        String token = JwtUtil.getToken(user);
        return BaseResult.create(token);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getUserInfo",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> getUserInfo() {
        User user = AuthenticationInterceptor.getLoginUser();
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateUserName",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> updateUserName(@RequestParam("userName") String userName) {
        User user = AuthenticationInterceptor.getLoginUser();
        user.setUserName(userName);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateEmail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> updateEmail(@RequestParam("email") String email) {
        User user = AuthenticationInterceptor.getLoginUser();
        user.setEmail(email);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateHeadImage",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FROM_DATA})
    public BaseResult<UserVO> updateImage(@RequestParam("image") MultipartFile multipartfile)  {
        User user = AuthenticationInterceptor.getLoginUser();
        // 源文件名称
        final String originalFileName = multipartfile.getOriginalFilename();
        if (StringUtils.isBlank(originalFileName)) {
            return BaseResult.create(ErrorCode.IMAGE_CAN_NOT_BE_NULL, "fail");
        }

        List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");
        final String suffix = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!IMAGE_EXTENSIONS.contains(suffix)) {
            return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
        }

        String lastFilePath;
        String newFileName = StringUtil.getUUID() + suffix;
        String folderName = File.separator + "headImage" + File.separator;
        String relativePath = folderName + DateUtil.getYYYYMMDD() + File.separator + DateUtil.getHH();
        String filePath = systemImagePath + relativePath;
        String fileUrl = null;
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = null;
        try {
            lastFilePath = filePath + File.separator + newFileName;
            out = new FileOutputStream(lastFilePath);
            out.write(multipartfile.getBytes());
            fileUrl = relativePath + File.separator + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fileUrl == null) {
            return BaseResult.create(ErrorCode.IMAGE_UPDATE_ERROR, "fail");
        }
        user.setPictureUrl(fileUrl);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    private UserVO convertUserVOFromUserModel(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}
