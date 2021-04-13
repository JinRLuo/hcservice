package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.Order;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.OrderInfoResponse;
import com.hcservice.service.OrderService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    OrderService orderService;

    @UserLoginToken
    @RequestMapping(value = "/user/createCarChargeOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> createCarChargeOrder(@RequestParam(name = "type")Integer type,
                                           @RequestParam(name = "itemId")Integer itemId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        String orderId = orderService.createCarChargeOrder(user, type, itemId);
        return BaseResult.create(orderId);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/createServiceCostChargeOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> createServiceCostChargeOrder(@RequestParam(name = "costId")Integer costId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        String orderId = orderService.createServiceCostChargeOrder(user, costId);
        return BaseResult.create(orderId);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getOrderInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> getOrderInfo(@RequestParam(name = "orderId")String orderId) {
        User user = AuthenticationInterceptor.getLoginUser();
        Order order = orderService.getOrderInfo(orderId);
        if(order == null) {
            return BaseResult.create(ErrorCode.ORDER_INFO_NOT_EXIST, "fail");
        }
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        OrderInfoResponse response = new OrderInfoResponse();
        BeanUtils.copyProperties(order, response);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.setCreateTime(dtf.format(order.getCreateTime()));
        return BaseResult.create(response);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/payOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult payOrder(@RequestParam(name = "orderId")String orderId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        orderService.paymentOrder(orderId, user);
        return BaseResult.create(null);
    }

}
