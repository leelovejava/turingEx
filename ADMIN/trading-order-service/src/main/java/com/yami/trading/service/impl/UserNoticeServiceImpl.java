package com.yami.trading.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.dto.UserNoticeDto;
import com.yami.trading.bean.model.UserNotice;
import com.yami.trading.dao.user.UserNoticeMapper;
import com.yami.trading.service.UserNoticeService;
import org.springframework.stereotype.Service;

@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements UserNoticeService {

    @Override
    public void saveNotice(String userId, String title, String content, String noticeType) {
        UserNotice notice = new UserNotice();
        notice.setUserId(userId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setNoticeType(noticeType);
        notice.setStatus(1);
        save(notice);
    }

    @Override
    public void pageNotice(Page<UserNoticeDto> page, String title, String noticeType, String userCode) {
        baseMapper.pageNotice(page, title, noticeType, userCode);
    }
}
