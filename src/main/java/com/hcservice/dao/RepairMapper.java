package com.hcservice.dao;

import com.hcservice.domain.model.Repair;

import java.util.List;

public interface RepairMapper {

    List<Repair> getRepairByUserId(Integer userId);

    int insertRepair(Repair repair);

    List<Repair> getAllRepairRecord();

    List<Repair> getRepairRecordByStatus(Integer status);

    Repair getRepairById(Integer repairId);

    int updateRepairFeedback(Repair repair);

}
