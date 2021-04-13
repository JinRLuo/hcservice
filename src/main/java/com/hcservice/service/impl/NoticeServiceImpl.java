package com.hcservice.service.impl;

import com.hcservice.dao.NoticeMapper;
import com.hcservice.domain.model.Notice;
import com.hcservice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    NoticeMapper noticeMapper;

    @Override
    public List<Notice> getAllNotice() {
        return noticeMapper.getAllNotice();
    }
}
