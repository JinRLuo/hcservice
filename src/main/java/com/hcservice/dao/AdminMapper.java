package com.hcservice.dao;

import com.hcservice.domain.model.Admin;

public interface AdminMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    int deleteByPrimaryKey(Integer adminId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    int insert(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    int insertSelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    Admin selectByPrimaryKey(Integer adminId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    int updateByPrimaryKeySelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN_INFO
     *
     * @mbg.generated Fri Mar 12 22:50:33 GMT+08:00 2021
     */
    int updateByPrimaryKey(Admin record);
}