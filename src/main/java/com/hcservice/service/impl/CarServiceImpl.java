package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.dao.CarMapper;
import com.hcservice.domain.model.Car;
import com.hcservice.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public PageInfo<Car> getCarsByPage(String search, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Car> cars = carMapper.getCarInfoBySearch(search);
        PageInfo<Car> pageInfo = new PageInfo<>(cars);
        return pageInfo;
    }

    @Override
    @Transactional
    public int modifyCarStatus(Integer carId, Integer status){
        Car car = carMapper.getCarById(carId);
        if(car == null) {
            return 0;
        }
        car.setStatus(status);
        return carMapper.updateCarById(car);
    }
}
