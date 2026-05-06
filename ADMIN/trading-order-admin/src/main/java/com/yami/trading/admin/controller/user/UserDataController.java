
package com.yami.trading.admin.controller.user;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.admin.controller.user.model.UserDataAddModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.UpdateUserModel;
import com.yami.trading.admin.model.UserDataListModel;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.user.dto.UserDataDto;
import com.yami.trading.common.annotation.SysLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("userData")
@Api(tags = "用户基础数据")
public class UserDataController {
    @Autowired
    UserService userService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    PasswordManager passwordManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRecomService userRecomService;

    @Autowired
    SysUserOperService sysUserOperService;

    @Autowired
    private PermissionFacade permissionFacade;

    @Autowired
    LogService logService;

    @PostMapping("list")
    @ApiOperation("列表")
    public Result<Page<UserDataDto>> list(@Valid @RequestBody UserDataListModel model) {
        List<String> roleNames = new ArrayList<>();
        if (StrUtil.isEmpty(model.getRolename())) {
            roleNames.add(Constants.SECURITY_ROLE_GUEST);
            roleNames.add(Constants.SECURITY_ROLE_MEMBER);
            roleNames.add(Constants.SECURITY_ROLE_TEST);
        } else {
            roleNames.add(model.getRolename());
        }

        Page<UserDataDto> page = new Page(model.getCurrent(), model.getSize());
        userService.listUserAndRecom(page, roleNames, model.getUserCode(), model.getUserName(), model.getLastIp(),
                permissionFacade.getOwnerUserIds(), model.getUserMail(), model.getUserMobile());
        for (UserDataDto userDataDto : page.getRecords()) {
            userDataDto.setOnline(userService.isOnline(userDataDto.getUserId()));
//            userDataDto.setLoginAuthority(userDataDto.getStatus() == 1);
            userDataDto.setUserRegip(userDataDto.getUserLastip());
            userDataDto.setUserLevel(userDataDto.getUserLevel() / 10);

        }
        return Result.ok(page);
    }

    @ApiOperation(value = "修改用户")
    @PostMapping("update")
    @SysLog("修改用户")
    public Result update(@Valid @RequestBody UpdateUserModel model) {
        User user = userService.getById(model.getUserId());
        if (user == null) {
            throw new YamiShopBindException("参数错误!");
        }

        boolean isLoginAuthority = user.isLoginAuthority();
        boolean isEnabled = user.isEnabled();
        boolean isWithdrawAuthority = user.isWithdrawAuthority();
        boolean isRealNameAuthority = user.isRealNameAuthority();
        String oldRemarks = user.getRemarks();
        int oldUserLevel = user.getUserLevel() / 10;
        
        // 保存旧值用于日志
        Integer oldLoanStatus = user.getLoanStatus();
        java.math.BigDecimal oldLoanCanAmount = user.getLoanCanAmount();
        Integer oldIsOldUser = user.getIsOldUser();
        java.math.BigDecimal oldLoanAlreadyAmount = user.getLoanAlreadyAmount();
        Integer oldCreateRobotStatus = user.getCreateRobotStatus();
        Integer oldTxState = user.getTxState();
        Integer oldOptionPreResult = user.getOptionPreResult();
        
        user.setEnabled(model.isEnabled());
        user.setRemarks(model.getRemarks());
        user.setWithdrawAuthority(model.isWithdrawAuthority());
        user.setLoginAuthority(model.isLoginAuthority());
        Boolean realNameAuthority = model.getRealNameAuthority();
        if (realNameAuthority != null) {
            user.setRealNameAuthority(realNameAuthority);
        }
        if (model.getUserLevel() >= 0) {
            user.setUserLevel(model.getUserLevel() * 10 + user.getUserLevel() % 10);
        }
        
        // 设置新字段
        if (model.getLoanStatus() != null) {
            user.setLoanStatus(model.getLoanStatus());
        }
        if (model.getLoanCanAmount() != null) {
            user.setLoanCanAmount(model.getLoanCanAmount());
        }
        if (model.getIsOldUser() != null) {
            user.setIsOldUser(model.getIsOldUser());
        }
        if (model.getLoanAlreadyAmount() != null) {
            user.setLoanAlreadyAmount(model.getLoanAlreadyAmount());
        }
        if (model.getCreateRobotStatus() != null) {
            user.setCreateRobotStatus(model.getCreateRobotStatus());
        }
        if (model.getTxState() != null) {
            user.setTxState(model.getTxState());
        }
        if (model.getOptionPreResult() != null) {
            user.setOptionPreResult(model.getOptionPreResult());
        }
        
        userService.updateById(user);
        String logtxt = MessageFormat.format(
                "ip:" + IPHelper.getIpAddr() + ",管理员手动修改了用户信息,用户名:{0},原登录权限:{1},原是否业务锁定:{2},原提现权限:{3},原基础认证:{4},原备注:{5},原信用分:{6},现登录权限:{7},现是否业务锁定:{8},现提现权限:{9},现基础认证:{10},现备注:{11},现信用分:{12},原借贷状态:{13},现借贷状态:{14},原可贷金额:{15},现可贷金额:{16},原是否老客户:{17},现是否老客户:{18},原已贷金额:{19},现已贷金额:{20},原购买量化机器状态:{21},现购买量化机器状态:{22},原提币状态:{23},现提币状态:{24},原期权预设结果:{25},现期权预设结果:{26}",
                user.getUserName(),

                isLoginAuthority,
                isEnabled,
                isWithdrawAuthority,
                isRealNameAuthority,
                oldRemarks,
                oldUserLevel,

                user.isLoginAuthority(),
                user.isEnabled(),
                user.isWithdrawAuthority(),
                user.isRealNameAuthority(),
                user.getRemarks(),
                user.getUserLevel() / 10,
                
                oldLoanStatus,
                user.getLoanStatus(),
                oldLoanCanAmount,
                user.getLoanCanAmount(),
                oldIsOldUser,
                user.getIsOldUser(),
                oldLoanAlreadyAmount,
                user.getLoanAlreadyAmount(),
                oldCreateRobotStatus,
                user.getCreateRobotStatus(),
                oldTxState,
                user.getTxState(),
                oldOptionPreResult,
                user.getOptionPreResult());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setUserId(user.getUserId());
        log.setLog(logtxt);
        logService.save(log);
        return Result.ok(null);
    }

    @ApiOperation(" 新增 演示账号")
    @PostMapping(value = "add")
    public Result add(@RequestBody @Valid UserDataAddModel request) {
        request.setPassword(passwordManager.decryptPassword(request.getPassword()));
        String username = request.getUsername().replace(" ", "");
        String password = request.getPassword().replace(" ", "");
        userService.saveUser(username, password, request.isLoginAuthority(), request.isEnabled(), request.getRemarks(), SecurityUtils.getSysUser().getUsername(), IPHelper.getIpAddr(), request.getParentsUseCode());
        return Result.succeed();
    }
}





