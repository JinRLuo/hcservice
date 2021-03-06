package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.common.BusinessException;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.Role;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Admin getAdminById(Integer adminId);

    BaseResult adminRegister(Admin admin);

    int modifyAdminPassword(Admin admin, String oldPassword, String newPassword) throws BusinessException;

    BaseResult getOtp(String phoneNum);

    Boolean verityOtp(String phoneNum, String userOtpCode);

    BaseResult register(String phoneNum, String password);

    User loginByPhoneNum(String phoneNum) throws BusinessException;

    User loginByPwd(String phoneNum, String password) throws BusinessException;

    User getUserByUserId(Integer userId);

    int updateUser(User user);

    PageInfo<Admin> getAdminListByPage(String searchAccount, int pageNum, int pageSize);

    ListByPageResponse<UserInfoResponse> getUserInfoByPage(String searchUserName, int pageNum, int pageSize);

    List<Role> modifyAdminRole(Integer adminId, Integer[] roleIds);

    int modifyAccountStatus(Integer adminId, boolean status);

    int modifyUserStatus(Integer userId, Integer status);

}
