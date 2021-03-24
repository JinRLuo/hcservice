package com.hcservice.service.impl;

import com.hcservice.common.ErrorCode;
import com.hcservice.dao.AdminMapper;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.response.BaseResult;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Admin admin = adminMapper.getAdminByName(s);
        if(admin == null) {
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
}
