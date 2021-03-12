package com.hcservice.domain.dataobject;

import lombok.Data;

import java.util.List;

@Data
public class RoleDO {

    private Integer roleId;
    private String roleName;

    private List<AdminDO> admins;

}
