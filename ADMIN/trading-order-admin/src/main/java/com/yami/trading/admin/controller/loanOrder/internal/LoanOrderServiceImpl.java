package com.yami.trading.admin.controller.loanOrder.internal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.admin.controller.loanOrder.LoanConstants;
import com.yami.trading.admin.controller.loanOrder.LoanOrderService;
import com.yami.trading.admin.controller.loanOrder.LoanRelationOrderService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.loanOrder.LoanOrder;
import com.yami.trading.bean.loanOrder.LoanRelationOrder;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.dao.loan.LoanOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * 质押借币service实现类
 */
@Service
@Transactional
public class LoanOrderServiceImpl extends ServiceImpl<LoanOrderMapper, LoanOrder> implements LoanOrderService {

	@Autowired
	private DataService dataService;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
//	private PagedQueryDao pagedQueryDao;

	@Autowired
	private WalletService walletService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private MoneyLogService moneyLogService;
	@Autowired
	private LoanRelationOrderService loanRelationOrderService;
	
	private Logger logger = LoggerFactory.getLogger(LoanOrderServiceImpl.class);
	
	protected Map<String, LoanOrder> cache = new ConcurrentHashMap<String, LoanOrder>();

	@PostConstruct
	public void init() {
		List<Map<String,Object>> list = queryOrdersByState();
		for (Map<String,Object> map : list) {
			LoanOrder order = JSON.parseObject(JSON.toJSONString(map), LoanOrder.class);
			cache.put(order.getUuid(), order);
		}
	}
	
	/**
	 * 新增借币订单
	 */
	public void saveLoanOrder(LoanOrder order) {
		
		WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getPledgeCurrency());
		if (walletExtend.getAmount() < order.getPledge_amount()) {
			throw new BusinessException(1, "Insufficient balance");
		}
		
		double amountBeforeExtend = walletExtend.getAmount();
		double pledgeAmount = order.getPledge_amount();
		String partyId = walletExtend.getPartyId().toString();
		// 修改拓展钱包 余额 及冻结余额
		walletService.updateExtend(partyId, walletExtend.getWallettype(), Arith.sub(0, pledgeAmount), pledgeAmount);

		String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
		
		// 冻结资金日志
		MoneyLog logExtend = new MoneyLog();
		logExtend.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		logExtend.setAmountBefore(BigDecimal.valueOf(amountBeforeExtend));
		logExtend.setAmount(BigDecimal.ZERO.subtract(BigDecimal.valueOf(pledgeAmount)));
		logExtend.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBeforeExtend, -pledgeAmount)));
		logExtend.setLog("质押借币，订单号[" + orderNo + "]，" + "冻结：" + pledgeAmount);
		logExtend.setUserId(partyId);
		logExtend.setWalletType(order.getPledgeCurrency());
		logExtend.setContentType(Constants.MONEYLOG_CONTENT_LOAN_FROZEN);
		logExtend.setCreateTime(new Date());
		moneyLogService.save(logExtend);
		
		Wallet wallet = walletService.saveWalletByPartyId(partyId);
		double amountBefore = wallet.getMoney().doubleValue();
		double loanAmount = order.getLoanAmount();
		// 修改主钱包余额
		walletService.update(partyId, loanAmount);
		
		// 借款资金日志
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
		moneylog.setAmount(BigDecimal.valueOf(loanAmount));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, loanAmount)));
		// 质押借币，订单号[orderNo]，借款：loanAmount
			moneylog.setLog("Pledge loan, order no.[" + orderNo + "], loan amount: " + loanAmount);
		moneylog.setUserId(partyId);
		moneylog.setWalletType(Constants.WALLET);
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_LOAN_ADD);
		moneylog.setCreateTime(new Date());
		moneyLogService.save(moneylog);
		
		// 质押借币记录
		order.setOrderNo(orderNo);
		order.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_LOAN);
		order.setState(LoanConstants.PLEDGE_ORDER_STATE_CALCULATE);
		order.setHourlyRate(Double.valueOf(getLoanConfig().get("hourlyRate").toString()));
		order.setDebtAmount(loanAmount);
		order.setPledgeType(LoanConstants.PLEDGE_TYPE);
		order.setLoanCurrency("usd");
		order.setOverdueRate(Double.valueOf(getLoanConfig().get("overdueRate").toString()));
		
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, order.getLoan_cycle());
		Date expireTime = calendar.getTime();
		order.setCreateTime(date);
		order.setExpire_time(expireTime);
		
		String insertSql = "INSERT INTO T_LOAN_ORDER(UUID,ORDER_NO,PARTY_ID,ORDER_TYPE,LOAN_AMOUNT,STATE,LOAN_CURRENCY,PLEDGE_CURRENCY,"
				+ "PLEDGE_AMOUNT,PLEDGE_RATE,PLEDGE_TYPE,DEBT_AMOUNT,INTEREST_AMOUNT,OVERDUE_AMOUNT,OVERDUE_RATE,HOURLY_RATE,LOAN_CYCLE,"
				+ "CREATE_TIME,EXPIRE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(insertSql,order.getUuid(),order.getOrderNo(),order.getPartyId(),order.getOrder_type(),order.getLoanAmount(),
				order.getState(),order.getLoanCurrency(),order.getPledgeCurrency(),order.getPledge_amount(),order.getPledge_rate(),order.getPledge_type(),
				order.getDebt_amount(),order.getInterest_amount(),order.getOverdue_amount(),order.getOverdue_rate(),order.getHourly_rate(),
				order.getLoan_cycle(),order.getCreate_time(),order.getExpire_time());
		// 放入缓存
		cache.put(order.getUuid(), order);
	}
	
	/**
	 * 修改质押借币订单
	 */
	public void updateLoanOrder(LoanOrder order) {
		String updateSql = "UPDATE T_LOAN_ORDER SET LOAN_AMOUNT=?,STATE=?,LOAN_CURRENCY=?,PLEDGE_CURRENCY=?,PLEDGE_AMOUNT=?,PLEDGE_RATE=?,"
				+ "PLEDGE_TYPE=?,DEBT_AMOUNT=?,INTEREST_AMOUNT=?,OVERDUE_AMOUNT=?,OVERDUE_RATE=?,HOURLY_RATE=?,LOAN_CYCLE=?,EXPIRE_TIME=?"
				+ " WHERE UUID=?";
		jdbcTemplate.update(updateSql,order.getLoanAmount(),order.getState(),order.getLoanCurrency(),order.getPledgeCurrency(),
				order.getPledge_amount(),order.getPledge_rate(),order.getPledge_type(),order.getDebt_amount(),order.getInterest_amount(),order.getOverdue_amount(),
				order.getOverdue_rate(),order.getHourly_rate(),order.getLoan_cycle(),order.getExpire_time(),order.getUuid());
	}
	
	/**
	 * 质押借币订单
	 */
	public List<Map<String, Object>> pagedQuery(int pageNo, int pageSize, String partyId) {
		if (pageNo <= 0) pageNo = 1;
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);
//		List<LoanOrder> orders = ApplicationUtil.executeSelect(LoanOrder.class,"WHERE PARTY_ID=? ORDER BY CREATE_TIME DESC LIMIT ?,?",new Object[] {partyId,page.getFirstElementNumber(),pageSize});
		String sql = "select * from t_loan_order WHERE 1=1 and party_id="+"'"+partyId+"'"+" ORDER BY CREATE_TIME DESC LIMIT "+(page.getCurrent()-1)*pageSize+","+pageSize+";";
		System.out.println(sql);
		List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);

		// 借款记录
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> map1 : maps) {
			LoanOrder order = JSON.parseObject(JSON.toJSONString(map1), LoanOrder.class);
			Map<String, Object> map = new HashMap<>();
			map.put("id", order.getUuid());
			map.put("orderType", order.getOrder_type());
			map.put("loanAmount", order.getLoanAmount());
			map.put("state", order.getState());
			String loanCurrency = order.getLoanCurrency();
			if("usdt".equalsIgnoreCase(loanCurrency)){
				loanCurrency = "usd";
			}
			map.put("loanCurrency", loanCurrency);
			if (LoanConstants.PLEDGE_ORDER_STATE_CALCULATE == order.getState() || LoanConstants.PLEDGE_ORDER_STATE_OVERDUE == order.getState()) {
				List<LoanRelationOrder> relations = loanRelationOrderService.queryOrders(order.getUuid(), LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH);
				double pledgeAmount = order.getPledge_amount();
				for (LoanRelationOrder relationOrder : relations) {
					pledgeAmount = Arith.add(pledgeAmount, relationOrder.getPledge_amount());
				}
				
				Map<String, Double> calculateMap = calculatePledgeRate(order.getPledgeCurrency(), order.getDebt_amount(), pledgeAmount);
				map.put("pledgeRate", calculateMap.get("pledgeRate"));
			}else {
				map.put("pledgeRate", order.getPledge_rate());
			}
			map.put("debtAmount", order.getDebt_amount());
			list.add(map);		
		}
		return list;
	}
	
	/**
	 * 根据订单关联ID获取订单列表
	 */
	public List<Map<String,Object>> queryOrdersByState() {
		List<Map<String,Object>> list = jdbcTemplate.queryForList("SELECT * FROM T_LOAN_ORDER WHERE STATE = 1 OR STATE = 4");
//		List<LoanOrder> list = jdbcTemplate.query("SELECT * FROM T_LOAN_ORDER WHERE STATE = 1 OR STATE = 4",
//				RecordObjectMapper.newInstance(LoanOrder.class));
		return list;
	}
	
	/**
	 * 根据订单关联ID获取订单列表
	 */
	public List<String> queryOrdersNoticeList(String partyId) {
//		List<LoanOrder> list = jdbcTemplate.query("SELECT * FROM T_LOAN_ORDER WHERE (STATE = 1 OR STATE = 4) AND PARTY_ID =?",
//				RecordObjectMapper.newInstance(LoanOrder.class), partyId);


		List<Map<String,Object>> list = jdbcTemplate.queryForList("SELECT * FROM T_LOAN_ORDER WHERE (STATE = 1 OR STATE = 4) AND PARTY_ID ="+"'"+partyId+"'");


		List<String> noticeList = new ArrayList<>();
		for (Map<String,Object> map1 : list) {
			LoanOrder order = JSON.parseObject(JSON.toJSONString(map1),LoanOrder.class);
//			Map<String, Double> calculateMap = calculatePledgeRate(order.getPledgeCurrency(), order.getDebt_amount(), order.getPledge_amount());
//			if (Double.valueOf(calculateMap.get("pledgeRate").toString()) >= LoanConstants.PLEDGE_RATE_NOTICE) {
//				noticeList.add(order.getUuid());
//			}
			if(order.getPledge_rate() >= LoanConstants.PLEDGE_RATE_NOTICE){
				noticeList.add(order.getUuid());
			}


		}	
		return noticeList;
	}
	
	/**
	 * 从缓存中获取订单
	 */
	public List<LoanOrder> cacheOrders() {
		return new ArrayList<LoanOrder>(cache.values());
	}
	
	/**
	 * 存入缓存
	 */
	public void addCacheOrder(LoanOrder order) {
		cache.put(order.getUuid(), order);
	}
	
	/**
	 * 从缓存中移除订单
	 */
	public void removeCacheOrder(String orderId) {
		cache.remove(orderId);
	}
	
	/**
	 * 从缓存中移除订单
	 */
	public void cacheRemoveOrders(List<String> orderIds) {
		for (String orderId : orderIds) {
			cache.remove(orderId);
		}
	}
	
	/**
	 * 订单详情
	 */
	public Map<String, Object> getLoanOrder(String id) {
		LoanOrder order = getLoanOrderParam(id);
		if (null == order) {
			throw new BusinessException(1, "order is unknown");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("id", order.getUuid());
		map.put("state", order.getState());
		map.put("debtAmount", order.getDebt_amount());
		map.put("loanCurrency", order.getLoanCurrency());
		map.put("pledgeCurrency", order.getPledgeCurrency());
		map.put("loanAmount", order.getLoanAmount());
		map.put("interestAmount", order.getInterest_amount());
		map.put("hourlyRate", order.getHourly_rate());
		map.put("orderNo", order.getOrderNo());
		map.put("createTime", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("expireTime", DateUtils.format(order.getExpire_time(), DateUtils.DF_yyyyMMddHHmmss));
		
		// 计息中、已逾期
		if (LoanConstants.PLEDGE_ORDER_STATE_CALCULATE == order.getState() 
				|| LoanConstants.PLEDGE_ORDER_STATE_OVERDUE == order.getState()) {
			
			List<LoanRelationOrder> relations = loanRelationOrderService.queryOrders(order.getUuid(), LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH);
			double pledgeAmount = order.getPledge_amount();
			for (LoanRelationOrder relationOrder : relations) {
				pledgeAmount = Arith.add(pledgeAmount, relationOrder.getPledge_amount());
			}
			
			Map<String, Double> calculateMap = calculatePledgeRate(order.getPledgeCurrency(), order.getDebt_amount(), pledgeAmount);
			map.put("pledgeRate", calculateMap.get("pledgeRate"));
			map.put("closeOut", calculateMap.get("closeOut"));
			map.put("overdueAmount", order.getOverdue_amount());
			map.put("overdueRate", order.getOverdue_rate());
		} 
		return map;
	}
	
	/**
	 * 订单
	 */
	public LoanOrder getLoanOrderParam(String id) {
//		List<LoanOrder> list = jdbcTemplate.query("SELECT * FROM T_LOAN_ORDER WHERE UUID=?", RecordObjectMapper.newInstance(LoanOrder.class), id);
		List<Map<String,Object>> list = jdbcTemplate.queryForList("SELECT * FROM T_LOAN_ORDER WHERE UUID='"+id+"';");
		if (null != list && list.size() > 0) {
			Map<String,Object> map1 = list.get(0);
			LoanOrder loanOrder = JSON.parseObject(JSON.toJSONString(map1),LoanOrder.class);
			return loanOrder;
        }
		return null;
	}
	
	/**
	 * 获取配置
	 */
	public Map<String, Object> getLoanConfig() {
		String config = sysparaService.find("exchange_loan").getSvalue();
		Map<String, Object> map = new HashMap<>();
		String[] configSplit = config.split("\\|");
		// 利息时率"|"周期"|"逾期时率"|"最小可借
		map.put("hourlyRate", configSplit[0]);				// 利息时率
		map.put("loanCycle", configSplit[1].split(":"));	// 周期
		map.put("loanAmountMin", configSplit[3]);			// 最小可借
		map.put("overdueRate", configSplit[2]);				// 逾期时率
		return map;
	}
	
	/**
	 * 分页查询 计息中、已逾期的订单
	 */
	public Page pagedQueryInterestOrder(int pageNo, int pageSize, Date date) {
		if (pageNo <= 0) pageNo = 1;
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);
//		List<LoanOrder> list=ApplicationUtil.executeSelect(LoanOrder.class,"WHERE STATE=1 OR STATE=4 AND CREATE_TIME<=? ORDER BY CREATE_TIME DESC LIMIT ?,?",new Object[] {date,page.getFirstElementNumber(),pageSize});
//		page.setElements(list);
//		List<LoanOrder> list=jdbcTemplate.queryForList("select * from t_loan_order  WHERE STATE=1 OR STATE=4 AND CREATE_TIME<="+pageSize+" ORDER BY CREATE_TIME DESC LIMIT "+page.getCurrent(),LoanOrder.class);
		List<LoanOrder> list=jdbcTemplate.query("select * from t_loan_order  WHERE (STATE=1 OR STATE=4) AND CREATE_TIME<=? ORDER BY CREATE_TIME DESC LIMIT ?, ?",BeanPropertyRowMapper.newInstance(LoanOrder.class),date,pageNo-1,pageSize);
		page.setRecords(list);
		return page;
	}
	
	/**
	 * 定时计算利息
	 */
	public void updateInterest(List<LoanOrder> orders) {
		List<LoanOrder> updateOrders = new ArrayList<>();
		Date date = new Date();
		for (LoanOrder order : orders) {
			// 还款记录
			List<LoanRelationOrder> relationList = loanRelationOrderService.queryOrders(order.getUuid(), LoanConstants.PLEDGE_ORDER_TYPE_REPAY);
			double repayAmount = 0;
			for (LoanRelationOrder relation : relationList ) {
				repayAmount += relation.getLoan_amount();
			}
			double loanAmountSum = Arith.sub(order.getLoanAmount(), repayAmount);
			// 借款利息
			double interestAmount = Arith.mul(loanAmountSum, order.getHourly_rate());
			// 总利息
			order.setInterestAmount(Arith.add(order.getInterest_amount(), interestAmount));
			// 总负债
			order.setDebtAmount(Arith.add(order.getDebt_amount(), interestAmount));
			// 到期
			if (LoanConstants.PLEDGE_ORDER_STATE_CALCULATE == order.getState() && date.after(order.getExpire_time())) {
				order.setState(LoanConstants.PLEDGE_ORDER_STATE_OVERDUE);
			}
			// 已逾期
			if (LoanConstants.PLEDGE_ORDER_STATE_OVERDUE == order.getState()) {
				// 逾期扣款
				double overdueAmount = Arith.mul(loanAmountSum, order.getOverdue_rate());
				// 总逾期
				order.setOverdueAmount(Arith.add(order.getOverdue_amount(), overdueAmount));
				// 总负债
				order.setDebtAmount(Arith.add(order.getDebt_amount(), overdueAmount));
			}
			updateOrders.add(order);
		}
		
		// 批量更新
		updateBatchOrder(updateOrders);
	}
	
	/**
	 * 批量新增收益记录
	 */
	protected void updateBatchOrder(final List<LoanOrder> orderList) {
		String sql = "UPDATE T_LOAN_ORDER SET INTEREST_AMOUNT = ?, DEBT_AMOUNT = ?, STATE = ?, OVERDUE_AMOUNT = ? WHERE UUID=?";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setDouble(1, orderList.get(i).getInterest_amount());
				ps.setDouble(2, orderList.get(i).getDebt_amount());
				ps.setInt(3, orderList.get(i).getState());
				ps.setDouble(4, orderList.get(i).getOverdue_amount());
				ps.setString(5, orderList.get(i).getUuid());
			}
			@Override
			public int getBatchSize() {
				return orderList.size();
			}
		});
		logger.info("end loan batch update attr:{}", batchUpdate);
	}
	
	
	/**
	 * 后台质押借币订单
	 */
	public Page pagedQueryAdmin(int pageNo, int pageSize, String userParam, String orderNo, String roleName, String state , List<String> children) {
		Page page = new Page(pageNo,pageSize);
		this.baseMapper.pagedQuery(page,userParam,orderNo,state,roleName , children);

		return page;
	}

//	public List queryForPage(String sql, int pageNo, int pageSize) {
//		int offset = (pageNo - 1) * pageSize;
//		sql = sql + " limit " + pageSize + " offset " + offset;
//		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
//	}
	
	/**
	 * 根据订单总负债 计算质押率
	 */
	public Map<String, Double> calculatePledgeRate(String pledgeCurrency, double debtAmount, double pledgeAmount) {
		List<Realtime> realtimes = dataService.realtime(pledgeCurrency);

		if(realtimes.isEmpty()){
			return null;
		}

//		Map<String, Object> config = getLoanConfig();
//		double hourlyRate = Double.valueOf(config.get("hourlyRate").toString());
//		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
//		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量11
//		double interestAmount = Arith.mul(loanAmount, hourlyRate * 24 * loanCycle);
//		double debtAmount = Arith.add(loanAmount, interestAmount);
//		System.out.println("realtimes.get(0).getClose().doubleValue() => " + realtimes.get(0).getClose().doubleValue());
//		System.out.println("pledgeAmount => " + pledgeAmount);
		Map<String, Double> map = new HashMap<>();
		map.put("pledgeRate", Arith.div(Arith.div(debtAmount, realtimes.get(0).getClose()), pledgeAmount, 2));
		map.put("closeOut", Arith.div(Arith.div(debtAmount, LoanConstants.PLEDGE_RATE_CLOSEOUT), pledgeAmount, 2));
		return map;
	}
	
	/**
	 * 系统强平
	 */
	public void updateCloseout(LoanOrder order, double pledgeRate) {
		WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getPledgeCurrency());
		double pledgeAmount = order.getPledge_amount();
		List<LoanRelationOrder> relationOrderList = loanRelationOrderService.queryOrders(order.getUuid(), LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH);
		for (LoanRelationOrder relation : relationOrderList) {
			// 补增质押
			pledgeAmount = Arith.add(relation.getPledge_amount(), pledgeAmount);
		}
		
		double amountBeforeExtend = walletExtend.getAmount();
		String partyId = walletExtend.getPartyId().toString();
		// 修改拓展钱包 冻结余额
		walletService.updateExtend(partyId, walletExtend.getWallettype(), 0, Arith.sub(0, pledgeAmount));

		// 强平资金日志
		MoneyLog logExtend = new MoneyLog();
		logExtend.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		logExtend.setAmountBefore(BigDecimal.valueOf(amountBeforeExtend));
		logExtend.setAmount(BigDecimal.ZERO.subtract(BigDecimal.valueOf(pledgeAmount)));
		logExtend.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBeforeExtend, Arith.sub(0, pledgeAmount))));
		logExtend.setLog("质押借币，订单号[" + order.getOrderNo() + "]，" + "强平：" + pledgeAmount);
		logExtend.setUserId(partyId);
		logExtend.setWalletType(order.getPledgeCurrency());
		logExtend.setContentType(Constants.MONEYLOG_CONTENT_LOAN_CLOSEOUT);
		logExtend.setCreateTime(new Date());
		moneyLogService.save(logExtend);
		
		order.setPledgeRate(pledgeRate);
		order.setState(LoanConstants.PLEDGE_ORDER_STATE_CLOSEOUT);
		order.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_CLOSEOUT);
		updateLoanOrder(order);
	}
	
//	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
//		this.pagedQueryDao = pagedQueryDao;
//	}

//	public void setWalletService(WalletService walletService) {
//		this.walletService = walletService;
//	}
//
//	public void setSysparaService(SysparaService sysparaService) {
//		this.sysparaService = sysparaService;
//	}
//
//	public void setMoneyLogService(MoneyLogService moneyLogService) {
//		this.moneyLogService = moneyLogService;
//	}

//	public void setDataService(DataService dataService) {
//		this.dataService = dataService;
//	}
//
//	public void setLoanRelationOrderService(LoanRelationOrderService loanRelationOrderService) {
//		this.loanRelationOrderService = loanRelationOrderService;
//	}
//
//	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//		this.jdbcTemplate = jdbcTemplate;
//	}

	//

	/**
	 * 补增质押
	 */
	public void saveReplenishOrder(String id, double pledgeAmount, String partyId) {
		LoanOrder order = this.getLoanOrderParam(id);
		if (null == order) {
			throw new BusinessException(1, "order is unknown");
		}

		if (LoanConstants.PLEDGE_ORDER_STATE_CALCULATE != order.getState()) {
			throw new BusinessException(1, "order state error");
		}

		WalletExtend walletExtend = walletService.saveExtendByPara(partyId, order.getPledgeCurrency());
		if (walletExtend.getAmount() < pledgeAmount) {
			throw new BusinessException(1, "Insufficient balance");
		}

		double amountBeforeExtend = walletExtend.getAmount();
		// 修改拓展钱包 余额 及冻结余额
		walletService.updateExtend(partyId, walletExtend.getWallettype(), Arith.sub(0, pledgeAmount), pledgeAmount);

		// 保存资金日志
		MoneyLog logExtend = new MoneyLog();
		logExtend.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		logExtend.setAmountBefore(BigDecimal.valueOf(amountBeforeExtend));
		logExtend.setAmount(BigDecimal.ZERO.subtract(BigDecimal.valueOf(pledgeAmount)));
		logExtend.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBeforeExtend, Arith.sub(0, pledgeAmount))));
		logExtend.setLog("补增质押，订单号[" + order.getOrderNo() + "]，" + "冻结：" + pledgeAmount);
		logExtend.setUserId(partyId);
		logExtend.setWalletType(order.getPledgeCurrency());
		logExtend.setContentType(Constants.MONEYLOG_CONTENT_LOAN_FROZEN);
		logExtend.setCreateTime(new Date());
		moneyLogService.save(logExtend);

		// 保存质押记录
		LoanRelationOrder replenish = new LoanRelationOrder();
		replenish.setUuid(UUIDGenerator.getUUID());
		replenish.setLoanRelationOrderId(id);
		replenish.setPartyId(partyId);
		replenish.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH);
		replenish.setPledgeType(LoanConstants.PLEDGE_TYPE);
		replenish.setPledgeCurrency(order.getPledgeCurrency());
		replenish.setPledge_amount(pledgeAmount);
		replenish.setCreate_time(new Date());
		loanRelationOrderService.insertLoanRelationOrder(replenish);
	}

	/**
	 * 续借
	 */
	public Object saveRefurbishOrder(String id, double loanAmountRq, String partyId) {
		ResultObject resultObject = new ResultObject();
		LoanOrder order = this.getLoanOrderParam(id);
		if (null == order) {
			throw new BusinessException(1, "order is unknown");
		}

		if (LoanConstants.PLEDGE_ORDER_STATE_CALCULATE != order.getState()) {
			throw new BusinessException(1, "order state error");
		}

		double pledgeAmount = order.getPledge_amount();
		String pledgeCurrency = order.getPledgeCurrency();
		int loanCycle = order.getLoan_cycle();

		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
		List<Realtime> realtimes = dataService.realtime(pledgeCurrency);
		double debtAmount = Arith.mul(realtimes.get(0).getClose(), pledgeAmount) * LoanConstants.PLEDGE_RATE_INITIAL;
		double rate = Arith.add(Arith.mul(order.getHourly_rate() * 24, loanCycle), 1);
		double loanAmountMax = Arith.div(debtAmount, rate, 2);
		double loanAmountSub = Arith.sub(loanAmountMax, order.getLoanAmount());
		if (loanAmountSub < loanAmountRq) {
			resultObject.setMsg(String.valueOf(new BigDecimal(loanAmountSub).setScale(2, RoundingMode.DOWN)));
			resultObject.setCode("20");
			return resultObject;
			// throw new BusinessException(1, "Maximum Borrow: " + new BigDecimal(loanAmountSub).setScale(2, RoundingMode.DOWN));
		}

		Wallet wallet = walletService.saveWalletByPartyId(partyId);
		double amountBefore = wallet.getMoney().doubleValue();
		// 修改主钱包余额
		walletService.update(partyId, loanAmountRq);

		// 保存资金日志
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
		moneylog.setAmount(BigDecimal.valueOf(loanAmountRq));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, loanAmountRq)));
		// 质押借币，订单号[orderNo]，借款：loanAmountRq
			moneylog.setLog("Pledge loan, order no.[" + order.getOrderNo() + "], loan amount: " + loanAmountRq);
		moneylog.setUserId(partyId);
		moneylog.setWalletType(Constants.WALLET);
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_LOAN_ADD);
		moneylog.setCreateTime(new Date());
		moneyLogService.save(moneylog);

		LoanRelationOrder refurbish = new LoanRelationOrder();
		refurbish.setUuid(UUIDGenerator.getUUID());
		refurbish.setLoanRelationOrderId(id);
		refurbish.setPartyId(partyId);
		refurbish.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_REFURBISH);
		refurbish.setLoanAmount(loanAmountRq);
		refurbish.setCreate_time(new Date());
		loanRelationOrderService.insertLoanRelationOrder(refurbish);

		// 修改质押借币单
		order.setDebtAmount(Arith.add(order.getDebt_amount(), loanAmountRq));
		order.setLoanAmount(Arith.add(order.getLoanAmount(), loanAmountRq));
		this.updateLoanOrder(order);

		// 更新缓存
		this.addCacheOrder(order);

		return null;
	}


	/**
	 * 还款
	 */
	public void saveRepayOrder(String id, double repayAmount, String partyId) {
		LoanOrder order = this.getLoanOrderParam(id);
		if (null == order) {
			throw new BusinessException(1, "order is unknown");
		}
		// 使用借款订单的用户ID，而非操作者（管理员）ID
		partyId = order.getPartyId();

		if (LoanConstants.PLEDGE_ORDER_STATE_SETTLE == order.getState()
				|| LoanConstants.PLEDGE_ORDER_STATE_CLOSEOUT == order.getState()) {
			throw new BusinessException(1, "order state error");
		}

		if (repayAmount > order.getDebt_amount()) {
			throw new BusinessException(1, "wrong repayment amount");
		}

		// 解冻
		if (repayAmount >= order.getDebt_amount()) {
			WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getPledgeCurrency());

			List<LoanRelationOrder> list = loanRelationOrderService.queryOrders(id, LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH);
			double pledgeAmount = order.getPledge_amount();
			for (LoanRelationOrder relationOrder : list) {
				pledgeAmount = Arith.add(pledgeAmount, relationOrder.getPledge_amount());
			}

		/*	double amountBeforeExtend = walletExtend.getAmount();
			// 修改拓展钱包 余额 及冻结余额
			walletService.updateExtend(partyId, walletExtend.getWallettype(), pledgeAmount, Arith.sub(0, pledgeAmount));

			// 保存资金日志
			MoneyLog logExtend = new MoneyLog();
			logExtend.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
			logExtend.setAmountBefore(BigDecimal.valueOf(amountBeforeExtend));
			logExtend.setAmount(BigDecimal.valueOf(pledgeAmount));
			logExtend.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBeforeExtend, pledgeAmount)));
			logExtend.setLog("质押借币，订单号[" + order.getOrderNo() + "]，" + "解冻：" + pledgeAmount);
			logExtend.setUserId(partyId);
			logExtend.setWalletType(order.getPledgeCurrency());
			logExtend.setContentType(Constants.MONEYLOG_CONTENT_LOAN_THAW);
			logExtend.setCreateTime(new Date());
			moneyLogService.save(logExtend);*/

			// 主动结清
			order.setState(LoanConstants.PLEDGE_ORDER_STATE_SETTLE);

		}

		/*Wallet wallet = walletService.saveWalletByPartyId(partyId);
		double amountBefore = wallet.getMoney().doubleValue();
		// 修改主钱包余额
		walletService.update(partyId, Arith.sub(0, repayAmount));

		// 保存资金日志
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		moneylog.setAmountBefore(BigDecimal.valueOf(amountBefore));
		moneylog.setAmount(BigDecimal.valueOf(repayAmount));
		moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amountBefore, repayAmount)));
		// 借币还款，订单号[orderNo]，还款：repayAmount
			moneylog.setLog("Loan repayment, order no.[" + order.getOrderNo() + "], repay amount: " + repayAmount);
		moneylog.setUserId(partyId);
		moneylog.setWalletType(Constants.WALLET);
		moneylog.setContentType(Constants.MONEYLOG_CONTENT_LOAN_REPAY);
		moneylog.setCreateTime(new Date());
		moneyLogService.save(moneylog);*/

		LoanRelationOrder repay = new LoanRelationOrder();
		repay.setUuid(UUIDGenerator.getUUID());
		repay.setLoanRelationOrderId(id);
		repay.setPartyId(partyId);
		repay.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_REPAY);
		repay.setLoanAmount(repayAmount);
		repay.setCreate_time(new Date());
		loanRelationOrderService.insertLoanRelationOrder(repay);

		// 修改质押借币单
		order.setDebtAmount(Arith.sub(order.getDebt_amount(), repayAmount));
		// order.setLoanAmount(Arith.sub(order.getLoanAmount(), repayAmount));
		this.updateLoanOrder(order);

		// 更新缓存
		if (LoanConstants.PLEDGE_ORDER_STATE_SETTLE == order.getState()) {
			// 主动结清
			this.removeCacheOrder(order.getUuid());
		} else {
			this.addCacheOrder(order);
		}
	}
	
}
