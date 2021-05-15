package com.hcservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hcservice.dao.NoticeMapper;
import com.hcservice.domain.model.Notice;
import com.hcservice.domain.model.User;
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

    public PageInfo<Notice> getAllNoticeByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> notices =  noticeMapper.getAllNotice();
        PageInfo<Notice> pageInfo = new PageInfo<>(notices);
        return pageInfo;
    }

    @Override
    public int addNotice(Notice notice) {
        return noticeMapper.insertNotice(notice);
    }

    @Override
    public int deleteNotice(Integer noticeId) {
        return noticeMapper.deleteNotice(noticeId);
    }
}
