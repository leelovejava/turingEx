package com.yami.trading.api.controller.exchange;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeSymbolDto;
import com.yami.trading.bean.exchange.dto.SumEtfDto;
import com.yami.trading.bean.exchange.dto.TodayTransactionDto;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.purchasing.dto.ExchangeLock;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.impl.ContractAndFutureProfit;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 现货交易
 */
@RestController
@CrossOrigin
@Slf4j
@Api(tags = "现货交易API")
public class ApiExchangeApplyOrderController {
    private final String action = "/api/exchangeapplyorder!";
    @Autowired
    SessionTokenService sessionTokenService;
    @Autowired
    UserService userService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    DataService dataService;
    @Autowired
    ExchangeApplyOrderService exchangeApplyOrderService;
    @Autowired
    WalletService walletService;
    @Autowired
    RealNameAuthRecordService realNameAuthRecordService;
    @Autowired
    ItemService itemService;
    @Autowired
    private ExchangeRateService exchangeRateService;

    private double getRealtimePrice(String symbol) {
        List<Realtime> realtimes = dataService.realtime(symbol);
        double close = 1;
        if (realtimes != null && realtimes.size() > 0) {
            close = realtimes.get(0).getClose();
        } else {
// 参数错误
            throw new YamiShopBindException("Parameter error");
        }
        return close;
    }

    /**
     * 首次进入页面，传递session_token
     */
    @RequestMapping(action + "view.action")
    public Result view() {
        String partyId = SecurityUtils.getCurrentUserId();
        Map<String, Object> session = new HashMap<>();
        String session_token = sessionTokenService.savePut(partyId);
        session.put("session_token", session_token);
        return Result.succeed(session);
    }


    @PostMapping(action + "todayTransaction")
    @ApiOperation("当日成交汇总")
    public Result todayTransaction(String symbol,String startDate,String  endDate) {
        Date now=new Date();
        Date startStrDate=now;
        Date endStrDate=now;
        if (StringUtils.isNotEmpty(startDate)){
            startStrDate = DateUtil.stringToDate(startDate,"yyyy-MM-dd");
            endStrDate=DateUtil.stringToDate(endDate,"yyyy-MM-dd");
        }
        log.info(startDate+"===="+endDate);
        log.info(startStrDate+"===="+endStrDate);
        List<TodayTransactionDto>  list=  exchangeApplyOrderService.getTodayTransaction(symbol,SecurityUtils.getCurrentUserId(),startStrDate,
                endStrDate);
        for (TodayTransactionDto dto:list){
            String symbolName = itemService.findBySymbol(dto.getSymbol()).getName();
            dto.setSymbolName(symbolName);
        }
        return Result.succeed(list);
    }


    /**
     * 兑换记录
     * 委托单记录
     */
    @RequestMapping(action + "list.action")
    public Result list(HttpServletRequest request) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        String symbol = request.getParameter("symbol");
        String type = request.getParameter("type");
        String isAll = request.getParameter("isAll");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String symbolType = request.getParameter("symbolType");
        String orderPriceType = request.getParameter("orderPriceType");
        String page_no = request.getParameter("page_no");
        int pageNo = Integer.parseInt(page_no);

        if ("orders".equals(type)) {
            data = this.exchangeApplyOrderService.getPaged(pageNo, 1000, SecurityUtils.getCurrentUserId(), symbol, type, isAll, startTime, endTime, symbolType, orderPriceType);
        } else {
            data = this.exchangeApplyOrderService.getPaged(pageNo, 10, SecurityUtils.getCurrentUserId(), symbol, type, isAll, startTime, endTime, symbolType, orderPriceType);
        }
        return Result.succeed(data);
    }

    /**
     * 获取etf 总资产
     *
     * @return
     */
    @RequestMapping(action + "getETFSum.action")
    public Result<SumEtfDto> getETFSum(String symbolType) {
        String userId = SecurityUtils.getCurrentUserId();
        if (StringUtils.isEmptyString(symbolType)) {
            symbolType = "indices";
        }
        DecimalFormat df2 = new DecimalFormat("#.####");
        ContractAndFutureProfit contractAndFutureProfit = walletService.calculateContractAndFutureProfit(userId, symbolType);

        Map<String, Object> moneyContract = walletService.getMoneyAll(userId, symbolType);

        SumEtfDto sumEtfDto = new SumEtfDto();
        sumEtfDto.setSumVolume(df2.format(Double.parseDouble(moneyContract.get("money_wallet").toString())));
        sumEtfDto.setSumPrice(df2.format(Double.parseDouble(moneyContract.get("symbol_type_asserts").toString())));
        sumEtfDto.setProfitLoss(df2.format(contractAndFutureProfit.getOrderProfit()));
        sumEtfDto.setToDayProfitLoss(df2.format(contractAndFutureProfit.getOrderProfitToday()));
        return Result.succeed(sumEtfDto);
    }

    /**
     * 查询现货交易持仓单列表
     */
    @RequestMapping(action + "getETFList.action")
    public Result<List<ExchangeSymbolDto>> querySpotTradPositionList(String symbolType) {
        if (StringUtils.isEmptyString(symbolType)) {
            symbolType = "indices";
        }
        List<ExchangeSymbolDto> sumEtfDto = exchangeApplyOrderService.querySpotTradPositionList(SecurityUtils.getCurrentUserId(), symbolType);
        return Result.succeed(sumEtfDto);
    }

    /**
     * 现货交易-买入 开仓页面参数
     */
    @RequestMapping(action + "openview.action")
    public Result openView(String type) {
        Map<String, Object> data = new HashMap<>();
        String partyId = SecurityUtils.getCurrentUserId();
        Wallet wallet = walletService.saveWalletByPartyId(partyId);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        data.put("volume", df.format(exchangeRateService.getCurrencyByType(wallet.getMoney(), type)));
        String session_token = sessionTokenService.savePut(partyId);
        data.put("session_token", session_token);
        data.put("fee", sysparaService.find("exchange_apply_order_buy_fee").getSvalue());
        return Result.succeed(data);
    }

    /**
     * 现货交易-卖出 平仓页面参数
     */
    @RequestMapping(action + "closeview.action")
    public Result closeView(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        String partyId = SecurityUtils.getCurrentUserId();
        String symbol = request.getParameter("symbol");
        if (!StringUtils.isNullOrEmpty(partyId)) {
            WalletExtend walletExtend = walletService.saveExtendByPara(partyId, symbol);
            data.put("volume", null == walletExtend ? 0 : walletExtend.getAmount());
            String session_token = sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
            data.put("fee", sysparaService.find("exchange_apply_order_sell_fee").getSvalue());
        }
        return Result.succeed(data);
    }

    /**
     * 现货交易-买入
     */
    @RequestMapping(action + "open.action")
    public Object open(String volume, String total, String session_token, String symbol, String price, String order_price_type) {
        if (StringUtils.isNullOrEmpty(volume) || !StringUtils.isDouble(volume) || Double.parseDouble(volume) <= 0) {
// 请输入正确的货币数量
            throw new YamiShopBindException("Please enter correct currency amount");
        }
        if (StringUtils.isNullOrEmpty(total) || !StringUtils.isDouble(total) || Double.parseDouble(total) <= 0) {
// 请输入正确的货币数量
            throw new YamiShopBindException("Please enter correct currency amount");
        }
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
        String partyId = SecurityUtils.getCurrentUserId();
        if ((!partyId.equals(object))) {
// 请稍后再试
            throw new YamiShopBindException("Please try again later");
        }
        User party = userService.getById(partyId);
        if (!party.isEnabled()) {
// 用户已禁用
            throw new YamiShopBindException("User is disabled");
        }
        Syspara syspara = sysparaService.find("stop_user_internet");
        String stopUserInternet = syspara.getSvalue();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
            String[] stopUsers = stopUserInternet.split(",");
            if (Arrays.asList(stopUsers).contains(party.getUserName())) {
// 无网络
                throw new YamiShopBindException("No network connection");
            }
        }
        Realtime realtime = this.dataService.realtime(symbol).get(0);
        double priceTemp = Double.parseDouble(price);
        if (ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT.equals(order_price_type)) {
            double rate = Math.abs(Arith.sub(realtime.getClose(), priceTemp));
            if (Arith.div(rate, priceTemp) > 0.05D) {
                log.error("币种数量异常{},{},{}", symbol, priceTemp, realtime.getClose());
    // 请稍后再试
            throw new YamiShopBindException("Please try again later");
            }
        }

        ExchangeApplyOrder order = new ExchangeApplyOrder();
        order.setPartyId(partyId);
        order.setSymbol(symbol);
        order.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
        order.setSymbolValue(Double.parseDouble(total));
        order.setVolume(Double.valueOf(volume));
        order.setPrice(priceTemp);
        order.setOrderPriceType(order_price_type);

        order.setClosePrice(realtime.getClose());
        exchangeApplyOrderService.saveSpotTradOpen(order);
        return Result.succeed();
    }

    /**
     * 币币交易-卖出
     */
    @RequestMapping(action + "close.action")
    public Object close(HttpServletRequest request) {
        // 委托数量
        String volume = request.getParameter("volume");
        String session_token = request.getParameter("session_token");
        String symbol = request.getParameter("symbol");
        // limit order的交易价格
        String price = request.getParameter("price");
        String order_price_type = request.getParameter("order_price_type");
        String partyId = SecurityUtils.getCurrentUserId();
        if (StringUtils.isNullOrEmpty(volume) || !StringUtils.isDouble(volume) || Double.parseDouble(volume) <= 0) {
// 请输入正确的货币数量
            throw new YamiShopBindException("Please enter correct currency amount");
        }
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
        if ((!partyId.equals(object))) {
// 请稍后再试
            throw new YamiShopBindException("Please try again later");
        }
        User party = userService.getById(partyId);
        if (!party.isEnabled()) {
// 用户已禁用
            throw new YamiShopBindException("User is disabled");
        }
        Syspara syspara = sysparaService.find("stop_user_internet");
        String stopUserInternet = syspara.getSvalue();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
            String[] stopUsers = stopUserInternet.split(",");
            if (Arrays.asList(stopUsers).contains(party.getUserName())) {
// 无网络
                throw new YamiShopBindException("No network connection");
            }
        }
        ExchangeApplyOrder order = new ExchangeApplyOrder();
        order.setPartyId(partyId);
        order.setSymbol(symbol);
        order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
        order.setVolume(Double.parseDouble(volume));
        order.setSymbolValue(Double.parseDouble(volume));
        order.setPrice(Double.parseDouble(price));
        order.setOrderPriceType(order_price_type);
        Realtime realtime = this.dataService.realtime(symbol).get(0);
        order.setClosePrice(realtime.getClose());
        this.exchangeApplyOrderService.saveSpotTradClose(order);
        return Result.succeed();
    }

    /**
     * 撤单
     */
    @RequestMapping(action + "cancel.action")
    public Object cancel(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");

        // 直接开线程的方式不合适，要放到线程池里去做 TODO
        CancelDelayThread lockDelayThread = new CancelDelayThread(SecurityUtils.getCurrentUserId(), order_no,
                exchangeApplyOrderService);
        Thread t = new Thread(lockDelayThread);
        t.start();

        return Result.succeed();
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CancelDelayThread implements Runnable {
        private String partyId;
        private String order_no;
        private ExchangeApplyOrderService exchangeApplyOrderService;

        @Override
        public void run() {
            try {
                while (true) {
                    if (ExchangeLock.add(order_no)) {
                        this.exchangeApplyOrderService.saveCancel(partyId, order_no);
                        break;
                    }
                    ThreadUtils.sleep(100);
                }
            } catch (Exception e) {
                log.error("error:", e);
            } finally {
                ThreadUtils.sleep(100);
                ExchangeLock.remove(order_no);
            }
        }

        public CancelDelayThread(String partyId, String order_no, ExchangeApplyOrderService exchangeApplyOrderService) {
            this.partyId = partyId;
            this.order_no = order_no;
            this.exchangeApplyOrderService = exchangeApplyOrderService;
        }
    }

    /**
     * 详情接口
     */
    @RequestMapping(action + "get.action")
    public Object get(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");
        ExchangeApplyOrder order = this.exchangeApplyOrderService.findByOrderNoAndPartyId(order_no,
                SecurityUtils.getCurrentUserId());
        return Result.succeed(bulidData(order));
    }

    private Map<String, Object> bulidData(ExchangeApplyOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", order.getOrderNo());
        map.put("name", itemService.findBySymbol(order.getSymbol()).getName());
        map.put("symbol", order.getSymbol());
        map.put("create_time", order.getCreateTime());
        map.put("create_time_ts", order.getCreateTimeTs());
        map.put("volume", order.getVolume());
        map.put("offset", order.getOffset());
        map.put("price", order.getPrice());
        map.put("order_price_type", order.getOrderPriceType());
        map.put("state", order.getState());
        map.put("fee", order.getFee());
        map.put("close_price", order.getClosePrice());
        map.put("close_time", order.getCloseTime());
        map.put("close_time_ts", order.getCloseTime() == null ? "" : order.getCloseTime().getTime());
        return map;
    }


    //=============================================闪兑BEGIN================================================================

    /**
     * 兑换币 如果是使用usdt兑换其他币种，则直接执行正常买币open流程 如果是其他币种--》usdt 则是直接卖币流程
     * 如果是其他币种到另一个币种，则需要先卖出，然后再买入
     * 兑换接口
     */
    @RequestMapping(action + "buy_and_sell.action")
    public Object buyAndSell(HttpServletRequest request) {
        String volume_temp = request.getParameter("volume");
        if (StringUtils.isNullOrEmpty(volume_temp) || !StringUtils.isDouble(volume_temp) || Double.parseDouble(volume_temp) <= 0) {
// 请输入正确的货币数量
            throw new YamiShopBindException("Please enter correct currency amount");
        }
        double volume = Double.parseDouble(volume_temp);
        String symbol = request.getParameter("symbol");
        String symbol_to = request.getParameter("symbol_to");
        if (symbol.equals(symbol_to)) {
// 请选择正确的币种
            throw new YamiShopBindException("Please select correct currency");
        }
        String session_token = request.getParameter("session_token");
        String partyId = SecurityUtils.getCurrentUserId();
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
        if ((!partyId.equals(object))) {
            log.info("sessionToken{}", object);
            System.out.println("sessionToken " + object);
// 请稍后再试
            throw new YamiShopBindException("Please try again later");
        }
        User party = userService.getById(partyId);
        if (!party.isEnabled()) {
// 用户已禁用
            throw new YamiShopBindException("User is disabled");
        }
        symbol = itemService.getCleanSymbol(symbol);
        symbol_to = itemService.getCleanSymbol(symbol_to);
        String relation_order_no = UUID.randomUUID().toString();
        // 如果是使用usdt兑换其他币种，则直接执行正常买币open流程
        if ("usdt".equals(symbol) || "usdt".equals(symbol_to)) {
            ExchangeApplyOrder order = new ExchangeApplyOrder();
            order.setPartyId(partyId);
            order.setVolume(volume);
            order.setOrderPriceType(ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT);
            order.setRelationOrderNo(relation_order_no);
            if ("usdt".equals(symbol)) {
                order.setSymbol(symbol_to);
                double openPrice = getRealtimePrice(symbol_to);
                order.setPrice(openPrice);
                order.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
            } else if ("usdt".equals(symbol_to)) {
                order.setSymbol(symbol);
                double closePrice = getRealtimePrice(symbol);
                order.setPrice(closePrice);
                order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
            }
            exchangeApplyOrderService.saveCreate(order);
        } else {
            // 非usdt则需要进行一次卖出
            ExchangeApplyOrder order_sell = new ExchangeApplyOrder();
            order_sell.setPartyId(partyId);
            order_sell.setSymbol(symbol);
            order_sell.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
            order_sell.setVolume(volume);
            order_sell.setOrderPriceType(ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT);
            order_sell.setRelationOrderNo(relation_order_no);
            double sellClose = getRealtimePrice(symbol);
            order_sell.setPrice(sellClose);
            exchangeApplyOrderService.saveCreate(order_sell);
            double close = getRealtimePrice(symbol);
            double sub = Arith.sub(volume,
                    Arith.mul(volume, sysparaService.find("exchange_apply_order_sell_fee").getDouble()));
            double amount = Arith.mul(sub, close);
            // 再买入币种
            ExchangeApplyOrder order_buy = new ExchangeApplyOrder();
            order_buy.setPartyId(partyId);
            order_buy.setSymbol(symbol_to);
            order_buy.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
            order_buy.setVolume(amount);
            order_buy.setRelationOrderNo(relation_order_no);
            order_buy.setOrderPriceType(ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT);
            double buyClose = getRealtimePrice(symbol_to);
            order_buy.setPrice(buyClose);
            exchangeApplyOrderService.saveCreate(order_buy);
        }
        return Result.succeed();
    }

    /**
     * 兑换汇率
     */
    @RequestMapping(action + "buy_and_sell_fee.action")
    public Object buy_and_sell_fee(HttpServletRequest request) {
        // 需兑换币种
        String symbol = request.getParameter("symbol");
        // 兑换后的币种
        String symbol_to = request.getParameter("symbol_to");
        if (symbol.equals(symbol_to)) {
// 请选择正确的币种
            throw new YamiShopBindException("Please select correct currency");
        }
        // 委托数量
        String volume_temp = request.getParameter("volume");
        if (StringUtils.isNullOrEmpty(volume_temp)
                || !StringUtils.isDouble(volume_temp) || Double.valueOf(volume_temp) < 0) {
// 请输入正确的兑换数量
            throw new YamiShopBindException("Please enter correct exchange amount");
        }
        Map<String, Object> data = new HashMap<>();
        symbol = itemService.getCleanSymbol(symbol);
        symbol_to = itemService.getCleanSymbol(symbol_to);
        double buy_fee = Double.valueOf(sysparaService.find("exchange_apply_order_buy_fee").getSvalue());
        double sell_fee = Double.valueOf(sysparaService.find("exchange_apply_order_sell_fee").getSvalue());
        double volume = Double.valueOf(volume_temp);
        if ("usdt".equals(symbol) || "usdt".equals(symbol_to)) {
            if ("usdt".equals(symbol)) {
                // 如果是使用Usdt 则计算收益
                Realtime realtime = dataService.realtime(symbol_to).get(0);
                double symbol_to_price = realtime.getClose();
                // usdt除以的数量
                data.put("get_rate", Arith.div(1, symbol_to_price, 5));
                // 实际兑换数量= 兑换数量-手续费数量
                double fact_volume = Arith.sub(volume, Arith.mul(volume, buy_fee));
                // 实际价值 = 实际兑换数量 * 被兑换品种价格
                double fact_price = Arith.mul(fact_volume, 1);
                // 实际获取数量 = 实际价值 / 将要兑换的品种的价值
                data.put("get_volume", Arith.div(fact_price, symbol_to_price, 5));
            }
            if ("usdt".equals(symbol_to)) {
                /**
                 * 如果是转成Usdt 则计算收益
                 */
                Realtime realtime = this.dataService.realtime(symbol).get(0);
                double symbol_price = realtime.getClose();
                // 对应usdt数量
                data.put("get_rate", Arith.div(symbol_price, 1, 5));
                // 实际兑换数量= 兑换数量-手续费数量
                double fact_volume = Arith.sub(volume, Arith.mul(volume, sell_fee));
                // 实际价值 = 实际兑换数量 * 被兑换品种价格
                double fact_price = Arith.mul(Arith.div(symbol_price, 1, 5), fact_volume);
                // 实际获取数量 = 实际价值 / 将要兑换的品种的价值
                data.put("get_volume", Arith.div(fact_price, 1, 5));
            }
        } else {
            Realtime realtime = this.dataService.realtime(symbol).get(0);
            Realtime realtimeTo = this.dataService.realtime(symbol_to).get(0);
            double symbol_price = realtime.getClose();
            double symbol_to_price = realtimeTo.getClose();
            data.put("get_rate", Arith.div(symbol_price, symbol_to_price, 5));
            // 总手续费比例
            double all_fee = Arith.add(buy_fee, sell_fee);
            // 实际兑换数量= 兑换数量-手续费数量
            double fact_volume = Arith.sub(volume, Arith.mul(volume, all_fee));
            // 实际价值 = 实际兑换数量 * 被兑换品种价格
            double fact_price = Arith.mul(fact_volume, symbol_price);
            // 实际获取数量 = 实际价值 / 将要兑换的品种的价值
            data.put("get_volume", Arith.div(fact_price, symbol_to_price, 5));
        }
        return Result.succeed(data);
    }

    /**
     * 闪兑记录列表
     */
    @RequestMapping(action + "queryExchangeList")
    public Result queryExchangeList(HttpServletRequest request) {
        List<Map<String, Object>> data = exchangeApplyOrderService.queryExchangeListPage(1, 10, SecurityUtils.getCurrentUserId());
        return Result.succeed(data);
    }

    //=============================================闪兑END================================================================

}
