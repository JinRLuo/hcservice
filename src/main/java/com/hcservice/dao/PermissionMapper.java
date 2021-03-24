package com.hcservice.dao;

import com.hcservice.domain.model.Permission;

import java.util.List;

public interface PermissionMapper {

    List<Permission> findAll();

    Permission getPermissionById(Integer permissionId);

    List<Permission> getPermissionsByRoleId(Integer roleId);

}
