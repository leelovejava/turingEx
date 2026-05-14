package com.yami.trading.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.dto.UserNoticeDto;
import com.yami.trading.bean.model.UserNotice;

public interface UserNoticeService extends IService<UserNotice> {

    /**
     * 保存用户通知
     */
    void saveNotice(String userId, String title, String content, String noticeType);

    void saveNoticeForAllUsers(String title, String content, String noticeType);

    /**
     * 分页查询通知
     */
    void pageNotice(Page<UserNoticeDto> page, String title, String noticeType, String userCode);
}
