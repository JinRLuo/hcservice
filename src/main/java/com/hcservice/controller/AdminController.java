package com.hcservice.controller;

import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.response.BaseResult;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class AdminController extends BaseController {

    @Autowired
    UserService userService;


    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public BaseResult login(String account, String password) {
        BaseResult result = new BaseResult();
        return result;
    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public BaseResult register(String account, String email, String phoneNumber, String password) {
        Admin admin = new Admin();
        admin.setAdminName(account);
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNumber);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        admin.setPassword(passwordEncoder.encode(password));
        admin.setPictureUrl("/img/1.img");
        admin.setStatus(true);
        return userService.adminRegister(admin);
    }




}
