package com.yami.trading.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.dto.CloseAction;
import com.yami.trading.api.dto.OpenAction;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemLeverageDTO;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractLockService;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemLeverageService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 订单表Controller
 *
 * @author lucas
 * @version 2023-03-29
 */

@Api(tags = "订单表")
@RestController
@CrossOrigin
@Slf4j
public class ApiContractApplyOrderController {
    private final String action = "/api/contractApplyOrder!";

    @Autowired
    private ContractApplyOrderService contractApplyOrderService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemLeverageService itemLeverageService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserService userService;
    @Autowired
    private ContractOrderService contractOrderService;
    @Autowired
    private ContractLockService contractLockService;
    @Autowired
    private SessionTokenService sessionTokenService;
    @Autowired
    TipService tipService;
//    @Autowired
//    private ExchangeRateService exchangeRateService;

    /**
     * 开仓页面参数
     * symbol 币种
     */
    @RequestMapping(action + "openview.action")
    public Result<Map<String, Object>> openview(@RequestParam String symbol) {
        Map<String, Object> data = new HashMap<String, Object>();
        Item item = itemService.findBySymbol(symbol);
        data.put("amount", item.getUnitAmount());
        data.put("fee", item.getUnitFee());
        data.put("feePercentage", "");
        List<ItemLeverageDTO> lLeverageDTO = itemLeverageService.findByItemId(item.getUuid());
        data.put("lever", lLeverageDTO);

        String partyId = SecurityUtils.getCurrentUserId();
        if (!StrUtil.isBlank(partyId)) {
            Wallet wallet = walletService.findByUserId(partyId);
            // 账户剩余资 金
            BigDecimal useAmount = BigDecimal.valueOf(item.getUnitAmount() + item.getUnitFee());
            if (useAmount.doubleValue() <= 0) {
                return Result.failed("币种手续费值不合理");
            }

            BigDecimal volume = wallet.getMoney().divide(useAmount,0, RoundingMode.FLOOR);
//            volume = exchangeRateService.getCurrencyByType(volume, item.getType());
            data.put("volume", volume.toPlainString());
            String session_token = this.sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
        } else {
            data.put("volume", 0.00D);
        }
        data.put("open", MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType()));

        String limitMin = sysparaService.find("contract_open_limit_min").getSvalue();
        if (StrUtil.isBlank(limitMin)) {
            return Result.failed("未配置正确的参数: contract_open_limit_min");
        }
        String limitMax = sysparaService.find("contract_open_limit_max").getSvalue();
        if (StrUtil.isBlank(limitMax)) {
            return Result.failed("未配置正确的参数: contract_open_limit_max");
        }
        try {
            BigDecimal contractOpenLimitMin = new BigDecimal(limitMin.trim());
            BigDecimal contractOpenLimitMax = new BigDecimal(limitMax.trim());
        } catch (Exception e) {
            return Result.failed("配置的: contract_open_limit_min 或 contract_open_limit_max 值不是合规的数字");
        }

        data.put("contract_open_limit_min", limitMax.trim());
        data.put("contract_open_limit_max", limitMax.trim());
        Syspara syspara = sysparaService.find("u_standard_contract");
        if (ObjectUtils.isNotEmpty(syspara)) {
            if ("1".equals(syspara.getSvalue())) {
                data.put("feePercentage", item.getUnitPercentage());
            }
        }

        return Result.succeed(data);
    }


    /**
     * 平仓页面参数
     * symbol 币种
     */
    @RequestMapping(action + "closeview.action")
    public Result<Map<String, Object>> closeview(HttpServletRequest request, @RequestParam String symbol) {
        String direction = request.getParameter("direction");
//        if(StringUtils.isEmptyString(direction)){
//             direction = "sell";
//        }
        String partyId = SecurityUtils.getCurrentUserId();
        Map<String, Object> data = new HashMap<>();

        if (StrUtil.isNotBlank(partyId)) {
            List<ContractOrder> list = this.contractOrderService.findSubmitted(partyId, symbol, direction);

            BigDecimal ordervolume = BigDecimal.ZERO;
            for (ContractOrder contractOrder : list) {
                ordervolume = ordervolume.add(contractOrder.getVolume());
            }
//            List<ContractApplyOrder> contractApplyOrderSubmitted = this.contractApplyOrderService.findSubmitted(partyId, symbol, ContractApplyOrder.OFFSET_CLOSE, direction);
//            for (ContractApplyOrder contractApplyOrder : contractApplyOrderSubmitted) {
//                ordervolume = ordervolume.subtract(contractApplyOrder.getVolume());
//            }
            data.put("amount", ordervolume);
            if (null == list || 0 == list.size()) {
                data.put("lever_rate", 1);
            } else {
                if (null == list.get(0) || null == list.get(0).getLeverRate()) {
                    data.put("lever_rate", 1);
                } else {
                    data.put("lever_rate", list.get(0).getLeverRate());
                }
            }
            String session_token = this.sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
        } else {
            data.put("amount", BigDecimal.ZERO);
            data.put("lever_rate", 1);
        }
        BigDecimal contractCloseLimitMin = new BigDecimal(sysparaService.find("contract_close_limit_min").getSvalue());
        data.put("contract_close_limit_min", contractCloseLimitMin);
        BigDecimal contractCloseLimitMax = new BigDecimal(sysparaService.find("contract_close_limit_max").getSvalue());
        data.put("contract_close_limit_max", contractCloseLimitMax);
        return Result.succeed(data);
    }

    /**
     * 开仓
     */
    @RequestMapping(action + "open.action")
    public Result<String> open(@Valid OpenAction openAction) {
        RLock rLock = null;
        boolean lockResult = false;
        try {
        	String partyId = SecurityUtils.getUser().getUserId();
            rLock = redissonClient.getLock("contract_open_" + partyId);
            
            lockResult = rLock.tryLock(5, TimeUnit.SECONDS);
            if (!lockResult) {
                // 请稍后再试
                throw new YamiShopBindException("Please try again later");
            }

            User user = userService.getById(partyId);
            if (!user.isEnabled()) {
                // 用户已锁定
                throw new YamiShopBindException("User has been locked");
            }

            Syspara syspara = sysparaService.find("stop_user_internet");
            String stopUserInternet = syspara.getSvalue();
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
                String[] stopUsers = stopUserInternet.split(",");

                System.out.println("userName = " + user.getUserName());
                System.out.println("stopUserInternet = " + stopUserInternet);

                if(Arrays.asList(stopUsers).contains(user.getUserName())){
                    // 无网络
                    throw new YamiShopBindException("No network connection");
                }
            }
			List<Map<String, Object>> list = this.contractOrderService.findSubmittedRedis(partyId, openAction.getSymbol());
			if (ObjectUtils.isNotEmpty(list)) {
				for (Map<String, Object> map :list) {
					if(!openAction.getDirection().equals(map.get("direction"))) {
                        // 同一币种不允许多空双开
						throw new YamiShopBindException("Cannot open both long and short positions");
					}
				}
			}

            ContractApplyOrder order = new ContractApplyOrder();
            order.setPartyId(partyId);
            order.setSymbol(openAction.getSymbol());
            order.setDirection(openAction.getDirection());
            order.setOffset(ContractApplyOrder.OFFSET_OPEN);
            order.setVolume(openAction.getAmount());
            order.setVolumeOpen(openAction.getAmount());
            order.setLeverRate(openAction.getLever_rate());
            order.setPrice(openAction.getPrice());
            order.setStopPriceProfit(openAction.getStop_price_profit());
            order.setStopPriceLoss(openAction.getStop_price_loss());
            order.setOrderPriceType(openAction.getPrice_type());
            order.setState(ContractApplyOrder.STATE_SUBMITTED);
            this.contractApplyOrderService.saveCreate(order);
            // 放入缓存
            RedisUtil.sadd(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, order.getUuid());
            log.info("-------> 产生一张 apply order，录入缓存:{}", order.getUuid());
        } catch (Exception e) {
           log.error("下单失败" , e);
            return Result.failed(e.getMessage());
        } finally {
            if (lockResult) {
                rLock.unlock();
            }
        }

        return Result.succeed(null,"ok");
    }

    /**
     * 平仓
     */
    @RequestMapping(action + "close.action")
    public Result<String> close(@Valid CloseAction closeAction) throws InterruptedException {
        String partyId = SecurityUtils.getUser().getUserId();
        RLock rLock = redissonClient.getLock("contract_close_" + partyId);
        boolean lockResult = rLock.tryLock(5, TimeUnit.SECONDS);
        if (!lockResult) {
// 请稍后再试
            throw new YamiShopBindException("Please try again later");
        }
        try {
            User user = userService.getById(partyId);
            if (!user.isEnabled()) {
// 用户已锁定
                throw new YamiShopBindException("User has been locked");
            }

            Syspara syspara = sysparaService.find("stop_user_internet");
            String stopUserInternet = syspara.getSvalue();
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
                String[] stopUsers = stopUserInternet.split(",");

                System.out.println("userName = " + user.getUserName());
                System.out.println("stopUserInternet = " + stopUserInternet);

                if(Arrays.asList(stopUsers).contains(user.getUserName())){
// 无网络
                    throw new YamiShopBindException("No network connection");
                }
            }

            ContractApplyOrder order = new ContractApplyOrder();
            order.setPartyId(partyId);
            order.setSymbol(closeAction.getSymbol());
            order.setDirection(closeAction.getDirection());
            order.setOffset(ContractApplyOrder.OFFSET_OPEN);
            order.setVolume(closeAction.getAmount());
            order.setVolumeOpen(closeAction.getAmount());
            order.setPrice(closeAction.getPrice());
            order.setOrderPriceType(closeAction.getPrice_type());
            order.setState(ContractApplyOrder.STATE_SUBMITTED);
            this.contractApplyOrderService.saveCreate(order);
            // 放入缓存
            RedisUtil.sadd(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, order.getUuid());
        } catch (Exception e) {
            log.error("平仓失败" , e);
            return Result.failed(e.getMessage());
        } finally {
            rLock.unlock();
        }
        return Result.succeed(null,"ok");
    }

    /**
     * 订单表列表数据
     *
     * @return
     */
    @ApiOperation(value = "查询订单表列表数据")
    @RequestMapping(action + "list.action")
    public Result<List<Map<String, Object>>> list(@RequestParam(value = "page_no", required = false,defaultValue = "1") Integer pageNo,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(required = false) String symbol,  @RequestParam(required = false)String startTime,
                                                  @RequestParam(required = false)String endTime,@RequestParam(required = false)String symbolType

    ) throws Exception {
        Page<ContractApplyOrder> page = new Page<>();
        if ("orders".equals(type)) {
            page.setSize(1000);
        }else{
            page.setSize(10);
        }
        Page<Map<String, Object>> result = contractApplyOrderService.findList(page, SecurityUtils.getUser().getUserId(), symbol, type, startTime, endTime, symbolType);

        Result<List<Map<String, Object>>> succeed = Result.succeed(result.getRecords());
        succeed.setTotal(result.getTotal());
        return succeed;
    }

    /**
     * 查询订单详情
     * order_no 订单号
     */
    @RequestMapping(action + "get.action")
    public Result<Map<String, Object>> get(@RequestParam @NotBlank String order_no) {

        ContractApplyOrder order = this.contractApplyOrderService.findByOrderNo(order_no);
        if (order == null) {
// 委托单不存在
            throw new YamiShopBindException("Order does not exist");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_no", order.getOrderNo());
        Item bySymbol = itemService.findBySymbol(order.getSymbol());
        map.put("name", bySymbol.getName());
        map.put("symbol", order.getSymbol());

        long showCreateTime = DateTimeTools.transferShowTimeToClientTime(order.getCreateTimeTs());
        map.put("create_time_ts", showCreateTime);
        map.put("volume", order.getVolume());
        map.put("volume_open", order.getVolumeOpen());
        map.put("direction", order.getDirection());
        map.put("offset", order.getOffset());
        map.put("lever_rate", order.getLeverRate());
        map.put("price", order.getPrice());
        map.put("stop_price_profit", order.getStopPriceProfit());
        if( order.getStopPriceLoss() !=null) {
            map.put("stop_price_loss", order.getStopPriceLoss().setScale(4, RoundingMode.FLOOR));
        }else{
            map.put("stop_price_loss", null);
        }
        map.put("price_type", order.getOrderPriceType());
        map.put("state", order.getState());
        map.put("amount", order.getVolume().multiply(order.getUnitAmount()).setScale(4, RoundingMode.FLOOR));
        map.put("amount_open", order.getVolumeOpen().multiply(order.getUnitAmount()));
        map.put("fee", order.getFee());
        map.put("deposit", order.getDeposit());
        return Result.succeed(map);
    }

    /**
     * 撤单
     * order_no 订单号
     */
    @RequestMapping(action + "cancel.action")
    public Result<String> cancel(@RequestParam @NotBlank String order_no) {
        try {
            CancelDelayThread lockDelayThread = new CancelDelayThread(SecurityUtils.getUser().getUserId(), order_no, this.contractApplyOrderService, false);
            Thread t = new Thread(lockDelayThread);
            t.start();
        } catch (Exception e) {
            log.error("执行撤单异常", e);
// 执行撤单异常
            throw new YamiShopBindException("Order cancellation failed");
        }
        return Result.succeed(null,"success");
    }

    /**
     * 一键撤单
     */
    @RequestMapping(action + "cancelAll.action")
    public Result<String> cancelAll() {
        try {
            CancelDelayThread lockDelayThread = new CancelDelayThread(SecurityUtils.getUser().getUserId(), "", this.contractApplyOrderService, true);
            Thread t = new Thread(lockDelayThread);
            t.start();
        } catch (Exception e) {
            log.error("执行撤单异常", e);
// 执行撤单异常
            throw new YamiShopBindException("Order cancellation failed");
        }
        return Result.succeed(null,"success");
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CancelDelayThread implements Runnable {
        private String partyId;
        private String order_no;
        private ContractApplyOrderService contractApplyOrderService;
        private boolean all = false;
        @Override
        public void run() {
            try {
                while (true) {
                    if (true == all) {
                        // 一键撤单
                        // if (ContractLock.add("all")) {
                        if (contractLockService.getContractLock("all")) {
                            this.contractApplyOrderService.saveCancelAllByPartyId(partyId);
                            // 处理完退出
                            break;
                        }
                        ThreadUtils.sleep(100);
                    } else {
                        // if (ContractLock.add(order_no)) {
                        if (contractLockService.getContractLock(order_no)) {
                            this.contractApplyOrderService.saveCancel(partyId, order_no);
                            // 处理完退出
                            break;
                        }
                        ThreadUtils.sleep(100);
                    }
                }

            } catch (Throwable t) {
                log.error("error:", t);
            } finally {
                ThreadUtils.sleep(100);
                if (true == all) {
                    // ContractLock.remove("all");
                    contractLockService.removeContractLock("all");
                } else {
                    // ContractLock.remove(order_no);
                    contractLockService.removeContractLock(order_no);
                }
            }
        }

        public CancelDelayThread(String partyId, String order_no, ContractApplyOrderService contractApplyOrderService, boolean all) {
            this.partyId = partyId;
            this.order_no = order_no;
            this.contractApplyOrderService = contractApplyOrderService;
            this.all = all;
        }
    }


    /**
     * 订单列表
     * page_no 页码
     * symbol 币种
     * type 查询类型：orders 当前持仓单；hisorders 历史持仓单；
     */
    @RequestMapping(action + "assets.action")
    public Result<Map<String, Object>> assets(HttpServletRequest request) {
        String page_no = request.getParameter("page_no");
        if(StringUtils.isEmptyString(page_no)){
            page_no="1";
        }
        String type = request.getParameter("type");
        String symbolType = request.getParameter("symbolType");
        if(StringUtils.isEmptyString(symbolType)){
            symbolType = Item.forex;
        }
        Page<ContractApplyOrder> page = new Page<>();
        if ("orders".equals(type)) {
            page.setSize(1000);
        }else{
            page.setSize(100);
        }
        Page<ContractApplyOrder> result = contractApplyOrderService.findList(page, SecurityUtils.getUser().getUserId(), type,  symbolType);
        List<ContractApplyOrder> datas = result.getRecords();
        if (!StringUtils.isInteger(page_no)) {
// 页码不是整数
            throw new YamiShopBindException("Page number must be an integer");
        }
        if (Integer.valueOf(page_no).intValue() <= 0) {
// 页码不能小于等于0
            throw new YamiShopBindException("Page number must be greater than 0");
        }
        Long count = 0L;


        String symbolsStr = "";
        Set<String> symbols = new HashSet<String>();
        for (int i = 0; i < datas.size(); i++) {
            String sym = datas.get(i).getSymbol();
            if (!symbols.contains(sym)) {
                symbols.add(sym);
                if (i != 0) {
                    symbolsStr = symbolsStr + "," + sym;
                } else {
                    symbolsStr = sym;
                }
            }
        }

        List<Realtime> realtime_all = dataService.realtime(symbolsStr);
        if (realtime_all.size() <= 0) {
            realtime_all = new ArrayList<>();
        }

        Map<String, Realtime> realtimeMap = new HashMap<>();
        for (int i = 0; i < realtime_all.size(); i++) {
            realtimeMap.put(realtime_all.get(i).getSymbol(), realtime_all.get(i));
        }

        double closeAll = 0;
        double inventory_charge_all = 0;
        double fee_all = 0;
        double deposit_all = 0;
        double expectedProfitAndLoss = 0;

        for (int i = 0; i < datas.size(); i++) {
            // 标记价格
            ContractApplyOrder data = datas.get(i);
            Realtime realtime = realtimeMap.get(data.getSymbol());
            BigDecimal mark_price = BigDecimal.valueOf(realtime.getClose());
            if (data.getDirection().equalsIgnoreCase("buy")){
                expectedProfitAndLoss += data.getVolume().multiply(data.getLeverRate()).multiply(data.getUnitAmount()).multiply(mark_price.subtract(data.getPrice()).divide(data.getPrice(), 4, RoundingMode.HALF_UP)).doubleValue();
            }else{
                expectedProfitAndLoss -= data.getVolume().multiply(data.getLeverRate()).multiply(data.getUnitAmount()).multiply(mark_price.subtract(data.getPrice()).divide(data.getPrice(), 4, RoundingMode.HALF_UP)).doubleValue();
            }
            //手续费
            double fee = Double.parseDouble(data.getFee().toString());
            fee_all += fee;

            //保证金
            double deposit = Double.parseDouble(data.getDeposit().toString());
            deposit_all += deposit;
        }

        Map<String, Object> dateN = new HashMap<>();
        dateN.put("profit_all", Double.valueOf(String.format("%.5f", 0.0)));                //利润
        dateN.put("cash_deposit", Double.valueOf(String.format("%.5f", closeAll)));                //入金
        dateN.put("inventory_charge_all", Double.valueOf(String.format("%.5f", inventory_charge_all)));        //库存费
        dateN.put("fee_all", fee_all);                    //手续费
        dateN.put("deposit_all", Double.valueOf(String.format("%.5f", deposit_all)));
        dateN.put("profitAndLoss", Double.valueOf(String.format("%.4f", 0.0D)));
        dateN.put("expectedProfitAndLoss", Double.valueOf(String.format("%.2f", expectedProfitAndLoss)));
        dateN.put("count", count);
        return Result.ok(dateN);
    }
}
