package com.yami.trading.service.finance.job;

import java.util.Date;

import com.yami.trading.bean.model.Log;
import com.yami.trading.service.system.LogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinanceOrderCreateRecomJob {
	private static Logger logger = LoggerFactory.getLogger(FinanceOrderCreateRecomJob.class);
	@Autowired
	protected LogService sysLogService;
	@Autowired
	private FinanceOrderCreateRecomService financeOrderCreateRecomService;

	//	<!-- 每天凌晨4点启动 -->
	//	<task:scheduled ref="dataTask" method="jobHandle" cron="0 0 4 * * ?"/>
	// 理财计算前一日购买奖励金额
	@Scheduled(cron = "0 0 4 * * ?")
	public void taskJob() {
		try {
			financeOrderCreateRecomService.computeRecom();
		} catch (Exception e) {
			logger.error("FinanceOrderCreateRecomJob run fail e:", e);
			Log entity = new Log();
//			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setOperator("FinanceOrderCreateRecomJob 理财购买奖励任务 执行失败 e:"+e);
			sysLogService.save(entity);
		} 
	}

//	public void setSysLogService(SysLogService sysLogService) {
//		this.sysLogService = sysLogService;
//	}

	public void setFinanceOrderCreateRecomService(FinanceOrderCreateRecomService financeOrderCreateRecomService) {
		this.financeOrderCreateRecomService = financeOrderCreateRecomService;
	}
	
}
