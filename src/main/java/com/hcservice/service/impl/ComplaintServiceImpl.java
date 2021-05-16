package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.dao.ComplaintMapper;
import com.hcservice.domain.model.Complaint;
import com.hcservice.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    ComplaintMapper complaintMapper;

    @Override
    public int addComplaint(Complaint complaint) {
        return complaintMapper.insertComplaint(complaint);
    }

    @Override
    public PageInfo<Complaint> getComplaintByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Complaint> complaintList = complaintMapper.getAllComplaint();
        PageInfo<Complaint> pageInfo = new PageInfo<>(complaintList);
        return pageInfo;
    }
}
