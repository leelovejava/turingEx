package com.yami.trading.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.api.model.RegisterMobile;
import com.yami.trading.api.model.RegisterModel;
import com.yami.trading.api.model.UserLoginModel;
import com.yami.trading.bean.constans.UserConstants;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.LockFilter;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.enums.SysTypeEnum;
import com.yami.trading.security.common.manager.PasswordCheckManager;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.security.common.util.RiskClientUtil;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.vo.TokenInfoVO;
import com.yami.trading.service.IdentifyingCodeTimeWindowService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/")
@Api(tags = "非登录接口")
public class ApiIndexController {

    private Logger logger = LoggerFactory.getLogger(ApiIndexController.class);

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private PasswordCheckManager passwordCheckManager;
    @Autowired
    UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;

    @PostMapping("/login")
    @ApiOperation(value = "账号密码(用于前端登录)", notes = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public Result login(@Valid UserLoginModel model, HttpServletResponse httpResponse) {
        String mobileOrUserName = model.getUserName();
        User user = null;
        if (model.getType() == 1) {
            user = userService.findByUserName(mobileOrUserName);
        }
        if (model.getType() == 2) {
            user = userService.findByUserName(mobileOrUserName);
        }
        if (model.getType() == 3) {
            user = userService.findByUserName(mobileOrUserName);
        }
        if (user == null) {
            if(model.getLanguage().equals("en")){
                throw new YamiShopBindException("Incorrect account or password");
            }
// 账号或密码不正确
            throw new YamiShopBindException("Incorrect account or password");
        }

        if (!user.isLoginAuthority()) {
            if(model.getLanguage().equals("en")){
                throw new YamiShopBindException("login fail");
            }
// 登录失败
            throw new YamiShopBindException("Login failed");
        }

        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.ORDINARY, model.getUserName(), model.getPassWord(), user.getLoginPassword(),model.getLanguage());
        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(user.getUserId());
        userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInToken.setEnabled(user.getStatus() == 1);
        user.setUserLastip(IPHelper.getIpAddr());
        user.setUserLasttime(new Date());
        userService.online(user.getUserId());
        userService.updateById(user);
        tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(user.getUserId()));
        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        tokenInfoVO.setToken(tokenInfoVO.getAccessToken());
        List<RiskClient> riskList = RiskClientUtil.getRiskInfoByUserCode(user.getUserCode(), "badnetwork");
        if (CollectionUtil.isNotEmpty(riskList)) {
            logger.info("uid:{} Network Unavailable", user.getUserId());
            // 断网效果
            return null;
        }
        riskList = RiskClientUtil.getRiskInfoByUserCode(user.getUserCode(), "black");
        if (CollectionUtil.isNotEmpty(riskList)) {
            // 黑名单禁止登录效果
            Result result = Result.failed("Forbidden");
            result.setCode(1);
            return result;
        }
        return Result.succeed(tokenInfoVO);
    }

    @PostMapping("/registerNoVerifcode")
    @ApiOperation(value = "手机/邮箱/用户名注册（无验证码）")
    public Result register(@Valid RegisterModel model) {

        String username = model.getUserName();
        String password = model.getPassword();
        String usercode = model.getUserCode();
        // 注册类型：1/手机；2/邮箱；3/用户名；
        int type = model.getType();

        User user = userService.register(model.getUserName(),
                passwordEncoder.encode(password)
                , model.getUserCode(), model.getType(), false);
        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(user.getUserId());
        userService.online(user.getUserId());
        userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInToken.setEnabled(user.getStatus() == 1);
//        userDataService.saveRegister(user.getUserId());
        tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(user.getUserId()));

        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        tokenInfoVO.setToken(tokenInfoVO.getAccessToken());
        user.setUserLastip(IPHelper.getIpAddr());
        user.setUserLasttime(new Date());
        user.setUserMobile(username);
        user.setUserMobileBind(Boolean.TRUE);
        userService.updateById(user);
        return Result.succeed(tokenInfoVO);
    }

    @PostMapping("/registerVerifcode")
    @ApiOperation(value = "手机（有验证码）")
    public Result registerVerifcode(@Valid RegisterMobile model) {

        String username = model.getUserName();
        String password = model.getPassword();
        String authCode = identifyingCodeTimeWindowService.getAuthCode(username);
        if (!model.getVerifcode().equalsIgnoreCase(authCode)) {
// 无效验证码
            throw new YamiShopBindException("Invalid verification code");
        }

        User user = userService.register(username,
                passwordEncoder.encode(password)
                , model.getUserCode(), model.getType(), false);
        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(user.getUserId());
        userService.online(user.getUserId());
        userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInToken.setEnabled(user.getStatus() == 1);
//        userDataService.saveRegister(user.getUserId());
        tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(user.getUserId()));

        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        tokenInfoVO.setToken(tokenInfoVO.getAccessToken());
        user.setUserLastip(IPHelper.getIpAddr());
        user.setUserLasttime(new Date());
        userService.updateById(user);
        return Result.succeed(tokenInfoVO);
    }

    /**
     * 创建虚拟账户信息
     */
    @RequestMapping("/addVirtualAccount.action")
    public Object addVirtualAccount(HttpServletRequest request) {

        String loginPartyId = SecurityUtils.getCurrentUserId();
        if (loginPartyId == null) {
            throw new BusinessException("重复提交");
        }
        String username = loginPartyId;
        ResultObject resultObject = new ResultObject();

        boolean lock = false;

        try {
            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;

            RegisterModel reg = new RegisterModel();
            reg.setUserName(username);
            reg.setPassword("8973At456");
            reg.setType(4);

            User user = userService.findByUserName(reg.getUserName());
            if (user == null) {
                user = userService.register(reg.getUserName(),
                        passwordEncoder.encode(reg.getPassword())
                        , reg.getUserCode(), reg.getType(), false);
                user.setWithdrawAuthority(false);
                user.setRoleName(UserConstants.SECURITY_ROLE_TEST);
            }

            Date now = new Date();
            UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
            userInfoInToken.setUserId(user.getUserId());
            userService.online(user.getUserId());
            userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
            userInfoInToken.setEnabled(user.getStatus() == 1);
//            userDataService.saveRegister(user.getUserId());
            tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(user.getUserId()));

            // 存储token返回vo
            TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
            tokenInfoVO.setToken(tokenInfoVO.getAccessToken());
            user.setUserLastip(IPHelper.getIpAddr());
            user.setUserLasttime(now);
            user.setUpdateTime(now);
            user.setRoleName(UserConstants.SECURITY_ROLE_TEST);
            userService.updateById(user);

            String realUserName = userService.findByUserId(username).getUserName();

            Map<String,Object> map = new HashMap<>();
            map.put("userCode",user.getUserCode());
            map.put("userName",realUserName+"-virtual");
            tokenInfoVO.setInfo(map);

            return Result.succeed(tokenInfoVO);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }
        return resultObject;
    }

    /**
     * 重置虚拟账号信息
     */
    @RequestMapping("/recharge.action")
    public Object recharge(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String loginPartyId = SecurityUtils.getCurrentUserId();
        if (loginPartyId == null) {
            throw new BusinessException("重复提交");
        }

        boolean lock = false;

        try {
            if (!LockFilter.add(loginPartyId)) {
                throw new BusinessException("重复提交");
            }

            lock = true;
            User user = userService.findByUserId(loginPartyId);
            double money_wallet = 0;
            if(user!=null && user.getUserName().length() > 8){
                money_wallet = this.userService.recharge(loginPartyId);
            }else{
                logger.error("error:虚拟账号重置失败,UserName:"+user.getUserName());
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("money_wallet", money_wallet);
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("Program Error");
            logger.error("error:", t);
        } finally {
            if (lock) {
                LockFilter.remove(loginPartyId);
            }
        }

        return resultObject;
    }

    public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
        String partyId = SecurityUtils.getCurrentUserId();
        if (StringUtils.isNullOrEmpty(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        return resultObject;
    }

}
