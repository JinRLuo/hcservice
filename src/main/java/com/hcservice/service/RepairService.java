package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.domain.model.Admin;
import com.hcservice.domain.model.Repair;

import java.util.List;

public interface RepairService {

    List<Repair> getRepairRecordByUserId(Integer userId);

    int reportRepair(Repair repair);

    PageInfo<Repair> getRepairRecord(Integer flag, Integer pageNum, Integer pageSize);

    int submitFeedback(Admin admin, Integer repairId, String feedback);

}
