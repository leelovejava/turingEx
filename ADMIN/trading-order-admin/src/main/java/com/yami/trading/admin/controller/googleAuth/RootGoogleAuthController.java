package com.yami.trading.admin.controller.googleAuth;

import com.yami.trading.admin.controller.googleAuth.model.AdminGoogleAuthModel;
import com.yami.trading.admin.controller.googleAuth.model.AdminGoogleAuthUnBindModel;
import com.yami.trading.admin.controller.googleAuth.model.SuperGoogleAuthBindModel;
import com.yami.trading.admin.controller.googleAuth.model.SuperGoogleAuthUnBindModel;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.GoogleAuthenticator;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "root 谷歌验证码")
@RestController
@CrossOrigin
@Slf4j
@RequestMapping(value = "adminGoogleAuthAction")
public class RootGoogleAuthController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    LogService logService;

    /**
     * 系统配置-admin谷歌验证器
     */
    @ApiOperation("获取admin谷歌验证器绑定状态")
    @GetMapping(value = "getAdminGoogleAuth")
    public Result getAdminGoogleAuth(HttpServletRequest request) {
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new BusinessException("权限不足");
        }
        SysUser secUser = sysUserService.getByUserName("admin");
        boolean google_auth_bind = secUser.isGoogleAuthBind();
        Map<String, Object> map = new HashMap<>();
        map.put("googleAuthBind", google_auth_bind);
        return Result.succeed(map);
    }

    /**
     * admin谷歌验证器-绑定
     */
    @ApiOperation("admin谷歌验证器-绑定")
    @PostMapping(value = "adminGoogleAuthBind")
    public Result adminGoogleAuthBind(@RequestBody @Valid AdminGoogleAuthModel model) {
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new BusinessException("权限不足");
        }
        Syspara superSecret = sysparaService.find("super_google_auth_secret");
        if (superSecret == null || StringUtils.isEmptyString(superSecret.getCode())) {
            throw new BusinessException("超级验证器尚未设置");
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
        boolean checkSuperCode = ga.check_code(superSecret.getSvalue(), Long.valueOf(model.getSuperGoogleAuthCode()), t);
        if (!checkSuperCode) {
            throw new YamiShopBindException("超级管理员谷歌验证码错误");
        }
        boolean checkCode = ga.check_code(model.getGoogleAuthSecret(), Long.valueOf(model.getGoogleAuthCode()), t);
        if (!checkCode) {
            throw new YamiShopBindException("谷歌验证码错误");
        }
        SysUser secUser = sysUserService.getByUserName("admin");
        secUser.setGoogleAuthBind(true);
        secUser.setGoogleAuthSecret(model.getGoogleAuthSecret());
        sysUserService.updateById(secUser);
        saveLog(secUser, SecurityUtils.getSysUser().getUsername(), "ip:" + IPHelper.getIpAddr() + "admin谷歌验证器绑定");
        return Result.succeed();
    }

    public void saveLog(SysUser secUser, String operator, String context) {
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setOperator(operator);
        log.setUsername(secUser.getUsername());
        log.setUserId(secUser.getUserId() + "");
        log.setLog(context);
        log.setCreateTime(new Date());
        logService.save(log);
    }

    /**
     * admin谷歌验证器-解绑
     */

    @ApiOperation(" admin谷歌验证器-解绑")
    @PostMapping(value = "adminGoogleAuthUnBind")
    public Result adminGoogleAuthUnBind(@RequestBody @Valid AdminGoogleAuthUnBindModel model) {
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new BusinessException("权限不足");
        }
        Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
        if (superSecret == null || StringUtils.isEmptyString(superSecret.getSvalue())) {
            throw new BusinessException("超级验证器尚未设置");
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
        boolean checkCode = ga.check_code(superSecret.getSvalue(), Long.valueOf(model.getSuperGoogleAuthCode()), t);
        if (!checkCode) {
            throw new YamiShopBindException("超级管理员谷歌验证码错误");
        }
        SysUser secUser = sysUserService.getByUserName("admin");
        secUser.setGoogleAuthBind(false);
        secUser.setGoogleAuthSecret("");
        sysUserService.updateById(secUser);
        saveLog(secUser, SecurityUtils.getSysUser().getUsername(), "ip:" + IPHelper.getIpAddr() + "admin谷歌验证器绑定");
        return Result.succeed();
    }

    /**
     * 系统配置-超级谷歌验证码
     */
    @GetMapping(value = "getUpdateSuperGoogleAuth")
    @ApiOperation("获取系统配置-超级谷歌验证码绑定状态")
    public Result toUpdateSuperGoogleAuth() {
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new BusinessException("权限不足");
        }
        Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
        boolean google_auth_bind = superSecret != null && !StringUtils.isEmptyString(superSecret.getSvalue());
        Map<String, Object> map = new HashMap<>();
        map.put("googleAuthBind", google_auth_bind);
        return Result.succeed(map);
    }

    /**
     * 系统配置-超级谷歌验证器-绑定
     */
    @PostMapping(value = "superGoogleAuthBind")
    @ApiOperation("系统配置-超级谷歌验证器-绑定")
    public Result superGoogleAuthBind(@RequestBody @Valid SuperGoogleAuthBindModel request) {
            if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
                throw new BusinessException("权限不足");
            }
            String  google_auth_secret= request.getSuperGoogleAuthSecret();
            if (StringUtils.isEmptyString(google_auth_secret)) {
                throw new BusinessException("密匙不能为空");
            }
            String super_google_auth_code =request.getSuperGoogleAuthCode();
            if (StringUtils.isEmptyString(super_google_auth_code)) {
                throw new BusinessException("超级谷歌验证码不能为空");
            }
            Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
            if (superSecret != null && !StringUtils.isEmptyString(superSecret.getSvalue())) {
                throw new BusinessException("用户已绑定");
            }
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
            boolean checkCode = ga.check_code(google_auth_secret, Long.valueOf(request.getSuperGoogleAuthCode()), t);
            if (!checkCode) {
                throw new YamiShopBindException("超级管理员谷歌验证码错误");
            }
            superSecret.setSvalue(google_auth_secret);
            this.sysparaService.updateById(superSecret);
            SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
            saveLog(secUser, SecurityUtils.getSysUser().getUsername(), "ip:" + IPHelper.getIpAddr() + "谷歌超级验证器绑定");
        return Result.succeed();
    }

    /**
     * 超级谷歌验证器-解绑
     */
    @PostMapping(value = "superGoogleAuthUnBind")
    @ApiOperation("超级谷歌验证器-解绑")
    public Result superGoogleAuthUnBind(@RequestBody @Valid SuperGoogleAuthUnBindModel request) {
        String message = "";
        String error = "";
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new BusinessException("权限不足");
        }
        Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
        if (superSecret == null || StringUtils.isEmptyString(superSecret.getSvalue())) {
            throw new BusinessException("用户未绑定，无需解绑");
        }
        String secert = superSecret.getSvalue();
        String super_google_auth_code = request.getSuperGoogleAuthCode();
        if (StringUtils.isNullOrEmpty(super_google_auth_code)) {
            throw new BusinessException("超级谷歌验证码不能为空");
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
        boolean checkCode = ga.check_code(superSecret.getSvalue(),
                Long.valueOf(request.getSuperGoogleAuthCode()), t);
        if (!checkCode) {
            throw new YamiShopBindException("超级管理员谷歌验证码错误");
        }
        superSecret.setSvalue("");
        sysparaService.updateById(superSecret);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        saveLog(secUser, SecurityUtils.getSysUser().getUsername(), "ip:" + IPHelper.getIpAddr() + "谷歌超级验证器解绑");
        return Result.succeed();
    }
}
