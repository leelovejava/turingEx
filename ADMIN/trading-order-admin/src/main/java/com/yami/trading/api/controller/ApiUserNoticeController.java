package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.UserNotice;
import com.yami.trading.common.domain.Result;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.UserNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Api(tags = "用户通知")
@RequestMapping("api/userNotice")
public class ApiUserNoticeController {

    @Autowired
    private UserNoticeService userNoticeService;

    @GetMapping("list")
    @ApiOperation("通知列表")
    public Result list(@RequestParam(defaultValue = "1") int pageNo,
                       @RequestParam(defaultValue = "20") int pageSize) {
        String userId = SecurityUtils.getCurrentUserId();
        Page<UserNotice> page = userNoticeService.page(
                new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<UserNotice>()
                        .eq(UserNotice::getUserId, userId)
                        .orderByDesc(UserNotice::getCreateTime)
        );
        return Result.ok(page);
    }

    @GetMapping("unreadCount")
    @ApiOperation("未读数量")
    public Result unreadCount() {
        String userId = SecurityUtils.getCurrentUserId();
        long count = userNoticeService.count(
                new LambdaQueryWrapper<UserNotice>()
                        .eq(UserNotice::getUserId, userId)
                        .eq(UserNotice::getStatus, 1)
        );
        return Result.ok(count);
    }

    @PostMapping("readAll")
    @ApiOperation("全部标记已读")
    public Result readAll() {
        String userId = SecurityUtils.getCurrentUserId();
        UserNotice update = new UserNotice();
        update.setStatus(2);
        userNoticeService.update(update,
                new LambdaQueryWrapper<UserNotice>()
                        .eq(UserNotice::getUserId, userId)
                        .eq(UserNotice::getStatus, 1)
        );
        return Result.ok("success");
    }

    @PostMapping("read/{id}")
    @ApiOperation("标记已读")
    public Result read(@PathVariable String id) {
        UserNotice notice = userNoticeService.getById(id);
        if (notice != null) {
            notice.setStatus(2);
            userNoticeService.updateById(notice);
        }
        return Result.ok("success");
    }
}
