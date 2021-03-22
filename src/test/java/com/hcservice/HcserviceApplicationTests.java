package com.hcservice;

import com.hcservice.dao.AdminMapper;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.Role;
import com.hcservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HcserviceApplicationTests {

    @Autowired
    UserService userService;

    @Autowired
    AdminMapper adminMapper;

    @Test
    void contextLoads() {
        Integer adminId = 1;
        String adminName = "test0001";
        Admin admin = adminMapper.getAdminByName(adminName);
        //Admin admin = adminMapper.selectByPrimaryKey(adminId);
        System.out.println(admin);


    }

}
