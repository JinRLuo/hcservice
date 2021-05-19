package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.common.BusinessException;
import com.hcservice.common.ErrorCode;
import com.hcservice.dao.OrderMapper;
import com.hcservice.dao.RoomMapper;
import com.hcservice.dao.ServiceCostMapper;
import com.hcservice.domain.model.ConfigPrice;
import com.hcservice.domain.model.Room;
import com.hcservice.domain.model.ServiceCost;
import com.hcservice.service.ServiceCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceCostServiceImpl implements ServiceCostService {

    @Autowired
    ServiceCostMapper serviceCostMapper;

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    OrderMapper orderMapper;

    @Override
    public List<ServiceCost> getNotPayServiceCostRecord(Integer userId) {
        return serviceCostMapper.getNotPayServiceCostsByUserId(userId);
    }

    @Override
    public PageInfo<ServiceCost> getServiceCostRecordByPage(Integer buildingNum, Integer roomNum, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ServiceCost> serviceCosts = serviceCostMapper.getServiceCostsBySearch(buildingNum, roomNum);
        PageInfo<ServiceCost> pageInfo = new PageInfo<>(serviceCosts);
        return pageInfo;
    }

    @Override
    public int deleteServiceCostRecordByCostId(Integer costId) {
        return serviceCostMapper.deleteServiceCostByCostId(costId);
    }

    @Override
    public int addServiceCostRecord(Integer year, Integer month, Integer buildingNum, Integer roomNum) throws BusinessException {
        Room room = roomMapper.getRoomByNum(buildingNum, roomNum);
        if(room == null){
            throw new BusinessException(ErrorCode.ROOM_NUM_NOT_EXIST);
        }
        LocalDateTime time = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        ConfigPrice configPrice = orderMapper.getConfigPriceByName("month-service-cost");
        ServiceCost serviceCost = new ServiceCost();
        serviceCost.setCost(configPrice.getPrice()*room.getArea());
        serviceCost.setTime(time);
        serviceCost.setCreateDate(now);
        serviceCost.setRoom(room);
        serviceCost.setStatus(0);
        return serviceCostMapper.insertServiceCost(serviceCost);
    }
}
