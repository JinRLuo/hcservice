package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.RoomMapper;
import com.hcservice.dao.VisitorMapper;
import com.hcservice.domain.model.Room;
import com.hcservice.domain.model.User;
import com.hcservice.domain.model.Visitor;
import com.hcservice.domain.request.SubscribeRequest;
import com.hcservice.service.VisitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    VisitorMapper visitorMapper;

    @Autowired
    RoomMapper roomMapper;

    @Override
    public int addVisitRecord(SubscribeRequest request, User user) throws BusinessException {
        Visitor visitor = convertVisitorFromSubscribeRequest(request);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime visitTime = null;
        try {
            visitTime = LocalDateTime.parse(request.getVisitTime(), dtf);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }
        LocalDateTime createTime = LocalDateTime.now();
        visitor.setVisitTime(visitTime);
        visitor.setCreateTime(createTime);
        visitor.setUser(user);
        Room room = roomMapper.getRoomByNum(request.getBuildingNum(), request.getRoomNum());
        if(room == null) {
            throw new BusinessException(ErrorCode.ROOM_NUM_NOT_EXIST);
        }
        visitor.setRoom(room);
        visitor.setStatus(0);
        return visitorMapper.insertVisitor(visitor);
    }

    @Override
    public List<Visitor> getVisitsByUserId(Integer userId) {
        return visitorMapper.getVisitorByUserId(userId);
    }

    @Override
    public PageInfo<Visitor> getVisitsByPage(String search, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Visitor> visitors = visitorMapper.getVisitorBySearch(search);
        PageInfo<Visitor> pageInfo = new PageInfo<>(visitors);
        return pageInfo;
    }

    public int confirmed(Integer visitorId) {
        return visitorMapper.updateVisitorStatus(visitorId);
    }

    private Visitor convertVisitorFromSubscribeRequest(SubscribeRequest request) {
        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(request, visitor);
        return visitor;
    }
}
