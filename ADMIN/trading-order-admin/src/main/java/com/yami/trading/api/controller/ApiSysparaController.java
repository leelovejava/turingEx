package com.yami.trading.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.TimeZoneContext;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.util.AuthUserContext;
import com.yami.trading.security.common.util.RiskClientUtil;
import com.yami.trading.service.syspara.LocalSysparaService;
import com.yami.trading.service.user.RiskClientService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin
@Data
@ApiModel
public class ApiSysparaController {
    @Autowired
    LocalSysparaService localSysparaService;

    @Autowired
    private RiskClientService riskClientService;

    @Autowired
    private UserService userService;

    /**
     * 可逗号相隔，查询多个参数值。 exchange_rate_out 兑出货币和汇率; exchange_rate_in
     * 兑入货币和汇率;withdraw_fee 提现手续费，type=fixed是单笔固定金额，=rate是百分比，结果到小数点2位。
     * index_top_symbols 首页显示的4个品种。customer_service_url 在线客服URL
     */
    @RequestMapping("api/syspara!getSyspara.action")
    public Result getSyspara(HttpServletRequest request) {
            String code = request.getParameter("code");
            Map<String, Object> data = localSysparaService.find(code);
        return Result.succeed(data);
    }

    @GetMapping("/api/timezone/info")
    public Result<Map<String, Object>> getTimeZoneInfo() {
        Map<String, Object> infoMap = new HashMap();

        Date now = new Date();

        // 当前客户端系统时区比预期的数据展示时区大的毫秒偏移值
        String showTimeZone = TimeZoneContext.showTimeZoneId.get();
        String clientTimeZone = TimeZoneContext.clientTimeZoneId.get();

        long timeOffset = DateTimeTools.getTimeOffset(ZoneId.of(showTimeZone), ZoneId.of(clientTimeZone));

        // 展示数据的时区配置
        infoMap.put("showTimeZone", showTimeZone);
        infoMap.put("timeOffset", timeOffset);
        infoMap.put("now", now);

        return Result.ok(infoMap);
    }

    @GetMapping("/api/check/network")
    public Result<Map<String, Object>> checkNetwork(@RequestParam(required = false) String clientUserCode) {
        Map<String, Object> infoMap = new HashMap();
        infoMap.put("network", "1");

        String clientIp = IPHelper.getIpAddr();
        String userCode = "";
        UserInfoInTokenBO userInfoIn = AuthUserContext.get();
        if (userInfoIn != null) {
            String userId = userInfoIn.getUserId();
            // 缓存优化 TODO
            User userEntity = userService.cacheUserBy(userId);
            if (userEntity != null) {
                userCode = userEntity.getUserCode();
            }
        }
        if (StrUtil.isBlank(userCode) || Objects.equals(userCode, "0")) {
            if (StrUtil.isNotBlank(clientUserCode)) {
                userCode = clientUserCode.trim();
            }
        }
        infoMap.put("userCode", userCode);

        if (StrUtil.isNotBlank(userCode) && !Objects.equals(userCode, "0")) {
            List<RiskClient> badNetwordConfigList = RiskClientUtil.getRiskInfoByUserCode(userCode, "badnetwork");
            if (CollectionUtil.isNotEmpty(badNetwordConfigList)) {
                // 当前用户仍然存在网络异常
                infoMap.put("network", "0");
            }
        }

        List<RiskClient> badNetwordConfigList = RiskClientUtil.getRiskInfoByIp(clientIp, "badnetwork");
        if (CollectionUtil.isNotEmpty(badNetwordConfigList)) {
            // 当前客户端对应的 ip 仍然存在网络异常
            infoMap.put("network", "0");
        }

        return Result.ok(infoMap);
    }

}
