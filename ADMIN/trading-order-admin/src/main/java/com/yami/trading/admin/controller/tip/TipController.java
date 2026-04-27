package com.yami.trading.admin.controller.tip;

import cn.hutool.core.util.ObjectUtil;
import com.qiniu.util.StringUtils;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.tip.NewTipsModel;
import com.yami.trading.common.domain.Result;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.chat.online.OnlineChatMessageService;
import com.yami.trading.service.system.TipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Api(tags = "管理员消息通知")
@RequestMapping("tip")
@Slf4j
public class TipController {
    @Autowired
    TipService tipService;
    @Autowired
    private PermissionFacade permissionFacade;
    @Autowired
    private RealNameAuthRecordService realNameAuthRecordService;

    @Autowired
    OnlineChatMessageService onlineChatMessageService;

    @GetMapping("getNewTips")
    @ApiOperation("获取最新通知")
    public Result<List<Map<String, Object>>> getNewTips(@RequestBody @Valid NewTipsModel model) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> userIds = permissionFacade.getOwnerUserIds();
        if (!StringUtils.isNullOrEmpty(model.getModel())) {
            list = tipService.cacheNewTipsByModel(SecurityUtils.getSysUser().getUsername(), model.getTimeStamp(), model.getModel(), userIds);
        } else {
            list = tipService.cacheNewTips(SecurityUtils.getSysUser().getUsername(), model.getTimeStamp(), userIds);
        }
        List<Map<String, Object>> newList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            if (map.get("tip_message") != null && !StringUtils.isNullOrEmpty(map.get("tip_message").toString())) {
                newList.add(map);
            }
        }
//        List<NewTipsDto> result = new ArrayList<>();
//        for (Map<String, Object> map : list) {
//            map.put("null","");
//            //String s = JSON.toJSONString(map);
//            //System.out.println(s);
//            NewTipsDto newTipsDto=new NewTipsDto();
//            newTipsDto.setTipDomName(map.keySet().toString());
//            newTipsDto.setTipContentSum((String) map.getOrDefault("tip_content_sum","0"));
//            newTipsDto.setTipMessage((String) map.getOrDefault("tip_message",""));
//            newTipsDto.setTipShow(map.getOrDefault("tip_show","false").toString());
//            newTipsDto.setTipUrl((String) map.getOrDefault("tip_url",""));
//
//
//
//
//            result.add(newTipsDto);
//        }
        return Result.ok(newList);
    }

    @GetMapping("getTips")
    @ApiOperation("获取通知")
    public Result<Map<String, Object>> getTips() {
//        if (!StringUtils.isNullOrEmpty(getLoginPartyId())) {
//            return "";
//        }
        String userName = SecurityUtils.getSysUser().getUsername();
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> userIds = permissionFacade.getOwnerUserIds();
        List<Map<String, Object>> cacheSumTips = tipService.cacheSumTips(userName, userIds);
        // kyc_untreated_cout 基础认证个数
        long kyc_untreated_cout = realNameAuthRecordService.waitCount(userIds);
        for (Map<String, Object> data : cacheSumTips) {
            if (ObjectUtil.equals(data.get("tip_dom_name"), ".kyc_untreated_cout")) {
                data.put("tip_content_sum", kyc_untreated_cout);
            }
        }
        result.put("tipList", cacheSumTips);
        return Result.ok(result);
    }

    @GetMapping("getAdminTips")
    @ApiOperation("获取通知")
    public Result<Map<String, Object>> getAdminTips(@RequestBody @Valid NewTipsModel model) {
        long allStartTime=    System.currentTimeMillis();
        String userName = SecurityUtils.getSysUser().getUsername();
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> userIds = permissionFacade.getCahceOwnerUserIds();
        List<Map<String, Object>> cacheSumTips = tipService.cacheSumTips(userName, userIds);
        for (Map<String, Object> data : cacheSumTips) {
//            if (ObjectUtil.equals(data.get("tip_dom_name"), ".kyc_untreated_cout")) {
//                long kyc_untreated_cout = realNameAuthRecordService.waitCount(userIds);
//                data.put("tip_content_sum", kyc_untreated_cout);
//            }
        }
		Map<String, Object> unreadMap = new HashMap<>();
		unreadMap.put("tip_dom_name", ".unread");
        int unreadMsg = onlineChatMessageService.unreadMsg(null, "customer", SecurityUtils.getSysUser().getUsername());
		unreadMap.put("tip_content_sum", unreadMsg);
		cacheSumTips.add(unreadMap);

        result.put("tipList", cacheSumTips);
        result.put("popTipList", tipService.cacheNewTips(SecurityUtils.getSysUser().getUsername(), model.getTimeStamp(), userIds));
        long allEndTime=    System.currentTimeMillis();
        long time = allEndTime - allStartTime;
        log.info("获取通知 getAdminTips 接口耗时时间：" + time + "毫秒");

        return Result.ok(result);
    }
}
