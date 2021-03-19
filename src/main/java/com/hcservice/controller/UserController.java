package com.hcservice.controller;

import com.hcservice.domain.response.BaseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
public class UserController extends BaseController {

    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    public BaseResult login() {
        BaseResult result = new BaseResult();
        return result;
    }




}
