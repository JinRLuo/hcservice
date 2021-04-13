package com.hcservice.service;

import com.hcservice.common.BusinessException;
import com.hcservice.domain.model.Room;
import com.hcservice.domain.model.User;
import com.hcservice.domain.request.BindHomeOwnerInfoRequest;

import java.util.List;

public interface HomeOwnerService {

    int bindHomeOwnerInfo(BindHomeOwnerInfoRequest request, User user) throws BusinessException;

    List<Room> getRoomsByUserId(Integer userId);

}
