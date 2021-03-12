package com.hcservice;

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

    @Test
    void contextLoads() {
        Integer adminId = 1;
        Admin admin = userService.getAdminById(adminId);
        System.out.println(admin.toString());
//        List<Role> list = admin.getRoles();
//        for(Role r : list) {
//            System.out.println(r.getRoleName());
//        }


    }

}
