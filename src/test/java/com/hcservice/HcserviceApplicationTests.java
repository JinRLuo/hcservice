package com.hcservice;

import com.hcservice.dao.AdminMapper;
import com.hcservice.dao.BuildingMapper;
import com.hcservice.dao.PermissionMapper;
import com.hcservice.dao.RoomMapper;
import com.hcservice.domain.model.*;
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

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    BuildingMapper buildingMapper;

    @Autowired
    RoomMapper roomMapper;

    @Test
    void contextLoads() {
        Integer adminId = 1;
        String adminName = "test0001";
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        //Admin admin = adminMapper.selectByPrimaryKey(adminId);
        System.out.println(admin);
        if(admin.getRoles() != null) {
            for (Role r : admin.getRoles()) {
                if (r.getPermissions() != null) {
                    for(Permission p : r.getPermissions()) {
                        System.out.println(p);
                    }
                }
            }
        }
    }

    @Test
    void testPermission(){
        Integer permissionId = 1;
        Permission permission = permissionMapper.getPermissionById(permissionId);
        System.out.println(permission);
    }

    @Test
    void testBuildingMapper() {
        Integer buildId = 2;
        Building building = buildingMapper.getBuildingById(buildId);
        System.out.println(building);
    }

    @Test
    void testRoomMapper() {
        Integer roomId = 15;
        Room room = roomMapper.getRoomById(roomId);
        System.out.println(room);
    }


}
