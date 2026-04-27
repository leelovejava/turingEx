package com.yami.trading.service.exchange.job;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.purchasing.dto.ExchangeLeverLock;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeLeverApplyOrderService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * 委托单进入市场
 */

@Slf4j
@Component
public class ExchangeLeverApplyOrderHandleJob implements Runnable {
    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;
    @Autowired
    ExchangeLeverApplyOrderService exchangeLeverApplyOrderService;
    @Autowired
    private DataService dataService;

    public void run() {
        /*
         * 系统启动先暂停30秒
         */
        ThreadUtils.sleep(1000 * 30);
        while (true)
            try {
                List<ExchangeLeverApplyOrder> list =exchangeLeverApplyOrderService.findSubmitted();
                for (int i = 0; i < list.size(); i++) {
                    ExchangeLeverApplyOrder order = list.get(i);
                    List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
                    Realtime realtime = null;
                    if (realtime_list.size() > 0) {
                        realtime = realtime_list.get(0);
                    } else {
                        continue;
                    }
                    if ("limit".equals(order.getOrder_price_type())) {
                        /**
                         * 限价单
                         */
                        if ("buy".equals(order.getDirection())) {
                            /**
                             * 买涨
                             */
                            if (realtime.getClose() <= order.getPrice()) {
                                this.handle(order, realtime);
                            }
                        } else {
                            /**
                             * 买跌
                             */
                            if (realtime.getClose() >= order.getPrice()) {
                                this.handle(order, realtime);
                            }
                        }
                    } else {
                        /**
                         * 非限制，直接进入市 场
                         */
                        this.handle(order, realtime);
                    }
                }
            } catch (Exception e) {
                log.error("run fail", e);
            } finally {
                ThreadUtils.sleep(1000 * 1);
            }
    }

    public void handle(ExchangeLeverApplyOrder applyOrder, Realtime realtime) {
        boolean lock = false;
        try {
            if (!ExchangeLeverLock.add(applyOrder.getOrderNo())) {
                return;
            }
            lock = true;
            if (ExchangeApplyOrder.OFFSET_OPEN.equals(applyOrder.getOffset())) {
              exchangeLeverApplyOrderService.saveOpen(applyOrder, realtime);
            } else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(applyOrder.getOffset())) {
                exchangeLeverApplyOrderService.saveCloseSymbol(applyOrder);
            }
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                ExchangeLeverLock.remove(applyOrder.getOrderNo());
            }
        }
    }

    public void start() {
        new Thread(this, "ExchangeLeverApplyOrderHandleJob").start();
        if (log.isInfoEnabled())
            log.info("币币交易杠杆委托单处理线程启动！");
    }
}
