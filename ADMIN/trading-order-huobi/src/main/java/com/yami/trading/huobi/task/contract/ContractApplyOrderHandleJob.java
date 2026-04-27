package com.yami.trading.huobi.task.contract;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.constants.RedisLockKeyConstants;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractLock;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * 委托单进入市场
 */
@Slf4j
@Component
public class ContractApplyOrderHandleJob implements Runnable {
	@Autowired
	private ContractOrderService contractOrderService;

	@Autowired
	private ContractApplyOrderService contractApplyOrderService;

	@Autowired
	@Qualifier("dataService")
	private DataService dataService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private RedissonClient redissonClient;

	public void start(){
		new Thread(this, "ContractApplyOrderHandleJob").start();
		log.info("委托单处理线程启动！");
	}

	@Override
	public void run() {
		/*
		 * 系统启动先暂停40秒
		 */
		ThreadUtils.sleep(1000 * 40);
		while (true) {
			boolean processSuccess = false;
			String oneContractApplyOrderId = null;
			try {
				oneContractApplyOrderId = RedisUtil.spop(RedisKeys.NEW_CONTRACT_APPLY_ORDERS);
				if (StrUtil.isBlank(oneContractApplyOrderId)) {
					// finally 里面仍然会执行了 sleep
					processSuccess = true;
					//log.info("---> ContractApplyOrderHandleJob.run 没有可以处理的单...");
					continue;
				}

				//log.info("---> ContractApplyOrderHandleJob.run 获取一个待处理的合约订单:{}", oneContractApplyOrderId);
				ContractApplyOrder order = this.contractApplyOrderService.getById(oneContractApplyOrderId);
				if (order == null || !order.getState().equals(ContractApplyOrder.STATE_SUBMITTED)) {
					processSuccess = true;
					continue;
				}

				List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
				Realtime realtime = null;
				if (realtime_list.size() > 0) {
					realtime = realtime_list.get(0);
					//log.info("---> ContractApplyOrderHandleJob.run symbol:{} 的 realTime 值为:{}", order.getSymbol(), realtime);
				} else {
					log.warn("---> ContractApplyOrderHandleJob.run symbol:{} 的 realTime 值为空", order.getSymbol());
					continue;
				}

				// 休市不做撮合
				Item bySymbol = itemService.findBySymbol(order.getSymbol());
				if (bySymbol == null) {
					continue;
				}
				boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType());
				if (!isOpen) {
					//log.warn("---> ContractApplyOrderHandleJob.run symbol:{} 未open", order.getSymbol());
					continue;
				}

				if ("limit".equals(order.getOrderPriceType())) {
					/**
					 * 限价单
					 */
					if ("buy".equals(order.getDirection())) {
						/**
						 * 买涨
						 */
						if (BigDecimal.valueOf(realtime.getClose()).compareTo(order.getPrice()) <= 0) {
							processSuccess = this.handle(order, realtime);
						}
					} else {
						/**
						 * 买跌
						 */
						if (BigDecimal.valueOf(realtime.getClose()).compareTo(order.getPrice()) >= 0) {
							processSuccess = this.handle(order, realtime);
						}
					}
				} else {
					/**
					 * 非限制，直接进入市场
					 */
					processSuccess = this.handle(order, realtime);
				}
			} catch (Exception e) {
				log.error("合约订单:{} 处理失败", oneContractApplyOrderId, e);
			} finally {
				if (!processSuccess) {
					// 未处理成功，缓存加回去
					//log.warn("合约订单:{} 处理失败，重新录入缓存", oneContractApplyOrderId);
					RedisUtil.sadd(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, oneContractApplyOrderId);
				}

				ThreadUtils.sleep(1000 * 1);
			}
		}
	}

	public boolean handle(ContractApplyOrder applyOrder, Realtime realtime) {
		boolean lock = false;
		try {
			if (!ContractLock.add(applyOrder.getOrderNo())) {
				return false;
			}
			lock = true;
			if ("open".equals(applyOrder.getOffset())) {
				// 全局锁
				String lockKey = RedisLockKeyConstants.LOCK_CONTRACT_APPLY_TO_ORDER_PREFIX + applyOrder.getUuid();
				RLock rLock = redissonClient.getLock(lockKey);
				if (!rLock.tryLock()) {
					return false;
				}

				try {
					ContractApplyOrder order = this.contractApplyOrderService.getById(applyOrder.getUuid());
					if (order == null || !order.getState().equals(ContractApplyOrder.STATE_SUBMITTED)) {
						// 已处理，跳过
						return true;
					}

					this.contractOrderService.saveOpen(applyOrder, realtime);
				} finally {
					rLock.unlock();
				}

				return true;
			} else if ("close".equals(applyOrder.getOffset())) {
				/**
				 * 平仓
				 */
				List<ContractOrder> list = this.contractOrderService.findSubmitted(applyOrder.getPartyId(),
						applyOrder.getSymbol(), applyOrder.getDirection());
				if (list.size() == 0) {
					applyOrder.setVolume(BigDecimal.ZERO);
					applyOrder.setState(ContractApplyOrder.STATE_CREATED);
					this.contractApplyOrderService.updateById(applyOrder);
					return true;
				}

				boolean allProcessOk = true;
				for (int i = 0; i < list.size(); i++) {
					ContractOrder order = list.get(i);
					boolean lock_order = false;
					try {
						if (!ContractLock.add(order.getOrderNo())) {
							allProcessOk = false;
							continue;
						}
						lock_order = true;
						applyOrder = this.contractOrderService.saveClose(applyOrder, realtime, order.getOrderNo());

						if (ContractApplyOrder.STATE_CREATED.equals(applyOrder.getState())) {
							allProcessOk = true;
							break;
						}
					} catch (Exception e) {
						allProcessOk = false;
						log.error("---> ContractApplyOrderHandleJob.handle 处理合约:{} 报错", applyOrder.getUuid(), e);
					} finally {
						if (lock_order) {
							ThreadUtils.sleep(100);
							ContractLock.remove(order.getOrderNo());
						}
					}
				}

				return allProcessOk;
			}

			log.error("---> ContractApplyOrderHandleJob.handle 当前合约:{} 记录无法处理", applyOrder.getUuid());
			return false;
		} catch (Exception e) {
			log.error("---> ContractApplyOrderHandleJob.handle 处理合约:{} 报错:", applyOrder.getUuid(), e);

			return false;
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ContractLock.remove(applyOrder.getOrderNo());
			}
		}
	}

}
