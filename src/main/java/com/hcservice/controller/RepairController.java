package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.DateUtil;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.common.utils.TencentCOS;
import com.hcservice.domain.model.Repair;
import com.hcservice.domain.model.User;
import com.hcservice.domain.request.ReportRepairRequest;
import com.hcservice.domain.response.RepairRecordResponse;
import com.hcservice.service.RepairService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @UserLoginToken
    @RequestMapping(value = "/user/getRepairRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<RepairRecordResponse>> getRepairRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Repair> repairs = repairService.getRepairRecordByUserId(user.getUserId());
        List<RepairRecordResponse> responses = repairs.stream().map(rp -> {
            RepairRecordResponse response = new RepairRecordResponse();
            BeanUtils.copyProperties(rp, response);
            response.setCreateTime(dtf.format(rp.getCreateTime()));
            if (rp.getAdmin() != null) {
                response.setAdminName(rp.getAdmin().getAdminName());
            }
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/reportRepair", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROM_DATA})
    public BaseResult<List<RepairRecordResponse>> reportRepair(ReportRepairRequest request) {
        User user = AuthenticationInterceptor.getLoginUser();

        if (StringUtils.isEmpty(request.getType()) || StringUtils.isEmpty(request.getDescription())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : request.getMultipartFiles()) {
            //获取文件的名称
            String fileName = multipartFile.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return BaseResult.create(ErrorCode.IMAGE_CAN_NOT_BE_NULL, "fail");
            }
            //判断有无后缀
            if (fileName.lastIndexOf(".") < 0) {
                return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
            }

            //获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));

            //如果不是图片
            if (!prefix.equalsIgnoreCase(".jpg") && !prefix.equalsIgnoreCase(".jpeg") && !prefix.equalsIgnoreCase(".svg") && !prefix.equalsIgnoreCase(".gif") && !prefix.equalsIgnoreCase(".png")) {
                return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
            }
            final File excelFile;
            try {
                excelFile = File.createTempFile("imagesFile-" + System.currentTimeMillis(), prefix);
                //将Multifile转换成File
                multipartFile.transferTo(excelFile);
            } catch (IOException e) {
                return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
            }

            //调用腾讯云工具上传文件
            String imageName = tencentCOS.uploadFile(excelFile, "repairImage");

            //程序结束时，删除临时文件
            tencentCOS.deleteFile(excelFile);
            if (imageName == null) {
                return BaseResult.create(ErrorCode.IMAGE_UPDATE_ERROR, "fail");
            }
            fileUrls.add(imageName);
        }
        StringBuilder imgUrl = new StringBuilder();
        if (fileUrls.size() > 0) {
            fileUrls.stream().forEach(url -> {
                imgUrl.append(url);
                imgUrl.append("|");
            });
            imgUrl.deleteCharAt(imgUrl.length() - 1);
        }
        Repair repair = new Repair();
        repair.setUser(user);
        repair.setDescription(request.getDescription());
        repair.setType(request.getType());
        repair.setCreateTime(LocalDateTime.now());
        repair.setImgUrl(imgUrl.toString());
        repair.setStatus(0);
        int res = repairService.reportRepair(repair);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Repair> repairs = repairService.getRepairRecordByUserId(user.getUserId());
        List<RepairRecordResponse> responses = repairs.stream().map(rp -> {
            RepairRecordResponse response = new RepairRecordResponse();
            BeanUtils.copyProperties(rp, response);
            response.setCreateTime(dtf.format(rp.getCreateTime()));
            if (rp.getAdmin() != null) {
                response.setAdminName(rp.getAdmin().getAdminName());
            }
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

}
