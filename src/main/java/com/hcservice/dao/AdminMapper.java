package com.hcservice.dao;

import com.hcservice.domain.model.Admin;
import java.util.List;

public interface AdminMapper {

    int deleteByPrimaryKey(Integer adminId);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer adminId);

    Admin getAdminByName(String adminName);

    List<Admin> getAdminsByRoleId(Integer roleId);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);

    List<Admin> getAllAdmins();

    List<Admin> getAdminsBySearch(String searchAccount);
}
