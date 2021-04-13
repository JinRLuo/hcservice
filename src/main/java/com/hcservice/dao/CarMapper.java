package com.hcservice.dao;

import com.hcservice.domain.model.Car;

import java.util.List;

public interface CarMapper {

    int insertCar(Car car);

    List<Car> getCarInfoByUserId(Integer userId);

    Car getCarById(Integer carId);

    int updateCarById(Car car);

}
