package com.hcservice.dao;

import com.hcservice.domain.model.User;

public interface UserMapper {

    int insertUser(User user);

    User getUserByUserId(Integer userId);

    User getUserByPhoneNum(String phoneNum);

    int updateUserById(User user);

}
