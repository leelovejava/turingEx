package com.yami.trading.admin.controller.trader;

import com.yami.trading.bean.trader.domain.TraderDaysSetting;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.trader.TraderDaysSettingService;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易跟单利息设置")
public class AdminTraderDaysSettingController {
    private static Log logger = LogFactory.getLog(AdminTraderDaysSettingController.class);

    @Autowired
    private TraderDaysSettingService traderDaysSettingService;

    private final String action = "/normal/adminTraderDaysSetting!";

    /**
     * 列表
     * @return
     */
    @GetMapping(action + "list.action")
    public Result list() {
        List<TraderDaysSetting> list = traderDaysSettingService.list();

        return Result.succeed(list, "获取数据成功");
    }

    /**
     * 新增
     */
    @PostMapping(action + "add.action")
    public Result add(@RequestBody TraderDaysSetting traderDaysSetting) {
        if(traderDaysSetting.getDays() <= 0) {
            return Result.failed("天数设置必须大于0");
        }

        if(traderDaysSetting.getDayRate() <= 0d && traderDaysSetting.getDays() > 1) {
            return Result.failed("借款超一天的日利率必须大于0");
        }

        traderDaysSetting.setUuid(ApplicationUtil.getCurrentTimeUUID());
        traderDaysSettingService.save(traderDaysSetting);
        return Result.succeed("添加数据成功");
    }

    /**
     * 回显
     */
    @GetMapping(action + "toUpdate.action")
    public Result toUpdate(String uuid) {
        if(StringUtils.isEmptyString(uuid)) {
            return Result.failed("记录ID不能为空");
        }
        TraderDaysSetting traderDaysSetting = traderDaysSettingService.selectById(uuid);
        if(null == traderDaysSetting) {
            return Result.failed("记录不存在");
        }
        return Result.succeed(traderDaysSetting,"修改数据成功");
    }

    /**
     * 修改
     */
    @PostMapping(action + "update.action")
    public Result update(@RequestBody TraderDaysSetting traderDaysSetting) {
        if(StringUtils.isEmptyString(traderDaysSetting.getUuid())) {
            return Result.failed("记录ID不能为空");
        }
        TraderDaysSetting exist = traderDaysSettingService.selectById(traderDaysSetting.getUuid());

        if(null == exist) {
            return Result.failed("更新的记录不存在");
        }
        boolean flag = false;
        if(exist.getDays() != traderDaysSetting.getDays() && traderDaysSetting.getDays() > 0) {
            exist.setDays(traderDaysSetting.getDays());
            flag = true;
        }

        if(traderDaysSetting.getDayRate() > 0d && BigDecimal.valueOf(exist.getDayRate()).compareTo(BigDecimal.valueOf(traderDaysSetting.getDayRate())) !=0) {
            exist.setDayRate(traderDaysSetting.getDayRate());
            flag = true;
        }

        if(flag) {
            traderDaysSettingService.update(traderDaysSetting);
            return Result.succeed("修改数据成功");
        } else {
            return Result.failed("数据未修改");
        }
    }

    /**
     * 删除
     */
    @GetMapping(action + "delete.action")
    public Result delete(String uuid) {
        if(StringUtils.isEmptyString(uuid)) {
            return Result.failed("删除记录ID不能为空");
        }
        traderDaysSettingService.delete(uuid);
        return Result.succeed("删除数据成功");
    }
}
