package com.hcservice.controller;

import com.github.pagehelper.PageInfo;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.BaseResult;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.Order;
import com.hcservice.domain.model.User;
import com.hcservice.domain.response.ListByPageResponse;
import com.hcservice.domain.response.OrderInfoAdminResponse;
import com.hcservice.domain.response.OrderInfoResponse;
import com.hcservice.service.OrderService;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import com.tencentcloudapi.tcb.v20180608.models.OrderInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    OrderService orderService;

    @RequestMapping(value = "/getOrderList", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ListByPageResponse<OrderInfoAdminResponse>> getOrderList(String search, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        if(search == null){
            search = "";
        }
        search = "%" + search + "%";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PageInfo<Order> pageInfo = orderService.getOrderListByPage(search, pageNum, pageSize);
        List<OrderInfoAdminResponse> list = pageInfo.getList().stream().map(order -> {
            OrderInfoAdminResponse orderInfo = new OrderInfoAdminResponse();
            BeanUtils.copyProperties(order, orderInfo);
            orderInfo.setCreateTime(dtf.format(order.getCreateTime()));
            orderInfo.setUserName(order.getUser().getUserName());
            orderInfo.setPhoneNum(order.getUser().getPhoneNum());
            return orderInfo;
        }).collect(Collectors.toList());
        ListByPageResponse<OrderInfoAdminResponse> response = new ListByPageResponse<>();
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        response.setList(list);
        return BaseResult.create(response);
    }

}
