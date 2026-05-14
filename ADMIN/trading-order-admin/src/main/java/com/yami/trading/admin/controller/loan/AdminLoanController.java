package com.yami.trading.admin.controller.loan;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.loan.SimpleLoanOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author JORGE
 * @description 借贷管理
 */
@RestController
@CrossOrigin
public class AdminLoanController {
	/**
	 * 借贷服务
	 */
	@Autowired
	private LoanService loanService;

	@Autowired
	UserService partyService;

	@Autowired
	private PermissionFacade permissionFacade;
	
	/**
	 * SLF4J日志组件
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdminLoanController.class);
	
	/**
	 * 时间格式
	 */
	private static final DateFormat DATA_FORMATTER=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 借贷额度正则式
	 */
	private static final Pattern QUOTA_REGEX=Pattern.compile("([A-Z]{2,8}):([1-9]{1}[0-9]{0,7})");
	
	/**
	 * 获取借贷申请单列表
	 * @return 申请单列表
	 */
	@RequestMapping("normal/loanadmin!list.action")
	public Result listLoanOrder(HttpServletRequest request) {

		List<String> children = permissionFacade.getOwnerUserIds();

		String status=request.getParameter("status");
		String orderNo=request.getParameter("orderNo");
		String userName=request.getParameter("userName");
		int pageNo= Integer.parseInt(request.getParameter("current"));
		int pageSize= Integer.parseInt(request.getParameter("size"));
//		int pageSize = 30;

		if(null==status || (status=status.trim()).isEmpty()) status="0";
		
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("status", new Integer(status));
		paramMap.put("userName", userName);
		paramMap.put("orderNo", orderNo);
		paramMap.put("pageNo", pageNo);
		paramMap.put("pageSize", pageSize);
		paramMap.put("children", children);
		
		Page page=null;
		try {
			page=loanService.pagedQuery(paramMap);
		}catch(Throwable e) {
			logger.error("list loan order occur error:", e);
			return Result.failed("list loan order occur error:" + e);
		}
		
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.addObject("pageNo", pageNo);
//		modelAndView.addObject("status", status);
//		modelAndView.addObject("pageSize", pageSize);
//		if(null!=page) modelAndView.addObject("page",page);
//		modelAndView.setViewName("simple_loan_order_list");
		
		return Result.ok(page);
	}
	
	/**
	 * 改变借贷申请单状态
	 * @return 申请单列表
	 */
	@RequestMapping("normal/loanadmin!change.action")
	public Result changeLoanOrderState(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		
		String orderId=getParamValue(request,modelAndView,"orderId");
		if(null==orderId) return Result.failed("orderId is null");
		
		String status=getParamValue(request,modelAndView,"statusStr");
		if(null==status) return Result.failed("status is null");
		
		String reason=getParamValue(request,modelAndView,"reason");
		try {
			loanService.updateLoanOrderState(orderId, status,reason);
			if(!"1".equals(status)) {
				//TODO
//				TipService tipService = ApplicationUtil.getBean(TipService.class);
//				tipService.deleteTip(orderId);
			}
//			modelAndView.addObject("message", "操作成功");
		}catch(BusinessException e) {
//			modelAndView.addObject("message", e.getMessage());
			logger.error("change loan order state error:", e);
			return Result.ok(e.getMessage());
		}catch(Throwable e) {
//			modelAndView.addObject("message", "操作失败");
			logger.error("change loan order state error:", e);
			return Result.ok("操作失败");
		}
		
//		modelAndView.setViewName("redirect:/normal/loanadmin!list.action");
		
		return Result.ok("操作成功");
	}

	@RequestMapping("normal/loanadmin!partialRepay.action")
	public Result partialRepay(HttpServletRequest request) {
		String orderId = request.getParameter("orderId");
		String amountStr = request.getParameter("amount");
		if (orderId == null || amountStr == null) return Result.failed("参数不能为空");
		try {
			loanService.partialRepay(orderId, new BigDecimal(amountStr));
		} catch (Exception e) {
			logger.error("partialRepay error:", e);
			return Result.failed(e.getMessage());
		}
		return Result.ok("操作成功");
	}

	/**
	 * 返回借贷申请单添加页面
	 * @return 添加申请单表单页面
	 */
	@RequestMapping("normal/loanadmin!toAdd.action")
	public ModelAndView addBefore(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("simple_loan_order_add");
		
		try {
			HashMap<String,Object> paramMap=loanService.getLoanParams(null);
			if(null!=paramMap) {
				modelAndView.addObject("paramMap",paramMap);
				//todo
//				ApplicationUtil.getHttpSession().setAttribute("paramMap", paramMap);
				loanService.setParamMap(paramMap);
			}else {
				modelAndView.addObject("code", "0");
				modelAndView.addObject("message", "获取借贷参数失败!");
			}
		}catch(Throwable e) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","程序错误");
			logger.error("error:", e);
		}
		
		return modelAndView;
	}
	
	/**
	 * 添加借贷申请单
	 * @return 添加状态
	 */
	@RequestMapping("normal/loanadmin!add.action")
	public Result add(HttpServletRequest request) {
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("simple_loan_order_add");

		String partyId=request.getParameter("partyId");
		if(null==partyId) return Result.failed("partyId is null");
		
		String term=request.getParameter("term");
		if(null==term) return Result.failed("term is null");
		
		String state=request.getParameter("state");
		if(null==state) return Result.failed("state is null");
		
		String quota=request.getParameter("quota");
		if(null==quota) return Result.failed("quota is null");
		
		String symbol=request.getParameter("symbol");
		if(null==symbol) return Result.failed("symbol is null");
		
		String dailyRate=request.getParameter("dailyRate");
		if(null==dailyRate) return Result.failed("dailyRate is null");
		
		String repayment=request.getParameter("repayment");
		if(null==repayment) return Result.failed("repayment is null");
		
		String repayCycle=request.getParameter("repayCycle");
		if(null==repayCycle) return Result.failed("repayCycle is null");
		
		String lendingInstitution=request.getParameter("lendingInstitution");
		if(null==lendingInstitution) return Result.failed("lendingInstitution is null");
		
		/*String incomeImg=request.getParameter("incomeImg");
		if(null==incomeImg || (incomeImg=incomeImg.trim()).isEmpty()) {
			incomeImg=null;
		}*/
		
		Integer quotaInt=new Integer(quota);
//		if(checkInvalidQuota(modelAndView,symbol,quotaInt)) return modelAndView;
		
//		String img_idimg_1=getParamValue(request,modelAndView,"img_idimg_1");
		String img_idimg_1=request.getParameter("img_idimg_1");

		if(null==img_idimg_1) return Result.failed("img_idimg_1 is null");
//		String img_idimg_2=getParamValue(request,modelAndView,"img_idimg_2");
		String img_idimg_2=request.getParameter("img_idimg_2");
		if(null==img_idimg_2) return Result.failed("img_idimg_2 is null");
//		String img_idimg_3=getParamValue(request,modelAndView,"img_idimg_3");
		String img_idimg_3=request.getParameter("img_idimg_3");
		if(null==img_idimg_3) return Result.failed("img_idimg_3 is null");
		String houseImgs=new StringBuilder(img_idimg_1).append(",").append(img_idimg_2).append(",").append(img_idimg_3).toString();
		
		SimpleLoanOrder simpleLoanOrder=new SimpleLoanOrder(partyId,new BigDecimal(quota),symbol);
		simpleLoanOrder.setLendingInstitution(new Integer(lendingInstitution));
		simpleLoanOrder.setRepayCycle(new Integer(repayCycle));
		simpleLoanOrder.setRepayment(new Integer(repayment));
		simpleLoanOrder.setDailyRate(new BigDecimal(dailyRate));
		simpleLoanOrder.setState(new Integer(state));
		simpleLoanOrder.setTerm(new Integer(term));
		simpleLoanOrder.setHouseImgs(houseImgs);
		simpleLoanOrder.setCreateTime(new Date());
		
		try {
			boolean flag=loanService.addLoanOrder(simpleLoanOrder);
			if(flag) {
//				modelAndView.addObject("message","借贷申请提交成功!");
			}else {
//				modelAndView.addObject("code", "1");
//				modelAndView.addObject("message","借贷申请提交失败!");
				return Result.failed("1","借贷申请提交成功!");
			}
		}catch(Throwable e) {
//			modelAndView.addObject("code", "1");
//			modelAndView.addObject("message","程序错误");
			logger.error("error:", e);
			return Result.failed("1","程序错误");
		}
		
		return Result.ok("借贷申请提交成功!");
	}
	
	/**
	 * 返回借贷申请单修改页面
	 * @return 修改申请单页面
	 */
	@RequestMapping("normal/loanadmin!toModify.action")
	public ModelAndView modifyBefore(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("simple_loan_order_edit");
		
		String orderNo=getParamValue(request,modelAndView,"orderNo");
		if(null==orderNo) return modelAndView;
		
		try {
			HashMap<String,Object> paramMap=loanService.getLoanParams(null);
			if(null!=paramMap) {
				modelAndView.addObject("paramMap",paramMap);
				//todo
//				ApplicationUtil.getHttpSession().setAttribute("paramMap", paramMap);
				loanService.setParamMap(paramMap);
			}else {
				modelAndView.addObject("code", "0");
				modelAndView.addObject("message", "获取借贷参数失败!");
				return modelAndView;
			}
			
			SimpleLoanOrder loanOrder=loanService.getLoanOrder(null,orderNo);
			
//			UserService partyService = ApplicationUtil.getBean(PartyService.class);
			
			User party = partyService.cacheUserBy(loanOrder.getPartyId());
			if(ObjectUtils.isNotEmpty(party)) {
				loanOrder.setUsername(party.getUserName());
			}		
			if(null!=loanOrder) {
				modelAndView.addObject("order",loanOrder);
			}
		}catch(Throwable e) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","程序错误");
			logger.error("error:", e);
		}
		
		return modelAndView;
	}
	
	/**
	 * 修改借贷申请单
	 * @return 修改申请单状态
	 */
	@RequestMapping("normal/loanadmin!modify.action")
	public Result modify(@RequestBody JSONObject request) {
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/normal/loanadmin!list.action");

		String id=request.getString("orderNo");
		if(null==id) return Result.failed("orderNo is null");
		
//		String id=getParamValue(request,modelAndView,"orderNo");
//		if(null==id) return modelAndView;
		
//		String term=getParamValue(request,modelAndView,"term");
//		if(null==term) return modelAndView;

		String term=request.getString("term");
		if(null==term) return Result.failed("term is null");
		
//		String quota=getParamValue(request,modelAndView,"quota");
//		if(null==quota) return modelAndView;
		String quota=request.getString("quota");
		if(null==quota) return Result.failed("quota is null");

		
//		String symbol=getParamValue(request,modelAndView,"symbol");
//		if(null==symbol) return modelAndView;

		String symbol=request.getString("symbol");
		if(null==symbol) return Result.failed("symbol is null");
		
//		String dailyRate=getParamValue(request,modelAndView,"dailyRate");
//		if(null==dailyRate) return modelAndView;

		String dailyRate=request.getString("dailyRate");
		if(null==dailyRate) return Result.failed("dailyRate is null");


		String repayment=request.getString("repayment");
//		String repayment=getParamValue(request,modelAndView,"repayment");
		if(null==repayment) {
			repayment = "1";
		}
		String repayCycle=request.getString("repayCycle");
		if(null==repayCycle) return Result.failed("repayCycle is null");

		String createTime=request.getString("createTime");
		if(null==createTime) return Result.failed("createTime is null");
		
//		String repayCycle=getParamValue(request,modelAndView,"repayCycle");
//		if(null==repayCycle) return modelAndView;
//
//		String createTime=getParamValue(request,modelAndView,"createTime");
//		if(null==createTime) return modelAndView;
		
		//String lendingInstitution=getParamValue(request,modelAndView,"lendingInstitution");
		//if(null==lendingInstitution) return modelAndView;
		
		//Integer quotaInt=new Integer(quota);
		//if(checkInvalidQuota(modelAndView,symbol,quotaInt)) return modelAndView;
		
//		String img_idimg_1=getParamValue(request,modelAndView,"img_idimg_1");
//		//if(null==img_idimg_1) return modelAndView;
//		String img_idimg_2=getParamValue(request,modelAndView,"img_idimg_2");
//		//if(null==img_idimg_2) return modelAndView;
//		String img_idimg_3=getParamValue(request,modelAndView,"img_idimg_3");
//		//if(null==img_idimg_3) return modelAndView;

		String img_idimg_1=request.getString("img_idimg_1");
		if(null==img_idimg_1) return Result.failed("img_idimg_1 is null");

		String img_idimg_2=request.getString("img_idimg_2");
		if(null==img_idimg_2) return Result.failed("img_idimg_2 is null");

		String img_idimg_3=request.getString("img_idimg_3");
		if(null==img_idimg_3) return Result.failed("img_idimg_3 is null");
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		SimpleLoanOrder simpleLoanOrder=new SimpleLoanOrder(new BigDecimal(quota),symbol,id);
		simpleLoanOrder.setRepayCycle(new Integer(repayCycle));
		simpleLoanOrder.setRepayment(new Integer(repayment));
		simpleLoanOrder.setDailyRate(new BigDecimal(dailyRate));
		simpleLoanOrder.setTerm(new Integer(term));
		if(StringUtils.isNotBlank(img_idimg_3)) {
			String houseImgs=new StringBuilder(img_idimg_1).append(",").append(img_idimg_2).append(",").append(img_idimg_3).toString();
			simpleLoanOrder.setHouseImgs(houseImgs);
		}
		System.out.println("-------------------------------");
		try {
			simpleLoanOrder.setCreateTime(DATA_FORMATTER.parse(createTime));
		} catch (ParseException e) {
//			modelAndView.addObject("code", "1");
//			modelAndView.addObject("message",e.getMessage());
//			return modelAndView;
			logger.error("error:", e);
			return Result.failed("1",e.getMessage());
		}
		
		try {
			System.out.println("-------------------------------");
			boolean flag=loanService.modLoanOrder(simpleLoanOrder);
			if(flag) {
//				modelAndView.addObject("message","借贷申请单修改成功!");
			}else {
//				modelAndView.addObject("code", "1");
//				modelAndView.addObject("message","借贷申请单修改失败!");
				return Result.failed("1","借贷申请单修改失败!");
			}
		}catch(Throwable e) {
//			modelAndView.addObject("code", "1");
//			modelAndView.addObject("message","程序错误");
			logger.error("error:", e);
			return Result.failed("1","程序错误");
		}
		
		return Result.ok("借贷申请单修改成功!");
	}
	
	/**
	 * 删除借贷申请单
	 * @return 申请单列表
	 */
	@RequestMapping("normal/loanadmin!delete.action")
	public Result deleteLoanOrder(HttpServletRequest request) {
//		ModelAndView modelAndView = new ModelAndView();
		
//		String orderId=getParamValue(request,modelAndView,"orderId");
//		if(null==orderId) return modelAndView;

		String orderId=request.getParameter("orderId");
		if(null==orderId) return Result.failed("partyId is orderId");
		
		try {
			loanService.deleteLoanOrder(orderId);
//			modelAndView.addObject("message", "操作成功");
		}catch(BusinessException e) {
//			modelAndView.addObject("message", e.getMessage());
			logger.error("delete loan order state error:", e);
			return Result.ok(e.getMessage());
		}catch(Throwable e) {
//			modelAndView.addObject("message", "操作失败");
			logger.error("delete loan order occur error:", e);
			return Result.ok("操作失败");
		}
		
//		modelAndView.setViewName("redirect:/normal/loanadmin!list.action");
		
		return Result.ok("操作成功");
	}
	
	/**
	 * 获取参数值
	 * @param request 请求对象
	 * @param modelAndView 结果对象
	 * @param paramName 参数名
	 * @return 参数值
	 */
	private static final String getParamValue(HttpServletRequest request,ModelAndView modelAndView,String paramName) {
		String paramValue=request.getParameter(paramName);
		if(null==paramValue || (paramValue=paramValue.trim()).isEmpty()) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message",String.format("获取参数[%s]为空!",paramName));
			return null;
		}else {
			return paramValue;
		}
	}
	
	/**
	 * 校验借贷额度是否有效
	 * @param modelAndView 结果对象
	 * @param quota 借贷额度
	 * @return 是否无效
	 */
	private final boolean checkInvalidQuota(ModelAndView modelAndView,String symbol,Integer quota) {
		HashMap<String,Object> paramMap=loanService.getParamMap();
//		HashMap<String,Object> paramMap=(HashMap<String,Object>)ApplicationUtil.getHttpSession().getAttribute("paramMap");
		if(null==paramMap) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","从会话中获取借贷参数[paramMap]为空!");
			return true;
		}
		
		Matcher matcher=QUOTA_REGEX.matcher((String)paramMap.get("maxQuota"));
		Integer maxQuota=null;
		while(matcher.find()) {
			if(symbol.equalsIgnoreCase(matcher.group(1))) {
				maxQuota=new Integer(matcher.group(2));
				break;
			}
		}
		
		if(null==maxQuota) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","未找到额度最大值!");
			return true;
		}
		
		matcher=QUOTA_REGEX.matcher((String)paramMap.get("minQuota"));
		Integer minQuota=null;
		while(matcher.find()) {
			if(symbol.equalsIgnoreCase(matcher.group(1))) {
				minQuota=new Integer(matcher.group(2));
				break;
			}
		}
		
		if(null==minQuota) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","未找到额度最小值!");
			return true;
		}
		
		if(quota<minQuota || quota>maxQuota) {
			modelAndView.addObject("code", "1");
			modelAndView.addObject("message","借贷额度不在允许范围内!");
			return true;
		}
		
		return false;
	}
}
