package com.hcservice.dao;

import com.hcservice.domain.model.Notice;

import java.util.List;

public interface NoticeMapper {

    List<Notice> getAllNotice();

    int insertNotice(Notice notice);

    int deleteNotice(Integer noticeId);

}
