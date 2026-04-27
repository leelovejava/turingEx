package com.yami.trading.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.*;
import com.yami.trading.bean.agent.AgentDto;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.user.dto.UserDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.GoogleAuthenticator;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.service.agent.AgentService;
import com.yami.trading.service.syspara.SysparaService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Api(tags = "代理商")
@RequestMapping("agent")
public class AgentController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PasswordManager passwordManager;
    @Autowired
    UserService userService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    AgentService agentService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    LogService logService;
    @Autowired
    SysUserOperService sysUserOperService;

    @Autowired
    PermissionFacade permissionFacade;
    @Autowired
    private UserRecomService userRecomService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<AgentDto>> list(@RequestBody @Valid UserAgentListModel model) {
        List<String> roleNames = new ArrayList<>();
        roleNames.add(Constants.SECURITY_ROLE_AGENT);
        Page<AgentDto> page = new Page(model.getCurrent(), model.getSize());
        if ("list".equals(model.getViewType())) {
            page = agentService.listTotal(page, model.getUserName(),permissionFacade.getOwnerUserIds());
        }else if ("child".equals(model.getViewType())) {
            List<UserRecom> recoms = userRecomService.findRecoms(model.getUserId());
            // 只取一级
            List<String> childs = recoms.stream().map(UserRecom::getRecomUserId).collect(Collectors.toList());
            page = agentService.listTotal(page, null,childs);
        }
        else {
            page = agentService.pagedQueryNetwork(page);
        }
        String shareUrl = sysparaService.find("share_url").getSvalue();
        shareUrl = shareUrl + "#/?usercode=";
        Map<String, Integer> allRecommCount = userRecomService.findAllRecommCount();
        for (AgentDto agentDto : page.getRecords()) {
            String userId = agentDto.getUserId();
            agentDto.setShareUrl(shareUrl + agentDto.getUserCode());
            agentDto.setLoginAuthority(agentDto.getStatus() == 1);
            agentDto.setOperaAuthority(Constants.SECURITY_ROLE_AGENT.equals(agentDto.getRoleName()));
            agentDto.setUserName(agentDto.getUserName() +"(网络代理数: "+allRecommCount.getOrDefault(userId, 0)+")");
        }

        return Result.ok(page);
    }

    @RequestMapping("getReconNumNet.action")
    public Result<List<Map<String,Integer>>> getReconNumNet(String userId) {
        List<Integer> recoNumNetList = userRecomService.getRecoNumNetList(userId);
        List<Map<String,Integer>> result = new ArrayList<>();
        for(int i=1;i<=recoNumNetList.size();i++){
            Map<String,Integer> data = new HashMap<>();
            data.put("level", i);
            data.put("userCount", recoNumNetList.get(i-1));
            result.add(data);
        }
        return Result.ok(result);
    }


    @ApiOperation(value = "获取下级代理")
    @PostMapping("child")
    public Result<Page<AgentDto>> child(@RequestBody @Valid UserAgentListModel model) {
        Page<UserDto> page = new Page(model.getCurrent(), model.getSize());

        List<UserRecom> recoms = userRecomService.findRecoms(model.getUserId());
        Map<String, String> selfAndParentMap = new HashMap<>();
        for(UserRecom recom: recoms){
            selfAndParentMap.put(recom.getRecomUserId(), recom.getUserId());
        }
        // 只取一级
        List<String> childs = recoms.stream().map(UserRecom::getRecomUserId).collect(Collectors.toList());

        String shareUrl = sysparaService.find("share_url").getSvalue();
        shareUrl = shareUrl + "#/?usercode=";
        Map<String, Integer> allRecommCount = userRecomService.findAllRecommCount();
        List<String> roleNames = new ArrayList<>();
        roleNames.add(Constants.SECURITY_ROLE_GUEST);
        roleNames.add(Constants.SECURITY_ROLE_MEMBER);
        roleNames.add(Constants.SECURITY_ROLE_TEST);

        userService.listUser(page, roleNames, null, null, null,null, childs);
        List<AgentDto> agentDtos = new ArrayList<>();
        for (UserDto userDto : page.getRecords()) {
            AgentDto agentDto = new AgentDto();
            String userId =userDto .getUserId();
            agentDto.setId(userId);
            agentDto.setUserId(userId);
            agentDto.setShareUrl(shareUrl + agentDto.getUserCode());
            agentDto.setLoginAuthority(agentDto.getStatus() == 1);
            agentDto.setOperaAuthority(Constants.SECURITY_ROLE_AGENT.equals(agentDto.getRoleName()));
            agentDto.setRoleName(userDto.getRolename());
            agentDto.setUserCode(userDto.getUserCode());
            agentDto.setUserName(userDto.getUserName() +"(网络代理数: "+allRecommCount.getOrDefault(userId, 0)+")");
            agentDto.setUserNameParent(userService.getById(selfAndParentMap.get(userId)).getUserName());
            agentDtos.add(agentDto);
        }
        Page<AgentDto> pageResult = new Page(model.getCurrent(), model.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(agentDtos);
        return Result.ok(pageResult);
    }



    @ApiOperation(value = "新增")
    @PostMapping("add")
    public Result<?> add(@RequestBody @Valid AgentAndModel model) {
        sysUserService.checkSafeWord(model.getSafeword());
        String roleName = model.isOperaAuthority() ? Constants.SECURITY_ROLE_AGENT : Constants.SECURITY_ROLE_AGENTLOW;
        sysUserOperService.addAgent(model.getUserName(),
                passwordManager.decryptPassword(model.getPassword()), "000000", roleName, model.getRemarks(), model.getParentsUseCode(),
                model.isLoginAuthority(), model.isOperaAuthority());
        return Result.ok(null);
    }

    @ApiOperation(value = "修改代理商")
    @PostMapping("update")
    public Result<?> update(@RequestBody @Valid UpdateAgentModel model) {
        agentService.updateAgent(model.getId(), model.isLoginAuthority(), model.isOperaAuthority(), model.getRemarks());
        return Result.ok(null);
    }

    @ApiOperation(value = "重置登录密码")
    @PostMapping("restPassword")
    public Result<?> add(@RequestBody @Valid RestPasswordModel model) {
        sysUserService.checkSafeWord(model.getSafeword());
        sysUserOperService.restPassword(passwordManager.decryptPassword(model.getPassword()),model.getId());
        return Result.ok(null);
    }

    @ApiOperation(value = "绑定谷歌验证码")
    @PostMapping("bindGoogleAuthCode")
    public Result<?> bindGoogleAuthCode(@RequestBody @Valid AgentBindGoogleModel model) {
        sysUserService.checkSuperGoogleAuthCode(model.getRootGoogleCode());
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
        boolean checkCode = ga.check_code(model.getGoogleAuthSecret(),
                Long.valueOf(model.getGoogleAuthCode()), t);
        if (!checkCode) {
            throw new YamiShopBindException("谷歌验证码错误");
        }
        sysUserOperService.bindGoogleAuthCode(model.getGoogleAuthSecret(), model.getId());
        return Result.ok(null);
    }

    @ApiOperation(value = "解除绑定谷歌验证码")
    @PostMapping("unbindGoogleAuthCode")
    public Result<?> unbindGoogleAuthCode(@RequestBody @Valid IdModel model) {
//        sysUserService.checkSuperGoogleAuthCode(model.getRootGoogleCode());
        sysUserOperService.unbindGoogleAuthCode( model.getId());
        return Result.ok(null);
    }
}
