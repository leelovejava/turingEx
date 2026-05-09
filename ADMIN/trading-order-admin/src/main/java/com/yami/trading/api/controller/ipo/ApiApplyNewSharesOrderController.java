package com.yami.trading.api.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.controller.ipo.dto.NewSharesDto;
import com.yami.trading.api.controller.ipo.model.ApplyListModel;
import com.yami.trading.api.controller.ipo.model.ApplyNewSharesOrderModel;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.BaseEntity;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/sNewSharesOrder")
@Api(tags = "申购订单")
@Slf4j
public class ApiApplyNewSharesOrderController {
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    DataService dataService;
    @Autowired
    ItemService itemService;
    @Autowired
    UserPromiseRecordService userPromiseRecordService;

    @Autowired
    SysparaService sysparaService;

    @ApiOperation(value = "一键抽签")
    @PostMapping("apply")
    public Result apply(ApplyNewSharesOrderModel model) {
        String userId = SecurityUtils.getCurrentUserId();
        applyNewSharesOrderService.apply(model.getAmount(), userId, model.getCode());
        return Result.succeed();
    }

    @ApiOperation("获取订单列表")
    @PostMapping("list")
    public Result list(ApplyListModel model, HttpServletRequest request) {
        int type = model.getType();
        String language = request.getParameter("language");
        List<String> symbols = null;
        if (StrUtil.isNotEmpty(model.getSymbolType())) {
            symbols = itemService.findDBByType(model.getSymbolType()).stream().map(Item::getSymbol).collect(Collectors.toList());
            symbols.add("-1");
        }
        //  1 抽签记录
        if (type == 1) {
            Page page=  getApplyOrder(model, request);;
            Result result=   Result.succeed(page.getRecords());
            result.setTotal(page.getTotal());
            return result;
        } else if (type == 2) { //2 新股库存
            List<NewSharesDto> newSharesDtos = new ArrayList<>();
            LambdaQueryWrapper<UserPromiseRecord> lambdaQueryWrapper = Wrappers.<UserPromiseRecord>query().lambda()
                    .eq(UserPromiseRecord::getShowFlag, 0)
                    .eq(UserPromiseRecord::getUserId, SecurityUtils.getCurrentUserId());
            if (StrUtil.isNotEmpty(model.getSymbolType())) {
                lambdaQueryWrapper.in(UserPromiseRecord::getProductCode, symbols);
            }
            Page<UserPromiseRecord> page = new Page<UserPromiseRecord>(model.getCurrent(), model.getSize());
            Syspara syspara=sysparaService.find("new_shares_order_show_day");
            if (syspara!=null){
                Integer day= syspara.getInteger();
                Calendar calendar=  Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR,day);
                lambdaQueryWrapper.ge(BaseEntity::getCreateTime,calendar.getTime());
            }
            lambdaQueryWrapper.orderByDesc(BaseEntity::getCreateTime);
                userPromiseRecordService.
                    page(page,lambdaQueryWrapper);
            for (UserPromiseRecord userPromiseRecord : page.getRecords()) {
                NewSharesDto newSharesDto = new NewSharesDto();
                newSharesDto.setStatus(userPromiseRecord.getStatus());
                newSharesDto.setSymbolName(userPromiseRecord.getName());
                newSharesDto.setSymbolCode(userPromiseRecord.getProductCode());
                Item item = itemService.findBySymbol(newSharesDto.getSymbolCode());
                if (item != null) {
                    newSharesDto.setSymbolType(item.getType());
                    newSharesDto.setSymbolName(item.getName());
                }
                ApplyNewSharesOrder applyNewSharesOrder = applyNewSharesOrderService.findByOrderNo(userPromiseRecord.getOrderNo());
                if (applyNewSharesOrder != null) {
                    newSharesDto.setOrderNo(applyNewSharesOrder.getOrderNo());
                    newSharesDto.setSubPrice(applyNewSharesOrder.getSubPrice());
                    newSharesDto.setSubNumber(applyNewSharesOrder.getSubNumber());
                    newSharesDto.setRequiredSubscribe(applyNewSharesOrder.getRequiredSubscribe());
                    newSharesDto.setRequiredNumber(applyNewSharesOrder.getRequiredNumber());
                    newSharesDto.setWinningNumber(applyNewSharesOrder.getWinningNumber());
                    newSharesDto.setRequiredNumber(applyNewSharesOrder.getRequiredNumber());
                }
                newSharesDtos.add(newSharesDto);
            }
            Result result=   Result.succeed(newSharesDtos);
            result.setTotal(page.getTotal());
            return result;
        } else if (type == 3) {// 3 现股库存
            List<NewSharesDto> newSharesDtos = new ArrayList<>();
         LambdaQueryWrapper<UserPromiseRecord>  lambdaQueryWrapper=   Wrappers.<UserPromiseRecord>query()
                    .lambda().eq(UserPromiseRecord::getShowFlag, 0).eq(UserPromiseRecord::getUserId, SecurityUtils.getCurrentUserId());
            lambdaQueryWrapper .orderByDesc(BaseEntity::getCreateTime);
            if (StrUtil.isNotEmpty(model.getSymbolType())) {
                lambdaQueryWrapper.in(UserPromiseRecord::getProductCode, symbols);
            }
            Syspara syspara=sysparaService.find("new_shares_order_show_day");
            if (syspara!=null){
                Integer day= syspara.getInteger();
                Calendar calendar=  Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR,day);
                lambdaQueryWrapper.ge(BaseEntity::getCreateTime,calendar.getTime());
            }

            Page<UserPromiseRecord> page = new Page<UserPromiseRecord>(model.getCurrent(), model.getSize());
            userPromiseRecordService.page(page,lambdaQueryWrapper);
            List<UserPromiseRecord> newList = new ArrayList<>();
            Map<String, UserPromiseRecord> map = new HashMap<>();
            for (UserPromiseRecord userPromiseRecord : page.getRecords()) {
                UserPromiseRecord promiseRecord = map.get(userPromiseRecord.getProductCode());
                if (promiseRecord == null) {
                    map.put(userPromiseRecord.getProductCode(), userPromiseRecord);
                } else {
                    promiseRecord.setDeductNumber(promiseRecord.getDeductNumber().add(userPromiseRecord.getDeductNumber()));
                }
            }
            for (String key : map.keySet()) {
                newList.add(map.get(key));
            }
            for (UserPromiseRecord userPromiseRecord : newList) {
                NewSharesDto newSharesDto = new NewSharesDto();
                newSharesDto.setStatus(userPromiseRecord.getStatus());
                newSharesDto.setSymbolName(userPromiseRecord.getName());
                newSharesDto.setSymbolCode(userPromiseRecord.getProductCode());
                ApplyNewSharesOrder applyNewSharesOrder = applyNewSharesOrderService.
                        findByOrderNo(userPromiseRecord.getOrderNo());
                if (applyNewSharesOrder != null) {
                    newSharesDto.setOrderNo(applyNewSharesOrder.getOrderNo());
                    newSharesDto.setSubPrice(applyNewSharesOrder.getSubPrice());
                    newSharesDto.setSubNumber(userPromiseRecord.getDeductNumber());
                    newSharesDto.setRequiredSubscribe(applyNewSharesOrder.getRequiredSubscribe());
                    newSharesDto.setRequiredNumber(applyNewSharesOrder.getRequiredNumber());
                    newSharesDto.setWinningNumber(newSharesDto.getWinningNumber());
                    newSharesDto.setRequiredNumber(applyNewSharesOrder.getRequiredNumber());
                    List<Realtime> realtimes = dataService.realtime(newSharesDto.getSymbolCode());
                    BigDecimal initMarketValue = newSharesDto.getSubNumber().multiply(newSharesDto.getSubPrice());
                    BigDecimal inventoryGainsLosses = new BigDecimal(0);
                    Item item = itemService.findBySymbol(newSharesDto.getSymbolCode());
                    if (item != null) {
                        newSharesDto.setSymbolType(item.getType());
                        newSharesDto.setSymbolName(item.getName());
                    }
                    if (CollectionUtil.isNotEmpty(realtimes)) {
                        Realtime realtime = realtimes.get(0);
                        BigDecimal newMarketValue = newSharesDto.getSubNumber().multiply(BigDecimal.valueOf(realtime.getClose()));
                        inventoryGainsLosses = newMarketValue.subtract(initMarketValue);
                        newSharesDto.setMarketValue(newMarketValue.doubleValue());
                        newSharesDto.setClosePrice(BigDecimal.valueOf(realtime.getClose()));
                        newSharesDto.setInventoryGainsLossesValue(calculateProfitPercentage(newSharesDto.getSubPrice().doubleValue(), realtime.getClose()));
                    } else {
                        newSharesDto.setMarketValue(initMarketValue.doubleValue());
                    }
                    newSharesDto.setInventoryGainsLosses(inventoryGainsLosses.doubleValue());
                    newSharesDtos.add(newSharesDto);
                }
            }
            Result result=   Result.succeed(newSharesDtos);
            result.setTotal(page.getTotal());
            return result;
        }
        return Result.succeed();
    }

    public Page getApplyOrder(ApplyListModel model, HttpServletRequest request) {
        LambdaQueryWrapper<ApplyNewSharesOrder> lambdaQueryWrapper = Wrappers.<ApplyNewSharesOrder>query().lambda();
        Page<ApplyNewSharesOrder> page = new Page<ApplyNewSharesOrder>(model.getCurrent(), model.getSize());
        lambdaQueryWrapper.eq(ApplyNewSharesOrder::getUserId, SecurityUtils.getCurrentUserId());
        lambdaQueryWrapper.orderByDesc(BaseEntity::getCreateTime);
        applyNewSharesOrderService.page(page, lambdaQueryWrapper);
        String language = request.getParameter("language");
        for (ApplyNewSharesOrder order : page.getRecords()) {
            List<Realtime> realtimes = dataService.realtime(order.getSymbolCode());
            BigDecimal initMarketValue = order.getWinningNumber().multiply(order.getSubPrice());
            BigDecimal inventoryGainsLosses = new BigDecimal(0);
            Item item = itemService.findBySymbol(order.getSymbolCode());
            if (item != null) {
                order.setSymbolType(item.getType());
                order.setSymbolName(item.getName());
            }
            if (CollectionUtil.isNotEmpty(realtimes)) {
                Realtime realtime = realtimes.get(0);
                BigDecimal newMarketValue = order.getWinningNumber().multiply(BigDecimal.valueOf(realtime.getClose()));
                inventoryGainsLosses = newMarketValue.subtract(initMarketValue);
                order.setMarketValue(newMarketValue.doubleValue());
                order.setClosePrice(BigDecimal.valueOf(realtime.getClose()));
                order.setInventoryGainsLossesValue(calculateProfitPercentage(order.getSubPrice().doubleValue(), realtime.getClose()));
            } else {
                order.setMarketValue(initMarketValue.doubleValue());
            }
            order.setInventoryGainsLosses(inventoryGainsLosses.doubleValue());
        }
        return page;
    }

    public static double calculateProfitPercentage(double buyPrice, double currentPrice) {
        double profit = currentPrice - buyPrice;
        double profitPercentage = (profit / buyPrice) * 100;
        System.out.println(profitPercentage);
        return new BigDecimal(profitPercentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
