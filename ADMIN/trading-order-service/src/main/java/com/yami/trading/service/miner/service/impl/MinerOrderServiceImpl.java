package com.yami.trading.service.miner.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.miner.MinerOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.yami.trading.service.quant.service.QuantPreIncomeService;
import com.yami.trading.service.miner.service.MinerOrderProfitService;
import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.miner.service.MinerRedisKeys;
import com.yami.trading.service.miner.service.MinerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MinerOrderServiceImpl extends ServiceImpl<MinerOrderMapper, MinerOrder> implements MinerOrderService {
    //	protected PagedQueryDao pagedDao;
    @Autowired
    protected WalletService walletService;
    @Autowired
    protected MoneyLogService moneyLogService;
    @Autowired
    protected MinerService minerService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected NamedParameterJdbcOperations namedParameterJdbcTemplate;
    @Autowired
    protected UserRecomService userRecomService;
    @Autowired
    protected UserService partyService;
    private Logger log = LoggerFactory.getLogger(MinerOrderServiceImpl.class);
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    protected LogService logService;
    @Autowired
    protected UserService secUserService;
    //	protected RedisHandler redisHandler;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    protected DataService dataService;

    /**
     * 实名认证服务 - 用于检查用户实名状态和认证时间
     */
    @Autowired
    protected RealNameAuthRecordService realNameAuthRecordService;

    /**
     * 预收益异步任务
     */
    @Autowired
    private com.yami.trading.service.miner.job.QuantPreIncomeJob quantPreIncomeJob;

    /**
     * 矿机订单收益服务 - 用于赎回时计算收益
     * 使用 @Lazy 解决循环依赖问题
     */
    @Autowired
    @Lazy
    private MinerOrderProfitService minerOrderProfitService;

    @Autowired
    private QuantPreIncomeService quantPreIncomeService;

    /**
     * 管理员新增订单
     *
     * @param entity
     * @param operator
     */
    public void saveCreateByManage(MinerOrder entity, String operator) {
        saveCreate(entity, true);
        User secUser = secUserService.cacheUserBy(entity.getPartyId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(entity.getOrder_no());
        log.setOperator(operator);
        log.setUsername(secUser.getUserName());
        log.setUserId(entity.getPartyId().toString());
        // 手动新增量化订单
        log.setLog("Manually add Quant Order, orderNo[" + entity.getOrder_no() + "], amount[" + entity.getAmount() + "]");

        logService.save(log);
    }

    /**
     * 矿机下单
     * <p>
     * 体验矿机新逻辑：
     * 1. 实名认证后赠送100U体验金额，实名超过7天作废
     * 2. 当天即可开始计息（不是第二天）
     * 3. 体验矿机金额为100U（不再是0）
     * 4. 收益 = 投资金额 × 随机日利率（日利率在开始和结束值之间随机生成）
     *
     * @param entity   矿机订单实体
     * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
     */
    public void saveCreateNew(MinerOrder entity, boolean isManage) {
        entity.setCreate_time(new Date());
        String partyId = entity.getPartyId().toString();
        Miner miner = minerService.findById(entity.getMiner_id());
        if (null == miner) {
            // 矿机不存在
            throw new BusinessException("Quant Order does not exist");
        }
        // 管理员解锁所有，用户正常流程
        if (!isManage && "0".equals(miner.getOn_sale())) {
            // 矿机未解锁，无法购买
            throw new BusinessException("Quant Order is locked, cannot purchase");
        }

        // 检查用户是否已购买过体验矿机（体验矿机每人仅能购买一次）
        if (miner.getTest().equals("Y") && this.findByTest(partyId)) {
            // 您已购买过体验矿机,不得重复购买
            throw new BusinessException("You have already purchased the experience Quant Order, cannot purchase again");
        }

        // 买入金额需要在区间内（非体验矿机）
        if (!miner.getTest().equals("Y")
                && (entity.getAmount() < miner.getInvestment_min() || entity.getAmount() > miner.getInvestment_max())) {
            if (miner.getInvestment_max() != 0) {
                // 买入金额需要在区间内
                throw new BusinessException("Purchase amount must be within the range");
            }
            // 无限制的矿机不得小于最小购买金额
            else if (entity.getAmount() < miner.getInvestment_min()) {
                // 买入金额需要在区间内
                throw new BusinessException("Purchase amount must be within the range");
            }
        }

        // ========================================
        // 矿机通用逻辑处理
        // ========================================

        // 在日利率开始和结束值之间随机生成一个日利率
        // 例如：daily_rate_start=1.5, daily_rate_end=2.5
        // 随机生成介于两者之间的利率
        double randomRate = generateRandomDailyRate(miner.getDaily_rate_start(), miner.getDaily_rate_end());
        entity.setRandom_daily_rate(randomRate);

        // 实际日收益 = 投入金额 × 日收益率
        long actualDailyIncome = (long) (entity.getAmount() * randomRate / 100);
        // 预计日收益：重新随机生成一个区间内的日利率计算
        double expectedRate = generateRandomDailyRate(miner.getDaily_rate_start(), miner.getDaily_rate_end());
        long expectedDailyIncome = (long) (entity.getAmount() * expectedRate / 100);

        if (miner.getTest().equals("Y")) {
            // 检查用户实名认证状态
            RealNameAuthRecord realNameAuth = realNameAuthRecordService.getByUserId(partyId);
            if (realNameAuth == null || realNameAuth.getStatus() != 2) {
                // 请先完成实名认证再购买AI体验量化
                throw new BusinessException("Please complete real-name authentication before purchasing the experience quant order");
            }

            // 检查用户 kycBonusAmount 是否有效（null 或 0 表示体验资格不可用）
            User kycUser = secUserService.cacheUserBy(partyId);
            if (kycUser == null || kycUser.getKycBonusAmount() == null || kycUser.getKycBonusAmount() <= 0) {
                throw new BusinessException("Real-name authentication has expired for more than 7 days, experience quant order qualification is invalid");
            }

            // 体验矿机固定金额为300U
            entity.setAmount(300.0);

            // 体验矿机当天即可开始计息（不需要等第二天）
            entity.setEarn_time(DateUtils.getDayStart(new Date()));

            // 计算截止时间 = 购买时间 + 周期天数
            // 例如：2025-01-01 01:00:00 购买，周期2天，到期时间 = 2025-01-03 01:00:00
            LocalDateTime stopTime = entity.getCreate_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays((int) miner.getCycle());
            entity.setStop_time(Date.from(stopTime.atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            // 非体验矿机：起息时间 = 创建时间
            entity.setEarn_time(entity.getCreate_time());

            // 非体验矿机截止时间 = 购买时间 + 传入周期天数（优先用传入cycle，否则用矿机配置cycle）
            int cycleDays = entity.getCycle() > 0 ? entity.getCycle() : (int) miner.getCycle();
            LocalDateTime stopTime = entity.getCreate_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(cycleDays);
            entity.setStop_time(Date.from(stopTime.atZone(ZoneId.systemDefault()).toInstant()));
        }

        // 预计总收益 = 预计日收益 × 周期天数（cycle 确定后再计算）
        int cycleDaysForExpected = entity.getCycle() > 0 ? entity.getCycle() : (int) miner.getCycle();
        entity.setExpectedTotalIncome(expectedDailyIncome * cycleDaysForExpected);
        entity.setTotalIncome(BigDecimal.valueOf(entity.getAmount() * randomRate / 100 * cycleDaysForExpected));

        if (findByFist(partyId)) {
            // 标识首次购买
            entity.setFirst_buy("1");
        } else {
            // 标识首次购买
            entity.setFirst_buy("0");
        }

        double amount = entity.getAmount();
        if (amount < 0) {
            entity.setAmount(-amount);
        }

        // 扣钱（体验矿机不扣钱）
        String buyCurrency = miner.getBuy_currency();
        double close = 0;
        if (miner.getTest().equals("Y")) {
            // 体验矿机不扣钱，跳过
        } else if ("usdt".equals(buyCurrency)) {
            saveMinerBuyUsdt(entity);
        } else {
            List<Realtime> realtimes = this.dataService.realtime(buyCurrency);
            if (null == realtimes || realtimes.size() <= 0) {
                // 行情获取异常，稍后再试
                throw new BusinessException("Market data acquisition exception, please try again later");
            }
            close = realtimes.get(0).getClose();
            saveMinerBuyOtherCoin(entity, buyCurrency);
        }

        this.insertMinerOrder(entity);

        redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

        // 创建机器人订单（同步），返回值为新建的 QuantBotOrder ID
        Integer botOrderId = quantPreIncomeJob.generatePreIncomeSync(
                partyId,
                entity.getAmount(),
                miner.getName(),
                miner.getUuid()
        );

        String quantBotOrderId = botOrderId != null ? String.valueOf(botOrderId) : entity.getUuid();

        // 保存机器人订单ID到矿机订单
        if (quantBotOrderId != null) {
            entity.setUuid(quantBotOrderId);
            this.updateById(entity);
            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

            // 异步生成预收益记录（220次/天 × 周期天数，80%盈利，20%亏损，总和符合日收益）
            int cycleDays = entity.getCycle() > 0 ? entity.getCycle() : (int) miner.getCycle();
            double dailyRate = entity.getRandom_daily_rate(); // 使用购买时随机生成的日利率
            quantPreIncomeJob.generatePreIncomeRecordsAsync(quantBotOrderId, entity.getAmount(), cycleDays, dailyRate);
        }

        if (!miner.getTest().equals("Y")) {

            Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);

            if (map_partyid == null) {
                map_partyid = new ConcurrentHashMap<String, MinerOrder>();
            }
            map_partyid.put(entity.getOrder_no(), entity);
            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId, map_partyid);

            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(entity.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOrder = entity.getAmount();

                Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);

                redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId,
                        Arith.add(null == minerAssets ? 0.000D : minerAssets, minerAssetsOrder));
            }
        }

        // userdata 数据
        if ("usdt".equals(buyCurrency)) {
            userDataService.saveMinerBuy(entity);
        } else {
            userDataService.saveMinerBuy(minerUserDataOtherCoin(entity, buyCurrency, close));
        }
    }


    /**
     * 矿机下单
     *
     * @param entity
     * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
     */
    @Override
    public void saveCreate(MinerOrder entity, boolean isManage) {

        entity.setCreate_time(new Date());

//		Party party = this.partyService.findPartyById(entity.getPartyId());
//		if (!party.getEnabled()) {
//			throw new BusinessException(1, "No permission");
//		}
        /**
         * 加入周期
         */
        Miner miner = minerService.findById(entity.getMiner_id());
        if (null == miner) {
            throw new BusinessException("矿机不存在");
        }
        if (!isManage && "0".equals(miner.getOn_sale())) {// 管理员解锁所有，用户正常流程
//			if (!this.getUnLockMiner(entity.getPartyId().toString(), miner.getId().toString())) {
            throw new BusinessException("矿机未解锁，无法购买");
//			}
        }
//		entity.setAmount(Arith.mul(miner.getInvestment_min(), entity.getVolume()));
//		entity.setCycle(miner.getCycle());

        if (miner.getTest().equals("Y") && this.findByTest(entity.getPartyId().toString())) {// 买过体验机则
            throw new BusinessException("您已购买过体验AI量化,不得重复购买");
        }
        /**
         * 买入金额需要在区间内
         */
//		if (entity.getAmount() < miner.getInvestment_min()) {
//			throw new BusinessException("不得低于该矿机的金额");
//
//		}
        /**
         * 买入金额需要在区间内(非体验矿机)
         */
        if (miner.getTest().equals("N")
                && (entity.getAmount() < miner.getInvestment_min() || entity.getAmount() > miner.getInvestment_max())) {
            if (miner.getInvestment_max() != 0) {
                throw new BusinessException("买入金额需要在区间内");
            } else if (entity.getAmount() < miner.getInvestment_min()) {// 无限制的矿机不得小于最小购买金额
                throw new BusinessException("买入金额需要在区间内");
            }
        }

        String minerBuySymbol = sysparaService.find("miner_buy_symbol").getSvalue();
        // 是否是其他币种购买
        boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
        double close = 0d;
        if (isOtherCoin) {
            List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
            if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
                throw new BusinessException("系统错误，请稍后重试 saveCreate");
            }
            close = realtimes.get(0).getClose();

            saveMinerBuyOtherCoin(entity, minerBuySymbol);
        } else if (miner.getTest().equals("Y")) {
            // 体验矿机：从KYC体验金冻结余额扣除300U
            User user = partyService.getById(entity.getPartyId());
            if (user.getKycBonusAmount() == null || user.getKycBonusAmount() < 300) {
                throw new BusinessException("无体验金资格");
            }
            walletService.updateExtend(String.valueOf(entity.getPartyId()), WalletConstants.WALLET_USDT, 0, -300);
            MoneyLog bonusUseLog = new MoneyLog();
            bonusUseLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            bonusUseLog.setAmount(BigDecimal.valueOf(-300));
            // 购买体验量化订单，消费300U体验金
            bonusUseLog.setLog("Buy experience Quant Order, use 300U bonus, orderNo[" + entity.getOrder_no() + "]");
            bonusUseLog.setUserId(entity.getPartyId());
            bonusUseLog.setWalletType(WalletConstants.WALLET_USDT);
            bonusUseLog.setContentType(WalletConstants.MONEYLOG_CONTENT_KYC_BONUS_USE);
            moneyLogService.save(bonusUseLog);
            user.setKycBonusAmount(0.0);
            user.setKycBonusTime(null);
            partyService.updateById(user);
        } else {
            /**
             * 查看余额
             */
            Wallet wallet = this.walletService.saveWalletByPartyId(String.valueOf(entity.getPartyId()));
            double amount_before = wallet.getMoney().doubleValue();
            if (wallet.getMoney().doubleValue() < entity.getAmount()) {
                // 余额不足
                throw new BusinessException("Insufficient balance");
            }

//		wallet.setMoney(Arith.sub(wallet.getMoney(), entity.getAmount()));
            this.walletService.update(wallet.getUserId(), Arith.sub(0, entity.getAmount()));
            /**
             * 保存资金日志
             */

            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
            moneylog.setAmountAfter(
                    BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
            // 购买矿机产品，订单号[" + entity.getOrder_no() + "]
            moneylog.setLog("Buy Quant Order product, orderNo[" + entity.getOrder_no() + "]");
            moneylog.setUserId(entity.getPartyId());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);

            moneyLogService.save(moneylog);
        }
        /**
         * 起息时间=确认时间加1天
         */
        // 起息时间 = 确认时间加1天
        LocalDateTime earnTime = LocalDateTime.now().plusDays(1);
        entity.setEarn_time(Date.from(earnTime.atZone(ZoneId.systemDefault()).toInstant()));

        if (miner.getTest().equals("Y")) {
            /**
             * 截止时间=购买时间+周期天数
             */
            LocalDateTime stopTime = entity.getCreate_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays((int) miner.getCycle());
            entity.setStop_time(Date.from(stopTime.atZone(ZoneId.systemDefault()).toInstant()));
            entity.setAmount(300.0);// 体验矿机固定300U
        }
        if (findByFist(entity.getPartyId().toString())) {
            entity.setFirst_buy("1");// 标识首次购买
        } else {
            entity.setFirst_buy("0");// 标识首次购买
        }
        this.save(entity);

        redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

        if (miner.getTest().equals("N")) {

            Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue()
                    .get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());

            if (map_partyid == null) {
                map_partyid = new ConcurrentHashMap<String, MinerOrder>();
            }
            map_partyid.put(entity.getOrder_no(), entity);

            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);

            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(entity.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOrder = entity.getAmount();

                Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());

                redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(),
                        Arith.add(null == minerAssets ? 0.000D : minerAssets, minerAssetsOrder));
            }
        }

        /**
         * userdata 数据
         */
        if (isOtherCoin) {
            userDataService.saveMinerBuy(minerUserDataOtherCoin(entity, minerBuySymbol, close));
        } else {
            userDataService.saveMinerBuy(entity);
        }

    }

    protected MinerOrder minerUserDataOtherCoin(MinerOrder entity, String symbol, double close) {
        MinerOrder order = new MinerOrder();
        // 不改变原有的
        BeanUtils.copyProperties(entity, order);
        // 转化成usdt，统计计算
        order.setAmount(Arith.div(order.getAmount(), close));
        return order;
    }

    protected void saveMinerBuyUsdt(MinerOrder entity) {
        Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
        double amount_before = wallet.getMoney().doubleValue();
        if (wallet.getMoney().doubleValue() < entity.getAmount()) {
            throw new BusinessException("余额不足");
        }

        this.walletService.update(wallet.getUserId().toString(), Arith.sub(0, entity.getAmount()));

        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
        moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
        moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
        moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
        // 购买矿机产品，订单号[" + entity.getOrder_no() + "]
        moneylog.setLog("Buy Quant Order product, orderNo[" + entity.getOrder_no() + "]");
        moneylog.setUserId(entity.getPartyId());
        moneylog.setWalletType(Constants.WALLET);
        moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);
        moneyLogService.save(moneylog);
    }

    protected void saveMinerBuyOtherCoin(MinerOrder entity, String symbol) {
        WalletExtend walletExtend = walletService.saveExtendByPara(entity.getUuid(), symbol);

        if (entity.getAmount() > walletExtend.getAmount()) {
            // 持有币种不足
            throw new BusinessException("Insufficient currency balance");
        }

        double amount_before = walletExtend.getAmount();
//		walletExtend.setAmount(Arith.sub(walletExtend.getAmount(), order.getVolume()));

//		walletService.save(walletExtend);
        walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
                Arith.sub(0, entity.getAmount()));
        /*
         * 保存资金日志
         */
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
        moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
        moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
        moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
        // 购买矿机产品，订单号[" + entity.getOrder_no() + "]
        moneylog.setLog("Buy Quant Order product, orderNo[" + entity.getOrder_no() + "]");
        moneylog.setUserId(entity.getUuid());
        moneylog.setWalletType(symbol);
        moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);
        moneyLogService.save(moneylog);
    }

    protected void saveMinerCloseUsdt(MinerOrder entity) {
        // 赎回：将冻结余额（本金+收益）转到可用余额
        double back_money = Arith.add(entity.getAmount(), entity.getProfit());
        WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId(), WalletConstants.WALLET_USDT);
        double freezeBefore = walletExtend.getFreezeAmount();
        // amount=+back_money（可用余额增加），frozenAmount=-back_money（冻结余额减少）
        walletService.updateExtend(entity.getPartyId(), WalletConstants.WALLET_USDT, back_money, -back_money);

        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
        moneylog.setAmountBefore(BigDecimal.valueOf(freezeBefore));
        moneylog.setAmount(BigDecimal.valueOf(back_money));
        moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(freezeBefore, back_money)));
        moneylog.setUserId(entity.getPartyId());
        moneylog.setWalletType(WalletConstants.WALLET_USDT);
        // 量化订单赎回，本金+收益从冻结转入余额
        moneylog.setLog("Quant Order redeem, principal+profit from frozen to available, orderNo[" + entity.getOrder_no() + "]");
        moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);
        moneyLogService.save(moneylog);
    }

    protected void saveMinerCloseOtherCoin(MinerOrder entity, String symbol) {

        WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId().toString(), symbol);

        double amount_before = walletExtend.getAmount();
        // 赎回时一次性发放本金+收益
        double back_money = Arith.add(entity.getAmount(), entity.getProfit());
//		walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
//		this.walletService.update(walletExtend);
        this.walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), back_money);

        /*
         * 保存资金日志
         */
        MoneyLog moneylog_deposit = new MoneyLog();
        moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
        moneylog_deposit.setAmountBefore(BigDecimal.valueOf(amount_before));
        moneylog_deposit.setAmount(BigDecimal.valueOf(back_money));
        moneylog_deposit.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before, back_money)));
        // 量化订单赎回，本金+收益退回
        moneylog_deposit.setLog("Quant Order redeem, principal+profit returned, orderNo[" + entity.getOrder_no() + "]");
        moneylog_deposit.setUserId(entity.getPartyId().toString());
        moneylog_deposit.setWalletType(symbol);
        moneylog_deposit.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);

        moneyLogService.save(moneylog_deposit);
    }

    /**
     * 赎回
     */
    public void saveClose(MinerOrder entity, Miner miner) {
        String buyCurrency = miner.getBuy_currency();
        double close = 0;
        // 体验矿机不退还本金
        boolean isTestMiner = "Y".equals(miner.getTest());
        if ("usdt".equals(buyCurrency)) {
            if (entity.getAmount() != 0 && !isTestMiner) {
                saveMinerCloseUsdt(entity);
            }
        } else {
            List<Realtime> realtimes = this.dataService.realtime(buyCurrency);
            if (null == realtimes || realtimes.size() <= 0) {
                // 行情获取异常，稍后再试
                throw new BusinessException("Market data acquisition exception, please try again later");
            }
            close = realtimes.get(0).getClose();
            saveMinerCloseOtherCoin(entity, buyCurrency);
        }

        entity.setClose_time(new Date());// 赎回时间

        this.updateMinerOrder(entity);

        // userdata 数据
        if ("usdt".equals(buyCurrency)) {
            userDataService.saveMinerClose(entity);
        } else {
            userDataService.saveMinerClose(minerUserDataOtherCoin(entity, buyCurrency, close));
        }

        // 更新矿机订单
        redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);
        String partyId = entity.getPartyId().toString();
        if (!miner.getTest().equals("Y")) {

            Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);
            if (map_partyid == null) {
                map_partyid = new ConcurrentHashMap<String, MinerOrder>();
            }

            map_partyid.put(entity.getOrder_no(), entity);
            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId, map_partyid);

            Double minerAssets = (Double) this.redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);

            MinerOrder minerOld = map_partyid.get(entity.getOrder_no());
            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(minerOld.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOld = minerOld.getAmount();

                minerAssets = null == minerAssets ? 0.000D - minerAssetsOld : minerAssets - minerAssetsOld;
            }

            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(entity.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOrder = entity.getAmount();

                minerAssets = null == minerAssets ? minerAssetsOrder : minerAssets + minerAssetsOrder;
            }

            this.redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId, null == minerAssets ? 0.000D : minerAssets);
        }
    }

    @Override
    public void saveClose(MinerOrder entity) {
        String minerBuySymbol = sysparaService.find("miner_buy_symbol").getSvalue();
        // 是否是其他币种购买
        boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
        double close = 0;
        // 获取矿机信息，判断是否为体验矿机（体验矿机不退还本金）
        Miner miner = minerService.findById(entity.getMiner_id());
        boolean isTestMiner = miner != null && "Y".equals(miner.getTest());

        // 赎回时计算收益
        minerOrderProfitService.saveComputeOrderProfit(entity);

        if (isOtherCoin) {
            List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
            if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
                throw new BusinessException("系统错误，请稍后重试 saveClose");
            }
            close = realtimes.get(0).getClose();

            saveMinerCloseOtherCoin(entity, minerBuySymbol);
        } else if (entity.getAmount() != 0 && !isTestMiner) {// 体验矿机不退还本金
            // 赎回：将冻结余额（本金+收益）转到可用余额
            double back_money = Arith.add(entity.getAmount(), entity.getProfit());
            WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId().toString(), WalletConstants.WALLET_USDT);
            double freezeBefore = walletExtend.getFreezeAmount();
            // amount=+back_money（可用余额增加），frozenAmount=-back_money（冻结余额减少）
            walletService.updateExtend(entity.getPartyId().toString(), WalletConstants.WALLET_USDT, back_money, -back_money);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
            moneylog.setAmountBefore(BigDecimal.valueOf(freezeBefore));
            moneylog.setAmount(BigDecimal.valueOf(back_money));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(freezeBefore, back_money)));
            moneylog.setUserId(entity.getUuid());
            moneylog.setWalletType(WalletConstants.WALLET_USDT);
            // 量化订单赎回，本金+收益从冻结转入余额
            moneylog.setLog("Quant Order redeem, principal+profit from frozen to available, orderNo[" + entity.getOrder_no() + "]");
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);
            moneyLogService.save(moneylog);
        }

        entity.setClose_time(new Date());// 赎回时间
        entity.setTotalIncome(BigDecimal.valueOf(quantPreIncomeService.selectTotalIncome(entity.getUuid())));
        this.updateById(entity);
        /**
         * userdata 数据
         */
        if (isOtherCoin) {
            userDataService.saveMinerClose(minerUserDataOtherCoin(entity, minerBuySymbol, close));
        } else {
            userDataService.saveMinerClose(entity);
        }

        // 更新矿机订单
        redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);
        if (miner != null && "N".equals(miner.getTest())) {

            Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue()
                    .get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());
            if (map_partyid == null) {
                map_partyid = new ConcurrentHashMap<String, MinerOrder>();
            }

            MinerOrder minerOld = map_partyid.get(entity.getOrder_no());

            map_partyid.put(entity.getOrder_no(), entity);
            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);

            Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());

            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(minerOld.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOld = minerOld.getAmount();

                minerAssets = null == minerAssets ? 0.000D - minerAssetsOld : minerAssets - minerAssetsOld;
            }

            // 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
            if ("1".equals(entity.getState())) {

                // 获取 单个订单 矿机总资产
                Double minerAssetsOrder = entity.getAmount();

                minerAssets = null == minerAssets ? 0.000D + minerAssetsOrder : minerAssets + minerAssetsOrder;
            }

            redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(), null == minerAssets ? 0.000D : minerAssets);
        }
    }

    public List<Map<String, Object>> findAllByState(String state) {

        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer("FROM MinerOrder WHERE state =:state ");
        parameters.put("state", state);
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);


//		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 ",
//				new Object[] { state });
        return list;
    }

    @Override
    public MinerOrder findByOrder_no(String order_no) {
        String sql = "SELECT * FROM t_miner_order WHERE order_no = :order_no";
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, java.util.Collections.singletonMap("order_no", order_no));
        if (rows.isEmpty()) return null;
        Map<String, Object> row = rows.get(0);
        MinerOrder o = new MinerOrder();
        o.setUuid(str(row, "uuid"));
        o.setOrder_no(str(row, "order_no"));
        o.setPartyId(str(row, "party_id"));
        o.setMiner_id(str(row, "miner_id"));
        o.setState(str(row, "state"));
        o.setSymbol(str(row, "symbol"));
        o.setFirst_buy(str(row, "first_buy"));
        if (row.get("amount") != null) o.setAmount(((Number) row.get("amount")).doubleValue());
        if (row.get("profit") != null) o.setProfit(((Number) row.get("profit")).doubleValue());
        if (row.get("base_compute_amount") != null) o.setBase_compute_amount(((Number) row.get("base_compute_amount")).doubleValue());
        if (row.get("random_daily_rate") != null) o.setRandom_daily_rate(((Number) row.get("random_daily_rate")).doubleValue());
        if (row.get("expected_total_income") != null) o.setExpectedTotalIncome(((Number) row.get("expected_total_income")).longValue());
        if (row.get("total_income") != null) o.setTotalIncome(new java.math.BigDecimal(row.get("total_income").toString()));
        if (row.get("cycle") != null) o.setCycle(((Number) row.get("cycle")).intValue());
        o.setCreate_time(toDate(row.get("create_time")));
        o.setEarn_time(toDate(row.get("earn_time")));
        o.setStop_time(toDate(row.get("stop_time")));
        o.setClose_time(toDate(row.get("close_time")));
        o.setCompute_day(toDate(row.get("compute_day")));
        return o;
    }

    private static java.util.Date toDate(Object v) {
        if (v == null) return null;
        if (v instanceof java.util.Date) return (java.util.Date) v;
        if (v instanceof java.time.LocalDateTime)
            return java.util.Date.from(((java.time.LocalDateTime) v).atZone(java.time.ZoneId.systemDefault()).toInstant());
        if (v instanceof java.time.LocalDate)
            return java.util.Date.from(((java.time.LocalDate) v).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return null;
    }

    private static String str(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : null;
    }

    @Override
    public List<Map<String, Object>> findByState(String partyId, String state) {
        if (StringUtils.isEmptyString(partyId)) {
            return findAllByState(state);
        }
        /**
         * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
         */
        List<Map<String, Object>> list;
        if (StringUtils.isEmptyString(state)) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE  party_id =:partyId");
            parameters.put("partyId", partyId);
            list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//			list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE  partyId =?0", new Object[] { partyId });
        } else {
            if ("2".equals(state)) {
                Map<String, Object> parameters = new HashMap<String, Object>();
                StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE party_id =?0 and ( state = 0 or state = 2 )");
                parameters.put("partyId", partyId);
                list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE partyId =?0 and ( state = ?1 or state =?2 )  ",
//						new Object[] { partyId, "0", "2" });
            } else {
                Map<String, Object> parameters = new HashMap<String, Object>();
                StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE state = :state and party_id =:partyId");
                parameters.put("state", state);
                parameters.put("partyId", partyId);
                list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 and partyId =?1",
//						new Object[] { state, partyId });
            }
        }

        if (!list.isEmpty())
            return list;
        return null;
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
        Page page = new Page(pageNo, pageSize);
        this.baseMapper.pagedQuery1(page, partyId, state);
        return page;
    }

    @Override
    public void deleteAllByPartyId(String partyId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer("FROM MinerOrder WHERE partyId=:partyId ");
        parameters.put("partyId", partyId);
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);

        //
//		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE partyId=? ",
//				new Object[] { partyId });
        if (!CollectionUtils.isEmpty(list)) {
            for (Map<String, Object> order : list) {
//				this.getHibernateTemplate().delete(order);
//				this.removeById(order);
//				redisTemplate.delete(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no());
            }
            redisTemplate.delete(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);

            this.redisTemplate.delete(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);
        }
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setMinerService(MinerService minerService) {
        this.minerService = minerService;
    }

    public MinerOrder findById(String id) {// 赎回时使用

//		namedParameterJdbcTemplate.
        return this.baseMapper.selectById(id);
//		return (MinerOrder) getHibernateTemplate().get(MinerOrder.class, id);
    }

    /**
     * true:买过体验矿机，false:没买过
     */
    @Override
    public boolean findByTest(String partyId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer("SELECT ");
        queryString.append("mo.UUID ");
        queryString.append("FROM T_MINER_ORDER mo ");
        queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
        queryString.append("WHERE 1=1  ");
        queryString.append("AND PARTY_ID=:partyId AND m.TEST='Y' ");
        parameters.put("partyId", partyId);
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
        return list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null;// 存在返回值，且不为空
    }

    /**
     * true：首次购买，false:非首次购买
     *
     * @param partyId
     * @return
     */
    @Override
    public boolean findByFist(String partyId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer("SELECT ");
        queryString.append("mo.UUID ");
        queryString.append("FROM T_MINER_ORDER mo ");
        queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
        queryString.append("WHERE 1=1  ");
        queryString.append("AND PARTY_ID=:partyId AND m.TEST='N' ");
        queryString.append("AND FIRST_BUY='1' ");
        parameters.put("partyId", partyId);
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
        return !(list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null);// 存在返回值，且不为空
    }

    @Override
    public boolean getUnLockMiner(String partyId, String minerId) {
        Miner miner = this.minerService.cacheById(minerId);

        List<UserRecom> list_userRecoms = this.userRecomService.findRecoms(partyId);
        int cycle = 0;
        for (int i = 0; i < list_userRecoms.size(); i++) {
            Map<String, MinerOrder> map = (Map<String, MinerOrder>) redisTemplate.opsForValue()
                    .get(MinerRedisKeys.MINER_ORDER_PARTY_ID + list_userRecoms.get(i).getUserId().toString());

            if (map != null) {
                cycle = cycle + map.size();
            }
        }

        return cycle >= miner.getCycle();// 如果周期比该矿机的大，则解锁
    }

    /**
     * 新增矿机订单
     */
    public void insertMinerOrder(MinerOrder order) {
        this.save(order);
    }

    /**
     * 修改矿机订单
     */
    public void updateMinerOrder(MinerOrder order) {
        this.updateById(order);
    }

    /**
     * 生成随机日利率
     * <p>
     * 在给定的日利率开始值和结束值之间随机生成一个利率值
     * 例如：daily_rate_start=1.5, daily_rate_end=2.5
     * 随机生成介于1.5%和2.5%之间的利率值
     *
     * @param dailyRateStart 日利率开始值(%)
     * @param dailyRateEnd   日利率结束值(%)
     * @return 随机生成的日利率值(%)
     */
    private double generateRandomDailyRate(double dailyRateStart, double dailyRateEnd) {
        if (dailyRateStart <= 0 || dailyRateEnd <= 0) {
            // 如果配置异常，返回默认值1%
            return 1.0;
        }
        if (dailyRateEnd < dailyRateStart) {
            // 如果结束值小于开始值，交换两者
            double temp = dailyRateStart;
            dailyRateStart = dailyRateEnd;
            dailyRateEnd = temp;
        }
        // 生成介于开始值和结束值之间的随机数
        double randomRate = dailyRateStart + Math.random() * (dailyRateEnd - dailyRateStart);
        // 保留4位小数
        return Math.round(randomRate * 10000) / 10000.0;
    }
}
