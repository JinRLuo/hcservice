package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.ErrorCode;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.domain.model.Car;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.CarInfoResponse;
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

    @UserLoginToken
    @RequestMapping(value = "/user/addCarInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult addCarInfo(@RequestParam(name = "plateNum") String plateNum) {
        User user = AuthenticationInterceptor.getLoginUser();
        if (!StringUtil.verifyPlateNum(plateNum)) {
           return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Car car = new Car();
        car.setPlateNum(plateNum);
        car.setBeginTime(LocalDateTime.of(2000,1,1,0,0,0));
        car.setEndTime(LocalDateTime.of(2000,1,1,0,0,0));
        car.setType("未办理");
        car.setStatus(0);
        car.setUser(user);
        int res = carService.addCarInfo(car);
        if (res < 0) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getCarInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<CarInfoResponse>> getCarInfo() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Car> carInfos = carService.getCarInfoByUserId(user.getUserId());
        List<CarInfoResponse> responses = carInfos.stream().map(carInfo -> {
            CarInfoResponse response = new CarInfoResponse();
            BeanUtils.copyProperties(carInfo, response);
            response.setBeginTime(dtf.format(carInfo.getBeginTime()));
            response.setEndTime(dtf.format(carInfo.getEndTime()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }


}
