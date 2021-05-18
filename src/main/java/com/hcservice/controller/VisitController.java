package com.hcservice.controller;

import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.User;
import com.hcservice.domain.model.Visitor;
import com.hcservice.domain.request.SubscribeRequest;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.VisitRecordAdminResponse;
import com.hcservice.domain.response.VisitRecordResponse;
import com.hcservice.service.VisitorService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/visit")
public class VisitController extends BaseController {

    @Autowired
    private VisitorService visitorService;

    @UserLoginToken
    @RequestMapping(value = "/user/getRecord",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<VisitRecordResponse>> getVisitRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        List<Visitor> visitorRecords = visitorService.getVisitsByUserId(user.getUserId());
        List<VisitRecordResponse> responses = visitorRecords.stream().map(record -> {
            VisitRecordResponse response = new VisitRecordResponse();
            BeanUtils.copyProperties(record, response);
            response.setRoomNum(record.getRoom().getRoomNum());
            response.setBuildingNum(record.getRoom().getBuildingNum());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.setCreateTime(dtf.format(record.getCreateTime()));
            response.setVisitTime(dtf.format(record.getVisitTime()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/subscribe",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult subscribe(SubscribeRequest request) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        int res = visitorService.addVisitRecord(request, user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/getRecordAdmin",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<VisitRecordAdminResponse>> getRecordAdmin(String search, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        if(search == null){
            search = "";
        }
        search = "%" + search + "%";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PageInfo<Visitor> pageInfo = visitorService.getVisitsByPage(search, pageNum, pageSize);
        List<VisitRecordAdminResponse> list = pageInfo.getList().stream().map(visitor -> {
            VisitRecordAdminResponse record = new VisitRecordAdminResponse();
            BeanUtils.copyProperties(visitor, record);
            record.setCreateTime(dtf.format(visitor.getCreateTime()));
            record.setVisitTime(dtf.format(visitor.getVisitTime()));
            record.setRoomNum(visitor.getRoom().getRoomNum());
            record.setBuildingNum(visitor.getRoom().getBuildingNum());
            record.setUserName(visitor.getUser().getUserName());
            record.setUserPhoneNum(visitor.getUser().getPhoneNum());
            return record;
        }).collect(Collectors.toList());
        ListByPageResponse<VisitRecordAdminResponse> response = new ListByPageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/confirmed",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult confirmed(Integer visitorId) {
        if(visitorId == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = visitorService.confirmed(visitorId);
        if(res<1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

}
