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


/**
 * LoanCloseoutJob - 借贷订单强平计算任务
 *
 * 功能说明：
 * 该任务负责检测所有借贷订单的健康状态，当质押率低于强平线时触发强平操作
 * 质押率 = 质押物价值 / 债务价值，当质押率过低时需要平仓以偿还债务
 *
 * 执行机制：
 * - 使用独立线程持续运行，不使用Spring的@Scheduled注解
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

	private Logger logger = LoggerFactory.getLogger(LoanCloseoutJob.class);

	@Autowired
	protected LoanOrderService loanOrderService;

	/**
	 * 启动强平计算任务
	 *
	 * 使用@PostConstruct在Bean初始化后启动独立线程
	 * 添加延迟确保数据源等依赖已完全初始化
	 */
	@PostConstruct
	public void start() {
		// 延迟启动，等待数据源等依赖初始化完成
		new Thread(() -> {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			new Thread(this, "LoanCloseoutJob").start();
		}).start();
	}

	public void run() {
		while (true) {
			try {
				List<String> list = new ArrayList<>();
				// 获取所有活跃借贷订单
				List<LoanOrder> orders = loanOrderService.cacheOrders();
				if (null != orders && orders.size() > 0) {
					for (LoanOrder order : orders) {
						// 跳过已结算和已强平的订单
						if (LoanConstants.PLEDGE_ORDER_STATE_SETTLE == order.getState()
								|| LoanConstants.PLEDGE_ORDER_STATE_CLOSEOUT == order.getState()) {
							list.add(order.getUuid());
							continue;
						}
						boolean lock = false;
						try {
							// 添加分布式锁，防止多节点重复处理
							if (!LockFilter.add(order.getUuid())) {
								return;
							}
							lock = true;
							// 计算当前质押率
							Map<String, Double> rateMap = loanOrderService.calculatePledgeRate(order.getPledgeCurrency(), order.getDebt_amount(), order.getPledge_amount());
							// 判断是否达到强平条件
							if (rateMap != null && rateMap.get("pledgeRate") >= LoanConstants.PLEDGE_RATE_CLOSEOUT) {
								// 触发强平操作
								loanOrderService.updateCloseout(order, rateMap.get("pledgeRate"));
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
					// 将已处理的订单从缓存移除
					loanOrderService.cacheRemoveOrders(list);
				}
			} catch (Throwable e) {
				logger.error("LoanCloseoutJob taskExecutor.execute() fail", e);
			} finally {
				// 每30秒检查一次
				ThreadUtils.sleep(1000 * 30);
			}
		}
	}

	public void setLoanOrderService(LoanOrderService loanOrderService) {
		this.loanOrderService = loanOrderService;
	}
}