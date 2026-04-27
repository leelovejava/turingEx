package com.yami.trading.admin.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.user.model.SaveRiskClientModel;
import com.yami.trading.admin.model.*;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.user.dto.RiskClientInfoDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.security.common.util.RiskClientUtil;
import com.yami.trading.service.user.RiskClientService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin
@Api(tags = "风控配置管理")
public class AdminRiskClientController {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    UserService userService;

    @Autowired
    private RiskClientService riskClientService;

    @PostMapping("/riskclient/pagelist")
    @ApiOperation("风控配置列表")
    public Result<Page<RiskClientInfoDto>> pageListRiskConfig(@Valid @RequestBody RiskConfigListModel query) {
        Page<RiskClient> page = new Page(query.getCurrent(), query.getSize());

        QueryWrapper<RiskClient> queryWrapper = new QueryWrapper<RiskClient>();
        if (query.getStatus() >= 0) {
            queryWrapper.eq("status", query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getClientType())) {
            queryWrapper.eq("client_type", query.getClientType());
        }
        if (StrUtil.isNotBlank(query.getType())) {
            queryWrapper.eq("type", query.getType());
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            queryWrapper.like("client_key", query.getKeyword().trim());
        }

        Page pageInfo = riskClientService.page(page, queryWrapper);
        List<RiskClientInfoDto> dtoPageList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(pageInfo.getRecords())) {
            for (Object oneRiskEntity : pageInfo.getRecords()) {
                RiskClient oneRiskClient = (RiskClient)oneRiskEntity;
                RiskClientInfoDto oneDto = new RiskClientInfoDto();
                BeanUtil.copyProperties(oneRiskClient, oneDto);
                oneDto.setId(oneRiskClient.getUuid());

                // 字段值为空时，返回的json结构里没有该字段
                oneDto.setBeginTime("");
                oneDto.setEndTime("");
                if (oneRiskClient.getBeginTimeTs() != null && oneRiskClient.getBeginTimeTs() > 0) {
                    oneDto.setBeginTime(DateTimeTools.formatDateTime(new Date(oneRiskClient.getBeginTimeTs())));
                }
                if (oneRiskClient.getEndTimeTs() != null && oneRiskClient.getEndTimeTs() > 0) {
                    oneDto.setEndTime(DateTimeTools.formatDateTime(new Date(oneRiskClient.getEndTimeTs())));
                }

                dtoPageList.add(oneDto);
            }
            pageInfo.setRecords(dtoPageList);
        }

        return Result.ok(pageInfo);
    }

    @ApiOperation(value = "删除风控配置")
    @GetMapping("/riskclient/deleteById")
    public Result deleteById(@RequestParam(name = "id") String id) {
        RiskClient riskClient = riskClientService.getById(id);
        if (riskClient == null) {
            return Result.failed("该记录不存在");
        }
        RiskClientUtil.disableRiskConfig(riskClient.getType(), riskClient.getClientType(), riskClient.getClientKey());
        riskClientService.removeById(id);
        return Result.succeed();
    }

    @ApiOperation(value = "保存风控配置")
    @PostMapping("/riskclient/save")
    //@SysLog("保存风控配置") // 无token访问此处包错
    public Result saveRiskClient(@Valid @RequestBody SaveRiskClientModel info) {
        String clientKey = info.getUserCode();
        String clientType = "userCode";
        String clientName = "";
        if (StrUtil.isNotBlank(info.getIp())) {
            clientType = "ip";
            clientKey = info.getIp();
            clientName = "";
        } else if (StrUtil.isNotBlank(info.getUserCode())) {
            clientType = "userCode";
            clientKey = info.getUserCode();
            User userEntity = userService.findUserByUserCode(info.getUserCode().trim());
            if (userEntity == null) {
                throw new BusinessException("用户不存在");
            }
            clientName = userEntity.getUserName();
        } else {
            throw new BusinessException("不支持的类型");
        }

        boolean isNew = false;
        Date now = new Date();
        RiskClient existRiskClient = riskClientService.getRiskClient(clientKey, clientType, info.getType());
        if (existRiskClient == null) {
            existRiskClient = new RiskClient();
            existRiskClient.setCreateTime(now);
            isNew = true;
        }

        if (StrUtil.isBlank(info.getBeginTime()) || Objects.equals(info.getBeginTime(), RiskClient.INIT_TIME)) {
            existRiskClient.setBeginTimeTs(0L);
        } else {
            Date time = DateTimeTools.parseWithTimeZone(info.getBeginTime(), ZoneId.of("Asia/Shanghai"));
            existRiskClient.setBeginTimeTs(time.getTime());
        }
        if (StrUtil.isBlank(info.getEndTime()) || Objects.equals(info.getEndTime(), RiskClient.INIT_TIME)) {
            existRiskClient.setEndTimeTs(0L);
        } else {
            Date time = DateTimeTools.parseWithTimeZone(info.getEndTime(), ZoneId.of("Asia/Shanghai"));
            existRiskClient.setEndTimeTs(time.getTime());
        }
        existRiskClient.setClientName(clientName);
        existRiskClient.setClientKey(clientKey);
        existRiskClient.setClientType(clientType);
        existRiskClient.setType(info.getType());
        existRiskClient.setStatus(1);
        if (info.getStatus() >= 0) {
            existRiskClient.setStatus(info.getStatus());
        }
        existRiskClient.setLastOperaTime(now);

        if (isNew) {
            riskClientService.save(existRiskClient);
        } else {
            riskClientService.updateById(existRiskClient);
        }
        if (existRiskClient.getStatus() == 0) {
            RiskClientUtil.disableRiskConfig(info.getType(), clientType, clientKey);
        } else {
            RiskClientUtil.saveRiskConfig(existRiskClient);
        }
        return Result.ok(existRiskClient);
    }

    /**
     * 加载指定的风控配置信息
     *
     * @param id
     * @return
     */
    @GetMapping("/riskclient/loadConfig")
    @ApiOperation("风控配置信息")
    public Result<RiskClientInfoDto> loadRiskConfig(@RequestParam(name = "id") String id) {
        RiskClientInfoDto retDto = new RiskClientInfoDto();

        RiskClient riskClient = riskClientService.getById(id);
        if (riskClient == null) {
            return Result.failed("记录不存在");
        }
        BeanUtil.copyProperties(riskClient, retDto);
        retDto.setId(riskClient.getUuid());
        // 字段值为空时，返回的json结构里没有该字段
        retDto.setBeginTime("");
        retDto.setEndTime("");

        if (riskClient.getBeginTimeTs() != null && riskClient.getBeginTimeTs() > 0) {
            retDto.setBeginTime(DateTimeTools.formatDateTime(new Date(riskClient.getBeginTimeTs())));
        }
        if (riskClient.getEndTimeTs() != null && riskClient.getEndTimeTs() > 0) {
            retDto.setEndTime(DateTimeTools.formatDateTime(new Date(riskClient.getEndTimeTs())));
        }

        return Result.ok(retDto);
    }

//    /**
//     * 查询用户基本信息
//     * @param userId
//     * @return
//     */
//    @GetMapping("/riskclient/info")
//    @ApiOperation("风控配置信息")
//    public Result<RiskClient> getRiskOfUser(@RequestParam(name = "userId") String userId) {
//        List<UserBasicDto> list = Lists.newArrayList();
//        User eUser = userService.findUserByUserCode(uid);
//        if(null == eUser) {
//            return Result.failed("用户不存在");
//        }
//        UserBasicDto self = new UserBasicDto();
//        self.setUserId(eUser.getUserId());
//        self.setUserName(eUser.getUserName());
//        self.setUid(eUser.getUserCode());
//        self.setAccountType(Constants.ROLE_MAP.get(eUser.getRoleName()));
//        self.setRealNameAuthority(eUser.isRealNameAuthority());
//        list.add(self);
//        List<UserRecom> parents = userRecomService.getParents(eUser.getUserId());
//
//
//        for (UserRecom userRecom: parents) {
//            User user = userService.getById(userRecom.getUserId());
//            UserBasicDto userBasicDto = new UserBasicDto();
//            userBasicDto.setUserId(user.getUserId());
//            userBasicDto.setUserName(user.getUserName());
//            userBasicDto.setUid(user.getUserCode());
//            userBasicDto.setAccountType(Constants.ROLE_MAP.get(user.getRoleName()));
//            userBasicDto.setRealNameAuthority(user.isRealNameAuthority());
//            list.add(userBasicDto);
//        }
//
//        Collections.reverse(list);
//
//        for(int level = 1;level<= list.size(); level++){
//            list.get(level-1).setUserLevel(level);
//        }
//        return Result.ok(list);
//    }


}





