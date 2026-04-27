package com.yami.trading.service.miner.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.miner.MinerOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.miner.service.MinerRedisKeys;
import com.yami.trading.service.miner.service.MinerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MinerOrderServiceImpl extends ServiceImpl<MinerOrderMapper, MinerOrder> implements MinerOrderService {
//	protected PagedQueryDao pagedDao;
	@Autowired
	protected WalletService walletService;
	@Autowired
	protected MoneyLogService moneyLogService;
	@Autowired
	protected MinerService minerService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected NamedParameterJdbcOperations namedParameterJdbcTemplate;
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected UserService partyService;
	private Logger log = LoggerFactory.getLogger(MinerOrderServiceImpl.class);
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected UserService secUserService;
//	protected RedisHandler redisHandler;
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	protected DataService dataService;

	/**
	 * 管理员新增订单
	 * 
	 * @param entity
	 * @param operator
	 */
	public void saveCreateByManage(MinerOrder entity, String operator) {
		saveCreate(entity, true);
		User secUser = secUserService.cacheUserBy(entity.getPartyId());
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setExtra(entity.getOrder_no());
		log.setOperator(operator);
		log.setUsername(secUser.getUserName());
		log.setUserId(entity.getPartyId().toString());
		log.setLog("手动新增矿机订单。订单号[" + entity.getOrder_no() + "],订单金额[" + entity.getAmount() + "]。");

		logService.save(log);
	}

	/**
	 * 矿机下单
	 * @param entity
	 * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
	 */
	public void saveCreateNew(MinerOrder entity, boolean isManage) {
		entity.setCreate_time(new Date());
		String partyId = entity.getPartyId().toString();
		Miner miner = minerService.findById(entity.getMiner_id());
		if (null == miner) {
			throw new BusinessException("矿机不存在");
		}
		// 管理员解锁所有，用户正常流程
		if (!isManage && "0".equals(miner.getOn_sale())) {
			throw new BusinessException("矿机未解锁，无法购买");
		}

		if (miner.getTest().equals("Y") && this.findByTest(partyId)) {
			throw new BusinessException("您已购买过体验矿机,不得重复购买");
		}

		// 买入金额需要在区间内(非体验矿机)
		if (!miner.getTest().equals("Y")
				&& (entity.getAmount() < miner.getInvestment_min() || entity.getAmount() > miner.getInvestment_max())) {
			if (miner.getInvestment_max() != 0) {
				throw new BusinessException("买入金额需要在区间内");
			}
			// 无限制的矿机不得小于最小购买金额
			else if (entity.getAmount() < miner.getInvestment_min()) {
				throw new BusinessException("买入金额需要在区间内");
			}
		}

		// 起息时间 = 确认时间加1天
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		// 把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(calendar.DATE, 1);
		// 这个时间就是日期往后推一天的结果
		date = calendar.getTime();
		entity.setEarn_time(date);

		if (miner.getTest().equals("Y")) {
			int days = (int) Arith.sub(miner.getCycle(), 1);
			calendar.add(calendar.DATE, days);
			date = calendar.getTime();
			entity.setStop_time(DateUtils.getDayEnd(date));
			// 体验矿机不管输入多少都为0
			entity.setAmount(0d);
		}

		if (findByFist(partyId)) {
			// 标识首次购买
			entity.setFirst_buy("1");
		} else {
			// 标识首次购买
			entity.setFirst_buy("0");
		}

		double amount=entity.getAmount();
		if(amount<0) {
			entity.setAmount(-amount);
		}

		// 扣钱
		String buyCurrency = miner.getBuy_currency();
		double close = 0;
		if ("usdt".equals(buyCurrency)) {
			saveMinerBuyUsdt(entity);
		} else {
			List<Realtime> realtimes = this.dataService.realtime(buyCurrency);
			if (null == realtimes || realtimes.size() <= 0) {
				throw new BusinessException("行情获取异常，稍后再试");
			}
			close = realtimes.get(0).getClose();
			saveMinerBuyOtherCoin(entity, buyCurrency);
		}

		this.insertMinerOrder(entity);

		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

		if (!miner.getTest().equals("Y")) {

			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);

			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			map_partyid.put(entity.getOrder_no(), entity);
			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId, map_partyid);

			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();

				Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);

				redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId,
						Arith.add(null == minerAssets ? 0.000D : minerAssets, minerAssetsOrder));
			}
		}

		// 首次购买则给予上级额外本金百分比奖励
		if ("1".equals(entity.getFirst_buy()) && !miner.getTest().equals("Y")) {
			List<UserRecom> list_parents = this.userRecomService.getParents(partyId);

			if (CollectionUtils.isNotEmpty(list_parents)) {
				String miner_bonus_parameters = "";
				int loop = 0;
				for (int i = 0; i < list_parents.size(); i++) {
					if (loop >= 3) {
						break;
					}
					User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
					if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
						continue;
					}
					loop++;
					String parentId = party_parent.getUserId();
					Map<String, MinerOrder> map_party = (Map<String, MinerOrder>) redisTemplate.opsForValue()
							.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + parentId);
					if (map_party == null || map_party.size() == 0) {
						continue;
					}

					// 判断是否
					int cycle = 0;
					Iterator<Entry<String, MinerOrder>> it = map_party.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, MinerOrder> entry = it.next();
						MinerOrder minerOrder = entry.getValue();
						if (!"1".equals(minerOrder.getState())) {
							continue;
						}
						Miner miner_party = this.minerService.cacheById(minerOrder.getMiner_id());
						if (cycle < miner_party.getCycle()) {
							cycle = miner_party.getCycle();
						}
					}

					if (cycle >= miner.getCycle()) {
						double pip_amount =0;
						// 增加首次推荐人收益
						Syspara syspara = sysparaService.find("miner_first_bonus_parameters");
						if(ObjectUtils.isNotEmpty(syspara)) {
							miner_bonus_parameters = syspara.getSvalue();
							if(StringUtils.isNotEmpty(miner_bonus_parameters)) {
								String[] miner_bonus_array = miner_bonus_parameters.split(",");
								pip_amount = Double.valueOf(miner_bonus_array[loop - 1]);
							}
						}
						double get_money = Arith.mul(entity.getAmount(), pip_amount);
						if ("usdt".equals(buyCurrency)) {
							firstBuyProfitUsdt(party_parent, get_money, i);
						} else {
							firstBuyProfitOtherCoin(buyCurrency, party_parent, get_money, i);
							// 转化成usdt，统计计算
							userDataService.saveMinerProfit(parentId, Arith.div(get_money, close));
						}
					}
				}
			}
		}

		// userdata 数据
		if ("usdt".equals(buyCurrency)) {
			userDataService.saveMinerBuy(entity);
		} else {
			userDataService.saveMinerBuy(minerUserDataOtherCoin(entity, buyCurrency, close));
		}
	}


	/**
	 * 矿机下单
	 * 
	 * @param entity
	 * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
	 */
	@Override
	public void saveCreate(MinerOrder entity, boolean isManage) {

		entity.setCreate_time(new Date());

//		Party party = this.partyService.findPartyById(entity.getPartyId());
//		if (!party.getEnabled()) {
//			throw new BusinessException(1, "No permission");
//		}
		/**
		 * 加入周期
		 */
		Miner miner = minerService.findById(entity.getMiner_id());
		if (null == miner) {
			throw new BusinessException("矿机不存在");
		}
		if (!isManage && "0".equals(miner.getOn_sale())) {// 管理员解锁所有，用户正常流程
//			if (!this.getUnLockMiner(entity.getPartyId().toString(), miner.getId().toString())) {
			throw new BusinessException("矿机未解锁，无法购买");
//			}
		}
//		entity.setAmount(Arith.mul(miner.getInvestment_min(), entity.getVolume()));
//		entity.setCycle(miner.getCycle());

		if (miner.getTest().equals("Y") && this.findByTest(entity.getPartyId().toString())) {// 买过体验机则
			throw new BusinessException("您已购买过体验矿机,不得重复购买");
		}
		/**
		 * 买入金额需要在区间内
		 */
//		if (entity.getAmount() < miner.getInvestment_min()) {
//			throw new BusinessException("不得低于该矿机的金额");
//
//		}
		/**
		 * 买入金额需要在区间内(非体验矿机)
		 */
		if (miner.getTest().equals("N")
				&& (entity.getAmount() < miner.getInvestment_min() || entity.getAmount() > miner.getInvestment_max())) {
			if (miner.getInvestment_max() != 0) {
				throw new BusinessException("买入金额需要在区间内");
			} else if (entity.getAmount() < miner.getInvestment_min()) {// 无限制的矿机不得小于最小购买金额
				throw new BusinessException("买入金额需要在区间内");
			}
		}

		String minerBuySymbol = sysparaService.find("miner_buy_symbol").getSvalue();
		// 是否是其他币种购买
		boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
		double close = 0d;
		if (isOtherCoin) {
			List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
			if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
				throw new BusinessException("系统错误，请稍后重试 saveCreate");
			}
			close = realtimes.get(0).getClose();

			saveMinerBuyOtherCoin(entity, minerBuySymbol);
		} else {
			/**
			 * 查看余额
			 */
			Wallet wallet = this.walletService.saveWalletByPartyId(String.valueOf(entity.getPartyId()));
			double amount_before = wallet.getMoney().doubleValue();
			if (wallet.getMoney().doubleValue() < entity.getAmount()) {
				throw new BusinessException("余额不足");
			}

//		wallet.setMoney(Arith.sub(wallet.getMoney(), entity.getAmount()));
			this.walletService.update(wallet.getUserId(), Arith.sub(0, entity.getAmount()));
			/**
			 * 保存资金日志
			 */

			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
			moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
			moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
			moneylog.setAmountAfter(
					BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
			moneylog.setLog("购买矿机产品，订单号[" + entity.getOrder_no() + "]");
			moneylog.setUserId(entity.getPartyId());
			moneylog.setWalletType(Constants.WALLET);
			moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);

			moneyLogService.save(moneylog);
		}
		/**
		 * 起息时间=确认时间加1天
		 */
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		entity.setEarn_time(date);

		if (miner.getTest().equals("Y")) {
			/**
			 * 截止时间=起息时间+周期+1
			 */
//			Date date = new Date();
//			Calendar calendar1 = new GregorianCalendar();
			int days = (int) Arith.sub(miner.getCycle(), 1);
			calendar.add(calendar.DATE, days);
			date = calendar.getTime();

			entity.setStop_time(DateUtils.getDayEnd(date));
			entity.setAmount(0d);// 体验矿机不管输入多少都为0
		}
		if (findByFist(entity.getPartyId().toString())) {
			entity.setFirst_buy("1");// 标识首次购买
		} else {
			entity.setFirst_buy("0");// 标识首次购买
		}
		this.save(entity);

		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

		if (miner.getTest().equals("N")) {
			
			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue()
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());

			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			map_partyid.put(entity.getOrder_no(), entity);

			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);
			
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {
				
				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();

				Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());

				redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(),
						Arith.add(null == minerAssets ? 0.000D : minerAssets, minerAssetsOrder));
			}			
		}
		
		/**
		 * 首次购买则给予上级额外本金百分比奖励
		 */
		if ("1".equals(entity.getFirst_buy()) && miner.getTest().equals("N")) {
			List<UserRecom> list_parents = this.userRecomService.getParents(entity.getPartyId().toString());

			if (CollectionUtils.isNotEmpty(list_parents)) {
				String miner_bonus_parameters = "";
				miner_bonus_parameters = sysparaService.find("miner_first_bonus_parameters").getSvalue();
				String[] miner_bonus_array = miner_bonus_parameters.split(",");
				int loop = 0;
				for (int i = 0; i < list_parents.size(); i++) {
					if (loop >= 3) {
						break;
					}

					User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
					if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
						continue;
					}
					loop++;
					Map<String, MinerOrder> map_party = (Map<String, MinerOrder>) redisTemplate.opsForValue()
							.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + party_parent.getUserId());
					if (map_party == null || map_party.isEmpty()) {
						continue;
					}
					/*
					 * 判断是否
					 */
					int cycle = 0;
					Iterator<Entry<String, MinerOrder>> it = map_party.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, MinerOrder> entry = it.next();
						MinerOrder minerOrder = entry.getValue();
						if (!"1".equals(minerOrder.getState())) {
							continue;
						}
						Miner miner_party = this.minerService.cacheById(minerOrder.getMiner_id());
						if (cycle < miner_party.getCycle()) {
							cycle = miner_party.getCycle();
						}

					}

					if (cycle >= miner.getCycle()) {
						/**
						 * 增加首次推荐人收益
						 */
						double pip_amount = Double.valueOf(miner_bonus_array[loop - 1]);
						double get_money = Arith.mul(entity.getAmount(), pip_amount);

						if (isOtherCoin) {
							firstBuyProfitOtherCoin(minerBuySymbol, party_parent, get_money, i);
							// 转化成usdt，统计计算
							userDataService.saveMinerProfit(party_parent.getUserId(),
									Arith.div(get_money, close));
						} else {
							Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getUserId());
							double amount_before_parent = wallet_parent.getMoney().doubleValue();
//						wallet_parent.setMoney(Arith.add(wallet_parent.getMoney(), get_money));
//						walletService.update(wallet_parent);
							walletService.update(wallet_parent.getUserId(), get_money);

							/**
							 * 保存资金日志
							 */
							MoneyLog moneyLog = new MoneyLog();
							moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
							moneyLog.setAmountBefore(BigDecimal.valueOf(amount_before_parent));
							moneyLog.setAmount(BigDecimal.valueOf(get_money));
							moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before_parent, get_money)));
							moneyLog.setLog("第" + (i + 1) + "代下级用户，首次购买矿机推荐奖励金");
							moneyLog.setUserId(party_parent.getUserId());
							moneyLog.setWalletType(Constants.WALLET);
							moneyLog.setContentType(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
							moneyLogService.save(moneyLog);
							userDataService.saveMinerProfit(party_parent.getUserId().toString(), get_money);

						}

					}
				}

			}
		}

		/**
		 * userdata 数据
		 */
		if (isOtherCoin) {
			userDataService.saveMinerBuy(minerUserDataOtherCoin(entity, minerBuySymbol, close));
		} else {
			userDataService.saveMinerBuy(entity);
		}

	}

	/**
	 * 增加首次推荐人收益
	 */
	protected void firstBuyProfitOtherCoin(String symbol, User partyParent, double getMoney, int i) {
		WalletExtend walletExtend = walletService.saveExtendByPara(partyParent.getUserId(), symbol);
		double amount_before_parent = walletExtend.getAmount();
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), symbol, getMoney);

		/**
		 * 保存资金日志
		 */
		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneyLog.setAmountBefore(BigDecimal.valueOf(amount_before_parent));
		moneyLog.setAmount(BigDecimal.valueOf(getMoney));
		moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before_parent, getMoney)));
		moneyLog.setLog("第" + (i + 1) + "代下级用户，首次购买矿机推荐奖励金");
		moneyLog.setUserId(partyParent.getUserId());
		moneyLog.setWalletType(symbol);
		moneyLog.setContentType(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
		moneyLogService.save(moneyLog);
		userDataService.saveMinerProfit(walletExtend.getPartyId().toString(), getMoney);
	}
	
	/**
	 * 增加首次推荐人收益
	 */
	protected void firstBuyProfitUsdt(User partyParent, double getMoney, int i) {
		Wallet wallet_parent = walletService.saveWalletByPartyId(partyParent.getUserId().toString());
		double amount_before_parent = wallet_parent.getMoney().doubleValue();
		walletService.update(wallet_parent.getUserId().toString(), getMoney);

		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneyLog.setAmountBefore(BigDecimal.valueOf(amount_before_parent));
		moneyLog.setAmount(BigDecimal.valueOf(getMoney));
		moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before_parent, getMoney)));
		moneyLog.setLog("第" + (i + 1) + "代下级用户，首次购买矿机推荐奖励金");
		moneyLog.setUserId(partyParent.getUserId());
		moneyLog.setWalletType(Constants.WALLET);
		moneyLog.setContentType(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
		moneyLogService.save(moneyLog);
		userDataService.saveMinerProfit(partyParent.getUserId(), getMoney);
	}

	protected MinerOrder minerUserDataOtherCoin(MinerOrder entity, String symbol, double close) {
		MinerOrder order = new MinerOrder();
		// 不改变原有的
		BeanUtils.copyProperties(entity, order);
		// 转化成usdt，统计计算
		order.setAmount(Arith.div(order.getAmount(), close));
		return order;
	}
	
	protected void saveMinerBuyUsdt(MinerOrder entity) {
		Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
		double amount_before = wallet.getMoney().doubleValue();
		if (wallet.getMoney().doubleValue() < entity.getAmount()) {
			throw new BusinessException("余额不足");
		}

		this.walletService.update(wallet.getUserId().toString(), Arith.sub(0, entity.getAmount()));

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
		moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
		moneylog.setLog("购买矿机产品，订单号[" + entity.getOrder_no() + "]");
		moneylog.setUserId(entity.getPartyId());
		moneylog.setWalletType(Constants.WALLET);
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);
		moneyLogService.save(moneylog);
	}

	protected void saveMinerBuyOtherCoin(MinerOrder entity, String symbol) {
		WalletExtend walletExtend = walletService.saveExtendByPara(entity.getUuid(), symbol);

		if (entity.getAmount() > walletExtend.getAmount()) {
			throw new BusinessException("持有币种不足");
		}

		double amount_before = walletExtend.getAmount();
//		walletExtend.setAmount(Arith.sub(walletExtend.getAmount(), order.getVolume()));

//		walletService.save(walletExtend);
		walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
				Arith.sub(0, entity.getAmount()));
		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
		moneylog.setAmount(BigDecimal.valueOf(Arith.sub(0, entity.getAmount())));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amount_before, entity.getAmount())));
		moneylog.setLog("购买矿机产品，订单号[" + entity.getOrder_no() + "]");
		moneylog.setUserId(entity.getUuid());
		moneylog.setWalletType(symbol);
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BUY);
		moneyLogService.save(moneylog);
	}
	
	protected void saveMinerCloseUsdt(MinerOrder entity) {
		Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
		double amount_before = wallet.getMoney().doubleValue();
		double back_money = entity.getAmount();
		walletService.update(wallet.getUserId().toString(), back_money);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
		moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, back_money)));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before, back_money)));
		moneylog.setUserId(entity.getPartyId());
		moneylog.setWalletType(Constants.WALLET);
		moneylog.setLog("矿机本金退回，订单号[" + entity.getOrder_no() + "]");
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);
		moneyLogService.save(moneylog);
	}

	protected void saveMinerCloseOtherCoin(MinerOrder entity, String symbol) {

		WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId().toString(), symbol);

		double amount_before = walletExtend.getAmount();
		double back_money = entity.getAmount();
//		walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
//		this.walletService.update(walletExtend);
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), back_money);

		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog_deposit.setAmountBefore(BigDecimal.valueOf(amount_before));
		moneylog_deposit.setAmount(BigDecimal.valueOf(back_money));
		moneylog_deposit.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before, back_money)));
		moneylog_deposit.setLog("矿机本金退回，订单号[" + entity.getOrder_no() + "]");
		moneylog_deposit.setUserId(entity.getPartyId().toString());
		moneylog_deposit.setWalletType(symbol);
		moneylog_deposit.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);

		moneyLogService.save(moneylog_deposit);
	}
	
	/**
	 * 赎回
	 */
	public void saveClose(MinerOrder entity, Miner miner) {
		String buyCurrency = miner.getBuy_currency();
		double close = 0;
		if ("usdt".equals(buyCurrency)) {
			if (entity.getAmount() != 0) {
				saveMinerCloseUsdt(entity);
			}
		} else {
			List<Realtime> realtimes = this.dataService.realtime(buyCurrency);
			if (null == realtimes || realtimes.size() <= 0) {
				throw new BusinessException("行情获取异常，稍后再试");
			}
			close = realtimes.get(0).getClose();
			saveMinerCloseOtherCoin(entity, buyCurrency);
		}

		entity.setClose_time(new Date());// 赎回时间
		
		this.updateMinerOrder(entity);

		// userdata 数据
		if ("usdt".equals(buyCurrency)) {
			userDataService.saveMinerClose(entity);
		} else {
			userDataService.saveMinerClose(minerUserDataOtherCoin(entity, buyCurrency, close));
		}

		// 更新矿机订单
		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);
		String partyId = entity.getPartyId().toString();
		if (!miner.getTest().equals("Y")) {
			
			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);
			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			
			map_partyid.put(entity.getOrder_no(), entity);
			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId, map_partyid);
			
			Double minerAssets = (Double) this.redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);
								
			MinerOrder minerOld = map_partyid.get(entity.getOrder_no());
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(minerOld.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOld = minerOld.getAmount();
				
				minerAssets = null == minerAssets ? 0.000D - minerAssetsOld : minerAssets - minerAssetsOld;
			}

			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();
								
				minerAssets = null == minerAssets ? minerAssetsOrder : minerAssets + minerAssetsOrder;
			}

			this.redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId, null == minerAssets ? 0.000D : minerAssets);
		}
	}

	@Override
	public void saveClose(MinerOrder entity) {
		String minerBuySymbol = sysparaService.find("miner_buy_symbol").getSvalue();
		// 是否是其他币种购买
		boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
		double close = 0;
		if (isOtherCoin) {
			List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
			if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
				throw new BusinessException("系统错误，请稍后重试 saveClose");
			}
			close = realtimes.get(0).getClose();

			saveMinerCloseOtherCoin(entity, minerBuySymbol);
		} else if (entity.getAmount() != 0) {// 体验矿机，购买价为0
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId().toString());
			double amount_before = wallet.getMoney().doubleValue();
			// 购买金额-违约金=退还金额
//			double back_money = Arith.sub(entity.getAmount(), entity.getDefault_money());
			double back_money = entity.getAmount();
//			if ("0".equals(entity.getState())) {// 正常状态下 到期后一天 奖励5%
//				double profit = entity.getCompute_profit();
//				back_money = profit;
//			}
//			wallet.setMoney(Arith.add(wallet.getMoney(), back_money));
			this.walletService.update(wallet.getUserId(), back_money);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
			moneylog.setAmountBefore(BigDecimal.valueOf(amount_before));
			moneylog.setAmount(BigDecimal.valueOf(Arith.add(0, back_money)));
			moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amount_before, back_money)));
			moneylog.setUserId(entity.getUuid());
			moneylog.setWalletType(Constants.WALLET);
			moneylog.setLog("矿机本金退回，订单号[" + entity.getOrder_no() + "]");
			moneylog.setContentType(Constants.MONEYLOG_CONTENT_MINER_BACK);
			moneyLogService.save(moneylog);
		}

		entity.setClose_time(new Date());// 赎回时间
		this.updateById(entity);
		/**
		 * userdata 数据
		 */
		if (isOtherCoin) {
			userDataService.saveMinerClose(minerUserDataOtherCoin(entity, minerBuySymbol, close));
		} else {
			userDataService.saveMinerClose(entity);
		}

		Miner miner = this.minerService.cacheById(entity.getMiner_id());

		// 更新矿机订单
		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);
		if (miner.getTest() == "N") {
			
			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisTemplate.opsForValue()
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());
			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			
			MinerOrder minerOld = map_partyid.get(entity.getOrder_no());
			
			map_partyid.put(entity.getOrder_no(), entity);
			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);
			
			Double minerAssets = (Double) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());
									
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(minerOld.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOld = minerOld.getAmount();
				
				minerAssets = null == minerAssets ? 0.000D - minerAssetsOld : minerAssets - minerAssetsOld;
			}

			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();
								
				minerAssets = null == minerAssets ? 0.000D + minerAssetsOrder : minerAssets + minerAssetsOrder;
			}

			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(), null == minerAssets ? 0.000D : minerAssets);
		}
	}

	public List<Map<String, Object>> findAllByState(String state) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("FROM MinerOrder WHERE state =:state ");
		parameters.put("state", state);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);


//		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 ",
//				new Object[] { state });
		return list;
	}

	@Override
	public MinerOrder findByOrder_no(String order_no) {

		return (MinerOrder) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ORDER_ORDERNO + order_no);

	}

	@Override
	public List<Map<String, Object>> findByState(String partyId, String state) {
		if (StringUtils.isEmptyString(partyId)) {
			return findAllByState(state);
		}
		/**
		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
		 */
		List<Map<String, Object>> list;
		if (StringUtils.isEmptyString(state)) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE  party_id =:partyId");
			parameters.put("partyId", partyId);
			list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//			list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE  partyId =?0", new Object[] { partyId });
		} else {
			if ("2".equals(state)) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE party_id =?0 and ( state = 0 or state = 2 )");
				parameters.put("partyId", partyId);
				list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE partyId =?0 and ( state = ?1 or state =?2 )  ",
//						new Object[] { partyId, "0", "2" });
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer queryString = new StringBuffer("select * FROM t_miner_order WHERE state = :state and party_id =:partyId");
				parameters.put("state", state);
				parameters.put("partyId", partyId);
				list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
//				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 and partyId =?1",
//						new Object[] { state, partyId });
			}
		}

		if (!list.isEmpty())
			return list;
		return null;
	}

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
		Page page = new Page(pageNo,pageSize);
		this.baseMapper.pagedQuery1(page,partyId,state);
		return page;
	}

	@Override
	public void deleteAllByPartyId(String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("FROM MinerOrder WHERE partyId=:partyId ");
		parameters.put("partyId", partyId);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);

		//
//		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE partyId=? ",
//				new Object[] { partyId });
		if (!CollectionUtils.isEmpty(list)) {
			for (Map<String, Object> order : list) {
//				this.getHibernateTemplate().delete(order);
//				this.removeById(order);
//				redisTemplate.delete(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no());
			}
			redisTemplate.delete(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);
			
			this.redisTemplate.delete(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);
		}
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setMinerService(MinerService minerService) {
		this.minerService = minerService;
	}

	public MinerOrder findById(String id) {// 赎回时使用

//		namedParameterJdbcTemplate.
		return this.baseMapper.selectById(id);
//		return (MinerOrder) getHibernateTemplate().get(MinerOrder.class, id);
	}

	/**
	 * true:买过体验矿机，false:没买过
	 */
	@Override
	public boolean findByTest(String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("mo.UUID ");
		queryString.append("FROM T_MINER_ORDER mo ");
		queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
		queryString.append("WHERE 1=1  ");
		queryString.append("AND PARTY_ID=:partyId AND m.TEST='Y' ");
		parameters.put("partyId", partyId);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null;// 存在返回值，且不为空
	}

	/**
	 * true：首次购买，false:非首次购买
	 * 
	 * @param partyId
	 * @return
	 */
	@Override
	public boolean findByFist(String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("mo.UUID ");
		queryString.append("FROM T_MINER_ORDER mo ");
		queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
		queryString.append("WHERE 1=1  ");
		queryString.append("AND PARTY_ID=:partyId AND m.TEST='N' ");
		queryString.append("AND FIRST_BUY='1' ");
		parameters.put("partyId", partyId);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return !(list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null);// 存在返回值，且不为空
	}

	@Override
	public boolean getUnLockMiner(String partyId, String minerId) {
		Miner miner = this.minerService.cacheById(minerId);

		List<UserRecom> list_userRecoms = this.userRecomService.findRecoms(partyId);
		int cycle = 0;
		for (int i = 0; i < list_userRecoms.size(); i++) {
			Map<String, MinerOrder> map = (Map<String, MinerOrder>) redisTemplate.opsForValue()
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + list_userRecoms.get(i).getUserId().toString());

			if (map != null) {
				cycle = cycle + map.size();
			}
		}

		return cycle >= miner.getCycle();// 如果周期比该矿机的大，则解锁
	}
	
	/**
	 * 新增矿机订单
	 */
	public void insertMinerOrder(MinerOrder order) {
		this.save(order);
	}
	
	/**
	 * 修改矿机订单
	 */
	public void updateMinerOrder(MinerOrder order) {
		this.updateById(order);
	}

}
