package com.hcservice.controller;

import com.hcservice.domain.response.BaseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
public class UserController extends BaseController {

    @RequestMapping(value = "/doLogin",method = {RequestMethod.POST, RequestMethod.GET})
    public BaseResult login(String account, String password) {
        BaseResult result = new BaseResult();
        return result;
    }




}
