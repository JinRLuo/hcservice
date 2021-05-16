package com.hcservice.dao;

import com.hcservice.domain.model.Complaint;

import java.util.List;

public interface ComplaintMapper {

    int insertComplaint(Complaint complaint);

    List<Complaint> getAllComplaint();

}
