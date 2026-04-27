package com.yami.trading.service.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeApplyOrderDto;
import com.yami.trading.bean.exchange.dto.ExchangeSymbolDto;
import com.yami.trading.bean.exchange.dto.TodayTransactionDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExchangeApplyOrderService extends IService<ExchangeApplyOrder> {

    /**
     * 生成闪兑订单
     */
    void saveCreate(ExchangeApplyOrder order);

    /**
     * 现货交易买入
     */
    void saveSpotTradOpen(ExchangeApplyOrder order);

    /**
     * 现货交易卖出
     */
    void saveSpotTradClose(ExchangeApplyOrder order);

    /**
     * 所有未处理状态的委托单
     */
    public List<ExchangeApplyOrder> findSubmitted();

    /**
     * 现货交易买入委托单成交
     */
    void saveSpotTradOpenCreated(ExchangeApplyOrder applyOrder, Double orderPrice);

    /**
     * 现货交易卖出委托单成交
     */
    void saveSpotTradCloseCreated(ExchangeApplyOrder applyOrder, Double orderPrice);

    /**
     * 闪兑买入成交
     */
    void saveOpenCreated(ExchangeApplyOrder applyOrder, Double orderPrice);

    /**
     * 闪兑卖出成交
     */
    void saveCloseCreated(ExchangeApplyOrder applyOrder, Double orderPrice);

    /**
     * 撤单
     */
    void saveCancel(String partyId, String orderNo);

    List<Map<String, Object>> getPaged(int pageNo, int size, String userId, String symbol, String type, String isAll, String startTime, String endTime, String symbolType,
                                           String orderPriceType);

    /**
     * 闪兑记录列表
     */
    List<Map<String, Object>> queryExchangeListPage(int pageNo, int size, String userId);

    ExchangeApplyOrder findByOrderNoAndPartyId(String order_no, String userId);

    Page<ExchangeApplyOrderDto> listPage(Page page, String rolename,
                                         String userName,
                                         String orderNo,
                                         String state, String offset, String symbolType, String userCode, String symbol,
                                         List<String> userIds);

    ExchangeApplyOrder findByOrderNo(String orderNo);

    /**
     * 查询现货交易持仓单列表
     */
    List<ExchangeSymbolDto> querySpotTradPositionList(String userId, String type);
    /**
     * 查询现货交易持仓单列表-全类型
     */
    List<ExchangeSymbolDto> querySpotTradPositionList(String userId);

    List<TodayTransactionDto> getTodayTransaction(String  symbol, String currentUserId,
                                                  Date startDate,
                                                  Date endDate);
}
