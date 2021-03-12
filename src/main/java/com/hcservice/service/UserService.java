package com.hcservice.service;

import com.hcservice.domain.model.Admin;
import org.springframework.stereotype.Service;

public interface UserService {

    public Admin getAdminById(Integer adminId);

}
