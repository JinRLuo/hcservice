package com.hcservice.service.impl;

import com.hcservice.dao.PermissionMapper;
import com.hcservice.domain.model.Permission;
import com.hcservice.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return permissionMapper.findAll();
    }
}
