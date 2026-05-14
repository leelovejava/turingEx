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

    /**
     * 校验用户资金密码是否正确
     *
     * @param user          用户对象
     * @param loginSafeword 待校验的资金密码（明文）
     * @return 匹配返回 true，否则 false
     */
    @Override
    public boolean checkLoginSafeword(User user, String loginSafeword) {
        return passwordEncoder.matches(loginSafeword, user.getSafePassword());
    }

    /**
     * 分页查询用户列表，并附加当日提现限制流水信息
     *
     * @param page        分页参数
     * @param roleNames   角色名称列表，用于过滤
     * @param userCode    用户邀请码，模糊匹配
     * @param userName    用户名，模糊匹配
     * @param userMail    邮箱，模糊匹配
     * @param userMobile  手机号，模糊匹配
     * @param checkedList 其他过滤条件
     * @return 用户 DTO 分页结果，含当日提现限制流水金额
     */
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

    /**
     * 测试用：生成 BCrypt 加密密码（仅供本地调试）
     */
    public static void main(String[] args) {
        String ff = new BCryptPasswordEncoder().encode("123456");
        log.info(ff);
    }

    /**
     * 分页查询用户及其推荐人信息
     *
     * @param page        分页参数
     * @param roleNames   角色名称列表
     * @param userCode    用户邀请码
     * @param userName    用户名
     * @param lastIp      最后登录IP
     * @param checkedList 其他过滤条件
     * @param userMail    邮箱
     * @param userMobile  手机号
     * @return 用户数据 DTO 分页结果
     */
    @Override
    public Page<UserDataDto> listUserAndRecom(Page page, List<String> roleNames, String userCode, String userName, String lastIp, List<String> checkedList, String userMail, String userMobile) {
        return baseMapper.listUserAndRecom(page, roleNames, userCode,
                userName, lastIp, checkedList, userMail, userMobile);
    }

    /**
     * 通过用户ID校验资金密码
     *
     * @param userId        用户ID
     * @param loginSafeword 待校验的资金密码（明文）
     * @return 匹配返回 true，否则 false
     */
    @Override
    public boolean checkLoginSafeword(String userId, String loginSafeword) {
        User user = getById(userId);
        if (user == null) {
            // 用户不存在
            throw new YamiShopBindException("User does not exist");
        }
        return checkLoginSafeword(user, loginSafeword);
    }

    /**
     * 更新代理用户的操作权限和登录权限
     *
     * @param userId         用户ID
     * @param operaAuthority true=高级代理，false=普通代理
     * @param loginAuthority true=允许登录，false=禁止登录
     */
    @Override
    public void updateAgent(String userId, boolean operaAuthority, boolean loginAuthority) {
        String roleName = operaAuthority ? Constants.SECURITY_ROLE_AGENT : Constants.SECURITY_ROLE_AGENTLOW;
        User user = getById(userId);
        user.setStatus(loginAuthority ? 1 : 0);
        user.setRealName(roleName);
        updateById(user);
    }

    /**
     * 带缓存的用户查询，缓存有效期5分钟
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    @Override
    public User cacheUserBy(String userId) {
        String key = "user:" + userId;
        User user = RedisUtil.get(key);
        if (user == null) {
            user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
            RedisUtil.set(key, user, 5 * 60);
        }
        return user;
    }

    /**
     * 后台手动修改用户账户余额（充值/赠送），支持 USDT 及其他币种
     * <p>
     * reset_type 说明：
     * <ul>
     *   <li>change  - 赠送金额，记录赠送报表并检查赠送达标线</li>
     *   <li>recharge - 充值金额，记录充值报表</li>
     * </ul>
     *
     * @param partyId       用户ID
     * @param money_revise  修改金额（正数增加，负数减少）
     * @param safeword      操作员资金密码（当前未校验，预留）
     * @param operator_name 操作员用户名
     * @param reset_type    操作类型：change/recharge
     * @param ip            操作员IP
     * @param coin_type     币种：usdt 或其他扩展币种
     */
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

                // 手动添加赠送金额
                log.setLog("ip:" + ip + ", manually add gift amount, currency[usdt], amount[" + money_revise + "]");
                moneyLog.setLog("Manually add gift amount");

                this.checkGiftUserLine(party);
            }

            // recharge--添加充值金额
            if ("recharge".equals(reset_type)) {

                // 只有正式用户才需要记录报表
                if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
                    this.userDataService.saveRechargeHandle(partyId, money_revise, "usdt");
                }

                // 手动添加充值金额（USDT）
                log.setLog("ip:" + ip + ", manually add recharge amount, currency[usdt], amount[" + money_revise + "]");
                moneyLog.setLog("Manually add recharge amount");
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

                // 手动添加充值金额（其他币种）
                log.setLog("ip:" + ip + ", manually add recharge amount, currency[" + coin_type + "]" + coin_str + ", amount[" + money_revise + "]");
                moneyLog.setLog("Manually add recharge amount");
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

    /**
     * 统计今日新注册的正式用户数量
     *
     * @param userIds 限定统计范围的用户ID列表，为空则统计全部
     * @return 今日注册用户数
     */
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

    /**
     * 填充用户手机号信息并标记手机已绑定
     *
     * @param phone 手机号
     * @param party 用户对象（直接修改，不持久化）
     */
    public void fillPhone(String phone, User party) {
        party.setUserMobile(phone);
        party.setUserMobileBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
    }

    /**
     * 保存用户手机号绑定并持久化到数据库
     *
     * @param phone   手机号
     * @param partyId 用户ID
     */
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

    /**
     * 用户退出登录，清除在线状态
     *
     * @param userId 用户ID
     */
    @Override
    public void logout(String userId) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return;
        }
        onlineUserService.del(userId);
    }

    /**
     * 分页查询代理商统计数据，包含下级代理数、正式用户数及各类交易汇总
     *
     * @param current   当前页码
     * @param size      每页条数
     * @param startTime 统计开始时间
     * @param endTime   统计结束时间
     * @param userName  代理用户名，模糊匹配
     * @param userIds   限定查询范围的用户ID列表
     * @return 代理统计分页结果
     */
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

    /**
     * 计算代理统计数据中的总收益、总手续费、交易盈亏等汇总字段
     * 各收益字段取负值（平台视角：用户盈利=平台亏损）
     *
     * @param datas 代理统计数据列表，直接修改原集合
     */
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

    /**
     * 按时间范围过滤用户数据列表
     *
     * @param datas     用户数据 Map（key=日期字符串）
     * @param startTime 开始日期（yyyyMMdd），为空则不限
     * @param endTime   结束日期（yyyyMMdd），为空则不限
     * @return 过滤后的用户数据列表
     */
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

    /**
     * 汇总指定用户列表在时间范围内的所有交易数据
     *
     * @param children  用户ID列表（正式用户）
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return 汇总结果 Map，包含充提、永续、理财、币币、交割、矿机等各项数据
     */
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

    /**
     * 将多个用户数据累加到汇总结果中
     * 若 item_result 已有数据则累加，否则直接赋值
     *
     * @param item_result 已有汇总结果（可为空 Map）
     * @param datas       待累加的用户数据列表
     * @return 累加后的汇总结果 Map
     */
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

    /**
     * 用户名注册（不需要验证码），支持推荐码、注册赠送金额
     *
     * @param username     用户名
     * @param password     登录密码（明文）
     * @param recoUserCode 推荐人邀请码
     * @param safeword     资金密码（明文）
     * @return 注册成功的用户对象
     */
    @Override
    @Transactional
    public User saveRegisterUsername(String username, String password, String recoUserCode, String safeword) {
        User party_reco = findUserByUserCode(recoUserCode);
//		用户注册是否需要推荐码
        if ("true".equals(sysparaService.find("register_need_usercode").getSvalue())) {
            if (StringUtils.isNotEmpty(recoUserCode)) {
                if (party_reco == null) {
                    // 请输入正确的推荐码
                    throw new YamiShopBindException("Please enter a valid referral code");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRoleName())) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
                }
                if (!party_reco.isEnabled()) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
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
            // 用户名重复
            throw new YamiShopBindException("Username already exists");
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
            if (register_gift_coins.length < 2 || StringUtils.isEmptyString(register_gift_coins[0])) {
                log.warn("Invalid syspara register_gift_coin config: [{}]", register_gift_coin);
                return party;
            }
            String gift_symbol = register_gift_coins[0];
            double gift_sum;
            try {
                gift_sum = Double.parseDouble(register_gift_coins[1]);
            } catch (Exception e) {
                log.warn("Invalid register_gift_coin amount: [{}]", register_gift_coin);
                return party;
            }
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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

    /**
     * 根据邮箱查询用户（不要求邮箱已验证）
     *
     * @param email 邮箱地址
     * @return 用户对象，不存在返回 null
     */
    public User findPartyByEmail(String email) {
        if (null == email) {
            return null;
        }
        List<User> list = list(Wrappers.<User>query().lambda().eq(User::getUserMail, email));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 手机/邮箱注册，需要验证码校验，支持推荐码和注册赠送金额
     * <p>
     * type 说明：1=手机注册，2=邮箱注册
     * 万能验证码：000000（测试用）
     *
     * @param username  手机号或邮箱
     * @param password  登录密码（明文）
     * @param usercode  推荐人邀请码
     * @param safeword  资金密码（明文）
     * @param verifcode 验证码
     * @param type      注册类型：1=手机，2=邮箱
     */
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
        if (!"000000".equals(verifcode) && ((authcode == null) || (!authcode.equals(verifcode)))) {
            // 验证码不正确
            throw new YamiShopBindException("Verification code is incorrect");
        }
        if ("true".equals(this.sysparaService.find("register_need_usercode").getSvalue())) {
            if (StringUtils.isNotEmpty(usercode)) {
                if (null == party_reco) {
                    // 推荐码不正确
                    throw new YamiShopBindException("Referral code is incorrect");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRoleName())) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
                }
                if (!party_reco.isEnabled()) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
                }
            }
        }
//        if ("true".equals(this.sysparaService.find("register_need_usercode_turn").getSvalue())) {
//            if (!party_reco.getRegister_usercode()) {
//                throw new BusinessException("推荐人无权限推荐");
//            }
//        }
        if (findByUserName(username) != null) {
            // 用户名重复
            throw new YamiShopBindException("Username already exists");
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
                // 请输入正确的手机号码
                throw new YamiShopBindException("Please enter a valid phone number");
            }

            fillPhone(username, party);
            this.save(party);
        } else {
            // 邮箱注册
            if (!Strings.isEmail(username)) {
                // 请输入正确的邮箱地址
                throw new YamiShopBindException("Please enter a valid email address");
            }
            if (findPartyByEmail(username) != null) {
                // 邮箱已重复
                throw new YamiShopBindException("Email already exists");
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
            if (register_gift_coins.length < 2 || StringUtils.isEmptyString(register_gift_coins[0])) {
                log.warn("Invalid syspara register_gift_coin config: [{}]", register_gift_coin);
                this.identifyingCodeTimeWindowService.delAuthCode(key);
                return;
            }
            String gift_symbol = register_gift_coins[0];
            double gift_sum;
            try {
                gift_sum = Double.parseDouble(register_gift_coins[1]);
            } catch (Exception e) {
                log.warn("Invalid register_gift_coin amount: [{}]", register_gift_coin);
                this.identifyingCodeTimeWindowService.delAuthCode(key);
                return;
            }
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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

    /**
     * 填充用户邮箱信息并标记邮箱已绑定
     *
     * @param email 邮箱地址
     * @param party 用户对象（直接修改，不持久化）
     */
    public void fillEmail(String email, User party) {
        party.setUserMail(email);
        party.setMailBind(true);
        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUserLevel();
//        party.setUserLevel(((int) Math.floor(userLevel / 10)) * 10 + 2);
    }

    /**
     * 保存用户邮箱绑定并持久化到数据库
     *
     * @param email   邮箱地址
     * @param partyId 用户ID
     */
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

    /**
     * 创建代理用户，支持推荐关系绑定
     *
     * @param userName       用户名
     * @param password       登录密码（明文）
     * @param safePassword   资金密码（明文）
     * @param roleName       角色名称
     * @param remarks        备注
     * @param userCode       推荐人邀请码
     * @param loginAuthority 是否允许登录
     * @return 创建成功的代理用户对象
     */
    @Override
    @Transactional
    public User saveAgentUser(String userName, String password, String safePassword, String roleName, String remarks,
                              String userCode, boolean loginAuthority) {
        User user = findByUserName(userName);
        if (user != null) {
            // 用户名重复
            throw new YamiShopBindException("Username already exists");
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
                // 推荐码不正确
                throw new YamiShopBindException("Referral code is incorrect");
            }
            if (UserConstants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
                // 推荐人无权限推荐
                throw new YamiShopBindException("Referrer has no permission to refer");
            }
            if (recomUser.getStatus() == 0) {
                // 推荐人无权限推荐
                throw new YamiShopBindException("Referrer has no permission to refer");
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

    /**
     * 后台修改用户钱包余额（充值或扣除）
     *
     * @param userId      用户ID
     * @param moneyRevise 修改金额（正数）
     * @param accountType 操作类型：1=充值，2=扣除
     * @param coinType    币种
     */
    @Override
    @Transactional
    public void updateWallt(String userId, BigDecimal moneyRevise, int accountType, String coinType) {
        User user = getById(userId);
        if (user == null) {
            // 用户不存在
            throw new YamiShopBindException("User does not exist");
        }
        if (accountType == 1) { //充值
        }
        if (accountType == 2) { //扣除
            moneyRevise = moneyRevise.negate();
        }
        walletService.updateMoney("", userId, moneyRevise, new BigDecimal(0), Constants.MONEYLOG_CATEGORY_COIN
                , coinType, accountType == 1 ? Constants.MONEYLOG_CONTENT_RECHARGE : Constants.MONEYLOG_CONTENT_WITHDRAW, "后台修改账号余额");
    }

    /**
     * 同时校验谷歌验证码和资金密码，两者均通过才放行
     *
     * @param user           用户对象
     * @param googleAuthCode 谷歌验证码
     * @param loginSafeword  资金密码（明文）
     */
    public void checkGooleAuthAndSefeword(User user, String googleAuthCode, String loginSafeword) {
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        long t = System.currentTimeMillis();
        boolean flag = ga.check_code(user.getGoogleAuthSecret(), Long.valueOf(googleAuthCode), t);
        if (!flag) {
            // 谷歌验证码错误
            throw new YamiShopBindException("Google authenticator code is incorrect");
        }
        if (!passwordEncoder.matches(loginSafeword, user.getSafePassword())) {
            // 登录人资金密码错误
            throw new YamiShopBindException("Fund password is incorrect");
        }
    }

    /**
     * 重置用户登录密码
     *
     * @param userId   用户ID
     * @param password 新登录密码（明文）
     */
    @Override
    public void restLoginPasswrod(String userId, String password) {
        User user = getById(userId);
        user.setLoginPassword(passwordEncoder.encode(password));
        updateById(user);
    }

    /**
     * 重置用户资金密码
     *
     * @param userId      用户ID
     * @param newSafeword 新资金密码（明文）
     */
    @Override
    public void restSafePassword(String userId, String newSafeword) {
        User user = getById(userId);
        user.setSafePassword(passwordEncoder.encode(newSafeword));
        updateById(user);
    }

    /**
     * 解绑用户谷歌验证器，需同时验证谷歌验证码和资金密码
     *
     * @param userId         用户ID
     * @param googleAuthCode 谷歌验证码
     * @param loginSafeword  资金密码（明文）
     */
    @Override
    public void deleteGooleAuthCode(String userId, String googleAuthCode, String loginSafeword) {
        User user = getById(userId);
        if (user == null) {
            // 参数错误
            throw new YamiShopBindException("Invalid parameter");
        }
        if (!user.isGoogleAuthBind()) {
            // 用户谷歌验证码未绑定
            throw new YamiShopBindException("Google authenticator is not bound");
        }
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        long t = System.currentTimeMillis();
        boolean flag = ga.check_code(user.getGoogleAuthSecret(), Long.valueOf(googleAuthCode), t);
        if (!flag) {
            // 谷歌验证码错误
            throw new YamiShopBindException("Google authenticator code is incorrect");
        }
        if (!passwordEncoder.matches(loginSafeword, user.getSafePassword())) {
            // 登录人资金密码错误
            throw new YamiShopBindException("Fund password is incorrect");
        }
        user.setGoogleAuthBind(false);
        user.setGoogleAuthSecret("");
        updateById(user);
    }

    /**
     * 后台手动修改用户提现限制流水金额，并记录操作日志
     *
     * @param userId        用户ID
     * @param moneyWithdraw 新的提现限制流水金额
     * @param userName      操作员用户名
     */
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
            // 请填入有效数字
            throw new YamiShopBindException("Please enter a valid number");
        }
        BigDecimal resultAmount = lastAmount.add(moneyWithdraw);
        if (moneyWithdraw.doubleValue() < 0) {
            // 修改后金额不能小于0
            throw new YamiShopBindException("Amount after modification cannot be less than 0");
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

    /**
     * 根据邀请码或用户ID查询用户
     *
     * @param userCode 邀请码或用户ID
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findUserByUserCode(String userCode) {
        return getOne(Wrappers.<User>query().lambda().eq(User::getUserCode, userCode).or().eq(User::getUserId, userCode));
    }

    /**
     * 生成用户邀请码（带并发锁，基于系统序列号随机递增）
     *
     * @return 用户邀请码字符串
     */
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

    /**
     * 生成代理用户邀请码（基于代理序列号自增）
     *
     * @return 代理用户邀请码字符串
     */
    private String getAgentUserCode() {
        Syspara syspara = sysparaService.find("agent_uid_sequence");
        int agent_uid_sequence = syspara.getInteger() + 1;
        SysparasDto sysparasDto = new SysparasDto();
        sysparasDto.setAgent_uid_sequence(String.valueOf(agent_uid_sequence));
        sysparaService.updateSysparas(sysparasDto);
        String usercode = String.valueOf(agent_uid_sequence);
        return usercode;
    }

    /**
     * 判断用户是否在线
     *
     * @param partyId 用户ID
     * @return 在线返回 true，否则 false
     */
    @Override
    public boolean isOnline(String partyId) {
        Object object = onlineUserService.get(partyId);
        if (object != null) {
            return true;
        }
        return false;
    }

    /**
     * 标记用户为在线状态
     *
     * @param partyId 用户ID
     */
    @Override
    public void online(String partyId) {
        if (StringUtils.isNullOrEmpty(partyId)) {
            return;
        }
        onlineUserService.put(partyId, new Date());
    }

    /**
     * 后台手动新增演示用户（GUEST角色），初始化钱包并记录操作日志
     *
     * @param username         用户名
     * @param password         登录密码（明文）
     * @param login_authority  是否允许登录
     * @param enabled          是否启用
     * @param remarks          备注
     * @param operatorUsername 操作员用户名
     * @param ip               操作员IP
     * @param parents_usercode 推荐人邀请码
     */
    @Override
    @Transactional
    public void saveUser(String username, String password, boolean login_authority, boolean enabled, String remarks, String operatorUsername, String ip, String parents_usercode) {
        username = username.trim();
        password = password.trim();
        if (findByUserName(username) != null) {
            // 用户名重复
            throw new YamiShopBindException("Username already exists");
        }
        /**
         * 用户code
         */
        String usercode = getUserCode();
        int userLevel = 1;
        if (!StringUtils.isNullOrEmpty(parents_usercode)) {
            User party_parents = findUserByUserCode(parents_usercode);
            if (party_parents == null) {
                // 推荐码不正确
                throw new YamiShopBindException("Referral code is incorrect");
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
                // 推荐码不正确
                throw new YamiShopBindException("Referral code is incorrect");
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

    /**
     * 后台手动修改用户账户的锁定/冻结金额，支持余额与锁定/冻结之间的相互转移
     * <p>
     * resetType 说明：
     * <ul>
     *   <li>moneryToLock   - 可用余额转入锁定</li>
     *   <li>lockToMoney    - 锁定转回可用余额</li>
     *   <li>addLock        - 直接增加锁定金额</li>
     *   <li>subLock        - 直接减少锁定金额</li>
     *   <li>moneryToFreeze - 可用余额转入冻结</li>
     *   <li>freezeToMoney  - 冻结转回可用余额</li>
     * </ul>
     *
     * @param partyId      用户ID
     * @param moneyRevise  操作金额
     * @param safeword     操作员资金密码（预留）
     * @param operatorName 操作员用户名
     * @param resetType    操作类型
     * @param ip           操作员IP
     * @param coinType     币种
     */
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

    /**
     * 计算余额/锁定/冻结金额的变化量
     * 根据操作类型校验余额是否充足，并返回各账户的变化值
     *
     * @param moneyRevise        操作金额
     * @param resetType          操作类型
     * @param amountBefore       当前可用余额
     * @param lockAmountBefore   当前锁定余额
     * @param freezeAmountBefore 当前冻结余额
     * @return Map，包含 changeMoney/lockMoney/freezeMoney 三个变化量
     */
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
            // 请选择转移类型
            throw new YamiShopBindException("Please select a transfer type");
        } else if ("moneryToLock".equals(resetType)) {//余额转到锁定
            if (moneyRevise > amountBefore) {
                // 操作失败！修正后账户余额小于0
                throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
            }
            changeMoney = Arith.sub(0, moneyRevise);
            lockMoney = moneyRevise;
        } else if ("lockToMoney".equals(resetType)) {
            if (moneyRevise > lockAmountBefore) {
                // 操作失败！修正后账户锁定余额小于0
                throw new YamiShopBindException("Operation failed! Lock balance after adjustment is less than 0");
            }
            changeMoney = moneyRevise;
            lockMoney = Arith.sub(0, moneyRevise);
        } else if ("addLock".equals(resetType)) {
            changeMoney = 0;
            lockMoney = moneyRevise;
        } else if ("subLock".equals(resetType)) {
            if (moneyRevise > lockAmountBefore) {
                // 操作失败！修正后账户锁定余额小于0
                throw new YamiShopBindException("Operation failed! Lock balance after adjustment is less than 0");
            }
            changeMoney = 0;
            lockMoney = Arith.sub(0, moneyRevise);
        } else if ("moneryToFreeze".equals(resetType)) {//余额转到冻结
            if (moneyRevise > amountBefore) {
                // 操作失败！修正后账户余额小于0
                throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
            }
            changeMoney = Arith.sub(0, moneyRevise);
            freezeMoney = moneyRevise;
        } else if ("freezeToMoney".equals(resetType)) {
            if (moneyRevise > freezeAmountBefore) {
                // 操作失败！修正后账户冻结余额小于0
                throw new YamiShopBindException("Operation failed! Freeze balance after adjustment is less than 0");
            }
            changeMoney = moneyRevise;
            freezeMoney = Arith.sub(0, moneyRevise);
        } else {
            // 请选择转移类型
            throw new YamiShopBindException("Please select a transfer type");
        }
        map.put("changeMoney", changeMoney);
        map.put("lockMoney", lockMoney);
        map.put("freezeMoney", freezeMoney);
        return map;
    }

    /**
     * 校验资金密码（当前未实现，预留接口）
     *
     * @param safeword 资金密码
     * @param partyId  用户ID
     * @return 始终返回 false
     */
    @Override
    public boolean checkSafeword(String safeword, String partyId) {

        return false;
    }

    /**
     * 通用注册方法，支持手机/邮箱/用户名/虚拟用户/机器人多种注册类型
     * <p>
     * type 说明：1=手机，2=邮箱，3=用户名，4=虚拟用户（赠送10万），>=4=机器人
     *
     * @param userName 注册账号（手机/邮箱/用户名）
     * @param password 登录密码（已加密）
     * @param userCode 推荐人邀请码
     * @param type     注册类型
     * @param robot    是否为机器人账号
     * @return 注册成功的用户对象
     */
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
                    // 请输入推荐码
                    throw new YamiShopBindException("Please enter a referral code");
                }
                if (null == recomUser) {
                    // 请输入正确的推荐码
                    throw new YamiShopBindException("Please enter a valid referral code");
                }
                if (Constants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
                }
                if (recomUser.getStatus() == 0) {
                    // 推荐人无权限推荐
                    throw new YamiShopBindException("Referrer has no permission to refer");
                }
            }
            if (null != recomUser) {
                userLevel++;
//                userLevel = getUserRecomLevel(userLevel, recomUser.getUserId());
            }
        }
        if (findByUserName(userName) != null) {
            // 用户名重复
            throw new YamiShopBindException("Username already exists");
        }
        User user = null;
        // 手机
        if (type == 1) {
            if (!isValidPhone(userName)) {
                // 手机号格式不正常
                throw new YamiShopBindException("Invalid phone number format");
            }
            user = findByUserMobile(userName);
            if (user != null) {
                // 手机号已存在
                throw new YamiShopBindException("Phone number already exists");
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
                // 邮箱已存在
                throw new YamiShopBindException("Email already exists");
            }
            user = new User();
            user.setMailBind(true);
            user.setUserMail(userName);
        }
        // 用户名
        if (type == 3 || type >= 4) {
            user = findByUserName(userName);
            if (user != null) {
                // 账号已存在
                throw new YamiShopBindException("Account already exists");
            }
            if (type == 3 && !isValidUsername(userName)) {
                // 用户名不合法
                throw new YamiShopBindException("Invalid username");
            }
            user = new User();
            user.setUserName(userName);

        }
        if (user == null) {
            // 注册失败
            throw new YamiShopBindException("Registration failed");
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
        Log logEntity = new Log();
        logEntity.setCategory(Constants.LOG_CATEGORY_SECURITY);
        logEntity.setLog("用户注册,ip[" + user.getUserRegip() + "]");
        logEntity.setUserId(user.getUserId());
        logEntity.setUsername(user.getUserName());
        logService.save(logEntity);
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
//                    // 推荐码不正确
//                    throw new YamiShopBindException("Referral code is incorrect");
//                }
//                if (UserConstants.SECURITY_ROLE_TEST.equals(recomUser.getRoleName())) {
//                    // 推荐人无权限推荐
//                    throw new YamiShopBindException("Referrer has no permission to refer");
//                }
//                if (recomUser.getStatus() == 0) {
//                    // 推荐人无权限推荐
//                    throw new YamiShopBindException("Referrer has no permission to refer");
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
        ///String register_gift_coin = this.sysparaService.find("register_gift_coin").getSvalue();
        String register_gift_coin = "";
        if (type == 4) {
            double gift_sum = 100000;
            Syspara virtual_register_gift_coin_sys = this.sysparaService.find("virtual_register_gift_coin");
            if (virtual_register_gift_coin_sys != null) {
                gift_sum = virtual_register_gift_coin_sys.getDouble();
            }
            userDataService.saveGiftMoneyHandle(user.getUserId(), gift_sum);
            this.walletService.update(wallet.getUserId(), gift_sum);
        } else if (!"".equals(register_gift_coin) && register_gift_coin != null) {
            String[] register_gift_coins = register_gift_coin.split(",");
            if (register_gift_coins.length < 2 || StringUtils.isEmptyString(register_gift_coins[0])) {
                log.warn("Invalid syspara register_gift_coin config: [{}]", register_gift_coin);
                return user;
            }
            String gift_symbol = register_gift_coins[0];
            double gift_sum;
            try {
                gift_sum = Double.parseDouble(register_gift_coins[1]);
            } catch (Exception e) {
                log.warn("Invalid register_gift_coin amount: [{}]", register_gift_coin);
                return user;
            }
            if ("usdt".equals(gift_symbol)) {
                Wallet walletExtend = walletService.findByUserId(user.getUserId());
                double amount_before = walletExtend.getMoney().doubleValue();
                if (Arith.add(gift_sum, walletExtend.getMoney().doubleValue()) < 0.0D) {
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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
                    // 操作失败！修正后账户余额小于0
                    throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
                }
                walletExtend.setAmount(Arith.add(walletExtend.getAmount(), gift_sum));
                if (!walletExtendService.saveOrUpdate(walletExtend)) {
                    // 操作钱包失败
                    throw new YamiShopBindException("Wallet operation failed");
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

    /**
     * 首次设置用户资金密码（只能设置一次，已设置则拒绝）
     *
     * @param userId       用户ID
     * @param safePassword 资金密码（已加密）
     */
    @Override
    public void setSafeword(String userId, String safePassword) {
        User user = getById(userId);
        if (user == null) {
            // 当前登录账号不存在
            throw new YamiShopBindException("Current account does not exist");
        }
        // 通过此接口设置过资金密码，updateTime 一定不为空
        if (StrUtil.isNotBlank(user.getSafePassword()) && user.getUpdateTime() != null) {
            throw new YamiShopBindException("fund password has been set");
        }
        user.setSafePassword(safePassword);
        user.setUpdateTime(new Date());
        updateById(user);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱地址
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findByEmail(String email) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserMail, email));
        return user;
    }

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findByUserName(String userName) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        return user;
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findByUserId(String userId) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        return user;
    }

    /**
     * 后台手动减少用户账户余额（提现/扣款），支持 USDT 及其他币种
     * money_revise 传负值表示减少
     *
     * @param partyId       用户ID
     * @param money_revise  减少金额（负数）
     * @param safeword      操作员资金密码（预留）
     * @param operator_name 操作员用户名
     * @param reset_type    操作类型
     * @param ip            操作员IP
     * @param coin_type     币种
     */
    @Override
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
            // 手动减少充值金额（USDT，不记录报表）
            log.setLog("ip:" + ip + ", manually reduce recharge amount, currency[usdt], amount[" + money_revise + "]");
            moneyLog.setLog("Manually reduce recharge amount, not recorded in report");

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

            // 手动减少充值金额（其他币种）
            log.setLog("ip:" + ip + ", manually reduce recharge amount, currency[" + coin_type + "]" + coin_str + ", amount[" + money_revise + "]");
            moneyLog.setLog("Manually reduce recharge amount");

            this.moneyLogService.save(moneyLog);
            this.logService.save(log);
        }
    }

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findByUserMobile(String mobile) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobile));
    }

    /**
     * 用户登录校验，验证用户名、登录权限和密码
     *
     * @param username 用户名
     * @param password 登录密码（明文）
     * @return 登录成功的用户对象
     */
    @Override
    public User login(String username, String password) {
        User user = findByUserName(username);
        if (user == null) {
            // 用户不存在
            throw new YamiShopBindException("User does not exist");
        }
        if (!user.isLoginAuthority()) {
            log.info("登录限制{}", user.isLoginAuthority());
            // 登录失败
            throw new YamiShopBindException("Login failed");
        }
        if (!passwordEncoder.matches(password, user.getLoginPassword())) {
            // 密码不正确
            throw new YamiShopBindException("Password is incorrect");
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
    /**
     * 获取用户系统认证等级
     * 1=新注册；2=手机/邮箱/谷歌其中一个已验证；3=实名认证；4=高级认证
     *
     * @param user 用户对象
     * @return 用户等级 1-4
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

    /**
     * 后台修改用户钱包余额（预留接口，当前未实现）
     *
     * @param userId      用户ID
     * @param moneyRevise 修改金额
     * @param accountType 操作类型
     * @param coinType    币种
     */
    @Override
    public void updateUserWallt(String userId, BigDecimal moneyRevise, int accountType, String coinType) {


    }

    /**
     * 根据用户ID获取可用的验证方式列表
     *
     * @param userId 用户ID
     * @return 验证方式列表，每项包含 account 和 verifcode_type
     */
    @Override
    public List<Map<String, String>> getTypeListById(String userId) {
        User currentUser = this.findByUserId(userId);
        return getTypeListByUser(currentUser);
    }

    /**
     * 根据用户对象获取可用的验证方式列表
     * verifcode_type：1=手机，2=邮箱，3=谷歌验证器
     *
     * @param currentUser 用户对象
     * @return 验证方式列表，每项包含 account 和 verifcode_type
     */
    @Override
    public List<Map<String, String>> getTypeListByUser(User currentUser) {
        List<Map<String, String>> typeList = new ArrayList<>();
        if (currentUser == null) {
            return typeList;
        }
        Map<String, String> verifcodeTypeMap = null;
        // 短信类型
        if (StringUtils.isNotEmpty(currentUser.getUserMobile()) && currentUser.isUserMobileBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", currentUser.getUserMobile());
            verifcodeTypeMap.put("verifcode_type", "1");
            typeList.add(verifcodeTypeMap);
        }

        // 邮箱类型
        if (StringUtils.isNotEmpty(currentUser.getUserMail()) && currentUser.isMailBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", currentUser.getUserMail());
            verifcodeTypeMap.put("verifcode_type", "2");
            typeList.add(verifcodeTypeMap);
        }

        // 谷歌类型
        if (StringUtils.isNotEmpty(currentUser.getGoogleAuthSecret()) && currentUser.isGoogleAuthBind()) {
            verifcodeTypeMap = new HashMap<>();
            verifcodeTypeMap.put("account", "");
            verifcodeTypeMap.put("verifcode_type", "3");
            typeList.add(verifcodeTypeMap);
        }
        return typeList;
    }

    /**
     * 校验用户验证码，支持手机/邮箱/谷歌验证器三种方式
     * verifcode_type：1=手机，2=邮箱，3=谷歌验证器
     *
     * @param userId          用户ID
     * @param verifcode_type  验证方式
     * @param verifcode_value 验证码
     */
    @Override
    public void checkCode(String userId, String verifcode_type, String verifcode_value) {
        User currentUser = this.getById(userId);
        if (StringUtils.isEmptyString(verifcode_type)) {
            // 验证码方式不能为空
            throw new YamiShopBindException("Verification type cannot be empty");
        }
        if (StringUtils.isEmptyString(verifcode_value)) {
            // 验证码不能为空
            throw new YamiShopBindException("Verification code cannot be empty");
        }
        List<Map<String, String>> typeList = this.getTypeListByUser(currentUser);
        Optional<Map<String, String>> optional =
                typeList.stream().filter(map -> verifcode_type.equals(map.get("verifcode_type"))).findFirst();
        // 手机、邮件验证
        if ("1".equals(verifcode_type) || "2".equals(verifcode_type)) {
            if (!optional.isPresent()) {
                // 请先绑定手机验证 请先绑定邮箱验证
                String msg = "1".equals(verifcode_type) ? "Please bind mobile verification first" : "Please bind email verification first";
                throw new YamiShopBindException(msg);
            }
            String key = "1".equals(verifcode_type) ? currentUser.getUserMobile() : currentUser.getUserMail();
            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
            this.identifyingCodeTimeWindowService.delAuthCode(key);
            if (!"000000".equals(verifcode_value) && (null == authcode || !authcode.equals(verifcode_value))) {
                // 验证码不正确
                throw new YamiShopBindException("Verification code is incorrect");
            }
        } else if ("3".equals(verifcode_type)) {
            if (!optional.isPresent()) {
                // 请绑定谷歌验证器
                throw new YamiShopBindException("Please bind Google authenticator first");
            }
            // 谷歌验证器
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            ga.setWindowSize(5);
            boolean flag = ga.check_code(currentUser.getGoogleAuthSecret(), Long.parseLong(verifcode_value), t);
            if (!flag) {
                // 验证码不正确
                throw new YamiShopBindException("Verification code is incorrect");
            }
        } else {
            throw new YamiShopBindException("The current verification method is invalid");
        }
    }

    /**
     * 给指定用户充值固定金额（usdt:100000 或其他扩展币种），用于测试/虚拟用户初始化
     *
     * @param id 用户ID
     * @return 实际充值金额
     */
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
                // 操作失败！修正后账户余额小于0
                throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
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
                // 操作失败！修正后账户余额小于0
                throw new YamiShopBindException("Operation failed! Balance after adjustment is less than 0");
            }
            walletExtend.setAmount(Arith.add(walletExtend.getAmount(), gift_sum));
            if (!walletExtendService.saveOrUpdate(walletExtend)) {
                // 余额不足
                throw new YamiShopBindException("Insufficient balance");
            }
        }
        return gift_sum;
    }

    @Override
    public List<User> listExpiredKycBonus(Date expireTime) {
        return baseMapper.listExpiredKycBonus(expireTime);
    }
}
