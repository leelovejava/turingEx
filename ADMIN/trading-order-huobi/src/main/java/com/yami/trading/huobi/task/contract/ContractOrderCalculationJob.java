package com.yami.trading.huobi.task.contract;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.contract.ContractOrderCalculationService;
import com.yami.trading.service.contract.ContractOrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ContractOrderCalculationJob implements Runnable {
	
	@Autowired
	private ContractOrderService contractOrderService;
	
	@Autowired
	private ContractOrderCalculationService contractOrderCalculationService;
	
	public void start() {
		ThreadPool.getFixedTaskExecutor(1,"ContractOrderCalculationJob").execute(this);
	}
	
	@Override
	public void run() {
		log.info("持仓单盈亏计算线程启动！");
		ThreadPoolTaskExecutor taskExecutor=ThreadPool.getApplicationThreadPool();
		while(!Thread.interrupted()) {
			try {
				// 优化 TODO
				List<ContractOrder> list = contractOrderService.findSubmitted();
				Set<String> orderNos = list.stream().map(order -> order.getOrderNo()).collect(Collectors.toSet());
				
				for (String orderNo : orderNos) {
					taskExecutor.execute(() -> {
						try {
							contractOrderCalculationService.saveCalculation(orderNo);
						} catch (Throwable e) {
							log.error("error:", e);
						}
					});
				}
			} catch (Throwable e) {
				e.printStackTrace();
				log.error("run fail", e);
			} finally {
				ThreadUtils.sleep(3000);
			}
		}
	}
}
