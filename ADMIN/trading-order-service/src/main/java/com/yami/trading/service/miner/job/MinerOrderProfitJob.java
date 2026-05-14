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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * 矿机收益定时任务类
 * 
 * <p>该类负责执行矿机收益的定时计算任务，主要功能包括：
 * <ul>
 *   <li>每12分钟执行一次收益计算任务</li>
 *   <li>从预收益表(t_quant_pre_income)读取未使用的收益记录</li>
 *   <li>标记预收益记录为已使用</li>
 *   <li>在机器人订单表(t_quant_bot_orders)中新增使用记录</li>
 *   <li>计算并更新矿机订单的累计收益</li>
 *   <li>计算推荐人收益</li>
 * </ul>
 * 
 * <p>收益计算流程：
 * <ol>
 *   <li>定时任务触发（每12分钟）</li>
 *   <li>分页查询所有计息中的矿机订单</li>
 *   <li>遍历每个订单，从预收益表读取一条未使用的记录</li>
 *   <li>标记预收益记录为已使用</li>
 *   <li>在机器人订单表中新增使用记录</li>
 *   <li>更新矿机订单的累计收益</li>
 *   <li>计算并发放推荐人收益</li>
 * </ol>
 */
@Component
@Slf4j
public class MinerOrderProfitJob {
	
	/** 日志记录器 */
	private static Logger logger = LoggerFactory.getLogger(MinerOrderProfitJob.class);

	/** 矿机收益服务，负责收益计算核心逻辑 */
	@Autowired
	protected MinerOrderProfitService minerOrderProfitService;

	/** 系统参数服务，用于获取配置参数 */
	@Autowired
	protected SysparaService sysparaService;

	/** 行情数据服务，用于获取币种实时行情 */
	@Autowired
	protected DataService dataService;

	/** 系统日志服务，用于记录任务执行日志 */
	@Autowired
	protected LogService sysLogService;

	/**
	 * 矿机收益计算主任务方法
	 * 
	 * <p>该方法是收益计算的核心入口，执行以下步骤：
	 * <ol>
	 *   <li>获取收益币种配置及行情数据</li>
	 *   <li>分页查询所有计息中的矿机订单</li>
	 *   <li>遍历订单计算收益</li>
	 *   <li>计算并持久化推荐人收益</li>
	 * </ol>
	 */
	public void taskJob() {
		try {
			// 分页参数，每页处理300条订单
			int pageNo = 1;
			int pageSize = 300;
			
			// 获取系统配置参数
			// 收益币种配置，为空表示USDT，非空表示指定币种
			String miner_profit_symbol = sysparaService.find("miner_profit_symbol").getSvalue();
			
			// 第三步：获取收益币种的实时行情
			List<Realtime> realtime_list = this.dataService.realtime(miner_profit_symbol);
			Realtime realtime = null;
			if (!realtime_list.isEmpty()) {
				realtime = realtime_list.get(0);
			}
			
			// 如果配置了非USDT收益币种但行情不存在，则退出计算
			if (StringUtils.isNotEmpty(miner_profit_symbol) && realtime == null) {
				logger.warn("收益币种行情不存在，退出计算");
				return;
			}

			// 第四步：分页遍历所有计息中的矿机订单
			while (true) {
				// 查询当前页的矿机订单
				Page page = minerOrderProfitService.pagedQueryComputeOrder(pageNo, pageSize);
				List minerOrders = page.getRecords();
				
				// 如果当前页没有数据，表示所有订单已处理完毕
				if (CollectionUtils.isEmpty(minerOrders)) {
					break;
				}
				
				try {
					// 计算当前页订单的收益
					this.minerOrderProfitService.saveComputeOrderProfit(minerOrders, miner_profit_symbol, realtime,
							null);
				} catch (Throwable e) {
					// 单个订单计算失败不影响其他订单处理
					logger.error("矿机收益计算异常:", e);
				}
				
				logger.info("矿机收益计算完成，当前页处理订单数:{}", minerOrders.size());
				pageNo++;
			}
			
		} catch (Throwable e) {
			logger.error("矿机收益定时任务执行失败", e);
		}
	}

	/**
	 * 矿机收益计算任务方法（带系统时间参数）
	 * 
	 * <p>与taskJob()方法功能相同，但允许传入指定的系统时间，
	 * 主要用于测试或手动触发时指定计算时间。
	 * 
	 * @param systemTime 指定的系统时间，用于收益计算的时间基准
	 */
	public void handleData(Date systemTime) {
		try {
			// 分页参数，每页处理300条订单
			int pageNo = 1;
			int pageSize = 300;
			
			// 获取系统配置参数
			String miner_profit_symbol = sysparaService.find("miner_profit_symbol").getSvalue();
			
			// 获取收益币种的实时行情
			List<Realtime> realtime_list = this.dataService.realtime(miner_profit_symbol);
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			}
			
			// 如果配置了非USDT收益币种但行情不存在，则退出计算
			if (StringUtils.isNotEmpty(miner_profit_symbol) && realtime == null)
				return;

			// 分页遍历所有计息中的矿机订单
			while (true) {
				Page page = minerOrderProfitService.pagedQueryComputeOrder(pageNo, pageSize);
				List minerOrders = page.getRecords();
				
				if (CollectionUtils.isEmpty(minerOrders)) {
					break;
				}
				
				try {
					// 使用指定的系统时间计算收益
					this.minerOrderProfitService.saveComputeOrderProfit(minerOrders, miner_profit_symbol, realtime,
							null, systemTime);
				} catch (Throwable e) {
					logger.error("矿机收益计算异常:", e);
				}
				
				logger.info("矿机收益计算完成，当前页处理订单数:{}", minerOrders.size());
				pageNo++;
			}
			
		} catch (Throwable e) {
			logger.error("矿机收益定时任务执行失败", e);
			
			// 记录错误日志到数据库
			Log entity = new Log();
			entity.setCreateTime(new Date());
			entity.setOperator("MinerOrderProfitJob 矿机任务 执行失败  e:" + e);
			sysLogService.save(entity);
		}
	}

	/**
	 * 设置矿机收益服务（用于测试注入）
	 * @param minerOrderProfitService 矿机收益服务实例
	 */
	public void setMinerOrderProfitService(MinerOrderProfitService minerOrderProfitService) {
		this.minerOrderProfitService = minerOrderProfitService;
	}

	/**
	 * 设置系统参数服务（用于测试注入）
	 * @param sysparaService 系统参数服务实例
	 */
	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	/**
	 * 设置行情数据服务（用于测试注入）
	 * @param dataService 行情数据服务实例
	 */
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	// ==================== 定时任务调度器 ====================

	/**
	 * 矿机收益定时任务（每5分钟26秒执行一次）
	 * 
	 * <p>使用Spring的@Scheduled注解实现定时调度
	 * 表示每12分钟的第0秒执行一次。
	 * 
	 * <p>执行流程：
	 * <ol>
	 *   <li>从预收益表(t_quant_pre_income)读取未使用的收益记录</li>
	 *   <li>标记预收益记录为已使用(status=1)</li>
	 *   <li>在机器人订单表(t_quant_bot_orders)中新增一条使用记录</li>
	 *   <li>更新矿机订单的累计收益</li>
	 * </ol>
	 * 
	 * <p>注意事项：
	 * <ul>
	 *   <li>每天最多处理220条预收益记录（与预收益生成逻辑对应）</li>
	 *   <li>单个订单每次只处理一条预收益记录</li>
	 *   <li>处理失败的订单会被跳过，不影响其他订单</li>
	 * </ul>
	 */
	@Scheduled(cron = "26 0/5 * * * ?")
	public void twelveMinuteJobHandle() {
		try {
			// 随机延迟 1-60 秒
			long delay = (long) (Math.random() * 60000) + 1000;
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.taskJob();
	}	

	/**
	 * 矿机收益定时任务（凌晨4点执行，已废弃）
	 * 
	 * <p>该方法为旧版定时任务入口，保留用于兼容历史配置。
	 * 当前已使用twelveMinuteJobHandle()方法替代，每12分钟执行一次。
	 * 
	 * <p>根据系统参数time_task_config判断是否执行：
	 * <ul>
	 *   <li>配置为空或值为"1"时执行</li>
	 *   <li>配置为其他值时不执行</li>
	 * </ul>
	 */
	public void taskJob1() {
		try {
			logger.info("========== 矿机收益定时任务（凌晨4点）开始执行 ==========");
			
			// 获取定时任务配置参数
			Syspara syspara = this.sysparaService.find("time_task_config");
			
			if (ObjectUtils.isEmpty(syspara)) {
				logger.info("定时任务配置为空，默认执行矿机收益计算");
				this.taskJob();
			} else {
				if ("0".equals(syspara.getSvalue())) {
					logger.info("定时任务配置为0，执行矿机收益计算");
					this.taskJob();
				}
			}
			
		} catch (Exception e) {
			logger.error("矿机收益定时任务执行失败", e);
		}
	}
}
