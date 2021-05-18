package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.domain.model.Car;

import java.util.List;

public interface CarService {

    int addCarInfo(Car car);

    List<Car> getCarInfoByUserId(Integer userId);

    PageInfo<Car> getCarsByPage(String search, Integer pageNum, Integer pageSize);

    int modifyCarStatus(Integer carId, Integer status);

}
