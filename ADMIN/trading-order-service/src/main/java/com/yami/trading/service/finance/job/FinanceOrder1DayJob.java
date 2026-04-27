package com.yami.trading.service.finance.job;

import java.util.Date;
import java.util.List;

import com.yami.trading.service.finance.service.FinanceOrderLock;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.system.LogService;

import com.yami.trading.bean.model.Log;
//import org.apache.commons.logging.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinanceOrder1DayJob {
	private static org.apache.commons.logging.Log logger = LogFactory.getLog(FinanceOrder1DayJob.class);
	@Autowired
	protected FinanceOrderService financeOrderService;
	@Autowired
	protected LogService sysLogService;

	//
	//	<!-- 每天凌晨4点启动 -->
	//	<task:scheduled ref="dataTask" method="jobHandle" cron="0 0 4 * * ?"/>
	@Scheduled(cron = "0 0 4 * * ?")
	public void taskJob() {
		try {
			List<FinanceOrder> financeOrder = financeOrderService.getAllStateBy_1();
			if(financeOrder != null) {
				for (int i = 0; i < financeOrder.size(); i++) {
					FinanceOrder order = financeOrder.get(i);
					boolean lock = false;
					try {

						if (!FinanceOrderLock.add(order.getOrderNo())) {
							continue;
						}
						lock = true;
						this.financeOrderService.addListProfit(order);

					} catch (Exception e) {
						logger.error("FinanceOrder1DayJob order profit fail，orderno:{"+order.getOrderNo()+"},e:", e);
					} finally {
						if (lock) {
							/**
							 * 每秒处理100个订单
							 */
							ThreadUtils.sleep(10);
							FinanceOrderLock.remove(order.getOrderNo());
						}

					}
				}
				logger.info(DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss)+" finance profit finished ,count:" + financeOrder.size());
			}
			
		
		} catch (Throwable e) {
			logger.error("FinanceOrder1DayJob run fail e:", e);
			Log entity = new Log();
//			entity.set(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setOperator("FinanceOrder1DayJob 理财任务 执行失败 e:"+e);
			sysLogService.save(entity);
		} finally {
			/**
			 * 暂停0.1秒
			 */
			ThreadUtils.sleep(1000);
		}

	}
	
	public void handleData(Date systemTime) {
		try {
			List<FinanceOrder> financeOrder = financeOrderService.getAllStateBy_1();
			if(financeOrder != null) {
				for (int i = 0; i < financeOrder.size(); i++) {
					FinanceOrder order = financeOrder.get(i);
					boolean lock = false;
					try {

						if (!FinanceOrderLock.add(order.getOrderNo())) {
							continue;
						}
						lock = true;
						this.financeOrderService.addListProfit(order,systemTime);

					} catch (Exception e) {
						logger.error("FinanceOrder1DayJob order profit fail，orderno:{"+order.getOrderNo()+"},e:", e);
					} finally {
						if (lock) {
							/**
							 * 每秒处理100个订单
							 */
							ThreadUtils.sleep(10);
							FinanceOrderLock.remove(order.getOrderNo());
						}

					}
				}
				logger.info(DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss)+" finance profit finished ,count:" + financeOrder.size());
			}
			
		} catch (Throwable e) {
			logger.error("FinanceOrder1DayJob run fail e:", e);
			Log entity = new Log();
//			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setOperator("FinanceOrder1DayJob 理财任务 执行失败  e:"+e);
			sysLogService.save(entity);
		} finally {
			/**
			 * 暂停0.1秒
			 */
			ThreadUtils.sleep(1000);
		}
	}

	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
		this.financeOrderService = financeOrderService;
	}

//	public void setSysLogService(SysLogService sysLogService) {
//		this.sysLogService = sysLogService;
//	}

	
}
