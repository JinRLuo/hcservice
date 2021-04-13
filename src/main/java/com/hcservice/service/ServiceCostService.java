package com.hcservice.service;

import com.hcservice.domain.model.ServiceCost;

import java.util.List;

public interface ServiceCostService {

    List<ServiceCost> getNotPayServiceCostRecord(Integer userId);

}
