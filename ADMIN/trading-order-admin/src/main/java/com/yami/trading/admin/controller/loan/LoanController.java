package com.yami.trading.admin.controller.loan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.loan.LoanParam;
import com.yami.trading.bean.loan.SimpleLoanOrder;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.UUIDGenerator;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 借贷API
 */
@RestController
@CrossOrigin
@SuppressWarnings("unchecked")
public class LoanController {
	/**
	 * Loan服务
	 */
	@Autowired
	private LoanService loanService;
	
	/**
	 * Token服务
	 */
//	@Autowired
//	private TokenService tokenService;
	
	/**
	 * SLF4J日志组件
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
	
	/**
	 * 借贷额度正则式
	 */
	private static final Pattern QUOTA_REGEX=Pattern.compile("([A-Z]{2,8}):([1-9]{1}[0-9]{0,7})");
	
	/**
	 * 获取借贷参数
	 * @param configId 配置ID
	 * @return 配置结果
	 */
	@RequestMapping("/api/loan!get.action")
	public ResultObject getParams(String configId) {
		ResultObject resultObject = new ResultObject();
		
		try {
			HashMap<String,Object> paramMap=loanService.getLoanParams(configId);
			if(null==paramMap) {
				resultObject.setMsg("获取借贷参数失败!");
				resultObject.setCode("1");
			}else {
//				ApplicationUtil.getHttpSession().setAttribute("paramMap", paramMap);
				resultObject.setMsg("获取借贷参数成功!");
				resultObject.setData(paramMap);
			}
		}catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 获取借贷参数
	 * @param request 配置ID
	 * @return 配置结果
	 */
	@RequestMapping("/api/loan!getLoanParamList.action")
	public ResultObject getLoanParamList(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String configId=request.getParameter("configId");
		try {
			List<LoanParam> loanParamList =loanService.getLoanParamList(configId);
			resultObject.setData(loanParamList);
		}catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}		
		return resultObject;
	}
	
	/**
	 * 提交借贷申请
	 * @param request 请求对象
	 * @return 申请结果
	 */
	@RequestMapping("/api/loan!apply.action")
	public ResultObject apply(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		
		// String token=request.getParameter("token");
		// if(null==token || (token=token.trim()).isEmpty()) {
		// 	resultObject.setCode("1");
		// 	resultObject.setMsg("从请求参数中获取Token为空!");
		// 	return resultObject;
		// }
		
//		String partyId=tokenService.cacheGet(token);
		String partyId = SecurityUtils.getCurrentUserId();
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		String term=getParamValue(request,resultObject,"term");
		if(null==term) return resultObject;
		
		String quota=getParamValue(request,resultObject,"quota");
		if(null==quota) return resultObject;
		
		String symbol=getParamValue(request,resultObject,"symbol");
		if(null==symbol) return resultObject;
		
		String dailyRate=getParamValue(request,resultObject,"dailyRate");
		if(null==dailyRate) return resultObject;
		
		String repayment=getParamValue(request,resultObject,"repayment");
		if(null==repayment) return resultObject;
		
		String repayCycle=getParamValue(request,resultObject,"repayCycle");
		if(null==repayCycle) return resultObject;
		
		String lendingInstitution=getParamValue(request,resultObject,"lendingInstitution");
		if(null==lendingInstitution) return resultObject;
		
		String lendingName = getParamValue(request,resultObject,"lendingName");		
		if(null==lendingName) return resultObject;
		
		String img_idimg_1=getParamValue(request,resultObject,"frontFile");
		if(null==img_idimg_1) return resultObject;
		String img_idimg_2=getParamValue(request,resultObject,"reverseFile");
		if(null==img_idimg_2) return resultObject;
		String img_idimg_3=getParamValue(request,resultObject,"fileList");
		if(null==img_idimg_3) return resultObject;
		String houseImgs=new StringBuilder(img_idimg_1).append(",").append(img_idimg_2).append(",").append(img_idimg_3).toString();	
		
		String incomeImg=request.getParameter("incomeImg");
		if(null==incomeImg || (incomeImg=incomeImg.trim()).isEmpty()) {
			incomeImg=null;
		}

		Date now = new Date();
		SimpleLoanOrder simpleLoanOrder=new SimpleLoanOrder(partyId,new BigDecimal(quota),symbol);
		String uuid = UUIDGenerator.getUUID();
		simpleLoanOrder.setUuid(uuid);
		simpleLoanOrder.setLendingInstitution(new Integer(lendingInstitution));
		simpleLoanOrder.setRepayCycle(new Integer(repayCycle));
		simpleLoanOrder.setRepayment(new Integer(repayment));
		simpleLoanOrder.setDailyRate(new BigDecimal(dailyRate));
		simpleLoanOrder.setTerm(new Integer(term));
		simpleLoanOrder.setHouseImgs(houseImgs);
		simpleLoanOrder.setIncomeImg(incomeImg);
		simpleLoanOrder.setState(1);
		simpleLoanOrder.setLendingName(lendingName);
		simpleLoanOrder.setCreateTime(now);
		try {
			boolean flag = loanService.addLoanOrder(simpleLoanOrder);
			if (flag) {
				//TODO
//				TipService tipService = ApplicationUtil.getBean(TipService.class);
//				tipService.saveTip(simpleLoanOrder.getId().toString(), TipConstants.LOAN_APPLY);
				resultObject.setMsg("借贷申请提交成功!");
			} else {
				resultObject.setCode("1");
				resultObject.setMsg("借贷申请提交失败!");
			}
		} catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 查看借贷申请单列表
	 * @param request 请求对象
	 * @return 申请单列表
	 */
	@RequestMapping("/api/loan!getOrders.action")
	public ResultObject getOrders(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		
		// String token=request.getParameter("token");
		// if(null==token || (token=token.trim()).isEmpty()) {
		// 	resultObject.setCode("1");
		// 	resultObject.setMsg("从请求参数中获取Token为空!");
		// 	return resultObject;
		// }
		
//		String partyId=tokenService.cacheGet(token);
		String partyId = SecurityUtils.getCurrentUserId();
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("partyId", partyId);
		paramMap.put("state", request.getParameter("state"));
		paramMap.put("symbol", request.getParameter("symbol"));
//		paramMap.put("endTime", request.getParameter("endTime"));
//		paramMap.put("startTime", request.getParameter("startTime"));
		paramMap.put("repayment", request.getParameter("repayment"));
		paramMap.put("lendingInstitution", request.getParameter("lendingInstitution"));

		if (StrUtil.isNotBlank(request.getParameter("endTime"))
				&& !request.getParameter("endTime").contains("null")
				&& !request.getParameter("endTime").contains("undefined")) {
			Date newTime = DateTimeTools.readQueryTime(request.getParameter("endTime").trim(), "yyyy-MM-dd HH:mm:ss", null);
			paramMap.put("endTime", DateUtil.formatDateTime(newTime));
		} else {
			paramMap.remove("endTime");
		}
		if (StrUtil.isNotBlank(request.getParameter("startTime"))
				&& !request.getParameter("startTime").contains("null")
				&& !request.getParameter("startTime").contains("undefined")) {
			Date newTime = DateTimeTools.readQueryTime(request.getParameter("startTime").trim(), "yyyy-MM-dd HH:mm:ss", null);
			paramMap.put("startTime", DateUtil.formatDateTime(newTime));
		} else {
			paramMap.remove("startTime");
		}

		try {
			List<SimpleLoanOrder> orders = loanService.getUserOrders(paramMap);
			resultObject.setMsg("获取借贷订单列表成功!");
			resultObject.setData(orders);
		}catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 查看借贷申请单
	 * @param request 请求对象
	 * @return 申请结果
	 */
	@RequestMapping("/api/loan!getOrderDetail.action")
	public ResultObject getOrderDetail(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		
		// String token=request.getParameter("token");
		// if(null==token || (token=token.trim()).isEmpty()) {
		// 	resultObject.setCode("1");
		// 	resultObject.setMsg("从请求参数中获取Token为空!");
		// 	return resultObject;
		// }

//		String partyId=tokenService.cacheGet(token);
		String partyId = SecurityUtils.getCurrentUserId();
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		String orderNo=getParamValue(request,resultObject,"orderNo");
		if(null==orderNo) return resultObject;
		
		try {
			SimpleLoanOrder loanOrder=loanService.getLoanOrder(partyId,orderNo);
			if(null!=loanOrder) {
				resultObject.setData(loanOrder);
			}else {
				resultObject.setCode("0");
				resultObject.setMsg("获取借贷订单失败!");
				return resultObject;
			}
		}catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 获取参数值
	 * @param request 请求对象
	 * @param resultObject 结果对象
	 * @param paramName 参数名
	 * @return 参数值
	 */
	private static final String getParamValue(HttpServletRequest request,ResultObject resultObject,String paramName) {
		String paramValue=request.getParameter(paramName);
		if(null==paramValue || (paramValue=paramValue.trim()).isEmpty()) {
			resultObject.setCode("1");
			resultObject.setMsg(String.format("获取参数[%s]为空!",paramName));
			return null;
		}else {
			return paramValue;
		}
	}
	
	/**
	 * 校验借贷额度是否有效
	 * @param resultObject 结果对象
	 * @param quota 借贷额度
	 * @return 是否无效
	 */
//	private static final boolean checkInvalidQuota(ResultObject resultObject,String symbol,Integer quota) {
//		HashMap<String,Object> paramMap=(HashMap<String,Object>)ApplicationUtil.getHttpSession().getAttribute("paramMap");
//		if(null==paramMap) {
//			resultObject.setCode("1");
//			resultObject.setMsg("从会话中获取借贷参数[paramMap]为空!");
//			return true;
//		}
//
//		Matcher matcher=QUOTA_REGEX.matcher((String)paramMap.get("maxQuota"));
//		Integer maxQuota=null;
//		while(matcher.find()) {
//			if(symbol.equalsIgnoreCase(matcher.group(1))) {
//				maxQuota=new Integer(matcher.group(2));
//				break;
//			}
//		}
//
//		if(null==maxQuota) {
//			resultObject.setCode("1");
//			resultObject.setMsg("未找到额度最大值!");
//			return true;
//		}
//
//		matcher=QUOTA_REGEX.matcher((String)paramMap.get("minQuota"));
//		Integer minQuota=null;
//		while(matcher.find()) {
//			if(symbol.equalsIgnoreCase(matcher.group(1))) {
//				minQuota=new Integer(matcher.group(2));
//				break;
//			}
//		}
//
//		if(null==minQuota) {
//			resultObject.setCode("1");
//			resultObject.setMsg("未找到额度最小值!");
//			return true;
//		}
//
//		if(quota<minQuota || quota>maxQuota) {
//			resultObject.setCode("1");
//			resultObject.setMsg("借贷额度不在允许范围内!");
//			return true;
//		}
//
//		return false;
//	}
}
