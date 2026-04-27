package com.yami.trading.admin.controller.finance;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.finance.service.AdminFinanceService;
import com.yami.trading.service.finance.service.FinanceService;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.system.LogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;


/**
 * 理财配置
 */
@RestController
@CrossOrigin
public class AdminFinanceController {
	
	private Logger logger = LogManager.getLogger(AdminFinanceController.class);
//	@Autowired
//	private UserService userService;
	@Autowired
	protected AdminFinanceService adminFinanceService;
	@Autowired
	protected FinanceService financeService;
//	@Autowired
//	protected PartyService partyService;
	@Autowired
	protected LogService logService;
//	@Autowired
//	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	AwsS3OSSFileService awsS3OSSFileService;
	
	private final String action = "/normal/adminFinanceAction!";

	/**
	 * 获取 理财配置 列表
	 */
	@RequestMapping(action + "list.action")
	public Result list(HttpServletRequest request) {

//		ResultObject resultObject = new ResultObject();
//		resultObject = this.readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}


		System.out.println("current = " + request.getParameter("current"));
//		request.get

		int pageNo = Integer.parseInt(request.getParameter("current"));
		int pageSize = Integer.parseInt(request.getParameter("size"));
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");


		Page page = this.adminFinanceService.pagedQuery(pageNo, pageSize, name_para);



//		modelAndView.addObject("pageNo", pageNo);
//		modelAndView.addObject("pageSize", pageSize);
//		modelAndView.addObject("page", page);
//		modelAndView.addObject("message", message);
//		modelAndView.addObject("error", error);
//		modelAndView.addObject("name_para", name_para);
		return Result.ok(page);
	}

	/**
	 * 新增 理财配置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("finance_add");
		return modelAndView;
	}

	/**
	 * 新增 理财配置
	 */
	@RequestMapping(action + "add.action")
	public Result add(HttpServletRequest request) {
		// 产品名称
		String name = request.getParameter("name");
		// 产品图片
		String img = request.getParameter("img");
		// 周期
		String cycle = request.getParameter("cycle");
		// 日利率
		String daily_rate = request.getParameter("daily_rate");
		String daily_rate_max = request.getParameter("daily_rate_max");
		// 今日利率
		String today_rate = request.getParameter("today_rate");
		// 违约结算比例
		String default_ratio = request.getParameter("default_ratio");
		// 投资金额区间min
		String investment_min = request.getParameter("investment_min");
		// 投资金额区间max
		String investment_max = request.getParameter("investment_max");
		// 资金密码
		String login_safeword = request.getParameter("login_safeword");
		String name_en = request.getParameter("name_en");
		String name_cn = request.getParameter("name_cn");
		String name_kn = request.getParameter("name_kn");
		String name_jn = request.getParameter("name_jn");
		String state = request.getParameter("state");
		
//		ModelAndView model = new ModelAndView();
//		model.addObject("name", name);
//		model.addObject("img", img);
//		model.addObject("cycle", cycle);
//		model.addObject("daily_rate", daily_rate);
//		model.addObject("daily_rate_max", daily_rate_max);
//		model.addObject("today_rate", today_rate);
//		model.addObject("default_ratio", default_ratio);
//		model.addObject("investment_min", investment_min);
//		model.addObject("investment_max", investment_max);
//		model.addObject("name_en", name_en);
//		model.addObject("name_cn", name_cn);
//		model.addObject("name_kn", name_kn);
//		model.addObject("name_jn", name_jn);
//		model.addObject("state", state);

		Finance finance = new Finance();
		try {

			String error = this.verification(name, img, cycle, daily_rate, daily_rate_max, today_rate, 
					default_ratio, investment_min, investment_max, login_safeword);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			

			finance.setName(name);
			finance.setNameEn(name_en);
			finance.setNameCn(name_cn);
			finance.setNameKn(name_kn);
			finance.setNameJn(name_jn);
			if(img.startsWith("http")){
				finance.setImg(img);
			}else{
				finance.setImg(awsS3OSSFileService.getUrl(img));
			}
			finance.setCycle(Integer.valueOf(cycle));
			finance.setDailyRate(Double.valueOf(daily_rate));
			finance.setDailyRateMax(Double.valueOf(daily_rate_max));
			finance.setTodayRate(Double.valueOf(today_rate));
			finance.setDefaultRatio(Double.valueOf(default_ratio));
			finance.setInvestmentMin(Double.valueOf(investment_min));
			finance.setInvestmentMax(Double.valueOf(investment_max));
			finance.setState(state);

			this.financeService.save(finance, login_safeword, SecurityUtils.getSysUser().getUsername());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(SecurityUtils.getSysUser().getUsername());
			log.setOperator(SecurityUtils.getSysUser().getUsername());
			log.setLog("手动添加理财配置,ip:["+this.getIp()+"]");
			logService.save(log);
			
		} catch (BusinessException e) {

			return Result.failed(1, e.getMessage());
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			return Result.failed(1, t.getMessage());
		}
		
//		model.addObject("message", "操作成功");
//		model.setViewName("redirect:/" + action + "list.action");

		return Result.ok(finance);
	}
	
	/**
	 * 修改 理财配置 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
		
			Finance finance = this.financeService.findById(id);

			modelAndView.addObject("id", id);
			modelAndView.addObject("name", finance.getName());
			modelAndView.addObject("img", finance.getImg());
			modelAndView.addObject("cycle", finance.getCycle());
			modelAndView.addObject("daily_rate", finance.getDailyRate());
			modelAndView.addObject("daily_rate_max", finance.getDailyRateMax());
			modelAndView.addObject("today_rate", finance.getTodayRate());
			modelAndView.addObject("default_ratio", finance.getDefaultRatio());
			modelAndView.addObject("investment_min", finance.getInvestmentMin());
			modelAndView.addObject("investment_max", finance.getInvestmentMax());
			modelAndView.addObject("name_en", finance.getNameEn());
			modelAndView.addObject("name_cn", finance.getNameCn());
			modelAndView.addObject("name_kn", finance.getNameKn());
			modelAndView.addObject("name_jn", finance.getNameJn());
			modelAndView.addObject("state", finance.getState());
		
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.setViewName("finance_update");
		return modelAndView;
	}

	/**
	 * 修改 理财配置
	 */
	@RequestMapping(action + "update.action")
	public Result update(HttpServletRequest request) {
		String id = request.getParameter("id");
		// 产品名称
		String name = request.getParameter("name");
		// 产品图片
		String img = request.getParameter("img");
		// 周期
		String cycle = request.getParameter("cycle");
		// 日利率
		String daily_rate = request.getParameter("daily_rate");
		String daily_rate_max = request.getParameter("daily_rate_max");
		// 今日利率
		String today_rate = request.getParameter("today_rate");
		// 违约结算比例
		String default_ratio = request.getParameter("default_ratio");
		// 投资金额区间min
		String investment_min = request.getParameter("investment_min");
		// 投资金额区间max
		String investment_max = request.getParameter("investment_max");
		String name_en = request.getParameter("name_en");
		String name_cn = request.getParameter("name_cn");
		String name_kn = request.getParameter("name_kn");
		String name_jn = request.getParameter("name_jn");
		String state = request.getParameter("state");
		// 资金密码
		String login_safeword = request.getParameter("login_safeword");
		
//		ModelAndView model = new ModelAndView();
//		model.addObject("id", id);
//		model.addObject("name", name);
//		model.addObject("img", img);
//		model.addObject("cycle", cycle);
//		model.addObject("daily_rate", daily_rate);
//		model.addObject("daily_rate_max", daily_rate_max);
//		model.addObject("today_rate", today_rate);
//		model.addObject("default_ratio", default_ratio);
//		model.addObject("investment_min", investment_min);
//		model.addObject("investment_max", investment_max);
//		model.addObject("name_en", name_en);
//		model.addObject("name_cn", name_cn);
//		model.addObject("name_kn", name_kn);
//		model.addObject("name_jn", name_jn);
//		model.addObject("state", state);
		Finance finance = null;
		try {
			
			String error = this.verification(name, img, cycle, daily_rate, daily_rate_max, today_rate, 
					default_ratio, investment_min, investment_max, login_safeword);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			finance = this.financeService.findById(id);

			finance.setName(name);
			finance.setNameEn(name_en);
			finance.setNameCn(name_cn);
			finance.setNameKn(name_kn);
			finance.setNameJn(name_jn);
			if(img.startsWith("http")){
				finance.setImg(img);
			}else{
				finance.setImg(awsS3OSSFileService.getUrl(img));
			}

			finance.setCycle(Integer.valueOf(cycle));
			finance.setDailyRate(Double.valueOf(daily_rate));
			finance.setDailyRateMax(Double.valueOf(daily_rate_max));
			finance.setTodayRate(Double.valueOf(today_rate));
			finance.setDefaultRatio(Double.valueOf(default_ratio));
			finance.setInvestmentMin(Double.valueOf(investment_min));
			finance.setInvestmentMax(Double.valueOf(investment_max));
			finance.setState(state);
			
			this.financeService.update(finance, login_safeword, SecurityUtils.getSysUser().getUsername());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(SecurityUtils.getSysUser().getUsername());
			log.setOperator(SecurityUtils.getSysUser().getUsername());
			log.setLog("手动修改理财配置,ip:["+this.getIp()+"]");
			logService.save(log);
			
		} catch (BusinessException e) {
			return Result.failed(1, e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed(1, t.getMessage());
		}

		return Result.ok(finance);
	}
	
	/**
	 * 删除 理财配置
	 */
	@RequestMapping(action + "toDelete.action")
	public Result toDelete(HttpServletRequest request) {
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");

//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			this.financeService.delete(id, login_safeword, SecurityUtils.getSysUser().getUsername());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(SecurityUtils.getSysUser().getUsername());
			log.setOperator(SecurityUtils.getSysUser().getUsername());
			log.setLog("手动删除理财配置,ip:["+this.getIp()+"]");
			logService.save(log);

		} catch (BusinessException e) {
			return Result.failed(1, e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed(1, t.getMessage());
		}

		return Result.ok(0);
	}

	protected String verification(String name, String img, String cycle, String daily_rate, String daily_rate_max, String today_rate, 
			String default_ratio, String investment_min, String investment_max, String login_safeword) {
		if (StringUtils.isEmptyString(name)) {
			return "请输入产品名称";
		}			
		if (StringUtils.isEmptyString(img)) {
			return "请上传产品图片";
		}		
		if (StringUtils.isEmptyString(login_safeword)) {
			return "请输入登录人资金密码";
		}			
		if (StringUtils.isNullOrEmpty(cycle) 
				|| !StringUtils.isInteger(cycle) 
				|| Integer.valueOf(cycle) <= 0) {
			return "周期不能小于等于0天";
		}
		if (StringUtils.isNullOrEmpty(daily_rate) 
				|| !StringUtils.isDouble(daily_rate) 
				|| Double.valueOf(daily_rate) < 0) {
			return "日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(daily_rate_max) 
				|| !StringUtils.isDouble(daily_rate_max) 
				|| Double.valueOf(daily_rate_max) < 0) {
			return "日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(today_rate) 
				|| !StringUtils.isDouble(today_rate) 
				|| Double.valueOf(today_rate) < 0) {
			return "今日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(default_ratio) 
				|| !StringUtils.isDouble(default_ratio) 
				|| Double.valueOf(default_ratio) < 0) {
			return "违约结算比例不能小于0";
		}
		if (StringUtils.isNullOrEmpty(investment_min) 
				|| !StringUtils.isDouble(investment_min) 
				|| Double.valueOf(investment_min) < 0) {
			return "投资金额区间不能小于0";
		}
		if (StringUtils.isNullOrEmpty(investment_max) 
				|| !StringUtils.isDouble(investment_max) 
				|| Double.valueOf(investment_max) < 0) {
			return "投资金额区间不能小于0";
		}
		if (Double.valueOf(investment_max) <= Double.valueOf(investment_min)) {
			return "投资金额区间错误";
		}
		return null;
	}

	public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
		String partyId = SecurityUtils.getCurrentSysUserId();
		if (StringUtils.isNullOrEmpty(partyId)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		return resultObject;
	}

	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

}
