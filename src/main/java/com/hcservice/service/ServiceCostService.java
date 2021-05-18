package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.domain.model.ServiceCost;

import java.util.List;

public interface ServiceCostService {

    List<ServiceCost> getNotPayServiceCostRecord(Integer userId);

    PageInfo<ServiceCost> getServiceCostRecordByPage(Integer buildingNum, Integer roomNum, Integer pageNum, Integer pageSize);

}
