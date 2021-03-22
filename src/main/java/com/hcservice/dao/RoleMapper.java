package com.hcservice.dao;

import com.hcservice.domain.model.Role;

import java.util.List;

public interface RoleMapper {

    int deleteByPrimaryKey(Integer roleId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer roleId);

    List<Role> getRolesByAdminId(Integer adminId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
}