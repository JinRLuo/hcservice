package com.hcservice.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.domain.model.Admin;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.model.Role;
import com.hcservice.domain.response.AdminInfoResponse;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.UserInfoResponse;
import com.hcservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    UserService userService;


    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public BaseResult login(String account, String password) {
        BaseResult result = new BaseResult();
        return result;
    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public BaseResult register(String account, String email, String phoneNum) {
        if(StringUtils.isAnyEmpty(account, email, phoneNum)) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Admin admin = new Admin();
        admin.setAdminName(account);
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNum);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setPictureUrl("/img/1.img");
        admin.setStatus(true);
        return userService.adminRegister(admin);
    }

    @RequestMapping(value = "/modifyPassword", method = {RequestMethod.POST})
    public BaseResult modifyPassword(String oldPassword, String newPassword) throws BusinessException {
        if(StringUtils.isAnyEmpty(oldPassword, newPassword)) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int res = userService.modifyAdminPassword(admin, oldPassword, newPassword);
        if(res<1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/getAdminInfoList", method = {RequestMethod.POST})
    public BaseResult<ListByPageResponse<AdminInfoResponse>> getAdminInfoList(String searchAccount, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        if (searchAccount == null) {
            searchAccount = "";
        }
        searchAccount = "%" + searchAccount + "%";
        ListByPageResponse<AdminInfoResponse> response = new ListByPageResponse<>();
        PageInfo<Admin> pageInfos = userService.getAdminListByPage(searchAccount, pageNum, pageSize);
        response.setPageNum(pageInfos.getPageNum());
        response.setPageSize(pageInfos.getPageSize());
        response.setTotal(pageInfos.getTotal());
        List<AdminInfoResponse> admins = pageInfos.getList().stream().map(admin -> {
            AdminInfoResponse adminInfo = new AdminInfoResponse();
            adminInfo.setAdminId(admin.getAdminId());
            adminInfo.setAdminName(admin.getAdminName());
            adminInfo.setEmail(admin.getEmail());
            adminInfo.setPhoneNum(admin.getPhoneNumber());
            adminInfo.setStatus(admin.isStatus());
            List<Role> roles = admin.getRoles();
            roles.stream().forEach(role -> {
                role.setPermissions(new ArrayList<>(0));
                role.setAdmins(new ArrayList<>(0));
            });
            adminInfo.setRoles(roles);
            return adminInfo;
        }).collect(Collectors.toList());
        response.setList(admins);
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/getUserInfoList", method = {RequestMethod.POST})
    public BaseResult<ListByPageResponse<UserInfoResponse>> getUserInfoList(String searchPhoneNum, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        if (searchPhoneNum == null) {
            searchPhoneNum = "";
        }
        searchPhoneNum = "%" + searchPhoneNum + "%";
        ListByPageResponse<UserInfoResponse> response = userService.getUserInfoByPage(searchPhoneNum, pageNum, pageSize);
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/modifyAdminRole", method = {RequestMethod.POST})
    public BaseResult<List<Role>> modifyAdminRole(Integer adminId, Integer[] roleIds) {
        if (adminId == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        List<Role> roles = userService.modifyAdminRole(adminId, roleIds);
        roles.stream().forEach(role -> {
            role.setPermissions(null);
            role.setAdmins(null);
        });
        return BaseResult.create(roles);
    }

    @RequestMapping(value = "/disableAdminAccount", method = {RequestMethod.POST})
    public BaseResult disableAdminAccount(Integer adminId) {
        if (adminId == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        int res = userService.modifyAccountStatus(adminId, false);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/freeAdminAccount", method = {RequestMethod.POST})
    public BaseResult freeAdminAccount(Integer adminId) {
        if (adminId == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        int res = userService.modifyAccountStatus(adminId, true);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/disableUserAccount", method = {RequestMethod.POST})
    public BaseResult disableUserAccount(Integer userId) {
        if (userId == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        int res = userService.modifyUserStatus(userId, 0);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/freeUserAccount", method = {RequestMethod.POST})
    public BaseResult freeUserAccount(Integer userId) {
        if (userId == null) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        int res = userService.modifyUserStatus(userId, 1);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }


}
