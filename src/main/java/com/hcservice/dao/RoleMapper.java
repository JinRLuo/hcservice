package com.hcservice.dao;

import com.hcservice.domain.model.Permission;
import com.hcservice.domain.model.Role;

import java.util.List;

public interface RoleMapper {

    int deleteByPrimaryKey(Integer roleId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer roleId);

    List<Role> getRolesByAdminId(Integer adminId);

    List<Permission> getRolesByPermissionId(Integer permissionId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    int deleteAdminRoleRelationByAdminId(Integer adminId);

    int addAdminRoleRelation(Integer adminId, Integer roleId);
}
