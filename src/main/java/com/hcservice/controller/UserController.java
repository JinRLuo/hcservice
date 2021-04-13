package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.*;
import com.hcservice.common.utils.DateUtil;
import com.hcservice.common.utils.JwtUtil;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.common.utils.TencentCOS;
import com.hcservice.domain.VO.UserVO;
import com.hcservice.domain.model.Complaint;
import com.hcservice.domain.model.Notice;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.request.ComplaintRequest;
import com.hcservice.domain.response.NoticeResponse;
import com.hcservice.service.ComplaintService;
import com.hcservice.service.NoticeService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/hc")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private TencentCOS tencentCOS;

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

        //获取文件的名称
        String fileName = multipartfile.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return BaseResult.create(ErrorCode.IMAGE_CAN_NOT_BE_NULL, "fail");
        }
        //判断有无后缀
        if (fileName.lastIndexOf(".") < 0) {
            return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
        }

        //获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));

        //如果不是图片
        if (!prefix.equalsIgnoreCase(".jpg") && !prefix.equalsIgnoreCase(".jpeg") && !prefix.equalsIgnoreCase(".svg") && !prefix.equalsIgnoreCase(".gif") && !prefix.equalsIgnoreCase(".png")) {
            return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
        }
        final File excelFile;
        try {
            excelFile = File.createTempFile("imagesFile-" + System.currentTimeMillis(), prefix);
            //将Multifile转换成File
            multipartfile.transferTo(excelFile);
        } catch (IOException e) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }

        //调用腾讯云工具上传文件
        String imageName = tencentCOS.uploadFile(excelFile, "headImage");

        //程序结束时，删除临时文件
        tencentCOS.deleteFile(excelFile);

        if (imageName == null) {
            return BaseResult.create(ErrorCode.IMAGE_UPDATE_ERROR, "fail");
        }
        user.setPictureUrl(imageName);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }


    /**
     * 用户查看小区公告接口
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/user/getNotice",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<NoticeResponse> getNotice() {
        List<Notice> notices = noticeService.getAllNotice();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<NoticeResponse> responses = notices.stream().map(notice -> {
            NoticeResponse response = new NoticeResponse();
            BeanUtils.copyProperties(notice, response);
            response.setAdminName(notice.getAdmin().getAdminName());
            response.setCreateDate(dtf.format(notice.getCreateDate()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    /**
     * 用户服务投诉接口
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/user/complain",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult complain(ComplaintRequest request) {
        User user = AuthenticationInterceptor.getLoginUser();

        if(StringUtils.isEmpty(request.getType()) || StringUtils.isEmpty(request.getContent())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }

        Complaint complaint = new Complaint();
        BeanUtils.copyProperties(request, complaint);
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUser(user);
        int res = complaintService.addComplaint(complaint);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        return BaseResult.create(null);
    }

    private UserVO convertUserVOFromUserModel(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}
