package com.hcservice.dao;

import com.hcservice.domain.model.ConfigPrice;
import com.hcservice.domain.model.Order;

public interface OrderMapper {

    int insertOrder(Order order);

    Order getOrderById(String orderId);

    ConfigPrice getConfigPriceByName(String name);

    int updateOrderById(Order order);

}
