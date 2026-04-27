package com.yami.trading.api.controller;

import com.alibaba.fastjson2.JSON;
import com.yami.trading.bean.exchange.dto.ExchangeSymbolDto;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.RealNameAuthRecord;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.UTCDateUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.impl.ContractAndFutureProfit;
import com.yami.trading.service.user.UserDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@Api(tags = "资产")
@Slf4j
public class ApiAssetsController {

    @Autowired
    RealNameAuthRecordService realNameAuthRecordService;
    @Autowired
    WalletService walletService;
    @Autowired
    UserDataService userDataService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    ExchangeApplyOrderService exchangeApplyOrderService;

    /**
     * 交易栏 顶部数据统计
     * 20231201
     */
    @RequestMapping("/api/assetsTradeTop")
    public Result<Map<String, String>> assetsTradeTop(String symbolType, String tradeType) {
        Map<String, String> data = new HashMap<>();
        String partyId = SecurityUtils.getCurrentUserId();
        if (null == partyId) {
            throw new BusinessException("user unknown");
        }
        Map<String, Object> assetsMap = walletService.getTotalAssets(partyId, symbolType);
        // USDT余额
        data.put("usdtBalance", String.valueOf(assetsMap.get("usdtBalance")));
        // 总资产
        data.put("totalAssets", String.valueOf(assetsMap.get("totalAssets")));

        double profit;
        double profitToday;
        //累计盈亏
        double profitTotal;
        // 合约
        if ("contract".equals(tradeType)) {
            ContractAndFutureProfit contractProfit = walletService.calculateContractAndFutureProfit(partyId, symbolType);
            profit = contractProfit.getOrderProfit();
            profitToday = contractProfit.getClosedOrderProfitToday();
            profitTotal = contractProfit.getClosedOrderProfit();
        }
        // 现货 exchange
        else {
            List<ExchangeSymbolDto> exchangeList = exchangeApplyOrderService.querySpotTradPositionList(partyId, symbolType);
            profit = exchangeList.stream().mapToDouble(ExchangeSymbolDto::getProfitLoss).sum();
            profitToday = exchangeList.stream().mapToDouble(ExchangeSymbolDto::getToDayProfitLoss).sum();
            profitTotal = exchangeList.stream().mapToDouble(ExchangeSymbolDto::getProfitTotal).sum();
        }
        // 浮动盈亏
        data.put("profit", String.format("%.2f", profit));
        // 当日盈亏
        data.put("profitToday", String.format("%.2f", profitToday));
        data.put("profitTotal", String.format("%.2f", profitTotal));
        return Result.ok(data);
    }


    /**
     * 总账户资产 所有币种，订单资产转换到Usdt余额
     */
    @RequestMapping("/api/assets!getContractBySymbolType.action")
    @ApiOperation("总账户资产 所有币种，订单资产转换到Usdt余额")
    public Result<Map<String, String>> getContractBySymbolType(String symbolType) {
        Map<String, String> data = new HashMap<>();
        DecimalFormat df2 = new DecimalFormat("#.####");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);
        String partyId = SecurityUtils.getCurrentUserId();
        if ("".equals(partyId) || null == partyId) {
            // 当前外汇资产
            data.put("money_contract", df2.format(0));
            data.put("money_contract_deposit", df2.format(0));
            // 外汇浮动盈亏
            data.put("money_contract_profit", df2.format(0));
            // 当日盈亏
            data.put("money_contract_profit_today", df2.format(0));
            // 外汇可用余额
            data.put("money_wallet", df2.format(0));
        } else {
            Map<String, Object> moneyContract = walletService.getMoneyAll(partyId, symbolType);
            data.put("money_contract", df2.format(Double.parseDouble(moneyContract.get("symbol_type_asserts").toString())));
            ContractAndFutureProfit contractAndFutureProfit = walletService.calculateContractAndFutureProfit(partyId, symbolType);

//            Map<String, Double> moneyContract = walletService.getMoneyContract(partyId, symbolType);
//            data.put("money_contract", df2.format(moneyContract.get("money_contract")));
            data.put("money_contract_deposit", df2.format(Double.parseDouble(moneyContract.get("money_contract_deposit").toString())));
            // 外汇浮动盈亏
            data.put("money_contract_profit", df2.format(contractAndFutureProfit.getOrderProfit()));
            // 当日盈亏
            data.put("money_contract_profit_today", df2.format(contractAndFutureProfit.getOrderProfitToday()));
            // 外汇可用余额
            data.put("money_wallet", df2.format(Double.parseDouble(moneyContract.get("money_wallet").toString())));
        }
        return Result.ok(data);
    }

    @RequestMapping("/api/assets!getAllAggregation.action")
    @ApiOperation("总账户资产 所有币种，订单资产转换到Usdt余额")
    public Object getAllAggregation() {
        ResultObject resultObject = new ResultObject();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            List<String> types = Item.types;
            for (String type : types) {
                data.put(type, getAssert(type));
            }
            data.put("all", getAssert(""));
            resultObject.setData(data);
            return resultObject;


        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * COM盘（首页统计模板）定制需求 下盘后删除
     */
    @RequestMapping("/api/assets!sum.action")
    @ApiOperation("总账户资产 所有币种，订单资产转换到Usdt余额,今日盈亏，余额变化")
    public Object sum() {
        ResultObject resultObject = new ResultObject();
        try {
            String partyId = SecurityUtils.getCurrentUserId();
            if (StringUtils.isNotEmpty(partyId)) {
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> anAssert = getAssert("");
                data.put("all", anAssert);
                BigDecimal balance = walletService.findByUserId(partyId).getMoney();
                MoneyLog moneyLog = moneyLogService.getMoneyLogByUserId(partyId);
                log.info("COM盘（首页统计模板）定制需求 moneyLog:{}", JSON.toJSONString(moneyLog));
                BigDecimal yesterdayBalance = BigDecimal.ZERO;
                if (null != moneyLog) {
                    yesterdayBalance = moneyLog.getAmountAfter();
                }
                if (yesterdayBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    data.put("balanceChange", "0.00");
                } else {
                    data.put("balanceChange", balance.subtract(yesterdayBalance).divide(yesterdayBalance, 2, RoundingMode.HALF_DOWN));
                }

                ContractAndFutureProfit contractAndFutureProfit = walletService.calculateContractAndFutureProfit(partyId, null);
                List<ExchangeSymbolDto> exchangeSymbolDtos = exchangeApplyOrderService.querySpotTradPositionList(partyId);
                double orderProfitTotal = contractAndFutureProfit.getOrderProfitTotal();
                double orderProfitToday = contractAndFutureProfit.getOrderProfitToday();
                //现货今日盈亏（结算）
                double todayProfit = exchangeSymbolDtos.stream().mapToDouble(ExchangeSymbolDto::getTodayProfit).sum();
                contractAndFutureProfit.setClosedOrderProfitToday(Arith.add(orderProfitTotal, todayProfit));

                //现货总盈亏
                double profitTotal = exchangeSymbolDtos.stream().mapToDouble(ExchangeSymbolDto::getProfitTotal).sum();
                contractAndFutureProfit.setClosedOrderProfit(Arith.add(orderProfitToday, profitTotal));
                data.put("profitObject", contractAndFutureProfit);
                resultObject.setData(data);
                return resultObject;
            }
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 总账户资产 所有币种，订单资产转换到Usdt余额
     */
    @RequestMapping("/api/assets!getAll.action")
    @ApiOperation("总账户资产 所有币种，订单资产转换到Usdt余额")
    public Object getAll(@RequestParam(required = false) String symbolType) {
        ResultObject resultObject = new ResultObject();

        Map<String, Object> data = new HashMap<>();
        DecimalFormat df2 = new DecimalFormat("#.##");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);

        String partyId = SecurityUtils.getCurrentUserId();
        try {

            if ("".equals(partyId) || null == partyId) {

                data.put("total", df2.format(0));
                data.put("lock_money", df2.format(0));
                //冻结金额
                data.put("freeze_money", df2.format(0));
                data.put("money_wallet", df2.format(0));
                data.put("money_coin", df2.format(0));
                data.put("money_all_coin", df2.format(0));
                data.put("money_miner", df2.format(0));
                data.put("money_finance", df2.format(0));
                data.put("money_contract", df2.format(0));
                data.put("money_contract_deposit", df2.format(0));
                data.put("money_contract_profit", df2.format(0));
                data.put("money_futures", df2.format(0));
                data.put("money_futures_profit", df2.format(0));

            } else {
                if (StringUtils.isNotEmpty(symbolType)) {
                    data = walletService.getMoneyAll(partyId, symbolType);
                } else {
                    data = walletService.getMoneyAll(partyId);
                }
            }

            RealNameAuthRecord kyc = realNameAuthRecordService.getByUserId(partyId);
            data.put("status", kyc == null ? 0 : kyc.getStatus());
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }

        return resultObject;
    }

    public Map<String, Object> getAssert(String symbolType) {
        Map<String, Object> data = new HashMap<String, Object>();
        DecimalFormat df2 = new DecimalFormat("#.##");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);

        String partyId = SecurityUtils.getCurrentUserId();
        if ("".equals(partyId) || null == partyId) {
            data.put("total", df2.format(0));
            data.put("lock_money", df2.format(0));
            //冻结金额
            data.put("freeze_money", df2.format(0));
            data.put("money_wallet", df2.format(0));
            data.put("money_coin", df2.format(0));
            data.put("money_all_coin", df2.format(0));
            data.put("money_miner", df2.format(0));
            data.put("money_finance", df2.format(0));
            data.put("money_contract", df2.format(0));
            data.put("money_contract_deposit", df2.format(0));
            data.put("money_contract_profit", df2.format(0));
            data.put("money_futures", df2.format(0));
            data.put("money_futures_profit", df2.format(0));

        } else {
            if (StringUtils.isNotEmpty(symbolType)) {
                data = walletService.getMoneyAll(partyId, symbolType);
            } else {
                data = walletService.getMoneyAll(partyId);
            }
        }

        RealNameAuthRecord kyc = realNameAuthRecordService.getByUserId(partyId);
        data.put("status", kyc == null ? 0 : kyc.getStatus());
        return data;

    }


    /**
     * 获取当前是否休市
     */
    @RequestMapping("/api/assets!isClosed.action")
    public Result<Map<String, Object>> isClosed() throws IOException, ParseException {
        Map<String, Object> data = new HashMap<String, Object>();

        // TODO
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = f.parse(f.format(new Date()));
//        if (now.before(UTCDateUtils.getClosedTime()) && now.after(UTCDateUtils.getOpenTime())) {
//            data.put("isClosed", "false");
//        } else {
//            // 休市
//            data.put("isClosed", "true");
//        }
        return Result.ok(data);
    }

    @RequestMapping("/api/assets!getTime.action")
    public Result<Map<String, Object>> getTime() throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        Date date = new Date();
        data.put("time", date.toGMTString());

        // TODO
        data.put("time2", System.currentTimeMillis());
        return Result.ok(data);
    }


}
