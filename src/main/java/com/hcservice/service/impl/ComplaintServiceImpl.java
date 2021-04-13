package com.hcservice.service.impl;

import com.hcservice.dao.ComplaintMapper;
import com.hcservice.domain.model.Complaint;
import com.hcservice.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    ComplaintMapper complaintMapper;

    @Override
    public int addComplaint(Complaint complaint) {
        return complaintMapper.insertComplaint(complaint);
    }
}
