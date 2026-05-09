package com.yami.trading.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.dto.FutureOpenAction;
import com.yami.trading.bean.future.domain.FuturesLock;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.future.domain.FuturesPara;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.future.FuturesOrderService;
import com.yami.trading.service.future.FuturesParaService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交割合约订单
 */
@Api(tags = "【h5】交割合约订单")
@RestController
@CrossOrigin
@Slf4j
public class ApiFuturesOrderController {
    public final static String action = "/api/futuresOrder!";
    @Autowired
    SessionTokenService sessionTokenService;
    @Autowired
    WalletService walletService;
    @Autowired
    UserService partyService;
    @Autowired
    ItemService itemService;
    @Autowired
    FuturesOrderService futuresOrderService;
    @Autowired
    FuturesParaService futuresParaService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    TipService tipService;
//    @Autowired
//    private ExchangeRateService exchangeRateService;

    /**
     * 开仓页面参数
     * <p>
     * symbol 币种
     */
    @GetMapping(action + "openview.action")
    @ApiOperation(value = "开仓页面参数")
    public Result<Map<String, Object>> openview(@RequestParam @NotEmpty String symbol) {
        Map<String, Object> data = new HashMap<>();
        Item bySymbol = itemService.findBySymbol(symbol);
        if (bySymbol == null) {
// 当前币对不存在
            throw new YamiShopBindException("Current trading pair does not exist");
        }

        Date now = new Date();
        List<Map<String, Object>> futuresParas = new ArrayList<>();
        for (FuturesPara para : this.futuresParaService.getBySymbolSort(symbol)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("para_id", para.getUuid());
            map.put("symbol", para.getSymbol());
            map.put("time_num", para.getTimenum());
            map.put("time_unit", para.getTimeunit());
            map.put("entityVersion", 0);
            map.put("timeUnitCn", para.getTimeunit());
            map.put("timestamp", para.getCreateTimeTs());
            map.put("now_time", now);
            DecimalFormat df = new DecimalFormat("#");
            if (para.getProfitRatio().doubleValue() < 0.01 || para.getProfitRatioMax().doubleValue() < 0.01) {
                df = new DecimalFormat("#.#");
            }
            // TMX start voaex
//		map.put("profit_ratio", df.format(Arith.mul(para.getProfit_ratio(), 100)));
            // TMX end
//		if(para.getProfit_ratio() != para.getProfit_ratio_max()) {
            map.put("profit_ratio", df.format(Arith.mul(para.getProfitRatio().doubleValue(), 100)) + "~"
                    + df.format(Arith.mul(para.getProfitRatioMax().doubleValue(), 100)));
//		}
            map.put("buy_min", para.getUnitAmount());
            map.put("unit_fee", para.getUnitFee());
            map.put("buy_max", para.getUnitMaxAmount().compareTo(BigDecimal.ZERO) <= 0 ? null : para.getUnitMaxAmount());
            futuresParas.add(map);
        }
        data.put("para", futuresParas);

        String partyId = SecurityUtils.getCurrentUserId();
        if (StrUtil.isNotBlank(partyId) && futuresParas != null) {
            Wallet wallet = this.walletService.findByUserId(partyId);
            // 账户剩余资金
            String session_token = this.sessionTokenService.savePut(partyId);
            data.put("session_token", session_token);
//            data.put("amount", exchangeRateService.getCurrencyByType(wallet.getMoney(), bySymbol.getType()).doubleValue());
            data.put("amount", wallet.getMoney());
        } else {
            data.put("amount", 0);
        }
        data.put("open", MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType()));
        return Result.succeed(data);
    }

    /**
     * 开仓
     * symbol 币种
     * direction "buy":多 "sell":空
     * amount 委托数量(张)
     * para_id 交割合约参数
     */
    @GetMapping(action + "open.action")
    @ApiOperation(value = "开仓")
    public Result<Map<String, String>> open(FutureOpenAction futureOpenAction) {
        Item bySymbol = itemService.findBySymbol(futureOpenAction.getSymbol());
        if (bySymbol == null) {
// 当前币对不存在
            throw new YamiShopBindException("Current trading pair does not exist");
        }
        boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType());
        if (!isOpen) {
// 当前已经休市
            throw new YamiShopBindException("Market is currently closed");
        }

        String partyId = SecurityUtils.getCurrentUserId();
        boolean lock = false;
        try {
            Map<String, String> data = new HashMap<>();

            if (!FuturesLock.add(partyId)) {
// 请稍后再试
                throw new YamiShopBindException("Please try again later");
            }

            lock = true;
            String session_token = futureOpenAction.getSession_token();
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.del(session_token);
            User party = this.partyService.findUserByUserCode(partyId);
            if (!party.isEnabled()) {
// 用户已锁定
                throw new YamiShopBindException("User has been locked");
            }
            if (null == object || !party.getUserId().equals((String) object)) {
                throw new BusinessException("请稍后再试");
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

            Date now = new Date();
            FuturesOrder order = new FuturesOrder();
            order.setPartyId(partyId);
            order.setSymbol(futureOpenAction.getSymbol());
            order.setDirection(futureOpenAction.getDirection());
            order.setVolume(futureOpenAction.getAmount().doubleValue());
            order.setCreateTime(now);
            order.setCreateTimeTs(now.getTime() / 1000L);
            order.setUpdateTime(now);
            order.setUpdateTimeTs(now.getTime() / 1000L);

            order = this.futuresOrderService.saveOpen(order, futureOpenAction.getPara_id());
            data.put("order_no", order.getOrderNo());
            data.put("open_price", order.getTradeAvgPrice().toString());
            return Result.succeed(data);
        } catch (Exception e) {
            log.error("开仓异常", e);
            throw new YamiShopBindException(e.getMessage());
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                FuturesLock.remove(partyId);
            }
        }
    }

    /**
     * 查询交割持仓列表
     * <p>
     * page_no 页码
     * symbol 币种
     * type 开仓页面订单类型
     */
    @RequestMapping(action + "list.action")
    @ApiOperation("查询交割持仓列表,实时价格，通过价格接口取")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) String symbol,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(required = false) String page_no
            , @RequestParam(required = false) String date,
                                                  @RequestParam(required = false) String startTime,
                                                  @RequestParam(required = false) String endTime,
                                                  @RequestParam(required = false) String symbolType) {
        IPage<FuturesOrder> page = new Page<>();
        if (StringUtils.isEmptyString(page_no)) {
            page.setCurrent(1);
        } else {
            page.setCurrent(Long.parseLong(page_no));
            ;
        }
        String loginPartyId = SecurityUtils.getCurrentUserId();
        IPage<FuturesOrder> paged = this.futuresOrderService.getPaged(page, loginPartyId, symbol, type, date, startTime, endTime, symbolType);
        List<FuturesOrder> list = paged.getRecords();
        Result<List<Map<String, Object>>> succeed = Result.succeed(futuresOrderService.bulidData(list));
        succeed.setTotal(paged.getTotal());
        return succeed;
    }

    /**
     * 查询交割持仓详情
     * <p>
     * order_no 订单号
     */
    @RequestMapping(action + "get.action")
    public Result<Map<String, Object>> get(@RequestParam @NotEmpty String order_no) {
        FuturesOrder order = this.futuresOrderService.cacheByOrderNo(order_no);
        if (null == order) {
            log.info("futuresOrder!get order_no:" + order_no + ", order null");
// 订单不存在
            throw new YamiShopBindException("Order does not exist");
        }
        return Result.succeed(this.futuresOrderService.bulidOne(order));
    }

}
