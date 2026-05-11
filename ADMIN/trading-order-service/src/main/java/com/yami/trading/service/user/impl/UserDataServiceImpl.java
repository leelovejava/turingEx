package com.yami.trading.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.user.dto.ChildrenLever;
import com.yami.trading.bean.user.dto.UserBenefitsDto;
import com.yami.trading.bean.user.dto.UserDataWithdrawLimitDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.dao.user.UserDataMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserDataSumService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData> implements UserDataService {
    @Autowired
    UserService userService;
    @Autowired
    UserRecomService userRecomService;
    @Autowired
    UserDataSumService userDataSumService;
    @Autowired
    UserService secUserService;
    @Autowired
    DataService dataService;
    @Resource
    NamedParameterJdbcOperations namedParameterJdbcTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    SysparaService sysparaService;
    String REDIS_KEY = "userdata:";
    @Autowired
    private ItemService itemService;
    private Map<String, Map<String, UserData>> cache = new ConcurrentHashMap<>();

    @Autowired
    WalletService walletService;

    @Autowired
    MoneyLogService moneyLogService;

    @PostConstruct
    public void init() {
        List<UserData> all = list();
        if(null == all) all = new ArrayList<>();
        for (UserData item : all) {
            if(null == item) continue;
            this.setCache(item);
        }
    }

    private UserData findBydate(String userId, Date date) {
        Date startDate = DateUtil.minDate(date);
        Date endDate = DateUtil.maxDate(date);
        List<UserData> list = list(Wrappers.<UserData>query().lambda().between(UserData::getCreateTime, startDate, endDate)
                .eq(UserData::getUserId, userId));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, UserData> cacheByPartyId(String partyId) {
        return cache.get(partyId);
    }

    @Override
    public Page listUserGenefits(Page page, Date startTime, Date endTime, String userName, List<String> children) {
        baseMapper.listUserGenefits(page, startTime, endTime, userName, children);
        compute(page.getRecords());
        return page;
    }

    /**
     * COM盘（首页统计模板）定制需求 下盘后删除
     */
    @Override
    public Map<String, BigDecimal> queryUserDataByUserId(String userId) {
        List<UserData> list = baseMapper.selectList(new QueryWrapper<UserData>().eq("user_id", userId));
        return buildData(list);
    }

    /**
     * 获取今日充值的人数
     *
     * @return
     */
    @Override
    public long countTodayRechargeUser(List<String> userIds) {
        Date now = new Date();
        LambdaQueryWrapper<UserData> lambdaQueryWrapper = Wrappers.<UserData>query().lambda().between(UserData::getCreateTime, DateUtil.minDate(now),
                DateUtil.maxDate(now)).gt(UserData::getRecharge, 0);
        if (CollectionUtil.isNotEmpty(userIds)) {
            lambdaQueryWrapper.in(UserData::getUserId, userIds);
        }
        return count(lambdaQueryWrapper);
    }

    @Override
    public UserBenefitsDto daySumData(Date startTime, Date endTime, List<String> userIds) {

        return baseMapper.daySumData(startTime, endTime, userIds);
    }

    @Override
    public Map<String, Object> daySumDataOld(String day, List<String> userIds) {
        Map<String,Object> parameters = new HashMap<String,Object>();
        StringBuffer queryString = new StringBuffer("select ");
//		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
        queryString.append("ifnull(sum(ud.recharge),0) as recharge,ifnull(sum(ud.recharge_usdt),0) as recharge_usdt,ifnull(sum(ud.recharge_eth),0) as recharge_eth,ifnull(sum(ud.recharge_btc),0) as recharge_btc,ifnull(sum(ud.recharge_ht),0) as recharge_ht,ifnull(sum(ud.recharge_ltc),0) as recharge_ltc,"
                + "ifnull(sum(ud.withdraw),0) as withdraw,ifnull(sum(ud.recharge_withdrawal_fee),0) as recharge_withdrawal_fee,ifnull(sum(ud.recharge_usdt)-sum(ud.withdraw),0) as balance_amount, ");//充提
        queryString.append("ifnull(sum(ud.fee),0) as fee,ifnull(sum(ud.order_income),0) as order_income, ");//永续
        queryString.append("ifnull(sum(ud.finance_amount),0) as finance_amount,ifnull(sum(ud.finance_income),0) as finance_income, ");//理财
        queryString.append("ifnull(sum(ud.exchange_fee),0) as exchange_fee,ifnull(sum(ud.exchange_income),0) as exchange_income, ");//币币
        queryString.append("ifnull(sum(ud.furtures_fee),0) as furtures_fee,ifnull(sum(ud.furtures_income),0) as furtures_income, ");//交割
        queryString.append("ifnull(sum(ud.miner_amount),0) as miner_amount,ifnull(sum(ud.miner_income),0) as miner_income, ");//矿机
        queryString.append("ifnull(sum(ud.third_recharge_amount),0) as third_recharge_amount, ");//三方充值
        queryString.append("ifnull(sum(ud.exchange_lever_amount),0) as exchange_lever_amount,ifnull(sum(ud.exchange_lever_fee),0) as exchange_lever_fee,ifnull(sum(ud.exchange_lever_order_income),0) as exchange_lever_order_income ");//币币杠杆
        queryString.append("from t_userdata ud ");
        queryString.append("where 1=1 ");
        queryString.append("and ud.rolename ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
        if (CollectionUtil.isNotEmpty(userIds)) {
            queryString.append(" and ud.user_id in (:children) ");
            parameters.put("children", userIds);
        }
        if (StringUtils.isNotEmpty(day)) {
            queryString.append("and date(ud.create_time) = date(:day)  ");
            parameters.put("day", DateUtils.toDate(day));
        }
        List<Map<String, Object>> queryForList = this.namedParameterJdbcTemplate.queryForList( queryString.toString(), parameters);
        computeSum(queryForList,false);
        return queryForList.get(0);
    }
    private void computeSum(List<Map<String,Object>> datas,boolean isSum) {
        if(CollectionUtils.isEmpty(datas)) return;
        Double totle_income=0d;
        Double totle_fee = 0d;
        Double business_profit = 0d;//交易盈亏
        Double fin_miner_amount = 0d;//理财 矿机 交易额
        Double fin_miner_income = 0d;//理财 矿机 收益
        for(Map<String,Object> data:datas) {

            totle_income=0d;
            totle_fee = 0d;
            business_profit = 0d;
            fin_miner_amount = 0d;
            fin_miner_income = 0d;
            if(null != data.get("order_income"))
                data.put("order_income", Arith.sub(0, new Double(data.get("order_income").toString())));//订单收益负数
            if(null != data.get("finance_income"))
                data.put("finance_income", Arith.sub(0, new Double(data.get("finance_income").toString())));//理财收益负数
            if(null != data.get("exchange_income"))
//			data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));//币币收益负数
                data.put("exchange_income",0);//币币收益负数
            if(null != data.get("furtures_income"))
                data.put("furtures_income", Arith.sub(0, new Double(data.get("furtures_income").toString())));//交割收益负数
            if (null != data.get("miner_income"))
                data.put("miner_income", Arith.sub(0, new Double(data.get("miner_income").toString())));// 矿机收益负数
            if (null != data.get("exchange_lever_order_income"))
                data.put("exchange_lever_order_income", Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString())));// 币币收益负数

            if(!dataExistNull(data)) continue;
            totle_income = Arith.add(totle_income,new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("order_income").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("fee").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("finance_income").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("exchange_fee").toString()));
            totle_income = Arith.add(totle_income,new Double(0));
//			totle_income = Arith.add(totle_income,new Double(data.get("exchange_income").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("furtures_fee").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("furtures_income").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("miner_income").toString()));
            totle_income = Arith.add(totle_income,new Double(data.get("exchange_lever_order_income").toString()));
            data.put("totle_income", totle_income);

            totle_fee = Arith.add(totle_fee, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("furtures_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_lever_fee").toString()));
            data.put("totle_fee", totle_fee);

            business_profit = Arith.add(business_profit, new Double(data.get("order_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("furtures_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("business_profit", business_profit);

            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("finance_amount").toString()));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("miner_amount").toString()));
            data.put("fin_miner_amount", fin_miner_amount);

            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("finance_income").toString()));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("miner_income").toString()));
            data.put("fin_miner_income",  fin_miner_income);

            data.put("recharge_btc", new BigDecimal(data.get("recharge_btc").toString()).setScale(8, RoundingMode.FLOOR).toPlainString());//订单收益负数
//			data.put("recharge_usdt", new BigDecimal(data.get("recharge_usdt").toString()).setScale(4, RoundingMode.FLOOR).toPlainString());
        }
    }

    /**
     * 矿机利息
     */
    @Override
    public void saveGiftMoneyHandle(String partyId, double amount) {

        User user = userService.getById(partyId);
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }

        Date now = new Date();
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(now);
        userData.setUserId(user.getUserId());
        userData.setGiftMoney(amount);

        saveUserData(userData);
    }

    @Override
    public List<Map<String, UserData>> cacheByPartyIds(List<String> partyIds) {

        if(CollectionUtils.isEmpty(partyIds)) return new LinkedList<Map<String, UserData>>();

        List<Map<String, UserData>> result = new LinkedList<Map<String, UserData>>();
        for(String id:partyIds) {
            result.add(cache.get(id));
        }
        return result;
    }

    @Override
    public void saveRegister(String userId) {

        User user = userService.getById(userId);
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        UserRecom userRecom = userRecomService.findByPartyId(userId);
        if (userRecom == null) {
            return;
        }
        List<UserRecom> parents = this.userRecomService.getParents(userId);
        int loop = 4;
        for (int i = 0; i < parents.size(); i++) {
            // 找到推荐人
            User party_parent = userService.getById(parents.get(i).getUserId());
            if (Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName()) && loop > 0) {
                UserData userData_reco = new UserData();
                userData_reco.setRolename(party_parent.getRoleName());
                userData_reco.setCreateTime(new Date());
                userData_reco.setUserId(parents.get(i).getUserId());
                userData_reco.setRecoNum(1);
                save(userData_reco);
                UserDataSum userDataSum = saveBySum(parents.get(i).getUserId());
                log.info(">>>> saveRegister前 推荐前人数为{}-> {}", userService.getById(userDataSum.getUserId()).getUserName(), userDataSum.getRecoNum());
                userDataSum.setRecoNum(userDataSum.getRecoNum() + 1);
                userDataSumService.updateById(userDataSum);
                log.info(">>>> saveRegister后 推荐前人数为{}-> {}", userService.getById(userDataSum.getUserId()).getUserName(), userDataSum.getRecoNum());

                loop--;
            }
//            if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRoleName())) {
//                List<UserDataSum> userDataSums = userDataSumService.getByUserId(parents.get(i).getRecomUserId().toString());
//                UserDataSum userDataSum = new UserDataSum();
//                if (userDataSums.size() > 0) {
//                    userDataSum = userDataSums.get(0);
//                }
//                userDataSum.setRecoNum(userDataSum.getRecoNum() + 1);
//                userDataSumService.saveOrUpdate(userDataSum);
//            }
        }
    }

    /**
     * 根据partyId获取UserDataSum
     */
    public UserDataSum saveBySum(String partyId) {

        List list = userDataSumService.getByUserId(partyId);
        if (list.size() > 0) {
            return (UserDataSum) list.get(0);
        }
        UserDataSum userDataSum = new UserDataSum();
        userDataSum.setUserId(partyId);
        userDataSumService.save(userDataSum);
        return userDataSum;
    }

    @Override
    public Page userAll(Page page, Date startTime, Date endTime, List<String> userIds) {

        Page page1 = baseMapper.userAll(page, startTime, endTime,userIds);
        compute(page1.getRecords(), false);
        return page1;
    }

    @Override
    public Map sumAll(Date startTime, Date endTime, List<String> userIds) {

        Map map = baseMapper.sumAll(startTime, endTime,userIds);
        List<Map> maps = new ArrayList<>();
        maps.add(map);
        compute(maps, true);
        return maps.get(0);
    }

    /**
     * 统计的数据存在空时，不统计总额
     *
     * @param data
     * @return
     */
    private boolean dataExistNull(Map<String, Object> data) {

        if (null == data.get("recharge_withdrawal_fee")) return false;
        if (null == data.get("order_income")) return false;
        if (null == data.get("fee")) return false;
        if (null == data.get("finance_income")) return false;
        if (null == data.get("exchange_fee")) return false;
        if (null == data.get("exchange_income")) return false;
        if (null == data.get("furtures_fee")) return false;
        if (null == data.get("furtures_income")) return false;
        return true;
    }

    private void compute(List<Map<String, Object>> datas) {
        DecimalFormat df4 = new DecimalFormat("#.####");
        // 向下取整
        df4.setRoundingMode(RoundingMode.FLOOR);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(datas)) {
            return;
        }
        Double totle_income = 0d;
        Double totle_fee = 0d;
        Double business_profit = 0d;//交易盈亏
        Double fin_miner_amount = 0d;//理财 矿机 交易额
        Double fin_miner_income = 0d;//理财 矿机 收益
        for (Map<String, Object> data : datas) {
            totle_income = 0d;
            totle_fee = 0d;
            business_profit = 0d;
            fin_miner_amount = 0d;
            fin_miner_income = 0d;
            if (null == data.get("reco_num")) {
                data.put("reco_num", 0);
            }
//			if(null!=data.get("rolename")) {
//				data.put("rolename", Constants.ROLE_MAP.get(data.get("rolename").toString()));
//			}
            if (null != data.get("order_income"))
                data.put("order_income", df4.format(Arith.sub(0, new Double(data.get("order_income").toString()))));//订单收益负数
            if (null != data.get("finance_income"))
                data.put("finance_income", df4.format(Arith.sub(0, new Double(data.get("finance_income").toString()))));//理财收益负数
            if (null != data.get("exchange_income"))
//			data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));//币币收益负数
                data.put("exchange_income", 0);//币币收益负数
            if (null != data.get("furtures_income"))
                data.put("furtures_income", df4.format(Arith.sub(0, new Double(data.get("furtures_income").toString()))));//交割收益负数
            if (null != data.get("miner_income"))
                data.put("miner_income", df4.format(Arith.sub(0, new Double(data.get("miner_income").toString()))));// 矿机收益负数
            if (null != data.get("exchange_lever_order_income"))
                data.put("exchange_lever_order_income",  df4.format(Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString()))));// 币币收益负数
            if (!dataExistNull(data)) continue;
            totle_income = Arith.add(totle_income, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("order_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("finance_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("exchange_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(0));
//			totle_income = Arith.add(totle_income,new Double(data.get("exchange_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("miner_income").toString()));
            data.put("totle_income",  df4.format(totle_income));
            totle_fee = Arith.add(totle_fee, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("furtures_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_lever_fee").toString()));
            data.put("totle_fee",  df4.format(totle_fee));
            business_profit = Arith.add(business_profit, new Double(data.get("order_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("furtures_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("business_profit",  df4.format(business_profit));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("finance_amount").toString()));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("miner_amount").toString()));
            data.put("fin_miner_amount", df4.format(fin_miner_amount));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("finance_income").toString()));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("miner_income").toString()));
            data.put("fin_miner_income", df4.format(fin_miner_income));
        }
    }

    private Map<String, BigDecimal> buildData(List<UserData> list) {
        Map<String, BigDecimal> map = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal todayIncome = BigDecimal.ZERO;
        String today = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
        for (UserData data : list) {
            totalIncome = totalIncome
                    // 充提手续费
                    .add(BigDecimal.valueOf(data.getRechargeWithdrawalFee()))
                    // 永续合约收益
                    .add(BigDecimal.valueOf(data.getOrderIncome()))
                    // 永续合约手续费
                    .add(BigDecimal.valueOf(data.getFee()))
                    // 理财收益
                    .add(BigDecimal.valueOf(data.getFinanceIncome()))
                    // 币币手续费
                    .add(BigDecimal.valueOf(data.getExchangeFee()))
                    // 交割合约手续费
                    .add(BigDecimal.valueOf(data.getFurturesFee()))
                    // 交割合约收益
                    .add(BigDecimal.valueOf(data.getFurturesIncome()))
                    // 矿机收益
                    .add(BigDecimal.valueOf(data.getMinerIncome()));
            if (today.equals(DateUtils.format(data.getCreateTime(), DateUtils.DF_yyyyMMdd))) {
                todayIncome = todayIncome
                        // 充提手续费
                        .add(BigDecimal.valueOf(data.getRechargeWithdrawalFee()))
                        // 永续合约收益
                        .add(BigDecimal.valueOf(data.getOrderIncome()))
                        // 永续合约手续费
                        .add(BigDecimal.valueOf(data.getFee()))
                        // 理财收益
                        .add(BigDecimal.valueOf(data.getFinanceIncome()))
                        // 币币手续费
                        .add(BigDecimal.valueOf(data.getExchangeFee()))
                        // 交割合约手续费
                        .add(BigDecimal.valueOf(data.getFurturesFee()))
                        // 交割合约收益
                        .add(BigDecimal.valueOf(data.getFurturesIncome()))
                        // 矿机收益
                        .add(BigDecimal.valueOf(data.getMinerIncome()));
            }
        }
        map.put("totalIncome", totalIncome.setScale(2, RoundingMode.HALF_DOWN));
        map.put("todayIncome", todayIncome.setScale(2, RoundingMode.HALF_DOWN));
        return map;
    }

    /**
     * 计算 统计时
     *
     * @param datas
     * @param isSum
     */
    private void compute(List<Map> datas, boolean isSum) {

        if (org.apache.commons.collections.CollectionUtils.isEmpty(datas)) return;
        Double totle_income = 0d;
        Double totle_fee = 0d;
        Double business_profit = 0d;//交易盈亏
        Double fin_miner_amount = 0d;//理财 矿机 交易额
        Double fin_miner_income = 0d;//理财 矿机 收益
        for (Map<String, Object> data : datas) {
            totle_income = 0d;
            totle_fee = 0d;
            business_profit = 0d;
            fin_miner_amount = 0d;
            fin_miner_income = 0d;
            if (null != data.get("order_income"))
                data.put("order_income", new BigDecimal(Arith.sub(0, new Double(data.get("order_income").toString()))) .setScale(8, RoundingMode.FLOOR).toPlainString());//订单收益负数
            if (null != data.get("finance_income"))
                data.put("finance_income",new BigDecimal( Arith.sub(0, new Double(data.get("finance_income").toString()))) .setScale(8, RoundingMode.FLOOR).toPlainString());//理财收益负数
            if (null != data.get("exchange_income"))
//			data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));//币币收益负数
                data.put("exchange_income", 0);//币币收益负数
            if (null != data.get("furtures_income"))
                data.put("furtures_income", new BigDecimal(Arith.sub(0, new Double(data.get("furtures_income").toString()))) .setScale(8, RoundingMode.FLOOR).toPlainString());//交割收益负数
            if (null != data.get("miner_income"))
                data.put("miner_income", new BigDecimal(Arith.sub(0, new Double(data.get("miner_income").toString()))).setScale(8, RoundingMode.FLOOR).toPlainString());// 矿机收益负数
            if (null != data.get("exchange_lever_order_income"))
                data.put("exchange_lever_order_income",new BigDecimal( Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString()))).setScale(8, RoundingMode.FLOOR).toPlainString());// 币币收益负数
            if (!dataExistNull(data)) continue;
            totle_income = Arith.add(totle_income, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("order_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("finance_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("exchange_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(0));
//			totle_income = Arith.add(totle_income,new Double(data.get("exchange_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("miner_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("totle_income", new BigDecimal(totle_income).setScale(8, RoundingMode.FLOOR).toPlainString() );
            totle_fee = Arith.add(totle_fee, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("furtures_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_lever_fee").toString()));
            data.put("totle_fee",   new BigDecimal(totle_fee).setScale(8, RoundingMode.FLOOR).toPlainString());
            business_profit = Arith.add(business_profit, new Double(data.get("order_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("furtures_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("business_profit", new BigDecimal(business_profit).setScale(8, RoundingMode.FLOOR).toPlainString());
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("finance_amount").toString()));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("miner_amount").toString()));
            data.put("fin_miner_amount", new BigDecimal(fin_miner_amount).setScale(8, RoundingMode.FLOOR).toPlainString());
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("finance_income").toString()));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("miner_income").toString()));
            data.put("fin_miner_income",new BigDecimal( fin_miner_income).setScale(8, RoundingMode.FLOOR).toPlainString());
            data.put("recharge_btc", new BigDecimal(data.get("recharge_btc").toString()).setScale(8, RoundingMode.FLOOR).toPlainString());//订单收益负数
        }
    }

    @Override
    public void saveUserData(UserData entity) {
        try {
            UserData db = findBydate(entity.getUserId(), entity.getCreateTime());
            if (db != null) {
                db.setRechargeDapp(Arith.add(db.getRechargeDapp(), entity.getRechargeDapp()));
                db.setWithdrawDapp(Arith.add(db.getWithdrawDapp(), entity.getWithdrawDapp()));
                db.setRecharge(Arith.add(db.getRecharge(), entity.getRecharge()));
                db.setRechargeEth(Arith.add(db.getRechargeEth(), entity.getRechargeEth()));
                db.setRechargeUsdt(Arith.add(db.getRechargeUsdt(), entity.getRechargeUsdt()));
                db.setRechargeBtc(Arith.add(db.getRechargeBtc(), entity.getRechargeBtc()));
                db.setRechargeHt(Arith.add(db.getRechargeHt(), entity.getRechargeHt()));
                db.setRechargeLtc(Arith.add(db.getRechargeLtc(), entity.getRechargeLtc()));
                // 充值返佣
                db.setRechargeRecom(Arith.add(db.getRechargeRecom(), entity.getRechargeRecom()));
                db.setWithdrawAll(Arith.add(db.getWithdrawAll(), entity.getWithdrawAll()));
                db.setWithdraw(Arith.add(db.getWithdraw(), entity.getWithdraw()));
                db.setWithdrawEth(Arith.add(db.getWithdrawEth(), entity.getWithdrawEth()));
                db.setWithdrawBtc(Arith.add(db.getWithdrawBtc(), entity.getWithdrawBtc()));
                db.setAmount(Arith.add(db.getAmount(), entity.getAmount()));
                db.setFee(Arith.add(db.getFee(), entity.getFee()));
                db.setOrderIncome(Arith.add(db.getOrderIncome(), entity.getOrderIncome()));
                db.setFinanceAmount(Arith.add(db.getFinanceAmount(), entity.getFinanceAmount()));
                db.setFinanceIncome(Arith.add(db.getFinanceIncome(), entity.getFinanceIncome()));
                db.setExchangeAmount(Arith.add(db.getExchangeAmount(), entity.getExchangeAmount()));
                db.setExchangeFee(Arith.add(db.getExchangeFee(), entity.getExchangeFee()));
                db.setExchangeIncome(Arith.add(db.getExchangeIncome(), entity.getExchangeIncome()));
                db.setCoinIncome(Arith.add(db.getCoinIncome(), entity.getCoinIncome()));
                db.setFurturesAmount(Arith.add(db.getFurturesAmount(), entity.getFurturesAmount()));
                db.setFurturesFee(Arith.add(db.getFurturesFee(), entity.getFurturesFee()));
                db.setFurturesIncome(Arith.add(db.getFurturesIncome(), entity.getFurturesIncome()));
                db.setRecoNum(db.getRecoNum() + entity.getRecoNum());
                db.setRechargeWithdrawalFee(
                        Arith.add(db.getRechargeWithdrawalFee(), entity.getRechargeWithdrawalFee()));
                db.setGiftMoney(Arith.add(db.getGiftMoney(), entity.getGiftMoney()));
                db.setMinerAmount(Arith.add(db.getMinerAmount(), entity.getMinerAmount()));
                db.setMinerIncome(Arith.add(db.getMinerIncome(), entity.getMinerIncome()));
                // 质押2.0
                db.setGalaxyAmount(Arith.add(db.getGalaxyAmount(), entity.getGalaxyAmount()));
                db.setGalaxyIncome(Arith.add(db.getGalaxyIncome(), entity.getGalaxyIncome()));
                db.setThirdRechargeAmount(Arith.add(db.getThirdRechargeAmount(), entity.getThirdRechargeAmount()));
                db.setHoldingMoney(Arith.add(db.getHoldingMoney(), entity.getHoldingMoney()));
                db.setTransferInMoney(Arith.add(db.getTransferInMoney(), entity.getTransferInMoney()));
                db.setTransferOutMoney(Arith.add(db.getTransferOutMoney(), entity.getTransferOutMoney()));
                db.setExchangeLeverAmount(Arith.add(db.getExchangeLeverAmount(), entity.getExchangeLeverAmount()));
                db.setExchangeLeverFee(Arith.add(db.getExchangeLeverFee(), entity.getExchangeLeverFee()));
                db.setExchangeLeverOrderIncome(Arith.add(db.getExchangeLeverOrderIncome(), entity.getExchangeLeverOrderIncome()));
                updateById(db);
                setCache(db);

//                log.info("saveUserData 00 => "+ JSONObject.toJSONString(db));
            } else {
                save(entity);
                setCache(entity);


//                log.info("saveUserData 00 => "+ JSONObject.toJSONString(entity));
            }
        } catch (Exception e) {
            log.error("saveUserData 异常", e);
        }
    }

    public void setCache(UserData entity) {
        Map<String, UserData> map_party = cache.get(entity.getUserId());
        if (map_party == null) {
            map_party = new ConcurrentHashMap<String, UserData>();
        }
        if(null != entity.getCreateTime()) {
            map_party.put(DateUtils.format(entity.getCreateTime(), DateUtils.DEFAULT_DATE_FORMAT), entity);
        }
        cache.put(entity.getUserId(), map_party);
    }

    public void put(String userId, Map<String, UserData> map_party) {
        redisTemplate.opsForValue().set(REDIS_KEY + userId, map_party);
    }

    @Override
    public List<Map<String, UserData>> findByPartyIds(List<String> children) {
        List<UserData> users = list(Wrappers.<UserData>query().lambda().in(UserData::getUserId, children));
        List<Map<String, UserData>> mapList = new ArrayList<>();
        for (UserData userData : users) {
            Map<String, UserData> map = new HashMap<>();
            map.put(userData.getUserId(), userData);
            mapList.add(map);
        }
        return mapList;
    }




    /**
     * 资金盘定制化需求，等盘口下架可以删除
     */

    @Override
    public List<Map<String, Object>> getChildrenLevelPagedForGalaxy(int pageNo, int pageSize, String partyId, Integer levelNum) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ChildrenLever children = cacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level = new ArrayList<String>();
        if (levelNum == 1) {
            level = children.getLever1();
        }
        if (levelNum == 2) {
            level = children.getLever2();
        }
        if (levelNum == 3) {
            level = children.getLever3();
        }
//		if (levelNum == 4) {
//			level = children.getLever4();
//		}
        if (level == null || level.isEmpty()) {
            return list;
        }
        Page<User> page = new Page(pageNo, pageSize);
        userService.page(page, Wrappers.<User>query().lambda().in(User::getUserId, level));
        for (int i = 0; i < page.getRecords().size(); i++) {
            Map<String, Object> element_map = new HashMap<String, Object>();
            User user = page.getRecords().get(i);
            element_map.put("username", user.getUserName());
            element_map.put("partyId", user.getUserId());


            String childrenPartyId = element_map.get("partyId").toString();
            ChildrenLever childrenLever = cacheChildrenLever4(childrenPartyId);
            // 获取子代partyId
            List<String> level_children = new ArrayList<String>();
            if (levelNum == 1) {
                level_children = childrenLever.getLever1();
            }
            if (levelNum == 2) {
                level_children = childrenLever.getLever2();
            }
            if (levelNum == 3) {
                level_children = childrenLever.getLever3();
            }
//			if (levelNum == 4) {
//				level_children = childrenLever.getLever4();
//			}
            element_map.put("reco_sum", level_children.size());
            // list里面的总业绩
            Map<String, UserData> map = cacheByPartyId(childrenPartyId);
            double sum = 0;
            double sumRechargeRecom = 0;
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    sum += userData.getRecharge();
                    sumRechargeRecom += userData.getRechargeRecom();
                }
            }
            element_map.put("recharge_sum", sum);
            element_map.put("recharge_recom",sumRechargeRecom);
            list.add(element_map);
        }
        return list;
    }

    @Override
    public ChildrenLever cacheChildrenLever4(String partyId) {

        ChildrenLever childrenLever = new ChildrenLever();
        /**
         * lever1
         */
        List<UserRecom> userrecom_lever1 = userRecomService.findRecoms(partyId);
        for (int i = 0; i < userrecom_lever1.size(); i++) {
            childrenLever.getLever1().add(userrecom_lever1.get(i).getRecomUserId().toString());
        }
        /**
         * lever2
         */
        if (childrenLever.getLever1().size() == 0) {
            return childrenLever;
        }
        for (int i = 0; i < childrenLever.getLever1().size(); i++) {
            List<UserRecom> userrecom_lever2 = userRecomService.findRecoms(childrenLever.getLever1().get(i));
            for (int j = 0; j < userrecom_lever2.size(); j++) {
                childrenLever.getLever2().add(userrecom_lever2.get(j).getRecomUserId().toString());
            }
        }
        /**
         * lever3
         */
        if (childrenLever.getLever2().size() == 0) {
            return childrenLever;
        }
        for (int i = 0; i < childrenLever.getLever2().size(); i++) {
            List<UserRecom> userrecom_lever3 = userRecomService.findRecoms(childrenLever.getLever2().get(i));
            for (int j = 0; j < userrecom_lever3.size(); j++) {
                childrenLever.getLever3().add(userrecom_lever3.get(j).getRecomUserId().toString());
            }
        }
//		/**
//		 * lever4
//		 */
//		if (childrenLever.getLever3().size() == 0) {
//			return childrenLever;
//		}
//		for (int i = 0; i < childrenLever.getLever3().size(); i++) {
//			List<UserRecom> userrecom_lever4 = userRecomService.findRecoms(childrenLever.getLever3().get(i));
//			for (int j = 0; j < userrecom_lever4.size(); j++) {
//				childrenLever.getLever4().add(userrecom_lever4.get(j).getPartyId().toString());
//			}
//
//		}
        return childrenLever;
    }

    @Override
    public void saveWithdrawHandle(String partyId, double amount, double amount_fee, String symbol) {

        System.out.println("saveWithdrawHandle -> partyId:" + partyId);
        User user = userService.getById(partyId);
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(partyId);
        if (StringUtils.isEmpty(symbol) || "usdt".equals(symbol)) {
            userData.setWithdraw(amount);
            userData.setRechargeWithdrawalFee(amount_fee);
            userData.setWithdrawAll(amount);
        } else {
            // TODO: 2023/4/21  = this.dataService.realtime(symbol);
            List<Realtime> realtime_list = dataService.realtime(symbol);
            Realtime realtime = null;
            if (realtime_list.size() > 0) {
                realtime = realtime_list.get(0);
            } else {
                throw new YamiShopBindException("系统错误，请稍后重试");
            }
            if ("btc".equals(symbol)) {
                userData.setRechargeWithdrawalFee(Arith.mul(amount_fee, realtime.getClose()));
                userData.setWithdrawBtc(amount);
                userData.setWithdrawAll(Arith.mul(amount, realtime.getClose()));
            }
            if ("eth".equals(symbol)) {
                userData.setRechargeWithdrawalFee(Arith.mul(amount_fee, realtime.getClose()));
                userData.setWithdrawEth(amount);
                userData.setWithdrawAll(Arith.mul(amount, realtime.getClose()));
            }
        }
        saveUserData(userData);
    }

    @Override
    public void saveRechargeHandle(String partyId, double amount, String symbol) {

        User party = userService.getById(partyId);
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(party.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        UserData userData = new UserData();
        userData.setRolename(party.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(partyId);
        if ("usdt".equals(symbol)) {
            userData.setRecharge(amount);
            userData.setRechargeUsdt(amount);
        } else {
            List<Realtime> realtime_list = this.dataService.realtime(symbol);
            Realtime realtime = null;
            if (realtime_list.size() > 0) {
                realtime = realtime_list.get(0);
            } else {
                log.info("symbol :{} 获取失败!",symbol);
                throw new YamiShopBindException("系统错误，请稍后重试");
            }
            if ("btc".equals(symbol)) {
                userData.setRecharge(Arith.mul(amount, realtime.getClose()));
                userData.setRechargeBtc(amount);
            }
            if ("eth".equals(symbol)) {
                userData.setRecharge(Arith.mul(amount, realtime.getClose()));
                userData.setRechargeEth(amount);
            }
            if ("ht".equals(symbol)) {
                userData.setRecharge(Arith.mul(amount, realtime.getClose()));
                userData.setRechargeHt(amount);
            }
            if ("ltc".equals(symbol)) {
                userData.setRecharge(Arith.mul(amount, realtime.getClose()));
                userData.setRechargeLtc(amount);
            }

            if ("usdc".equals(symbol)) {
                userData.setRecharge(Arith.mul(amount, realtime.getClose()));
                userData.setRechargeUsdc(amount);
            }
        }
        saveUserData(userData);
    }

    @Override
    public void saveClose(ContractOrder order) {
        ApplicationUtil.getBean(ContractOrderService.class).wrapProfit(order);

        User user = userService.getById(order.getPartyId());
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId());
        userData.setAmount(order.getDepositOpen().doubleValue());
        if (order.getAmountClose() == null) {
            order.setAmountClose(BigDecimal.ZERO);
        }
        if (order.getAmountClose().compareTo(BigDecimal.ZERO) < 0) {
            order.setAmountClose(BigDecimal.ZERO);
        }
        userData.setFee(order.getFee().doubleValue());
        // 直接用profit保证一直
        userData.setOrderIncome(order.getProfit().doubleValue());
        Item bySymbol = itemService.findBySymbol(order.getSymbol());
        if(bySymbol != null){
            userData.setItemType(bySymbol.getType());
        }
        saveUserData(userData);

    }

    //基础反水
    @Transactional
    public void saveBackwaterBasicTrading(String userId,double exchangeAmount,double fee) {
        log.info(userId+"========>"+exchangeAmount+"======"+fee);

        if (fee==0){
            return;
        }
        Syspara syspara= sysparaService.find("backwater_basic_tradin");
        if (syspara==null){
            return;
        }
        boolean flag=  Boolean.valueOf(syspara.getSvalue());
        if (!flag){
            return;
        }
        long sumExchangeAmount =sumExchangeAmount(userId);
        double commission=0;
        //对于月交易量超过500000美元的用户，返还15%。
        if (sumExchangeAmount>500000){
            commission=fee*0.15;
        }
        //对于月交易量超过1000000美元的用户，返还20%
        else if (sumExchangeAmount>1000000){
            commission=fee*0.2;
        }
        else {//所有用户对每笔交易的手续费，返还10%作为返佣
            commission=fee*0.1;
        }
        if (commission>0){
            Wallet wallet =walletService.saveWalletByPartyId(userId);
            double   amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), commission);
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.WALLET_USDT);
            moneylog.setAmountBefore(new BigDecimal(amountBefore));
            moneylog.setAmount(new BigDecimal(commission));
            moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, commission)));
            moneylog.setLog("基础交易反水usdt"+commission);
            moneylog.setUserId(userId);
            moneylog.setWalletType("usdt");
            moneylog.setContentType(Constants.MONEYLOG_WAllET_EXCHANGE_COMMISSION);
            moneyLogService.save(moneylog);

            User user = userService.findByUserId(userId);
            UserData userData = new UserData();
            userData.setRolename(user.getRoleName());
            userData.setCreateTime(new Date());
            userData.setUserId(userId);
            saveUserData(userData);
        }
    }

    @Override
    public void saveSell(ExchangeApplyOrder order) {

        User user = userService.getById(order.getPartyId());
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
        Realtime realtime = null;
        if (realtime_list.size() > 0) {
            realtime = realtime_list.get(0);
        } else {
            throw new YamiShopBindException("系统错误，请稍后重试");
        }
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId());
        userData.setExchangeAmount(Arith.mul(realtime.getClose(), order.getVolume()));
        userData.setExchangeFee(Arith.mul(realtime.getClose(), order.getFee()));
        userData.setExchangeIncome(0);
        saveUserData(userData);
        // ICE 问盘口 未回复 暂时不上
        // saveBackwaterBasicTrading(order.getPartyId(), userData.getExchangeAmount(),userData.getExchangeFee());
    }





    @Override
    public void saveBuy(ExchangeApplyOrder order) {

        User user = userService.getById(order.getPartyId());
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(user.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(user.getRoleName())) {
            guest = true;
        }
        if (guest) {
            return;
        }
        List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
        Realtime realtime = null;
        if (realtime_list.size() > 0) {
            realtime = realtime_list.get(0);
        } else {
            throw new YamiShopBindException("系统错误，请稍后重试");
        }
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId());
        userData.setExchangeAmount(order.getVolume());
        userData.setExchangeFee(order.getFee());
        saveUserData(userData);
        // saveBackwaterBasicTrading(order.getPartyId(), userData.getExchangeAmount(),userData.getExchangeFee());
    }

    /**
     * 交割合约平仓
     */
    @Override
    public void saveFuturesClose(FuturesOrder order) {

        User user = userService.getById(order.getPartyId());
        String roleName = user.getRoleName();
        boolean guest = false;
        if (Constants.SECURITY_ROLE_GUEST.equals(roleName) || Constants.SECURITY_ROLE_TEST.equals(roleName)) {
            guest = true;
        }
        if (guest) {
            return;
        }
        User party = userService.getById(order.getPartyId());
        UserData userData = new UserData();
        userData.setRolename(party.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId());
        userData.setFurturesAmount(order.getVolume());
        userData.setFurturesFee(order.getFee());
        userData.setFurturesIncome(order.getProfit());
        saveUserData(userData);
//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setFurtures_amount(order.getVolume());
//				userData_reco.setFurtures_fee(order.getFee());
//				userData_reco.setFurtures_income(order.getProfit());
//				save(userData_reco);
//			}
    }

    /**
	 * 理财产品平仓
	 */
	@Override
    public void saveSellFinance(FinanceOrder order) {
        User user = this.secUserService.findUserByUserCode(order.getFinanceId());
//		SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
//		user.getRoles();
		boolean guest = false;

//		for (Role role : user.getRoles()) {
//			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())||Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
//				guest = true;
//			}
//		}
		if (guest) {
			return;
		}

		User party = userService.cacheUserBy(order.getPartyId().toString());
		UserData userData = new UserData();

		userData.setRolename(party.getRoleName());
		userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId().toString());
		userData.setFinanceAmount(order.getAmount());
		userData.setFinanceIncome(order.getProfit());
		save(userData);

//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setFinance_amount(order.getAmount());
//				userData_reco.setFinance_income(order.getProfit());
//				save(userData_reco);
//			}
//		}
	}

    /**
     * 矿机买入
     */
    @Override
    public void saveMinerBuy(MinerOrder order) {

        User user = this.secUserService.cacheUserBy(order.getPartyId().toString());
//        user.getRoles();
        boolean guest = false;
//        for (Role role : user.getRoles()) {
//            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())||Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
//                guest = true;
//            }
//        }
        if (guest) {
            return;
        }

        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId().toString());
        userData.setMinerAmount(order.getAmount());
        save(userData);
    }

    /**
     * 矿机赎回
     */
    @Override
    public void saveMinerClose(MinerOrder order) {
        User user = this.secUserService.cacheUserBy(order.getPartyId().toString());
//        user.getRoles();
        boolean guest = false;
//        for (Role role : user.getRoles()) {
//            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())||Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
//                guest = true;
//            }
//        }
        if (guest) {
            return;
        }

//        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(order.getPartyId().toString());
        userData.setMinerAmount(Arith.sub(0, order.getAmount()));
        save(userData);
    }

    /**
     * 矿机利息
     *
     * @param partyId 获利人
     * @param profit  利息
     */
    @Override
    public void saveMinerProfit(String partyId, double profit) {
        User user = this.secUserService.cacheUserBy(partyId);
//        user.getRoles();
        boolean guest = false;
//        for (Role role : user.getRoles()) {
//            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
//                guest = true;
//            }
//        }
        if (guest) {
            return;
        }

//        Party party = partyService.cachePartyBy(partyId, true);
        UserData userData = new UserData();
        userData.setRolename(user.getRoleName());
        userData.setCreateTime(new Date());
        userData.setUserId(partyId);
        userData.setMinerIncome(profit);
        save(userData);
    }

    @Override
    public List<UserDataWithdrawLimitDto> withdrawLimit(Date startTime, Date endTime, List<String> userIds) {
        return baseMapper.withdrawLimit(startTime, endTime, userIds);
    }

    @Override
    public long sumExchangeAmount(String userId) {
        return baseMapper.sumExchangeAmount(userId);
    }

    @Override
    public Map<String, String> getPromoteData(String partyId, Map<String, String> data, Date startTime, Date endTime) {
        ChildrenLever children = cacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level1 = new ArrayList<String>();
        List<String> level2 = new ArrayList<String>();
        List<String> level3 = new ArrayList<String>();
        level1 = children.getLever1();
        level2 = children.getLever2();
        level3 = children.getLever3();

        Map<String, String> peoples = new HashMap<>();
        double rechangeAmount = 0;
        for (String value1 : level1) {
            Map<String, UserData> map = cacheByPartyId(value1);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) continue;
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getUserId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getUserId()), "");
                    }
                }
            }
        }

        for (String value2 : level2) {
            Map<String, UserData> map = cacheByPartyId(value2);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) continue;
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getUserId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getUserId()), "");
                    }
                }
            }
        }

        for (String value3 : level3) {
            Map<String, UserData> map = cacheByPartyId(value3);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) continue;
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getUserId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getUserId()), "");
                    }
                }
            }
        }

        data.put("peopleNum", String.valueOf(peoples.size()));
        data.put("rechangeAmount", String.valueOf(rechangeAmount));
        return data;
    }
}
