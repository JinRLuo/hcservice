package com.hcservice.domain.dataobject;

import lombok.Data;

import java.util.List;

@Data
public class AdminDO {

    private Integer adminId;
    private String adminName;
    private String email;
    private String phoneNumber;
    private String password;
    private String pictureURL;

    private List<RoleDO> roles;

}
