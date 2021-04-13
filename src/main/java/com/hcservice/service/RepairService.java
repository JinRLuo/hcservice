package com.hcservice.service;

import com.hcservice.domain.model.Repair;

import java.util.List;

public interface RepairService {

    List<Repair> getRepairRecordByUserId(Integer userId);

    int reportRepair(Repair repair);

}
