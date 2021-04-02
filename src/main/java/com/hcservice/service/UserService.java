package com.hcservice.service;

import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.BaseResult;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public interface UserService extends UserDetailsService {

    public Admin getAdminById(Integer adminId);

    public BaseResult adminRegister(Admin admin);

    public BaseResult getOtp(String phoneNum);

    public Boolean verityOtp(String phoneNum, String userOtpCode);

    public BaseResult register(String phoneNum, String userName, String password);

    public BaseResult login(String phoneNum);

    public User getUserByUserId(Integer userId);

}
