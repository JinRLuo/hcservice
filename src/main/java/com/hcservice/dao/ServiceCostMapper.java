package com.hcservice.dao;

import com.hcservice.domain.model.ServiceCost;

import java.util.List;

public interface ServiceCostMapper {

    List<ServiceCost> getNotPayServiceCostsByUserId(Integer userId);

    List<ServiceCost> getServiceCostsBySearch(Integer buildingNum, Integer roomNum);

    ServiceCost getServiceCostById(Integer costId);

    int updateServiceCostById(ServiceCost serviceCost);

    int deleteServiceCostByCostId(Integer costId);

    int insertServiceCost(ServiceCost serviceCost);

}
