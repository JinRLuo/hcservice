package com.hcservice.service.impl;

import com.hcservice.dao.RepairMapper;
import com.hcservice.domain.model.Repair;
import com.hcservice.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private RepairMapper repairMapper;

    @Override
    public List<Repair> getRepairRecordByUserId(Integer userId) {
       return repairMapper.getRepairByUserId(userId);
    }


    @Override
    public int reportRepair(Repair repair) {
        return repairMapper.insertRepair(repair);
    }
}
