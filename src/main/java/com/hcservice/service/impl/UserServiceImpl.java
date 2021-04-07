package com.hcservice.service.impl;

import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.AdminMapper;
import com.hcservice.dao.UserMapper;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Admin admin = adminMapper.getAdminByName(s);
        if (admin == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else {
            return admin;
        }

    }

    @Override
    public BaseResult adminRegister(Admin admin) {
        Admin am = adminMapper.getAdminByName(admin.getAdminName());
        if (am != null) {
            return BaseResult.create(ErrorCode.ACCOUNT_NAME_EXISTS, "fail");
        }
        int res = adminMapper.insert(admin);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(admin.getAdminName());
    }

    @Override
    public BaseResult getOtp(String phoneNum) {
        //需要按照一定的规则生成OTP验证码
        String otpCode = (String) redisTemplate.opsForValue().get(phoneNum);
        if (otpCode != null) {
            return BaseResult.create(ErrorCode.GET_OTP_CODE_FREQUENTLY, "fail");
        }
        Random random = new Random();
        int randomInt = random.nextInt(899999);
        randomInt += 100000;
        otpCode = String.valueOf(randomInt);
        //将OTP验证码同对应用户的手机号关联
        redisTemplate.opsForValue().set(phoneNum, otpCode, 60, TimeUnit.SECONDS);
        System.out.println("otpCode:" + phoneNum + " & " + otpCode);
        return BaseResult.create(phoneNum);
    }

    @Override
    public Boolean verityOtp(String phoneNum, String userOtpCode) {
        String otpCode = (String) redisTemplate.opsForValue().get(phoneNum);
        if (userOtpCode == null || !userOtpCode.equals(otpCode)) {
            return false;
        }
        redisTemplate.delete(phoneNum);
        return true;
    }

    @Override
    public BaseResult register(String phoneNum, String password) {
        User oldUser = userMapper.getUserByPhoneNum(phoneNum);
        if (oldUser != null) {
            return BaseResult.create(ErrorCode.PHONE_NUMBER_HAS_BEEN_REGISTERED, "fail");
        }
        User user = new User();
        //随机生成用户名
        user.setUserName("hc_" + RandomStringUtils.randomAlphabetic(8));
        user.setPhoneNum(phoneNum);
        user.setPassword(password);
        user.setPictureUrl("/img/1.jpg");
        user.setStatus(1);
        int res = userMapper.insertUser(user);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @Override
    public User loginByPhoneNum(String phoneNum) throws BusinessException {
        User user = userMapper.getUserByPhoneNum(phoneNum);
        if (user == null) {
            throw new BusinessException(ErrorCode.PHONE_NUMBER_UNREGISTERED);
        }
        return user;
//        String token = JwtUtil.getToken(user);
//        UserVO userVO = convertUserVOFromUserModel(user, token);
    }

    @Override
    public User loginByPwd(String phoneNum, String password) throws BusinessException {
        User user = userMapper.getUserByPhoneNum(phoneNum);
        if (user == null || user.getPassword().equals(password)) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOGIN_FAIL);
        }
        return user;
    }

    @Override
    public User getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUserById(user);
    }
}
