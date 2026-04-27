package com.yami.trading.api.controller;

import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.IdentifyingCodeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("api/idcode")
@RestController
@CrossOrigin
@Api(tags = "idcode")
public class ApiIdentifyingCodeController {
    @Autowired
    TokenStore tokenStore;
    @Autowired
    private IdentifyingCodeService identifyingCodeService;

    @RequestMapping("execute")
    public Result execute(@RequestParam String target, HttpServletRequest request) {

        String accessToken = request.getHeader("token");
        UserInfoInTokenBO userInfoInToken = null;
        String userId = "";
        if (StringUtils.isNotEmpty(accessToken)) {
            userInfoInToken = tokenStore.getUserInfoByAccessToken(accessToken, true);
            userId = userInfoInToken.getUserId();
        }
        identifyingCodeService.send(target, IPHelper.getIpAddr(), userId);
        return Result.succeed(null);
    }

}
