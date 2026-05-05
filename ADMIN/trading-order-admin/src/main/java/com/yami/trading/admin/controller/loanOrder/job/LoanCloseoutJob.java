package com.yami.trading.admin.controller.loanOrder.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yami.trading.admin.controller.loanOrder.LoanConstants;
import com.yami.trading.admin.controller.loanOrder.LoanOrderService;
import com.yami.trading.bean.loanOrder.LoanOrder;
import com.yami.trading.common.util.LockFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * LoanCloseoutJob - 借贷订单强平计算任务
 *
 * 功能说明：
 * 该任务负责检测所有借贷订单的健康状态，当质押率低于强平线时触发强平操作
 * 质押率 = 质押物价值 / 债务价值，当质押率过低时需要平仓以偿还债务
 *
 * 执行机制：
 * - 使用独立线程持续运行，不使用Spring的Scheduled注解
 * - 每30秒检查一次所有活跃的借贷订单
 * - 使用分布式锁LockFilter防止多节点重复处理同一订单
 *
 * 处理流程：
 * 1. 从缓存获取所有活跃借贷订单
 * 2. 过滤掉已结算和已强平的订单
 * 3. 对每个订单计算当前质押率
 * 4. 如果质押率低于强平阈值，触发强平操作
 * 5. 将已处理订单从缓存移除
 */
@Component
@Slf4j
public class LoanCloseoutJob implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(LoanCloseoutJob.class);

	@Autowired
	protected LoanOrderService loanOrderService;

	private volatile boolean running = false;
	private volatile Thread workerThread;

	@EventListener(ApplicationReadyEvent.class)
	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		Thread thread = new Thread(this, "LoanCloseoutJob");
		thread.setDaemon(true);
		workerThread = thread;
		thread.start();
		logger.info("LoanCloseoutJob started");
	}

	public synchronized void stop() {
		running = false;
		Thread thread = workerThread;
		if (thread != null) {
			thread.interrupt();
		}
	}

	@PreDestroy
	public void destroy() {
		stop();
	}

	@Override
	public void run() {
		try {
			while (running && !Thread.currentThread().isInterrupted()) {
				try {
					List<String> list = new ArrayList<>();
					List<LoanOrder> orders = loanOrderService.cacheOrders();
					if (orders != null && !orders.isEmpty()) {
						for (LoanOrder order : orders) {
							if (LoanConstants.PLEDGE_ORDER_STATE_SETTLE == order.getState()
									|| LoanConstants.PLEDGE_ORDER_STATE_CLOSEOUT == order.getState()) {
								list.add(order.getUuid());
								continue;
							}
							if (!running || Thread.currentThread().isInterrupted()) {
								break;
							}
							boolean lock = false;
							try {
								if (!LockFilter.add(order.getUuid())) {
									continue;
								}
								lock = true;
								Map<String, Double> rateMap = loanOrderService.calculatePledgeRate(
										order.getPledgeCurrency(),
										order.getDebt_amount(),
										order.getPledge_amount());
								if (rateMap != null && rateMap.get("pledgeRate") >= LoanConstants.PLEDGE_RATE_CLOSEOUT) {
									loanOrderService.updateCloseout(order, rateMap.get("pledgeRate"));
									list.add(order.getUuid());
								}
							} catch (Throwable t) {
								if (!running) {
									break;
								}
								logger.error("LoanCloseoutJob taskExecutor.execute() fail", t);
							} finally {
								if (lock) {
									LockFilter.remove(order.getUuid());
								}
							}
						}
						if (!list.isEmpty()) {
							loanOrderService.cacheRemoveOrders(list);
						}
					}
				} catch (Throwable e) {
					if (!running) {
						break;
					}
					logger.error("LoanCloseoutJob taskExecutor.execute() fail", e);
				}
				if (!sleepQuietly(30_000L)) {
					break;
				}
			}
		} finally {
			synchronized (this) {
				if (Thread.currentThread() == workerThread) {
					workerThread = null;
				}
			}
			logger.info("LoanCloseoutJob stopped");
		}
	}

	private boolean sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
			return running;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public void setLoanOrderService(LoanOrderService loanOrderService) {
		this.loanOrderService = loanOrderService;
	}
}
