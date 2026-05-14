package com.yami.trading.admin.controller.loan.internal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.loan.LoanService;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.loan.LoanParam;
import com.yami.trading.bean.loan.SimpleLoanOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.user.UserService;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author JORGE
 * @description 借贷服务接口实现类
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	UserService userService;

	@Autowired
	WalletService walletService;

	@Autowired
	MoneyLogService moneyLogService;

	@Getter
	HashMap<String,Object> paramMap = new HashMap<>();

	/**
	 * 借贷状态字典
	 */
	private static HashMap<Integer,String> stateMap;
	
	/**
	 * 付款方式字典
	 */
	private static HashMap<Integer,String> repayments;
	
	/**
	 * 逗号正则表达式
	 */
	private static Pattern commaPattern=Pattern.compile(",");
	
	/**
	 * 冒号正则表达式
	 */
	private static Pattern colonPattern=Pattern.compile(":");
	
	/**
	 * 放款机构字典
	 */
	private static HashMap<Integer,String> lendingInstitutions;
	
	public LoanServiceImpl() {
		stateMap=new HashMap<Integer,String>();
		repayments=new HashMap<Integer,String>();
		lendingInstitutions=new HashMap<Integer,String>();
		
		stateMap.put(1, "未审");
		stateMap.put(2, "待还款");
		stateMap.put(3, "驳回");
		stateMap.put(4, "已逾期");
		stateMap.put(5, "已还款");
		stateMap.put(6, "还款中");
		
		repayments.put(1,"到期还本息");
		repayments.put(2,"到期还本金");
		repayments.put(3,"到期还利息");
		
		lendingInstitutions.put(1,"LOAN1");
		lendingInstitutions.put(2,"LOAN2");
		lendingInstitutions.put(3,"LOAN3");
	}

	@Override
	public Boolean addLoanOrder(SimpleLoanOrder simpleLoanOrder) {
//		Object primaryKey= DBUtil.executeInsert(simpleLoanOrder);
		String sql="insert into t_simple_loan_order(uuid,party_id,symbol,quota,state,term,create_time,repay_cycle,daily_rate,repayment,lending_institution,lending_name,reason,house_imgs,income_img)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


//		  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单主键',
//  `party_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借贷用户ID',
//  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借贷币种',
//  `quota` int(8) NOT NULL COMMENT '借贷额度',
//  `state` int(1) NULL DEFAULT 1 COMMENT '审核状态',
//  `term` int(8) NOT NULL COMMENT '借贷期限(天)',
//  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '申请时间',
//  `repay_cycle` int(8) NOT NULL COMMENT '还款周期(天)',
//  `daily_rate` double(5, 4) NOT NULL COMMENT '日利率(浮点数)',
//  `repayment` int(2) NOT NULL COMMENT '还款方式(文本)',
//  `lending_institution` int(2) NOT NULL COMMENT '放款机构(文本)',
//  `lending_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '放款机构名称',
//  `reason` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '驳回原因',
//  `house_imgs` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房屋信息图片',
//  `income_img` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收入证明图片',
		System.out.println("xxx= " + simpleLoanOrder.getHouseImgs());
		//变式为
		Object[] args= {
				simpleLoanOrder.getUuid(),
				simpleLoanOrder.getPartyId(),
				simpleLoanOrder.getSymbol(),
				simpleLoanOrder.getQuota(),
				simpleLoanOrder.getState(),
				simpleLoanOrder.getTerm(),
				simpleLoanOrder.getCreateTime(),
				simpleLoanOrder.getRepayCycle(),
				simpleLoanOrder.getDailyRate(),
				simpleLoanOrder.getRepayment(),
				simpleLoanOrder.getLendingInstitution(),
				simpleLoanOrder.getLendingName(),
				simpleLoanOrder.getReason(),
				simpleLoanOrder.getHouseImgs(),
				simpleLoanOrder.getIncomeImg()
		};
		int update = jdbcTemplate.update(sql, args);

		//输出影响的值
		System.out.println(update);

		return true;
	}
	
	@Override
	public List<LoanParam> getLoanParamList(String uuid){
		List<LoanParam> loanParamList = new ArrayList<>();
		if(StringUtils.isBlank(uuid)){
//			loanParamList = DBUtil.executeSelect(LoanParam.class);
			String str = "select uuid,term,max_quota,min_quota,repay_cycle,daily_rate,repayment,lending_institution,lending_name from t_loan_param where 1=1;";
			List<Map<String,Object>> mapList = jdbcTemplate.queryForList(str);
			for(int i = 0 ; i < mapList.size() ; i++){
				Map<String,Object> map1 = mapList.get(i);
				LoanParam loan1 = JSON.parseObject(JSON.toJSONString(map1),LoanParam.class);
				loanParamList.add(loan1);
			}
			// System.out.println("mapList=>"+mapList);
//			loanParamList = jdbcTemplate.queryForList(str,LoanParam.class);
		}else {
//			loanParamList = DBUtil.executeSelect(LoanParam.class,"WHERE UUID=?",new Object[] {uuid});
			String str = "select uuid,term,max_quota,min_quota,repay_cycle,daily_rate,repayment,lending_institution,lending_name from t_loan_param where 1=1 and uuid ="+uuid+";";
//			loanParamList = jdbcTemplate.queryForList(str,LoanParam.class);
			List<Map<String,Object>> mapList = jdbcTemplate.queryForList(str);
			for(int i = 0 ; i < mapList.size() ; i++){
				Map<String,Object> map1 = mapList.get(i);
				LoanParam loan1 = JSON.parseObject(JSON.toJSONString(map1),LoanParam.class);
				loanParamList.add(loan1);
			}
		}

		return loanParamList;
	}

	@Override
	public HashMap<String,Object> getLoanParams(String uuid) {
		LoanParam loanParam=null;
		List<LoanParam> loanParamList = new ArrayList<>();
		if(null==uuid || (uuid=uuid.trim()).isEmpty()) {
//			List<LoanParam> loanParamList=DBUtil.executeSelect(LoanParam.class);
//			loanParam=(null==loanParamList || loanParamList.isEmpty())?null:loanParamList.get(0);

			String str = "select uuid,term,max_quota,min_quota,repay_cycle,daily_rate,repayment,lending_institution from t_loan_param where 1=1;";
			// List<Map<String,Object>> mapList = jdbcTemplate.queryForList(str);
			// System.out.println("mapList=>"+mapList);
			// loanParamList = jdbcTemplate.queryForList(str,LoanParam.class);

			List<Map<String,Object>> mapList = jdbcTemplate.queryForList(str);
			for(int i = 0 ; i < mapList.size() ; i++){
				Map<String,Object> map1 = mapList.get(i);
				LoanParam loan1 = JSON.parseObject(JSON.toJSONString(map1),LoanParam.class);
				loanParamList.add(loan1);
			}

		}else {
//			loanParam=DBUtil.executeGet(uuid, LoanParam.class);

			String str = "select uuid,term,max_quota,min_quota,repay_cycle,daily_rate,repayment,lending_institution from t_loan_param where 1=1 and uuid ="+uuid+";";
			// loanParamList = jdbcTemplate.queryForList(str,LoanParam.class);
			List<Map<String,Object>> mapList = jdbcTemplate.queryForList(str);
			for(int i = 0 ; i < mapList.size() ; i++){
				Map<String,Object> map1 = mapList.get(i);
				LoanParam loan1 = JSON.parseObject(JSON.toJSONString(map1),LoanParam.class);
				loanParamList.add(loan1);
			}
		}

		loanParam=(null==loanParamList || loanParamList.isEmpty())?null:loanParamList.get(0);
		
		if(null==loanParam) return null;
		
		HashMap<String,Object> paramMap=new HashMap<String,Object>(BeanMap.create(loanParam));
		String term=(String)paramMap.get("term");
		if(null==term || (term=term.trim()).isEmpty()) {
			paramMap.put("termList",new Object[0]);
		}else {
			paramMap.put("termList",commaPattern.splitAsStream(term).map(tr->new String[] {tr,tr}).collect(Collectors.toList()));
		}
		
		String repayCycle=(String)paramMap.get("repayCycle");
		if(null==repayCycle || (repayCycle=repayCycle.trim()).isEmpty()) {
			paramMap.put("repayCycleList",new Object[0]);
		}else {
			paramMap.put("repayCycleList",commaPattern.splitAsStream(repayCycle).map(rc->new String[] {rc,rc}).collect(Collectors.toList()));
		}
		
		String dailyRate=(String)paramMap.get("dailyRate");
		if(null==dailyRate || (dailyRate=dailyRate.trim()).isEmpty()) {
			paramMap.put("dailyRateList",new Object[0]);
		}else {
			paramMap.put("dailyRateList",commaPattern.splitAsStream(dailyRate).map(dr->new String[] {dr,dr}).collect(Collectors.toList()));
		}
		
		String repayment=(String)paramMap.get("repayment");
		if(null==repayment || (repayment=repayment.trim()).isEmpty()) {
			paramMap.put("repaymentList",new Object[0]);
		}else {
			paramMap.put("repaymentList",commaPattern.splitAsStream(repayment).map(rep->colonPattern.split(rep)).collect(Collectors.toList()));
		}
		
		String lendingInstitution=(String)paramMap.get("lendingInstitution");
		if(null==lendingInstitution || (lendingInstitution=lendingInstitution.trim()).isEmpty()) {
			paramMap.put("lendingList",new Object[0]);
		}else {
			paramMap.put("lendingList",commaPattern.splitAsStream(lendingInstitution).map(led->colonPattern.split(led)).collect(Collectors.toList()));
		}
		
		paramMap.put("stateList",commaPattern.splitAsStream("1:未审,2:通过,3:驳回").map(stat->colonPattern.split(stat)).collect(Collectors.toList()));
		
		return paramMap;
	}

	@Override
	public Page pagedQuery(Map<String, Object> queryParams) {
		int pageSize=(Integer)queryParams.get("pageSize");
		int pageNo=(Integer)queryParams.get("pageNo");
		if (pageNo <= 0) pageNo = 1;

		StringBuilder whereStatement1=new StringBuilder("select count(*) from t_simple_loan_order WHERE 1=1 ");

		String userName=(String)queryParams.get("userName");
		String orderNo=(String)queryParams.get("orderNo");

		Object children= queryParams.get("children");

		int status=(Integer)queryParams.get("status");
		
		StringBuilder queryStatement=new StringBuilder("select * from t_simple_loan_order WHERE 1=1 ");

		StringBuilder where =new StringBuilder();

		
		if(0!=status) {
			where.append("AND STATE=");
			where.append(status);
		}

		if(Objects.nonNull(children)) {
			List<String> childrens = (List<String>) children;
			where.append(" AND party_id in");

			StringBuffer partyIdsBuf = new StringBuffer();
			for (String onePartyId : childrens) {
				partyIdsBuf.append("'").append(onePartyId.trim()).append("',");
			}
			partyIdsBuf.deleteCharAt(partyIdsBuf.length() - 1);
			where.append("(" + partyIdsBuf.toString() + ") ");
		}

		if(null!=orderNo && !(orderNo=orderNo.trim()).isEmpty()) {
			where.append(" AND UUID=");
			where.append("'"+orderNo+"'");
		}
		
		if(null!=userName && !(userName=userName.trim()).isEmpty()) {
			where.append(" AND PARTY_ID=(SELECT USER_ID FROM tz_user WHERE user_name= ");
			where.append("'"+userName+"'");
			where.append(")");
		}

		queryStatement.append(where);

		queryStatement.append(" ORDER BY CREATE_TIME DESC");


		String countQuery = whereStatement1.append(where).toString();

		int count = jdbcTemplate.queryForObject(countQuery ,Integer.class);

		Page page = new Page(pageNo, pageSize, count);

		if(StringUtils.isEmpty(orderNo) && StringUtils.isEmpty(userName)){
			queryStatement.append(" LIMIT ");
			queryStatement.append((page.getCurrent()-1)*pageSize);
			queryStatement.append(",");
			queryStatement.append(pageSize);
		}

		queryStatement.append(";");

		RowMapper<SimpleLoanOrder> rowMapper=new BeanPropertyRowMapper<>(SimpleLoanOrder.class);
		List<SimpleLoanOrder> orderList=jdbcTemplate.query(queryStatement.toString(),rowMapper);

//		List<SimpleLoanOrder> orderList=DBUtil.executeSelect(SimpleLoanOrder.class,whereStatement.toString(),paramList.toArray(new Object[paramList.size()]));
		if(null==orderList || orderList.isEmpty()) return page;
		String partyIds=orderList.stream().map(order->order.getPartyId()).collect(Collectors.joining(","));
//		List<User> partyList=DBUtil.executeSelect(User.class,"WHERE FIND_IN_SET(UUID,?)",new Object[] {partyIds});

//		System.out.println(Arrays.asList(partyIds.split(",")));

		List<String> array = Arrays.asList(partyIds.split(","));
//		System.out.println(array);
//		System.out.println(array.size());
//		array.set(0,"0042a30afe9d5eab655ff7ba39d30dca");



		List<User> partyList = userService.listByIds(array);

		Map<?,?> idToNameMap=partyList.stream().collect(Collectors.toMap(party->party.getUserId(),party->party.getUserName(),(oldname,newname)->newname));
		Map<?,?> rolenameMap=partyList.stream().collect(Collectors.toMap(party->party.getUserId(),party->party.getRoleName(),(oldname,newname)->newname));

		Date endDay = new Date();
		Long endTimel = endDay.getTime();		
		List<HashMap<String,Object>> transferList=orderList.stream().map(order->{
			Integer state=order.getState();
			Integer rep=order.getRepayment();
			String houseImgs=(String)order.getHouseImgs();
			Integer led=order.getLendingInstitution();
			houseImgs=null==houseImgs?null:(houseImgs=houseImgs.trim()).isEmpty()?null:houseImgs;
			HashMap<String,Object> transferMap=new HashMap<String,Object>(BeanMap.create(order));
			transferMap.put("state", new Object[] {state,stateMap.get(state)});
			transferMap.put("userName", idToNameMap.get(order.getPartyId()));
			transferMap.put("rolename", rolenameMap.get(order.getPartyId()));
			if(ObjectUtils.isNotEmpty(transferMap.get("rolename"))) {
				String roleName = transferMap.get("rolename").toString();
				transferMap.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
			}

			transferMap.put("repayment", new Object[] {rep,repayments.get(rep)});
			transferMap.put("houseImgs", null==houseImgs?new String[0]:commaPattern.split(houseImgs));
			transferMap.put("lendingInstitution", new Object[] {led,order.getLendingName()});
			
			
			Long starTimel = order.getCreateTime().getTime();
			Long num = endTimel - starTimel;
			Long remainQuota = num/24/60/60/1000;
			order.setTotalInterest(BigDecimal.ZERO);
			order.setRemainQuota(0);
			if(order.getState().intValue()==2||order.getState().intValue()==4) {
				BigDecimal totalInterest = order.getDailyRate().multiply(new BigDecimal(remainQuota)).multiply(order.getQuota());
				order.setTotalInterest(totalInterest);
				order.setRemainQuota(order.getTerm() - remainQuota.intValue());
				transferMap.put("totalInterest", totalInterest);
			}		
			
			return transferMap;
		}).collect(Collectors.toList());
		page.setRecords(transferList);
		
		return page;
	}

	@Override
	public boolean deleteLoanOrder(String orderId) {
		if(null==orderId || (orderId=orderId.trim()).isEmpty()) {
			throw new BusinessException("申请单ID不能为空!");
		}
		
//		int count=DBUtil.executeDel(orderId,SimpleLoanOrder.class);
		String sql = "delete * from t_simple_loan_order where 1=1 and uuid=?;";
		int count=jdbcTemplate.update(sql,orderId);
		if(1!=count) {
			throw new BusinessException("根据申请单ID删除了多条记录,说明申请单ID重复!");
		}
		
		return count==1?true:false;
	}

	@Override
	public boolean updateLoanOrderState(String orderId, String status,String reason) {
		if(null==orderId || (orderId=orderId.trim()).isEmpty()) {
			throw new BusinessException("申请单ID不能为空!");
		}

		if(null==status || (status=status.trim()).isEmpty()) {
			throw new BusinessException("审核状态不能为空!");
		}

		int state=Integer.parseInt(status);
		int count=0;
		if(StringUtils.isNotBlank(reason)) {
			count = jdbcTemplate.update("UPDATE T_SIMPLE_LOAN_ORDER SET state=?,REASON=? WHERE UUID=?",state,reason,orderId);
		}else {
			count = jdbcTemplate.update("UPDATE T_SIMPLE_LOAN_ORDER SET state=? WHERE UUID=?",state,orderId);
		}
		if(1!=count) {
			throw new BusinessException("根据申请单ID修改了多条记录,说明申请单ID重复!");
		}

		// 审核通过（state=2），给用户增加资产
		if (state == 2) {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
				"SELECT party_id, quota, symbol FROM T_SIMPLE_LOAN_ORDER WHERE UUID=?", orderId);
			if (!rows.isEmpty()) {
				Map<String, Object> row = rows.get(0);
				String partyId = (String) row.get("party_id");
				BigDecimal quota = new BigDecimal(row.get("quota").toString());
				Wallet wallet = walletService.saveWalletByPartyId(partyId);
				double amountBefore = wallet.getMoney().doubleValue();
				walletService.update(partyId, quota.doubleValue());

				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
				moneyLog.setAmountBefore(BigDecimal.valueOf(amountBefore));
				moneyLog.setAmount(quota);
				moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(amountBefore, quota.doubleValue())));
				moneyLog.setLog("借贷审核通过，订单号[" + orderId + "]，放款：" + quota);
				moneyLog.setUserId(partyId);
				moneyLog.setWalletType(Constants.WALLET);
				moneyLog.setContentType(Constants.MONEYLOG_CONTENT_LOAN_ADD);
				moneyLog.setCreateTime(new Date());
				moneyLogService.save(moneyLog);
			}
		}

		return true;
	}

	@Override
	public Boolean modLoanOrder(SimpleLoanOrder simpleLoanOrder) {
		Serializable id=simpleLoanOrder.getUuid();
		if(null==id) {
			throw new BusinessException("申请单ID不能为空!");
		}
		
//		Set<String> excludeFields=new HashSet<String>();
//		excludeFields.add("id");
//		excludeFields.add("partyId");
//		excludeFields.add("state");
//		excludeFields.add("incomeImg");
//		excludeFields.add("incomeImg");
//		excludeFields.add("lendingInstitution");
//
//		int count=DBUtil.executeUpdate(simpleLoanOrder,excludeFields);

		String sql = "update t_simple_loan_order set quota = " + simpleLoanOrder.getQuota() + " where 1=1 and uuid='"+id+"';";
		System.out.println("sql => " + sql);
		int count=jdbcTemplate.update(sql);

		if(1!=count) {
			throw new BusinessException("根据申请单ID修改了多条记录,说明申请单ID重复!");
		}
		
		return true;
	}
	
	@Override
	public SimpleLoanOrder getLoanOrder(String partyId,String orderId) {
		if(null==orderId || (orderId=orderId.trim()).isEmpty()) {
			throw new BusinessException("申请单ID不能为空!");
		}
		
		ArrayList<Object> paramList=new ArrayList<Object>();
		StringBuilder whereStatement=new StringBuilder("select * from t_simple_loan_order WHERE UUID=");
		whereStatement.append("'"+orderId+"'");
		
		if(null!=partyId) {
			whereStatement.append(" AND PARTY_ID= ");
			whereStatement.append("'"+partyId+"'");
			whereStatement.append(";");
		}

		RowMapper<SimpleLoanOrder> rowMapper=new BeanPropertyRowMapper<>(SimpleLoanOrder.class);
		List<SimpleLoanOrder> loanOrders = jdbcTemplate.query(whereStatement.toString(),rowMapper);
//		List<SimpleLoanOrder> loanOrders=DBUtil.executeSelect(SimpleLoanOrder.class,whereStatement.toString(),paramList.toArray(new Object[paramList.size()]));
		if(null==loanOrders || 0==loanOrders.size()) return null;
		
		SimpleLoanOrder loanOrder=loanOrders.get(0);
		String houseImgs=(String)loanOrder.getHouseImgs();
		loanOrder.setHouseImgs(null==houseImgs?new String[0]:commaPattern.split(houseImgs));
		Date endDay = new Date();
		Long endTimel = endDay.getTime();
		Long starTimel = loanOrder.getCreateTime().getTime();
		Long num = endTimel - starTimel;
		Long remainQuota = num/24/60/60/1000;
		loanOrder.setTotalInterest(BigDecimal.ZERO);
		loanOrder.setRemainQuota(0);
		if(loanOrder.getState().intValue()==2||loanOrder.getState().intValue()==4) {
			BigDecimal totalInterest = loanOrder.getDailyRate().multiply(new BigDecimal(remainQuota)).multiply(loanOrder.getQuota());
			loanOrder.setTotalInterest(totalInterest);	
			loanOrder.setRemainQuota(loanOrder.getTerm() - remainQuota.intValue());
		}		
		return loanOrder;
	}
	
	@Override
	public List<SimpleLoanOrder> getUserOrders(Map<String,Object> paramMap) {
		String state=(String)paramMap.get("state");
		String symbol=(String)paramMap.get("symbol");
		String partyId=(String)paramMap.get("partyId");
		String repayment=(String)paramMap.get("repayment");
		String endTimeStr=(String)paramMap.get("endTime");
		String startTimeStr=(String)paramMap.get("startTime");
		String lendingInstitution=(String)paramMap.get("lendingInstitution");
		
		Integer status=(null==state || (state=state.trim()).isEmpty())?null:new Integer(state);
		Timestamp startTime=(null==startTimeStr || (startTimeStr=startTimeStr.trim()).isEmpty())?null:Timestamp.valueOf(startTimeStr);
		
		Timestamp endTime=null;
		if(null!=startTime) {
			endTime=(null==endTimeStr || (endTimeStr=endTimeStr.trim()).isEmpty())?null:Timestamp.valueOf(endTimeStr);
		}
		
		ArrayList<Object> paramList=new ArrayList<Object>();
		StringBuilder whereStatement=new StringBuilder("select * from t_simple_loan_order WHERE PARTY_ID=");
		whereStatement.append("'"+partyId+"'");
		
		if(null!=status) {
			whereStatement.append("AND STATE=");
			whereStatement.append(status);
		}
		
		if(null!=symbol && !(symbol=symbol.trim()).isEmpty()) {
			whereStatement.append("AND SYMBOL=");
			whereStatement.append(symbol);
		}
		
		if(null!=repayment && !(repayment=repayment.trim()).isEmpty()) {
			whereStatement.append("AND REPAYMENT=");
			whereStatement.append(repayment);
		}
		
		if(null!=lendingInstitution && !(lendingInstitution=lendingInstitution.trim()).isEmpty()) {
			whereStatement.append("AND LENDING_INSTITUTION=");
			whereStatement.append(lendingInstitution);
		}
		
		if(null!=startTime) {
			if(null==endTime) {
				whereStatement.append("AND CREATE_TIME>= ");
				whereStatement.append(startTime);
			}else {
				whereStatement.append("AND CREATE_TIME>=  ");
				whereStatement.append(startTime);
				whereStatement.append("AND CREATE_TIME<=");
				whereStatement.append(endTime);
				whereStatement.append(";");
			}
		}
		whereStatement.append(" ORDER BY CREATE_TIME DESC ");
//		List<SimpleLoanOrder> simpleLoanOrderList = DBUtil.executeSelect(SimpleLoanOrder.class,whereStatement.toString(),paramList.toArray(new Object[paramList.size()]));
		System.out.println("whereStatement.toString() = " + whereStatement.toString());
//		List<SimpleLoanOrder> simpleLoanOrderList = jdbcTemplate.queryForList(whereStatement.toString(),SimpleLoanOrder.class);
		RowMapper<SimpleLoanOrder> rowMapper=new BeanPropertyRowMapper<>(SimpleLoanOrder.class);
		List<SimpleLoanOrder> simpleLoanOrderList = jdbcTemplate.query(whereStatement.toString(), rowMapper);
		//DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		
		Date endDay = new Date();
		Long endTimel = endDay.getTime();
		for(SimpleLoanOrder simpleLoanOrder : simpleLoanOrderList) {
			Long starTimel = simpleLoanOrder.getCreateTime().getTime();
			Long num = endTimel - starTimel;
			Long remainQuota = num/24/60/60/1000;
			simpleLoanOrder.setTotalInterest(BigDecimal.ZERO);
			simpleLoanOrder.setRemainQuota(0);
			if(simpleLoanOrder.getState().intValue()==2||simpleLoanOrder.getState().intValue()==4) {
				BigDecimal totalInterest = simpleLoanOrder.getDailyRate().multiply(new BigDecimal(remainQuota)).multiply(simpleLoanOrder.getQuota());
				simpleLoanOrder.setTotalInterest(totalInterest);
				simpleLoanOrder.setRemainQuota(simpleLoanOrder.getTerm() - remainQuota.intValue());
			}						
		}
		return simpleLoanOrderList;
	}

	public void setParamMap(HashMap<String,Object> params){
		this.paramMap = params;
	}

	@Override
	public void partialRepay(String orderId, BigDecimal amount) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			"SELECT party_id, quota FROM T_SIMPLE_LOAN_ORDER WHERE UUID=? AND STATE=2", orderId);
		if (rows.isEmpty()) {
			throw new BusinessException("订单不存在或状态不正确");
		}
		Map<String, Object> row = rows.get(0);
		String partyId = (String) row.get("party_id");
		BigDecimal quota = new BigDecimal(row.get("quota").toString());
		if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(quota) > 0) {
			throw new BusinessException("还款金额不合法");
		}

		Wallet wallet = walletService.saveWalletByPartyId(partyId);
		if (wallet.getMoney().compareTo(amount) < 0) {
			throw new BusinessException("用户余额不足");
		}
		double amountBefore = wallet.getMoney().doubleValue();
		walletService.update(partyId, -amount.doubleValue());

		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_LOAN);
		moneyLog.setAmountBefore(BigDecimal.valueOf(amountBefore));
		moneyLog.setAmount(amount.negate());
		moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amountBefore, amount.doubleValue())));
		moneyLog.setLog("借贷部分还款，订单号[" + orderId + "]，还款：" + amount);
		moneyLog.setUserId(partyId);
		moneyLog.setWalletType(Constants.WALLET);
		moneyLog.setContentType(Constants.MONEYLOG_CONTENT_LOAN_REPAY);
		moneyLog.setCreateTime(new Date());
		moneyLogService.save(moneyLog);

		// 更新已还金额，若已还完则状态改为已还款(5)，否则还款中(6)
		jdbcTemplate.update(
			"UPDATE T_SIMPLE_LOAN_ORDER SET REPAID_AMOUNT=IFNULL(REPAID_AMOUNT,0)+?, STATE=CASE WHEN IFNULL(REPAID_AMOUNT,0)+? >= QUOTA THEN 5 ELSE 6 END WHERE UUID=?",
			amount, amount, orderId);

		// 同步用户表：已贷减少，可贷增加
		jdbcTemplate.update(
			"UPDATE tz_user SET loan_already_amount=GREATEST(0, IFNULL(loan_already_amount,0)-?), loan_can_amount=IFNULL(loan_can_amount,0)+? WHERE user_id=?",
			amount, amount, partyId);
	}

}
