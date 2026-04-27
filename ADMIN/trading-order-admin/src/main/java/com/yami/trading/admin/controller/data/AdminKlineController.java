package com.yami.trading.admin.controller.data;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.huobi.data.internal.KlineInitService;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.security.common.model.YamiSysUser;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@RestController
@CrossOrigin
@RequestMapping("normal/adminItemAction!")
@Api(tags = "行情数据")
@Slf4j
public class AdminKlineController {

    @Resource
    private KlineInitService klineInitService;
    @Resource
    private ItemService itemService;
    @Resource
    private KlineService klineService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserOperService sysUserOperService;

    /**
     * kline初始化
     */
    @ApiOperation(value = "kline初始化")
    @GetMapping(value = {"klineInit.action", "klineInitConfig.action"})
    public Result<String> klineInit(@RequestParam @NotBlank String paraInitSymbol,
                                    @RequestParam(required = false) String symbolType) {
        String symbols;
        if (StringUtils.isNotEmpty(symbolType)) {
            symbols = itemService.findByType(symbolType).stream().filter(i -> !Objects.equals("1", i.getFake()) && i.isActive())
                    .map(Item::getSymbol)
                    .collect(Collectors.joining(","));
        } else {
            symbols = paraInitSymbol;
        }
        try {
            ThreadUtil.execAsync(() -> klineInitService.klineInit(symbols));
            YamiSysUser sysUser = SecurityUtils.getSysUser();
            if (sysUser != null) {
                SysUser secUser = sysUserService.getByUserName(sysUser.getUsername());
                if (secUser != null) {
                    sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("kline初始化:{}", symbols));
                }
            }
        } catch (Exception e) {
            log.error("k线图初始化失败", e);
            throw new YamiShopBindException("k线图初始化失败");
        }
        return Result.succeed("K线图初始化完成");
    }

    @ApiOperation(value = "kline清理")
    @GetMapping(value = {"clean.action"})
    public Result<String> clean() {
        klineService.clean();
        return Result.succeed("kline清理完成");
    }
}
