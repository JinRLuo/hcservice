package com.hcservice.service.impl;

import com.hcservice.dao.AdminMapper;
import com.hcservice.domain.model.Admin;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }
}
