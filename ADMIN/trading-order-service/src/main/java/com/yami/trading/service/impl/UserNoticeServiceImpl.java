package com.yami.trading.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.dto.UserNoticeDto;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserNotice;
import com.yami.trading.dao.user.UserNoticeMapper;
import com.yami.trading.service.UserNoticeService;
import com.yami.trading.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements UserNoticeService {

    @Autowired
    private UserService userService;

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
    public void saveNoticeForAllUsers(String title, String content, String noticeType) {
        List<User> users = userService.list();
        List<UserNotice> notices = users.stream().map(u -> {
            UserNotice n = new UserNotice();
            n.setUserId(u.getUserId());
            n.setTitle(title);
            n.setContent(content);
            n.setNoticeType(noticeType);
            n.setStatus(1);
            return n;
        }).collect(Collectors.toList());
        saveBatch(notices);
    }

    @Override
    public void pageNotice(Page<UserNoticeDto> page, String title, String noticeType, String userCode) {
        baseMapper.pageNotice(page, title, noticeType, userCode);
    }
}
