package com.hcservice.controller;


import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.RoomMapper;
import com.hcservice.domain.model.Room;
import com.hcservice.domain.model.User;
import com.hcservice.domain.request.BindHomeOwnerInfoRequest;
import com.hcservice.domain.response.BindHouseInfoResponse;
import com.hcservice.service.HomeOwnerService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/homeOwner")
public class HomeOwnerController extends BaseController {

    @Autowired
    HomeOwnerService homeOwnerService;



    @UserLoginToken
    @RequestMapping(value = "/user/bindHomeOwnerInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult bindHomeOwnerInfo(BindHomeOwnerInfoRequest request) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        if(StringUtils.isAnyEmpty(request.getName(), request.getCredentialType(), request.getCredentialNum(), request.getPhoneNum())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = homeOwnerService.bindHomeOwnerInfo(request, user);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getBindHouse", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<BindHouseInfoResponse> getBindHouse() {
        User user = AuthenticationInterceptor.getLoginUser();
        List<Room> rooms = homeOwnerService.getRoomsByUserId(user.getUserId());
        List<BindHouseInfoResponse> responses = rooms.stream().map(room -> {
            BindHouseInfoResponse response = new BindHouseInfoResponse();
            BeanUtils.copyProperties(room, response);
            response.setName(room.getOwner().getName());
            response.setCredentialType(room.getOwner().getCredentialType());
            response.setCredentialNum(room.getOwner().getCredentialNum());
            response.setPhoneNum(room.getOwner().getPhoneNum());
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }
}
