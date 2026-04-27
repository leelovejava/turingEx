package com.yami.trading.service.future;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.future.domain.FuturesLock;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.future.domain.FuturesPara;
import com.yami.trading.bean.future.domain.FuturesRedisKeys;
import com.yami.trading.bean.future.domain.ProfitLossConfig;
import com.yami.trading.bean.future.dto.TFuturesOrderDTO;
import com.yami.trading.bean.future.query.FuturesOrderQuery;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.TipConstants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.dao.future.FuturesOrderMapper;
import com.yami.trading.service.RechargeBonusService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 交割合约订单Service
 *
 * @author lucas
 * @version 2023-04-08
 */
@Service
@Transactional
public class FuturesOrderService extends ServiceImpl<FuturesOrderMapper, FuturesOrder> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostConstruct
    public void init() {
        List<FuturesOrder> list = this.findSubmitted(null);
        for (FuturesOrder order : list) {
            cache.put(order.getOrderNo(), order);
        }
    }

    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserRecomService userRecomService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private FuturesParaService futuresParaService;
    @Autowired(required = false)
    @Qualifier("dataService")
    private DataService dataService;
    @Autowired
    private TipService tipService;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RechargeBonusService rechargeBonusService;

    @Autowired
    private ProfitLossConfigService profitLossConfigService;

//    @Autowired
//    private ExchangeRateService exchangeRateService;

    protected Map<String, FuturesOrder> cache = new ConcurrentHashMap<String, FuturesOrder>();

    public IPage<FuturesOrder> getPaged(IPage<FuturesOrder> page, String loginPartyId, String symbol, String type, String date, String startTime, String endTime, String symbolType) {
        String state = "";
        if ("orders".equals(type)) {
            state = FuturesOrder.STATE_SUBMITTED;
        } else if ("hisorders".equals(type)) {
            state = FuturesOrder.STATE_CREATED;
        }
        QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        queryWrapper.eq(StrUtil.isNotBlank(state), "state", state);
        queryWrapper.eq(StringUtils.isNotEmpty(date), "date_format(create_time,'%Y-%m-%d')", date);

        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        // 如果没有传symbol，才需要做symbolType过滤
        if (StringUtils.isNotEmpty(symbolType) && StringUtils.isEmptyString(symbol)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }
        queryWrapper.ge(StrUtil.isNotBlank(startTime), "create_time", startTime + " 00:00:00");
        queryWrapper.le(StrUtil.isNotBlank(endTime), "create_time", endTime + " 23:59:59");
        queryWrapper.orderByDesc("create_time");

        queryWrapper.eq("party_id", loginPartyId);
        return baseMapper.selectPage(page, queryWrapper);
    }

    public List<Map<String, Object>> bulidData(List<FuturesOrder> list) {
        List<Map<String, Object>> data = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            FuturesOrder order = list.get(i);
            Map<String, Object> map = bulidOne(order);
            data.add(map);
        }
        return data;
    }

    public IPage<TFuturesOrderDTO> listRecord(Page page, FuturesOrderQuery query) {
        IPage<TFuturesOrderDTO> tFuturesOrderDTOIPage = baseMapper.listRecord(page, query);
        Map<String, FuturesOrder> orderNoSubmitted = findSubmitted(null).stream().collect(Collectors.toMap(FuturesOrder::getOrderNo, f -> f));
        tFuturesOrderDTOIPage.getRecords().forEach(dto -> {
            String timeUnitCn = FuturesPara.TIMENUM.valueOf(dto.getTimeunit()).getCn();
            FuturesOrder order = orderNoSubmitted.get(dto.getOrderNo());
            if (order == null) {
                dto.setRemainTime("0:0:0");
            }
            dto.setTimenumStr(dto.getTimenum() + timeUnitCn);
        });
        return tFuturesOrderDTOIPage;
    }

    public List<FuturesOrder> findSubmitted(String orderNo) {
        QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", FuturesOrder.STATE_SUBMITTED);
        queryWrapper.eq(StrUtil.isNotBlank(orderNo), "order_no", orderNo);
        return list(queryWrapper);
    }
    public List<FuturesOrder> findSubmitted(String partyID, List<String> symbols) {
        symbols.add("-1");
        QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyID);
        queryWrapper.eq("state", FuturesOrder.STATE_SUBMITTED);
        queryWrapper.in( "symbol", symbols);
        return list(queryWrapper);
    }

    public FuturesOrder findSubmittedByOrderNo(String orderNo) {
        List<FuturesOrder> submitted = findSubmitted(orderNo);
        if (CollectionUtil.isEmpty(submitted)) {
            return null;
        }
        return submitted.get(0);

    }
    public FuturesOrder cacheByOrderNo(String order_no) {
        FuturesOrder futuresOrder = (FuturesOrder) RedisUtil
                .get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order_no);
//		FuturesOrder futuresOrder = cache.get(order_no);
        if (null == futuresOrder) {
            QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no", order_no);
            return getOne(queryWrapper);
        }
        return futuresOrder;
    }
    /**
     * 修改订单场控
     *
     * @param orderNo
     * @param porfitOrLoss
     * @param operaName
     * @return
     */
    public String saveOrderPorfitOrLoss(String orderNo, String porfitOrLoss, String operaName) {
        String message = "";
        boolean lock = false;
        while (true) {
            try {

                if (!FuturesLock.add(orderNo)) {
                    continue;
                }
                lock = true;

                FuturesOrder futuresOrder = (FuturesOrder) RedisUtil.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + orderNo);
                if (futuresOrder == null) {
                    message = "订单已结算或不存在";
                    break;
                }

                // 获取 单个订单 交割合约总资产、总未实现盈利
                Map<String, Double> futuresAssetsOld = this.walletService.getMoneyFuturesByOrder(futuresOrder);

                String oldProfitLoss = futuresOrder.getProfitLoss();
                futuresOrder.setProfitLoss(porfitOrLoss);

                RedisUtil.set(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + futuresOrder.getOrderNo(), futuresOrder);
                cache.put(futuresOrder.getOrderNo(), futuresOrder);

                Double futuresAssets = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + futuresOrder.getPartyId().toString());
                Double futuresAssetsProfit = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + futuresOrder.getPartyId().toString());

                // 获取 单个订单 交割合约总资产、总未实现盈利
                Map<String, Double> futuresAssetsOrder = this.walletService.getMoneyFuturesByOrder(futuresOrder);

                RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + futuresOrder.getPartyId().toString(),
                        Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures") - futuresAssetsOld.get("money_futures")));
                RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + futuresOrder.getPartyId().toString(),
                        Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit") - futuresAssetsOld.get("money_futures_profit")));

                User party = userService.getById(futuresOrder.getPartyId());
                Log log = new Log();
                log.setCategory(Constants.LOG_CATEGORY_OPERATION);
                log.setOperator(operaName);
                log.setUsername(party.getUserName());
                log.setUserId(party.getUserId());
                log.setCreateTime(new Date());
                log.setLog("手动修改交割订单场控。订单号[" + futuresOrder.getOrderNo() + "],原订单场控["
                        + Constants.PROFIT_LOSS_TYPE.get(oldProfitLoss) + "],修改后订单场控为["
                        + Constants.PROFIT_LOSS_TYPE.get(porfitOrLoss) + "].");
                this.logService.save(log);
                // todo 老代码没有入库
                updateById(futuresOrder);
                /**
                 * 100毫秒业务处理
                 */
                ThreadUtils.sleep(100);
            } catch (Throwable e) {
                log.error("error:", e);
                message = "修改错误";
            } finally {
                if (lock) {
                    FuturesLock.remove(orderNo);
                    break;
                }

            }
        }
        return message;
    }

    /**
     * 业绩交易奖励
     */
    public void saveRecomProfit(String partyId, BigDecimal volume) {
        String futures_bonus_parameters = sysparaService.find("futures_bonus_parameters").getSvalue();
        if (StrUtil.isEmpty(futures_bonus_parameters)) {
            return;
        }
        String[] futuresBonusArray = futures_bonus_parameters.split(",");
        List<UserRecom> listParents = this.userRecomService.getParents(partyId);
        if (listParents.size() == 0) {
            return;
        }

        int loop = 0;
        int loopMax = futuresBonusArray.length;
//		int loopMax = 3;
        for (int i = 0; i < listParents.size(); i++) {
            if (loop >= loopMax) {
                break;
            }
            String recomUserId = listParents.get(i).getRecomUserId();
            User party_parent = this.userService.getById(recomUserId);
            if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
                continue;
            }
            loop++;
            BigDecimal pip_amount = new BigDecimal(futuresBonusArray[i]);
            BigDecimal getMoney = volume.multiply(pip_amount);
            walletService.updateMoney("",recomUserId, getMoney, BigDecimal.ZERO,
                    Constants.MONEYLOG_CATEGORY_REWARD, Constants.WALLET, Constants.MONEYLOG_CONTENT_REWARD, "第" + (i + 1) + "代用户产生了交易，佣金收益[" + getMoney + "]");
            ThreadUtils.sleep(200);
        }
    }


    public FuturesOrder saveOpen(FuturesOrder futuresOrder, String paraId) {

        Item item = this.itemService.findBySymbol(futuresOrder.getSymbol());
        if (item == null) {
            throw new BusinessException("参数错误");
        }

        FuturesPara futuresPara = this.futuresParaService.getById(paraId);
        if (futuresPara == null) {
            throw new BusinessException("参数错误");
        }

        List<Realtime> realtime_list = this.dataService.realtime(futuresOrder.getSymbol());
        Realtime realtime = null;
        if (realtime_list.size() > 0) {
            realtime = realtime_list.get(0);
        }
        if (null == realtime) {
            throw new BusinessException(1, "请稍后再试");
        }

        Double volume = futuresOrder.getVolume();
        if (volume < futuresPara.getUnitAmount()) {
            throw new BusinessException("下单不能小于最小金额限制");
        }
        if (futuresPara.getUnitMaxAmount().doubleValue() > 0 && volume > futuresPara.getUnitMaxAmount().doubleValue()) {
            throw new BusinessException("金额不在购买区间");
        }
        checkSubmitOrder(futuresOrder.getPartyId().toString(), futuresPara);

        futuresOrder.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        futuresOrder.setTimenum(futuresPara.getTimenum());
        futuresOrder.setTimeunit(futuresPara.getTimeunit());

        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat df1 = new DecimalFormat("#.####");
        futuresOrder.setFee(Double.valueOf(df.format(Arith.mul(futuresPara.getUnitFee().doubleValue(), volume))));
        /**
         * 随机生成
         */
        // 生成5-26之间的随机数，包括26
        // int randNum = rand.nextInt(22)+5;
        int result = (int) Arith.mul(futuresPara.getProfitRatio().doubleValue(), 10000)
                + (int) (Math.random() * (((int) Arith.mul(futuresPara.getProfitRatioMax().doubleValue(), 10000)
                - (int) Arith.mul(futuresPara.getProfitRatio().doubleValue(), 10000)) + 1));
        futuresOrder.setProfitRatio(Double.parseDouble(df1.format(Arith.div(result, 10000))));
        futuresOrder.setTradeAvgPrice(realtime.getClose());
        futuresOrder.setCloseAvgPrice(realtime.getClose());
        futuresOrder.setCreateTime(new Date());
        futuresOrder.setState(FuturesOrder.STATE_SUBMITTED);
        futuresOrder.setCreateTimeTs(System.currentTimeMillis() / 1000);

        Integer profitRatioCardinality = Objects.isNull(futuresPara.getProfitRatioCardinality()) ? 1000 : futuresPara.getProfitRatioCardinality();
        futuresOrder.setProfitRatioCardinality(profitRatioCardinality);
        futuresOrder.setFuturesParaId(paraId);


        switch (futuresPara.getTimeunit()) {
            case FuturesPara.TIMENUM_SECOND:
                futuresOrder
                        .setSettlementTime(futuresOrder.getCreateTimeTs() + futuresPara.getTimenum());
                break;
            case FuturesPara.TIMENUM_MINUTE:

                futuresOrder
                        .setSettlementTime(futuresOrder.getCreateTimeTs() + futuresPara.getTimenum() * 60);
                break;
            case FuturesPara.TIMENUM_HOUR:
                futuresOrder.setSettlementTime(futuresOrder.getCreateTimeTs() + futuresPara.getTimenum() * 60 * 60);
                break;
            case FuturesPara.TIMENUM_DAY:
                futuresOrder.setSettlementTime(futuresOrder.getCreateTimeTs() + futuresPara.getTimenum() * 60 * 60 * 24);
                break;
        }
        //汇率转换
        String type = item.getType();
//        double fee = exchangeRateService.getUsdtByType(BigDecimal.valueOf(futuresOrder.getFee()), type).doubleValue();
//        volume = exchangeRateService.getUsdtByType(BigDecimal.valueOf(volume), type).doubleValue();
        BigDecimal moneyCost = new BigDecimal(volume).add(BigDecimal.valueOf(futuresOrder.getFee())).negate();
        // 钱包扣费
        walletService.updateMoney(futuresOrder.getSymbol(), futuresOrder.getPartyId(), moneyCost, BigDecimal.ZERO,
                Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET, Constants.DELIVERY_MONEYLOG_CONTENT_CONTRACT_OPEN, "交割合约，订单号[" + futuresOrder.getOrderNo() + "]");
        checkProfitAndLoss(futuresOrder);
        save(futuresOrder);

        this.refreshCache(futuresOrder, realtime.getClose());

        //
        //ICE盘定制交易手续费返佣
        rechargeBonusService.saveTradeBounsHandle(futuresOrder.getPartyId(),1, futuresOrder.getFee(),futuresOrder.getOrderNo(),futuresOrder.getSymbol());

        User party = this.userService.getById(futuresOrder.getPartyId());
        if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
            
        //     tipService.saveTip(futuresOrder.getUuid().toString(), TipConstants.FUTURES_ORDER,
        //             party.getUserId());
            Syspara isUnionStocks = sysparaService.find("is-union-stocks");
            if(isUnionStocks != null && isUnionStocks.getBoolean()){
                // 提示
                if(TipConstants.ACTION_FUTURES_ITEM_MAP_NEW.get(item.getType()) != null){
                    tipService.saveTip(futuresOrder.getUuid(), TipConstants.ACTION_FUTURES_ITEM_MAP_NEW.get(item.getType()),party.getUserId());
                }
            }else{
                // 提示
            if (TipConstants.ACTION_FUTURES_ITEM_MAP.get(type) != null) {
                tipService.saveTip(futuresOrder.getUuid(), TipConstants.ACTION_FUTURES_ITEM_MAP.get(type), party.getUserId());
                }
            }
        }



//		saveRecomProfit(futuresOrder.getPartyId().toString(),futuresOrder.getVolume());
        return futuresOrder;
    }


    private String fomatTime(long time) {
        long h = time >= 3600D ? new Double(Math.floor(Arith.div(time, 3600D, 2))).longValue() : 0L;
        long m = time - (h * 3600D) >= 60D ? new Double(Math.floor(Arith.div(time - (h * 3600D), 60D, 2))).longValue() : 0L;
        long s = new Double(time - (h * 3600D + m * 60D)).longValue();
        if (s < 0) {
            s = 0;
        }
        return String.format("%d:%d:%d", h, m, s);
    }

    /**
     * 缓存计算更新
     *
     * @param order
     * @param close
     */
    public void refreshCache(FuturesOrder order, Double close) {
        if (order.getState().equals(FuturesOrder.STATE_CREATED)) {
            return;
        }

        long remain_time = DateUtils.calcTimeBetween("s", new Date(), order.getSettlementTime()*1000);
        order.setRemainTime(fomatTime(remain_time));
        order.setCloseAvgPrice(close);

        double futures_loss_part = Double.valueOf(this.sysparaService.find("futures_loss_part").getSvalue());

        double closeAvgPrice = order.getCloseAvgPrice().doubleValue();
        double tradeAvgPrice = order.getTradeAvgPrice().doubleValue();
        if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
            /*
             * 0 买涨
             */
            if (closeAvgPrice >= tradeAvgPrice) {
                DecimalFormat df = new DecimalFormat("#.##");
                order.setProfit(Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfitRatio()))));
            }

            if (closeAvgPrice <= tradeAvgPrice) {
                if (futures_loss_part == 2) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    order.setProfit(Arith.sub(0, Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfitRatio())))));
                } else {
                    order.setProfit(Arith.sub(0, order.getVolume()));
                }
            }
        } else {
            /*
             * 1 买跌
             */
            if (closeAvgPrice <= tradeAvgPrice) {
                DecimalFormat df = new DecimalFormat("#.##");
                order.setProfit(Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfitRatio()))));
            }
            if (closeAvgPrice >= tradeAvgPrice) {
                if (futures_loss_part == 2) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    order.setProfit(Arith.sub(0, Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfitRatio())))));
                } else {
                    order.setProfit(Arith.sub(0, order.getVolume()));
                }
            }
        }

        FuturesOrder futuresOld = (FuturesOrder) RedisUtil.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrderNo());

        RedisUtil.set(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrderNo(), order);
        cache.put(order.getOrderNo(), order);
        // 暂时先更新下，有问题再看 todo
        updateById(order);


        Object futuresAssetsObj = RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString());
        Object futuresAssetsProfitObj = RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
        Double futuresAssets;
        Double futuresAssetsProfit;
        if (futuresAssetsObj instanceof BigDecimal) {
            futuresAssets = ((BigDecimal) futuresAssetsObj).doubleValue();
        } else {
            futuresAssets = (Double) futuresAssetsObj;
        }
        if (futuresAssetsProfitObj instanceof BigDecimal) {
            futuresAssetsProfit = ((BigDecimal) futuresAssetsProfitObj).doubleValue();
        } else {
            futuresAssetsProfit = (Double) futuresAssetsProfitObj;
        }
        Map<String, Double> futuresAssetsOrder = this.walletService.getMoneyFuturesByOrder(order);

        if (null != futuresOld) {
            // 获取 单个订单 交割合约总资产、总未实现盈利
            Map<String, Double> futuresAssetsOld = this.walletService.getMoneyFuturesByOrder(futuresOld);

            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures") - futuresAssetsOld.get("money_futures")));
            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit") - futuresAssetsOld.get("money_futures_profit")));
        } else {
            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures")));
            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit")));
        }
    }

    /**
     * 购买时检查是否有全局场控配置
     *
     * @param order
     */
    public void checkProfitAndLoss(FuturesOrder order) {
        Syspara profit_loss_symbol = sysparaService.find("profit_loss_symbol");
        if (profit_loss_symbol == null) {
            return;
        }
        String ProfitLoss_symbol = profit_loss_symbol.getSvalue();
        if (StrUtil.isEmpty(ProfitLoss_symbol) || !order.getSymbol().equals(ProfitLoss_symbol)) {
            return;
        }
        Syspara profit_loss_type = sysparaService.find("profit_loss_type");
        if (profit_loss_type == null) {
            return;
        }
        String ProfitLoss_type = profit_loss_type.getSvalue();
        if (StrUtil.isEmpty(ProfitLoss_type)) {
            return;
        }
        String profitLoss = null;
        switch (ProfitLoss_type) {
//		case ProfitAndLossConfig.TYPE_PROFIT:
//			ProfitLoss = "profit";
//			break;
//		case ProfitAndLossConfig.TYPE_LOSS:
//			ProfitLoss = "loss";
//			break;
//		case ProfitAndLossConfig.TYPE_BUY_PROFIT:
//			if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
//				ProfitLoss = "profit";
//			}
//			break;
//		case ProfitAndLossConfig.TYPE_SELL_PROFIT:
//			if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
//				ProfitLoss = "profit";
//			}
//			break;
            case ProfitLossConfig.TYPE_BUY_PROFIT_SELL_LOSS:
                if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
                    profitLoss = "profit";
                }
                if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
                    profitLoss = "loss";
                }
                break;
            case ProfitLossConfig.TYPE_SELL_PROFIT_BUY_LOSS:
                if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
                    profitLoss = "profit";
                }
                if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
                    profitLoss = "loss";
                }
                break;
        }
        order.setProfitLoss(profitLoss);
    }

    /**
     * 检验是否已经有持仓单，有则无法下单
     *
     * @param partyId
     */
    public void checkSubmitOrder(final String partyId, final FuturesPara futuresPara) {
        boolean button = sysparaService.find("futures_order_only_one_button").getBoolean();
        if (!button) {
            return;
        }
        List<FuturesOrder> submittedOrders = findSubmitted(null);
        CollectionUtils.filter(submittedOrders, new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                FuturesOrder order = (FuturesOrder) arg0;
                // 是否存在交割单
                boolean flag = order != null && partyId.equals(order.getPartyId().toString());
                // 是否存在相同产品
                flag = flag && order.getSymbol().equals(futuresPara.getSymbol())// symbol是否一致
                        && order.getTimenum().equals(futuresPara.getTimenum())// 时间是否一致
                        && order.getTimeunit().equals(futuresPara.getTimeunit());// 时间单位是否一致
                return flag;
            }
        });
        if (!CollectionUtils.isEmpty(submittedOrders)) {
            throw new YamiShopBindException("您已存在订单");
        }
    }


    public void pushAsynRecom(FuturesOrder futuresOrder) {
        String futures_bonus_parameters = sysparaService.find("futures_bonus_parameters").getSvalue();
        if (StrUtil.isEmpty(futures_bonus_parameters)) {
            return;
        }
        RedisUtil.pushSync(FuturesRedisKeys.FUTURES_RECOM_QUEUE_UPDATE,
                new FuturesRecomMessage(futuresOrder.getOrderNo(), futuresOrder.getPartyId().toString(),
                        new BigDecimal(futuresOrder.getVolume()), futuresOrder.getCreateTime()));
    }


    public void saveClose(FuturesOrder order) {
        if (order.getState().equals(FuturesOrder.STATE_CREATED)) {
            return;
        }

        order.setCloseTime(System.currentTimeMillis() / 1000);
        order.setState(FuturesOrder.STATE_CREATED);

        String ProfitLoss = null;
        /**
         * 计算盈亏状态
         */
        if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
            /*
             * 0 买涨
             */
            if (order.getCloseAvgPrice() >= order.getTradeAvgPrice()) {
                ProfitLoss = "profit";
            }

            if (order.getCloseAvgPrice() <= order.getTradeAvgPrice()) {
                ProfitLoss = "loss";
            }

        } else {
            /*
             * 1 买跌
             */
            if (order.getCloseAvgPrice() <= order.getTradeAvgPrice()) {
                ProfitLoss = "profit";
            }
            if (order.getCloseAvgPrice() >= order.getTradeAvgPrice()) {
                ProfitLoss = "loss";
            }
        }

        /**
         * 交割场控是否生效 0为开启， 1为不开启
         */
//		double ProfitAndLossConfig_on = 0;

        // 24小时内交割合约客户最高赢率（正式用户交割盈利/正式用户交割金额），高于设定的值时客户必亏，低于时则不限制（范例：10，为最高赢10%），为空则不限制
        double futures_most_prfit_level = 0;
        futures_most_prfit_level = Double.valueOf(sysparaService.find("futures_most_prfit_level").getSvalue());
        if (futures_most_prfit_level > 0) {
            List<FuturesOrder> futuresOrders24Hour = new ArrayList();
            futuresOrders24Hour = findByHourAndSate("created", Constants.SECURITY_ROLE_MEMBER);
            double futures24Amount = 0;
            double futures24Profit = 0;
            /**
             * 客户赢钱率=纯盈利金额除以交割订单总金额
             */
            double futures_ratio = 0;
            if (futuresOrders24Hour != null && futuresOrders24Hour.size() != 0) {
                for (int i = 0; i < futuresOrders24Hour.size(); i++) {
                    FuturesOrder orders = futuresOrders24Hour.get(i);
                    futures24Amount = Arith.add(futures24Amount, orders.getVolume());
                    if (orders.getProfit() > 0) {
                        futures24Profit = Arith.add(futures24Profit, orders.getProfit());
                    }
                }
                futures_ratio = Arith.div(futures24Profit, futures24Amount);
                /**
                 * 赢钱率大于设置的百分比时，客户固定为亏损，并且交割场控不生效
                 */
                if (futures_ratio >= futures_most_prfit_level) {
                    logger.warn("---> FuturesOrderService.saveClose 当前订单:{} 执行结算时发现命中全局修正条件，" +
                            "futures24Profit:{}, futures24Amount:{}, futures_ratio:{}, futures_most_prfit_level:{}",
                            order.getOrderNo(), futures24Profit, futures24Amount, futures_ratio, futures_most_prfit_level);

                    ProfitLoss = "loss";
//					ProfitAndLossConfig_on = 1;
                }
            }

        }


        /**
         * 场控修正
         */
        Item bySymbol = itemService.findBySymbol(order.getSymbol());
        String symbolType = bySymbol!=null?bySymbol.getType():"";
        ProfitLossConfig profitAndLossConfig = profitLossConfigService
                .findByPartyId(order.getPartyId().toString(), bySymbol.getType());

//		if (profitAndLossConfig != null && ProfitAndLossConfig_on == 0) {
//		&& (StringUtils.isEmptyString(profitAndLossConfig.getSymbol())// 字符为空则表示所有币种场控
//				|| order.getSymbol().equals(profitAndLossConfig.getSymbol()))// 指定币种场控
        if (profitAndLossConfig != null) {
            logger.warn("---> FuturesOrderService.saveClose 当前订单:{} 执行结算时发现为当前用户配置了盈亏配置:{}",
                    order.getOrderNo(), profitAndLossConfig.getType());

            switch (profitAndLossConfig.getType()) {
                case ProfitLossConfig.TYPE_PROFIT:
                    ProfitLoss = "profit";
                    break;
                case ProfitLossConfig.TYPE_LOSS:
                    ProfitLoss = "loss";
                    break;
                case ProfitLossConfig.TYPE_BUY_PROFIT:
                    if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
                        ProfitLoss = "profit";
                    }
                    break;
                case ProfitLossConfig.TYPE_SELL_PROFIT:
                    if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
                        ProfitLoss = "profit";
                    }
                    break;
                case ProfitLossConfig.TYPE_BUY_PROFIT_SELL_LOSS:
                    if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
                        ProfitLoss = "profit";
                    }
                    if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
                        ProfitLoss = "loss";
                    }
                    break;
                case ProfitLossConfig.TYPE_SELL_PROFIT_BUY_LOSS:
                    if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
                        ProfitLoss = "profit";
                    }
                    if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
                        ProfitLoss = "loss";
                    }
                    break;

            }
        }
        /**
         * 订单是否有场控设置
         */
        if (!StringUtils.isEmptyString(order.getProfitLoss())) {
            ProfitLoss = order.getProfitLoss();
            logger.warn("---> FuturesOrderService.saveClose 当前订单:{} 执行结算时发现当前订单配置了盈亏标记:{}",
                    order.getOrderNo(), order.getProfitLoss());
        }

        Item item = itemService.findBySymbol(order.getSymbol());
        /**
         * 行情修正
         */

        Integer rofitRatioCardinality = Objects.isNull(order.getProfitRatioCardinality()) ? 1000 : order.getProfitRatioCardinality();

        double random = Math.random() * rofitRatioCardinality + 1;

        random = Double.parseDouble(String.format("%.2f", random));

        if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
            if ("profit".equals(ProfitLoss) && order.getCloseAvgPrice() <= order.getTradeAvgPrice()) {
                order.setCloseAvgPrice(Arith.add(order.getTradeAvgPrice(), Arith.mul(item.getPips(), random)));
            } else if ("loss".equals(ProfitLoss) && order.getCloseAvgPrice() >= order.getTradeAvgPrice()) {
                order.setCloseAvgPrice(Arith.sub(order.getTradeAvgPrice(), Arith.mul(item.getPips(), random)));
            }

        } else {
            if ("profit".equals(ProfitLoss) && order.getCloseAvgPrice() >= order.getTradeAvgPrice()) {
                order.setCloseAvgPrice(Arith.sub(order.getTradeAvgPrice(), Arith.mul(item.getPips(), random)));
            } else if ("loss".equals(ProfitLoss) && order.getCloseAvgPrice() <= order.getTradeAvgPrice()) {
                order.setCloseAvgPrice(Arith.add(order.getTradeAvgPrice(), Arith.mul(item.getPips(), random)));
            }
        }

        Double volume = order.getVolume();
        if ("profit".equals(ProfitLoss)) {
            /**
             * 盈利
             */
            DecimalFormat df = new DecimalFormat("#.##");
            Double profit = Double.valueOf(df.format(Arith.mul(volume, order.getProfitRatio())));
            order.setProfit(profit);
            //汇率转换
//            BigDecimal profitUSDT = exchangeRateService.getUsdtByType(BigDecimal.valueOf(profit), symbolType);
//            BigDecimal volumeUSDT = exchangeRateService.getUsdtByType(BigDecimal.valueOf(volume), symbolType);
            BigDecimal amount = BigDecimal.valueOf(profit).add(BigDecimal.valueOf(volume));
            walletService.updateMoney(order.getSymbol(), order.getPartyId(), amount, BigDecimal.ZERO,
                    Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET, Constants.DELIVERY_MONEYLOG_CONTENT_CONTRACT_CLOSE, "交割合约盈利,订单号[" + order.getOrderNo() + "]");

            String future_profit_bonus_parameters = sysparaService.find("future_profit_bonus_parameters").getSvalue();
            if (StringUtils.isNotEmpty(future_profit_bonus_parameters)) {
                saveParentFeeProfit(order, future_profit_bonus_parameters);
            }
//			miner_bonus_parameters = sysparaService.find("miner_first_bonus_parameters").getValue();
        } else {
            /**
             * 亏损
             */
            double futures_loss_part = Double.valueOf(sysparaService.find("futures_loss_part").getSvalue());
            if (futures_loss_part == 2) {
                /**
                 * 盈亏都按百分比 start
                 */
                //
                order.setProfit(Arith.sub(0, Arith.mul(volume, order.getProfitRatio())));// 亏损的时候，- 盈亏率*购买金额
                /**
                 * 盈亏都按百分比 start
                 */
            } else {
                /**
                 * 盈利按百分比，亏损全损
                 */
                order.setProfit(Arith.sub(0, volume));// 8.14 亏损的时候，-购买金额
            }
            //汇率转换
//            BigDecimal profitUSDT = exchangeRateService.getUsdtByType(BigDecimal.valueOf(order.getProfit()), symbolType);
//            BigDecimal volumeUSDT = exchangeRateService.getUsdtByType(BigDecimal.valueOf(volume), symbolType);
            BigDecimal amount = BigDecimal.valueOf(volume).add(BigDecimal.valueOf(order.getProfit()));
            walletService.updateMoney(order.getSymbol(), order.getPartyId(), amount, BigDecimal.ZERO,
                    Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET, Constants.DELIVERY_MONEYLOG_CONTENT_CONTRACT_CLOSE, "交割合约亏损退还,订单号[" + order.getOrderNo() + "]");
        }
        updateById(order);

        cache.remove(order.getOrderNo());

        FuturesOrder futuresOld = (FuturesOrder) RedisUtil.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrderNo());

        RedisUtil.del(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrderNo());

        Double futuresAssets = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString());
        Double futuresAssetsProfit = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());

        if (null != futuresOld) {
            // 获取 单个订单 交割合约总资产、总未实现盈利
            Map<String, Double> futuresAssetsOld = this.walletService.getMoneyFuturesByOrder(futuresOld);

            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssets ? 0.000D : futuresAssets, 0 - futuresAssetsOld.get("money_futures")));
            RedisUtil.set(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),
                    Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, 0 - futuresAssetsOld.get("money_futures_profit")));
        }

        this.userDataService.saveFuturesClose(order);

         //删除提示
        // tipService.deleteTip(order.getUuid());

        User party = userService.getById(order.getPartyId());
        party.setWithdrawLimitNowAmount(new BigDecimal(Arith.add(party.getWithdrawLimitNowAmount().doubleValue(), volume)));
        userService.updateById(party);
        // if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
        //     tipService.deleteTip(order.getPartyId().toString());
        // }

         tipService.deleteTip(order.getUuid());
    }


    public List<FuturesOrder> findByHourAndSate(String state, String rolename) {
        return baseMapper.findByHourAndSate(state, rolename, new Date());
    }

    /**
     * 推荐人手续费奖励
     */
    public void saveParentFeeProfit(FuturesOrder order, String bonus) {
        List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId());

        if (CollectionUtils.isNotEmpty(list_parents)) {
            String[] bonus_array = bonus.split(",");
            int loop = 0;
            for (int i = 0; i < list_parents.size(); i++) {
                if (loop >= 3) {
                    break;
                }
                User partyParent = userService.getById(list_parents.get(i).getRecomUserId());
                if (!Constants.SECURITY_ROLE_MEMBER.equals(partyParent.getRoleName())) {
                    continue;
                }
                loop++;

                /**
                 * 交易手续费 推荐人收益
                 */
                BigDecimal pip_amount = new BigDecimal(bonus_array[loop - 1]);
                BigDecimal get_money = new BigDecimal(order.getFee()).multiply(pip_amount);

                walletService.updateMoney("",order.getPartyId(), get_money, BigDecimal.ZERO,
                        Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET, Constants.MONEYLOG_CONTENT_REWARD, "第" + (i + 1) + "代下级用户，交割盈利手续费推荐奖励金");
            }

        }
    }


    public List<FuturesOrder> findByPartyIdAndToday(String partyId) {
        QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);
        queryWrapper.eq(" DateDiff(create_time,NOW())", 0);
        return list(queryWrapper);
    }


    /**
     * 根据用户批量赎回订单
     *
     * @param partyId
     */
    public void saveCloseAllByPartyId(final String partyId) {
        List<FuturesOrder> submittedOrders = findSubmitted(null);
        CollectionUtils.filter(submittedOrders, new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                // TODO Auto-generated method stub
                FuturesOrder order = (FuturesOrder) arg0;
                // 是否存在交割单
                boolean flag = partyId.equals(order.getPartyId());
                return flag;
            }
        });
        for (FuturesOrder order : submittedOrders) {
            saveClose(order);
        }
    }
    public List<FuturesOrder> cacheSubmitted() {

        return new ArrayList<FuturesOrder>(cache.values());
    }
    public List<FuturesOrder> queryByDate(int page, int pageSize, String date) {
        QueryWrapper<FuturesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_format(create_time,'%Y-%m-%d')", date);
        Page<FuturesOrder> iPage = new Page<FuturesOrder>();
        iPage.setSize(pageSize);
        iPage.setCurrent(page);
        return baseMapper.selectPage(iPage, queryWrapper).getRecords();
    }

    /**
     * 注意： 此处对时间戳进行了时区转换，外层调用方法不要再做转换了！！！
     *
     * @param order
     * @return
     */
    public Map<String, Object> bulidOne(FuturesOrder order) {
        FuturesOrder order_cache = (FuturesOrder) RedisUtil
                .get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrderNo());
        if (order_cache != null) {
            order = order_cache;
        }
        List<FuturesPara> paras = futuresParaService.getBySymbolSort(order.getSymbol());
        Item item = this.itemService.findBySymbol(order.getSymbol());
        if (item == null) {
            throw new YamiShopBindException("参数错误");
        }
        String decimals = "#.";

        for (int i = 0; i < item.getDecimals(); i++) {
            decimals = decimals + "#";
        }
        if (item.getDecimals() == 0) {
            decimals = "#";
        }
        DecimalFormat df = new DecimalFormat("#.##");

        DecimalFormat df_symbol = new DecimalFormat(decimals);
        df_symbol.setRoundingMode(RoundingMode.FLOOR);// 向下取整

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_no", order.getOrderNo());
        map.put("name", item.getName());
        map.put("symbol", order.getSymbol());

        // 注意：此处转换了时间戳后，外层务必不要再转换了
        long initOpenTimestamp = DateTimeTools.transferShowTimeToClientTime(order.getCreateTimeTs());
        map.put("open_time", initOpenTimestamp);
        if (order.getCloseTime() != null) {
            initOpenTimestamp = DateTimeTools.transferShowTimeToClientTime(order.getCloseTime());
            map.put("close_time", initOpenTimestamp);
        } else {
            map.put("close_time", "--");
        }

        map.put("direction", order.getDirection());
        map.put("open_price", df_symbol.format(order.getTradeAvgPrice()));
        map.put("state", order.getState());
        map.put("amount", order.getVolume());
        map.put("fee", order.getFee());
        /**
         * 收益
         */
        if (order.getProfit() > 0) {
//			if ("submitted".equals(order.getState())) {
//				map.put("profit", df.format(Arith.mul(order.getVolume(), ratio_min)) + " " + "~ "
//						+ df.format(Arith.mul(order.getVolume(), ratio_max)));
//			} else {
            map.put("profit", df.format(order.getProfit()));
//			}
            map.put("profit_state", "1");

        } else {
            map.put("profit", order.getProfit());
            map.put("profit_state", "0");
        }

        map.put("volume", order.getVolume());

        long settlementTimestamp = DateTimeTools.transferShowTimeToClientTime(order.getSettlementTime());
        map.put("settlement_time", settlementTimestamp);// 交割时间
        map.put("close_price", df_symbol.format(order.getCloseAvgPrice()));
        map.put("remain_time", StrUtil.isEmpty(order.getRemainTime()) ? "0:0:0" : order.getRemainTime());
        map.put("time_num", order.getTimenum());
        map.put("time_unit", order.getTimeunit().substring(0, 1));
        return map;
    }

}
