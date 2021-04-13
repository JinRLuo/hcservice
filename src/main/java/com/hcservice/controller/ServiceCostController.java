package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.model.ServiceCost;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.ServiceCostRecordResponse;
import com.hcservice.service.ServiceCostService;
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
@RequestMapping("/serviceCost")
public class ServiceCostController extends BaseController {

    @Autowired
    ServiceCostService serviceCostService;

    @UserLoginToken
    @RequestMapping(value = "/user/getServiceCostRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ServiceCostRecordResponse> getServiceCostRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ServiceCost> costList = serviceCostService.getNotPayServiceCostRecord(user.getUserId());
        List<ServiceCostRecordResponse> responses = costList.stream().map(cost -> {
            ServiceCostRecordResponse response = new ServiceCostRecordResponse();
            BeanUtils.copyProperties(cost, response);
            response.setTime(dtf.format(cost.getTime()));
            response.setBuildingNum(cost.getRoom().getBuildingNum());
            response.setRoomNum(cost.getRoom().getRoomNum());
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

}
