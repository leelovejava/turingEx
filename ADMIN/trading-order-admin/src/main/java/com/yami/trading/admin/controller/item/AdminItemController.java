package com.yami.trading.admin.controller.item;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemConfig;
import com.yami.trading.bean.item.dto.ItemDTO;
import com.yami.trading.bean.item.mapstruct.ItemWrapper;
import com.yami.trading.bean.item.query.ItemQuery;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.query.QueryWrapperGenerator;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.MessageFormat;


/**
 * 产品Controller
 *
 * @author lucas
 * @version 2023-03-10
 */

@Api(tags = "永续合约管理和行情品种")
@RestController
@CrossOrigin
@RequestMapping(value = "normal/adminItemAction!")
public class AdminItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemWrapper itemWrapper;
    @Autowired
    private UserService userService;

    @Autowired
    SysUserService sysUserService;
    @Autowired
    private SysUserOperService sysUserOperService;

    /**
     * 产品列表数据
     */
    @ApiOperation(value = "永续合约列表，配置列表")
    @GetMapping("list")
    public Result<IPage<ItemDTO>> list(ItemQuery itemQuery, Page<ItemDTO> page) throws Exception {
        QueryWrapper queryWrapper = QueryWrapperGenerator.buildQueryCondition(itemQuery, ItemQuery.class);
        IPage<ItemDTO> result = itemService.findPage(page, queryWrapper);
        return Result.ok(result);
    }

    @ApiOperation(value = "设置前端显示状态,返回修改后的状态")
    @GetMapping("setShowStatus")
    public Result<String> setShowStatus(String symbol, String showStatus) throws Exception {
        Item bySymbol = itemService.getOne(new LambdaQueryWrapper<Item>().eq(Item::getSymbol, symbol));
        if (bySymbol == null) {
            throw new YamiShopBindException(symbol + "不存在");
        }
//		Item update = new Item();
//		update.setUuid(bySymbol.getUuid());
//		update.setShowStatus(showStatus);
        bySymbol.setShowStatus(showStatus);
        itemService.updateById(bySymbol);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("将 {}前端显示状态修改为 {}", symbol, showStatus));
        return Result.ok(showStatus);
    }

    @ApiOperation(value = "设置交易状态, 返回修改后的状态")
    @GetMapping("setTradeStatus")
    public Result<String> setTradeStatus(String symbol, String tradeStatus) throws Exception {
        Item bySymbol = itemService.getOne(new LambdaQueryWrapper<Item>().eq(Item::getSymbol, symbol));
        if (bySymbol == null) {
            throw new YamiShopBindException(symbol + "不存在");
        }
//		Item update = new Item();
//		update.setUuid(bySymbol.getUuid());
//		update.setTradeStatus(tradeStatus);
        bySymbol.setTradeStatus(tradeStatus);
        itemService.updateById(bySymbol);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("将 {}交易状态显示修改为 {}", symbol, tradeStatus));
        return Result.ok(tradeStatus);
    }

    /**
     * 根据Id获取产品数据
     */
    @ApiOperation(value = "根据Id获取产品数据")
    @GetMapping("queryById")
    public Result<ItemDTO> queryById(String id) {
        return Result.ok(itemService.findById(id));
    }

    /**
     * 保存产品
     */
    @ApiOperation(value = "保存产品")
    @PostMapping("save")
    public Result<String> save(@Valid @RequestBody Item item) {
        //新增或编辑表单保存
        if (StrUtil.isBlank(item.getSorted())) {
            item.setSorted("999999");
        }
        try {
            item.setSorted(item.getSorted().trim());
            Integer.parseInt(item.getSorted());
        } catch (Exception e) {
            throw new YamiShopBindException("排序值不正确");
        }

        itemService.saveOrUpdate(item);
        return Result.ok("保存产品成功");
    }

    /**
     * 保存产品
     */
    @ApiOperation(value = "保存配置")
    @PostMapping("addConfig.action")
    public Result<String> addConfig(@Valid @RequestBody ItemConfig itemConfig) {
        //userService.checkLoginSafeword(SecurityUtils.getSysUser().getUserId().toString(), itemConfig.getLoginSafeword());
        if (StrUtil.isNotBlank(itemConfig.getUuid())) {
            throw new YamiShopBindException("新增配置不要传入uuid");
        }
        Item bySymbol = itemService.findBySymbol(itemConfig.getSymbol());
        if (bySymbol != null) {
            throw new YamiShopBindException("代码已经存在");
        }
        Item item = itemWrapper.toEntity(itemConfig);
        //新增或编辑表单保存
        itemService.save(item);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("更新了 {} 行情品种配置", itemConfig.getSymbol()));
        // todo log
        return Result.ok("保存行情品种成功");
    }


    /**
     * 保存永续合约
     */
    @ApiOperation(value = "保存永续合约")
    @PostMapping("add.action")
    public Result<String> addConfig(@Valid @RequestBody ItemDTO itemDTO) {
        //userService.checkLoginSafeword(SecurityUtils.getSysUser().getUserId().toString(), itemDTO.getLoginSafeword());
        if (StrUtil.isNotBlank(itemDTO.getUuid())) {
            throw new YamiShopBindException("新增配置不要传入uuid");
        }
        Item bySymbol = itemService.findBySymbol(itemDTO.getSymbol());
        if (bySymbol != null) {
            throw new YamiShopBindException("代码已经存在");
        }
        Item item = itemWrapper.toEntity(itemDTO);
        //新增或编辑表单保存
        itemService.save(item);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("新增{}的永续合约成功 ", itemDTO.getSymbol()));
        // todo log
        return Result.ok("保存永续合约成功");
    }

    /**
     * 保存产品
     */
    @ApiOperation(value = "更新产品/修改交易对/修改状态")
    @PostMapping("update.action")
    public Result<String> updateConfig(@Valid @RequestBody ItemDTO itemDTO) {
        //	userService.checkLoginSafeword(SecurityUtils.getSysUser().getUserId().toString(), itemDTO.getLoginSafeword());
        if (StrUtil.isBlank(itemDTO.getUuid())) {
            throw new YamiShopBindException("更新数据时候uuid不能为空");
        }
        Item byId = itemService.getById(itemDTO.getUuid());
        if (byId == null) {
            throw new YamiShopBindException("更新永续合约不存在");
        }
        String symbol = itemDTO.getSymbol();

        // 新老代码不等
        if (!byId.getSymbol().equalsIgnoreCase(symbol)) {
            // 且老的名称已经存在
            Item bySymbol = itemService.findBySymbol(symbol);
            if (bySymbol != null) {
                throw new YamiShopBindException("被更新的永续合约已经存在");
            }
        }

        sysUserService.checkSafeWord(itemDTO.getLoginSafeword());
        Integer decimals = itemDTO.getDecimals();
        if (decimals != null) {
            byId.setDecimals(decimals);
        }
        BigDecimal pips = itemDTO.getPips();
        if (pips != null) {
            byId.setPips(pips.doubleValue());
        }
        BigDecimal pipsAmount = itemDTO.getPipsAmount();
        if (pipsAmount != null) {
            byId.setPipsAmount(pipsAmount.doubleValue());
        }
        BigDecimal unitAmount = itemDTO.getUnitAmount();
        if (unitAmount != null) {
            byId.setUnitAmount(unitAmount.doubleValue());
        }
        Double unitFee = itemDTO.getUnitFee();
        if (unitFee != null) {
            byId.setUnitFee(unitFee);
        }
        BigDecimal unitPercentage = itemDTO.getUnitPercentage();
        if (unitPercentage != null) {
            byId.setUnitPercentage(unitPercentage.doubleValue());
        }
        BigDecimal borrowingRate = itemDTO.getBorrowingRate();
        if (borrowingRate != null) {
            byId.setBorrowingRate(borrowingRate.doubleValue());
        }
        BigDecimal multiple = itemDTO.getMultiple();
        if (multiple != null) {
            byId.setMultiple(multiple.doubleValue());
        }
        String name = itemDTO.getName();
        if (StringUtils.isNotEmpty(name)) {
            byId.setName(name);
        }
        String canBuyAtMarketPrice = itemDTO.getCanBuyAtMarketPrice();
        if (StringUtils.isNotEmpty(canBuyAtMarketPrice)) {
            byId.setCanBuyAtMarketPrice(canBuyAtMarketPrice);
        }
        String canSellAtMarketPrice = itemDTO.getCanSellAtMarketPrice();
        if (StringUtils.isNotEmpty(canSellAtMarketPrice)) {
            byId.setCanSellAtMarketPrice(canSellAtMarketPrice);
        }
        String enable = itemDTO.getEnable();
        if (StringUtils.isNotEmpty(enable)) {
            byId.setEnable(enable);
        }
        String enName = itemDTO.getEnName();
        if (StringUtils.isNotEmpty(enName)) {
            byId.setEnName(enName);
        }
        String limitCanBuy = itemDTO.getLimitCanBuy();
        if (StringUtils.isNotEmpty(limitCanBuy)) {
            byId.setLimitCanBuy(limitCanBuy);
        }
        String limitCanSell = itemDTO.getLimitCanSell();
        if (StringUtils.isNotEmpty(limitCanSell)) {
            byId.setLimitCanSell(limitCanSell);
        }
        String quoteCurrency = itemDTO.getQuoteCurrency();
        if (StringUtils.isNotEmpty(quoteCurrency)) {
            byId.setQuoteCurrency(quoteCurrency);
        }
        String showStatus = itemDTO.getShowStatus();
        if (StringUtils.isNotEmpty(showStatus)) {
            byId.setShowStatus(showStatus);
        }
        String sorted = itemDTO.getSorted();
        if (StringUtils.isNotEmpty(sorted)) {
            byId.setSorted(sorted);
        }
        String symbolData = itemDTO.getSymbolData();
        if (StringUtils.isNotEmpty(symbolData)) {
            byId.setSymbolData(symbolData);
        }
        String symbolFullName = itemDTO.getSymbolFullName();
        if (StringUtils.isNotEmpty(symbolFullName)) {
            byId.setSymbolFullName(symbolFullName);
        }
        String tradeStatus = itemDTO.getTradeStatus();
        if (StringUtils.isNotEmpty(tradeStatus)) {
            byId.setTradeStatus(tradeStatus);
        }
        //新增或编辑表单保存
        itemService.saveOrUpdate(byId);

        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr() +
                        ",永续合约配置进行了修改,id:{0}, 原名称:{1},原保留精度:{2},原金额:{3},原合约手续费:{4},原手续费:{5},原最小变动单位:{6},原最小变动单位的单位盈亏{7}," +
                        "原名称:{8},原保留精度:{9},新金额:{10},新合约手续费:{11},新手续费:{12},新最小变动单位:{13},新最小变动单位的单位盈亏{14}",
                byId.getUuid(), byId.getName(), byId.getDecimals(), byId.getUnitAmount(), byId.getUnitPercentage(), byId.getUnitFee(), byId.getPips(), byId.getPipsAmount(),
                name, decimals, unitAmount, unitPercentage, unitFee, pips, pipsAmount);

        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), log);
        return Result.ok("保存行情永续合约成功");
    }

    /**
     * 保存产品
     */
    @ApiOperation(value = "更新配置")
    @PostMapping("updateConfig.action")
    public Result<String> updateConfig(@Valid @RequestBody ItemConfig itemConfig) {
        //userService.checkLoginSafeword(SecurityUtils.getSysUser().getUserId().toString(), itemConfig.getLoginSafeword());
        if (StrUtil.isBlank(itemConfig.getUuid())) {
            throw new YamiShopBindException("更新数据时候uuid不能为空");
        }
        Item byId = itemService.getById(itemConfig.getUuid());
        String symbol = itemConfig.getSymbol();
        if (byId == null) {
            throw new YamiShopBindException("更新品种不存在");
        }
        // 新老代码不等
        if (!byId.getSymbol().equalsIgnoreCase(symbol)) {
            // 且老的名称已经存在
            Item bySymbol = itemService.findBySymbol(symbol);
            if (bySymbol != null) {
                throw new YamiShopBindException("被更新的品种已经存在");
            }
        }
        Double borrowingRate = itemConfig.getBorrowingRate();
        if (borrowingRate != null) {
            byId.setBorrowingRate(borrowingRate);
        }
        Integer decimals = itemConfig.getDecimals();
        if (decimals != null) {
            byId.setDecimals(decimals);
        }
        Double multiple = itemConfig.getMultiple();
        if (multiple != null) {
            byId.setMultiple(multiple);
        }
        String name = itemConfig.getName();
        if (StringUtils.isNotEmpty(name)) {
            byId.setName(name);
        }
        String symbolData = itemConfig.getSymbolData();
        if (StringUtils.isNotEmpty(symbolData)) {
            byId.setSymbolData(symbolData);
        }

        //新增或编辑表单保存
        itemService.saveOrUpdate(byId);
        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("对{}行情品种配置进行了修改", symbol));
        // todo log
        return Result.ok("保存行情品种成功");
    }

    /**
     * 删除产品
     */
    @ApiOperation(value = "删除产品")
    @DeleteMapping("delete")
    public Result<String> delete(String ids) {
        String idArray[] = ids.split(",");
        itemService.removeByIds(Lists.newArrayList(idArray));
        return Result.ok("删除产品成功");
    }

}
