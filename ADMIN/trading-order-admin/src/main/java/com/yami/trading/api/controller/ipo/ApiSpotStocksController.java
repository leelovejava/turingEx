package com.yami.trading.api.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.controller.ipo.dto.CurrentSharesDto;
import com.yami.trading.api.controller.ipo.dto.SpotStocksDataDto;
import com.yami.trading.api.controller.ipo.model.ApplyListModel;
import com.yami.trading.api.controller.ipo.model.GetTopDataModel;
import com.yami.trading.api.controller.ipo.model.SellModel;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.BaseEntity;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("api/spotStock")
@Api(tags = "现股库存")
@Slf4j
public class ApiSpotStocksController {
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;

    @Autowired
    ExchangeApplyOrderService exchangeApplyOrderService;

    @Autowired
    SysparaService sysparaService;
    @Autowired
    DataService dataService;

    @Autowired
    UserPromiseRecordService userPromiseRecordService;

    @Autowired
    UserService userService;

    @ApiOperation("获取top数据")
    @PostMapping("getTopData")
    public Result<SpotStocksDataDto> getTopData(GetTopDataModel model) {

        List<ApplyNewSharesOrder> list = new ArrayList<>();
        //1 抽签记录 2 新股库存  3 现股库存
        if (model.getType() == 1) { //抽签
            list = applyNewSharesOrderService.list(Wrappers.<ApplyNewSharesOrder>
                    query().lambda().eq(ApplyNewSharesOrder::getUserId, SecurityUtils.getCurrentUserId()));
        } else if (model.getType() == 2) {// 新股库存
            List<UserPromiseRecord> userPromiseRecords = userPromiseRecordService.list(Wrappers.<UserPromiseRecord>query()
                    .lambda().eq(UserPromiseRecord::getShowFlag, 0).eq(UserPromiseRecord::getUserId,SecurityUtils.getCurrentUserId()));
            for (UserPromiseRecord userPromiseRecord : userPromiseRecords) {
                ApplyNewSharesOrder order = new ApplyNewSharesOrder();
                ApplyNewSharesOrder applyNewSharesOrder = applyNewSharesOrderService.findByOrderNo(userPromiseRecord.getOrderNo());
                if (applyNewSharesOrder != null) {
                    order.setSymbolCode(userPromiseRecord.getProductCode());
                    order.setSubPrice(applyNewSharesOrder.getSubPrice());
                    order.setWinningNumber(applyNewSharesOrder.getWinningNumber());
                    order.setSubNumber(applyNewSharesOrder.getWinningNumber());
                    list.add(order);
                }
            }
        } else { //现股
            List<UserPromiseRecord> userPromiseRecords = userPromiseRecordService.list(Wrappers.<UserPromiseRecord>query()
                    .lambda().eq(UserPromiseRecord::getShowFlag, 0).eq(UserPromiseRecord::getUserId,SecurityUtils.getCurrentUserId()));
            for (UserPromiseRecord userPromiseRecord : userPromiseRecords) {
                ApplyNewSharesOrder order = new ApplyNewSharesOrder();
                ApplyNewSharesOrder applyNewSharesOrder = applyNewSharesOrderService.findByOrderNo(userPromiseRecord.getOrderNo());
                if (applyNewSharesOrder != null) {
                    if (applyNewSharesOrder.getSell()!=1){
                        order.setSymbolCode(userPromiseRecord.getProductCode());
                        order.setSubPrice(applyNewSharesOrder.getSubPrice());
                        order.setWinningNumber(applyNewSharesOrder.getWinningNumber());
                        order.setSubNumber(applyNewSharesOrder.getWinningNumber());
                        list.add(order);
                    }
                }
            }
        }

        SpotStocksDataDto sumSpotStockDto = new SpotStocksDataDto();
        BigDecimal marketValue = new BigDecimal(0);  //市值
        BigDecimal inventoryGainsLosses = new BigDecimal(0); //库存损益
        BigDecimal availableLimit = new BigDecimal(0); //可用额度
        for (ApplyNewSharesOrder order : list) {
            if (model.getType() == 1) {
                BigDecimal initMarketValue = order.getSubNumber().multiply(order.getSubPrice());
                marketValue = marketValue.add(initMarketValue);
                availableLimit = availableLimit.add(order.getSubNumber());
            } else {
                List<Realtime> realtimes = dataService.realtime(order.getSymbolCode());
                log.info(order.getSymbolCode()+"获取行情数据失败");
                BigDecimal initMarketValue = order.getWinningNumber().multiply(order.getSubPrice());
                if (CollectionUtil.isNotEmpty(realtimes)) {
                    Realtime realtime = realtimes.get(0);
                    BigDecimal newMarketValue = order.getWinningNumber().multiply(BigDecimal.valueOf(realtime.getClose()));
                    marketValue = marketValue.add(newMarketValue);
                    availableLimit = availableLimit.add(order.getWinningNumber());
                    inventoryGainsLosses = inventoryGainsLosses.add(newMarketValue.subtract(initMarketValue));
                } else {
                    marketValue = marketValue.add(initMarketValue);
                    availableLimit = availableLimit.add(order.getSubNumber());
                }
            }
        }

        sumSpotStockDto.setAvailableLimit(availableLimit.doubleValue());
        sumSpotStockDto.setMarketValue(marketValue.doubleValue());
        sumSpotStockDto.setInventoryGainsLosses(inventoryGainsLosses.doubleValue());
        return Result.succeed(sumSpotStockDto);
    }

    @ApiOperation("卖出")
    @PostMapping("sell")
    public Result sell(SellModel model) {
        userPromiseRecordService.sell(model.getOrderNo(), SecurityUtils.getCurrentUserId());
        return Result.succeed();
    }

}
