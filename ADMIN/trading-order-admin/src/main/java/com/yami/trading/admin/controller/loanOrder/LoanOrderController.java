package com.yami.trading.admin.controller.loanOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.loanOrder.LoanOrder;
import com.yami.trading.bean.loanOrder.LoanRelationOrder;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.UUIDGenerator;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 质押借币 API
 */
@RestController
@CrossOrigin
public class LoanOrderController {
	
	private Logger logger = LoggerFactory.getLogger(LoanOrderController.class);
	
	@Autowired
	LoanOrderService loanOrderService;
	
	@Autowired
	@Qualifier("dataService")
	private DataService dataService;
	@Autowired
	WalletService walletService;
	@Autowired
	LoanRelationOrderService loanRelationOrderService;

	private final String action = "/api/loan!";
	
	/**
	 * 获取配置信息
	 */
	@RequestMapping(action + "getLoanConfig.action")
	public Object getLoanConfig() {
		ResultObject resultObject = new ResultObject();
		resultObject.setData(loanOrderService.getLoanConfig());
		resultObject.setCode("0");
		return resultObject;
	}
	
	/**
	 * 获取页面参数
	 */
	@RequestMapping(action + "getLoanParam.action")
	public Object getLoanParam(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = SecurityUtils.getCurrentUserId();
//		String partyId = getLoginPartyId();
		// 借款订单ID
		String loanOrderId = request.getParameter("loanOrderId");
		// 借款金额
		String loanAmountRq = request.getParameter("loanAmount");
		// 还款金额
		String repayAmountRq = request.getParameter("repayAmount");
		// 质押金额
		String pledgeAmountRq = request.getParameter("pledgeAmount");
	    try {
	    	
			// 质押币 页面参数
			if (StringUtils.isNullOrEmpty(loanOrderId)) {
				String pledgeCurrencyRq = request.getParameter("pledgeCurrency");
				String loanCycleRq = request.getParameter("loanCycle");
				// 参数校验
				String error = verifyAddOrderParam(loanAmountRq, pledgeAmountRq, pledgeCurrencyRq, loanCycleRq, partyId);
				if (!StringUtils.isNullOrEmpty(error)) {
					if (StringUtils.isDouble(error)) {
						resultObject.setCode("10");
						resultObject.setMsg(error);
						return resultObject;
					} else {
						throw new BusinessException(error);
					}
				}
				
				Map<String, Object> map = loanParam(Double.valueOf(pledgeAmountRq), pledgeCurrencyRq, Double.valueOf(loanAmountRq), Integer.valueOf(loanCycleRq));
				resultObject.setData(map);
				resultObject.setCode("0");
				return resultObject;
			}
			
			// 续借 页面参数
			if (!StringUtils.isNullOrEmpty(loanOrderId) && !StringUtils.isNullOrEmpty(loanAmountRq)) {
				LoanOrder order = loanOrderService.getLoanOrderParam(loanOrderId);
				// 还款记录
				List<LoanRelationOrder> relationList = loanRelationOrderService.queryOrders(order.getUuid(), LoanConstants.PLEDGE_ORDER_TYPE_REPAY);
				double repayAmount = 0;
				for (LoanRelationOrder relation : relationList ) {
					repayAmount += relation.getLoan_amount();
				}
				double loanAmountSum = Arith.sub(order.getLoanAmount(), repayAmount);
				double pledgeAmount = order.getPledge_amount();
				String pledgeCurrency = order.getPledgeCurrency();
				double loanAmount = Arith.add(loanAmountSum, Double.valueOf(loanAmountRq));
				int loanCycle = order.getLoan_cycle();
				
				// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
				List<Realtime> realtimes = dataService.realtime(pledgeCurrency);
				double debtAmount = Arith.mul(realtimes.get(0).getClose(), pledgeAmount) * LoanConstants.PLEDGE_RATE_INITIAL;
				double rate = Arith.add(Arith.mul(order.getHourly_rate() * 24, loanCycle), 1);
				double loanAmountMax = Arith.div(debtAmount, rate, 2);
				double loanAmountSub = Arith.sub(loanAmountMax, loanAmountSum);
				if (loanAmountSub < Double.valueOf(loanAmountRq)) {
					resultObject.setCode("20");
					resultObject.setMsg(new BigDecimal(loanAmountSub).setScale(2, RoundingMode.DOWN).toString());
					return resultObject;
				}
				
				Map<String, Object> map = loanParam(pledgeAmount, pledgeCurrency, loanAmount, loanCycle);
				resultObject.setData(map);
				resultObject.setCode("0");
				return resultObject;
			}
			
			// 还款 页面参数
			if (!StringUtils.isNullOrEmpty(loanOrderId) && !StringUtils.isNullOrEmpty(repayAmountRq)) {
				LoanOrder order = loanOrderService.getLoanOrderParam(loanOrderId);
				double pledgeAmount = order.getPledge_amount();
				String pledgeCurrency = order.getPledgeCurrency();
				double repayAmount = Double.valueOf(repayAmountRq);
				Wallet wallet = walletService.saveWalletByPartyId(partyId);
				if (repayAmount > wallet.getMoney().doubleValue()) {
					throw new BusinessException(1, "Insufficient balance");
				}
				
				// 如果还款金额>负债金额 取 负债金额
				if (repayAmount > order.getDebt_amount()) {
					repayAmount = order.getDebt_amount();
				}
				
				double loanAmount = Arith.sub(order.getDebt_amount(), repayAmount);
				int loanCycle = order.getLoan_cycle();
				Map<String, Object> map = param(pledgeAmount, pledgeCurrency, loanAmount, loanCycle);
				resultObject.setData(map);
				resultObject.setCode("0");
				return resultObject;
			}
			
			// 补增 页面参数
			if (!StringUtils.isNullOrEmpty(loanOrderId) && !StringUtils.isNullOrEmpty(pledgeAmountRq)) {
				LoanOrder order = loanOrderService.getLoanOrderParam(loanOrderId);
				
				WalletExtend walletExtend = walletService.saveExtendByPara(partyId, order.getPledgeCurrency());
				if (walletExtend.getAmount() < Double.valueOf(pledgeAmountRq)) {
					throw new BusinessException(1, "Order error, Insufficient balance");
				}
				
				double pledgeAmount = Arith.add(order.getPledge_amount(), Double.valueOf(pledgeAmountRq));
				String pledgeCurrency = order.getPledgeCurrency();
				double loanAmount = order.getLoanAmount();
				int loanCycle = order.getLoan_cycle();
				
				Map<String, Object> map = param(pledgeAmount, pledgeCurrency, loanAmount, loanCycle);
				resultObject.setData(map);
				resultObject.setCode("0");
				return resultObject;
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}
	
	/**
	 * 借币
	 */
	@RequestMapping(action + "addOrder.action")
	public Object addOrder(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);

		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		String partyId = SecurityUtils.getCurrentUserId();
		// 借款金额
		String loanAmountRq = request.getParameter("loanAmount");
		// 质押金额
		String pledgeAmountRq = request.getParameter("pledgeAmount");
		// 质押币种
		String pledgeCurrencyRq = request.getParameter("pledgeCurrency");
		// 质押期限
		String loanCycleRq = request.getParameter("loanCycle");
		boolean lock = false;
	    try {
			String error = verifyAddOrderParam(loanAmountRq, pledgeAmountRq, pledgeCurrencyRq, loanCycleRq, partyId);
			if (!StringUtils.isNullOrEmpty(error)) {
				if (StringUtils.isDouble(error)) {
					throw new BusinessException(10, error);
				} else {
					throw new BusinessException(error);
				}
			}
			if (!LoanLock.add(partyId)) {
				throw new BusinessException("Please try again later");
			}
			lock = true;

			if (!StringUtils.isInteger(loanAmountRq)) {
				DecimalFormat df2 = new DecimalFormat("#.#########");
				loanAmountRq = df2.format(Double.valueOf(loanAmountRq));
			}

			if (!StringUtils.isInteger(pledgeAmountRq)) {
				DecimalFormat df4 = new DecimalFormat("#.########");
				pledgeAmountRq = df4.format(Double.valueOf(pledgeAmountRq));
			}
			// 质押借币记录
	    	LoanOrder order = new LoanOrder();
			String uuid = UUIDGenerator.getUUID();
			order.setUuid(uuid);
	    	order.setLoanAmount(Double.valueOf(loanAmountRq));
	    	order.setPledge_amount(Double.valueOf(pledgeAmountRq));
	    	order.setPledgeCurrency(pledgeCurrencyRq);
	    	order.setLoanCycle(Integer.valueOf(loanCycleRq));
	    	order.setPartyId(partyId);
	    	loanOrderService.saveLoanOrder(order);

	    	// 关联记录
	    	LoanRelationOrder relationOrder = new LoanRelationOrder();
	    	relationOrder.setLoanRelationOrderId(uuid);
	    	relationOrder.setPartyId(partyId);
	    	relationOrder.setLoanAmount(order.getLoanAmount());
	    	relationOrder.setPledgeCurrency(pledgeCurrencyRq);
	    	relationOrder.setPledge_amount(order.getPledge_amount());
	    	loanRelationOrderService.saveLoanRelationOrder(relationOrder);

			resultObject.setMsg("successful loan");
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
			logger.error("error:", e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				LoanLock.remove(partyId);
			}
		}
		return resultObject;
	}
	
	/**
	 * 质押借币订单
	 */
	@RequestMapping(action + "orderList.action")
	public Object orderList(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		String page_no = request.getParameter("page_no");
		int pageNo = Integer.valueOf(page_no);
//		String partyId = getLoginPartyId();
		String partyId = SecurityUtils.getCurrentUserId();
		List<Map<String, Object>> datas = loanOrderService.pagedQuery(pageNo, 10, partyId);
		Map<String, Object> data = new HashMap<>();
		data.put("list", datas);
		List<String> noticeList = loanOrderService.queryOrdersNoticeList(partyId);
		data.put("noticeList", noticeList);
		resultObject.setData(data);
		return resultObject;
	}
	
	/**
	 * 订单详情
	 */
	@RequestMapping(action + "getOrder.action")
	public Object getOrder(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
	    try {
			// 借款订单ID
			String loanOrderId = request.getParameter("loanOrderId");
			resultObject.setData(loanOrderService.getLoanOrder(loanOrderId));
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}
	
	/**
	 * 质押记录
	 */
	@RequestMapping(action + "relationOrderList.action")
	public Object relationOrderList(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		// 借款订单ID
		String loanOrderId = request.getParameter("loanOrderId");
		String page_no = request.getParameter("page_no");
		int pageNo = Integer.valueOf(page_no);
		resultObject.setData(loanRelationOrderService.pagedQueryPledge(pageNo, 10, loanOrderId));
		return resultObject;
	}
	
	/**
	 * 补增质押
	 */
	@RequestMapping(action + "replenishOrder.action")
	public Object replenishOrder(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		// 借款订单ID
		String loanOrderId = request.getParameter("loanOrderId");
		if (StringUtils.isNullOrEmpty(loanOrderId)) {
			throw new BusinessException(1, "Order error, loanOrderId is null");
		}
		
		// 质押金额
		String pledgeAmount = request.getParameter("pledgeAmount");
		if (StringUtils.isNullOrEmpty(pledgeAmount) 
				|| !StringUtils.isDouble(pledgeAmount) 
				|| Double.valueOf(pledgeAmount) <= 0) {
			throw new BusinessException(1, "Order error, pledge amount is a number");
		}
		
//		String partyId = getLoginPartyId();
		String partyId = SecurityUtils.getCurrentUserId();
		boolean lock = false;
	    try {
			if (!LoanLock.add(partyId)) {
				throw new BusinessException("Please try again later");
			}
			lock = true;
			loanOrderService.saveReplenishOrder(loanOrderId, Double.valueOf(pledgeAmount), partyId);
			resultObject.setMsg("successful replenish");
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				LoanLock.remove(partyId);
			}
		}
		return resultObject;
	}
	
	/**
	 * 续借
	 */
	@RequestMapping(action + "refurbishOrder.action")
	public Object refurbishOrder(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		// 借款订单ID
		String loanOrderId = request.getParameter("loanOrderId");
		if (StringUtils.isNullOrEmpty(loanOrderId)) {
			throw new BusinessException(1, "Order error, loanOrderId is null");
		}
		
		// 借款金额
		String loanAmount = request.getParameter("loanAmount");
		if (StringUtils.isNullOrEmpty(loanAmount) 
				|| !StringUtils.isDouble(loanAmount) 
				|| Double.valueOf(loanAmount) <= 0) {
			throw new BusinessException(1, "Order error, loan amount is a number");
		}
//		String partyId = getLoginPartyId();
		String partyId = SecurityUtils.getCurrentUserId();
		boolean lock = false;
	    try {
			if (!LoanLock.add(partyId)) {
				throw new BusinessException("Please try again later");
			}
			lock = true;
			ResultObject resultObject1 = (ResultObject) loanOrderService.saveRefurbishOrder(loanOrderId, Double.valueOf(loanAmount), partyId);
			if(resultObject1!=null){
				return resultObject1;
			}

			resultObject.setMsg("successful refurbish");
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				LoanLock.remove(partyId);
			}
		}
		return resultObject;
	}
	
	/**
	 * 还款
	 */
	@RequestMapping(action + "repayOrder.action")
	public Object repayOrder(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		// 借款订单ID
		String loanOrderId = request.getParameter("loanOrderId");
		if (StringUtils.isNullOrEmpty(loanOrderId)) {
			throw new BusinessException(1, "Order error, loanOrderId is null");
		}
		
		// 借款金额
		String repayAmount = request.getParameter("repayAmount");
		if (StringUtils.isNullOrEmpty(repayAmount) 
				|| !StringUtils.isDouble(repayAmount) 
				|| Double.valueOf(repayAmount) <= 0) {
			throw new BusinessException(1, "Order error, loan amount is a number");
		}
//		String partyId = getLoginPartyId();
		String partyId = SecurityUtils.getCurrentUserId();
		boolean lock = false;
	    try {
			if (!LoanLock.add(partyId)) {
				throw new BusinessException("Please try again later");
			}
			lock = true;
			loanOrderService.saveRepayOrder(loanOrderId, Double.valueOf(repayAmount), partyId);
			resultObject.setMsg("successful repay");
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				LoanLock.remove(partyId);
			}
		}
		return resultObject;
	}
	
	private Map<String, Object> loanParam(double pledgeAmount, String pledgeCurrencyRq, double loanAmount, int loanCycle) {
		List<Realtime> realtimes = dataService.realtime(pledgeCurrencyRq);
		
		Map<String, Object> config = loanOrderService.getLoanConfig();
		double hourlyRate = Double.valueOf(config.get("hourlyRate").toString());
		
		Map<String, Object> map = new HashMap<String, Object>();

		
		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
		double interestAmount = Arith.mul(loanAmount, hourlyRate * 24 * loanCycle);
		double debtAmount = Arith.add(loanAmount, interestAmount);
		
		// 强平价格
		map.put("closeOut", Arith.div(Arith.div(debtAmount, LoanConstants.PLEDGE_RATE_CLOSEOUT), pledgeAmount, 2));
		
		map.put("pledgeRate", Arith.div(Arith.div(debtAmount, realtimes.get(0).getClose()), pledgeAmount, 2));
		
		// 小时利率
		map.put("hourlyRate", hourlyRate);
		
		// 总利息
		DecimalFormat df4 = new DecimalFormat("#.####");
		map.put("interestAmount", df4.format(interestAmount));
		// 预计还款 即总负债
		map.put("debtAmount", df4.format(debtAmount));
		return map;
	}
	
	private Map<String, Object> param(double pledgeAmount, String pledgeCurrencyRq, double loanAmount, int loanCycle) {
		List<Realtime> realtimes = dataService.realtime(pledgeCurrencyRq);
		
		Map<String, Object> config = loanOrderService.getLoanConfig();
		double hourlyRate = Double.valueOf(config.get("hourlyRate").toString());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
		double interestAmount = Arith.mul(loanAmount, hourlyRate * 24 * loanCycle);
		double debtAmount = Arith.add(loanAmount, interestAmount);
		
		map.put("pledgeRate", Arith.div(Arith.div(debtAmount, realtimes.get(0).getClose()), pledgeAmount, 2));
		return map;
	}
	
	/**
	 * 校验借币接口参数
	 */
	private String verifyAddOrderParam(String loanAmountRq, String pledgeAmountRq, String pledgeCurrencyRq, 
			String loanCycleRq, String partyId) {
		
		if (StringUtils.isNullOrEmpty(loanAmountRq) 
				|| !StringUtils.isDouble(loanAmountRq) 
				|| Double.valueOf(loanAmountRq) <= 0) {
			return "Order error, loan amount is a number";
		}
		
		if (StringUtils.isNullOrEmpty(pledgeAmountRq) 
				|| !StringUtils.isDouble(pledgeAmountRq) 
				|| Double.valueOf(pledgeAmountRq) <= 0) {
			return "Order error, pledge amount is a number";
		}
		
		if (StringUtils.isNullOrEmpty(loanAmountRq) 
				|| !StringUtils.isDouble(loanAmountRq) 
				|| Double.valueOf(loanAmountRq) <= 0) {
			return "Order error, loan amount is a number";
		}
		
		if (StringUtils.isNullOrEmpty(loanCycleRq) 
				|| !StringUtils.isInteger(loanCycleRq) 
				|| Integer.valueOf(loanCycleRq) <= 0) {
			return "Order error, loan cycle is a whole number";
		}
		
		List<Realtime> realtimes = dataService.realtime(pledgeCurrencyRq);
		if (!"usdt".equalsIgnoreCase(pledgeCurrencyRq)){
			if (null == realtimes || realtimes.size() <= 0) {
				return "Order error, pledge currency is not found";
			}
		}

		
		Map<String, Object> map = loanOrderService.getLoanConfig();
		if (Double.valueOf(loanAmountRq) < Double.valueOf(map.get("loanAmountMin").toString())) {
			return "Order error, The minimum loan amount is " + map.get("loanAmountMin").toString();
		}
		
		List<String> loanCycles = Arrays.asList(map.get("loanCycle").toString());
		for (String cycle : loanCycles) {
			if (cycle.equals(loanCycleRq)) {
				return "Order error, loan cycle error";
			}
		}
		
		// 质押率 = (借款金额 + (借款金额 * 时利率 * 24 * 借款周期)) / 质押币的价格  / 质押数量
		double pledgeAmount = Double.valueOf(pledgeAmountRq);
		
		WalletExtend walletExtend = walletService.saveExtendByPara(partyId, pledgeCurrencyRq);
		if (walletExtend.getAmount() < pledgeAmount) {
			return "Order error, Insufficient balance";
		}
		
		double loanAmount = Double.valueOf(loanAmountRq);
		double hourlyRate = Double.valueOf(map.get("hourlyRate").toString());
		int loanCycle = Integer.valueOf(loanCycleRq);
		double interestAmount = Arith.mul(loanAmount, hourlyRate * 24 * loanCycle);
		double debtAmount = Arith.add(loanAmount, interestAmount);
		double pledgeAmountMin = Arith.div(Arith.div(debtAmount, realtimes.get(0).getClose()), LoanConstants.PLEDGE_RATE_INITIAL);
		
		if (pledgeAmountMin > pledgeAmount) {
			String min = new BigDecimal(pledgeAmountMin).setScale(2, RoundingMode.UP).toString();
			return min;
			// return "pledge amount min is " + min;
		}
		
//		if (pledgeAmountMin > pledgeAmount) {
//			return "The pledge rate is too high, pledge amount must not be less than: " + new BigDecimal(pledgeAmountMin).setScale(2, RoundingMode.UP);
//		}
	
		return "";
	}

	public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
		String partyId = SecurityUtils.getCurrentUserId();
		if (StringUtils.isNullOrEmpty(partyId)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		return resultObject;
	}
	
}
