package com.hcservice.service.impl;

import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.CarMapper;
import com.hcservice.dao.OrderMapper;
import com.hcservice.dao.ServiceCostMapper;
import com.hcservice.domain.model.*;
import com.hcservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private ServiceCostMapper serviceCostMapper;

    @Override
    public String createCarChargeOrder(User user, Integer type, Integer itemId) throws BusinessException {
        Order order = new Order();
        String configName;
        if (type == 0) {
            configName = "month-park";
        } else if (type == 1) {
            configName = "half-year-park";
        } else {
            configName = "year-park";
        }

        ConfigPrice configPrice = orderMapper.getConfigPriceByName(configName);
        if (configPrice == null) {
            throw new BusinessException(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE_TIME).replace("-", "").replace("T", "").replace(":", "").replace(".", "").substring(0, 16);
        stringBuilder.append(nowDate);
        order.setOrderId(stringBuilder.toString());
        order.setUser(user);
        order.setCreateTime(LocalDateTime.now());
        order.setStatus(0);
        order.setOrderPrice(configPrice.getPrice());
        order.setChargeName(configPrice.getName());
        order.setType(configPrice.getType());
        order.setItemId(itemId);
        int res = orderMapper.insertOrder(order);
        if (res < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }
        return order.getOrderId();
    }

    @Override
    public String createServiceCostChargeOrder(User user, Integer costId) throws BusinessException {
        Order order = new Order();
        ServiceCost serviceCost = serviceCostMapper.getServiceCostById(costId);
        if (serviceCost == null) {
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
        }
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE_TIME).replace("-", "").replace("T", "").replace(":", "").replace(".", "").substring(0, 16);
        stringBuilder.append(nowDate);
        order.setOrderId(stringBuilder.toString());
        order.setOrderPrice(serviceCost.getCost());
        order.setItemId(costId);
        order.setType("ServiceCost");
        order.setChargeName("ServiceCost");
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUser(user);
        int res = orderMapper.insertOrder(order);
        if (res < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }
        return order.getOrderId();
    }

    @Override
    public Order getOrderInfo(String orderId) {
        return orderMapper.getOrderById(orderId);
    }

    @Override
    @Transactional
    public int paymentOrder(String orderId, User user) throws BusinessException {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_INFO_NOT_EXIST);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_NOT_ACTIONABLE);
        }
        if (order.getType().equals("park")) {
            LocalDateTime nowDate = LocalDateTime.now();
            Car car = carMapper.getCarById(order.getItemId());
            int monthCount = 0;
            if (order.getChargeName().equals("month-park")) {
                car.setType("月卡");
                monthCount = 1;
            } else if (order.getChargeName().equals("half-year-park")) {
                car.setType("半年卡");
                monthCount = 6;
            } else if (order.getChargeName().equals("year-park")) {
                car.setType("年卡");
                monthCount = 12;
            }
            if (car.getEndTime().isAfter(nowDate)) {
                car.setEndTime(car.getEndTime().plusMonths(monthCount));
            } else {
                car.setEndTime(nowDate.plusMonths(monthCount));
            }
            int res = carMapper.updateCarById(car);
            if (res < 1) {
                throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
            }
        } else if (order.getType().equals("ServiceCost")) {
            ServiceCost serviceCost = serviceCostMapper.getServiceCostById(order.getItemId());
            if(serviceCost.getStatus() != 0) {
                throw new BusinessException(ErrorCode.SERVICE_COST_ALREADY_PAID);
            }
            serviceCost.setStatus(1);
            int res = serviceCostMapper.updateServiceCostById(serviceCost);
            if (res < 1) {
                throw new BusinessException(ErrorCode.PARAMETER_VALIDATION_ERROR);
            }
        }
        //更改订单状态
        order.setStatus(1);
        int res = orderMapper.updateOrderById(order);
        if (res < 1) {
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
        }
        return 1;
    }
}
