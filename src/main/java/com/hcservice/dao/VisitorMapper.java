package com.hcservice.dao;

import com.hcservice.domain.model.Visitor;

import java.util.List;

public interface VisitorMapper {

    List<Visitor> getVisitorByUserId(Integer userId);

    int insertVisitor(Visitor visitor);

    List<Visitor> getVisitorBySearch(String search);

    int updateVisitorStatus(Integer visitorId);
}
