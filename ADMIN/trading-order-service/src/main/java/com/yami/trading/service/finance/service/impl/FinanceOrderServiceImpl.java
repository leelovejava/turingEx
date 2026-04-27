package com.yami.trading.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.UUIDGenerator;
import com.yami.trading.dao.finance.FinanceOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.finance.service.FinanceService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class FinanceOrderServiceImpl extends ServiceImpl<FinanceOrderMapper, FinanceOrder> implements FinanceOrderService {
    @Autowired
    protected WalletService walletService;
    @Autowired
    protected MoneyLogService moneyLogService;
    @Autowired
    protected FinanceService financeService;
    @Autowired
    protected UserService partyService;
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected UserRecomService userRecomService;

    public void saveCreate(FinanceOrder entity) {

        entity.setCreateTime(new Date());

        // 加入周期
        Finance finance = financeService.findById(entity.getFinanceId());
        entity.setCycle(finance.getCycle());

        // 买入金额需要在区间内
        if (entity.getAmount() > finance.getInvestmentMax() || entity.getAmount() < finance.getInvestmentMin()) {
            throw new BusinessException("金额错误");

        }

        // 查看余额
        Wallet wallet = this.walletService.saveWalletByPartyId((String) entity.getPartyId());
        double amount_before = wallet.getMoney().doubleValue();
        System.out.println("wallet = " + wallet);
        System.out.println("amount_before = " + amount_before);
        if (amount_before < entity.getAmount()) {
            throw new BusinessException("余额不足");
        }

//		wallet.setMoney(Arith.sub(wallet.getMoney(), entity.getAmount()));
//		this.walletService.update(wallet);
        this.walletService.update(wallet.getUserId(), Arith.sub(0, entity.getAmount()));

        // 起息时间=确认时间加1天
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 把日期往后增加一天.整数往后推,负数往前移动
        calendar.add(calendar.DATE, 1);
        // 这个时间就是日期往后推一天的结果
        date = calendar.getTime();
        entity.setEarnTime(date);

        // 截止时间=起息时间+周期+1
        int days = (int) Arith.sub(entity.getCycle(), 1);
        calendar.add(calendar.DATE, days);
        date = calendar.getTime();
        entity.setStopTime(date);

        // 默认的赎回时间=截止时间+1
        calendar.add(calendar.DATE, 1);
        date = calendar.getTime();
        entity.setCloseTime(date);

        //
        double amount = Arith.sub(0, entity.getAmount());
        double amount_after = Arith.add(amount_before, amount);
        System.out.println("amount_before => " + amount_before);
        System.out.println("amount_after => " + amount_after);
        System.out.println("amount => " + amount);

        // 保存资金日志
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
        moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
        moneylog.setAmount(BigDecimal.valueOf(amount));
        moneylog.setAmountAfter(BigDecimal.valueOf(amount_after));
        moneylog.setLog("购买理财产品，订单号[" + entity.getOrderNo() + "]");
        moneylog.setUserId((String) entity.getPartyId());
        moneylog.setWalletType(Constants.WALLET);
        moneylog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_BUY);

        moneyLogService.save(moneylog);
        String uuid = UUIDGenerator.getUUID();
        entity.setUuid(uuid);

        this.save(entity);

        // 购买理财产品后是否需要增加用户提现流水，1不增加，2要增加(coinspace,cqpro,amex,emex)
        String finance_buy_add_userdata = this.sysparaService.find("finance_buy_add_userdata").getSvalue();

        // 理财购买后给他增加提现流水限制金额
        if ("2".equals(finance_buy_add_userdata)) {
            User party = this.partyService.cacheUserBy(entity.getPartyId().toString());
//			User party = this.partyService.cachePartyBy(entity.getPartyId(), false);
            party.setWithdrawLimitAmount(
                    BigDecimal.valueOf(
                            Arith.add(party.getWithdrawLimitAmount().doubleValue(), entity.getAmount())
                    ));
//			party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), entity.getAmount()));
            partyService.updateById(party);
        }
    }

    public void saveClose(FinanceOrder entity, Date systemTime) {

        double get_amount = Arith.add(entity.getAmount(), entity.getProfit());
        if (get_amount < 0) {
            entity.setProfit(Arith.sub(0, entity.getAmount()));
            get_amount = Arith.add(entity.getAmount(), entity.getProfit());
        }

        if (get_amount > 0) {
            Wallet wallet = this.walletService.saveWalletByPartyId((String) entity.getPartyId());
            double amount_before = wallet.getMoney().doubleValue();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
            this.walletService.update(wallet.getUserId(), get_amount);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, get_amount)));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before, get_amount)));
            moneylog.setLog("赎回理财产品，订单号[" + entity.getOrderNo() + "]");
            moneylog.setUserId((String) entity.getPartyId());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_BACK);
            moneylog.setCreateTime(systemTime != null ? systemTime : new Date());

            moneyLogService.save(moneylog);

        }

        entity.setCloseTime(new Date());// 赎回时间
        this.update(entity);

        this.userDataService.saveSellFinance(entity);

    }

    public void saveClose(FinanceOrder entity) {
        double get_amount = Arith.add(entity.getAmount(), entity.getProfit());
        if (get_amount < 0) {
            entity.setProfit(Arith.sub(0, entity.getAmount()));
            get_amount = Arith.add(entity.getAmount(), entity.getProfit());
        }

        if (get_amount > 0) {
            Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId().toString());
            double amount_before = wallet.getMoney().doubleValue();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
            this.walletService.update(wallet.getUserId(), get_amount);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, get_amount)));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(wallet.getMoney().doubleValue(), get_amount)));
            moneylog.setLog("赎回理财产品，订单号[" + entity.getOrderNo() + "]");
            moneylog.setUserId(entity.getPartyId().toString());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_BACK);
            moneylog.setCreateTime(new Date());

            moneyLogService.save(moneylog);

        }

        entity.setCloseTime(new Date());// 赎回时间
        this.update(entity);

        this.userDataService.saveSellFinance(entity);

    }

    @Override
    public FinanceOrder findByOrder_no(String order_no) {
        LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<FinanceOrder>()
                .eq(FinanceOrder::getOrderNo, order_no)
                .last("LIMIT 1");
        return getOne(queryWrapper);
    }

    public List<FinanceOrder> findByState(String partyId, String state) {
        /**
         * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
         */
        //暂时隐藏
        List<FinanceOrder> list = null;
        if ("2".equals(state)) {
//			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE partyId =?0 and state = ?1 or state =?2  ",
//					new Object[] { partyId, "0", "2" });
            LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<FinanceOrder>().and(
                    wq -> wq.eq(FinanceOrder::getState, 0).or().eq(FinanceOrder::getState, 2))
                    .eq(FinanceOrder::getPartyId, partyId);
            list = this.getBaseMapper().selectList(queryWrapper);
        }
        if ("0".equals(state) || "1".equals(state)) {
//			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE state = ?0 and partyId =?1",
//					new Object[] { state, partyId });
            LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<FinanceOrder>()
                    .eq(FinanceOrder::getState, state).eq(FinanceOrder::getPartyId, partyId);
            list = this.getBaseMapper().selectList(queryWrapper);
        } else {
//			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE  partyId =?0", new Object[] { partyId });
            LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<FinanceOrder>()
                    .eq(FinanceOrder::getPartyId, partyId);
            list = this.getBaseMapper().selectList(queryWrapper);
        }

        if (!list.isEmpty())
            return list;
        return null;
    }

    public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
        Page page = new Page(pageNo, pageSize);
        LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(partyId), FinanceOrder::getPartyId, partyId);
        if(Objects.equals(state, "4")){
            queryWrapper.eq(FinanceOrder::getState, "0").or().eq(FinanceOrder::getState, "2").eq(StringUtils.isNotEmpty(partyId), FinanceOrder::getPartyId, partyId);;
        }else{
            queryWrapper.eq(StringUtils.isNotEmpty(state), FinanceOrder::getState, state);
        }

        return baseMapper.selectPage(page, queryWrapper);
    }

//	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
//		/**
//		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
//		 */
//		StringBuffer queryString = new StringBuffer("");
//		queryString.append(" FROM FinanceOrder WHERE 1=1 ");
//		Map parameters = new HashMap();
//		queryString.append("AND partyId=:partyId ");
//		parameters.put("partyId", partyId);
//		if (StringUtils.isNotEmpty(state)) {
//			if ("2".equals(state)) {
//				queryString.append(" AND (state =:state_2 OR state =:state_0 )");
//				parameters.put("state_2", "0");
//				parameters.put("state_0", "2");
//
//			} else if ("02".equals(state)) {
//				/**

//				 * 兼容其他项目所写,2提前赎回 (违约)
//				 */
//				queryString.append("AND state=:state ");
//				parameters.put("state", "2");
//			} else {
//				queryString.append("AND state=:state ");
//				parameters.put("state", state);
//			}
//		}
//		queryString.append(" order by create_time desc ");
//
//		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
//
//		return page;
//	}

    /**
     * 根据日期获取到当日的购买订单
     *
     * @param pageNo
     * @param pageSize
     * @param date
     * @return
     */
    public Page pagedQueryByDate(int pageNo, int pageSize, String date) {
        /**
         * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
         */
        Page page = new Page(pageNo, pageSize);
        return baseMapper.pagedQueryByDate(page, date);

//		StringBuffer queryString = new StringBuffer("");
//		queryString.append(" FROM FinanceOrder WHERE 1=1 ");
//		Map parameters = new HashMap();
//		queryString.append("AND DATE(create_time) = DATE(:date) ");
//		parameters.put("date", date);
//
//		queryString.append(" order by create_time asc ");
//
//		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
//
//		return page;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setFinanceService(FinanceService financeService) {
        this.financeService = financeService;
    }

    @Override
    public List<FinanceOrder> getAllStateBy_1() {
        LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<FinanceOrder>().eq(FinanceOrder::getState, 1);
        return list(queryWrapper);
    }

    public void addListProfit(FinanceOrder order) {

        /**
         * 截止时间要大于现在这个时间则计算收益 赎回时间如果小于现在时间则不计算收益
         */
        List<Finance> finances = financeService.findAll();
        Finance finance = new Finance();
        Date now_date = new Date();
        String date_string = DateUtils.format(now_date, DateUtils.DF_yyyyMMdd);

        /**
         * 现在时间是否大于赎回时间，如果大于等于，则赎回，不计算收益了,并将状态改变为已赎回 只计算年月日
         */
        String order_date = DateUtils.format(order.getCloseTime(), DateUtils.DF_yyyyMMdd);
        String order_earn = DateUtils.format(order.getEarnTime(), DateUtils.DF_yyyyMMdd);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_now = null;
        Date close_date = null;
        Date earn_date = null;
        try {
            date_now = dateFormat.parse(date_string);
            close_date = dateFormat.parse(order_date);
            earn_date = dateFormat.parse(order_earn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * 理财收益赎回时统一下发1还是每日下发2
         */
        String finance_profit_days = sysparaService.find("finance_profit_days").getSvalue();
        System.out.println("finance_profit_days2 = " + finance_profit_days);
        if ("1".equals(finance_profit_days)) {

            // 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
            int arrive_close = date_now.compareTo(close_date);
            if (arrive_close >= 0) {
                order.setState("0");
                saveClose(order);
                return;
            }
            // 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
            // 起息时间要大于等于今天
            int arrive_earn = date_now.compareTo(earn_date);
            if (arrive_earn < 0) {
                return;
            }

            for (int j = 0; j < finances.size(); j++) {
                finance = finances.get(j);
                if (finance.getUuid().equals(order.getFinanceId())) {
                    break;
                }
            }
            /**
             * 当日获取的收益
             */
            double today_rate = Arith.mul(finance.getTodayRate(), 0.01);
            double get_amount = Arith.mul(order.getAmount(), today_rate);
            order.setProfit(Arith.add(order.getProfit(), get_amount));

            this.update(order);
        }

        if ("2".equals(finance_profit_days)) {

            // 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
            int arrive_close = date_now.compareTo(close_date);
            if (arrive_close >= 0) {
                order.setState("0");
                // 自动赎回调用,只返回本金
                saveClosePrincipal(order);
                return;
            }
            // 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
            /**
             * 起息时间要大于等于今天
             */
            int arrive_earn = date_now.compareTo(earn_date);
            if (arrive_earn < 0) {
                return;
            }

            for (int j = 0; j < finances.size(); j++) {
                finance = finances.get(j);
                if (finance.getUuid().equals(order.getFinanceId())) {
                    break;
                }
            }
            /**
             * 当日获取的收益
             */
            double today_rate = Arith.mul(finance.getTodayRate(), 0.01);
            double get_amount = Arith.mul(order.getAmount(), today_rate);

            // 5,0.5,3,0.3,2,0.2
            String finance_level_profit = sysparaService.find("finance_level_profit").getSvalue();
            boolean finance_level_profit_open = StringUtils.isNotEmpty(finance_level_profit);
            // 判断是否有理财推荐奖励，如果有，需要扣除30%给推荐人
            String finance_bonus_parameters = "";
            finance_bonus_parameters = this.sysparaService.find("finance_bonus_parameters").getSvalue();
            if ((finance_bonus_parameters != null && !"".equals(finance_bonus_parameters))
                    || finance_level_profit_open) {
                String[] finance_bonus_array = StringUtils.isNotEmpty(finance_bonus_parameters)
                        ? finance_bonus_parameters.split(",")
                        : new String[]{};
                List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId().toString());

                if (CollectionUtils.isNotEmpty(list_parents)) {

                    int loop = 0;
                    for (int i = 0; i < list_parents.size(); i++) {
                        if (finance_level_profit_open) {// 等级奖励只给推荐一人
                            if (loop >= 1) {
                                break;
                            }
                        } else {
                            if (loop >= 3) {
                                break;
                            }
                        }

                        User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
                        if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
                            continue;
                        }
                        get_amount = Arith.mul(order.getAmount(), today_rate);
                        /**
                         * 增加推荐人理财收益
                         */
                        double parent_get_money = 0d;
                        if (finance_level_profit_open) {
                            parent_get_money = levelProfit(party_parent.getUserId(), get_amount,
                                    finance_level_profit);
                        } else {
                            double finance_pip_amount = Double.valueOf(finance_bonus_array[loop]);
                            parent_get_money = Arith.mul(get_amount, finance_pip_amount);
                        }
                        if (parent_get_money == 0d) {
                            break;
                        }

                        Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getUserId());
                        double amount_before_parent = wallet_parent.getMoney().doubleValue();
                        walletService.update(wallet_parent.getUserId(), parent_get_money);
                        /**
                         * 保存资金日志
                         */
                        MoneyLog moneyLog = new MoneyLog();
                        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
                        moneyLog.setAmountBefore(BigDecimal.valueOf(amount_before_parent));
                        moneyLog.setAmount(BigDecimal.valueOf(parent_get_money));
                        moneyLog.setAmountAfter(
                                BigDecimal.valueOf(
                                        Arith.add(wallet_parent.getMoney().doubleValue(), parent_get_money)));
                        moneyLog.setLog("第" + (i + 1) + "代下级用户，每日理财收益奖励金");
                        moneyLog.setUserId(party_parent.getUserId());
                        moneyLog.setWalletType(Constants.WALLET);
                        moneyLog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT);
                        moneyLogService.save(moneyLog);

                        loop++;
                    }
                }
                /**
                 * 理财收益减少百分之30给上级
                 */
                if (!finance_level_profit_open) {// 等级奖励则无需扣除本人收益
                    get_amount = Arith.sub(get_amount, Arith.mul(order.getAmount(), 0.3));
                }
            }

            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId().toString());
            double amount_before = wallet.getMoney().doubleValue();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
            this.walletService.update(wallet.getUserId(), get_amount);
//			this.walletService.update(wallet);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, get_amount)));
            moneylog.setAmountAfter(BigDecimal.valueOf(
                    Arith.add(wallet.getMoney().doubleValue(), get_amount)));
            moneylog.setLog("理财产品每日收益，订单号[" + order.getOrderNo() + "]");
            moneylog.setUserId(order.getPartyId().toString());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_FINANCE_PROFIT);

            moneyLogService.save(moneylog);

            order.setProfit(Arith.add(order.getProfit(), get_amount));

            this.update(order);
        }

    }

    public void addListProfit(FinanceOrder order, Date systemTime) {

        /**
         * 截止时间要大于现在这个时间则计算收益 赎回时间如果小于现在时间则不计算收益
         */
        List<Finance> finances = financeService.findAll();
        Finance finance = new Finance();
        Date now_date = systemTime != null ? systemTime : new Date();
        String date_string = DateUtils.format(now_date, DateUtils.DF_yyyyMMdd);

        /**
         * 现在时间是否大于赎回时间，如果大于等于，则赎回，不计算收益了,并将状态改变为已赎回 只计算年月日
         */
        String order_date = DateUtils.format(order.getCloseTime(), DateUtils.DF_yyyyMMdd);
        String order_earn = DateUtils.format(order.getEarnTime(), DateUtils.DF_yyyyMMdd);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_now = null;
        Date close_date = null;
        Date earn_date = null;
        try {
            date_now = dateFormat.parse(date_string);
            close_date = dateFormat.parse(order_date);
            earn_date = dateFormat.parse(order_earn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * 理财收益赎回时统一下发1还是每日下发2
         */
        String finance_profit_days = sysparaService.find("finance_profit_days").getSvalue();
        System.out.println("finance_profit_days = " + finance_profit_days);

        /**
         * 1计算收益，赎回统一发放
         */
        if ("1".equals(finance_profit_days)) {
            // 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
            int arrive_close = date_now.compareTo(close_date);
            if (arrive_close >= 0) {
                order.setState("0");
                saveClose(order, systemTime);
                return;
            }
            // 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
            /**
             * 起息时间要大于等于今天
             */
            int arrive_earn = date_now.compareTo(earn_date);
            if (arrive_earn < 0) {
                return;
            }

            for (int j = 0; j < finances.size(); j++) {
                finance = finances.get(j);
                if (finance.getUuid().equals(order.getFinanceId())) {
                    break;
                }
            }
            /**
             * 当日获取的收益
             */
            double today_rate = Arith.mul(finance.getTodayRate(), 0.01);
            double get_amount = Arith.mul(order.getAmount(), today_rate);

            order.setProfit(Arith.add(order.getProfit(), get_amount));

            this.update(order);

        }

        /**
         * 2每日下发收益
         */
        if ("2".equals(finance_profit_days)) {

            // 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
            int arrive_close = date_now.compareTo(close_date);
            if (arrive_close >= 0) {
                order.setState("0");
                /**
                 * 自动赎回调用,只返回本金
                 */
                saveClosePrincipal(order);
                return;
            }
            // 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
            /**
             * 起息时间要大于等于今天
             */
            int arrive_earn = date_now.compareTo(earn_date);
            if (arrive_earn < 0) {
                return;
            }

            for (int j = 0; j < finances.size(); j++) {
                finance = finances.get(j);
                if (finance.getUuid().equals(order.getFinanceId())) {
                    break;
                }
            }

            /**
             * 当日获取的收益
             */

            double today_rate = Arith.mul(finance.getTodayRate(), 0.01);
            double get_amount = Arith.mul(order.getAmount(), today_rate);

            // 5,0.5,3,0.3,2,0.2
            String finance_level_profit = sysparaService.find("finance_level_profit").getSvalue();
            boolean finance_level_profit_open = StringUtils.isNotEmpty(finance_level_profit);
            // 判断是否有理财推荐奖励，如果有，需要扣除30%给推荐人
            String finance_bonus_parameters = "";
            finance_bonus_parameters = this.sysparaService.find("finance_bonus_parameters").getSvalue();
            if ((finance_bonus_parameters != null && !"".equals(finance_bonus_parameters))
                    || finance_level_profit_open) {
                String[] finance_bonus_array = StringUtils.isNotEmpty(finance_bonus_parameters)
                        ? finance_bonus_parameters.split(",")
                        : new String[]{};
                List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId().toString());

                if (CollectionUtils.isNotEmpty(list_parents)) {

                    int loop = 0;
                    for (int i = 0; i < list_parents.size(); i++) {
                        if (finance_level_profit_open) {// 等级奖励只给推荐一人
                            if (loop >= 1) {
                                break;
                            }
                        } else {
                            if (loop >= 3) {
                                break;
                            }
                        }
                        User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
                        if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
                            continue;
                        }
                        get_amount = Arith.mul(order.getAmount(), today_rate);
                        /**
                         * 增加推荐人理财收益
                         */
                        double parent_get_money = 0d;
                        if (finance_level_profit_open) {
                            parent_get_money = levelProfit(party_parent.getUserId(), get_amount,
                                    finance_level_profit);
                        } else {
                            double finance_pip_amount = Double.valueOf(finance_bonus_array[loop]);
                            parent_get_money = Arith.mul(get_amount, finance_pip_amount);
                        }
                        if (parent_get_money == 0d) {
                            break;
                        }
                        Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getUserId());
                        double amount_before_parent = wallet_parent.getMoney().doubleValue();
                        walletService.update(wallet_parent.getUserId(), parent_get_money);
                        /**
                         * 保存资金日志
                         */
                        MoneyLog moneyLog = new MoneyLog();
                        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
                        moneyLog.setAmountBefore(BigDecimal.valueOf(amount_before_parent));
                        moneyLog.setAmount(BigDecimal.valueOf(parent_get_money));
                        moneyLog.setAmountAfter(
                                BigDecimal.valueOf(Arith.add(wallet_parent.getMoney().doubleValue(), parent_get_money)));
                        moneyLog.setLog("第" + (i + 1) + "代下级用户，每日理财收益奖励金");
                        moneyLog.setUserId(party_parent.getUserId());
//						moneyLog.setPartyId(party_parent.getId().toString());
                        moneyLog.setWalletType(Constants.WALLET);
                        moneyLog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT);
                        moneyLog.setCreateTime(systemTime);
                        moneyLogService.save(moneyLog);

                        loop++;
                    }
                }
                /**
                 * 理财收益减少百分之30给上级
                 */
                if (finance_level_profit_open) {// 等级奖励则无需扣除本人收益
                    get_amount = Arith.sub(get_amount, Arith.mul(get_amount, 0.3));
                }
            }

            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId().toString());
            double amount_before = wallet.getMoney().doubleValue();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
            this.walletService.update(wallet.getUserId().toString(), get_amount);
//			this.walletService.update(wallet);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, get_amount)));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(wallet.getMoney().doubleValue(), get_amount)));
            moneylog.setLog("理财产品每日收益，订单号[" + order.getOrderNo() + "]");
            moneylog.setUserId(order.getPartyId().toString());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(Constants.MONEYLOG_CONTENT_FINANCE_PROFIT);
            moneylog.setCreateTime(systemTime);
            moneyLogService.save(moneylog);
            order.setProfit(Arith.add(order.getProfit(), get_amount));
            this.update(order);

        }

    }

    /**
     * 每日下发收益,自动赎回调用,只返回本金
     */
    public void saveClosePrincipal(FinanceOrder entity) {
        double get_amount = Arith.add(entity.getAmount(), entity.getProfitBefore());
        if (get_amount < 0) {
            entity.setProfit(Arith.sub(0, entity.getAmount()));
            get_amount = Arith.add(entity.getAmount(), entity.getProfit());
        }

        if (get_amount > 0) {
            Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId().toString());
            double amount_before = wallet.getMoney().doubleValue();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
            this.walletService.update(wallet.getUserId().toString(), get_amount);
            /**
             * 保存资金日志
             */
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
            moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
            moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, get_amount)));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(wallet.getMoney().doubleValue(), get_amount)));
            moneylog.setLog("赎回理财产品，订单号[" + entity.getOrderNo() + "]");
            moneylog.setUserId(entity.getPartyId().toString());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_BACK);

            moneyLogService.save(moneylog);

        }

        this.update(entity);

        this.userDataService.saveSellFinance(entity);

    }

    /**
     * 等级奖励
     *
     * @param partyId
     * @param profit
     * @return 返回奖励
     */
    public double levelProfit(String partyId, double profit, String finance_level_profit) {
        // 5,0.5,3,0.3,2,0.2
        double levelProfitMoney = 0d;
        if (StringUtils.isEmptyString(finance_level_profit)) {
            return levelProfitMoney;
        }
        List<UserRecom> listRecoms = this.userRecomService.findRecoms(partyId);
        int recomsNum = listRecoms.size();
        String[] finance_level_profit_array = finance_level_profit.split(",");

        for (int i = 0; i < finance_level_profit_array.length; i++) {
            double levelNeed = Double.valueOf(finance_level_profit_array[i]);

            if (recomsNum >= levelNeed) {// 等级满足要求
                double pipAmount = Double.valueOf(finance_level_profit_array[i + 1]);
                levelProfitMoney = Arith.mul(profit, pipAmount);
                break;
            }
            i++;
        }
        return levelProfitMoney;
    }

    public FinanceOrder findById(String id) {
        return getById(id);
    }

    public void update(FinanceOrder entity) {
       updateById(entity);
    }
}
