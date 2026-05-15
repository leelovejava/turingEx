package com.yami.trading.service.miner.service.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.miner.job.MinerOrderMessage;
import com.yami.trading.service.miner.service.MinerOrderProfitService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.*;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.dao.miner.MinerOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.miner.service.MinerRedisKeys;
import com.yami.trading.service.miner.service.MinerService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yami.trading.service.miner.service.MinerOrderService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class MinerOrderProfitServiceImpl extends ServiceImpl<MinerOrderMapper, MinerOrder> implements MinerOrderProfitService {
    @Autowired
    protected MinerService minerService;
    @Autowired
    protected UserService partyService;
    @Autowired
    protected UserRecomService userRecomService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected MoneyLogService moneyLogService;
    @Autowired
    protected MinerOrderService minerOrderService;
    @Autowired
    protected WalletService walletService;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    protected DataService dataService;
    @Autowired
    protected NamedParameterJdbcOperations namedParameterJdbcTemplate;

    /**
     * 预收益服务
     */
    @Autowired
    private com.yami.trading.service.quant.service.QuantPreIncomeService quantPreIncomeService;

    /**
     * 机器人订单服务
     */
    @Autowired
    private com.yami.trading.service.quant.service.QuantBotOrderService quantBotOrderService;

    /**
     * 计算推荐人收益
     */
    protected Map<String, Double> cacheRecomProfit = new ConcurrentHashMap<>();

    /**
     * 分页获取计息中的矿机订单
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page pagedQueryComputeOrder(int pageNo, int pageSize) {
        Page page = new Page(pageNo, pageSize);
        this.baseMapper.pagedQueryComputeOrder(page);
        return page;
    }

    /**
     * 计算订单收益
     * <p>
     * 所有矿机收益计算逻辑（已调整）：
     * - 收益 = 投资金额 × 随机日利率(%)
     * - 随机日利率在购买时生成，介于矿机的daily_rate_start和daily_rate_end之间
     * - 例如：投资金额100U，随机日利率1.8%，则每日收益 = 100 × 1.8% = 1.8U
     *
     * @param orders                 订单列表
     * @param miner_profit_symbol    指定币种
     * @param realtime               币种行情
     * @param miner_bonus_parameters 推荐人收益参数
     * @param systemTime             系统时间
     */
    @Override
    public void saveComputeOrderProfit(List orders, String miner_profit_symbol, Realtime realtime,
                                       String miner_bonus_parameters, Date systemTime) {
        log.info("start compute order size:{}", orders.size());
        List<MinerOrderMessage> saveMinerOrders = new ArrayList<>();
        Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();
        for (Object order1 : orders) {
            MinerOrder order = JSON.parseObject(JSON.toJSONString(order1), MinerOrder.class);
            /**
             * 截止时间要大于现在这个时间则计算收益
             */
            Miner miner = minerService.cacheById(order.getMiner_id());
            if (null == miner) {
                log.error("该矿机不存在，停止计息，minerId:" + order.getMiner_id());
                continue;
            }
            // 非体验矿机，今天<起息时间 不计息，表示满24小时才开始计息
            if (miner.getTest().equals("N") && systemTime.before(order.getEarn_time())) {
                continue;
            }
            // 体验矿机判断：systemTime < 当日起息日零点 则不计息（体验矿机当天购买当天计息）
            if (miner.getTest().equals("Y") && systemTime.before(DateUtils.getDayStart(order.getEarn_time()))) {
                continue;
            }

            if (order.getStop_time() != null && systemTime.after(order.getStop_time())) {// 当前时间>截止时间
                order.setState("0");// 正常赎回，停止计息，退还本金
                order.setCompute_day(systemTime);
                minerOrderService.saveClose(order);// 截止日=今天时就已经返还完毕
            } else {
                // 当天计算过，则不再计算，例如 1号4点计算， 则2号0点以前进入都判定计算过
                if (order.getCompute_day() != null
                        && systemTime.before(DateUtils.getDayStart(DateUtils.addDate(order.getCompute_day(), 1)))) {
                    // 收益已计算过，跳过（不在每日发放，改为赎回时一次性发放）
                    continue;
                }

                // 从预收益表中读取收益（属于该订单的预收益记录）
                com.yami.trading.bean.quant.QuantPreIncome preIncome =
                        quantPreIncomeService.findTodayRandomUnusedByQuantOrderId(order.getUuid());

                if (preIncome != null) {
                    double dailyTotalProfit = preIncome.getIncome() == null ? 0D : preIncome.getIncome();
                    Integer lastPreIncomeId = preIncome.getId();

                    order.setCompute_day(systemTime);// 记息日期
                    order.setProfit(Arith.add(order.getProfit(), dailyTotalProfit));// 累计收益（不发放，赎回时一起发放）

                    // 在 t_quant_bot_orders 表中新增一条使用记录（当天的汇总记录）
                    com.yami.trading.bean.quant.QuantBotOrder botOrder = new com.yami.trading.bean.quant.QuantBotOrder();
                    botOrder.setUserId(order.getPartyId());
                    botOrder.setNumber(BigDecimal.valueOf(order.getAmount()));
                    botOrder.setStatus(3); // 已平仓
                    botOrder.setCreatedAt(new Date());
                    botOrder.setUpdatedAt(new Date());
                    botOrder.setHandledAt(new Date());
                    botOrder.setCompleteAt(new Date());
                    // 最终收益 = 当天220条预收益记录的累加值
                    botOrder.setFactProfits(BigDecimal.valueOf(dailyTotalProfit));
                    botOrder.setProfitResult(dailyTotalProfit >= 0 ? 1 : -1); // 1:盈利, -1:亏损
                    botOrder.setPreProfitResult(dailyTotalProfit >= 0 ? 1 : -1);
                    botOrder.setBotOrderId(lastPreIncomeId); // 关联最后一条预收益记录ID
                    botOrder.setProfitRatio(BigDecimal.valueOf(order.getRandom_daily_rate())); // 设置收益率（日利率）
                    quantBotOrderService.createBotOrder(botOrder);

                    // 修改收益为已使用
                    preIncome.setStatus(1);
                    quantPreIncomeService.updateById(preIncome);

                    saveMinerOrders
                            .add(new MinerOrderMessage(order.getOrder_no(), order.getProfit(), order.getCompute_day()));
                    // 更新矿机订单
                    redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no(), order);
                }
                //每单处理完后等待100ms，避免循环提交事务导致问题
                ThreadUtils.sleep(200);
            }
        }
        log.info("start miner batch update size:{}", saveMinerOrders.size());
        updateBatchMinerOrdersProfit(saveMinerOrders);
    }

    /**
     * 计算订单收益
     *
     * @param orders                 订单列表
     * @param miner_profit_symbol    指定币种
     * @param realtime               币种行情
     * @param miner_bonus_parameters 推荐人收益参数
     */
    @Override
    public void saveComputeOrderProfit(List orders, String miner_profit_symbol, Realtime realtime,
                                       String miner_bonus_parameters) {
        log.info("开始计算订单数量:{}", orders.size());
        List<MinerOrderMessage> saveMinerOrders = new ArrayList<MinerOrderMessage>();
        ///Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();
        for (Object obj : orders) {
            MinerOrder order = JSON.parseObject(JSON.toJSONString(obj), MinerOrder.class);
            /**
             * 截止时间要大于现在这个时间则计算收益
             */
            Miner miner = minerService.cacheById(order.getMiner_id());
            if (null == miner) {
                log.error("该矿机不存在，停止计息，minerId:" + order.getMiner_id());
                continue;
            }

            log.info("订单结束时间:{}", order.getStop_time());
            if (order.getStop_time() != null && new Date().after(order.getStop_time())) {
                log.info("当前时间>截止时间");
                // 当前时间>截止时间
                // 正常赎回，停止计息，退还本金
                order.setState("0");
                order.setCompute_day(new Date());
                // 截止日=今天时就已经返还完毕
                minerOrderService.saveClose(order);
            } else {

                // 从预收益表中读取收益（属于该订单的预收益记录）
                com.yami.trading.bean.quant.QuantPreIncome preIncome =
                        quantPreIncomeService.findTodayRandomUnusedByQuantOrderId(order.getUuid());

                if (preIncome == null) {
                    log.warn("订单无可用预收益记录，跳过计息，orderNo:{}", order.getOrder_no());
                    continue;
                }
                double dailyTotalProfit = preIncome.getIncome() == null ? 0D : preIncome.getIncome();
                Integer lastPreIncomeId = preIncome.getId();

                order.setCompute_day(new Date());// 记息日期
                order.setProfit(Arith.add(order.getProfit(), dailyTotalProfit));// 累计收益（不发放，赎回时一起发放）

                // 在 t_quant_bot_orders 表中新增一条使用记录（当天的汇总记录）
                com.yami.trading.bean.quant.QuantBotOrder botOrder = new com.yami.trading.bean.quant.QuantBotOrder();
                botOrder.setUserId(order.getPartyId());
                botOrder.setNumber(BigDecimal.valueOf(order.getAmount()));
                botOrder.setStatus(3); // 已平仓
                botOrder.setCreatedAt(new Date());
                botOrder.setUpdatedAt(new Date());
                botOrder.setHandledAt(new Date());
                botOrder.setCompleteAt(new Date());
                // 最终收益 = 当天220条预收益记录的累加值
                botOrder.setFactProfits(BigDecimal.valueOf(dailyTotalProfit));
                botOrder.setProfitResult(dailyTotalProfit >= 0 ? 1 : -1); // 1:盈利, -1:亏损
                botOrder.setPreProfitResult(dailyTotalProfit >= 0 ? 1 : -1);
                botOrder.setBotOrderId(lastPreIncomeId); // 关联最后一条预收益记录ID
                botOrder.setProfitRatio(BigDecimal.valueOf(order.getRandom_daily_rate())); // 设置收益率（日利率）
                quantBotOrderService.createBotOrder(botOrder);

                preIncome.setStatus(1);
                // 开始时间=当前时间随机减1-2分钟，结束时间=当前时间
                preIncome.setStartTime(new Date((long) (System.currentTimeMillis() - (60 + Math.random() * 60) * 1000)));
                preIncome.setEndTime(new Date());
                quantPreIncomeService.updateById(preIncome);

                saveMinerOrders
                        .add(new MinerOrderMessage(order.getOrder_no(), order.getProfit(), order.getCompute_day()));
                // 更新矿机订单
                redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no(), order);
            }
            //每单处理完后等待100ms，避免循环提交事务导致问题
            ThreadUtils.sleep(200);
        }
        log.info("start miner batch update size:{}", saveMinerOrders.size());
        updateBatchMinerOrdersProfit(saveMinerOrders);
    }

    /**
     * 更新收益到钱包
     *
     * @param miner_profit_symbol 指定币种
     * @param day_profit          当日收益
     * @param realtime            币种行情
     * @param order               矿机订单
     */
    protected void updateProfitToWallet(String miner_profit_symbol, double day_profit, Realtime realtime, MinerOrder order) {
        // 矿机购买时使用的币种，则产生对应的币种，转换成u
        String miner_buy_symbol = sysparaService.find("miner_buy_symbol").getSvalue();
        if (StringUtils.isNotEmpty(miner_buy_symbol)) {
            if (miner_buy_symbol.equals(miner_profit_symbol)) {
                day_profit = Arith.mul(day_profit, realtime.getClose());
            } else {
                List<Realtime> realtime_list = this.dataService.realtime(miner_buy_symbol);
                Realtime realtimeBuySymbol = null;
                if (realtime_list.size() > 0) {
                    realtimeBuySymbol = realtime_list.get(0);
                }
                day_profit = Arith.mul(day_profit, realtimeBuySymbol.getClose());
            }
        }
        // 非USDT币种收益
        if (StringUtils.isNotEmpty(miner_profit_symbol)) {
            double getSymbolVolume = Arith.div(day_profit, realtime.getClose());

            WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), miner_profit_symbol);
            double amountBefore = walletExtend.getAmount();
            this.walletService.updateExtend(order.getPartyId(), miner_profit_symbol, getSymbolVolume);

            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(getSymbolVolume));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, getSymbolVolume)));
            // 量化订单收益
            moneylog.setLog("Quant Order profit, orderNo[" + order.getOrder_no() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(realtime.getSymbol());
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_PROFIT);
            moneylog.setCreateTime(new Date());
            moneyLogService.save(moneylog);
        }
        // 此为收益为USDT，收益进入冻结余额（赎回时再转为可用余额）
        else {
            Wallet wallet = walletService.saveWalletByPartyId(order.getPartyId());
            double amountBefore = wallet.getFreezeMoney().doubleValue();
            walletService.updateWithLockAndFreeze(order.getPartyId(), 0, 0, day_profit);

            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(day_profit));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, day_profit)));
            // 量化订单收益（冻结）
            moneylog.setLog("Quant Order profit (frozen), orderNo[" + order.getOrder_no() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(WalletConstants.WALLET_USDT);
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_PROFIT);
            moneylog.setCreateTime(new Date());
            moneyLogService.save(moneylog);
        }
    }

    /**
     * 更新收益到钱包
     *
     * @param miner_profit_symbol 指定币种
     * @param day_profit          当日收益
     * @param realtime            币种行情
     * @param order               矿机订单
     */
    protected void updateProfitToWallet(String miner_profit_symbol, double day_profit, Realtime realtime, MinerOrder order, Date systemTime) {
        // 矿机购买时使用的币种，则产生对应的币种，转换成u
        String miner_buy_symbol = sysparaService.find("miner_buy_symbol").getSvalue();
        if (StringUtils.isNotEmpty(miner_buy_symbol)) {
            if (miner_buy_symbol.equals(miner_profit_symbol)) {
                day_profit = Arith.mul(day_profit, realtime.getClose());
            } else {
                List<Realtime> realtime_list = this.dataService.realtime(miner_buy_symbol);
                Realtime realtimeBuySymbol = null;
                if (realtime_list.size() > 0) {
                    realtimeBuySymbol = realtime_list.get(0);
                }
                day_profit = Arith.mul(day_profit, realtimeBuySymbol.getClose());
            }
        }
        // 非Usdt币种收益
        if (StringUtils.isNotEmpty(miner_profit_symbol)) {
            double get_symbol_volume = Arith.div(day_profit, realtime.getClose());

            WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), miner_profit_symbol);
            double amountBefore = walletExtend.getAmount();
            this.walletService.updateExtend(order.getPartyId(), miner_profit_symbol, get_symbol_volume);

            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(get_symbol_volume));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, get_symbol_volume)));
            // 量化订单收益
            moneylog.setLog("Quant Order profit, orderNo[" + order.getOrder_no() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(realtime.getSymbol());
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_PROFIT);
            moneylog.setCreateTime(systemTime);
            moneyLogService.save(moneylog);
        }
        // 此为收益为usdt，收益进入冻结余额（赎回时再转为可用余额）
        else {
            Wallet wallet = walletService.saveWalletByPartyId(order.getPartyId());
            double amountBefore = wallet.getFreezeMoney().doubleValue();
            walletService.updateWithLockAndFreeze(order.getPartyId(), 0, 0, day_profit);

            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(day_profit));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, day_profit)));
            // 量化订单收益（冻结）
            moneylog.setLog("Quant Order profit (frozen), orderNo[" + order.getOrder_no() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(WalletConstants.WALLET_USDT);
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_PROFIT);
            moneylog.setCreateTime(systemTime);
            moneyLogService.save(moneylog);
        }
    }

    /**
     * 批量更新订单收益
     *
     * @param orderList
     */
    protected void updateBatchMinerOrdersProfit(final List<MinerOrderMessage> orderList) {
        String sql = "UPDATE T_MINER_ORDER SET PROFIT=?,COMPUTE_DAY=? WHERE ORDER_NO=?";
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setDouble(1, orderList.get(i).getProfit());
                ps.setTimestamp(2, new Timestamp(orderList.get(i).getComputeDay().getTime()));
                ps.setString(3, orderList.get(i).getOrderNo());
            }

            @Override
            public int getBatchSize() {
                return orderList.size();
            }
        });
        log.info("end miner batch update attr:{}", batchUpdate);
    }

    public void handleRecomProfit(String partyId, double profit, Miner miner, String miner_bonus_parameters) {
        if (miner.getTest().equals("Y")) {
            return;
        }
        List<UserRecom> list_parents = this.userRecomService.getParents(partyId);
        if (list_parents.size() == 0) {
            return;
        }
        String[] miner_bonus_array = miner_bonus_parameters.split(",");
        int loop = 0;
        int loopMax = miner_bonus_array.length;
        for (int i = 0; i < list_parents.size(); i++) {
            if (loop >= loopMax) {
                break;
            }
            User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
            if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
                continue;
            }
            loop++;
            Map<String, MinerOrder> map_party = (Map<String, MinerOrder>) redisTemplate.opsForValue()
                    .get(MinerRedisKeys.MINER_ORDER_PARTY_ID + party_parent.getUserId().toString());
            if (map_party == null || map_party.size() == 0) {
                continue;
            }
            /*
             * 判断是否
             */
            int cycle = 0;
            Iterator<Entry<String, MinerOrder>> it = map_party.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, MinerOrder> entry = it.next();
                MinerOrder minerOrder = entry.getValue();
                if (!"1".equals(minerOrder.getState())) {
                    continue;
                }
                Miner miner_party = this.minerService.cacheById(minerOrder.getMiner_id());
                if (cycle < miner_party.getCycle()) {
                    cycle = miner_party.getCycle();
                }

            }

            if (cycle >= miner.getCycle()) {
                /**
                 * 增加收益
                 */
                double pip_amount = Double.valueOf(miner_bonus_array[loop - 1]);
                double get_money = Arith.mul(profit, pip_amount);

                Double recom_profit = cacheRecomProfit.get(party_parent.getUserId().toString());
                cacheRecomProfit.put(party_parent.getUserId().toString(),
                        Arith.add(recom_profit == null ? 0D : recom_profit, get_money));
            }
        }
    }

    /**
     * 最终收益持久化数据库
     */
    @Override
    public void saveRecomProfit() {
        if (cacheRecomProfit.isEmpty()) {
            return;
        }

        // 开始增加推荐人收益
        log.info("start ------recom user miner profit,date:{},count:{}",
                new Object[]{new Date(), cacheRecomProfit.size()});
        for (Entry<String, Double> entry : cacheRecomProfit.entrySet()) {
            Wallet wallet = walletService.saveWalletByPartyId(entry.getKey());
            double amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), entry.getValue());

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneyLog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneyLog.setAmount(BigDecimal.valueOf(entry.getValue()));
            moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, entry.getValue())));
            // 量化订单推荐奖励金
            moneyLog.setLog("Quant Order referral bonus");
            moneyLog.setUserId(entry.getKey());
            moneyLog.setWalletType(Constants.WALLET);
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
            moneyLogService.save(moneyLog);
            userDataService.saveMinerProfit(entry.getKey(), entry.getValue());
        }
        // 推荐人矿机收益计算完成，纪录日志
        log.info("finish ------recom user miner profit,date:{},count:{}",
                new Object[]{new Date(), cacheRecomProfit.size()});
        // 处理完后收益清空

    }

    /**
     * 最终收益持久化数据库
     */
    @Override
    public void saveRecomProfit(Date systemTime) {
        if (cacheRecomProfit.isEmpty()) {
            return;
        }

        // 开始增加推荐人收益
        log.info("start ------recom user miner profit,date:{},count:{}",
                new Object[]{new Date(), cacheRecomProfit.size()});
        for (Entry<String, Double> entry : cacheRecomProfit.entrySet()) {
            Wallet wallet = walletService.saveWalletByPartyId(entry.getKey());
            double amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), entry.getValue());

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneyLog.setAmountBefore(BigDecimal.valueOf(amountBefore));
            moneyLog.setAmount(BigDecimal.valueOf(entry.getValue()));
            moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, entry.getValue())));
            // 量化订单推荐奖励金
            moneyLog.setLog("Quant Order referral bonus");
            moneyLog.setUserId(entry.getKey());
            moneyLog.setWalletType(Constants.WALLET);
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
            moneyLog.setCreateTime(systemTime);
            moneyLogService.save(moneyLog);
            userDataService.saveMinerProfit(entry.getKey(), entry.getValue());
        }
        // 推荐人矿机收益计算完成，纪录日志
        log.info("finish ------recom user miner profit,date:{},count:{}",
                new Object[]{new Date(), cacheRecomProfit.size()});
        // 处理完后收益清空

    }

    /**
     * 计算单个订单收益（赎回时调用）
     *
     * @param order 矿机订单
     */
    @Override
    public void saveComputeOrderProfit(MinerOrder order) {
        log.info("start compute single order profit, orderNo:{}", order.getOrder_no());

        Miner miner = minerService.cacheById(order.getMiner_id());
        if (null == miner) {
            log.error("该矿机不存在，停止计息，minerId:" + order.getMiner_id());
            return;
        }

        // 如果没有关联的预收益订单ID，直接返回
        String quantBotOrderId = order.getUuid();
        if (StringUtils.isEmpty(quantBotOrderId)) {
            log.info("no quantBotOrderId found for order:{}", order.getOrder_no());
            return;
        }

        // 从预收益表中读取该订单所有未使用的收益记录
        List<com.yami.trading.bean.quant.QuantPreIncome> preIncomes =
                quantPreIncomeService.findUnusedByQuantOrderId(quantBotOrderId);

        if (preIncomes != null && !preIncomes.isEmpty()) {
            // 计算所有未使用的预收益记录总和
            double totalProfit = 0;
            for (com.yami.trading.bean.quant.QuantPreIncome preIncome : preIncomes) {
                totalProfit = Arith.add(totalProfit, preIncome.getIncome());
                // 标记预收益记录为已使用
                quantPreIncomeService.markAsUsed(preIncome.getId());
            }

            order.setProfit(Arith.add(order.getProfit(), totalProfit));

            // 更新矿机订单
            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no(), order);

            // 持久化到数据库
            String sql = "UPDATE T_MINER_ORDER SET PROFIT=? WHERE ORDER_NO=?";
            jdbcTemplate.update(sql, order.getProfit(), order.getOrder_no());

            log.info("finish compute single order profit, orderNo:{}, totalProfit:{}", order.getOrder_no(), totalProfit);
        } else {
            log.info("no unused pre-income records found for order:{}", order.getOrder_no());
        }
    }

    @Override
    public void cacheRecomProfitClear() {
        cacheRecomProfit.clear();
    }

}
