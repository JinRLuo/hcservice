package com.hcservice.service;

import com.github.pagehelper.PageInfo;
import com.hcservice.domain.model.Notice;

import java.util.List;

public interface NoticeService {

    public List<Notice> getAllNotice();

    PageInfo<Notice> getAllNoticeByPage(Integer pageNum, Integer pageSize);

    int addNotice(Notice notice);

    int deleteNotice(Integer noticeId);

}
