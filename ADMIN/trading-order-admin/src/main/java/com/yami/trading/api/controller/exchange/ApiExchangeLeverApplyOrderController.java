package com.yami.trading.api.controller.exchange;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeLever;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.domain.ItemLeverage;
import com.yami.trading.bean.item.dto.ItemLeverageDTO;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.purchasing.dto.ExchangeLeverLock;
import com.yami.trading.bean.purchasing.dto.ExchangeLock;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeLeverApplyOrderService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import com.yami.trading.service.exchange.ExchangeLeverService;
import com.yami.trading.service.item.ItemLeverageService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@CrossOrigin
@Slf4j
@RequestMapping("api/exchangeleverapplyorder")
@Api(tags = "现货杠杆 -api")
public class ApiExchangeLeverApplyOrderController {
    @Autowired
    SessionTokenService sessionTokenService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    UserService userService;
    @Autowired
    private DataService dataService;
    @Autowired
    ExchangeLeverApplyOrderService exchangeLeverApplyOrderService;

    @Autowired
    WalletService walletService;

    @Autowired
    ItemLeverageService itemLeverageService;
    @Autowired
    ItemService itemService;

    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;

    /**
     * 开仓页面参数
     */

    @ApiOperation("开仓页面参数")
    @PostMapping("openview")
    public Result openview(String symbol) {

        Map<String, Object> data = new HashMap<String, Object>();
        double exchange_lever_fee = this.sysparaService.find("exchange_lever_fee").getDouble();
        Item item = itemService.findBySymbol(symbol);
        if (item == null) {
            throw  new BusinessException("币信息不存在!");
        }
        List<ItemLeverageDTO> itemLeverageDTOS = itemLeverageService.findByItemId(item.getUuid());
        List<ExchangeLever>  levers=new ArrayList<>();
        if (CollectionUtils.isEmpty(itemLeverageDTOS)) {
            double exchange_lever_rate = this.sysparaService.find("exchange_lever_rate").getDouble();
            ExchangeLever lever = new ExchangeLever();
            lever.setSymbol(symbol);
            lever.setLeverRate(exchange_lever_rate);
            levers.add(lever);
        }
        else {
            for (ItemLeverageDTO dto:itemLeverageDTOS){
                ExchangeLever lever = new ExchangeLever();
                lever.setSymbol(symbol);
                lever.setLeverRate(Double.valueOf(dto.getLeverRate()));
                levers.add(lever);
            }
        }
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat df8 = new DecimalFormat("#.########");
        double interestRate = sysparaService.find("exchange_lever_interest_rate").getDouble();// 利率
     //   interestRate = item.getBorrowingRate() > 0 ? item.getBorrowingRate() : interestRate;
        data.put("borrowing_rate", interestRate);  //每小时率
        data.put("exchange_lever", levers); //杠杆
        data.put("fee", exchange_lever_fee);   //手续
        String partyId = SecurityUtils.getCurrentUserId();
        if (!StringUtils.isNullOrEmpty(partyId)) {
            Wallet wallet = walletService.saveWalletByPartyId(partyId);
            WalletExtend walletExtend = walletService.saveExtendByPara(partyId, symbol);
            /*
             * 账户剩余资 金
             */
//				DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            data.put("money", df.format(wallet.getMoney()));
            data.put("volume", df8.format(walletExtend.getAmount()));
            String session_token = sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
        } else {
            data.put("money", 0);
            data.put("volume", 0);
        }
        return Result.succeed(data);
    }

    @ApiOperation("平仓页面参数")
    @PostMapping("closeview")
    public Result closeview(String symbol, String direction) {

        Map<String, Object> data = new HashMap<String, Object>();
        String partyId = SecurityUtils.getCurrentUserId();
        if (!StringUtils.isNullOrEmpty(partyId)) {
            DecimalFormat df = new DecimalFormat("#.##");
            DecimalFormat df8 = new DecimalFormat("#.########");
            List<ExchangeLeverOrder> list = exchangeLeverOrderService.findSubmitted(partyId, symbol, direction);
            double ordervolume = 0;
            //换算成U
        //    double sum = 0d;
            // 数据库是保留10位，这里多保留一位避免计算误差
//            for (ExchangeLeverOrder order : list) {
////					sum = Arith.add(Arith.mul(order.getDeposit(), order.getLever_rate()), sum);// 保证金杠杆
//                if (order.getVolume().compareTo(order.getVolumeOpen()) == 0) {
////						sum = Arith.add(Arith.mul(order.getDeposit(), order.getLever_rate()), sum);
//                    // 买涨 用U 则 对应的保证金也是U，
//                    if (ExchangeLeverApplyOrder.DIRECTION_BUY.equals(order.getDirection())) {
//                        sum = Arith.add(Arith.mul(order.getDeposit(), order.getLeverRate()), sum);
//                    } else {// 买跌 用指定币种 则 对应的保证金也是指定币种，
//                        sum = Arith.add(Arith.mul(order.getVolume(), order.getUnitAmount()), sum);
//                    }
//                } else {
//                    sum = Arith.add(Arith.mul(order.getVolume(), order.getUnitAmount()), sum);// 保证金杠杆
//                }
//                ordervolume = Arith.add(ordervolume, order.getVolume());
//            }

            double volume=0;
            double deposit=0;
            if (CollectionUtil.isNotEmpty(list)){
                volume=list.get(0).getVolume();
                deposit= list.get(0).getDeposit();
            }
            data.put("volume",volume);
            data.put("money",deposit);
            String session_token = sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
        } else {
            data.put("volume", 0);
            data.put("money", 0);
        }
        return Result.succeed(data);
    }

    @ApiOperation("开仓委托")
    @PostMapping("open")
    public Result open(String lever_rate, String direction, String session_token,
                       String symbol, String deposit, String price, String order_price_type) {

        String partyId = SecurityUtils.getUser().getUserId();
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
        SecurityUtils.getCurrentUserId();
        if ((object == null) || (!partyId.equals((String) object))) {
// 请稍后再试
            throw new YamiShopBindException("Please try again later");
        }
        User party = userService.getById(partyId);
        if (!party.isEnabled()) {
// 用户已禁用
            throw new YamiShopBindException("User is disabled");
        }
        ExchangeLeverApplyOrder order = new ExchangeLeverApplyOrder();
        order.setPartyId(SecurityUtils.getCurrentUserId());
        order.setSymbol(symbol);
        order.setDirection(direction);
        order.setOffset("open");
        double lever_rateD = 0;
        if (lever_rate == null) {
            double exchange_lever_rate = sysparaService.find("exchange_lever_rate").getDouble();
            lever_rateD = exchange_lever_rate;
        } else {
            lever_rateD = Double.valueOf(lever_rate);
        }
        order.setLeverRate(lever_rateD);
        order.setPrice(StringUtils.isNullOrEmpty(price) ? 0 : Double.valueOf(price));
        order.setDeposit(Double.valueOf(deposit));
        order.setOrder_price_type(order_price_type);
        exchangeLeverApplyOrderService.saveCreate(order);
        return Result.succeed();
    }

    /**
     *
     */
    @ApiOperation("获取购买手续费")
    @GetMapping("buyAndSellFee")
    public Result<Map<String, Object>> buyAndSellFee(String symbol, String
            symbol_to, String volume) {

        Map<String, Object> data = new HashMap<>();
        if (symbol.equals(symbol_to))
            throw new BusinessException("请选择正确的币种");
        symbol = symbol.toLowerCase();
        symbol_to = symbol_to.toLowerCase();
        double buy_fee = Double.valueOf(sysparaService.find("exchange_apply_order_buy_fee").getSvalue());
        double sell_fee = Double.valueOf(sysparaService.find("exchange_apply_order_sell_fee").getSvalue());
        if ("usdt".equals(symbol) || "usdt".equals(symbol_to)) {
            if ("usdt".equals(symbol)) {
                /**
                 * 如果是使用Usdt 则计算收益
                 */
                List<Realtime> realtime_list = dataService.realtime(symbol_to);
                Realtime realtime = null;
                if (realtime_list.size() > 0) {
                    realtime = realtime_list.get(0);
                } else {
                    throw new BusinessException("系统错误，请稍后重试");
                }
                double symbol_to_price = realtime.getClose();
                // usdt除以的数量
                data.put("get_rate", Arith.div(1, symbol_to_price));
                /**
                 * 实际兑换数量= 兑换数量-手续费数量
                 */
                double fact_volume = Arith.sub(Double.valueOf(volume), Arith.mul(Double.valueOf(volume), buy_fee));
                /**
                 * 实际价值 = 实际兑换数量 * 被兑换品种价格
                 */
                double fact_price = Arith.mul(fact_volume, 1);
                /**
                 * 实际获取数量 = 实际价值 / 将要兑换的品种的价值
                 */
                data.put("get_volume", Arith.div(fact_price, symbol_to_price));
            }
            if ("usdt".equals(symbol_to)) {
                /**
                 * 如果是转成Usdt 则计算收益
                 */
                List<Realtime> realtime_list = this.dataService.realtime(symbol);
                Realtime realtime = null;
                if (realtime_list.size() > 0) {
                    realtime = realtime_list.get(0);
                } else {
                    throw new BusinessException("系统错误，请稍后重试");
                }
                double symbol_price = realtime.getClose();
                // 对应usdt数量
                data.put("get_rate", Arith.div(symbol_price, 1));
                /**
                 * 实际的数量等于 卖出数量-手续费数量
                 */
                /**
                 * 实际兑换数量= 兑换数量-手续费数量
                 */
                double fact_volume = Arith.sub(Double.valueOf(volume), Arith.mul(Double.valueOf(volume), sell_fee));
                /**
                 * 实际价值 = 实际兑换数量 * 被兑换品种价格
                 */
                double fact_price = Arith.mul(Arith.div(symbol_price, 1), fact_volume);
                /**
                 * 实际获取数量 = 实际价值 / 将要兑换的品种的价值
                 */
                data.put("get_volume", Arith.div(fact_price, 1));
            }
        } else {
            double symbol_price = 0;
            double symbol_to_price = 0;
            /**
             * 获取币种最新价格
             */
            List<Realtime> realtime_list = this.dataService.realtime(symbol);
            Realtime realtime = null;
            if (realtime_list.size() > 0) {
                realtime = realtime_list.get(0);
                symbol_price = realtime.getClose();
            } else {
                throw new BusinessException("系统错误，请稍后重试");
            }
            /**
             * 获取币种最新价格
             */
            List<Realtime> realtime_list_to = this.dataService.realtime(symbol_to);
            Realtime realtime_to = null;
            if (realtime_list_to.size() > 0) {
                realtime_to = realtime_list_to.get(0);
                symbol_to_price = realtime_to.getClose();
            } else {
                throw new BusinessException("系统错误，请稍后重试");
            }
            if (symbol_to_price == 0) {
                symbol_to_price = 1;
            }
            if (symbol_price == 0) {
                symbol_price = 1;
            }
            data.put("get_rate", Arith.div(symbol_price, symbol_to_price));
            /**
             * 总手续费比例
             */
            double all_fee = Arith.add(buy_fee, sell_fee);
            /**
             * 实际兑换数量= 兑换数量-手续费数量
             */
            double fact_volume = Arith.sub(Double.valueOf(volume), Arith.mul(Double.valueOf(volume), all_fee));
            /**
             * 实际价值 = 实际兑换数量 * 被兑换品种价格
             */
            double fact_price = Arith.mul(fact_volume, symbol_price);
            /**
             * 实际获取数量 = 实际价值 / 将要兑换的品种的价值
             */
            data.put("get_volume", Arith.div(fact_price, symbol_to_price));
        }
        return Result.succeed(data);
    }

    @ApiOperation("列表")
    @PostMapping("list")
    public Result list(String page_no, String symbol, String type) {

        List<Map<String, Object>> data = this.exchangeLeverApplyOrderService.getPaged(Integer.valueOf(page_no),
                10, SecurityUtils.getCurrentUserId(),
                symbol, type);
        return Result.succeed(data);
    }

    @ApiOperation("获取详情")
    @PostMapping("get")
    public Result get(String order_no) {

        ExchangeLeverApplyOrder order = exchangeLeverApplyOrderService.findByOrderNo(order_no);
        return Result.succeed(bulidData(order));
    }

    private Map<String, Object> bulidData(ExchangeLeverApplyOrder order) {

        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_no", order.getOrderNo());
        map.put("name", itemService.findBySymbol(order.getSymbol()) == null ? "" : itemService.findBySymbol(order.getSymbol()).getName());
        map.put("symbol", order.getSymbol());
        map.put("create_time", DateUtils.format(order.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
        map.put("volume", order.getVolume());
        map.put("volume_open", order.getVolumeOpen());
        map.put("direction", order.getDirection());
        map.put("offset", order.getOffset());
        map.put("lever_rate", order.getLeverRate());
        map.put("price", order.getPrice());
        map.put("stop_price_profit", order.getStop_price_profit());
        map.put("stop_price_loss", order.getStop_price_loss());
        map.put("order_price_type", order.getOrder_price_type());
        map.put("state", order.getState());
//		map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
//		map.put("amount_open", Arith.mul(order.getVolume_open(), order.getUnit_amount()));
//		if (order.getVolume().compareTo(order.getVolume_open()) != 0) {// 不相等
//			map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
//		} else {
//			// 开仓委托才有杠杆
//			map.put("amount", Arith.mul(order.getDeposit(),
//					ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLever_rate() : 1d));
//		}
        map.put("amount", Arith.mul(order.getDeposit(),
                ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLeverRate() : 1d));
        map.put("amount_open", Arith.mul(order.getDeposit(),
                ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLeverRate() : 1d));
        map.put("fee", order.getFee());
        map.put("deposit", ExchangeLeverApplyOrder.OFFSET_CLOSE.equals(order.getOffset()) ? 0 : order.getDeposit());// 平仓委托保证金为0，和合约相同
        return map;
    }

    @ApiOperation("平仓")
    @PostMapping("close")
    public Result close(String symbol, String direction, String price, String order_price_type,
                        String deposit) {

        String partyId = SecurityUtils.getCurrentUserId();
        boolean lock = false;
        try {
            if (!ExchangeLeverLock.add(partyId)) {
                throw new BusinessException("请稍后再试");
            }
            lock = true;
            User party = userService.getById(partyId);
            if (!party.isEnabled()) {
                throw new BusinessException("用户已锁定");
            }
//			if (!party.getKyc_authority()) {
//				resultObject.setCode("401");
//				resultObject.setMsg(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
            ExchangeLeverApplyOrder order = new ExchangeLeverApplyOrder();
            order.setPartyId(partyId);
            order.setSymbol(symbol);
            order.setDirection(direction);
            order.setOffset(ExchangeLeverApplyOrder.OFFSET_CLOSE);
//			order.setVolume(volume);
//			order.setVolume_open(volume);
            order.setPrice(Double.valueOf(price));
            order.setOrder_price_type(order_price_type);
            order.setDeposit(Double.valueOf(deposit));
            ExchangeLeverOrder  exchangeLeverOrder = exchangeLeverOrderService.findBySymbol(partyId, symbol);
           if (exchangeLeverOrder==null){
               throw new BusinessException("没有可平仓仓位");
           }
            if (order.getDeposit()>exchangeLeverOrder.getDeposit()){
                throw new BusinessException("可平仓保证金余额不足");
            }
            exchangeLeverApplyOrderService.saveCreate(order);
        } catch (BusinessException e) {
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            return Result.failed("程序错误");
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                ExchangeLeverLock.remove(partyId);
            }
        }
        return Result.succeed();
    }

    @ApiOperation("撤单")
    @PostMapping("cancel")
    public Result cancel(String order_no) {

        CancelDelayThread lockDelayThread =
                new CancelDelayThread(SecurityUtils.getCurrentUserId(),
                        order_no, this.exchangeLeverApplyOrderService);
        Thread t = new Thread(lockDelayThread);
        t.start();
        return Result.succeed();
    }

    public class CancelDelayThread implements Runnable {
        private String partyId;
        private String order_no;
        private ExchangeLeverApplyOrderService exchangeLeverApplyOrderService;

        public void run() {

            while (true) {
                try {
                    if (!ExchangeLeverLock.add(this.order_no)) {
                        ThreadUtils.sleep(100L);
                        continue;
                    }
                    exchangeLeverApplyOrderService.saveCancel(this.partyId, this.order_no);
                } catch (Exception var5) {
                    log.error("error:", var5);
                } finally {
                    ThreadUtils.sleep(100L);
                    ExchangeLeverLock.remove(this.order_no);
                }
                return;
            }
        }

        public CancelDelayThread(String partyId, String order_no, ExchangeLeverApplyOrderService exchangeLeverApplyOrderService) {

            this.partyId = partyId;
            this.order_no = order_no;
            this.exchangeLeverApplyOrderService = exchangeLeverApplyOrderService;
        }

    }

}
