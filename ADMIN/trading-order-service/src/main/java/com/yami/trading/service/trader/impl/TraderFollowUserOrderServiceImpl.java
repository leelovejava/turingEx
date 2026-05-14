package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.trader.domain.*;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.util.*;
import com.yami.trading.dao.trader.TraderFollowUserOrderMapper;
import com.yami.trading.service.FollowMoneyLogService;
import com.yami.trading.service.FollowWalletService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.trader.*;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class TraderFollowUserOrderServiceImpl implements TraderFollowUserOrderService {
	@Resource
	private TraderService traderService;
	@Resource
	private TraderFollowUserService traderFollowUserService;
	@Resource
	private TraderUserService traderUserService;
	@Resource
	private WalletService walletService;

	@Resource
	private FollowWalletService followWalletService;

	@Resource
	private MoneyLogService moneyLogService;

	@Resource
	private FollowMoneyLogService followMoneyLogService;

	@Resource
	private TraderOrderService traderOrderService;
	@Resource
	private UserRecomService userRecomService;
	@Resource
	private SysparaService sysparaService;

	@Resource
	private TraderFollowUserOrderMapper traderFollowUserOrderMapper;

	@Resource
	private UserService userService;
	
	private static Log logger = LogFactory.getLog(TraderFollowUserOrderServiceImpl.class);

	public List<Map<String, Object>> getPaged(Page page, String partyId, String state) {
//		StringBuffer queryString = new StringBuffer(" SELECT orders.SYMBOL symbol,orders.AMOUNT_CLOSE amount_close,  "
//				+ " orders.TRADE_AVG_PRICE trade_avg_price,  "
//				+ " orders.DIRECTION direction,orders.UNIT_AMOUNT unit_amount,  "
//				+ "  orders.STATE state,orders.FEE fee,orders.PROFIT profit, "
//				+ " orders.DEPOSIT deposit,orders.DEPOSIT_OPEN deposit_open,orders.CLOSE_AVG_PRICE close_avg_price,  "
//				+ "  DATE_FORMAT(orders.CLOSE_TIME, '%Y-%m-%d %H:%i:%S') closeTime,DATE_FORMAT(orders.CREATE_TIME, '%Y-%m-%d %H:%i:%S') createTime, "
//				+ "  orders.VOLUME_OPEN volume_open,orders.VOLUME volume,item.NAME itemname, "
//				+ " trader_user_order.USER_ORDER_NO order_no   ");
//		queryString.append(" FROM T_TRADER_FOLLOW_USER_ORDER trader_user_order  ");
//		queryString
//				.append(" LEFT JOIN T_CONTRACT_ORDER orders ON orders.ORDER_NO  = trader_user_order.USER_ORDER_NO  ");
//		queryString.append(" LEFT JOIN T_ITEM item ON orders.SYMBOL = item.SYMBOL  ");
//
//		queryString.append("  WHERE 1 = 1 ");
//
//		Map<String, Object> parameters = new HashMap();
//		queryString.append(" and trader_user_order.PARTY_ID =:partyId");
//		parameters.put("partyId", partyId);
//
//		queryString.append(" order by trader_user_order.CREATE_TIME desc ");
		List<Map<String, Object>> datas = traderFollowUserOrderMapper.listDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), partyId, state);

		List<Map<String, Object>> data = this.bulidData(datas);
		return data;
	}

	private List<Map<String, Object>> bulidData(List<Map<String, Object>> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traders == null) {
			return result_traders;
		}
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> entity = traders.get(i);
			map.put("order_no", entity.get("order_no"));

			map.put("close_avg_price", entity.get("close_avg_price"));
			map.put("amount_close", entity.get("amount_close"));
			map.put("trade_avg_price", entity.get("trade_avg_price"));
			map.put("direction", entity.get("direction"));
			map.put("unit_amount", entity.get("unit_amount"));
			map.put("state", entity.get("state"));
			map.put("fee", entity.get("fee"));
			map.put("profit", entity.get("profit"));
			map.put("deposit", entity.get("deposit"));
			map.put("deposit_open", entity.get("deposit_open"));
			if (entity.get("closeTime") != null) {
				logger.info("------> TraderFollowUserOrderServiceImpl.bulidData 时间值类型：" + entity.get("closeTime"));
				Date curCloseTime = DateUtils.toDate(entity.get("closeTime").toString(), DateUtils.DF_yyyyMMddHHmmss);
				Date curShowCloseTime = DateTimeTools.transferToShowTime(curCloseTime);
				map.put("closeTime", DateUtils.format(curShowCloseTime, "MM-dd HH:mm:ss"));
			} else {
				map.put("closeTime", "");
			}
			if (entity.get("createTime") != null) {
				Date curCreateTime = DateUtils.toDate(entity.get("createTime").toString(), DateUtils.DF_yyyyMMddHHmmss);
				Date curShowCreateTime = DateTimeTools.transferToShowTime(curCreateTime);
				map.put("createTime", DateUtils.format(curShowCreateTime,"MM-dd HH:mm:ss"));
			} else {
				map.put("createTime", "");
			}

//			map.put("createTime", entity.get("createTime"));
			map.put("volume_open", entity.get("volume_open"));
			map.put("volume", entity.get("volume"));
			map.put("itemname", entity.get("itemname"));
			map.put("volume", entity.get("volume"));
			map.put("change_ratio", getChange_ratio(entity.get("state").toString(),
					Double.parseDouble(entity.get("amount_close").toString()),
					Double.parseDouble(entity.get("profit").toString()), Double.parseDouble(entity.get("deposit").toString()),
					Double.parseDouble(entity.get("deposit_open").toString())));

			map.put("stop_price_profit", entity.get("stop_price_profit"));
			map.put("stop_price_loss", entity.get("stop_price_loss"));
			map.put("lever_rate", entity.get("lever_rate"));
			map.put("force_close_price", entity.get("force_close_price"));

			String trader_party_id = (String) entity.get("trader_party_id");
			User user = userService.findByUserId(trader_party_id);
			map.put("trader_username", user.getUserName());

			result_traders.add(map);
		}

		return result_traders;

	}

	public Double getChange_ratio(String state, double amount_close, double profit, double deposit,
			double deposit_open) {
		double change_ratio = 0;
		if (ContractOrder.STATE_SUBMITTED.equals(state)) {
			change_ratio = Arith.div(Arith.sub(Arith.add(Arith.add(amount_close, profit), deposit), deposit_open),
					deposit_open);
		} else {
			change_ratio = Arith.div(Arith.sub(Arith.add(amount_close, deposit), deposit_open), deposit_open);

		}

		change_ratio = Arith.mul(change_ratio, 100);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(change_ratio));
	}

	/**
	 * 判断是否是交易员
	 */
	public boolean isOrNotTrader(String partyId) {
		Trader trader = this.traderService.findByPartyId(partyId);
		if (trader != null) {
			return true;
		} else {
			return false;
		}
	}

	public void update(TraderFollowUserOrder entity) {
//		ApplicationUtil.executeUpdate(entity);
		traderFollowUserOrderMapper.updateById(entity);
	}

	@Override
	public void traderOpen(ContractOrder contractOrder, ContractApplyOrderService contractApplyOrderService, ContractOrderService contractOrderService, int follow) {
		if (isOrNotTrader(contractOrder.getPartyId())) {
			CreateDelayThread lockDelayThread = new CreateDelayThread(contractOrder, contractApplyOrderService, contractOrderService, follow);
			Thread t = new Thread(lockDelayThread);
			t.start();
		}
	}
	
	@Override
	public void traderClose(ContractOrder contractOrder, ContractOrderService contractOrderService) {
		if (isOrNotTrader(contractOrder.getPartyId())) {
			CloseDelayThread lockDelayThread = new CloseDelayThread(contractOrder, contractOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();

		}
//			else {
//			CloseOrderDelayThread lockOrderDelayThread = new CloseOrderDelayThread(contractOrder);
//			Thread t = new Thread(lockOrderDelayThread);
//			t.start();
//
//		}

	}
	
	/**
	 * 用户跟随交易员创建委托单
	 */
	public class CreateDelayThread implements Runnable {
		private ContractOrder contractOrder;
		private ContractApplyOrderService contractApplyOrderService;

		private ContractOrderService contractOrderService;

		private int follow;

		public void run() {
			try {
				List<TraderFollowUser> users = traderFollowUserService.findByTrader_partyId(contractOrder.getPartyId()); //查找当前交易员的跟随者
				if (users != null) {
					for (TraderFollowUser user : users) {
						if (!"".equals(user.getPartyId())) {
							/**
							 * 判断当前用户最多还可以买几张
							 */
							try {
								List<TraderFollowUserOrder> userOrders = findByPartyIdAndTraderPartyIdAndState(user.getPartyId(), contractOrder.getPartyId(), ContractOrder.STATE_SUBMITTED);
								double volume_last = user.getVolumeMax();  // 跟单时设置的最大持仓张数
								if (userOrders != null) {
									for (TraderFollowUserOrder userOrder : userOrders) {
										volume_last = Arith.sub(volume_last, userOrder.getVolume());
									}
								}
								if (volume_last <= 0) { // 已达到最大持仓张数，无法委托永续合约下单
									continue;
								}

								ContractApplyOrder order = new ContractApplyOrder();
								order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
								order.setPartyId(user.getPartyId());
								order.setSymbol(contractOrder.getSymbol());
								order.setDirection(contractOrder.getDirection());
								order.setOffset(ContractApplyOrder.OFFSET_OPEN);
								order.setFollow(follow); // 标记为跟单订单

								/**
								 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
								 */
								if ("1".equals(user.getFollowType())) {
									if (volume_last < user.getVolume()) { // 剩余可下单张数小于用户设置的固定开仓单数
										order.setVolume(new BigDecimal(volume_last));
										order.setVolumeOpen(new BigDecimal(volume_last));
									} else {
										order.setVolume(BigDecimal.valueOf(user.getVolume()));
										order.setVolumeOpen(BigDecimal.valueOf(user.getVolume()));
									}
								}
								if ("2".equals(user.getFollowType())) {
									if (volume_last < Arith.mul(contractOrder.getVolumeOpen().doubleValue(), user.getVolume())) {
										order.setVolume(new BigDecimal(volume_last));
										order.setVolumeOpen(new BigDecimal(volume_last));
									} else {
										order.setVolume(BigDecimal.valueOf(Arith.mul(contractOrder.getVolumeOpen().doubleValue(), user.getVolume())));
										order.setVolumeOpen(BigDecimal.valueOf(Arith.mul(contractOrder.getVolumeOpen().doubleValue(), user.getVolume())));
									}
								}
								order.setLeverRate(contractOrder.getLeverRate()); // 杠杆
								order.setPrice(contractOrder.getTradeAvgPrice()); // 永续合约交易委托价格,设置为交易员成交无效
								order.setStopPriceProfit(contractOrder.getStopPriceProfit());
								order.setStopPriceLoss(contractOrder.getStopPriceLoss());
								order.setState(ContractApplyOrder.STATE_SUBMITTED);
								/**
								 * 默认市价单
								 */
								order.setOrderPriceType(ContractApplyOrder.ORDER_PRICE_TYPE_OPPONENT);

								if (order.getVolumeOpen().compareTo(new BigDecimal(0)) <= 0) { // 委托开仓数量小于等0
									continue;
								}

								contractApplyOrderService.saveCreate(order);
								// 放入缓存
								RedisUtil.sadd(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, order.getUuid());

								/**
								 * 跟单产生的交易手续费，奖励给推荐人
								 */
								saveFeeBounsHandle(order);

								/**
								 * 跟随交易员添加下单记录
								 */
								TraderFollowUserOrder traderFollowUserOrder = new TraderFollowUserOrder();
								traderFollowUserOrder.setUuid(ApplicationUtil.getCurrentTimeUUID());
								traderFollowUserOrder.setPartyId(user.getPartyId());
								traderFollowUserOrder.setTraderPartyId(contractOrder.getPartyId());
								/**
								 * 交易员的持仓单号
								 */
								traderFollowUserOrder.setTraderOrderNo(contractOrder.getOrderNo()); // 交易员订单
								traderFollowUserOrder.setUserOrderNo(order.getOrderNo());
								traderFollowUserOrder.setVolume(order.getVolumeOpen().doubleValue());
								traderFollowUserOrder.setState(order.getState());
								traderFollowUserOrder.setCreateTime(order.getCreateTime());
								traderFollowUserOrderMapper.insert(traderFollowUserOrder);
//								ApplicationUtil.executeSaveOrUpdate(traderFollowUserOrder);

								/**
								 * 将数据加入用户跟随总累计收益表
								 */

								TraderUser traderUser = traderUserService.saveTraderUserByPartyId(user.getPartyId());
								traderUser.setAmountSum(Arith.add(traderUser.getAmountSum(), Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue())));
								traderUserService.update(traderUser);
								/**
								 * 给用户跟随表添加累计金额
								 */
								user.setAmountSum(Arith.add(user.getAmountSum(),Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue())));
								traderFollowUserService.update(user);
							} catch (Exception e) {
								logger.error("TraderFollowUserOrderServiceImpl_error:", e);
							}
						}
						ThreadUtils.sleep(10);
					}
				}
				/**
				 * 交易员自身数据更新
				 */
				Trader trader = traderService.findByPartyId(contractOrder.getPartyId());
				trader.setOrderAmount(Arith.add(trader.getOrderAmount(), Arith.mul(contractOrder.getVolumeOpen().doubleValue(), contractOrder.getUnitAmount().doubleValue())));
				trader.setOrderSum((int) Arith.add(trader.getOrderSum(), 1));
				/**
				 * 近三周数据更新 累计数据更新
				 */
				List<ContractOrder> orders =  contractOrderService.findSubmitted(trader.getPartyId(),"","");
				traderService.updateTrader(trader, orders);

			} catch (Exception e) {
				logger.error("error:", e);
			}
		}

		public CreateDelayThread(ContractOrder contractOrder, ContractApplyOrderService contractApplyOrderService, ContractOrderService contractOrderService, int follow) {
			this.contractOrder = contractOrder;
			this.contractApplyOrderService = contractApplyOrderService;
			this.contractOrderService = contractOrderService;
			this.follow = follow;
		}

	}

	/**
	 * 交易员平仓
	 *
	 */
	public class CloseDelayThread implements Runnable {
		private ContractOrder contractOrder;
		private ContractOrderService contractOrderService;

		public void run() {
			try {
				if (ContractOrder.STATE_CREATED.equals(contractOrder.getState())) {
					List<TraderFollowUserOrder> orders = findByTraderPartyIdAndOrder_no(
							contractOrder.getPartyId(), contractOrder.getOrderNo(),
							ContractOrder.STATE_SUBMITTED);
					if (orders != null) {
						for (TraderFollowUserOrder order : orders) {
							try {
								if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
									ContractOrder user_contract_order = contractOrderService
											.saveClose(order.getPartyId(), order.getUserOrderNo());
									order.setState(ContractOrder.STATE_CREATED);
									traderFollowUserOrderMapper.updateById(order);
//									ApplicationUtil.executeUpdate(order);

									if (user_contract_order != null) {
										closeUserContractOrder(user_contract_order);
									}

								}
							} catch (Exception e) {
								logger.error("error:", e);
							} finally {
							}
							ThreadUtils.sleep(10);
						}
					}
					/**
					 * 交易员自身数据更新
					 */
					Trader trader = traderService.findByPartyId(contractOrder.getPartyId());
					trader.setProfit(Arith.add(trader.getProfit(), contractOrder.getProfit().doubleValue()));
					if (contractOrder.getProfit().compareTo(new BigDecimal(0)) >= 0) {
						trader.setOrderProfit((int) Arith.add(trader.getOrderProfit(), 1));
					} else {
						trader.setOrderLoss((int) Arith.add(trader.getOrderLoss(), 1));
					}
					/**
					 * 近三周数据更新 累计数据更新
					 */
					List<ContractOrder> sorders =  contractOrderService.findSubmitted(trader.getPartyId(),"","");
					traderService.updateTrader(trader, sorders);

					TraderOrder trader_order = new TraderOrder();
					trader_order.setUuid(ApplicationUtil.getCurrentTimeUUID());
					trader_order.setPartyId(contractOrder.getPartyId());
					trader_order.setOrderNo(contractOrder.getOrderNo());
					trader_order.setSymbol(contractOrder.getSymbol());
					trader_order.setProfit(contractOrder.getProfit().doubleValue());
					trader_order.setChangeRatio(contractOrder.getChangeRatio().doubleValue());
					trader_order.setCloseAvgPrice(contractOrder.getCloseAvgPrice().doubleValue());
					trader_order.setTradeAvgPrice(contractOrder.getTradeAvgPrice().doubleValue());
					trader_order.setCloseTime(new Date(contractOrder.getCloseTime()));
					trader_order.setCreateTime(contractOrder.getCreateTime());
					trader_order.setDirection(contractOrder.getDirection());
					trader_order.setLeverRate(contractOrder.getLeverRate().doubleValue());
					trader_order.setState(contractOrder.getState());
					trader_order.setVolumeOpen(contractOrder.getVolumeOpen().doubleValue());

					traderOrderService.save(trader_order);

				}

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public CloseDelayThread(ContractOrder contractOrder, ContractOrderService contractOrderService) {
			this.contractOrder = contractOrder;
			this.contractOrderService = contractOrderService;
		}

	}

	/**
	 * 用户平仓
	 * 
	 * @param contractOrder
	 */
	public void closeUserContractOrder(ContractOrder contractOrder) {
		ThreadUtils.sleep(1000);
		TraderFollowUserOrder traderFollowUserOrder = findByPartyIdAndOrderNo(contractOrder.getPartyId().toString(),
				contractOrder.getOrderNo());
		/**
		 * 是否跟单判断
		 */

		double follow_order_profit = 0;
		if (traderFollowUserOrder != null && contractOrder.getProfit().doubleValue() > 0) {
			Trader trader = traderService.findByPartyId(traderFollowUserOrder.getTraderPartyId());
			follow_order_profit = Arith.mul(contractOrder.getProfit().doubleValue(), trader.getProfitShareRatio());

			FollowWallet wallet = followWalletService.saveWalletByPartyId(contractOrder.getPartyId());
			double wallet_before = wallet.getMoney().doubleValue();
			followWalletService.update(contractOrder.getPartyId(), Arith.sub(0, follow_order_profit));

			FollowMoneyLog moneylog = new FollowMoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
			moneylog.setAmount_before(new BigDecimal(wallet_before));
			moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, follow_order_profit)));
			moneylog.setAmount_after(BigDecimal.valueOf(Arith.sub(wallet.getMoney().doubleValue(), follow_order_profit)));
			// 交易员订单号[traderOrderNo]，跟单用户订单号[orderNo]，跟单手续费[fee]
			moneylog.setLog("Trader order no.[" + traderFollowUserOrder.getTraderOrderNo() + "], copy-trade user order no.["
					+ contractOrder.getOrderNo() + "], copy-trade fee[" + Arith.sub(0, follow_order_profit) + "]");
			moneylog.setUserId(contractOrder.getPartyId());
			moneylog.setWalletType(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_FOLLOW_UP_FEE);

			followMoneyLogService.save(moneylog);

			Wallet wallet_trader = walletService.saveWalletByPartyId(trader.getPartyId());
			double wallet_trader_before = wallet_trader.getMoney().doubleValue();
			walletService.update(wallet_trader.getUserId(), follow_order_profit);

			MoneyLog moneylog_trader = new MoneyLog();
			moneylog_trader.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
			moneylog_trader.setAmount_before(new BigDecimal(wallet_trader_before));
			moneylog_trader.setAmount(new BigDecimal(follow_order_profit));
			moneylog_trader.setAmount_after(BigDecimal.valueOf(Arith.add(wallet_trader.getMoney().doubleValue(), follow_order_profit)));
			// 交易员订单号[traderOrderNo]，跟单用户订单号[orderNo]，带单手续费收益[profit]
				moneylog_trader.setLog("Trader order no.[" + traderFollowUserOrder.getTraderOrderNo() + "], copy-trade user order no.["
					+ contractOrder.getOrderNo() + "], lead-trade fee income[" + follow_order_profit + "]");
			moneylog_trader.setUserId(wallet_trader.getUserId());
			moneylog_trader.setWalletType(Constants.WALLET);
			moneylog_trader.setContent_type(Constants.MONEYLOG_CONTENT_FOLLOW_UP_FEE);

			moneyLogService.save(moneylog_trader);

			/**
			 * 检查是否是跟单订单，如果是需要将TraderFollowUserOrder里的订单状态修改
			 */

			if (traderFollowUserOrder != null) {
				traderFollowUserOrder.setState(contractOrder.getState());
				update(traderFollowUserOrder);

				/**
				 * 将收益加入用户跟随累计
				 */
				TraderUser traderUser = traderUserService.saveTraderUserByPartyId(contractOrder.getPartyId());
				traderUser.setProfit(Arith.add(traderUser.getProfit(), contractOrder.getProfit().doubleValue()));
				traderUserService.update(traderUser);

				TraderFollowUser traderFollowUser = traderFollowUserService.findByPartyIdAndTrader_partyId(
						traderFollowUserOrder.getPartyId(),
						traderFollowUserOrder.getTraderPartyId());
				/**
				 * 给用户跟随表添加累计金额
				 */
				traderFollowUser.setProfit(Arith.add(traderFollowUser.getProfit(), contractOrder.getProfit().doubleValue()));
				traderFollowUserService.update(traderFollowUser);

			}
			saveProfitBounsHandle(contractOrder);
		}
	}

	public TraderFollowUserOrder findByPartyIdAndOrderNo(String partyId, String apply_oder_no) {
//		StringBuffer queryString = new StringBuffer(
//				" where PARTY_ID=? and  USER_ORDER_NO= ? ");
//		List<TraderFollowUserOrder> list = ApplicationUtil.executeSelect(TraderFollowUserOrder.class, queryString.toString(),
//				new Object[] { partyId, apply_oder_no });
		List<TraderFollowUserOrder> list = traderFollowUserOrderMapper.selectList(Wrappers.<TraderFollowUserOrder>lambdaQuery().eq(TraderFollowUserOrder::getPartyId, partyId).eq(TraderFollowUserOrder::getUserOrderNo, apply_oder_no));
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<TraderFollowUserOrder> findByPartyIdAndTraderPartyIdAndState(String partyId, String trader_partyId,
			String state) {
//		StringBuffer queryString = new StringBuffer(
//				" where PARTY_ID=? and TRADER_PARTY_ID = ? and STATE = ? ");
//		List<TraderFollowUserOrder> list = ApplicationUtil.executeSelect(TraderFollowUserOrder.class, queryString.toString(),
//				new Object[] { partyId, trader_partyId, state });
		List<TraderFollowUserOrder> list = traderFollowUserOrderMapper.selectList(Wrappers.<TraderFollowUserOrder>lambdaQuery().eq(TraderFollowUserOrder::getPartyId, partyId)
				.eq(TraderFollowUserOrder::getTraderPartyId, trader_partyId).eq(TraderFollowUserOrder::getState, state));
		if (!list.isEmpty()) {
			return list;
		}
		return null;
	}

	public List<TraderFollowUserOrder> findByTraderPartyIdAndOrder_no(String trader_partyId, String trader_order_no,
			String state) {
//		StringBuffer queryString = new StringBuffer(
//				" where TRADER_PARTY_ID=? and  TRADER_ORDER_NO= ? and STATE = ? ");
//		List<TraderFollowUserOrder> list = ApplicationUtil.executeSelect(TraderFollowUserOrder.class, queryString.toString(),
//				new Object[] { trader_partyId, trader_order_no, state });
		List<TraderFollowUserOrder> list = traderFollowUserOrderMapper.selectList(Wrappers.<TraderFollowUserOrder>lambdaQuery().eq(TraderFollowUserOrder::getTraderOrderNo, trader_order_no)
				.eq(TraderFollowUserOrder::getTraderPartyId, trader_partyId).eq(TraderFollowUserOrder::getState, state));
		if (!list.isEmpty()) {
			return list;
		}
		return null;
	}

	/**
	 * 跟单产生手续费，奖励给推荐人
	 * 
	 * @param entity
	 */
	public void saveFeeBounsHandle(ContractApplyOrder entity) {
		List<UserRecom> recom_parents = userRecomService.getParents(entity.getPartyId());
		if (recom_parents == null) {
			return;
		}
		if (recom_parents.isEmpty()) {
			return;
		}
		/**
		 * 上级为空则直接结束
		 */

		if ("".equals(recom_parents.get(0).getRecomUserId()) || recom_parents.get(0).getRecomUserId() == null) {
			return;
		}

		/**
		 * 获取数据库奖金分成比例
		 */
//		String trade_follow_bonus_parameters = sysparaService.find("trade_follow_bonus_parameters").getValue();
		String trade_follow_bonus_parameters = sysparaService.find("trade_follow_bonus_parameters").getSvalue();
		String[] trade_follow_bonus_array = trade_follow_bonus_parameters.split(",");

		/**
		 * 判断有几个父级代理，最多不超过3个有奖励
		 */
		for (int i = 0; i < recom_parents.size(); i++) {
			if (i >= 3) {
				return;
			}
			/**
			 * 邀请人是正式用户和演示用户才加奖金
			 */
			User party = new User();
			party = userService.cacheUserBy(recom_parents.get(i).getRecomUserId());
			if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
				continue;
			}
			double pip_amount = Double.parseDouble(trade_follow_bonus_array[i]);
			double get_money = Arith.mul(entity.getFee().doubleValue(), pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(recom_parents.get(i).getRecomUserId());
			double amount_before = wallet.getMoney().doubleValue();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
			walletService.update(wallet.getUserId(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_REWARD);
			moneyLog.setAmount_before(new BigDecimal(amount_before));
			moneyLog.setAmount(new BigDecimal(get_money));
			moneyLog.setAmount_after(BigDecimal.valueOf(Arith.add(wallet.getMoney().doubleValue(), get_money)));
			// 第N代用户跟单产生了交易，手续费奖励[get_money]
			moneyLog.setLog("Generation " + (i + 1) + " user copy-trade transaction, fee reward[" + get_money + "]");
			moneyLog.setUserId(recom_parents.get(i).getRecomUserId());
			moneyLog.setWalletType(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
			moneyLogService.save(moneyLog);

		}

	}

	/**
	 * 跟单产生收益，奖励给推荐人
	 * 
	 * @param entity
	 */
	public void saveProfitBounsHandle(ContractOrder entity) {
		List<UserRecom> recom_parents = userRecomService.getParents(entity.getPartyId());
		if (recom_parents == null) {
			return;
		}
		if (recom_parents.isEmpty()) {
			return;
		}
		/**
		 * 上级为空则直接结束
		 */

		if ("".equals(recom_parents.get(0).getRecomUserId()) || recom_parents.get(0).getRecomUserId() == null) {
			return;
		}

		/**
		 * 获取数据库奖金分成比例
		 */
		String trade_follow_profit_bonus_parameters = sysparaService.find("trade_follow_profit_bonus_parameters")
				.getSvalue();
		String[] trade_follow_profit_bonus_array = trade_follow_profit_bonus_parameters.split(",");

		/**
		 * 判断有几个父级代理，最多不超过1个有奖励
		 */
		for (int i = 0; i < recom_parents.size(); i++) {
			if (i >= 1) {
				return;
			}
			/**
			 * 邀请人是正式用户和演示用户才加奖金
			 */
			User party = new User();
			party = userService.cacheUserBy(recom_parents.get(i).getRecomUserId());
			if (!"MEMBER".equals(party.getRoleName()) && !"GUEST".equals(party.getRoleName())) {
				continue;
			}
			double pip_amount = Double.parseDouble(trade_follow_profit_bonus_array[i]);
			double get_money = Arith.mul(entity.getProfit().doubleValue(), pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(recom_parents.get(i).getRecomUserId());
			double amount_before = wallet.getMoney().doubleValue();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
			walletService.update(wallet.getUserId(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_REWARD);
			moneyLog.setAmount_before(new BigDecimal(amount_before));
			moneyLog.setAmount(new BigDecimal(get_money));
			moneyLog.setAmount_after(BigDecimal.valueOf(Arith.add(wallet.getMoney().doubleValue(), get_money)));
			// 第N代用户跟单产生了交易，分红奖励[get_money]
			moneyLog.setLog("Generation " + (i + 1) + " user copy-trade transaction, dividend reward[" + get_money + "]");
			moneyLog.setUserId(recom_parents.get(i).getRecomUserId());
			moneyLog.setWalletType(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
			moneyLogService.save(moneyLog);

		}

	}
}
