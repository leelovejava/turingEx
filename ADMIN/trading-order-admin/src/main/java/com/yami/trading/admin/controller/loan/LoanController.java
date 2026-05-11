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
import com.yami.trading.bean.model.User;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.UUIDGenerator;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 借贷API控制器
 * 
 * <p>提供用户端的借贷相关接口，包括：</p>
 * <ul>
 *   <li>获取借贷配置参数</li>
 *   <li>提交借贷申请</li>
 *   <li>查询借贷订单列表</li>
 *   <li>查看借贷订单详情</li>
 * </ul>
 * 
 * @author system
 * @version 1.0
 */
@RestController
@CrossOrigin
@SuppressWarnings("unchecked")
public class LoanController {
	
	/**
	 * 借贷业务服务
	 * 用于处理借贷订单的创建、查询等业务逻辑
	 */
	@Autowired
	private LoanService loanService;
	
	/**
	 * 用户服务
	 * 用于查询用户信息和用户相关字段
	 */
	@Autowired
	private UserService userService;
	
	/**
	 * SLF4J日志组件
	 * 用于记录程序运行日志，便于问题排查和审计
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
	
	/**
	 * 借贷额度正则表达式
	 * 格式：币种代码(2-8位大写字母):金额(1-8位数字，首位不能为0)
	 * 例如：USDT:10000 表示最多可借10000个USDT
	 */
	private static final Pattern QUOTA_REGEX=Pattern.compile("([A-Z]{2,8}):([1-9]{1}[0-9]{0,7})");
	
	/**
	 * 获取借贷配置参数
	 * 
	 * <p>根据配置ID获取系统当前的借贷相关配置参数，包括：</p>
	 * <ul>
	 *   <li>最小借款额度</li>
	 *   <li>最大借款额度</li>
	 *   <li>日利率范围</li>
	 *   <li>支持的借贷机构</li>
	 * </ul>
	 * 
	 * @param configId 借贷配置ID，用于区分不同场景的借贷配置
	 * @return ResultObject 包含配置参数的JSON对象，code为"0"表示成功，其他表示失败
	 */
	@RequestMapping("/api/loan!get.action")
	public ResultObject getParams(String configId) {
		// 创建返回结果对象
		ResultObject resultObject = new ResultObject();
		
		try {
			// 调用服务层获取借贷参数配置
			HashMap<String,Object> paramMap=loanService.getLoanParams(configId);
			// 检查参数是否为空
			if(null==paramMap) {
				// 配置不存在或获取失败
				resultObject.setMsg("获取借贷参数失败!");
				resultObject.setCode("1");
			}else {
				// 配置获取成功，返回参数数据
				resultObject.setMsg("获取借贷参数成功!");
				resultObject.setData(paramMap);
			}
		}catch(Throwable e) {
			// 捕获异常，记录日志并返回错误信息
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("获取借贷参数异常:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 获取借贷参数列表
	 * 
	 * <p>根据配置ID获取该配置下的所有借贷参数列表，通常用于下拉选择</p>
	 * 
	 * @param request HTTP请求对象，包含configId参数
	 * @return ResultObject 包含借贷参数列表的JSON对象
	 */
	@RequestMapping("/api/loan!getLoanParamList.action")
	public ResultObject getLoanParamList(HttpServletRequest request) {
		String configId=request.getParameter("configId");
		ResultObject resultObject = new ResultObject();
		try {
			List<LoanParam> loanParamList = loanService.getLoanParamList(configId);
			String partyId = SecurityUtils.getCurrentUserId();
			BigDecimal loanCanAmount = BigDecimal.ZERO;
			if (partyId != null) {
				User user = userService.findByUserId(partyId);
				if (user != null && user.getLoanCanAmount() != null) {
					loanCanAmount = user.getLoanCanAmount();
				}
			}
			HashMap<String, Object> data = new HashMap<>();
			data.put("list", loanParamList);
			data.put("loanCanAmount", loanCanAmount);
			resultObject.setData(data);
		} catch(Throwable e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("获取借贷参数列表异常:", e);
		}
		return resultObject;
	}
	
	/**
	 * 提交借贷申请
	 * /api/loan!apply.action
	 * <p>用户提交借贷申请的核心接口，主要流程如下：</p>
	 * <ol>
	 *   <li>从SecurityContext中获取当前登录用户的partyId</li>
	 *   <li>校验各项必填参数（期限、额度、币种、利率等）</li>
	 *   <li>构建借贷订单对象SimpleLoanOrder</li>
	 *   <li>调用服务层保存借贷订单</li>
	 *   <li>返回申请结果</li>
	 * </ol>
	 * 
	 * @param request HTTP请求对象，包含以下参数：
	 *        term: 借款期限（月）
	 *        quota: 借款额度（金额）
	 *        symbol: 借款币种（如USDT）
	 *        dailyRate: 日利率
	 *        repayment: 还款方式（1-到期还本付息，2-按日还款等）
	 *        repayCycle: 还款周期
	 *        lendingInstitution: 借贷机构ID
	 *        lendingName: 借贷机构名称
	 *        frontFile: 身份证正面图片
	 *        reverseFile: 身份证背面图片
	 *        fileList: 房产证或其他证明材料图片
	 *        incomeImg: 收入证明图片（可选）
	 * @return ResultObject 包含申请结果，code为"0"表示成功，其他表示失败
	 */
	@RequestMapping("/api/loan!apply.action")
	public ResultObject apply(HttpServletRequest request) {
		// 创建返回结果对象
		ResultObject resultObject = new ResultObject();
		
		// 从安全上下文获取当前登录用户的partyId（用户唯一标识）
		String partyId = SecurityUtils.getCurrentUserId();
		// 校验用户是否登录
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		// ========== 获取用户信息并校验借贷状态 ==========
		// 根据partyId查询用户信息
		User user = userService.findByUserId(partyId);
		if (user == null) {
			resultObject.setCode("1");
			resultObject.setMsg("用户不存在!");
			return resultObject;
		}
		
		// 校验借贷状态：1正常 2禁止
		if (user.getLoanStatus() != null && user.getLoanStatus() == 2) {
			resultObject.setCode("1");
			resultObject.setMsg("借贷状态禁止!");
			return resultObject;
		}
		
		// 借款额度（金额）
		String quota=getParamValue(request,resultObject,"quota");
		if(null==quota) return resultObject;
		
		// 校验可贷金额
		BigDecimal quotaDecimal = new BigDecimal(quota);
		BigDecimal loanCanAmount = user.getLoanCanAmount();
		if (loanCanAmount == null) {
			loanCanAmount = BigDecimal.ZERO;
		}
		// 申请金额不能超过可贷金额
		if (quotaDecimal.compareTo(loanCanAmount) > 0) {
			resultObject.setCode("1");
			// 可贷金额不足
			resultObject.setMsg("Insufficient loanable amount!");
			return resultObject;
		}
		
		// 借款期限（月）
		String term=getParamValue(request,resultObject,"term");
		if(null==term) return resultObject;
		
		// 借款币种
		String symbol=getParamValue(request,resultObject,"symbol");
		if(null==symbol) return resultObject;
		
		// 日利率
		String dailyRate=getParamValue(request,resultObject,"dailyRate");
		if(null==dailyRate) return resultObject;
		
		// 还款方式
		String repayment=getParamValue(request,resultObject,"repayment");
		if(null==repayment) return resultObject;
		
		// 还款周期
		String repayCycle=getParamValue(request,resultObject,"repayCycle");
		if(null==repayCycle) return resultObject;
		
		// 借贷机构ID
		String lendingInstitution=getParamValue(request,resultObject,"lendingInstitution");
		if(null==lendingInstitution) return resultObject;
		
		// 借贷机构名称
		String lendingName = getParamValue(request,resultObject,"lendingName");		
		if(null==lendingName) return resultObject;
		
		// ========== 获取证明材料图片 ==========
		// 身份证正面
		String frontFile=getParamValue(request,resultObject,"frontFile");
		if(null==frontFile) return resultObject;
		// 身份证背面
		String reverseFile=getParamValue(request,resultObject,"reverseFile");
		if(null==reverseFile) return resultObject;
		// 房产证或其他证明材料
		String fileList=getParamValue(request,resultObject,"fileList");
		if(null==fileList) return resultObject;
		// 拼接所有图片路径，用逗号分隔
		String houseImgs=new StringBuilder(frontFile).append(",").append(reverseFile).append(",").append(fileList).toString();	
		
		// 收入证明图片（可选参数）
		String incomeImg=request.getParameter("incomeImg");
		if(null==incomeImg || (incomeImg=incomeImg.trim()).isEmpty()) {
			incomeImg=null;
		}

		// 获取当前时间作为创建时间
		Date now = new Date();
		
		// ========== 构建借贷订单对象 ==========
		// 创建借贷订单，传入用户ID、借款金额、借款币种
		SimpleLoanOrder simpleLoanOrder=new SimpleLoanOrder(partyId,new BigDecimal(quota),symbol);
		// 生成唯一订单号
		String uuid = UUIDGenerator.getUUID();
		simpleLoanOrder.setUuid(uuid);
		// 设置借贷机构
		simpleLoanOrder.setLendingInstitution(Integer.parseInt(lendingInstitution));
		// 设置还款周期
		simpleLoanOrder.setRepayCycle(Integer.parseInt(repayCycle));
		// 设置还款方式
		simpleLoanOrder.setRepayment(Integer.parseInt(repayment));
		// 设置日利率
		simpleLoanOrder.setDailyRate(new BigDecimal(dailyRate));
		// 设置借款期限
		simpleLoanOrder.setTerm(Integer.parseInt(term));
		// 设置证明材料图片
		simpleLoanOrder.setHouseImgs(houseImgs);
		// 设置收入证明图片
		simpleLoanOrder.setIncomeImg(incomeImg);
		// 设置订单状态为待审核（1-待审核，2-审核通过，3-审核拒绝）
		simpleLoanOrder.setState(1);
		// 设置借贷机构名称
		simpleLoanOrder.setLendingName(lendingName);
		// 设置创建时间
		simpleLoanOrder.setCreateTime(now);
		
		// ========== 保存订单并返回结果 ==========
		try {
			// 调用服务层保存借贷订单
			boolean flag = loanService.addLoanOrder(simpleLoanOrder);
			if (flag) {
				// 订单保存成功，更新用户可贷金额和已贷金额
				User updateUser = new User();
				updateUser.setUserId(partyId);
				// 计算新的可贷金额 = 原可贷金额 - 申请金额
				BigDecimal newLoanCanAmount = loanCanAmount.subtract(quotaDecimal);
				// 计算新的已贷金额 = 原已贷金额 + 申请金额
				BigDecimal newLoanAlreadyAmount = user.getLoanAlreadyAmount() != null 
					? user.getLoanAlreadyAmount().add(quotaDecimal) 
					: quotaDecimal;
				updateUser.setLoanCanAmount(newLoanCanAmount);
				updateUser.setLoanAlreadyAmount(newLoanAlreadyAmount);
				// 更新用户借贷信息
				userService.updateById(updateUser);
				
				resultObject.setMsg("借贷申请提交成功!");
			} else {
				// 订单保存失败
				resultObject.setCode("1");
				resultObject.setMsg("借贷申请提交失败!");
			}
		} catch(Throwable e) {
			// 捕获异常，记录日志并返回错误信息
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("提交借贷申请异常:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 查看借贷申请单列表
	 * 
	 * <p>获取当前用户的借贷订单列表，支持多条件筛选</p>
	 * 
	 * <p>筛选条件包括：</p>
	 * <ul>
	 *   <li>state: 订单状态</li>
	 *   <li>symbol: 借款币种</li>
	 *   <li>repayment: 还款方式</li>
	 *   <li>lendingInstitution: 借贷机构</li>
	 *   <li>startTime/endTime: 创建时间范围</li>
	 * </ul>
	 * 
	 * @param request HTTP请求对象，包含筛选参数
	 * @return ResultObject 包含借贷订单列表
	 */
	@RequestMapping("/api/loan!getOrders.action")
	public ResultObject getOrders(HttpServletRequest request) {
		// 创建返回结果对象
		ResultObject resultObject = new ResultObject();
		
		// 从安全上下文获取当前登录用户的partyId
		String partyId = SecurityUtils.getCurrentUserId();
		// 校验用户是否登录
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		// 构建查询参数Map
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("partyId", partyId);
		paramMap.put("state", request.getParameter("state"));
		paramMap.put("symbol", request.getParameter("symbol"));
		paramMap.put("repayment", request.getParameter("repayment"));
		paramMap.put("lendingInstitution", request.getParameter("lendingInstitution"));

		// 处理结束时间参数
		if (StrUtil.isNotBlank(request.getParameter("endTime"))
				&& !request.getParameter("endTime").contains("null")
				&& !request.getParameter("endTime").contains("undefined")) {
			// 解析结束时间字符串为Date对象
			Date newTime = DateTimeTools.readQueryTime(request.getParameter("endTime").trim(), "yyyy-MM-dd HH:mm:ss", null);
			paramMap.put("endTime", DateUtil.formatDateTime(newTime));
		} else {
			paramMap.remove("endTime");
		}
		
		// 处理开始时间参数
		if (StrUtil.isNotBlank(request.getParameter("startTime"))
				&& !request.getParameter("startTime").contains("null")
				&& !request.getParameter("startTime").contains("undefined")) {
			// 解析开始时间字符串为Date对象
			Date newTime = DateTimeTools.readQueryTime(request.getParameter("startTime").trim(), "yyyy-MM-dd HH:mm:ss", null);
			paramMap.put("startTime", DateUtil.formatDateTime(newTime));
		} else {
			paramMap.remove("startTime");
		}

		try {
			// 调用服务层获取用户的借贷订单列表
			List<SimpleLoanOrder> orders = loanService.getUserOrders(paramMap);
			resultObject.setMsg("获取借贷订单列表成功!");
			resultObject.setData(orders);
		}catch(Throwable e) {
			// 捕获异常，记录日志并返回错误信息
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("获取借贷订单列表异常:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 查看借贷申请单详情
	 * 
	 * <p>根据订单号查询单条借贷订单的详细信息</p>
	 * 
	 * @param request HTTP请求对象，包含orderNo参数（订单号）
	 * @return ResultObject 包含借贷订单详情
	 */
	@RequestMapping("/api/loan!getOrderDetail.action")
	public ResultObject getOrderDetail(HttpServletRequest request) {
		// 创建返回结果对象
		ResultObject resultObject = new ResultObject();
		
		// 从安全上下文获取当前登录用户的partyId
		String partyId = SecurityUtils.getCurrentUserId();
		// 校验用户是否登录
		if(null==partyId) {
			resultObject.setCode("1");
			resultObject.setMsg("通过Token获取partyId为空!");
			return resultObject;
		}
		
		// 获取并校验订单号参数
		String orderNo=getParamValue(request,resultObject,"orderNo");
		if(null==orderNo) return resultObject;
		
		try {
			// 调用服务层获取指定订单号的借贷订单详情
			SimpleLoanOrder loanOrder=loanService.getLoanOrder(partyId,orderNo);
			if(null!=loanOrder) {
				// 订单存在，返回订单数据
				resultObject.setData(loanOrder);
			}else {
				// 订单不存在或无权访问
				resultObject.setCode("0");
				resultObject.setMsg("获取借贷订单失败!");
				return resultObject;
			}
		}catch(Throwable e) {
			// 捕获异常，记录日志并返回错误信息
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("获取借贷订单详情异常:", e);
		}
		
		return resultObject;
	}
	
	/**
	 * 获取请求参数值
	 * 
	 * <p>从HTTP请求中获取指定名称的参数值，并进行非空校验</p>
	 * 
	 * <p>校验规则：</p>
	 * <ul>
	 *   <li>参数值不能为null</li>
	 *   <li>参数值不能为空白字符串（trim后）</li>
	 * </ul>
	 * 
	 * @param request HTTP请求对象
	 * @param resultObject 结果对象，用于设置错误信息
	 * @param paramName 参数名称
	 * @return 参数值，如果校验失败则返回null
	 */
	private static final String getParamValue(HttpServletRequest request,ResultObject resultObject,String paramName) {
		// 从请求中获取参数值
		String paramValue=request.getParameter(paramName);
		// 校验参数是否为空
		if(null==paramValue || (paramValue=paramValue.trim()).isEmpty()) {
			// 设置错误码和错误信息
			resultObject.setCode("1");
			resultObject.setMsg(String.format("获取参数[%s]为空!",paramName));
			return null;
		}else {
			// 参数校验通过，返回参数值
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
