package com.yami.trading.admin.controller.loanOrder.job;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.loanOrder.LoanOrderService;
import com.yami.trading.bean.loanOrder.LoanOrder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


/**
 * 质押借币 利息JOB
 *
 */
@Component
@Slf4j
public class LoanInterestJob {
	
	private Logger logger = LoggerFactory.getLogger(LoanInterestJob.class);
	@Autowired
	private LoanOrderService loanOrderService;

	@Scheduled(fixedDelay = 300000, initialDelay = 5000)
	public void taskJob() {

		try {
			int pageNo = 1;
			int pageSize = 300;
			// 小于当前时间，新增的订单都不查
			Date date = new Date();
			while (true) {
				
				Page page = loanOrderService.pagedQueryInterestOrder(pageNo, pageSize, date);
				List<LoanOrder> orders = page.getRecords();
				// 分页没数据时表示已经计算结束
				if (CollectionUtils.isEmpty(orders)) {
					break;
				}
				try {
					loanOrderService.updateInterest(orders);
				} catch (Throwable e) {
					logger.error("error:", e);
				}
				logger.info("loan interest finished, count:" + orders.size());
				pageNo++;
			}
		} catch (Throwable e) {
			logger.error("PledgeGalaxyOrderProfit run fail", e);
		}
	}

}
