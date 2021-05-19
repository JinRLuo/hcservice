package com.hcservice.controller;

import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.DateUtil;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.common.utils.TencentCOS;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.Repair;
import com.hcservice.domain.model.User;
import com.hcservice.domain.request.ReportRepairRequest;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.RepairRecordAdminResponse;
import com.hcservice.domain.response.RepairRecordResponse;
import com.hcservice.service.RepairService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repairInfo")
public class RepairController extends BaseController {

    @Autowired
    RepairService repairService;

    @Autowired
    TencentCOS tencentCOS;

    @RequestMapping(value = "/getRepairRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<RepairRecordAdminResponse>> getRepairRecordAdmin(Integer flag, Integer pageNum, Integer pageSize) {
        if(flag == null || pageNum == null || pageSize == null){
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        ListByPageResponse<RepairRecordAdminResponse> response = new ListByPageResponse<>();
        PageInfo<Repair> pageInfo = repairService.getRepairRecord(flag, pageNum, pageSize);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<RepairRecordAdminResponse> recordList = pageInfo.getList().stream().map(repair -> {
            RepairRecordAdminResponse record = new RepairRecordAdminResponse();
            BeanUtils.copyProperties(repair, record);
            if(repair.getAdmin() != null) {
                record.setAdminName(repair.getAdmin().getAdminName());
            }
            record.setCreateTime(dtf.format(repair.getCreateTime()));
            record.setPhoneNum(repair.getUser().getPhoneNum());
            record.setUserName(repair.getUser().getUserName());
            return record;
        }).collect(Collectors.toList());
        response.setList(recordList);
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/submitFeedback", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult submitFeedback(Integer repairId, String feedback) {
        if(repairId == null || StringUtils.isEmpty(feedback)){
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int res = repairService.submitFeedback(admin, repairId, feedback);
        if(res<1){
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }


}
