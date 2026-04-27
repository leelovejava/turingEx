package com.yami.trading.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.RechargeBlockchainOrderService;
import com.yami.trading.service.RechargeBonusService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.service.user.WalletLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RechargeBonusServiceImpl implements RechargeBonusService {
    private Logger logger = LoggerFactory.getLogger(RechargeBonusServiceImpl.class);
    @Autowired
    UserRecomService userRecomService;

    @Autowired
    SysparaService sysparaService;

    @Autowired
    UserService userService;


    @Autowired
    UserDataService userDataService;


    @Autowired
    WalletService walletService;

    @Autowired
    MoneyLogService moneyLogService;


    @Autowired
    WalletLogService walletLogService;


    @Override
    public void saveBounsHandle(RechargeBlockchainOrder entity, double transfer_usdt, List<RechargeBlockchainOrder> orders) {

        List<UserRecom> recom_parents = userRecomService.getParents(entity.getPartyId());
        if (recom_parents == null) {
            return;
        }
        if (recom_parents.size() == 0) {
            return;
        }
        /**
         * 上级为空则直接结束
         */

        if ("".equals(recom_parents.get(0).getRecomUserId()) || recom_parents.get(0).getRecomUserId() == null) {
            return;
        }

        /**
         * 邀请奖励是否第三代后无限返佣一代 XX% 二代XX% 三代以后 XX%
         *  true , false
         */
        boolean recharge_bonus_forever = sysparaService.find("recharge_bonus_forever").getBoolean();
        /**
         * 获取数据库奖金分成比例
         */
        String recharge_bonus_parameters = sysparaService.find("recharge_bonus_parameters").getSvalue();
        String[] recharge_bonus_array = recharge_bonus_parameters.split(",");
        double base_amount = Double.valueOf(recharge_bonus_array[0]);
        double order_usdt_amount = Arith.mul(transfer_usdt, entity.getVolume());
        /**
         * 充值奖励类型（默认1）
         * 1.下级用户每日首次充值超过分成金额则上级奖励；
         * 2.上级累计充值超过分成金额则有奖励
         */
        String recharge_bonus_type = sysparaService.find("recharge_bonus_type").getSvalue();
        if (StringUtils.isEmptyString(recharge_bonus_type) || "1".equals(recharge_bonus_type)) {
            /**
             * 如果到账usdt金额小于可分成金额，直接退出
             */
            if (order_usdt_amount < base_amount) {
                return;
            }
            /**
             * 如果今日该用户还有充值过超过分成金额的记录，则不再奖励
             */
            if (orders == null) {
                return;
            }
            if (orders.size() > 1) {
                for (int i = 0; i < orders.size(); i++) {
                    RechargeBlockchainOrder order = orders.get(i);
                    double order_amount = Arith.mul(order.getVolume(), transfer_usdt);
                    if (entity.getOrderNo().equals(order.getOrderNo())) {
                        continue;
                    }
                    if (order_amount >= base_amount && order.getSucceeded() == 1) {
                        return;
                    }

                }
            }
        }


        boolean recharge_new_bonus_button = sysparaService.find("recharge_new_bonus_button").getBoolean();

        // --start-- 12.2 新盘需求
        // 1000,10,0.05,0.03,0.003,0.003
        double first_bonus_max_num = 0d;
        if (recharge_new_bonus_button) {
            first_bonus_max_num = Double.valueOf(recharge_bonus_array[1]);
        }
        // --end-- 12.2 新盘需求
        /**
         * 判断有几个父级代理，最多不超过4个有奖励
         */
        for (int i = 0; i < recom_parents.size(); i++) {
            String parentUserId = recom_parents.get(i).getUserId();
            if (recharge_new_bonus_button) {
                // --start-- 12.2 新盘需求
                if (i >= 3) {
                    return;
                }
                // --end-- 12.2 新盘需求
            } else {
                if (i >= 4 && !recharge_bonus_forever) {
                    return;
                }
            }
            /**
             * 邀请人是正式用户和演示用户才加奖金
             */

            User party = userService.getById(recom_parents.get(i).getRecomUserId());
            if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
                continue;
            }
            /**
             * 2.上级累计充值超过分成金额则有奖励
             */
            if ("2".equals(recharge_bonus_type) && !checkRechargeBonus(party.getUserId().toString(), order_usdt_amount, base_amount)) {
                continue;
            }
//			double pip_amount = Double.valueOf(recharge_bonus_array[i + 1]);
            double pip_amount = 0d;
            if (recharge_new_bonus_button) {
                // --start-- 12.2 新盘需求
                /**
                 * 直推奖励 3%~5%，满足10人时为5%
                 */
                if (i == 0 && this.userRecomService.findRecoms(parentUserId)
                        .size() >= first_bonus_max_num) {
                    pip_amount = Double.valueOf(recharge_bonus_array[i + 2]);
                } else {
                    pip_amount = Double.valueOf(recharge_bonus_array[i + 3]);
//					 --end-- 12.2 新盘需求
                }
            } else {
                if (i >= 4) {
                    pip_amount = Double.valueOf(recharge_bonus_array[4]);
                } else {
                    pip_amount = Double.valueOf(recharge_bonus_array[i + 1]);
                }

            }
            double get_money = Arith.mul(order_usdt_amount, pip_amount);

            Wallet wallet = walletService.saveWalletByPartyId(parentUserId);
            double amount_before = wallet.getMoney().doubleValue();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
            walletService.update(wallet.getUserId().toString(), get_money);


            /*统计总返佣*/
            User parentUser = userService.findByUserId(parentUserId);
            UserData userData = new UserData();
            userData.setUserId(parentUserId);
            userData.setRolename(parentUser.getRoleName());
            userData.setRechargeRecom(get_money);
            userData.setCreateTime(new Date());
            userDataService.saveUserData(userData);

            //返佣是否是大类型-aitopit定制需求
            String type1 = Constants.MONEYLOG_CATEGORY_COIN;
//            Syspara rebate_alone = sysparaService.find("rebate_alone");
//            if(rebate_alone!=null&&rebate_alone.getBoolean()){
//                type1 = Constants.MONEYLOG_REBATE;
//            }

            /**
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(type1);
            moneyLog.setAmountBefore(new BigDecimal(amount_before));
            moneyLog.setAmount(new BigDecimal(get_money));
            moneyLog.setAmountAfter(wallet.getMoney());
            moneyLog.setLog("第" + (i + 1) + "代用户充值到账了币种" + entity.getSymbol() + "，数量" + entity.getVolume() + "，订单号["
                    + entity.getOrderNo() + "]所奖励");
            moneyLog.setUserId(parentUserId);
            moneyLog.setWalletType("USDT");
            moneyLog.setContentType(Constants.MONEYLOG_REBATE + "_" + (i + 1));
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());
            moneyLogService.save(moneyLog);

            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
            walletLog.setPartyId(parentUserId);
            walletLog.setOrderNo(entity.getOrderNo());
            walletLog.setWallettype(Constants.WALLET);
            walletLog.setStatus(1);

            walletLog.setAmount(get_money);
            walletLogService.save(walletLog);
        }
    }

    /**
     * 累计充值是否超过分成金额
     *
     * @param partyId
     * @param usdtAmount
     * @param baseAmount
     * @return
     */
    private boolean checkRechargeBonus(String partyId, double usdtAmount, double baseAmount) {
        if (usdtAmount >= baseAmount) {
            return true;
        }
        Map<String, UserData> map = userDataService.cacheByPartyId(partyId);
        double rechargeMoney = rechargeMoney(map, null, null);
        return rechargeMoney >= baseAmount;
    }

    /**
     * 时间范围内的充值总额
     *
     * @param datas
     * @param startTime
     * @param endTime
     * @return
     */
    private double rechargeMoney(Map<String, UserData> datas, String startTime, String endTime) {
        if (datas == null || datas.isEmpty())
            return 0;
        double userRecharge = 0;
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
            userRecharge = Arith.add(userdata.getRechargeUsdt(), userRecharge);
        }

        return userRecharge;
    }

    //--------------------------------------------------------------------------------------------------------
    @Override
    public void saveUsdtBounsHandle(RechargeBlockchainOrder entity, double transferUsdt, List<RechargeBlockchainOrder> orders, Map<String, String> itemMap) {


        List<UserRecom> recomParents = userRecomService.getParents(entity.getPartyId());
        if (recomParents == null) {
            return;
        }
        if (recomParents.isEmpty()) {
            return;
        }
        /**
         * 上级为空则直接结束
         */

        if ("".equals(recomParents.get(0).getRecomUserId()) || recomParents.get(0).getRecomUserId() == null) {
            return;
        }

        System.out.println("itemMap => " + JSONObject.toJSONString(itemMap));

        /**
         * 邀请奖励是否第三代后无限返佣一代 XX% 二代XX% 三代以后 XX%
         *  true , false
         */
        boolean rechargeBonusForever = sysparaService.find("recharge_bonus_forever").getBoolean();
        /**
         * 获取数据库奖金分成比例
         */
        double orderUsdtAmount = Arith.mul(transferUsdt, entity.getVolume());

        /**
         * 新版返佣(1)
         */

        double baseAmount = Double.parseDouble(itemMap.get("base_1_level_1"));
        /**
         * 充值奖励类型（默认1）
         * 1.下级用户每日首次充值超过分成金额则上级奖励；
         * 2.上级累计充值超过分成金额则有奖励
         */
        String rechargeBonusType = sysparaService.find("recharge_bonus_type").getSvalue();
        logger.info("rechargeBonusType => " + rechargeBonusType);
        if (StringUtils.isEmptyString(rechargeBonusType) || "1".equals(rechargeBonusType)) {
            /**
             * 如果到账usdt金额小于可分成金额，直接退出
             */
            if (orderUsdtAmount < baseAmount) {
                return;
            }
            /**
             * 如果今日该用户还有充值过超过分成金额的记录，则不再奖励
             */
            if (orders == null) {
                return;
            }
            if (orders.size() > 1) {
                for (int i = 0; i < orders.size(); i++) {
                    RechargeBlockchainOrder order = orders.get(i);
                    double order_amount = Arith.mul(order.getVolume(), transferUsdt);
                    if (entity.getOrderNo().equals(order.getOrderNo())) {
                        continue;
                    }
                    if (order_amount >= baseAmount && order.getSucceeded() == 1) {
                        return;
                    }

                }
            }
        }


        if (StringUtils.isEmptyString(rechargeBonusType) || "3".equals(rechargeBonusType)) {
            /**
             * 如果到账usdt金额小于可分成金额，直接退出
             */
            if (orderUsdtAmount < baseAmount) {
                return;
            }
        }


        //代理最大等级
        String[] playerLevels = itemMap.get("player_level").split("-");
        int playerLevelMax = playerLevels.length;

        /**
         * 判断有几个父级代理，最多不超过4个有奖励
         */
        for (int i = 0; i < recomParents.size(); i++) {
            {
                if (i >= 4 && !rechargeBonusForever) {
                    return;
                }
            }
            String parentUserId = recomParents.get(i).getUserId();
            /**
             * 邀请人是正式用户和演示用户才加奖金
             */

            User party = userService.getById(recomParents.get(i).getRecomUserId());
            logger.info("party => " + party);
            if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
                continue;
            }
            /**
             * 2.上级累计充值超过分成金额则有奖励
             */
            if ("2".equals(rechargeBonusType) && !checkRechargeBonus(party.getUserId(), orderUsdtAmount, baseAmount)) {
                continue;
            }
            //用户代理等级
            int userLevel = 1;
            //几级分佣
            double pipAmount = 0d;
            int playerSize = this.userRecomService.findRecoms(parentUserId).size();
            for (int j = playerLevelMax; j >= 1; j--) {
                int playerLevelSize = Integer.parseInt(playerLevels[j - 1]);
                if (playerSize >= playerLevelSize) {
                    userLevel = j;
                    break;
                }
            }
            {
                String str = "rebate_" + userLevel + "_level_" + (i + 1);
                if (i >= 4) {
                    str = "rebate_" + userLevel + "_level_4";
                }
                pipAmount = Double.parseDouble(itemMap.get(str));
                logger.info(str + "=> " + pipAmount);
            }
            double getMoney = Arith.mul(orderUsdtAmount, pipAmount);


            logger.info(parentUserId + " === getMoney => " + getMoney + "=> " + orderUsdtAmount + "=>" + pipAmount);

            Wallet wallet = walletService.saveWalletByPartyId(parentUserId);
            double amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), getMoney);

            /*统计总返佣*/
            User parentUser = userService.findByUserId(parentUserId);
            UserData userData = new UserData();
            userData.setUserId(parentUserId);
            userData.setRolename(parentUser.getRoleName());
            userData.setRechargeRecom(getMoney);
            userData.setCreateTime(new Date());
            userDataService.saveUserData(userData);


//            logger.info("setRechargeRecom => "+ recomParents.get(i).getUserId() + "===>" + getMoney);


            /**
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amountBefore));
            moneyLog.setAmount(new BigDecimal(getMoney));
            moneyLog.setAmountAfter(wallet.getMoney());
            moneyLog.setLog("第" + (i + 1) + "代用户充值到账了币种" + entity.getSymbol() + "，数量" + entity.getVolume() + "，订单号["
                    + entity.getOrderNo() + "]所奖励,用户代理等级:" + userLevel + ",代理人数:" + playerSize);
            moneyLog.setUserId(parentUserId);
            moneyLog.setWalletType("USDT");
            moneyLog.setContentType(Constants.MONEYLOG_REBATE + "_" + (i + 1));
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());
            moneyLogService.save(moneyLog);

            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
            walletLog.setPartyId(parentUserId);
            walletLog.setOrderNo(entity.getOrderNo());
            walletLog.setWallettype(Constants.WALLET);
            walletLog.setStatus(1);

            walletLog.setAmount(getMoney);
            walletLogService.save(walletLog);
        }
    }


    @Override
    public void saveFirstUsdtBounsHandle(RechargeBlockchainOrder entity, double transferUsdt, long number, String[] recharges) {
        double baseAmount = Double.parseDouble(recharges[1]);
        double pipAmount = Double.parseDouble(recharges[2]);

        List<UserRecom> recomParents = userRecomService.getParents(entity.getPartyId());
        logger.info("saveFirstUsdtBounsHandle ===> " + recomParents);
        if (recomParents == null) {
            return;
        }
        if (recomParents.isEmpty()) {
            return;
        }
        /**
         * 上级为空则直接结束
         */

        if ("".equals(recomParents.get(0).getRecomUserId()) || recomParents.get(0).getRecomUserId() == null) {
            return;
        }

        /**
         * 获取数据库奖金分成比例
         */
        double orderUsdtAmount = Arith.mul(transferUsdt, entity.getVolume());
        logger.info("saveFirstUsdtBounsHandle orderUsdtAmount ===> " + orderUsdtAmount + " ===> " + baseAmount + "====>" + number);
        //是否是首次充值,该用户还有充值过超过分成金额的记录，则不再奖励
        if (number > 0) {
            return;
        }

        int i = 0;
        {
            /**
             * 邀请人是正式用户和演示用户才加奖金
             */

            User party = userService.getById(recomParents.get(i).getRecomUserId());
            logger.info("saveFirstUsdtBounsHandle party ===> " + party);
            if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
                return;
            }
            /**
             * 2.上级累计充值超过分成金额则有奖励
             */
            if (orderUsdtAmount < baseAmount) {
                return;
            }

            //分佣比例

            double getMoney = Arith.mul(orderUsdtAmount, pipAmount);
            logger.info("pipAmount ===> " + pipAmount + "====>" + getMoney);
            Wallet wallet = walletService.saveWalletByPartyId(recomParents.get(i).getUserId());
            double amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), getMoney);

            /**
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amountBefore));
            moneyLog.setAmount(new BigDecimal(getMoney));
            moneyLog.setAmountAfter(wallet.getMoney());
//            moneyLog.setLog("用户首次充值到账了币种" + entity.getSymbol() + "，数量" + entity.getVolume() + "，订单号["
//                    + entity.getOrderNo() + "]所奖励");
            moneyLog.setLog(entity.getPartyId());
            moneyLog.setUserId(recomParents.get(i).getUserId());
            moneyLog.setWalletType("USDT");
            moneyLog.setContentType(Constants.MONEYLOG_CONTENT_FIRST_RECHARGE);
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());
            moneyLogService.save(moneyLog);

            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
            walletLog.setPartyId(recomParents.get(i).getUserId());
            walletLog.setOrderNo(entity.getOrderNo());
            walletLog.setWallettype(Constants.WALLET);
            walletLog.setStatus(1);

            walletLog.setAmount(getMoney);
            walletLogService.save(walletLog);
        }
    }


    @Override
    public void saveTradeBounsHandle(String userId, double transferUsdt, double volume, String orderNo, String symbol) {
        // INSERT INTO t_syspara(UUID, CODE, PARTY_ID, SVALUE, SORDER, STYPE, NOTES, MODIFY) VALUES ('trade_bouns', 'trade_bouns', NULL, '0.15,0.10,0.05,10000', 100, 2, 'ICE盘交易返佣:一代，二代，三代,交易量超过才能提现', 0);
        //获取配置
        Syspara tradeBouns = sysparaService.find("trade_bouns");
        if (tradeBouns == null) {
            return;
        }
        logger.info("saveTradeBounsHandle =" + tradeBouns.getSvalue());
        String[] pipAmount = tradeBouns.getSvalue().split(",");


//       //数量
//        double volume = 100;
//        //订单号
//        String orderNo = "";
//        //币种
//        String symbol = "";

        List<UserRecom> recomParents = userRecomService.getParents(userId);

        if (recomParents == null) {
            return;
        }
        if (recomParents.isEmpty()) {
            return;
        }

        //上级为空则直接结束
        if ("".equals(recomParents.get(0).getRecomUserId()) || recomParents.get(0).getRecomUserId() == null) {
            return;
        }

        //获取数据库奖金分成比例
        double orderUsdtAmount = Arith.mul(transferUsdt, volume);


        for (int i = 0; i < recomParents.size(); i++) {
            if (i >= 3) {
                return;
            }
            String parentUserId = recomParents.get(i).getUserId();

            //邀请人是正式用户和演示用户才加奖金
            User party = userService.getById(recomParents.get(i).getRecomUserId());
            if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
                continue;
            }


            //分佣比例
            double getMoney = Arith.mul(orderUsdtAmount, Double.parseDouble(pipAmount[i]));
            logger.info("pipAmount ===> " + pipAmount + "====>" + getMoney);
            Wallet wallet = walletService.saveWalletByPartyId(parentUserId);
            double amountBefore = wallet.getMoney().doubleValue();
            walletService.update(wallet.getUserId(), getMoney);

            /*统计总返佣*/
            User parentUser = userService.findByUserId(parentUserId);
            UserData userData = new UserData();
            userData.setUserId(parentUserId);
            userData.setRolename(parentUser.getRoleName());
            userData.setRechargeRecom(getMoney);
            userData.setCreateTime(new Date());
            userDataService.saveUserData(userData);

            /**
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmountBefore(new BigDecimal(amountBefore));
            moneyLog.setAmount(new BigDecimal(getMoney));
            moneyLog.setAmountAfter(wallet.getMoney());
            moneyLog.setLog("第" + (i + 1) + "代用户交易返佣到账了币种" + symbol + "，数量" + volume + "，订单号["
                    + orderNo + "]所奖励");
            moneyLog.setUserId(parentUserId);
            moneyLog.setWalletType("USDT");
            moneyLog.setContentType(Constants.MONEYLOG_REBATE + "_" + (i + 1));
            moneyLog.setCreateTime(new Date());
            moneyLog.setUpdateTime(new Date());
            moneyLogService.save(moneyLog);

            WalletLog walletLog = new WalletLog();
            walletLog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
            walletLog.setPartyId(parentUserId);
            walletLog.setOrderNo(orderNo);
            walletLog.setWallettype(Constants.WALLET);
            walletLog.setStatus(1);

            walletLog.setAmount(getMoney);
            walletLogService.save(walletLog);
        }


    }

}
