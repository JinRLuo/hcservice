package com.hcservice.service.impl;

import com.hcservice.dao.CarMapper;
import com.hcservice.domain.model.Car;
import com.hcservice.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    CarMapper carMapper;

    @Override
    public int addCarInfo(Car car) {
        return carMapper.insertCar(car);
    }

    @Override
    public List<Car> getCarInfoByUserId(Integer userId) {
        return carMapper.getCarInfoByUserId(userId);
    }
}
