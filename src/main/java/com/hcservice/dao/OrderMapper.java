package com.hcservice.dao;

import com.hcservice.domain.model.ConfigPrice;
import com.hcservice.domain.model.Order;

import java.util.List;

public interface OrderMapper {

    int insertOrder(Order order);

    Order getOrderById(String orderId);

    ConfigPrice getConfigPriceByName(String name);

    int updateOrderById(Order order);

    List<Order> getOrderListBySearch(String search);

}
