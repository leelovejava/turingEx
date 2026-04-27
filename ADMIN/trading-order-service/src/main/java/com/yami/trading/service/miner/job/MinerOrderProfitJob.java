package com.yami.trading.service.miner.job;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.service.miner.service.MinerOrderProfitService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class MinerOrderProfitJob {
	private static Logger logger = LoggerFactory.getLogger(MinerOrderProfitJob.class);
	@Autowired
	protected MinerOrderProfitService minerOrderProfitService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected DataService dataService;
	@Autowired
	protected LogService sysLogService;

	public void taskJob() {

		try {
			int pageNo = 1;
			int pageSize = 300;
			minerOrderProfitService.cacheRecomProfitClear();

			String miner_bonus_parameters = sysparaService.find("miner_bonus_parameters").getSvalue();
			String miner_profit_symbol = sysparaService.find("miner_profit_symbol").getSvalue();
			List<Realtime> realtime_list = this.dataService.realtime(miner_profit_symbol);
			Realtime realtime = null;
			if (!realtime_list.isEmpty()) {
				realtime = realtime_list.get(0);
			}
			if (StringUtils.isNotEmpty(miner_profit_symbol) && realtime == null) {
				// 行情不存在，则退出计算
				return;
			}

			while (true) {
				Page page = minerOrderProfitService.pagedQueryComputeOrder(pageNo, pageSize);
				List minerOrders = page.getRecords();
				if (CollectionUtils.isEmpty(minerOrders)) {// 分页没数据时表示已经计算结束
					break;
				}
				try {
					this.minerOrderProfitService.saveComputeOrderProfit(minerOrders, miner_profit_symbol, realtime,
							miner_bonus_parameters);
				} catch (Throwable e) {
					logger.error("error:", e);
				}
				logger.info("miner profit finished ,count:" + minerOrders.size());
				pageNo++;
			}
			// 用户收益计算完，计算推荐人收益
			minerOrderProfitService.saveRecomProfit();
		} catch (Throwable e) {
			logger.error("miner profit run fail", e);
		}

	}

	public void handleData(Date systemTime) {

		try {
			int pageNo = 1;
			int pageSize = 300;
			minerOrderProfitService.cacheRecomProfitClear();

			String miner_bonus_parameters = sysparaService.find("miner_bonus_parameters").getSvalue();
			String miner_profit_symbol = sysparaService.find("miner_profit_symbol").getSvalue();
			List<Realtime> realtime_list = this.dataService.realtime(miner_profit_symbol);
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			}
			if (StringUtils.isNotEmpty(miner_profit_symbol) && realtime == null)
				return;// 行情不存在，则退出计算

			while (true) {
				Page page = minerOrderProfitService.pagedQueryComputeOrder(pageNo, pageSize);
				List minerOrders = page.getRecords();
				if (CollectionUtils.isEmpty(minerOrders)) {// 分页没数据时表示已经计算结束
					break;
				}
				try {
					this.minerOrderProfitService.saveComputeOrderProfit(minerOrders, miner_profit_symbol, realtime,
							miner_bonus_parameters,systemTime);
				} catch (Throwable e) {
					logger.error("error:", e);
				}
				logger.info("miner profit finished ,count:" + minerOrders.size());
				pageNo++;
			}
			// 用户收益计算完，计算推荐人收益
			minerOrderProfitService.saveRecomProfit(systemTime);
		} catch (Throwable e) {
			logger.error("MinerOrderProfitJob run fail e:", e);
			Log entity = new Log();
//			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setOperator("MinerOrderProfitJob 矿机任务 执行失败  e:"+e);
			sysLogService.save(entity);
		}

	}
	public void setMinerOrderProfitService(MinerOrderProfitService minerOrderProfitService) {
		this.minerOrderProfitService = minerOrderProfitService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

//	public void setSysLogService(SysLogService sysLogService) {
//		this.sysLogService = sysLogService;
//	}
	

	// 计时器--------------------------------------------------------------------------------
//	<!-- 每天凌晨4点启动 -->
//	cron="0 0 0 * * ?"
	// @Scheduled(cron = "0 */3 * * * ?")
	@Scheduled(cron = "0 0 4 * * ?")
	public void zeroJobHandle() {
		logger.error("矿机收益凌晨4点开始下发");
		Syspara syspara = this.sysparaService.find("time_task_config");
//		System.out.println("syspara.getSvalue1() => " + syspara.getSvalue());
		if( ObjectUtils.isEmpty(syspara) || (ObjectUtils.isNotEmpty(syspara)&&"1".equals(syspara.getSvalue()))) {
			logger.error("矿机配置为1矿机收益凌晨4点开始下发");
			//矿机每日计息
			this.taskJob();
		}
	}
//	<!-- 每天0,12点启动 -->
//	cron="0 0 0,12 * * ?"
	// @Scheduled(cron = "0 0 0,12 * * ?")
	// @Scheduled(cron = "0 */2 * * * ?")
	@Scheduled(cron = "0 0 0,12 * * ?")
	public void zeroOrtwelveJobHandle() {
		logger.error("矿机收益凌晨零点和12点开始下发");
		Syspara syspara = this.sysparaService.find("time_task_config");
//		System.out.println("syspara.getSvalue2() => " + syspara.getSvalue());
		if(ObjectUtils.isNotEmpty(syspara)&&"2".equals(syspara.getSvalue())) {
			logger.error("矿机配置为2矿机收益凌晨和12点开始下发");
			// 矿机每日计息
			this.taskJob();
		}
	}


	public void taskJob1() {
		/**
		 * 凌晨4点主定时任务
		 */
		try {
			// DBBackupLock.add(DBBackupLock.ALL_DB_LOCK);
			
			// activityOrderTaskJobHandle.taskJob();
			
			// 理财每日计息
			// financeOrder1DayJob.taskJob();
			logger.error("矿机收益凌晨4点开始下发");
			Syspara syspara = this.sysparaService.find("time_task_config");
			if(ObjectUtils.isEmpty(syspara)) {
				logger.error("当前配置为空矿机收益默认凌晨4点下");
				// 矿机每日计息
				this.taskJob();
			}else {
				if("0".equals(syspara.getSvalue())) {
					logger.error("当前配置为0矿机收益凌晨4点下");
					// 矿机每日计息
					this.taskJob();
				}
			}
			
			// 每日定时返佣前一天的 充值,为空则 上级不 返佣
			// 是否开启每日定时任务返佣，为空则不开启 0.5% 0.3% 0.2% = 0.005,0.003,0.002
			// Syspara dailyRechargeRecom = this.sysparaService.find("daily_recharge_recom");
			// if(null != dailyRechargeRecom && !"".equals(dailyRechargeRecom.getValue())) {
			// 	recharegeBonus1DayJob.taskJob();
			// }
			
			/**
			 * 每日定时任务是否重置提现限制流水，1重置，2不重置
			 */
			// String withdraw_reset_daily = this.sysparaService.find("withdraw_reset_daily").getValue();
			/**
			 * 用户提现限制流水重置时间(1为凌晨4点，2为凌晨0点)
			 */
			// String withdraw_reset_daily_time = this.sysparaService.find("withdraw_reset_daily_time").getValue();
			/**
			 *	是否重置当日提现限制流水
			 */
			// if("1".equals(withdraw_reset_daily) && "1".equals(withdraw_reset_daily_time)) {
			// 	withdrawLimitAmountr1DayJob.taskJob();
			// }
			
			// 理财计算前一日购买奖励金额
			// financeOrderCreateRecomJob.taskJob();

			// 交割计算前一日购买推荐奖励
			// futuresOrderCreateRecomJob.taskJob();
			
			// 删除和重置分时和K线数据
			// cleanDataJob.taskJob();
			
			// C2C统计数据
			// c2cStatistics1DayJob.taskJob();
			
//			logger.error("TaskJobHandle backup 发起调用 start...........");
			// 每日备份数据库
			// BackupUtil.backup(sysLogService,sysparaService);
//			logger.error("TaskJobHandle backup 调用结束 end...........");
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("TaskJobHandle fail e:",e);
		}finally {
//			DBBackupLock.remove(DBBackupLock.ALL_DB_LOCK);
		}
	}
	

}
