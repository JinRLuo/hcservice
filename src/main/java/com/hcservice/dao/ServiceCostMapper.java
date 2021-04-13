package com.hcservice.dao;

import com.hcservice.domain.model.ServiceCost;

import java.util.List;

public interface ServiceCostMapper {

    List<ServiceCost> getNotPayServiceCostsByUserId(Integer userId);

    ServiceCost getServiceCostById(Integer costId);

    int updateServiceCostById(ServiceCost serviceCost);

}
