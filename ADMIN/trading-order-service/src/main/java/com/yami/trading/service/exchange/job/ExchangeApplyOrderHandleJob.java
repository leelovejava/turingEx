package com.yami.trading.service.exchange.job;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.purchasing.dto.ExchangeLock;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 委托单进入市场
 */

@Slf4j
@Component
public class ExchangeApplyOrderHandleJob implements Runnable {
    @Autowired
    ExchangeApplyOrderService exchangeApplyOrderService;
    @Autowired
    DataService dataService;
    @Autowired
    ItemService itemService;
    @Autowired
    SysparaService sysparaService;

    public void start() {
        new Thread(this, "ExchangeApplyOrderHandleJob").start();
        log.info("现货交易委托单处理线程启动！");
    }

    @Override
    public void run() {
        // 系统启动先暂停30秒
        ThreadUtils.sleep(1000 * 30);
        while (true) {
            try {
                List<ExchangeApplyOrder> list = this.exchangeApplyOrderService.findSubmitted();
                for (int i = 0; i < list.size(); i++) {
                    ExchangeApplyOrder order = list.get(i);
                    Item bySymbol = itemService.findBySymbol(order.getSymbol());
                    if (bySymbol == null) {
                        throw new YamiShopBindException("当前币对不存在");
                    }
                    boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType());
                    if (!isOpen) {
                        continue;
                    }
                    Realtime realtime = this.dataService.realtime(order.getSymbol()).get(0);
                    // 限价单
                    if (ExchangeApplyOrder.ORDER_PRICE_TYPE_LIMIT.equals(order.getOrderPriceType())) {
                        if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
                            if (realtime.getClose() <= order.getPrice()) {
                                this.handle(order, realtime.getClose());
                            }
                        } else {
                            if (realtime.getClose() >= order.getPrice()) {
                                this.handle(order, realtime.getClose());
                            }
                        }
                    }
                    // 市价单
                    else {
                        this.handle(order, realtime.getClose());
                    }
                }
            } catch (Exception e) {
                log.error("run fail", e);
            } finally {
                ThreadUtils.sleep(1000 * 1);
            }
        }
    }

    public void handle(ExchangeApplyOrder applyOrder, Double orderPrice) {
        boolean lock = false;
        try {
            if (!ExchangeLock.add(applyOrder.getOrderNo())) {
                return;
            }
            lock = true;
            if (ExchangeApplyOrder.OFFSET_OPEN.equals(applyOrder.getOffset())) {
                // 现货交易订单
                if (null == applyOrder.getRelationOrderNo()) {
                    this.exchangeApplyOrderService.saveSpotTradOpenCreated(applyOrder, orderPrice);
                }
                // 闪兑订单
                else {
                    this.exchangeApplyOrderService.saveOpenCreated(applyOrder, orderPrice);
                }
            } else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(applyOrder.getOffset())) {
                // 现货交易订单
                if (null == applyOrder.getRelationOrderNo()) {
                    this.exchangeApplyOrderService.saveSpotTradCloseCreated(applyOrder, orderPrice);
                }
                // 闪兑订单
                else {
                    this.exchangeApplyOrderService.saveCloseCreated(applyOrder, orderPrice);
                }
            }
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                ExchangeLock.remove(applyOrder.getOrderNo());
            }
        }
    }

}

