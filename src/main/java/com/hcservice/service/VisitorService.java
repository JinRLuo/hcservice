package com.hcservice.service;

import com.hcservice.common.BusinessException;
import com.hcservice.domain.model.User;
import com.hcservice.domain.model.Visitor;
import com.hcservice.domain.request.SubscribeRequest;

import java.util.List;

public interface VisitorService {

    int addVisitRecord(SubscribeRequest request, User user) throws BusinessException;

    List<Visitor> getVisitsByUserId(Integer userId);

}
