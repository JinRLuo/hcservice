package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.SmsUtil;
import com.hcservice.dao.AdminMapper;
import com.hcservice.dao.HomeOwnerMapper;
import com.hcservice.dao.RoleMapper;
import com.hcservice.dao.UserMapper;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.HomeOwner;
import com.hcservice.domain.model.Role;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.response.HomeOwnerInfoResponse;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.UserInfoResponse;
import com.hcservice.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private HomeOwnerMapper homeOwnerMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Admin admin = adminMapper.getAdminByName(s);
        if (admin == null) {
            throw new UsernameNotFoundException("???????????????");
        } else {
            return admin;
        }
    }

    @Override
    @Transactional
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
    public int modifyAdminPassword(Admin admin, String oldPassword, String newPassword) throws BusinessException {
        admin = adminMapper.getAdminByName(admin.getAdminName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        return adminMapper.updateByPrimaryKey(admin);
    }

    @Override
    public BaseResult getOtp(String phoneNum) {
        //?????????????????????????????????OTP?????????
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(phoneNum);
        if (expire > 240) {
            return BaseResult.create(ErrorCode.GET_OTP_CODE_FREQUENTLY, "fail");
        }
        Random random = new Random();
        int randomInt = random.nextInt(899999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);
        //???OTP??????????????????????????????????????????
        redisTemplate.opsForValue().set(phoneNum, otpCode, 5, TimeUnit.MINUTES);
        smsUtil.sendCode(phoneNum, "+86", otpCode, 933781);
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
        //?????????????????????
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
        if(user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        return user;
    }

    @Override
    public User loginByPwd(String phoneNum, String password) throws BusinessException {
        User user = userMapper.getUserByPhoneNum(phoneNum);
        if (user == null || !user.getPassword().equals(password)) {
            throw new BusinessException(ErrorCode.PHONE_LOGIN_FAIL);
        }
        if(user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
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

    @Override
    public PageInfo<Admin> getAdminListByPage(String searchAccount, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> admins = adminMapper.getAdminsBySearch(searchAccount);
        PageInfo<Admin> pageInfo = new PageInfo<>(admins);
        return pageInfo;
    }

    @Override
    public ListByPageResponse<UserInfoResponse> getUserInfoByPage(String searchPhoneNum, int pageNum, int pageSize) {
        ListByPageResponse<UserInfoResponse> response = new ListByPageResponse<>();
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.getUsersBySearch(searchPhoneNum);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        List<User> usersByPage = pageInfo.getList();
        List<UserInfoResponse> userInfos = usersByPage.stream().map(user -> {
            UserInfoResponse userInfo = new UserInfoResponse();
            BeanUtils.copyProperties(user, userInfo);
            List<HomeOwner> owners = homeOwnerMapper.getHomeOwnersByUserId(user.getUserId());
            List<HomeOwnerInfoResponse> homeOwnerInfoResponses = owners.stream().map(homeOwner -> {
                HomeOwnerInfoResponse homeOwnerInfoResponse = new HomeOwnerInfoResponse();
                BeanUtils.copyProperties(homeOwner, homeOwnerInfoResponse);
                BeanUtils.copyProperties(homeOwner.getRoom(), homeOwnerInfoResponse);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                homeOwnerInfoResponse.setCreateTime(dtf.format(homeOwner.getCreateTime()));
                return homeOwnerInfoResponse;
            }).collect(Collectors.toList());
            userInfo.setHomeOwners(homeOwnerInfoResponses);
            return userInfo;
        }).collect(Collectors.toList());
        response.setList(userInfos);
        return response;
    }

    @Override
    @Transactional
    public List<Role> modifyAdminRole(Integer adminId, Integer[] roleIds) {
        roleMapper.deleteAdminRoleRelationByAdminId(adminId);
        if (roleIds != null && roleIds.length != 0) {
            Arrays.stream(roleIds).forEach(roleId -> {
                roleMapper.addAdminRoleRelation(adminId, roleId);
            });
        }
        List<Role> roles = roleMapper.getRolesByAdminId(adminId);
        return roles;
    }

    @Override
    @Transactional
    public int modifyAccountStatus(Integer adminId, boolean status) {
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        if(admin == null) {
            return 0;
        }
        admin.setStatus(status);
        return adminMapper.updateByPrimaryKey(admin);
    }

    @Override
    @Transactional
    public int modifyUserStatus(Integer userId, Integer status) {
        User user = userMapper.getUserByUserId(userId);
        if(user == null) {
            return 0;
        }
        user.setStatus(status);
        return userMapper.updateUserById(user);
    }
}
