package com.yami.trading.admin.controller.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.cms.model.DeleteModel;
import com.yami.trading.admin.controller.notice.model.UserNoticeListModel;
import com.yami.trading.admin.controller.notice.model.UserNoticeModel;
import com.yami.trading.bean.dto.UserNoticeDto;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserNotice;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.service.UserNoticeService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("userNotice")
@Api(tags = "用户通知管理")
public class UserNoticeController {

    @Autowired
    UserNoticeService userNoticeService;

    @Autowired
    PasswordManager passwordManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    UserService userService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<UserNoticeDto>> list(@RequestBody @Valid UserNoticeListModel request) {
        Page<UserNoticeDto> page = new Page(request.getCurrent(), request.getSize());
        userNoticeService.pageNotice(page, request.getTitle(), request.getNoticeType(), request.getUserCode());
        return Result.ok(page);
    }

    @ApiOperation(value = "新增")
    @PostMapping("add")
    public Result<?> add(@RequestBody @Valid UserNoticeModel model) {
        if (StringUtils.isEmptyString(model.getUserCode())) {
            userNoticeService.saveNoticeForAllUsers(model.getTitle(), model.getContent(), model.getNoticeType());
        } else {
            User user = userService.findUserByUserCode(model.getUserCode());
            if (user == null) {
                throw new com.yami.trading.common.exception.YamiShopBindException("UID不存在");
            }
            userNoticeService.saveNotice(user.getUserId(), model.getTitle(), model.getContent(), model.getNoticeType());
        }
        return Result.ok(null);
    }

    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result<?> update(@RequestBody @Valid UserNoticeModel model) {
        UserNotice notice = userNoticeService.getById(model.getId());
        if (notice == null) {
            throw new com.yami.trading.common.exception.YamiShopBindException("参数错误!");
        }

        if (!StringUtils.isEmptyString(model.getUserCode())) {
            User user = userService.findUserByUserCode(model.getUserCode());
            if (user == null) {
                throw new com.yami.trading.common.exception.YamiShopBindException("UID不存在");
            }
            notice.setUserId(user.getUserId());
        } else {
            notice.setUserId("");
        }
        notice.setTitle(model.getTitle());
        notice.setContent(model.getContent());
        notice.setNoticeType(model.getNoticeType());
        userNoticeService.updateById(notice);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除")
    @PostMapping("delete")
    public Result<?> delete(@RequestBody @Valid DeleteModel model) {
        userNoticeService.removeById(model.getId());
        return Result.ok(null);
    }
}
