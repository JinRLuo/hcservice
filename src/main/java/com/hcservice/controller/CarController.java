package com.hcservice.controller;

import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.domain.model.Car;
import com.hcservice.domain.model.User;
import com.hcservice.domain.model.Visitor;
import com.hcservice.domain.response.CarInfoAdminResponse;
import com.hcservice.domain.response.CarInfoResponse;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.service.CarService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/car")
public class CarController extends BaseController {

    @Autowired
    CarService carService;

    @RequestMapping(value = "/getCarInfoAdmin", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<CarInfoAdminResponse>> getCarInfoAdmin(String search, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        if (search == null) {
            search = "";
        }
        search = "%" + search + "%";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PageInfo<Car> pageInfo = carService.getCarsByPage(search, pageNum, pageSize);
        List<CarInfoAdminResponse> list = pageInfo.getList().stream().map(car -> {
            CarInfoAdminResponse carInfo = new CarInfoAdminResponse();
            BeanUtils.copyProperties(car, carInfo);
            carInfo.setBeginTime(dtf.format(car.getBeginTime()));
            carInfo.setEndTime(dtf.format(car.getEndTime()));
            carInfo.setPhoneNum(car.getUser().getPhoneNum());
            carInfo.setUserName(car.getUser().getUserName());
            return carInfo;
        }).collect(Collectors.toList());
        ListByPageResponse<CarInfoAdminResponse> response = new ListByPageResponse<>();
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        response.setList(list);
        return BaseResult.create(response);
    }

    @RequestMapping(value = "/modifyCarStatus", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult modifyCarStatus(Integer carId, Integer status) {
        if (carId == null || status == null || (status != 0 && status != 1)) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = carService.modifyCarStatus(carId, status);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR);
        }
        return BaseResult.create(null);
    }

}
