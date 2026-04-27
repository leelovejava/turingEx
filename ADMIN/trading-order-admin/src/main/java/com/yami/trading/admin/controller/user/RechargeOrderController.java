package com.yami.trading.admin.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.*;
import com.yami.trading.bean.user.dto.RechargeBlockchainOrderDto;
import com.yami.trading.common.annotation.SysLog;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.RechargeBlockchainOrderService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("rechargeOrder")
@Api(tags = "USDT充值订单")
public class RechargeOrderController {
    @Autowired
    RechargeBlockchainOrderService rechargeBlockchainOrderService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;
    @Autowired
    PermissionFacade permissionFacade;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<RechargeBlockchainOrderDto>> list(@RequestBody @Valid RechargeOrderModel request) {
        Page<RechargeBlockchainOrderDto> page = new Page(request.getCurrent(), request.getSize());
        if (request.getStartTime() == null && request.getEndTime() != null) {
            request.setEndTime(DateUtil.maxDate(request.getEndTime()));
            request.setStartTime(DateUtil.minDate(request.getEndTime()));
        }
        if (request.getStartTime() != null && request.getEndTime() == null) {
            request.setEndTime(DateUtil.maxDate(request.getStartTime()));
            request.setStartTime(DateUtil.minDate(request.getStartTime()));
        }
        rechargeBlockchainOrderService.pageRecord(page, request.getRoleName(),
                request.getOrderNo(), request.getUserName(), request.getStartTime()
                , request.getEndTime(), request.getStatus()
                , permissionFacade.getOwnerUserIds());
        for (RechargeBlockchainOrderDto dto : page.getRecords()) {
            dto.setImg(awsS3OSSFileService.getUrl(dto.getImg()));
            dto.setBlockchainName(dto.getCoin()+"_"+dto.getBlockchainName());

            if (dto.getCoin() != null) {
                dto.setCoin(dto.getCoin().toUpperCase());
            }
        }
        return Result.ok(page);
    }

    @ApiOperation(value = "手动到账")
    @PostMapping("manualReceipt")
    @SysLog("USDT充值订单-手动到账")
    public Result<?> manualReceipt(@RequestBody @Valid ManualReceiptModel model) {
        SysUser user = sysUserService.getSysUserById(SecurityUtils.getSysUser().getUserId());
        sysUserService.checkSafeWord(model.getSafePasssword());
        rechargeBlockchainOrderService.manualReceipt(model.getId(), model.getAmount(), user.getUsername());
        return Result.ok(null);
    }

    @ApiOperation(value = "驳回充值申请")
    @PostMapping("refusalApply")
    @SysLog("USDT充值订单-驳回充值申请")
    public Result<?> refusalApply(@RequestBody @Valid RefusalApplyModel model) {
        SysUser user = sysUserService.getSysUserById(SecurityUtils.getSysUser().getUserId());
        rechargeBlockchainOrderService.refusalApply(model.getId(), model.getContent(), user.getUsername());
        return Result.ok(null);
    }
}
