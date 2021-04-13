package com.hcservice.service;

import com.hcservice.domain.model.Car;

import java.util.List;

public interface CarService {

    int addCarInfo(Car car);

    List<Car> getCarInfoByUserId(Integer userId);

}
