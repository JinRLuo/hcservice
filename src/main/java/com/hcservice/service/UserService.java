package com.hcservice.service;

import com.hcservice.domain.model.Admin;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public interface UserService extends UserDetailsService {

    public Admin getAdminById(Integer adminId);

}
