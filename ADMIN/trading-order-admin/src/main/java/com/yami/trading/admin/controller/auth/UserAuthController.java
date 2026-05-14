package com.yami.trading.admin.controller.auth;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.RealNameExamineModel;
import com.yami.trading.admin.model.UserAuthListModel;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.RealNameAuthRecord;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.user.dto.RealNameAuthDto;
import com.yami.trading.bean.user.dto.RealNameAuthUpdateDto;
import com.yami.trading.common.annotation.SysLog;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@Api(tags = "用户基础认证")
public class UserAuthController {


    @Autowired
    RealNameAuthRecordService realNameAuthRecordService;

    @Autowired
    UserService userService;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @Autowired
    TipService tipService;


    @Autowired
    PermissionFacade permissionFacade;

    @Autowired
    LogService logService;

    @Autowired
    SysUserOperService sysUserOperService;

    @Autowired
    WalletService walletService;

    @Autowired
    MoneyLogService moneyLogService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<RealNameAuthDto>> list(@RequestBody @Valid UserAuthListModel request) {
        List<String> userIds = permissionFacade.getOwnerUserIds();
        Page<RealNameAuthDto> page = new Page(request.getCurrent(), request.getSize());
        realNameAuthRecordService.pageRecord(page, request.getRoleName(), request.getIdNumber(),
                request.getStatus(), request.getUserName(), userIds);
        for (RealNameAuthDto dto : page.getRecords()) {
            if (StrUtil.isNotBlank(dto.getIdBackImg())) {
                dto.setIdBackImg(awsS3OSSFileService.getUrl(dto.getIdBackImg()));
            }
            if (StrUtil.isNotBlank(dto.getIdFrontImg())) {
                dto.setIdBackImg(awsS3OSSFileService.getUrl(dto.getIdFrontImg()));
            }
        }

        return Result.ok(page);
    }

    @ApiOperation("查询用户信息")
    @GetMapping("getById/{id}")
    public Result<RealNameAuthDto> getById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.failed("记录ID不能为空");
        }

        RealNameAuthRecord realNameAuthRecord = realNameAuthRecordService.getOne(Wrappers.<RealNameAuthRecord>lambdaQuery().eq(RealNameAuthRecord::getUuid, id));
        if (null == realNameAuthRecord) {
            return Result.failed("记录不存在");
        }
        User user = userService.getById(realNameAuthRecord.getUserId());
        if (null == user) {
            return Result.failed("用户不存在");
        }

        RealNameAuthDto realNameAuthDto = new RealNameAuthDto();
        realNameAuthDto.setUserName(user.getUserName());
        realNameAuthDto.setName(user.getRealName());
        if (null != realNameAuthRecord) {
            realNameAuthDto.setName(realNameAuthRecord.getName());

            realNameAuthDto.setUuid(realNameAuthRecord.getUuid());
            realNameAuthDto.setIdName(realNameAuthRecord.getIdName());
            realNameAuthDto.setIdNumber(realNameAuthRecord.getIdNumber());
            realNameAuthDto.setNationality(realNameAuthRecord.getNationality());

            realNameAuthDto.setIdFrontImg(awsS3OSSFileService.getUrl(realNameAuthRecord.getIdFrontImg()));
            realNameAuthDto.setIdBackImg(awsS3OSSFileService.getUrl(realNameAuthRecord.getIdBackImg()));
            realNameAuthDto.setHandheldPhoto(awsS3OSSFileService.getUrl(realNameAuthRecord.getHandheldPhoto()));
        }
        return Result.succeed(realNameAuthDto, "查询成功");
    }

    @ApiOperation(value = "修改")
    @PutMapping("edit")
    public Result<?> edit(@RequestBody @Valid RealNameAuthUpdateDto dto) {
        RealNameAuthRecord realNameAuthRecord = realNameAuthRecordService.getById(dto.getUuid());
        if (realNameAuthRecord == null) {
            return Result.failed("记录ID不能为空");
        }
        if (StringUtils.isNotEmpty(dto.getIdName())) {
            realNameAuthRecord.setIdName(dto.getIdName());
        }
        if (StringUtils.isNotEmpty(dto.getIdNumber())) {
            realNameAuthRecord.setIdNumber(dto.getIdNumber());
        }
        if (StringUtils.isNotEmpty(dto.getName())) {
            realNameAuthRecord.setName(dto.getName());
        }
        if (StringUtils.isNotEmpty(dto.getNationality())) {
            realNameAuthRecord.setNationality(dto.getNationality());

        }
        if (StringUtils.isNotEmpty(dto.getIdFrontImg())) {
            realNameAuthRecord.setIdFrontImg(dto.getIdFrontImg());
        }
        if (StringUtils.isNotEmpty(dto.getIdBackImg())) {
            realNameAuthRecord.setIdBackImg(dto.getIdBackImg());

        }
        if (StringUtils.isNotEmpty(dto.getHandheldPhoto())) {
            realNameAuthRecord.setHandheldPhoto(dto.getHandheldPhoto());
        }
        User user = userService.getById(realNameAuthRecord.getUserId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(realNameAuthRecord.getIdNumber());
        log.setUsername(user.getUserName());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setUserId(realNameAuthRecord.getUserId());
        log.setLog("修改用户[" + user.getUserName() + "]认证申请");
        logService.save(log);
        realNameAuthRecordService.updateById(realNameAuthRecord);
        return Result.ok("修改成功");
    }

    /**
     * examine
     * @param model
     * @return
     */
    @ApiOperation(value = "审核")
    @PostMapping("examine")
    @SysLog("用户基础认证-审核")
    public Result<?> examine(@RequestBody @Valid RealNameExamineModel model) {
        RealNameAuthRecord realNameAuthRecord = realNameAuthRecordService.getById(model.getId());
        if (realNameAuthRecord == null) {
            throw new YamiShopBindException("参数错误");
        }
        int status = realNameAuthRecord.getStatus();
        if (model.getType() == 1) {
            realNameAuthRecord.setStatus(2);
            realNameAuthRecord.setOperationTime(new Date());
            realNameAuthRecordService.updateById(realNameAuthRecord);

            User user = userService.getById(realNameAuthRecord.getUserId());
            user.setRealNameAuthority(true);
            user.setRealName(realNameAuthRecord.getName());
            // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
            int userLevelSystem = userService.getUserLevelByAuth(user);

            // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
            // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
            // 如：级别11表示：新注册的前端显示为VIP1；
            int userLevel = user.getUserLevel();
//            user.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);
            userService.updateById(user);


            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setExtra(realNameAuthRecord.getIdNumber());
            log.setUsername(user.getUserName());
            log.setOperator(SecurityUtils.getSysUser().getUsername());
            log.setUserId(realNameAuthRecord.getUserId());
            log.setLog("审核通过用户[" + user.getUserName() + "]认证申请。");
            logService.save(log);

            // 发放300U体验金到USDT冻结账户
            walletService.updateWithLockAndFreeze(String.valueOf(user.getUserId()), 0, 0, 300);
            MoneyLog bonusLog = new MoneyLog();
            bonusLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            bonusLog.setAmount(BigDecimal.valueOf(300));
            // 实名认证通过，赠送300U体验金，冻结至USDT账户
            bonusLog.setLog("KYC verification passed, 300U experience bonus granted and frozen to USDT account");
            bonusLog.setUserId(user.getUserId());
            bonusLog.setWalletType(WalletConstants.WALLET_USDT);
            bonusLog.setContentType(WalletConstants.MONEYLOG_CONTENT_KYC_BONUS);
            moneyLogService.save(bonusLog);
            user.setKycBonusTime(new Date());
            user.setKycBonusAmount(300.0);
            userService.updateById(user);
        }
        if (model.getType() == 2) {
            realNameAuthRecord.setStatus(3);
            realNameAuthRecord.setMsg(model.getContent());
            realNameAuthRecord.setOperationTime(new Date());
            realNameAuthRecordService.updateById(realNameAuthRecord);
            User user = userService.getById(realNameAuthRecord.getUserId());

            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setExtra(realNameAuthRecord.getIdNumber());
            log.setUsername(user.getUserName());
            log.setOperator(SecurityUtils.getSysUser().getUsername());
            log.setUserId(realNameAuthRecord.getUserId());
            log.setLog("驳回用户[" + user.getUserName() + "]认证申请。驳回原因[" + model.getContent() + "]");
            logService.save(log);
        }
        tipService.deleteTip(realNameAuthRecord.getUuid());
        return Result.ok(null);
    }


}
