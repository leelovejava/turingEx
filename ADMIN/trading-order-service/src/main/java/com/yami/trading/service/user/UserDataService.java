package com.yami.trading.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.UserData;
import com.yami.trading.bean.user.dto.ChildrenLever;
import com.yami.trading.bean.user.dto.UserBenefitsDto;
import com.yami.trading.bean.user.dto.UserDataWithdrawLimitDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserDataService  extends IService<UserData> {
    public void saveUserData(UserData entity);
    public Page userAll(Page page,Date startTime,Date endTime,List<String> userIds);

    Map sumAll(Date startTime,Date endTime,List<String> userIds);

    Page listUserGenefits(Page page,  Date startTime,Date endTime, String userName,List<String> children);

    /**
     * COM盘（首页统计模板）定制需求 下盘后删除
     */
    Map<String, BigDecimal> queryUserDataByUserId(String userId);

    long countTodayRechargeUser(List<String> userIds);
    public void saveGiftMoneyHandle(String partyId, double amount);

    UserBenefitsDto daySumData( Date startTime,
                                Date endTime,List<String> userIds);
    Map<String,Object> daySumDataOld(String day,List<String> userIds);

    public List<Map<String, UserData>> cacheByPartyIds(List<String> partyIds);

    /**
     * 1、api注册 2、推荐关系更改
     */
    public void saveRegister(String userId);


    public Map<String, UserData> cacheByPartyId(String partyId);

    void saveRechargeHandle(String partyId, double amount, String symbol);
    /**
     * 合约平仓
     *
     * @param partyId
     * @param amount
     */
    public void saveClose(ContractOrder order) ;
    /**
     * 交割平仓
     *
     * @param partyId
     * @param amount
     */
    public void saveFuturesClose(FuturesOrder order);
    /**
     * 卖币
     */
    public void saveSell(ExchangeApplyOrder order);



    /**
     * 买币
     */
    void saveBuy(ExchangeApplyOrder order);

    /**
     * 提现
     *
     * @param partyId
     * @param amount
     */
    public void saveWithdrawHandle(String partyId, double amount, double amount_fee, String symbol);



    public ChildrenLever cacheChildrenLever4(String partyId);

    /**
     * 资金盘
     */
    public List<Map<String, Object>> getChildrenLevelPagedForGalaxy(int pageNo, int pageSize, String partyId, Integer level);

    List<Map<String, UserData>> findByPartyIds(List<String> children);

    /**
	 * 赎回理财产品
	 */
	public void saveSellFinance(FinanceOrder order);

    /**
     * 矿机买入
     *
     * @param order
     */
    public void saveMinerBuy(MinerOrder order);

    /**
     * 矿机赎回
     */
    public void saveMinerClose(MinerOrder order);

    /**
     * 矿机利息
     *
     * @param partyId 获利人
     * @param profit  利息
     */
    public void saveMinerProfit(String partyId, double profit);

    /**
     * 统计用户可提现额度
     *
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @param userIds  用户ID
     */
    public List<UserDataWithdrawLimitDto> withdrawLimit(Date startTime, Date endTime, List<String> userIds);

    long sumExchangeAmount(String userId);


    /**
     * 交易所-数据总览-PC端
     */
    Map<String, String> getPromoteData(String partyId, Map<String, String> data, Date startTime, Date endTime);
}
