package com.hcservice.service;

import com.hcservice.common.BusinessException;
import com.hcservice.domain.model.Order;
import com.hcservice.domain.model.User;

public interface OrderService {

    String createCarChargeOrder(User user, Integer type, Integer itemId) throws BusinessException;

    String createServiceCostChargeOrder(User user, Integer costId) throws BusinessException;

    Order getOrderInfo(String orderId);

    int paymentOrder(String orderId, User user) throws BusinessException;

}
