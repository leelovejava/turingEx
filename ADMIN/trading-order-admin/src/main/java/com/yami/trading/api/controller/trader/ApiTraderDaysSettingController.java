package com.yami.trading.api.controller.trader;

import com.yami.trading.bean.trader.domain.TraderDaysSetting;
import com.yami.trading.common.domain.Result;
import com.yami.trading.service.trader.TraderDaysSettingService;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易跟单利息设置")
public class ApiTraderDaysSettingController {
    private static Log logger = LogFactory.getLog(ApiTraderDaysSettingController.class);

    @Autowired
    private TraderDaysSettingService traderDaysSettingService;

    private final String action = "/api/traderDaysSetting!";

    /**
     * 列表
     * @return
     */
    @GetMapping(action + "list.action")
    public Result list() {
        List<TraderDaysSetting> list = traderDaysSettingService.list();

        return Result.succeed(list, "获取数据成功");
    }
}
