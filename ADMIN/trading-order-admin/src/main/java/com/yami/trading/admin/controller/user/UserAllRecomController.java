package com.yami.trading.admin.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.UserAllRecomUpdateModel;
import com.yami.trading.admin.model.UserRecomListModel;
import com.yami.trading.admin.model.UserRecomUpdateModel;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.user.dto.UserAllRecomDto;
import com.yami.trading.bean.user.dto.UserRecomDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
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
@RequestMapping("userAllRecom")
@Api(tags = "所有用户推荐关系")
public class UserAllRecomController {
    @Autowired
    UserRecomService userRecomService;


    @Autowired
    PasswordManager passwordManager;

    @Autowired
    UserService userService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PermissionFacade permissionFacade;

    @PostMapping("list")
    @ApiOperation("列表")
    public Result<Page<UserAllRecomDto>> list(@Valid @RequestBody UserRecomListModel model) {
        Page<UserAllRecomDto> page = new Page(model.getCurrent(), model.getSize());
        userRecomService.listUserAll(page, model.getUserName(), model.getRecomUserName(),
                permissionFacade.getOwnerUserIds());
        for (UserAllRecomDto userRecomDto:page.getRecords()){
           String roleName=  userRecomDto.getRoleName();
            userRecomDto.setRoleNameDesc(Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
        }
        return Result.ok(page);
    }



    @PostMapping("update")
    @ApiOperation("修改推荐关系")
    public Result<Page<UserRecomDto>> update(@Valid @RequestBody UserAllRecomUpdateModel model) {
        model.setLoginSafeword(passwordManager.decryptPassword(model.getLoginSafeword()));
        SysUser sysUser = sysUserService.getSysUserById( SecurityUtils.getSysUser().getUserId());
        if(!passwordEncoder.matches(model.getLoginSafeword(), sysUser.getSafePassword())) {
            throw new YamiShopBindException("资金密码不正确!");
        }
        userRecomService.updateAllRecom(model.getUserCode(),model.getUserId());
        return Result.ok(null);
    }
}
