package com.yami.trading.admin.controller.etf;

import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.etf.domain.EtfKLine;
import com.yami.trading.bean.etf.domain.EtfSecKLine;
import com.yami.trading.bean.etf.domain.KlineConfig;
import com.yami.trading.bean.etf.dto.KlineConfigDTO;
import com.yami.trading.bean.etf.mapstruct.EtfSecKLineWrapper;
import com.yami.trading.bean.etf.mapstruct.KlineConfigWrapper;
import com.yami.trading.bean.etf.query.KlineConfigQuery;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemDTO;
import com.yami.trading.bean.item.query.ItemQuery;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.query.QueryWrapperGenerator;
import com.yami.trading.common.util.CopyBeanUtils;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.security.common.model.YamiSysUser;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.etf.EtfSecKLineService;
import com.yami.trading.service.etf.KlineConfigService;
import com.yami.trading.service.etf.MarketService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * etfK线图配置表Controller
 *
 * @author lucas
 * @version 2023-05-03
 */

@Api(tags = "债劵ETF_币对管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/etf/klineConfig")
public class KlineConfigController {

    @Autowired
    private KlineConfigService klineConfigService;

    @Autowired
    private KlineConfigWrapper klineConfigWrapper;

    @Autowired
    private EtfSecKLineWrapper etfSecKLineWrapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    MarketService marketService;

    @Autowired
    EtfSecKLineService etfSecKLineService;
    @Autowired
    SysUserService sysUserService;

    @Autowired
    private SysUserOperService sysUserOperService;

    /**
     * etfK线图配置表列表数据
     */
    @ApiOperation(value = "查询etfK线图配置表列表数据")
    @GetMapping("list")
    public Result<IPage<ItemDTO>> list(ItemQuery itemQuery, Page<ItemDTO> page) throws Exception {
        QueryWrapper queryWrapper = QueryWrapperGenerator.buildQueryCondition(itemQuery, ItemQuery.class);
        IPage<ItemDTO> result = itemService.findPage(page, queryWrapper);
        return Result.ok(result);
    }

    /**
     * 添加交易对
     */
    @ApiOperation(value = "添加交易对")
    @PostMapping("addItem")
    public Result<String> addItem(@Valid @RequestBody Item item) throws Exception {
        //新增或编辑表单保存
        String uuid = item.getUuid();
        Item old = null;
        if (uuid != null) {
            old = itemService.getById(uuid);
            String[] propertyNames = CopyBeanUtils.getNullPropertyNames(item);
            String[] copy = Arrays.copyOf(propertyNames, propertyNames.length + 5);
            copy[propertyNames.length] = "pips";
            copy[propertyNames.length + 1] = "pipsAmount";
            copy[propertyNames.length + 2] = "adjustmentValue";
            copy[propertyNames.length + 3] = "unitAmount";
            copy[propertyNames.length + 4] = "unitFee";
            BeanUtils.copyProperties(item, old, copy);
            itemService.updateById(old);
        } else {
            itemService.save(item);
        }

        YamiSysUser sysUser = SecurityUtils.getSysUser();
        if (sysUser != null) {
            SysUser secUser = sysUserService.getByUserName(sysUser.getUsername());
            if (secUser != null) {
                if (old != null) {
                    String dec = "修改前数据：{" +
                            "名称=" + old.getName() +
                            ", 代码=" + old.getSymbol() +
                            ", 保留精度=" + old.getDecimals() +
                            ", 交易量倍数=" + old.getMultiple() +
                            ", 借贷利率=" + old.getBorrowingRate() +
                            "}。" + "修改后数据：{" + "名称=" + item.getName() +
                            ", 代码=" + item.getSymbol() +
                            ", 保留精度=" + item.getDecimals() +
                            ", 交易量倍数=" + item.getMultiple() +
                            ", 借贷利率=" + item.getBorrowingRate() +
                            "}";
                    sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("ip:{},行情品种 {}的数据被编辑。{}", IPHelper.getIpAddr(), item.getSymbol(), dec));
                } else {
                    sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("ip:{},新增行情品种 {}。", IPHelper.getIpAddr(), item.getSymbol()));
                }
            }
        }
        return Result.ok("添加交易对成功");
    }


    /**
     * 根据Id获取etfK线图配置表数据
     */
    @ApiOperation(value = "根据Id获取etfK线图配置表数据")
    @GetMapping("queryById")
    public Result<KlineConfigDTO> queryById(String id) {
        return Result.ok(klineConfigWrapper.toDTO(klineConfigService.getById(id)));
    }

    /**
     * 根据Id获取etfK线图配置表数据
     */
    @ApiOperation(value = "查询etfK线图配置列表")
    @GetMapping("pageList")
    public Result<IPage<KlineConfigDTO>> pageList(KlineConfigQuery query, Page<KlineConfig> page) throws Exception {
        IPage<KlineConfigDTO> list;
        if (Objects.isNull(query.getSymbol())) {
            list = klineConfigService.page(page, new LambdaQueryWrapper<KlineConfig>().orderBy(true, false, KlineConfig::getOpenTimeTs)).convert(klineConfigWrapper::toDTO);
        } else {
            list = klineConfigService.page(page, new LambdaQueryWrapper<>(new KlineConfig()).eq(KlineConfig::getSymbol, query.getSymbol()).orderBy(true, false, KlineConfig::getOpenTimeTs)).convert(klineConfigWrapper::toDTO);
        }
        list.getRecords().forEach(r -> {
            Item bySymbol = itemService.findBySymbol(r.getSymbol());
            if (bySymbol != null) {
                r.setSymbolName(bySymbol.getName());
            }
        });
        return Result.ok(list);
    }

    /**
     * 保存etfK线图配置表
     */
    @ApiOperation(value = "保存etfK线图配置表")
    @PostMapping("save")
    public Result<String> save(@RequestBody KlineConfigDTO klineConfigDTO) {
        Tuple startAndEnd = klineConfigService.getStartAndEnd(klineConfigDTO.getOpenTimeTs());
        klineConfigDTO.setOpenTimeTs(startAndEnd.get(0));
        klineConfigDTO.setCloseTimeTs(startAndEnd.get(1));

        if (etfSecKLineService.count(new LambdaQueryWrapper<>(new EtfSecKLine()).eq(EtfSecKLine::getSymbol, klineConfigDTO.getSymbol()).between(EtfSecKLine::getTs, klineConfigDTO.getOpenTimeTs(), klineConfigDTO.getCloseTimeTs())) > 0) {
            return Result.failed("今天数据已存在，无法添加");
        }

        //新增或编辑表单保存
        KlineConfig klineConfig = klineConfigWrapper.toEntity(klineConfigDTO);
        klineConfig.setCreateTime(new Date());

        List<Kline> klines = klineConfigService.generateSecKLine(klineConfigDTO);
        if (klines != null) {
            List<EtfSecKLine> etfSecKLines = etfSecKLineWrapper.toDTO(klines);
            etfSecKLineService.saveBatch(etfSecKLines, 10000);
        }

        klineConfigService.saveOrUpdate(klineConfig);
        marketService.clear();
        return Result.ok("保存etfK线图配置表成功");
    }


    /**
     * 删除etfK线图配置表
     */
    @ApiOperation(value = "删除etfK线图配置表")
    @DeleteMapping("delete")
    public Result<String> delete(String ids) {
        List<String> list = Arrays.asList(ids.split(","));

        klineConfigService.deleteByRobotModelId(list);
        return Result.ok("删除etfK线图配置表成功");
    }


    /**
     * 删除etfK线图配置表
     */
    @ApiOperation(value = "初始化ETF当天K线图,分钟级别")
    @PostMapping("init")
    public Result<EtfKLine> init(@RequestBody KlineConfigDTO klineConfigDTO) {
        Item symbol = itemService.findBySymbol(klineConfigDTO.getSymbol());
        EtfKLine model = klineConfigService.queryKLine(klineConfigDTO);
        List<Kline> klineList = model.getKlineList();
        List<Kline> result = new ArrayList<>();
        // 抽象成15分钟图，方便前端显示
        List<List<Kline>> partition = Lists.partition(klineList, 15);
        for (List<Kline> list1Min : partition) {
            Double high = list1Min.get(0).getHigh();
            Double low = list1Min.get(0).getLow();
            for (Kline kline : list1Min) {
                if (high <= kline.getHigh()) {
                    high = kline.getHigh();
                }
                if (low >= kline.getLow()) {
                    low = kline.getLow();
                }
            }
            int lastIndex = list1Min.size() - 1;
            Kline kline = new Kline();
            kline.setSymbol(klineConfigDTO.getSymbol());
            kline.setTs(list1Min.get(lastIndex).getTs());
            kline.setSymbol(klineConfigDTO.getSymbol());
            kline.setOpen(BigDecimal.valueOf(list1Min.get(0).getOpen()).setScale(symbol.getDecimals(), BigDecimal.ROUND_DOWN).doubleValue());
            kline.setHigh(BigDecimal.valueOf(high).setScale(symbol.getDecimals(), BigDecimal.ROUND_DOWN).doubleValue());
            kline.setLow(BigDecimal.valueOf(low).setScale(symbol.getDecimals(), BigDecimal.ROUND_DOWN).doubleValue());
            kline.setClose(BigDecimal.valueOf(list1Min.get(lastIndex).getClose()).setScale(symbol.getDecimals(), BigDecimal.ROUND_DOWN).doubleValue());
            kline.setPeriod(Kline.PERIOD_15MIN);
            // 格式化小数点位
            // klineOneTop.formatPoint(kline);
            double sumAmount = klineList.stream()
                    .map(Kline::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            double sumVolume = klineList.stream()
                    .map(Kline::getVolume)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            kline.setAmount(sumAmount);
            kline.setVolume(sumVolume);
            result.add(kline);
        }
        model.setKlineList(result);
        return Result.ok(model);
    }

    /**
     * 根据Id获取etfK线图配置表数据
     */
    @ApiOperation(value = "获取每秒k线图")
    @GetMapping("secKline")
    public Result<Realtime> secKline(String symbol) {
        return Result.ok(marketService.queryRealtime(symbol));
    }


    /**
     * 根据Id获取etfK线图配置表数据
     */
    @ApiOperation(value = "获取每秒深度")
    @GetMapping("secDepth")
    public Result<Depth> secDepth(String symbol) {
        return Result.ok(marketService.queryDepth(symbol));
    }


    /**
     * 根据Id获取etfK线图配置表数据
     */
    @ApiOperation(value = "加倍深度")
    @GetMapping("accelerate")
    public Result accelerate(@RequestParam(value = "symbol") String symbol, @RequestParam(value = "enlarge") Double
            enlarge, @RequestParam(value = "enable") Boolean enable) {
        Map<String, Double> accelerate = marketService.getAccelerate();
        if (enable) {
            accelerate.put(symbol, enlarge);
        } else {
            accelerate.remove(symbol);
        }
        return Result.succeed();
    }


}
