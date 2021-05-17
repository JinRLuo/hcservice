package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.dao.RepairMapper;
import com.hcservice.domain.model.Admin;
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

    @Override
    public PageInfo<Repair> getRepairRecord(Integer flag, Integer pageNum, Integer pageSize) {
        List<Repair> repairs;
        if(flag == 2) {
            PageHelper.startPage(pageNum, pageSize);
            repairs = repairMapper.getAllRepairRecord();
        } else {
            PageHelper.startPage(pageNum, pageSize);
            repairs = repairMapper.getRepairRecordByStatus(flag);
        }
        PageInfo<Repair> pageInfo = new PageInfo<>(repairs);
        return pageInfo;
    }

    @Override
    public int submitFeedback(Admin admin, Integer repairId, String feedback) {
        Repair repair = repairMapper.getRepairById(repairId);
        repair.setStatus(1);
        repair.setAdmin(admin);
        repair.setRepairId(repairId);
        repair.setFeedBack(feedback);
        return repairMapper.updateRepairFeedback(repair);
    }
}
