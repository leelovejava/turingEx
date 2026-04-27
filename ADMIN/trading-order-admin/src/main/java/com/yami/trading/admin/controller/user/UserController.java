package com.yami.trading.admin.controller.user;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.user.model.UserResetExchangeModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.*;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.user.dto.UserBasicDto;
import com.yami.trading.bean.user.dto.UserDto;
import com.yami.trading.common.annotation.SysLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.enums.SysTypeEnum;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.service.user.UserStatisticsService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    UserService userService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    LogService logService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserRecomService userRecomService;

    @Autowired
    UserStatisticsService userStatisticsService;
    @Autowired
    PasswordManager passwordManager;
    @Autowired
    PermissionFacade permissionFacade;

    @PostMapping("list")
    @ApiOperation("列表")
    public Result<Page<UserDto>> list(@Valid @RequestBody UserListModel model) {

        List<String> roleNames = new ArrayList<>();
        if (StrUtil.isEmpty(model.getRolename())) {
            roleNames.add(Constants.SECURITY_ROLE_GUEST);
            roleNames.add(Constants.SECURITY_ROLE_MEMBER);
            roleNames.add(Constants.SECURITY_ROLE_TEST);
        } else {
            roleNames.add(model.getRolename());
        }
        Page<UserDto> page = new Page(model.getCurrent(), model.getSize());
        userService.listUser(page, roleNames, model.getUserCode(), model.getUserName(), model.getUserMail(), model.getUserMobile(),
            permissionFacade.getOwnerUserIds());
        return Result.ok(page);
    }

    @GetMapping("asset/{id}")
    @ApiOperation("用户资产")
    public Result<?> asset(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getById(id);
        Wallet wallet = walletService.getOne(Wrappers.<Wallet>lambdaQuery().eq(Wallet::getUserId, id));
        result.put("walletMone", wallet.getMoney());
        return Result.ok(result);
    }




    /**
     * 交易所 修改账户余额
     */
    @PostMapping(value = "resetExchange")
    @ApiOperation("交易所修改账户余额")
    public Result reset_exchange(@Valid @RequestBody UserResetExchangeModel model) {
        String id = model.getId();
        String moneyRevise = model.getMoneyRevise();
        String login_safeword =model.getLoginSafeword();
        // 修改余额的方式。1. recharge--充值有记录报表 2.change----增加余额，不记录报表 3.withdraw----平台扣款，不记录报表
        String reset_type =model.getResetType();
        String coin_type = model.getCoinType();
        String changeType = model.getChangeType();
        if(StringUtils.isNotEmpty(changeType)) {
            reset_type = "change";
            coin_type = "usdt";
        }
        boolean lock = false;

        sysUserService.checkSafeWord(login_safeword);
        SysUser sysUser= sysUserService.getSysUserById( SecurityUtils.getSysUser().getUserId());
        try {
            String error = this.verificationReset(moneyRevise, reset_type, coin_type, login_safeword);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new YamiShopBindException(error);
            }
            if (!LockFilter.add(id)) {
                throw new BusinessException("请稍后再试");
            }
            lock = true;
            double money_revise = Double.valueOf(moneyRevise).doubleValue();
            if ("change".equals(reset_type) || "recharge".equals(reset_type)) {
                userService.saveResetCreateOrder(id, money_revise, login_safeword,SecurityUtils.getSysUser().getUsername(),
                        reset_type, IPHelper.getIpAddr(), coin_type);
            }
            // 将修改余额的的减少金额去除
            if ("changesub".equals(reset_type) || "withdraw".equals(reset_type)) {
                money_revise = Arith.sub(0, money_revise);
                userService.saveResetCreateWithdraw(id, money_revise, login_safeword,
                        sysUser.getUsername(), reset_type,IPHelper.getIpAddr(), coin_type);
            }
            ThreadUtils.sleep(300);
        }catch (Exception e){
            e.printStackTrace();
            return Result.failed(e.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(id);
            }
        }
        return Result.succeed();
    }

    private String verificationReset(String money_revise, String reset_type, String coin_type, String login_safeword) {

        if (StringUtils.isNullOrEmpty(money_revise)) {
            throw new BusinessException("账变金额必填11");
        }
        if (!StringUtils.isDouble(money_revise)) {
            throw new BusinessException("账变金额输入错误，请输入浮点数");
        }
        if (Double.valueOf(money_revise).doubleValue() <= 0) {
            throw new BusinessException("账变金额不能小于等于0");
        }

        if (StringUtils.isNullOrEmpty(login_safeword)) {
            throw new BusinessException("请输入资金密码");
        }

        if (StringUtils.isNullOrEmpty(reset_type)) {
            throw new BusinessException("请选择账变类型");
        }

        if (StringUtils.isNullOrEmpty(coin_type)) {
            throw new BusinessException("请选择账变币种");
        }

        return null;
    }



    @ApiOperation(value = "修改账户余额")
    @PostMapping("updateWallt")
    @SysLog("修改账户余额")
    public Result updateWallt(@Valid @RequestBody UpdateWalltModel model) {
        sysUserService.checkSafeWord(model.getSafePassword());



        if (model.getAccountType()==1) {

            userService.saveResetCreateOrder(model.getUserId(), model.getMoneyRevise().doubleValue(),
                    model.getSafePassword(),SecurityUtils.getSysUser().getUsername(),
                   "recharge", IPHelper.getIpAddr(), model.getCoinType());
        }
        else if (model.getAccountType()==2){

           double     money_revise = Arith.sub(0, model.getMoneyRevise().doubleValue());
            userService.saveResetCreateWithdraw(model.getUserId(), money_revise,  model.getSafePassword(),
                    SecurityUtils.getSysUser().getUsername(), "withdraw", IPHelper.getIpAddr(), model.getCoinType());
        }
        
//
//        User user= userService.getById(model.getUserId());
//        Log log = new Log();
//        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
//        log.setUsername(user.getUserName());
//        log.setUserId(user.getUserId());
//        log.setOperator(SecurityUtils.getSysUser().getUsername());
//        log.setLog("修改["+user.getUserName()+"]账户余额,ip:[" + IPHelper.getIpAddr() + "]");
//        logService.save(log);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改提现限制流水")
    @PostMapping("updateWithdrawalLimitFlow")
    @SysLog("修改提现限制流水")
    public Result updateWithdrawalLimitFlow(@Valid @RequestBody WithdrawalLimitFlowModel model) {
        String  userName=  SecurityUtils.getSysUser().getUsername();
        userService.updateWithdrawalLimitFlow(model.getUserId(), model.getMoneyWithdraw(),userName);

        User user= userService.getById(model.getUserId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setUserId(user.getUserId());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setLog("修改["+user.getUserName()+"]提现限制流水,ip:[" + IPHelper.getIpAddr() + "]");
        logService.save(log);
        return Result.ok(null);
    }

    @ApiOperation(value = "重置登录密码")
    @PostMapping("restLoginPasswrod")
    @SysLog("重置登录密码")
    public Result restLoginPasswrod(@Valid @RequestBody RestLoginPasswrodModel model) {

        if (!sysUserService.checkGooleAuthCode(Long.valueOf(model.getGoogleAuthCode()))){
            throw new YamiShopBindException("谷歌验证码错误");
        }
        sysUserService.checkSafeWord(model.getLoginSafeword());
        userService.restLoginPasswrod(model.getUserId(), passwordManager.decryptPassword(model.getPassword()));
        User user= userService.getById(model.getUserId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setUserId(user.getUserId());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setLog("重置["+user.getUserName()+"]登录密码,ip:[" + IPHelper.getIpAddr() + "]");
        logService.save(log);
        return Result.ok(null);
    }

    @ApiOperation(value = "解绑用户谷歌验证器")
    @PostMapping("deleteGooleAuthCode")
    @SysLog("解绑用户谷歌验证器")
    public Result deleteGooleAuthCode(@Valid @RequestBody DeleteGooleAuthCodeModel model) {

        userService.deleteGooleAuthCode(model.getUserId(), model.getGoogleAuthCode(), model.getLoginSafeword());
        User user= userService.getById(model.getUserId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setUserId(user.getUserId());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setLog("解绑["+user.getUserName()+"]谷歌验证器,ip:[" + IPHelper.getIpAddr() + "]");
        logService.save(log);
        return Result.ok(null);
    }

    @ApiOperation(value = "重置资金密码")
    @PostMapping("restSafePassword")
    @SysLog("重置资金密码")
    public Result restSafePassword(@Valid @RequestBody RestSafePasswordModel model) {
        if (!sysUserService.checkGooleAuthCode(Long.valueOf(model.getGoogleAuthCode()))){
            throw new YamiShopBindException("登录人谷歌验证码错误");
        }
        sysUserService.checkSafeWord(model.getLoginSafeword());
        userService.restSafePassword(model.getUserId(), passwordManager.decryptPassword(model.getNewSafeword()));
        User user= userService.getById(model.getUserId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setUserId(user.getUserId());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setLog("重置用户["+user.getUserName()+"]资金密码,ip:[" + IPHelper.getIpAddr() + "]");
        logService.save(log);
        return Result.ok(null);
    }

    /**
     * 退出用户登录状态
     */
    @PostMapping(value = "resetUserLoginState")
    @ApiOperation(value = "强制用户退出登录状态")
    public Result resetUserLoginState(@Valid @RequestBody ResetUserLoginStateModel model) {
        try {
            if (!sysUserService.checkGooleAuthCode(Long.valueOf(model.getGoogleAuthCode()))){
                throw new YamiShopBindException("谷歌验证码错误");
            }
            sysUserService.checkSafeWord(model.getLoginSafeword());
            tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(model.getUserId()));
             User user=userService.getById(model.getUserId());
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(user.getUserName());
            log.setUserId(user.getUserId());
            log.setOperator(SecurityUtils.getSysUser().getUsername());
            log.setLog("手动退出用户登录状态,ip:[" + IPHelper.getIpAddr() + "]");
            logService.save(log);
        } catch (Exception e) {
            //throw  new YamiShopBindException("操作失败!");
        }
        return Result.ok(null);
    }

    /**
     * 查询用户基本信息
     * @param uid
     * @return
     */
    @GetMapping("/{uid}")
    @ApiOperation("详情")
    public Result<List<UserBasicDto>> detail(@PathVariable String uid) {
        List<UserBasicDto> list = Lists.newArrayList();
        User eUser = userService.findUserByUserCode(uid);
        if(null == eUser) {
            return Result.failed("用户不存在");
        }
        UserBasicDto self = new UserBasicDto();
        self.setUserId(eUser.getUserId());
        self.setUserName(eUser.getUserName());
        self.setUid(eUser.getUserCode());
        self.setAccountType(Constants.ROLE_MAP.get(eUser.getRoleName()));
        self.setRealNameAuthority(eUser.isRealNameAuthority());
        list.add(self);
        List<UserRecom> parents = userRecomService.getParents(eUser.getUserId());


        for(UserRecom userRecom: parents){
            User user = userService.getById(userRecom.getUserId());
            UserBasicDto userBasicDto = new UserBasicDto();
            userBasicDto.setUserId(user.getUserId());
            userBasicDto.setUserName(user.getUserName());
            userBasicDto.setUid(user.getUserCode());
            userBasicDto.setAccountType(Constants.ROLE_MAP.get(user.getRoleName()));
            userBasicDto.setRealNameAuthority(user.isRealNameAuthority());
            list.add(userBasicDto);
        }

        Collections.reverse(list);

        for(int level = 1;level<= list.size(); level++){
            list.get(level-1).setUserLevel(level);
        }
        return Result.ok(list);
    }



    @GetMapping("/assetsAll")
    @ApiOperation("用户资产 登录者只能看自己下面的用户资产")
    public  Result assetsAll(@RequestParam  String userId){

     User user =  userService.findUserByUserCode(userId);
        String loginPartyId = SecurityUtils.getCurrentSysUserId();
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List<String> children = permissionFacade.getOwnerUserIds();
            if(children != null){
                if(!children.contains(user.getUserId())) throw new BusinessException("目标用户不属于登录人下级");
            }
        }
        List<Map<String, Object>> asset_data =   userStatisticsService.getAssetsAll(SecurityUtils.getCurrentSysUserId(),user.getUserId());
        return  Result.succeed(asset_data);
    }

}





