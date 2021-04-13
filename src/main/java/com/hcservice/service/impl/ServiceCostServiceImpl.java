package com.hcservice.service.impl;

import com.hcservice.dao.ServiceCostMapper;
import com.hcservice.domain.model.ServiceCost;
import com.hcservice.service.ServiceCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCostServiceImpl implements ServiceCostService {

    @Autowired
    ServiceCostMapper serviceCostMapper;

    @Override
    public List<ServiceCost> getNotPayServiceCostRecord(Integer userId) {
        return serviceCostMapper.getNotPayServiceCostsByUserId(userId);
    }
}
