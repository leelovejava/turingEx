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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老客户管理控制器
 */
@RestController
@CrossOrigin
@Api(tags = "老客户管理")
@RequestMapping("admin/userOld")
public class UserOldController {

    @Autowired
    private UserOldService userOldService;

    /**
     * 老客户列表
     */
    @PostMapping("list")
    @ApiOperation("老客户列表")
    public Result<Page<UserOld>> list(Page<UserOld> page, String phone, String email) {
        LambdaQueryWrapper<UserOld> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按手机号查询
        if (StringUtils.isNotEmpty(phone)) {
            queryWrapper.and(wrapper -> wrapper
                .like(UserOld::getPhone, phone)
                .or()
                .like(UserOld::getPhoneAll, phone)
            );
        }
        
        // 按邮箱查询
        if (StringUtils.isNotEmpty(email)) {
            queryWrapper.like(UserOld::getEmail, email);
        }
        
        // 按ID降序排列
        queryWrapper.orderByDesc(UserOld::getId);
        
        Page<UserOld> result = userOldService.page(page, queryWrapper);
        return Result.succeed(result);
    }

    /**
     * 根据ID获取老客户信息
     */
    @GetMapping("getById")
    @ApiOperation("根据ID获取老客户信息")
    public Result<UserOld> getById(Integer id) {
        if (id == null || id <= 0) {
            return Result.failed("ID不能为空");
        }
        UserOld tzUserOld = userOldService.getById(id);
        return Result.succeed(tzUserOld);
    }

    /**
     * 添加老客户
     */
    @PostMapping("save")
    @ApiOperation("添加老客户")
    public Result<String> save(@RequestBody UserOld tzUserOld) {
        // 验证
        if (StringUtils.isEmpty(tzUserOld.getPhone()) && StringUtils.isEmpty(tzUserOld.getEmail())) {
            return Result.failed("手机号和邮箱不能同时为空");
        }
        
        tzUserOld.setId(null);
        userOldService.save(tzUserOld);
        return Result.succeed("添加成功");
    }

    /**
     * 更新老客户信息
     */
    @PostMapping("update")
    @ApiOperation("更新老客户信息")
    public Result<String> update(@RequestBody UserOld tzUserOld) {
        if (tzUserOld.getId() == null || tzUserOld.getId() <= 0) {
            return Result.failed("ID不能为空");
        }

        userOldService.updateById(tzUserOld);
        return Result.succeed("更新成功");
    }

    /**
     * 删除老客户
     */
    @GetMapping("delete")
    @ApiOperation("删除老客户")
    public Result<String> delete(Integer id) {
        if (id == null || id <= 0) {
            return Result.failed("ID不能为空");
        }

        userOldService.removeById(id);
        return Result.succeed("删除成功");
    }

    /**
     * 批量删除老客户
     */
    @PostMapping("deleteBatch")
    @ApiOperation("批量删除老客户")
    public Result<String> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("请选择要删除的记录");
        }

        userOldService.removeByIds(ids);
        return Result.succeed("批量删除成功");
    }
}