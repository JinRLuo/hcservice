package com.hcservice.controller;


import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.RoomMapper;
import com.hcservice.domain.model.*;
import com.hcservice.domain.request.BindHomeOwnerInfoRequest;
import com.hcservice.domain.response.*;
import com.hcservice.service.ComplaintService;
import com.hcservice.service.HomeOwnerService;
import com.hcservice.service.NoticeService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/homeOwner")
public class HomeOwnerController extends BaseController {

    @Autowired
    private HomeOwnerService homeOwnerService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ComplaintService complaintService;

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

    @RequestMapping(value = "/getBindRequest", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<HomeOwnerInfoResponse>> getBindRequest() {
        List<HomeOwner> homeOwners = homeOwnerService.getBindRequest();
        List<HomeOwnerInfoResponse> bindRequest = homeOwners.stream().map(homeOwner -> {
            HomeOwnerInfoResponse response = new HomeOwnerInfoResponse();
            BeanUtils.copyProperties(homeOwner, response);
            BeanUtils.copyProperties(homeOwner.getRoom(), response);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.setCreateTime(dtf.format(homeOwner.getCreateTime()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(bindRequest);
    }

    @RequestMapping(value = "/auditBindRequest", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<HomeOwnerInfoResponse>> auditBindRequest(Integer ownerId, Integer status) {
        if(ownerId == null || status==null){
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = homeOwnerService.auditHouse(ownerId, status);
        if(res < 1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    /**
     * 管理员查看小区公告接口
     * @return
     */
    @RequestMapping(value = "/getNotice",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<NoticeResponse>> getNoticeAdmin(Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        PageInfo<Notice> pageInfo = noticeService.getAllNoticeByPage(pageNum, pageSize);
        ListByPageResponse<NoticeResponse> response = new ListByPageResponse<>();
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<NoticeResponse> notices = pageInfo.getList().stream().map(notice -> {
            NoticeResponse nr = new NoticeResponse();
            BeanUtils.copyProperties(notice, nr);
            nr.setAdminName(notice.getAdmin().getAdminName());
            nr.setCreateDate(dtf.format(notice.getCreateDate()));
            return nr;
        }).collect(Collectors.toList());
        response.setList(notices);
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/addNotice",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult addNotice(String title, String content) {
        if(StringUtils.isAnyEmpty(title, content)) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreateDate(LocalDateTime.now());
        notice.setAdmin(admin);
        int res = noticeService.addNotice(notice);
        if(res < 1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/deleteNotice",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult deleteNotice(Integer noticeId) {
        if(noticeId == null){
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = noticeService.deleteNotice(noticeId);
        if(res < 1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/getComplaintInfo",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<ComplaintInfoResponse>> getComplaintInfo(Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        ListByPageResponse<ComplaintInfoResponse> response = new ListByPageResponse<>();
        PageInfo<Complaint> pageInfo = complaintService.getComplaintByPage(pageNum, pageSize);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ComplaintInfoResponse> list = pageInfo.getList().stream().map(complaint -> {
            ComplaintInfoResponse complaintInfoResponse = new ComplaintInfoResponse();
            BeanUtils.copyProperties(complaint, complaintInfoResponse);
            complaintInfoResponse.setCreateTime(dtf.format(complaint.getCreateTime()));
            complaintInfoResponse.setPhoneNum(complaint.getUser().getPhoneNum());
            return complaintInfoResponse;
        }).collect(Collectors.toList());
        response.setList(list);
        return BaseResult.create(response);
    }



}
