package com.hcservice.service.impl;

import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.HomeOwnerMapper;
import com.hcservice.dao.RoomMapper;
import com.hcservice.domain.model.HomeOwner;
import com.hcservice.domain.model.Room;
import com.hcservice.domain.model.User;
import com.hcservice.domain.request.BindHomeOwnerInfoRequest;
import com.hcservice.service.HomeOwnerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeOwnerServiceImpl implements HomeOwnerService {

    @Autowired
    HomeOwnerMapper homeOwnerMapper;

    @Autowired
    RoomMapper roomMapper;

    @Override
    public int bindHomeOwnerInfo(BindHomeOwnerInfoRequest request, User user) throws BusinessException {
        HomeOwner homeOwner = new HomeOwner();
        BeanUtils.copyProperties(request, homeOwner);
        Room room = roomMapper.getRoomByNum(request.getBuildingNum(), request.getRoomNum());
        if (room == null) {
            throw new BusinessException(ErrorCode.ROOM_NUM_NOT_EXIST);
        }
        homeOwner.setRoom(room);
        homeOwner.setUser(user);
        homeOwner.setStatus(0);
        homeOwner.setCreateTime(LocalDateTime.now());
        return homeOwnerMapper.insertHomeOwner(homeOwner);
    }

    @Override
    public List<Room> getRoomsByUserId(Integer userId) {
        return roomMapper.getRoomsByUserId(userId);
    }
}
