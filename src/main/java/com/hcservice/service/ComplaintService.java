package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.domain.model.Complaint;

public interface ComplaintService {

    int addComplaint(Complaint complaint);

    PageInfo<Complaint> getComplaintByPage(Integer pageNum, Integer pageSize);

}
