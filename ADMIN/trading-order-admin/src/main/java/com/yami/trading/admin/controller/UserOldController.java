package com.yami.trading.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.UserOld;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.user.UserOldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Api(tags = "User Old")
@RequestMapping("admin/userOld")
public class UserOldController {

    @Autowired
    private UserOldService userOldService;

    @PostMapping("list")
    @ApiOperation("User old list")
    public Result<Page<UserOld>> list(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> query = params == null ? Collections.emptyMap() : params;
        long current = toLong(query.get("current"), 1L);
        long size = toLong(query.get("size"), 10L);
        String id = toText(query.get("id"));
        String phone = toText(query.get("phone"));
        String email = toText(query.get("email"));

        Page<UserOld> page = new Page<>(current, size);
        LambdaQueryWrapper<UserOld> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(id)) {
            try {
                queryWrapper.eq(UserOld::getId, Integer.valueOf(id));
            } catch (NumberFormatException ignored) {
            }
        }

        if (StringUtils.isNotEmpty(phone)) {
            queryWrapper.and(wrapper -> wrapper
                .like(UserOld::getPhone, phone)
                .or()
                .like(UserOld::getPhoneAll, phone)
            );
        }

        if (StringUtils.isNotEmpty(email)) {
            queryWrapper.like(UserOld::getEmail, email);
        }

        queryWrapper.orderByDesc(UserOld::getId);
        return Result.succeed(userOldService.page(page, queryWrapper));
    }

    @GetMapping("getById")
    @ApiOperation("Get by id")
    public Result<UserOld> getById(Integer id) {
        if (id == null || id <= 0) {
            return Result.failed("ID cannot be empty");
        }
        return Result.succeed(userOldService.getById(id));
    }

    @PostMapping("save")
    @ApiOperation("Save")
    public Result<String> save(@RequestBody UserOld userOld) {
        if (StringUtils.isEmpty(userOld.getPhone()) && StringUtils.isEmpty(userOld.getEmail())) {
            return Result.failed("phone and email cannot both be empty");
        }
        userOld.setId(null);
        userOldService.save(userOld);
        return Result.succeed("ok");
    }

    @PostMapping("update")
    @ApiOperation("Update")
    public Result<String> update(@RequestBody UserOld userOld) {
        if (userOld.getId() == null || userOld.getId() <= 0) {
            return Result.failed("ID cannot be empty");
        }
        userOldService.updateById(userOld);
        return Result.succeed("ok");
    }

    @GetMapping("delete")
    @ApiOperation("Delete")
    public Result<String> delete(Integer id) {
        if (id == null || id <= 0) {
            return Result.failed("ID cannot be empty");
        }
        userOldService.removeById(id);
        return Result.succeed("ok");
    }

    @PostMapping("deleteBatch")
    @ApiOperation("Delete batch")
    public Result<String> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("Please select records to delete");
        }
        userOldService.removeByIds(ids);
        return Result.succeed("ok");
    }

    private long toLong(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }
}
