package com.yami.trading.admin.controller.loanOrder.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yami.trading.admin.controller.loanOrder.LoanConstants;
import com.yami.trading.admin.controller.loanOrder.LoanOrderService;
import com.yami.trading.bean.loanOrder.LoanOrder;
import com.yami.trading.common.util.LockFilter;
import com.yami.trading.common.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


import javax.annotation.PostConstruct;


/**
 * 定时计算强平
 *
 */
@Component
@Slf4j
public class LoanCloseoutJob implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(LoanCloseoutJob.class);

	@Autowired
	protected LoanOrderService loanOrderService;
	
	@PostConstruct
	public void start() {
		System.out.println("LoanCloseoutJob start");
		new Thread(this, "LoanCloseoutJob").start();
	}
	
	public void run() {
		while (true) {
			try {
				List<String> list = new ArrayList<>();
				List<LoanOrder> orders = loanOrderService.cacheOrders();
				if (null != orders && orders.size() > 0) {
					for (LoanOrder order : orders) {
						if (LoanConstants.PLEDGE_ORDER_STATE_SETTLE == order.getState()
								|| LoanConstants.PLEDGE_ORDER_STATE_CLOSEOUT == order.getState()) {
							// 加入移除列表
							list.add(order.getUuid());
							continue;
						}
						boolean lock = false;
						try {
							if (!LockFilter.add(order.getUuid())) {
								return;
							}
							lock = true;
							Map<String, Double> rateMap = loanOrderService.calculatePledgeRate(order.getPledgeCurrency(), order.getDebt_amount(), order.getPledge_amount());
							// logger.info("强平率=>"+rateMap.get("pledgeRate")+"<=>"+LoanConstants.PLEDGE_RATE_CLOSEOUT);
							// 达到强平率
							if (rateMap!=null && rateMap.get("pledgeRate") >= LoanConstants.PLEDGE_RATE_CLOSEOUT) {
								loanOrderService.updateCloseout(order, rateMap.get("pledgeRate"));
								// 加入移除列表
								list.add(order.getUuid());
							}
						} catch (Throwable t) {
							logger.error("LoanCloseoutJob taskExecutor.execute() fail", t);
						} finally {
							if (lock) {
								ThreadUtils.sleep(200);
								LockFilter.remove(order.getUuid());
							}
						}
					}
					
					loanOrderService.cacheRemoveOrders(list);
				}
			} catch (Throwable e) {
				logger.error("LoanCloseoutJob taskExecutor.execute() fail", e);
			}finally {
				ThreadUtils.sleep(1000 * 30);
			}
		}
	}

	public void setLoanOrderService(LoanOrderService loanOrderService) {
		this.loanOrderService = loanOrderService;
	}
	
	
}
