package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.constans.UserConstants;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.syspara.dto.SysparasDto;
import com.yami.trading.bean.user.dto.AgentUserDto;
import com.yami.trading.bean.user.dto.UserDataDto;
import com.yami.trading.bean.user.dto.UserDataWithdrawLimitDto;
import com.yami.trading.bean.user.dto.UserDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.dao.user.UserMapper;
import com.yami.trading.service.IdentifyingCodeTimeWindowService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.OnlineUserService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserRecomService userRecomService;
    @Autowired
    WalletService walletService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    LogService logService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    @Lazy
    private UserDataService userDataService;


    @Autowired
    WalletLogService walletLogService;

    @Autowired
    UserOldService tzUserOldService;

    /**
     * UserCode生成并发锁
     */
    private static final String USER_CODE_LOCK = "USER_CODE_LOCK";

    /**
     * 图片验证key，保证前后一致性
     */
    private Map<String, String> imageCodeCache = new ConcurrentHashMap<String, String>();
    @Autowired
    OnlineUserService onlineUserService;
    @Autowired
    WalletExtendService walletExtendService;
    @Autowired
    IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
    @Autowired(required = false)
    @Qualifier("dataService")
    private DataService dataService;

    @Override
    public boolean checkLoginSafeword(User user, String loginSafeword) {
        return passwordEncoder.matches(loginSafeword, user.getSafePassword());
    }

    @Override
    public Page<UserDto> listUser(Page page, List<String> roleNames, String userCode, String userName,
        String userMail, String userMobile, List<String> checkedList) {

        Page<UserDto> userDtoPage = baseMapper.listUser(page, roleNames, userCode, userName, userMail, userMobile, checkedList);

        // 提现限制流水开启后，提现判断用的用户当前流水是使用UserData表的当日流水1还是使用Party表里的用户当前流水2
        String withdraw_limit_open_use_type = this.sysparaService.find("withdraw_limit_open_use_type").getSvalue();

        // 当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加
        String withdraw_limit_contract_or = this.sysparaService.find("withdraw_limit_contract_or").getSvalue();

        if ("1".equals(withdraw_limit_open_use_type)) {
            List<UserDto> records = userDtoPage.getRecords();
            if (CollectionUtil.isNotEmpty(records)) {

                records.forEach(v -> v.setWithdrawLimitNowAmount(new BigDecimal(0)));

                List<String> userIds = records.stream().map(UserDto::getUserId).collect(Collectors.toList());

                List<UserDataWithdrawLimitDto> userDtos = userDataService.withdrawLimit(DateUtil.minDate(new Date()), DateUtil.maxDate(new Date()), userIds);

                if (CollectionUtil.isNotEmpty(userDtos)) {

                    Map<String, List<UserDataWithdrawLimitDto>> limitMap = userDtos.stream().collect(Collectors.groupingBy(UserDataWithdrawLimitDto::getUserId));
                    for (UserDto data : records) {

                        List<UserDataWithdrawLimitDto> userDataWithdrawLimitDtos = limitMap.get(data.getUserId());
                        if (CollectionUtil.isNotEmpty(userDataWithdrawLimitDtos)) {
                            UserDataWithdrawLimitDto userDataWithdrawLimitDto = userDataWithdrawLimitDtos.get(0);
                            double amount1 = Arith.add(userDataWithdrawLimitDto.getMinerAmount(), userDataWithdrawLimitDto.getFinanceAmount());
                            double amount2 = userDataWithdrawLimitDto.getFurturesAmount();
                            if ("1".equals(withdraw_limit_contract_or)) {
                                amount2 = Arith.add(amount2, userDataWithdrawLimitDto.getAmount());
                            }
                            data.setWithdrawLimitNowAmount(new BigDecimal(Double.toString(Arith.add(amount1, amount2))));
                        }
                    }
                }
            }
            userDtoPage.setRecords(records);
        }
        return userDtoPage;
    }

    public static void main(String[] args) {
        String ff = new BCryptPasswordEncoder().encode("123456");
        log.info(ff);
    }

    @Override
    public Page<UserDataDto> listUserAndRecom(Page page, List<String> roleNames, String userCode, String userName, String lastIp, List<String> checkedList, String userMail, String userMobile) {
        return baseMapper.listUserAndRecom(page, roleNames, userCode,
                userName, lastIp, checkedList, userMail, userMobile);
    }

    @Override
    public boolean checkLoginSafeword(String userId, String loginSafeword) {
        User user = getById(userId);
        if (user == null) {
            throw new YamiShopBindException("用户不存在!");
        }
        return checkLoginSafeword(user, loginSafeword);
    }

    @Override
    public void updateAgent(String userId, boolean operaAuthority, boolean loginAuthority) {
        String roleName = operaAuthority ? Constants.SECURITY_ROLE_AGENT : Constants.SECURITY_ROLE_AGENTLOW;
        User user = getById(userId);
        user.setStatus(loginAuthority ? 1 : 0);
        user.setRealName(roleName);
        updateById(user);
    }

    @Override
    public User cacheUserBy(String userId) {
        String key= "user:"+userId;
        User user=  RedisUtil.get(key);
        if (user==null){
            user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
            RedisUtil.set(key,user,5*60);
        }
        return user;
    }

    @Override
    public void saveResetCreateOrder(String partyId, double money_revise, String safeword, String operator_name, String reset_type, String ip, String coin_type) {

        if (money_revise == 0 || coin_type == "") {
            return;
        }

        if ("usdt".equals(coin_type)) {
            // 交易所修改usdt

            User party = getById(partyId);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

            Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
            double amount_before = wallet.getMoney().doubleValue();
            if (Arith.add(money_revise, wallet.getMoney().doubleValue()) < 0.0D) {
                throw new BusinessException("操作失败！修正后账户余额小于0。");
            }

//            User sec =findByUserName(operator_name);
//            String sysSafeword = sec.getSafeword();
//            String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
//            if (!safeword_md5.equals(sysSafeword)) {
//                throw new BusinessException("资金密码错误");
//            }

            // 更新金额
            this.walletService.update(wallet.getUserId().toString(), money_revise);

            // 账变日志
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amount_before));
            moneyLog.setAmount(new BigDecimal(money_revise));
            moneyLog.setAmountAfter(new BigDecimal(Arith.add(wallet.getMoney().doubleValue(), money_revise)));
            moneyLog.setUserId(partyId);
            moneyLog.setWalletType(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());

            // 钱包日志
            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
            walletLog.setPartyId(partyId);
            walletLog.setOrderNo("");
            walletLog.setStatus(1);
            walletLog.setAmount(money_revise);
            walletLog.setWallettype("USDT");
            walletLogService.save(walletLog);

            // 操作日志
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(party.getUserName());
            log.setOperator(operator_name);
            log.setUserId(partyId);
            // change----添加赠送金额
            if ("change".equals(reset_type)) {

                // 只有正式用户才需要记录报表
                if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
                    this.userDataService.saveGiftMoneyHandle(partyId, money_revise);
                }

                log.setLog("ip:" + ip + ",手动添加赠送金额。修改币种[usdt]，修改数量[" + money_revise + "]");
                moneyLog.setLog("手动添加赠送金额");

                this.checkGiftUserLine(party);
            }

            // recharge--添加充值金额
            if ("recharge".equals(reset_type)) {

                // 只有正式用户才需要记录报表
                if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
                    this.userDataService.saveRechargeHandle(partyId, money_revise, "usdt");
                }

                log.setLog("ip:" + ip + ",手动添加充值金额。修改币种[usdt]，修改数量[" + money_revise + "]");
                moneyLog.setLog("手动添加充值金额");
            }

            this.moneyLogService.save(moneyLog);
            logService.save(log);
            if (party.getWithdrawLimitAmount() == null) {
                party.setWithdrawLimitAmount(new BigDecimal(0));
            }
            // 充值到账后给他增加提现流水限制金额
            party.setWithdrawLimitAmount(new BigDecimal(Arith.add(party.getWithdrawLimitAmount().doubleValue(), money_revise)));
            updateById(party);

        } else {
            // 交易所修改btc、eth；DAPP修改质押账户（USDT）【USDT_DAPP】；DAPP演示用户修改DAPP余额【USDT_USER】；

            User party = getById(partyId);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

            WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coin_type);
            double amount_before = walletExtend.getAmount();
            if (Arith.add(money_revise, walletExtend.getAmount()) < 0.0D) {
                throw new BusinessException("操作失败！修正后账户余额小于0。");
            }


            this.walletService.updateExtend(walletExtend.getPartyId().toString(), coin_type, money_revise);

            // 账变日志
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amount_before));
            moneyLog.setAmount(new BigDecimal(money_revise));
            moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getAmount(), money_revise)));
            moneyLog.setUserId(partyId);
            moneyLog.setWalletType(coin_type.toUpperCase());
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());

            // 钱包日志
            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
            walletLog.setPartyId(partyId);
            walletLog.setOrderNo("");
            walletLog.setStatus(1);
            walletLog.setAmount(money_revise);
            walletLog.setWallettype(coin_type.toUpperCase());
            walletLogService.save(walletLog);

            // 操作日志
//			Party party = this.partyService.cachePartyBy(partyId, true);
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(party.getUserName());
            log.setOperator(operator_name);

            // recharge--添加充值金额
            if ("recharge".equals(reset_type)) {
                String coin_str = "";
                if ("USDT_USER".equals(coin_type)) {
                    coin_str = "[用户钱包USDT映射]";
                }

                // 只有正式用户才需要记录报表
                if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
                    this.userDataService.saveRechargeHandle(partyId, money_revise, coin_type);
                }

                log.setLog("ip:" + ip + ",手动添加充值金额。修改币种[" + coin_type + "]" + coin_str + "，修改数量[" + money_revise + "]");
                moneyLog.setLog("手动添加充值金额");
            }

            moneyLogService.save(moneyLog);
            logService.save(log);
        }
    }

    /**
     * 检验是否达到赠送用户的达标线
     *
     * @param party
     */
    public void checkGiftUserLine(User party) {
        if (!party.isGiftMoneyFlag()) {
            party.setGiftMoneyFlag(true);
        }
        if (party.isGiftUser()) {//已经是赠送用户则无需判定
            return;
        }
        Map<String, UserData> datas = userDataService.cacheByPartyId(party.getUserId().toString());
        Double giftUserLine = sysparaService.find("gift_user_line").getDouble();
        String gift_user_date_start = sysparaService.find("gift_user_date_start").getSvalue();
        double giftMoney = giftMoney(datas, gift_user_date_start, null);
        if (giftMoney >= giftUserLine) {
            party.setGiftUser(true);
        }
    }

    @Override
    public long countToDay(List<String> userIds) {
        Date now = new Date();
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda().between(User::getCreateTime, DateUtil.minDate(now),
                DateUtil.maxDate(now)).eq(User::getRoleName, Constants.SECURITY_ROLE_MEMBER);
        if (CollectionUtil.isNotEmpty(userIds)) {
            queryWrapper.in(User::getUserId, userIds);
        }
        return count(queryWrapper);
    }

    /**
     * 时间范围内的充值总额
     *
     * @param datas
     * @param startTime
     * @param endTime
     * @return
     */
    private double giftMoney(Map<String, UserData> datas, String startTime, String endTime) {
        if (datas == null || datas.isEmpty())
            return 0;
        double giftMoney = 0;
        for (Map.Entry<String, UserData> valueEntry : datas.entrySet()) {
            UserData userdata = valueEntry.getValue();
            Date time = userdata.getCreateTime();
            if (!StringUtils.isNullOrEmpty(startTime)) {
                Date startDate = DateUtils.toDate(startTime, DateUtils.DF_yyyyMMdd);
                int intervalDays = DateUtils.getIntervalDaysByTwoDate(startDate, time);// 开始-数据时间
                if (intervalDays > 0) // 开始>数据时间 ，则过滤
                    continue;
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                Date endDate = DateUtils.toDate(endTime, DateUtils.DF_yyyyMMdd);
                int intervalDays = DateUtils.getIntervalDaysByTwoDate(endDate, time);// 结束-数据时间
                if (intervalDays < 0) // 结束<数据时间
                    continue;
            }
            giftMoney = Arith.add(userdata.getGiftMoney(), giftMoney);
        }

        return giftMoney;
    }

    /**
     * 根据已验证的邮箱获取Party对象
     *
     * @param email 电子邮件
     * @return 用户对象
     */
    @Override
    public User findPartyByVerifiedEmail(String email) {
        if (null == email) return null;
        List<User> list = list(Wrappers.<User>query().lambda().eq(User::getUserMail, email).eq(User::isMailBind, true));
        return list.size() > 0 ? list.get(0) : null;
    }

    public void fillPhone(String phone, User party) {
        party.setUserMobile(phone);
        party.setUserMobileBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
    }

    public void savePhone(String phone, String partyId) {
//        String jarFilePath = BaseMapper.class.getProtectionDomain().getCodeSource().getLocation().getFile();
//        try {
//            jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8");
//            log.info("---> UserServiceImpl.savePhone 获取 jarFilePath 信息:{}", jarFilePath);
//            // 经调试，在前面 save 执行成功后，sleep 一段时间，后面的 getById 才能查询到数据返回，否则返回为 null
//            Thread.sleep(3000L);
//        } catch (Exception e) {
//            log.error("---> UserServiceImpl.savePhone 获取 jarFilePath 信息报错: ", e);
//        }
        /**
         * party
         */
        User party = getById(partyId);
        log.info("---> UserServiceImpl.savePhone partyId:{}, phone:{}, party:{}", partyId, phone, party);
        party.setUserMobile(phone);
        party.setUserMobileBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
        updateById(party);
    }

    @Override
    public void logout(String userId) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return;
        }
        onlineUserService.del(userId);
    }

    @Override
    public Page getAgentAllStatistics(long current, long size, String startTime, String endTime, String userName,
                                      List<String> userIds) {
        Page<AgentUserDto> page = new Page(current, size);
        baseMapper.getAgentAllStatistics(page, userName, userIds);
        /**
         * 页面查询第一层partyId级
         */
        List<String> list_partyId = new ArrayList<String>();
        for (int i = 0; i < page.getRecords().size(); i++) {
            AgentUserDto agentUserDto = page.getRecords().get(i);
            list_partyId.add(agentUserDto.getUserId().toString());
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list_partyId.size(); i++) {
            int reco_agent = 0;
            log.info(list_partyId.get(i));
            /**
             * 所有子集
             */
            List<String> children_all = this.userRecomService.findChildren(list_partyId.get(i));
            /**
             * 正式用户
             */
            List<String> children_member = new ArrayList<>();
            for (int j = 0; j < children_all.size(); j++) {
                String partyId = children_all.get(j);
                User party = getById(partyId);
                if (Constants.SECURITY_ROLE_AGENT.equals(party.getRoleName()) || Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRoleName())) {
                    reco_agent++;
                } else if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
                    children_member.add(partyId);
                }
            }
            Map<String, Object> item_result = this.sumUserData(children_member, startTime, endTime);
            item_result.put("reco_agent", reco_agent);
            item_result.put("reco_member", children_member.size());
            item_result.put("partyId", list_partyId.get(i));
            User party = getById(list_partyId.get(i));
            item_result.put("username", party.getUserName());
            item_result.put("UID", party.getUserCode());
            result.add(item_result);
        }
        Page page_result = new Page();
        page_result.setRecords(result);
        page_result.setTotal(page.getTotal());
        compute(page_result.getRecords());// 计算总收益
        return page_result;
    }

    /**
     * 统计的数据存在空时，不统计总额
     *
     * @param data
     * @return
     */
    private boolean dataExistNull(Map<String, Object> data) {
        if (null == data.get("recharge_withdrawal_fee"))
            return false;
        if (null == data.get("order_income"))
            return false;
        if (null == data.get("fee"))
            return false;
        if (null == data.get("finance_income"))
            return false;
        if (null == data.get("exchange_fee"))
            return false;
        if (null == data.get("exchange_income"))
            return false;
        if (null == data.get("furtures_fee"))
            return false;
        if (null == data.get("furtures_income"))
            return false;
        return true;
    }

    private void compute(List<Map<String, Object>> datas) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(datas))
            return;
        Double totle_income = 0d;
        Double totle_fee = 0d;
        Double business_profit = 0d;//交易盈亏
        Double fin_miner_amount = 0d;//理财 矿机 交易额
        Double fin_miner_income = 0d;//理财 矿机 收益
        DecimalFormat df4 = new DecimalFormat("#.####");
        // 向下取整
        df4.setRoundingMode(RoundingMode.FLOOR);
        for (Map<String, Object> data : datas) {
            totle_income = 0d;
            totle_fee = 0d;
            business_profit = 0d;
            fin_miner_amount = 0d;
            fin_miner_income = 0d;
            if (null != data.get("order_income"))
                data.put("order_income", df4.format(Arith.sub(0, new Double(data.get("order_income").toString()))));// 订单收益负数
            if (null != data.get("finance_income"))
                data.put("finance_income", df4.format(Arith.sub(0, new Double(data.get("finance_income").toString()))));// 理财收益负数
            if (null != data.get("exchange_income"))
                data.put("exchange_income", 0);// 币币收益负数
            if (null != data.get("furtures_income"))
                data.put("furtures_income", df4.format(Arith.sub(0, new Double(data.get("furtures_income").toString()))));// 交割收益负数
            if (null != data.get("miner_income"))
                data.put("miner_income", df4.format(Arith.sub(0, new Double(data.get("miner_income").toString()))));// 矿机收益负数
            if (null != data.get("exchange_lever_order_income"))
                data.put("exchange_lever_order_income", df4.format(Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString()))));// 币币收益负数
            if (!dataExistNull(data))
                continue;
            totle_income = Arith.add(totle_income, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("order_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("finance_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("exchange_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(0));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_fee").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("furtures_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("miner_income").toString()));
            totle_income = Arith.add(totle_income, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("totle_income", df4.format(totle_income));
            totle_fee = Arith.add(totle_fee, new Double(data.get("recharge_withdrawal_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("furtures_fee").toString()));
            totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_lever_fee").toString()));
            data.put("totle_fee", df4.format(totle_fee));
            business_profit = Arith.add(business_profit, new Double(data.get("order_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("furtures_income").toString()));
            business_profit = Arith.add(business_profit, new Double(data.get("exchange_lever_order_income").toString()));
            data.put("business_profit", df4.format(business_profit));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("finance_amount").toString()));
            fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("miner_amount").toString()));
            data.put("fin_miner_amount", df4.format(fin_miner_amount));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("finance_income").toString()));
            fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("miner_income").toString()));
            data.put("fin_miner_income", df4.format(fin_miner_income));
        }
    }

    private List<UserData> filterData(Map<String, UserData> datas, String startTime, String endTime) {
        List<UserData> result = new ArrayList<UserData>();
        for (Map.Entry<String, UserData> valueEntry : datas.entrySet()) {
            UserData userdata = valueEntry.getValue();
            Date time = userdata.getCreateTime();
            if (!StringUtils.isNullOrEmpty(startTime)) {
                Date startDate = DateUtils.toDate(startTime, DateUtils.DF_yyyyMMdd);
                int intervalDays = DateUtils.getIntervalDaysByTwoDate(startDate, time);// 开始-数据时间
                if (intervalDays > 0) // 开始>数据时间 ，则过滤
                    continue;
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                Date endDate = DateUtils.toDate(endTime, DateUtils.DF_yyyyMMdd);
                int intervalDays = DateUtils.getIntervalDaysByTwoDate(endDate, time);// 结束-数据时间
                if (intervalDays < 0) // 结束<数据时间
                    continue;
            }
            result.add(userdata);
        }
        return result;
    }

    private Map<String, Object> sumUserData(List<String> children, String startTime, String endTime) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(children)) {//children数据为空时，数据填充,这里操作减少dubbo调用
            return sumData(new HashMap<String, Object>(), new ArrayList<UserData>());
        }
        Map<String, Object> item_result = new HashMap<String, Object>();
        List<Map<String, UserData>> datas = this.userDataService.findByPartyIds(children);
        for (int i = 0; i < datas.size(); i++) {
            Map<String, UserData> data_all = datas.get(i);
            if (data_all == null) {
                continue;
            }
            List<UserData> userdata = filterData(data_all, startTime, endTime);
            item_result = sumData(item_result, userdata);
        }
        if (item_result.isEmpty()) {//item_result数据为空时，数据填充
            item_result = sumData(item_result, new ArrayList<UserData>());
        }
        return item_result;
    }

    private Map<String, Object> sumData(Map<String, Object> item_result, List<UserData> datas) {
        double recharge_dapp = 0;
        double withdraw_dapp = 0;
        double recharge = 0;
        double recharge_usdt = 0;
        double recharge_eth = 0;
        double recharge_btc = 0;
        double recharge_ht = 0;
        double recharge_ltc = 0;
        double withdraw = 0;
        double withdraw_eth = 0;
        double withdraw_btc = 0;
        double recharge_withdrawal_fee = 0;
        double gift_money = 0;
        double balance_amount = 0;
        double amount = 0;
        double fee = 0;
        double order_income = 0;
        double finance_amount = 0;
        double finance_income = 0;
        double exchange_amount = 0;
        double exchange_fee = 0;
        double exchange_income = 0;
        double coin_income = 0;
        double furtures_amount = 0;
        double furtures_fee = 0;
        double furtures_income = 0;
        double miner_income = 0;
        double miner_amount = 0;
        double third_recharge_amount = 0;
        double exchange_lever_amount = 0;
        double exchange_lever_fee = 0;
        double exchange_lever_order_income = 0;
        for (int i = 0; i < datas.size(); i++) {
            UserData data = datas.get(i);
            // 充提
            recharge_dapp = Arith.add(data.getRechargeDapp(), recharge_dapp);
            withdraw_dapp = Arith.add(data.getWithdrawDapp(), withdraw_dapp);
            recharge = Arith.add(data.getRecharge(), recharge);
            recharge_usdt = Arith.add(data.getRechargeUsdt(), recharge_usdt);
            recharge_eth = Arith.add(data.getRechargeEth(), recharge_eth);
            recharge_btc = Arith.add(data.getRechargeBtc(), recharge_btc);
            recharge_ht = Arith.add(data.getRechargeHt(), recharge_ht);
            recharge_ltc = Arith.add(data.getRechargeLtc(), recharge_ltc);
            withdraw = Arith.add(data.getWithdraw(), withdraw);
            withdraw_eth = Arith.add(data.getWithdrawEth(), withdraw_eth);
            withdraw_btc = Arith.add(data.getWithdrawBtc(), withdraw_btc);
            recharge_withdrawal_fee = Arith.add(data.getRechargeWithdrawalFee(), recharge_withdrawal_fee);
            gift_money = Arith.add(data.getGiftMoney(), gift_money);
            balance_amount = Arith.add(Arith.sub(data.getRecharge(), data.getWithdraw()), balance_amount);
            // 永续
            amount = Arith.add(data.getAmount(), amount);
            fee = Arith.add(data.getFee(), fee);
            order_income = Arith.add(data.getOrderIncome(), order_income);
            // 理财
            finance_amount = Arith.add(data.getFinanceAmount(), finance_amount);
            finance_income = Arith.add(data.getFinanceIncome(), finance_income);
            // 币币
            exchange_amount = Arith.add(data.getExchangeAmount(), exchange_amount);
            exchange_fee = Arith.add(data.getExchangeFee(), exchange_fee);
            //exchange_income = Arith.add(data.getExchange_income(), exchange_income);
            exchange_income = 0;
            coin_income = Arith.add(data.getCoinIncome(), coin_income);
            // 交割
            furtures_amount = Arith.add(data.getFurturesAmount(), furtures_amount);
            furtures_fee = Arith.add(data.getFurturesFee(), furtures_fee);
            furtures_income = Arith.add(data.getFurturesIncome(), furtures_income);
            //矿机
            miner_income = Arith.add(data.getMinerIncome(), miner_income);
            miner_amount = Arith.add(data.getMinerAmount(), miner_amount);
            //三方充值货币金额
            third_recharge_amount = Arith.add(data.getThirdRechargeAmount(), third_recharge_amount);
            //币币杠杆
            exchange_lever_amount = Arith.add(data.getExchangeLeverAmount(), exchange_lever_amount);
            exchange_lever_fee = Arith.add(data.getExchangeLeverFee(), exchange_lever_fee);
            exchange_lever_order_income = Arith.add(data.getExchangeLeverOrderIncome(), exchange_lever_order_income);
        }
        if (item_result != null && item_result.size() != 0) {
            // 充提
            item_result.put("recharge_dapp", Arith.add(Double.valueOf(item_result.get("recharge_dapp").toString()), recharge_dapp));
            item_result.put("withdraw_dapp", Arith.add(Double.valueOf(item_result.get("withdraw_dapp").toString()), withdraw_dapp));
            item_result.put("recharge", Arith.add(Double.valueOf(item_result.get("recharge").toString()), recharge));
            item_result.put("recharge_usdt", Arith.add(Double.valueOf(item_result.get("recharge_usdt").toString()), recharge_usdt));
            item_result.put("recharge_eth", Arith.add(Double.valueOf(item_result.get("recharge_eth").toString()), recharge_eth));
            item_result.put("recharge_btc", Arith.add(Double.valueOf(item_result.get("recharge_btc").toString()), recharge_btc));
            item_result.put("recharge_ht", Arith.add(Double.valueOf(item_result.get("recharge_ht").toString()), recharge_ht));
            item_result.put("recharge_ltc", Arith.add(Double.valueOf(item_result.get("recharge_ltc").toString()), recharge_ltc));
            item_result.put("withdraw", Arith.add(Double.valueOf(item_result.get("withdraw").toString()), withdraw));
            item_result.put("withdraw_eth", Arith.add(Double.valueOf(item_result.get("withdraw_eth").toString()), withdraw_eth));
            item_result.put("withdraw_btc", Arith.add(Double.valueOf(item_result.get("withdraw_btc").toString()), withdraw_btc));
            item_result.put("recharge_withdrawal_fee", Arith.add(Double.valueOf(item_result.get("recharge_withdrawal_fee").toString()), recharge_withdrawal_fee));
            item_result.put("gift_money", Arith.add(Double.valueOf(item_result.get("gift_money").toString()), gift_money));
            item_result.put("balance_amount", Arith.add(Double.valueOf(item_result.get("balance_amount").toString()), balance_amount));
            // 永续
            item_result.put("amount", Arith.add(Double.valueOf(item_result.get("amount").toString()), amount));
            item_result.put("fee", Arith.add(Double.valueOf(item_result.get("fee").toString()), fee));
            item_result.put("order_income", Arith.add(Double.valueOf(item_result.get("order_income").toString()), order_income));
            // 理财
            item_result.put("finance_amount", Arith.add(Double.valueOf(item_result.get("finance_amount").toString()), finance_amount));
            item_result.put("finance_income", Arith.add(Double.valueOf(item_result.get("finance_income").toString()), finance_income));
            // 币币
            item_result.put("exchange_amount", Arith.add(Double.valueOf(item_result.get("exchange_amount").toString()), exchange_amount));
            item_result.put("exchange_fee", Arith.add(Double.valueOf(item_result.get("exchange_fee").toString()), exchange_fee));
            //item_result.put("exchange_income", Arith.add(Double.valueOf( item_result.get("exchange_income").toString()),exchange_income));
            item_result.put("exchange_income", 0);
            item_result.put("coin_income", Arith.add(Double.valueOf(item_result.get("coin_income").toString()), coin_income));
            // 交割
            item_result.put("furtures_amount", Arith.add(Double.valueOf(item_result.get("furtures_amount").toString()), furtures_amount));
            item_result.put("furtures_fee", Arith.add(Double.valueOf(item_result.get("furtures_fee").toString()), furtures_fee));
            item_result.put("furtures_income", Arith.add(Double.valueOf(item_result.get("furtures_income").toString()), furtures_income));
            //矿机
            item_result.put("miner_income", Arith.add(Double.valueOf(item_result.get("miner_income").toString()), miner_income));
            item_result.put("miner_amount", Arith.add(Double.valueOf(item_result.get("miner_amount").toString()), miner_amount));
            //三方充值货币金额
            item_result.put("third_recharge_amount", Arith.add(Double.valueOf(item_result.get("third_recharge_amount").toString()), third_recharge_amount));
            //币币杠杆
            item_result.put("exchange_lever_amount", Arith.add(Double.valueOf(item_result.get("exchange_lever_amount").toString()), exchange_lever_amount));
            item_result.put("exchange_lever_fee", Arith.add(Double.valueOf(item_result.get("exchange_lever_fee").toString()), exchange_lever_fee));
            item_result.put("exchange_lever_order_income", Arith.add(Double.valueOf(item_result.get("exchange_lever_order_income").toString()), exchange_lever_order_income));
        } else {
            // 充提
            item_result.put("recharge_dapp", recharge_dapp);
            item_result.put("withdraw_dapp", withdraw_dapp);
            item_result.put("recharge", recharge);
            item_result.put("recharge_usdt", recharge_usdt);
            item_result.put("recharge_eth", recharge_eth);
            item_result.put("recharge_btc", recharge_btc);
            item_result.put("recharge_ht", recharge_ht);
            item_result.put("recharge_ltc", recharge_ltc);
            item_result.put("withdraw", withdraw);
            item_result.put("withdraw_eth", withdraw_eth);
            item_result.put("withdraw_btc", withdraw_btc);
            item_result.put("recharge_withdrawal_fee", recharge_withdrawal_fee);
            item_result.put("gift_money", gift_money);
            item_result.put("balance_amount", balance_amount);
            // 永续
            item_result.put("amount", amount);
            item_result.put("fee", fee);
            item_result.put("order_income", order_income);
            // 理财
            item_result.put("finance_amount", finance_amount);
            item_result.put("finance_income", finance_income);
            // 币币
            item_result.put("exchange_amount", exchange_amount);
            item_result.put("exchange_fee", exchange_fee);
            item_result.put("exchange_income", 0);
            item_result.put("coin_income", coin_income);
            // 交割
            item_result.put("furtures_amount", furtures_amount);
            item_result.put("furtures_fee", furtures_fee);
            item_result.put("furtures_income", furtures_income);
            // 矿机
            item_result.put("miner_income", miner_income);
            item_result.put("miner_amount", miner_amount);
            //三方充值货币金额
            item_result.put("third_recharge_amount", third_recharge_amount);
            //币币杠杆
            item_result.put("exchange_lever_amount", exchange_lever_amount);
            item_result.put("exchange_lever_fee", exchange_lever_fee);
            item_result.put("exchange_lever_order_income", exchange_lever_order_income);
        }
        return item_result;
    }

    @Override
    @Transactional
    public User saveRegisterUsername(String username, String password, String recoUserCode, String safeword) {
        User party_reco = findUserByUserCode(recoUserCode);
//		用户注册是否需要推荐码
        if ("true".equals(sysparaService.find("register_need_usercode").getSvalue())) {
            if (StringUtils.isNotEmpty(recoUserCode)) {
                if (party_reco == null) {
                    throw new YamiShopBindException("请输入正确的推荐码");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRoleName())) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
                if (!party_reco.isEnabled()) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
            }

        }
        int userLevel = 1;
        if (null != party_reco) {
            userLevel++;
//            userLevel = getUserRecomLevel(userLevel, party_reco.getUserId());
        }
//        if ("true".equals(sysparaService.find("register_need_usercode_turn").getSvalue())) {
//            if (!party_reco.getRegisterUsercode()) {
//                throw new BusinessException("推荐人无权限推荐");
//            }
//        }
        if (findByUserName(username) != null) {
            throw new YamiShopBindException("用户名重复");
        }
        /**
         * 用户code
         */
        String usercode = getUserCode();
        /**
         * party
         */
        User party = new User();
        party.setUserName(username);
        party.setUserCode(usercode);
        int ever_user_level_num = sysparaService.find("ever_user_level_num").getInteger();
        int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();

        party.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // if(ever_user_level_num_custom>0){
        //     party.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // }else{
        //     party.setUserLevel(userLevel);
        // }

        party.setSafePassword(passwordEncoder.encode(safeword));
        party.setRoleName(Constants.SECURITY_ROLE_MEMBER);
        party.setLoginPassword(passwordEncoder.encode(password));
        party.setStatus(1);
        save(party);
        /**
         * usdt账户
         */
        Wallet wallet = new Wallet();
        wallet.setUserId(party.getUserId().toString());
        this.walletService.save(wallet);
        if (party_reco != null) {
            UserRecom userRecom = new UserRecom();
            userRecom.setUserId(party_reco.getUserId());
            userRecom.setRecomUserId(party.getUserId());// 父类partyId
            this.userRecomService.save(userRecom);
            party.setUserRecom(party_reco.getUserId());
            updateById(party);
        }
//        String uuid = UUIDGenerator.getUUID();
//        String partyId = party.getUserId().toString();
//        String partyRecoId = party_reco != null?party_reco.getUserId().toString():"";
//        jdbcTemplate.execute("INSERT INTO T_USER(UUID,PARTY_ID,PARENT_PARTY_ID) VALUES('"+uuid+"','"+partyId+"','"+partyRecoId+"')");
        userDataService.saveRegister(party.getUserId());
        /**
         * 用户注册自动赠送金额 start
         */
        String register_gift_coin = sysparaService.find("register_gift_coin").getSvalue();
        if (!"".equals(register_gift_coin) && register_gift_coin != null) {
            String[] register_gift_coins = register_gift_coin.split(",");
            String gift_symbol = register_gift_coins[0];
            double gift_sum = Double.valueOf(register_gift_coins[1]);
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }
                walletService.update(wallet.getUserId().toString(), gift_sum);
                userDataService.saveGiftMoneyHandle(wallet.getUserId(), gift_sum);

                /*
                 * 保存账变日志
                 */
//                MoneyLog moneyLog = new MoneyLog();
//                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//                moneyLog.setAmountBefore(new BigDecimal(amount_before));
//                moneyLog.setAmount(new BigDecimal(gift_sum));
//                moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getMoney().doubleValue(), gift_sum)));
//                moneyLog.setUserId(party.getUserId());
//                moneyLog.setWalletType(gift_symbol.toUpperCase());
//                moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
//                moneyLog.setLog("用户注册自动赠送金额");
//                this.moneyLogService.save(moneyLog);
            } else {
                WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getUserId(), gift_symbol);
                if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }
                walletService.updateExtend(walletExtend.getPartyId(), gift_symbol, gift_sum);
                double close = dataService.realtime(gift_symbol).get(0).getClose();
                double amount = Arith.mul(close, gift_sum, 2);
                userDataService.saveGiftMoneyHandle(wallet.getUserId(), amount);

                /*
                 * 保存账变日志
                 */
//                MoneyLog moneyLog = new MoneyLog();
//                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//                moneyLog.setAmountBefore(new BigDecimal(amount_before));
//                moneyLog.setAmount(new BigDecimal(gift_sum));
//                moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getAmount(), gift_sum)));
//                moneyLog.setUserId(party.getUserId());
//                moneyLog.setWalletType(gift_symbol.toUpperCase());
//                moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
//                moneyLog.setLog("用户注册自动赠送金额");
//                this.moneyLogService.save(moneyLog);
            }
        }
        return party;
    }

    public User findPartyByEmail(String email) {
        if (null == email) {
            return null;
        }
        List<User> list = list(Wrappers.<User>query().lambda().eq(User::getUserMail, email));
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void saveRegister(String username, String password, String usercode, String safeword, String verifcode, String type) {
        username = username.trim();
        password = password.trim();
        if (!"null".equals(safeword) && !StringUtils.isEmptyString(safeword)) {
            safeword = safeword.trim();
        }
        User party_reco = findUserByUserCode(usercode);
        String key = username;
        String authcode = identifyingCodeTimeWindowService.getAuthCode(key);
        //log.info("---> UserServiceImpl.saveRegister 用户名:{} 注册，正确的验证码值为:{}, 输入的值为:{}", username, authcode, verifcode);
        if ((authcode == null) || (!authcode.equals(verifcode))) {
            throw new YamiShopBindException("验证码不正确");
        }
        if ("true".equals(this.sysparaService.find("register_need_usercode").getSvalue())) {
            if (StringUtils.isNotEmpty(usercode)) {
                if (null == party_reco) {
                    throw new YamiShopBindException("推荐码不正确");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRoleName())) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
                if (!party_reco.isEnabled()) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
            }
        }
//        if ("true".equals(this.sysparaService.find("register_need_usercode_turn").getSvalue())) {
//            if (!party_reco.getRegister_usercode()) {
//                throw new BusinessException("推荐人无权限推荐");
//            }
//        }
        if (findByUserName(username) != null) {
            throw new YamiShopBindException("用户名重复");
        }
        int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
        int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
        int userLevel = 1;
        if (null != party_reco) {
            userLevel++;
//            userLevel = getUserRecomLevel(userLevel, party_reco.getUserId());
        }
        User party = new User();
        party.setUserName(username);
        party.setUserCode(getUserCode());
        party.setLoginPassword(passwordEncoder.encode(password));
        party.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
    //   if(ever_user_level_num_custom>0){
    //         party.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
    //     }else{
    //         party.setUserLevel(userLevel);
    //     }
        party.setSafePassword(this.passwordEncoder.encode(safeword));
        party.setRoleName(Constants.SECURITY_ROLE_MEMBER);

//		if (reg.getUsername().indexOf("@") == -1) {
        if (type.equals("1")) {
            // 手机注册
//			if (StringUtils.isEmptyString(reg.getUsername()) || !Strings.isNumber(reg.getUsername()) || reg.getUsername().length() > 15) {
            if (StringUtils.isEmptyString(username) || username.length() > 20) {
                throw new YamiShopBindException("请输入正确的手机号码");
            }

            fillPhone(username, party);
            this.save(party);
        } else {
            // 邮箱注册
            if (!Strings.isEmail(username)) {
                throw new YamiShopBindException("请输入正确的邮箱地址");
            }
            if (findPartyByEmail(username) != null) {
                throw new YamiShopBindException("邮箱已重复");
            }
            this.fillEmail(username, party);
            this.save(party);
        }
        try {
            Thread.sleep(2000L);
        } catch (Exception e) {

        }

//        Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);
        // usdt账户
        Wallet wallet = new Wallet();
        wallet.setUserId(party.getUserId().toString());
        this.walletService.save(wallet);
        if (party_reco != null) {
            UserRecom userRecom = new UserRecom();
            userRecom.setUserId(party_reco.getUserId());
            userRecom.setRecomUserId(party.getUserId());
            this.userRecomService.save(userRecom);
            party.setUserRecom(party_reco.getUserId());
            updateById(party);
        }
        this.userDataService.saveRegister(party.getUserId());
        // 用户注册自动赠送金额
        String register_gift_coin = this.sysparaService.find("register_gift_coin").getSvalue();
        if (!"".equals(register_gift_coin) && register_gift_coin != null) {
            String[] register_gift_coins = register_gift_coin.split(",");
            String gift_symbol = register_gift_coins[0];
            double gift_sum = Double.valueOf(register_gift_coins[1]);
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }
                this.walletService.update(wallet.getUserId().toString(), gift_sum);
                userDataService.saveGiftMoneyHandle(wallet.getUserId(), gift_sum);

                // 保存账变日志
//                MoneyLog moneyLog = new MoneyLog();
//                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//                moneyLog.setAmountBefore(new BigDecimal(amount_before));
//                moneyLog.setAmount(new BigDecimal(gift_sum));
//                moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getMoney().doubleValue(), gift_sum)));
//                moneyLog.setUserId(party.getUserId());
//                moneyLog.setWalletType(gift_symbol.toUpperCase());
//                moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
//                moneyLog.setLog("用户注册自动赠送金额");
//                this.moneyLogService.save(moneyLog);
            } else {
                WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getUserId(), gift_symbol);
                double amount_before = walletExtend.getAmount();
                if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }

                double close = dataService.realtime(gift_symbol).get(0).getClose();
                double amount = Arith.mul(close, gift_sum, 2);
                // BigDecimal amount = dataService.realtime(gift_symbol).get(0).getClose().multiply(new BigDecimal(gift_sum)).setScale(2, RoundingMode.FLOOR);
                userDataService.saveGiftMoneyHandle(wallet.getUserId(), amount);                // 保存账变日志
//                MoneyLog moneyLog = new MoneyLog();
//                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//                moneyLog.setAmountBefore(new BigDecimal(amount_before));
//                moneyLog.setAmount(new BigDecimal(gift_sum));
//                moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getAmount(), gift_sum)));
//                moneyLog.setUserId(party.getUserId());
//                moneyLog.setWalletType(gift_symbol.toUpperCase());
//                moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
//                moneyLog.setLog("用户注册自动赠送金额");
//                this.moneyLogService.save(moneyLog);
            }
        }
        this.identifyingCodeTimeWindowService.delAuthCode(key);
    }

//    /**
//     * 根据推荐关系获取层级
//     *
//     * @param userLevel
//     * @param userId
//     * @return
//     */
//    private int getUserRecomLevel(int userLevel, String userId) {
        // 查询上级用户
//        UserRecom userRecom = userRecomService.getOne(Wrappers.<UserRecom>lambdaQuery().eq(UserRecom::getUserId, userId));
//        if(null != userRecom) {
//            userLevel ++;
//            return this.getUserRecomLevel(userLevel, userRecom.getRecomUserId());
//        }
//        return 0;
//    }

    public void fillEmail(String email, User party) {
        party.setUserMail(email);
        party.setMailBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
    }

    public void saveEmail(String email, String partyId) {
        /**
         * party
         */
        User party = getById(partyId);
        party.setUserMail(email);
        party.setMailBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
        updateById(party);
    }

    @Override
    @Transactional
    public User saveAgentUser(String userName, String password, String safePassword, String roleName, String remarks,
                              String userCode, boolean loginAuthority) {
        User user = findByUserName(userName);
        if (user != null) {
            throw new YamiShopBindException("用户名重复");
        }
        User recomUser = null;
        int userLevel = 1;
        //推荐人
        if (StrUtil.isNotBlank(userCode)) {
            recomUser = findUserByUserCode(userCode);
            if (null != recomUser) {
                userLevel++;
//                userLevel = getUserRecomLevel(userLevel, recomUser.getUserId());
            }
        }
        int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
        int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();

        user = new User();
        user.setUserName(userName);
        user.setUserCode(getAgentUserCode());
        user.setRemarks(remarks);
        user.setRoleName(roleName);
        user.setLoginPassword(passwordEncoder.encode(password));
        user.setSafePassword(passwordEncoder.encode(safePassword));
        user.setStatus(loginAuthority ? 1 : 0);
        user.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // user.setUserLevel(userLevel);
        save(user);
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getUserId());
        walletService.save(wallet);
        //推荐人
        if (StrUtil.isNotBlank(userCode)) {
//            if ("true".equals(this.sysparaService.find("register_need_usercode").getSvalue())) {
            if (null == recomUser) {
                throw new YamiShopBindException("推荐码不正确");
            }
            if (UserConstants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
                throw new YamiShopBindException("推荐人无权限推荐");
            }
            if (recomUser.getStatus() == 0) {
                throw new YamiShopBindException("推荐人无权限推荐");
            }
            UserRecom userRecom = new UserRecom();
            userRecom.setUserId(recomUser.getUserId());
            // 父类partyId
            userRecom.setRecomUserId(user.getUserId());
            userRecomService.save(userRecom);
            user.setUserRecom(recomUser.getUserId());
            updateById(user);
            // }
        }
        return user;
    }

    @Override
    @Transactional
    public void updateWallt(String userId, BigDecimal moneyRevise, int accountType, String coinType) {
        User user = getById(userId);
        if (user == null) {
            throw new YamiShopBindException("用户不存在");
        }
        if (accountType == 1) { //充值
        }
        if (accountType == 2) { //扣除
            moneyRevise = moneyRevise.negate();
        }
        walletService.updateMoney("", userId, moneyRevise, new BigDecimal(0), Constants.MONEYLOG_CATEGORY_COIN
                , coinType, accountType == 1 ? Constants.MONEYLOG_CONTENT_RECHARGE : Constants.MONEYLOG_CONTENT_WITHDRAW, "后台修改账号余额");
    }

    public void checkGooleAuthAndSefeword(User user, String googleAuthCode, String loginSafeword) {
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        long t = System.currentTimeMillis();
        boolean flag = ga.check_code(user.getGoogleAuthSecret(), Long.valueOf(googleAuthCode), t);
        if (!flag) {
            throw new YamiShopBindException("谷歌验证码错误!");
        }
        if (!passwordEncoder.matches(loginSafeword, user.getSafePassword())) {
            throw new YamiShopBindException("登录人资金密码错误");
        }
    }

    @Override
    public void restLoginPasswrod(String userId, String password) {
        User user = getById(userId);
        user.setLoginPassword(passwordEncoder.encode(password));
        updateById(user);
    }

    @Override
    public void restSafePassword(String userId, String newSafeword) {
        User user = getById(userId);
        user.setSafePassword(passwordEncoder.encode(newSafeword));
        updateById(user);
    }

    @Override
    public void deleteGooleAuthCode(String userId, String googleAuthCode, String loginSafeword) {
        User user = getById(userId);
        if (user == null) {
            throw new YamiShopBindException("参数错误!");
        }
        if (!user.isGoogleAuthBind()) {
            throw new YamiShopBindException("用户谷歌验证码未绑定!");
        }
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        long t = System.currentTimeMillis();
        boolean flag = ga.check_code(user.getGoogleAuthSecret(), Long.valueOf(googleAuthCode), t);
        if (!flag) {
            throw new YamiShopBindException("谷歌验证码错误!");
        }
        if (!passwordEncoder.matches(loginSafeword, user.getSafePassword())) {
            throw new YamiShopBindException("登录人资金密码错误");
        }
        user.setGoogleAuthBind(false);
        user.setGoogleAuthSecret("");
        updateById(user);
    }

    @Override
    @Transactional
    public void updateWithdrawalLimitFlow(String userId, BigDecimal moneyWithdraw, String userName) {


        User user = getById(userId);
        BigDecimal lastAmount = user.getWithdrawLimitAmount();
        lastAmount = lastAmount.setScale(4, RoundingMode.FLOOR);
        if (lastAmount == null) {
            lastAmount = new BigDecimal(0);
        }
        if (moneyWithdraw == null) {
            throw new YamiShopBindException("请填入有效数字");
        }
        BigDecimal resultAmount = lastAmount.add(moneyWithdraw);
        if (moneyWithdraw.doubleValue() < 0) {
            throw new YamiShopBindException("修改后金额不能小于0");
        }
        user.setWithdrawLimitAmount(moneyWithdraw);
        BigDecimal afterParty = user.getWithdrawLimitAmount();
        updateById(user);
        /**
         * 操作日志
         */
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);

        log.setUsername(user.getUserName());
        log.setUserId(user.getUserId());
        log.setOperator(userName);
        log.setLog("ip:" + IPHelper.getIpAddr() + ",手动修改提现限制流水。修改前数量为[" + lastAmount.toPlainString() + "]，"
                + "修改数量为[" + moneyWithdraw + "]，修改后数量为["
                + afterParty + "]。");
        logService.save(log);
    }

    @Override
    public User findUserByUserCode(String userCode) {
        return getOne(Wrappers.<User>query().lambda().eq(User::getUserCode, userCode).or().eq(User::getUserId, userCode));
    }

    private String getUserCode() {
        Integer user_uid_sequence = null;
        synchronized (USER_CODE_LOCK.intern()) {
            Syspara syspara = sysparaService.find("user_uid_sequence");
            user_uid_sequence = syspara.getInteger().intValue() + ((int) (Math.random() * 3 + 1));
            SysparasDto sysparasDto = new SysparasDto();
            sysparasDto.setUser_uid_sequence(user_uid_sequence + "");
            sysparaService.updateSysparas(sysparasDto);
        }
        String usercode = String.valueOf(user_uid_sequence);
        return usercode;
    }

    private String getAgentUserCode() {
        Syspara syspara = sysparaService.find("agent_uid_sequence");
        int agent_uid_sequence = syspara.getInteger() + 1;
        SysparasDto sysparasDto = new SysparasDto();
        sysparasDto.setAgent_uid_sequence(String.valueOf(agent_uid_sequence));
        sysparaService.updateSysparas(sysparasDto);
        String usercode = String.valueOf(agent_uid_sequence);
        return usercode;
    }

    @Override
    public boolean isOnline(String partyId) {
        Object object = onlineUserService.get(partyId);
        if (object != null) {
            return true;
        }
        return false;
    }

    @Override
    public void online(String partyId) {
        if (StringUtils.isNullOrEmpty(partyId)) {
            return;
        }
        onlineUserService.put(partyId, new Date());
    }

    @Override
    @Transactional
    public void saveUser(String username, String password, boolean login_authority, boolean enabled, String remarks, String operatorUsername, String ip, String parents_usercode) {
        username = username.trim();
        password = password.trim();
        if (findByUserName(username) != null) {
            throw new YamiShopBindException("用户名重复");
        }
        /**
         * 用户code
         */
        String usercode = getUserCode();
        int userLevel = 1;
        if (!StringUtils.isNullOrEmpty(parents_usercode)) {
            User party_parents = findUserByUserCode(parents_usercode);
            if (party_parents == null) {
                throw new YamiShopBindException("推荐码不正确");
            }
            userLevel++;
//            userLevel = getUserRecomLevel(userLevel, party_parents.getUserId());
        }
        int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
        int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
        /**
         * party
         */
        User party = new User();
        party.setUserName(username);
        party.setEnabled(enabled);
        party.setStatus(login_authority ? 1 : 0);
        party.setRemarks(remarks);
        party.setUserCode(usercode);
        party.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // if(ever_user_level_num_custom>0){
            
        // }else{
        //     party.setUserLevel(userLevel);
        // }
        party.setSafePassword(passwordEncoder.encode("000000"));
        party.setLoginPassword(passwordEncoder.encode(password));
        party.setRoleName(Constants.SECURITY_ROLE_GUEST);
        
        // 借贷状态 1正常 2禁止
        party.setLoanStatus(1);
        // 已贷金额(借贷)
        party.setLoanAlreadyAmount(BigDecimal.ZERO);
        // 可贷金额(借贷) - 从系统配置获取
        Syspara loanMaxAmount = this.sysparaService.find("loan_max_amount");
        party.setLoanCanAmount(loanMaxAmount != null && loanMaxAmount.getBigDecimal() != null 
            ? loanMaxAmount.getBigDecimal() 
            : BigDecimal.ZERO);
        // 是否老客户 1老客户 2新客户 - 默认新客户
        party.setIsOldUser(2);
        // 购买量化机器状态 1正常 2禁止
        party.setCreateRobotStatus(1);
        // 提币状态 1正常 2禁止
        party.setTxState(1);
        // 期权预设结果 - 未设置
        party.setOptionPreResult(0);
        
        save(party);
        if (!StringUtils.isNullOrEmpty(parents_usercode)) {
            User party_parents = findUserByUserCode(parents_usercode);
            if (party_parents == null) {
                throw new YamiShopBindException("推荐码不正确");
            }
            UserRecom userRecom = new UserRecom();
            userRecom.setUserId(party_parents.getUserId());
            // 父类partyId
            userRecom.setRecomUserId(party.getUserId());
            this.userRecomService.save(userRecom);
            party.setUserRecom(party_parents.getUserRecom());
            updateById(party);
        }
        /**
         * usdt账户
         */
        Wallet wallet = new Wallet();
        wallet.setUserId(party.getUserId().toString());
        this.walletService.save(wallet);
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(username);
        log.setOperator(operatorUsername);
        log.setUserId(party.getUserId());
        log.setLog("ip:" + ip + ",手动新增了演示用户:" + username);
        logService.save(log);
    }

    @Override
    @Transactional
    public void saveResetLock(String partyId, double moneyRevise, String safeword, String operatorName, String resetType, String ip, String coinType) {
        double amount_before = 0D;
        double lock_amount_before = 0D;
        double freeze_amount_before = 0D;
        //更改的可用金额
        double changeMoney = 0d;
        //更改的锁定金额
        double lockMoney = 0.0d;
        //更改的冻结金额
        double freezeMoney = 0.0d;

        if ("usdt".equals(coinType.toLowerCase())) {
            Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
            amount_before = wallet.getMoney().doubleValue();
            lock_amount_before = wallet.getLockMoney().doubleValue();
            freeze_amount_before = wallet.getFreezeMoney().doubleValue();
            Map<String, Object> map = checkChangeMoney(moneyRevise, resetType, amount_before, lock_amount_before, freeze_amount_before);
            changeMoney = Double.valueOf(map.get("changeMoney").toString());
            lockMoney = Double.valueOf(map.get("lockMoney").toString());
            freezeMoney = Double.valueOf(map.get("freezeMoney").toString());
            walletService.updateWithLockAndFreeze(wallet.getUserId().toString(), changeMoney, lockMoney, freezeMoney);
        } else {
            WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coinType);
            amount_before = walletExtend.getAmount();
            lock_amount_before = walletExtend.getLockAmount();
            freeze_amount_before = walletExtend.getFreezeAmount();
            Map<String, Object> map = checkChangeMoney(moneyRevise, resetType, amount_before, lock_amount_before, freeze_amount_before);
            changeMoney = Double.valueOf(map.get("changeMoney").toString());
            lockMoney = Double.valueOf(map.get("lockMoney").toString());
            freezeMoney = Double.valueOf(map.get("freezeMoney").toString());
            walletService.updateExtendWithLockAndFreeze(walletExtend.getPartyId().toString(), coinType, changeMoney, lockMoney, freezeMoney);
        }

        /*
         * 保存账变日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmountBefore(new BigDecimal(amount_before));
        moneyLog.setAmount(new BigDecimal(changeMoney));
        moneyLog.setAmountAfter(new BigDecimal(Arith.add(amount_before, changeMoney)));
        moneyLog.setUserId(partyId);
        moneyLog.setWalletType(coinType.toUpperCase());
        moneyLog.setContentType(Constants.MONEYLOG_CONTENT_SYS_LOCK);
        /**
         * 操作日志
         */
        User user = getById(partyId);
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setUsername(user.getUserName());
        log.setOperator(operatorName);
        log.setUserId(user.getUserId());
        String logInfo = "";
        String logResetType = "";
        double lockOrFreezeMoney = 0d;
        if ("moneryToLock".equals(resetType)) {//余额转到锁定
            logInfo = "手动操作,可用金额->锁定金额";
            logResetType = "锁定";
            lockOrFreezeMoney = lockMoney;
            moneyLog.setLog(logInfo);
            moneyLogService.save(moneyLog);
        } else if ("lockToMoney".equals(resetType)) {//锁定转到余额
            logInfo = "手动操作,锁定金额->可用金额";
            logResetType = "锁定";
            lockOrFreezeMoney = lockMoney;
            moneyLog.setLog(logInfo);
            moneyLogService.save(moneyLog);
        } else if ("addLock".equals(resetType)) {
            logInfo = "手动添加锁定金额";
            logResetType = "锁定";
            lockOrFreezeMoney = lockMoney;
            log.setExtra(Constants.MONEYLOG_CONTENT_SYS_MONEY_ADD_LOCK);
            moneyLog.setLog(logInfo);
            moneyLogService.save(moneyLog);

        } else if ("subLock".equals(resetType)) {
            logInfo = "手动减少锁定金额";
            logResetType = "锁定";
            moneyLog.setLog(logInfo);
            lockOrFreezeMoney = lockMoney;
            log.setExtra(Constants.MONEYLOG_CONTENT_SYS_MONEY_SUB_LOCK);
            moneyLogService.save(moneyLog);
        } else if ("moneryToFreeze".equals(resetType)) {//余额转到冻结
            logInfo = "手动操作,可用金额->冻结金额";
            logResetType = "冻结";
            lockOrFreezeMoney = freezeMoney;
            moneyLog.setLog(logInfo);
            moneyLogService.save(moneyLog);
        } else if ("freezeToMoney".equals(resetType)) {//冻结转到余额
            logInfo = "手动操作,冻结金额->可用金额";
            logResetType = "冻结";
            lockOrFreezeMoney = freezeMoney;
            moneyLog.setLog(logInfo);
            moneyLogService.save(moneyLog);
        }
        String logText = MessageFormat.format("ip:{0},{1}。修改币种[{2}],修改可用数量[{3}],修改{4}数量[{5}]", ip, logInfo, coinType, changeMoney, logResetType, lockOrFreezeMoney);
        log.setLog(logText);
        logService.save(log);
    }

    private Map<String, Object> checkChangeMoney(double moneyRevise, String resetType, double amountBefore,
                                                 double lockAmountBefore,
                                                 double freezeAmountBefore) {
        Map<String, Object> map = new HashMap<String, Object>();
        //更改的可用金额
        double changeMoney = 0d;
        //更改的锁定金额
        double lockMoney = 0.0d;
        //更改的冻结金额
        double freezeMoney = 0.0d;
        if (StringUtils.isEmptyString(resetType)) {
            throw new YamiShopBindException("请选择转移类型");
        } else if ("moneryToLock".equals(resetType)) {//余额转到锁定
            if (moneyRevise > amountBefore) {
                throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
            }
            changeMoney = Arith.sub(0, moneyRevise);
            lockMoney = moneyRevise;
        } else if ("lockToMoney".equals(resetType)) {
            if (moneyRevise > lockAmountBefore) {
                throw new YamiShopBindException("操作失败！修正后账户锁定余额小于0。");
            }
            changeMoney = moneyRevise;
            lockMoney = Arith.sub(0, moneyRevise);
        } else if ("addLock".equals(resetType)) {
            changeMoney = 0;
            lockMoney = moneyRevise;
        } else if ("subLock".equals(resetType)) {
            if (moneyRevise > lockAmountBefore) {
                throw new YamiShopBindException("操作失败！修正后账户锁定余额小于0。");
            }
            changeMoney = 0;
            lockMoney = Arith.sub(0, moneyRevise);
        } else if ("moneryToFreeze".equals(resetType)) {//余额转到冻结
            if (moneyRevise > amountBefore) {
                throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
            }
            changeMoney = Arith.sub(0, moneyRevise);
            freezeMoney = moneyRevise;
        } else if ("freezeToMoney".equals(resetType)) {
            if (moneyRevise > freezeAmountBefore) {
                throw new YamiShopBindException("操作失败！修正后账户冻结余额小于0。");
            }
            changeMoney = moneyRevise;
            freezeMoney = Arith.sub(0, moneyRevise);
        } else {
            throw new YamiShopBindException("请选择转移类型");
        }
        map.put("changeMoney", changeMoney);
        map.put("lockMoney", lockMoney);
        map.put("freezeMoney", freezeMoney);
        return map;
    }

    @Override
    public boolean checkSafeword(String safeword, String partyId) {

        return false;
    }

    @Override
    @Transactional
    public User register(String userName, String password, String userCode, int type, boolean robot) {
        User recomUser = null;
        int userLevel = 1;
        //推荐人
        if (!robot) {

            recomUser = findUserByUserCode(userCode);
            if ("true".equals(this.sysparaService.find("register_need_usercode").getSvalue())) {
                if (StrUtil.isEmpty(userCode)) {
                    throw new YamiShopBindException("请输入推荐码");
                }
                if (null == recomUser) {
                    throw new YamiShopBindException("请输入正确的推荐码");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
                if (recomUser.getStatus() == 0) {
                    throw new YamiShopBindException("推荐人无权限推荐");
                }
            }
            if (null != recomUser) {
                userLevel++;
//                userLevel = getUserRecomLevel(userLevel, recomUser.getUserId());
            }
        }
        if (findByUserName(userName) != null) {
            throw new YamiShopBindException("用户名重复");
        }
        User user = null;
        // 手机
        if (type == 1) {
            if (!isValidPhone(userName)) {
                throw new YamiShopBindException("手机号格式不正常");
            }
            user = findByUserMobile(userName);
            if (user != null) {
                throw new YamiShopBindException("手机号已存在");
            }
            user = new User();
            user.setUserName(userName);
            user.setUserMobile(userName);
            user.setUserMobileBind(true);
        }
        // 邮箱
        if (type == 2) {
            if (!isValidEmail(userName)) {
                throw new YamiShopBindException("not a valid Email!");
            }
            user = findByEmail(userName);
            if (user != null) {
                throw new YamiShopBindException("邮箱已存在");
            }
            user = new User();
            user.setMailBind(true);
            user.setUserMail(userName);
        }
        // 用户名
        if (type == 3 || type >= 4) {
            user = findByUserName(userName);
            if (user != null) {
                throw new YamiShopBindException("账号已存在");
            }
            if (type == 3 && !isValidUsername(userName)) {
                throw new YamiShopBindException("用户名不合法");
            }
            user = new User();
            user.setUserName(userName);

        }
        if (user == null) {
            throw new YamiShopBindException("注册失败");
        }
        Date now = new Date();
        int ever_user_level_num = sysparaService.find("ever_user_level_num").getInteger();
        int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
        user.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // if(ever_user_level_num_custom > 0){
        //     user.setUserLevel(ever_user_level_num_custom * 10 + ever_user_level_num);
        // }else{
        //     user.setUserLevel(userLevel);
        // }
        user.setSafePassword(passwordEncoder.encode("000000"));
        user.setLoginPassword(password);
        user.setUserName(userName);
        user.setStatus(1);
        user.setRoleName(robot ? UserConstants.SECURITY_ROLE_ROBOT : UserConstants.SECURITY_ROLE_MEMBER);
        user.setUserRegip(IPHelper.getIpAddr());
        user.setUserLastip(user.getUserRegip());
        user.setUserCode(getUserCode());
        user.setCreateTime(now);
        
        // 借贷状态 1正常 2禁止
        user.setLoanStatus(1);
        // 已贷金额(借贷)
        user.setLoanAlreadyAmount(BigDecimal.ZERO);
        // 可贷金额(借贷) - 从系统配置获取
        Syspara loanMaxAmount = this.sysparaService.find("LOAN_MAX_AMOUNT");
        user.setLoanCanAmount(loanMaxAmount != null && loanMaxAmount.getBigDecimal() != null 
            ? loanMaxAmount.getBigDecimal() 
            : BigDecimal.ZERO);
        // 是否老客户 1老客户 2新客户
        // 查询老客户表，如果邮箱或手机号存在则标记为老客户
        String email = user.getUserMail();
        String phone = user.getUserMobile();
        boolean isOld = tzUserOldService.isOldUser(email, phone);
        user.setIsOldUser(isOld ? 1 : 2);
        // 购买量化机器状态 1正常 2禁止
        user.setCreateRobotStatus(1);
        // 提币状态 1正常 2禁止
        user.setTxState(1);
        // 期权预设结果 - 未设置
        user.setOptionPreResult(0);
        
        save(user);

        //1.保存钱包记录
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getUserId());
        wallet.setCreateTime(now);
        walletService.save(wallet);
        //
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_SECURITY);
        log.setLog("用户注册,ip[" + user.getUserRegip() + "]");
        log.setUserId(user.getUserId());
        log.setUsername(user.getUserName());
        logService.save(log);
        if (recomUser != null) {
            //推荐人
            UserRecom userRecom = new UserRecom();
            userRecom.setCreateTime(now);
            userRecom.setUserId(recomUser.getUserId());
            userRecom.setRecomUserId(user.getUserId());
            userRecomService.save(userRecom);
            user.setUserRecom(recomUser.getUserId());
            updateById(user);
        }
        //推荐人
//        if (StrUtil.isNotBlank(userCode)) {
//            User recomUser = findUserByUserCode(userCode);
//            if ("true".equals(this.sysparaService.find("register_need_usercode").getSvalue())) {
//                if (null == recomUser) {
//                    throw new YamiShopBindException("推荐码不正确");
//                }
//                if (UserConstants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
//                    throw new YamiShopBindException("推荐人无权限推荐");
//                }
//                if (recomUser.getStatus() == 0) {
//                    throw new YamiShopBindException("推荐人无权限推荐");
//                }
//                UserRecom userRecom = new UserRecom();
//                userRecom.setCreateTime(now);
//                userRecom.setUserId(user.getUserId());
//                userRecom.setRecomUserId(recomUser.getUserId());
//                userRecomService.save(userRecom);
//                user.setUserRecom(recomUser.getUserId());
//                updateById(user);
//            }
//        }
        userDataService.saveRegister(user.getUserId());
        // 用户注册自动赠送金额
        String register_gift_coin = this.sysparaService.find("register_gift_coin").getSvalue();
        if(type == 4){
            double gift_sum = 100000;
            Syspara virtual_register_gift_coin_sys = this.sysparaService.find("virtual_register_gift_coin");
            if(virtual_register_gift_coin_sys != null){
                gift_sum = virtual_register_gift_coin_sys.getDouble();
            }
            userDataService.saveGiftMoneyHandle(user.getUserId(), gift_sum);
            this.walletService.update(wallet.getUserId(), gift_sum);
        }
        else if (!"".equals(register_gift_coin) && register_gift_coin != null) {
            String[] register_gift_coins = register_gift_coin.split(",");
            String gift_symbol = register_gift_coins[0];
            double gift_sum = Double.valueOf(register_gift_coins[1]);
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = walletService.findByUserId(user.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }
                userDataService.saveGiftMoneyHandle(user.getUserId(), gift_sum);
                this.walletService.update(wallet.getUserId(), gift_sum);
                // 保存账变日志
//                MoneyLog moneyLog = new MoneyLog();
//                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//                moneyLog.setAmountBefore(new BigDecimal(amount_before));
//                moneyLog.setAmount(new BigDecimal(gift_sum));
//                moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getMoney().doubleValue(), gift_sum)));
//                moneyLog.setUserId(user.getUserId());
//                moneyLog.setWalletType(gift_symbol.toUpperCase());
//                moneyLog.setContentType(Constants.MONEYLOG_CONTENT_RECHARGE);
//                moneyLog.setLog("用户注册自动赠送金额");
//                moneyLogService.save(moneyLog);
            } else {
                List<WalletExtend> walletExtends = this.walletExtendService.findByUserIdAndWallettype(user.getUserId(), gift_symbol);
                WalletExtend walletExtend;
                if (CollectionUtils.isEmpty(walletExtends)) {
                    walletExtend = new WalletExtend();
                } else {
                    walletExtend = walletExtends.get(0);
                }
                if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
                    throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
                }
                walletExtend.setAmount(Arith.add(walletExtend.getAmount(), gift_sum));
                if (!walletExtendService.saveOrUpdate(walletExtend)) {
                    throw new YamiShopBindException("操作钱包失败！");
                }
            }
        }
        return user;
    }

    // 手机号校验
    private boolean isValidPhone(String username) {
        Pattern p = Pattern.compile("[0-9]*");
        return p.matcher(username).matches();
    }

    // 邮箱校验
    private boolean isValidEmail(String username) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(username)
                .matches();
    }

    // 用户名校验
    private boolean isValidUsername(String username) {
        String regex = "^[A-Za-z]\\w{5,29}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    @Override
    public void setSafeword(String userId, String safePassword) {
        User user = getById(userId);
        if (user == null) {
            throw new YamiShopBindException("当前登录账号不存在!");
        }
        // 通过此接口设置过资金密码，updateTime 一定不为空
        if (StrUtil.isNotBlank(user.getSafePassword()) && user.getUpdateTime() != null) {
            throw new YamiShopBindException("fund password has been set");
        }
        user.setSafePassword(safePassword);
        user.setUpdateTime(new Date());
        updateById(user);
    }

    @Override
    public User findByEmail(String email) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserMail, email));
        return user;
    }

    @Override
    public User findByUserName(String userName) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        return user;
    }

    @Override
    public User findByUserId(String userId) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        return user;
    }

    @Override
    /**
     * DAPP/交易所 修改余额 减少
     */
    public void saveResetCreateWithdraw(String partyId, double money_revise, String safeword, String operator_name, String reset_type, String ip, String coin_type) {

        // money_revise为负值
        if (money_revise == 0 || coin_type == "") {
            return;
        }

        if ("usdt".equals(coin_type)) {
            // 交易所修改usdt

            User party = getById(partyId);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

            Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
            double amount_before = wallet.getMoney().doubleValue();
            if (wallet.getMoney().doubleValue() < money_revise) {
                throw new BusinessException("余额不足");
            }
            if (Arith.add(money_revise, wallet.getMoney().doubleValue()) < 0.0D) {
                throw new BusinessException("操作失败！修正后账户余额小于0。");
            }


            // 更新金额
            this.walletService.update(wallet.getUserId().toString(), money_revise);

            // 账变日志
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amount_before));
            moneyLog.setAmount(new BigDecimal(money_revise));
            moneyLog.setAmountAfter(new BigDecimal(Arith.add(wallet.getMoney().doubleValue(), money_revise)));
            moneyLog.setUserId(partyId);
            moneyLog.setWalletType(Constants.WALLET);
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_WITHDRAW);

            // 钱包日志
            WalletLog walletLog = new WalletLog();
            walletLog.setCategory("withdraw");
            walletLog.setPartyId(partyId);
            walletLog.setStatus(1);
            walletLog.setAmount(money_revise);
            walletLog.setWallettype(Constants.WALLET);
            this.walletLogService.save(walletLog);
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(party.getUserName());
            log.setOperator(operator_name);
            log.setUserId(partyId);
            log.setLog("ip:" + ip + ",手动减少充值金额，修改币种[usdt]，" + "修改数量[" + money_revise + "]");
            moneyLog.setLog("手动减少充值金额，减少余额不记录报表");

            this.moneyLogService.save(moneyLog);
            this.logService.save(log);

        } else {
            // 交易所修改btc、eth；DAPP修改质押账户（USDT）【USDT_DAPP】；DAPP演示用户修改DAPP余额【USDT_USER】；

            User party = getById(partyId);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

            WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coin_type);
            double amount_before = walletExtend.getAmount();
            if (walletExtend.getAmount() < money_revise) {
                throw new BusinessException("余额不足");
            }
            if (Arith.add(money_revise, walletExtend.getAmount()) < 0.0D) {
                throw new BusinessException("操作失败！修正后[" + coin_type.toUpperCase() + "]账户余额小于0。");
            }


            // 更新金额
            this.walletService.updateExtend(walletExtend.getPartyId().toString(), coin_type, money_revise);

            // 账变日志
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amount_before));
            moneyLog.setAmount(new BigDecimal(money_revise));
            moneyLog.setAmountAfter(new BigDecimal(Arith.add(walletExtend.getAmount(), money_revise)));
            moneyLog.setUserId(partyId);
            moneyLog.setWalletType(coin_type.toUpperCase());
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_WITHDRAW);

            // 钱包日志
            WalletLog walletLog = new WalletLog();
            walletLog.setCategory("withdraw");
            walletLog.setPartyId(partyId);
            walletLog.setStatus(1);
            walletLog.setAmount(money_revise);
            walletLog.setWallettype(coin_type.toUpperCase());
            this.walletLogService.save(walletLog);


            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(party.getUserName());
            log.setOperator(operator_name);

            String coin_str = "";
            if ("USDT_DAPP".equals(coin_type)) {
                coin_str = "[质押账户(USDT)]";
            }
            if ("USDT_USER".equals(coin_type)) {
                coin_str = "[用户钱包USDT映射]";
            }

            log.setLog("ip:" + ip + ",手动减少充值金额，修改币种[" + coin_type + "]" + coin_str + "，" + "修改数量[" + money_revise + "]");
            moneyLog.setLog("手动减少充值金额");

            this.moneyLogService.save(moneyLog);
            this.logService.save(log);
        }
    }

    @Override
    public User findByUserMobile(String mobile) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobile));
    }

    @Override
    public User login(String username, String password) {
        User user = findByUserName(username);
        if (user == null) {
            throw new YamiShopBindException("用户不存在");
        }
        if (!user.isLoginAuthority()) {
            log.info("登录限制{}", user.isLoginAuthority());
            throw new YamiShopBindException("登录失败");
        }
        if (!passwordEncoder.matches(password, user.getLoginPassword())) {
            throw new YamiShopBindException("密码不正确");
        }
        user.setUserLasttime(new Date());
        updateById(user);
        return user;
    }

    /**
     * 根据已验证的电话号码获取Party对象
     *
     * @param phone 电话号码
     * @return 用户对象
     */
    @Override
    public User findPartyByVerifiedPhone(String phone) {
        if (null == phone) return null;
        List<User> list = list(Wrappers.<User>query().lambda().eq(User::getUserMobile, phone).eq(User::isUserMobileBind, true));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 获取用户系统等级： 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
     */
    public int getUserLevelByAuth(User user) {
        int userLevel = 1;
        if (user.isMailBind() || user.isUserMobileBind() || user.isGoogleAuthBind()) {
            if (user.isRealNameAuthority()) {
                if (user.isHighlevelAuthority()) {
                    userLevel = 4;
                } else {
                    userLevel = 3;
                }
            } else {
                userLevel = 2;
            }
        } else {
            userLevel = 1;
        }
        return userLevel;
    }

    @Override
    public void updateUserWallt(String userId, BigDecimal moneyRevise, int accountType, String coinType) {


    }

    @Override
    public List<Map<String, String>> getTypeListById(String userId) {
        User currentUser = this.findByUserId(userId);
        return getTypeListByUser(currentUser);
    }

    @Override
    public List<Map<String, String>> getTypeListByUser(User currentUser) {
        List<Map<String,String>> typeList = new ArrayList<>();
        if (currentUser == null) {
            return typeList;
        }
        Map<String,String> verifcodeTypeMap = null;
        // 短信类型
        if(StringUtils.isNotEmpty(currentUser.getUserMobile()) && currentUser.isUserMobileBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", currentUser.getUserMobile());
            verifcodeTypeMap.put("verifcode_type", "1");
            typeList.add(verifcodeTypeMap);
        }

        // 邮箱类型
        if(StringUtils.isNotEmpty(currentUser.getUserMail()) && currentUser.isMailBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", currentUser.getUserMail());
            verifcodeTypeMap.put("verifcode_type", "2");
            typeList.add(verifcodeTypeMap);
        }

        // 谷歌类型
        if(StringUtils.isNotEmpty(currentUser.getGoogleAuthSecret()) && currentUser.isGoogleAuthBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", "");
            verifcodeTypeMap.put("verifcode_type", "3");
            typeList.add(verifcodeTypeMap);
        }
        return typeList;
    }

    @Override
    public void checkCode(String userId, String verifcode_type, String verifcode_value) {
        User currentUser = this.getById(userId);
        if (StringUtils.isEmptyString(verifcode_type)) {
            throw new YamiShopBindException("验证码方式不能为空");
        }
        if (StringUtils.isEmptyString(verifcode_value)) {
            throw new YamiShopBindException("验证码不能为空");
        }
        List<Map<String, String>> typeList = this.getTypeListByUser(currentUser);
        Optional<Map<String, String>> optional =
            typeList.stream().filter(map -> verifcode_type.equals(map.get("verifcode_type"))).findFirst();
        // 手机、邮件验证
        if ("1".equals(verifcode_type) || "2".equals(verifcode_type)) {
            if (!optional.isPresent()) {
                String msg = "1".equals(verifcode_type) ? "请先绑定手机验证" : "请先绑定邮箱验证";
                throw new YamiShopBindException(msg);
            }
            String key = "1".equals(verifcode_type) ? currentUser.getUserMobile() : currentUser.getUserMail();
            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
            this.identifyingCodeTimeWindowService.delAuthCode(key);
            if (null == authcode || !authcode.equals(verifcode_value)) {
                throw new YamiShopBindException("验证码不正确");
            }
        } else if ("3".equals(verifcode_type)) {
            if (!optional.isPresent()) {
                throw new YamiShopBindException("请绑定谷歌验证器");
            }
            // 谷歌验证器
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            ga.setWindowSize(5);
            boolean flag = ga.check_code(currentUser.getGoogleAuthSecret(), Long.parseLong(verifcode_value), t);
            if (!flag) {
                throw new YamiShopBindException("验证码不正确");
            }
        } else {
            throw new YamiShopBindException("The current verification method is invalid");
        }
    }

    public double recharge(String id) {
        // 用户注册自动赠送金额
        String register_gift_coin = "usdt,100000";

        String[] register_gift_coins = register_gift_coin.split(",");
        String gift_symbol = register_gift_coins[0];
        double gift_sum = Double.valueOf(register_gift_coins[1]);

        // 用户注册自动赠送金额
        if ("usdt".equals(gift_symbol)) {
            Wallet walletExtend = walletService.findByUserId(id);
            double amount_before = walletExtend.getMoney().doubleValue();
            if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
            }
            userDataService.saveGiftMoneyHandle(id, gift_sum);
            this.walletService.update(id, gift_sum);
        } else {
            List<WalletExtend> walletExtends = this.walletExtendService.findByUserIdAndWallettype(id, gift_symbol);
            WalletExtend walletExtend;
            if (CollectionUtils.isEmpty(walletExtends)) {
                walletExtend = new WalletExtend();
            } else {
                walletExtend = walletExtends.get(0);
            }
            double amount_before = walletExtend.getAmount();
            if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
                throw new YamiShopBindException("操作失败！修正后账户余额小于0。");
            }
            walletExtend.setAmount(Arith.add(walletExtend.getAmount(), gift_sum));
            if (!walletExtendService.saveOrUpdate(walletExtend)) {
                throw new YamiShopBindException("余额不足");
            }
        }
        return gift_sum;
    }
}
