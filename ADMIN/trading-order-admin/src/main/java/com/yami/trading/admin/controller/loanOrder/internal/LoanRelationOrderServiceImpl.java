package com.yami.trading.admin.controller.loanOrder.internal;

import java.util.*;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.loanOrder.LoanConstants;
import com.yami.trading.admin.controller.loanOrder.LoanRelationOrderService;
import com.yami.trading.bean.loanOrder.LoanRelationOrder;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.UUIDGenerator;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanRelationOrderServiceImpl implements LoanRelationOrderService {

	@Autowired
	private DataService dataService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WalletService walletService;

	@Autowired
	private MoneyLogService moneyLogService;

//	@Autowired
//	private LoanOrderService loanOrderService;
	
	/**
	 * 新增质押借币关联订单
	 */
	public void saveLoanRelationOrder(LoanRelationOrder relationOrder) {
		relationOrder.setUuid(UUIDGenerator.getUUID());
		relationOrder.setPledgeType(LoanConstants.PLEDGE_TYPE);
		relationOrder.setOrder_type(LoanConstants.PLEDGE_ORDER_TYPE_LOAN);
		relationOrder.setLoanCurrency("usd");
		relationOrder.setCreate_time(new Date());
		this.insertLoanRelationOrder(relationOrder);
	}
	
	/**
	 * 质押记录
	 */
	public List<Map<String, Object>> pagedQueryPledge(int pageNo, int pageSize, String loanRelationOrderId) {
		if (pageNo <= 0) pageNo = 1;
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);

//		List<LoanRelationOrder> orders = ApplicationUtil.executeSelect(LoanRelationOrder.class,"WHERE LOAN_RELATION_ORDER_ID=? ORDER BY CREATE_TIME DESC LIMIT ?,?",new Object[] {loanRelationOrderId,page.getFirstElementNumber(),pageSize});

		String str = "select * from t_LOAN_RELATION_ORDER WHERE LOAN_RELATION_ORDER_ID='"+loanRelationOrderId+"' ORDER BY CREATE_TIME DESC LIMIT "+((pageNo-1)*pageSize)+","+pageSize;
		System.out.println("质押记录 :str sql => " + str);
		List<Map<String,Object>> maps = jdbcTemplate.queryForList(str);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map1 : maps) {
			LoanRelationOrder order = JSON.parseObject(JSON.toJSONString(map1), LoanRelationOrder.class);
			Map<String, Object> map = new HashMap<>();
			// 借款
			if (LoanConstants.PLEDGE_ORDER_TYPE_LOAN == order.getOrder_type()) {
				map.put("orderType", order.getOrder_type());
				map.put("loanAmount", order.getLoan_amount());
				map.put("pledgeType", order.getPledge_type());
				map.put("pledgeAmount", order.getPledge_amount());
				map.put("pledgeCurrency", order.getPledgeCurrency());
				map.put("createTime", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			}
			// 续借、强平、还款
			if (LoanConstants.PLEDGE_ORDER_TYPE_REFURBISH == order.getOrder_type() 
					|| LoanConstants.PLEDGE_ORDER_TYPE_CLOSEOUT == order.getOrder_type()
					|| LoanConstants.PLEDGE_ORDER_TYPE_REPAY == order.getOrder_type()) {
				map.put("orderType", order.getOrder_type());
				map.put("loanAmount", order.getLoan_amount());
				map.put("createTime", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			}
			// 补增质押
			if (LoanConstants.PLEDGE_ORDER_TYPE_REPLENISH == order.getOrder_type()) {
				map.put("orderType", order.getOrder_type());
				map.put("pledgeType", order.getPledge_type());
				map.put("pledgeAmount", order.getPledge_amount());
				map.put("pledgeCurrency", order.getPledgeCurrency());
				map.put("createTime", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			}
			if(ObjectUtils.isNotEmpty(map)) {
				list.add(map);
			}		
		}
		return list;
	}
	

	


	

	
	/**
	 * 后台质押记录
	 */
	public List<Map<String,Object>> queryLoanRelation(String loanRelationOrderId) {
//		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
//		List<LoanRelationOrder> list = jdbcTemplate.query("SELECT * FROM T_LOAN_RELATION_ORDER WHERE LOAN_RELATION_ORDER_ID=? ORDER BY CREATE_TIME DESC",
//				RecordObjectMapper.newInstance(LoanRelationOrder.class), loanRelationOrderId);

		List<Map<String,Object>> result = jdbcTemplate.queryForList("SELECT * FROM T_LOAN_RELATION_ORDER WHERE LOAN_RELATION_ORDER_ID='"+loanRelationOrderId+"' ORDER BY CREATE_TIME DESC");

//		for (LoanRelationOrder order : list) {
//			Map<String,Object> data = new HashMap<String, Object>();
//			data.put("orderType", order.getOrder_type());
//			data.put("loanAmount", order.getLoanAmount());
//			data.put("pledgeType", order.getPledgeType());
//			data.put("pledgeAmount", order.getPledgeAmount());
//			data.put("pledgeCurrency", order.getPledgeCurrency());
//			data.put("createTime", DateUtils.format(order.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
//			result.add(data);
//		}
		return result;
	}
	
	/**
	 * 根据订单关联ID获取订单列表
	 */
	public List<LoanRelationOrder> queryOrders(String loanRelationOrderId, int orderType) {
//		List<LoanRelationOrder> list = jdbcTemplate.query("SELECT * FROM T_LOAN_RELATION_ORDER WHERE LOAN_RELATION_ORDER_ID=? AND ORDER_TYPE=?",
//				RecordObjectMapper.newInstance(LoanRelationOrder.class), loanRelationOrderId, orderType);
		List<LoanRelationOrder> list = new Vector<>();
		List<Map<String,Object>> list2 = jdbcTemplate.queryForList("SELECT * FROM T_LOAN_RELATION_ORDER WHERE LOAN_RELATION_ORDER_ID='"+loanRelationOrderId+"' AND ORDER_TYPE="+orderType);
		for(Map<String,Object> map1 : list2){
			LoanRelationOrder order = JSON.parseObject(JSON.toJSONString(map1), LoanRelationOrder.class);
			list.add(order);
		}

		//
		return list;
	}
	
	public void insertLoanRelationOrder(LoanRelationOrder order) {
		String insertSql = "INSERT INTO T_LOAN_RELATION_ORDER(UUID,LOAN_RELATION_ORDER_ID,PARTY_ID,ORDER_TYPE,LOAN_AMOUNT,LOAN_CURRENCY,"
				+ "PLEDGE_AMOUNT,PLEDGE_CURRENCY,PLEDGE_TYPE,CREATE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(insertSql,order.getUuid(),order.getLoanRelationOrderId(),order.getParty_id(),order.getOrder_type(),
				order.getLoan_amount(),order.getLoan_currency(),order.getPledge_amount(),order.getPledgeCurrency(),
				order.getPledge_type(),order.getCreate_time());
	}

//	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//		this.jdbcTemplate = jdbcTemplate;
//	}
//
//	public void setWalletService(WalletService walletService) {
//		this.walletService = walletService;
//	}
//
//	public void setMoneyLogService(MoneyLogService moneyLogService) {
//		this.moneyLogService = moneyLogService;
//	}
//
//	public void setDataService(DataService dataService) {
//		this.dataService = dataService;
//	}
//
//	public void setLoanOrderService(LoanOrderService loanOrderService) {
//		this.loanOrderService = loanOrderService;
//	}
//
}
