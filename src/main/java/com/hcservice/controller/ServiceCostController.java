package com.hcservice.controller;

import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.ServiceCost;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.ServiceCostRecordAdminResponse;
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

    @RequestMapping(value = "/getServiceCostRecordAdmin", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<ServiceCostRecordAdminResponse>> getServiceCostRecordAdmin(Integer buildingNum, Integer roomNum, Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        PageInfo<ServiceCost> pageInfo = serviceCostService.getServiceCostRecordByPage(buildingNum, roomNum, pageNum, pageSize);
        List<ServiceCostRecordAdminResponse> list = pageInfo.getList().stream().map(serviceCost -> {
            ServiceCostRecordAdminResponse record = new ServiceCostRecordAdminResponse();
            BeanUtils.copyProperties(serviceCost, record);
            record.setYear(serviceCost.getTime().getYear());
            record.setMonth(serviceCost.getTime().getMonthValue());
            record.setBuildingNum(serviceCost.getRoom().getBuildingNum());
            record.setRoomNum(serviceCost.getRoom().getRoomNum());
            if(serviceCost.getRoom().getOwner()!=null) {
                record.setName(serviceCost.getRoom().getOwner().getName());
                record.setPhoneNum(serviceCost.getRoom().getOwner().getPhoneNum());
            }
            return record;
        }).collect(Collectors.toList());
        ListByPageResponse response = new ListByPageResponse();
        response.setList(list);
        response.setTotal(pageInfo.getTotal());
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/addServiceCostRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult addServiceCostRecord(Integer year, Integer month, Integer buildingNum, Integer roomNum) throws BusinessException {
        if(year == null || month == null || buildingNum == null || roomNum == null){
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = serviceCostService.addServiceCostRecord(year, month, buildingNum, roomNum);
        if(res<1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR);
        }
        return BaseResult.create(null);
    }

    @RequestMapping(value = "/deleteServiceCostRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult deleteServiceCostRecord(Integer costId) {
        if(costId == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = serviceCostService.deleteServiceCostRecordByCostId(costId);
        if(res<1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR);
        }
        return BaseResult.create(null);
    }


}
