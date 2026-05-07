package com.yami.trading.admin.controller.miner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.util.ServletUtil;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yami.trading.service.miner.service.AdminMinerService;
import com.yami.trading.service.miner.service.MinerService;

/**
 * 矿机配置
 */
@RestController
@CrossOrigin
public class AdminMinerController {

	private Logger logger = LogManager.getLogger(AdminMinerController.class);

	@Autowired
	protected AdminMinerService adminMinerService;
	@Autowired
	protected MinerService minerService;
	@Autowired
	protected UserService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SysUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	private final String action = "normal/adminMinerAction!";
	
	/**
	 * 获取 矿机配置 列表
	 */
	@RequestMapping(action + "list.action")
	public Result list(HttpServletRequest request) {
//		this.checkAndSetPageNo(request.getParameter("pageNo"));
		String name_para = request.getParameter("name_para");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		int pageNo = 0;
		int pageSize = 300;
		Page page = this.adminMinerService.pagedQuery(pageNo, 300, name_para);
		for(Map<String,Object> data:(List<Map<String,Object>>) (page.getRecords())) {
			if("N".equals(data.get("test"))) {
				data.put("cycle", null);
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		map.put("page", page);
		map.put("name_para", name_para);
		map.put("message", message);
		map.put("error", error);

		return Result.ok(map);
	}

	public String toAdd() {
		return "add";
	}
	
	/**
	 * 验证基础信息
	 */
	protected String verifBase(String algorithm, String computing_power, String power, String product_factory,
			String product_size, String weight, String work_temperature_min, String work_temperature_max, String work_humidity_min,
			String work_humidity_max, String internet) {
		if (StringUtils.isEmptyString(algorithm)) {
			return "请选择适用算法";
		}
			
		if (StringUtils.isNullOrEmpty(computing_power) 
				|| !StringUtils.isDouble(computing_power)
				|| Double.valueOf(computing_power) < 0) {
			return "额定算力不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(power) 
				|| !StringUtils.isDouble(power)
				|| Double.valueOf(power) < 0) {
			return "功耗不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(product_factory)) {
			return "请输入生产厂家";
		}
			
		if (StringUtils.isNullOrEmpty(product_size)) {
			return "请输入外箱尺寸";
		}
		
		if (StringUtils.isNullOrEmpty(weight) 
				|| !StringUtils.isDouble(weight)
				|| Double.valueOf(weight) < 0) {
			return "整机重量不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(work_temperature_min) 
				|| !StringUtils.isDouble(work_temperature_min)) {
			return "工作温度区间最小值错误";
		}
		
		if (StringUtils.isNullOrEmpty(work_temperature_max) 
				|| !StringUtils.isDouble(work_temperature_max)
				|| Double.valueOf(work_temperature_max) == 0) {
			return "工作温度区间最大值错误";
		}
		
		if (Double.valueOf(work_temperature_max) <= Double.valueOf(work_temperature_min)) {
			return "工作温度区间错误";
		}
		
		if (StringUtils.isNullOrEmpty(work_humidity_min) 
				|| !StringUtils.isDouble(work_humidity_min)
				|| Double.valueOf(work_humidity_min) < 0) {
			return "工作湿度区间不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(work_humidity_max) 
				|| !StringUtils.isDouble(work_humidity_max)
				|| Double.valueOf(work_humidity_max) == 0) {
			return "工作温度区间最大值错误";
		}
		
		if (StringUtils.isNullOrEmpty(work_humidity_max) 
				|| !StringUtils.isDouble(work_humidity_max)
				|| Double.valueOf(work_humidity_max) == 0) {
			return "工作温度区间最大值错误";
		}
		
		if (Double.valueOf(work_humidity_max) <= Double.valueOf(work_humidity_min)) {
			return "工作温度区间错误";
		}
		
		if (StringUtils.isEmptyString(internet)) {
			return "请选择网络连接";
		}
			
		return null;
	}
	protected String verification(String name, String show_daily_rate, String daily_rate, String cycle_close, String investment_min, String investment_max,
			String algorithm, String computing_power, String power, String product_factory,
			String product_size, String weight, String work_temperature_min, String work_temperature_max, String work_humidity_min,
			String work_humidity_max, String internet) {
		
		String verifBase = verifBase(algorithm, computing_power, power, product_factory,
				product_size, weight, work_temperature_min, work_temperature_max, work_humidity_min,
				work_humidity_max, internet);
		
		if(StringUtils.isNotEmpty(verifBase)) {
			return verifBase;
		}
		if (StringUtils.isEmptyString(name)) {
			return "请输入产品名称";
		}
			
//		if (StringUtils.isEmptyString(this.img))
//			return "请上传产品图片";
//		if (this.cycle <= 0) {
//			return "周期不能小于等于0天";
//		}
//		if (this.daily_rate < 0.0D) {
//			return "日利率不能小于0";
//		}
		
		if (StringUtils.isNullOrEmpty(show_daily_rate) 
				|| !StringUtils.isDouble(show_daily_rate) 
				|| Double.valueOf(show_daily_rate) < 0) {
			return "日利率不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(daily_rate) 
				|| !StringUtils.isDouble(daily_rate) 
				|| Double.valueOf(daily_rate) < 0) {
			return "今日利率不能小于0";
		}

		if (StringUtils.isNullOrEmpty(cycle_close) 
				|| !StringUtils.isDouble(cycle_close) 
				|| Double.valueOf(cycle_close) < 0) {
			return "解锁周期不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(investment_min) 
				|| !StringUtils.isDouble(investment_min) 
				|| Double.valueOf(investment_min) < 0) {
			return "投资金额区间最小值不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(investment_max) 
				|| !StringUtils.isDouble(investment_max) 
				|| Double.valueOf(investment_max) < 0) {
			return "投资金额区间最大值不能小于0";
		}
		
		// max=0表示没有设置上限
		if (Double.valueOf(investment_max) < Double.valueOf(investment_min)) {
			return "投资金额区间错误";
		}
		return null;
	}

//	public String add() {
//		try {
//			this.error = verification();
//			if (!StringUtils.isNullOrEmpty(this.error))
//				return toAdd();
////      if (this.minerService.findByName(this.name) != null) {
////    	  this.error ="产品名称已存在";
////    	  return toAdd(); 
////		}
//			Miner miner = new Miner();
//			miner.setName(this.name);
//			miner.setName_en(this.name_en);
//			miner.setName_cn(this.name_cn);
////			miner.setImg(this.img);
////			miner.setCycle(this.cycle);
//			miner.setCycle_close(this.cycle_close);
//			miner.setDaily_rate(this.daily_rate);
//			miner.setShow_daily_rate(this.show_daily_rate);
//			miner.setInvestment_min(this.investment_min);
//			miner.setInvestment_max(this.investment_max);
//			miner.setState("1");
//			miner.setTest(false);
//			miner.setOn_sale(this.on_sale);
//			
//			//基础参数
//			miner.setAlgorithm(algorithm);
//			miner.setComputing_power(computing_power);
//			miner.setComputing_power_unit(computing_power_unit);
//			miner.setPower(power);
//			miner.setProduct_factory(product_factory);
//			miner.setProduct_size(product_size);
//			miner.setWeight(weight);
//			miner.setWork_temperature_min(work_temperature_min);
//			miner.setWork_temperature_max(work_temperature_max);
//			miner.setWork_humidity_min(work_humidity_min);
//			miner.setWork_humidity_max(work_humidity_max);
//			miner.setInternet(internet);
//
//			this.minerService.save(miner);
//			this.message = "操作成功";
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return toAdd();
//		} catch (Throwable t) {
//			logger.error("UserAction.register error ", t);
//			this.error = "[ERROR] " + t.getMessage();
//			return toAdd();
//		}
//		return list();
//	}

	protected String verificationUpdate(String name, String show_daily_rate, String daily_rate, String cycle_close, String investment_min, String investment_max,
			String algorithm, String computing_power, String power, String product_factory,
			String product_size, String weight, String work_temperature_min, String work_temperature_max, String work_humidity_min,
			String work_humidity_max, String internet) {
		String verifBase = verifBase(algorithm, computing_power, power, product_factory,
				product_size, weight, work_temperature_min, work_temperature_max, work_humidity_min,
				work_humidity_max, internet);
		if(StringUtils.isNotEmpty(verifBase)) {
			return verifBase;
		}
		if (StringUtils.isEmptyString(name))
			return "请输入产品名称";
//		if (StringUtils.isEmptyString(this.img))
//			return "请上传产品图片";
//		if (this.cycle <= 0) {
//			return "周期不能小于等于0天";
//		}
		if (StringUtils.isNullOrEmpty(show_daily_rate) 
				|| !StringUtils.isDouble(show_daily_rate) 
				|| Double.valueOf(show_daily_rate) < 0) {
			return "日利率不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(daily_rate) 
				|| !StringUtils.isDouble(daily_rate) 
				|| Double.valueOf(daily_rate) < 0) {
			return "今日利率不能小于0";
		}

		if (StringUtils.isNullOrEmpty(cycle_close) 
				|| !StringUtils.isDouble(cycle_close) 
				|| Double.valueOf(cycle_close) < 0) {
			return "解锁周期不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(investment_min) 
				|| !StringUtils.isDouble(investment_min) 
				|| Double.valueOf(investment_min) < 0) {
			return "投资金额区间最小值不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(investment_max) 
				|| !StringUtils.isDouble(investment_max) 
				|| Double.valueOf(investment_max) < 0) {
			return "投资金额区间最大值不能小于0";
		}
		
		// max=0表示没有设置上限
		if (Double.valueOf(investment_max) < Double.valueOf(investment_min)) {
			return "投资金额区间错误";
		}

		return null;
	}

	@RequestMapping(action + "toUpdate.action")
	public Result toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");
		Miner miner = this.minerService.findById(id);
		String name = miner.getName();
		String name_en = miner.getName_en();
		String name_cn = miner.getName_cn();
//		this.img = miner.getImg();
//		this.cycle = miner.getCycle();
		int cycle_close = miner.getCycle_close();
		double daily_rate = miner.getDaily_rate();
		double investment_min = miner.getInvestment_min();
		double investment_max = (miner.getTest() == "N") && miner.getInvestment_max() == 0 ? null : miner.getInvestment_max();
		double show_daily_rate = miner.getShow_daily_rate();
//		this.state = miner.getState();
		String test = miner.getTest();
		String on_sale = miner.getOn_sale();
		
		// 体验矿机随机日利率范围
		double daily_rate_start = miner.getDaily_rate_start();
		double daily_rate_end = miner.getDaily_rate_end();
		
		//基础参数
		String algorithm = miner.getAlgorithm();
		double computing_power = miner.getComputing_power();
		String computing_power_unit = miner.getComputing_power_unit();
		double power = miner.getPower();
		String product_factory = miner.getProduct_factory();
		String product_size = miner.getProduct_size();
		double weight = miner.getWeight();
		double work_temperature_min = miner.getWork_temperature_min();
		double work_temperature_max = miner.getWork_temperature_max();
		double work_humidity_min = miner.getWork_humidity_min();
		double work_humidity_max = miner.getWork_humidity_max();
		String internet = miner.getInternet();
		
		Map<String,Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		map.put("name_en", name_en);
		map.put("name_cn", name_cn);
		map.put("cycle_close", cycle_close);
		map.put("daily_rate", daily_rate);
		map.put("investment_min", investment_min);
		map.put("investment_max", investment_max);
		map.put("show_daily_rate", show_daily_rate);
		map.put("test", test);
		map.put("on_sale", on_sale);
		// 体验矿机日利率范围
		map.put("daily_rate_start", daily_rate_start);
		map.put("daily_rate_end", daily_rate_end);
		map.put("algorithm", algorithm);
		map.put("computing_power", computing_power);
		map.put("computing_power_unit", computing_power_unit);
		map.put("power", power);
		map.put("product_factory", product_factory);
		map.put("product_size", product_size);
		map.put("weight", weight);
		map.put("work_temperature_min", work_temperature_min);
		map.put("work_temperature_max", work_temperature_max);
		map.put("work_humidity_min", work_humidity_min);
		map.put("work_humidity_max", work_humidity_max);
		map.put("internet", internet);

		return Result.ok(map);
	}

	@RequestMapping(action + "update.action")
	public Result update(HttpServletRequest request) {

		String error = "";
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String show_daily_rate = request.getParameter("show_daily_rate"); 
		String daily_rate = request.getParameter("daily_rate");
		String cycle_close = request.getParameter("cycle_close");
		String investment_min = request.getParameter("investment_min");
		String investment_max = request.getParameter("investment_max");
		String algorithm = request.getParameter("algorithm");
		String computing_power = request.getParameter("computing_power");
		String power = request.getParameter("power");
		String product_factory = request.getParameter("product_factory");
		String product_size = request.getParameter("product_size");
		String weight = request.getParameter("weight");
		String work_temperature_min = request.getParameter("work_temperature_min");
		String work_temperature_max = request.getParameter("work_temperature_max");
		String work_humidity_min = request.getParameter("work_humidity_min");
		String work_humidity_max = request.getParameter("work_humidity_max");
		String internet = request.getParameter("internet");
		String buyCurrency = request.getParameter("buyCurrency");
		String outputCurrency = request.getParameter("outputCurrency");

		if(StringUtils.isNullOrEmpty(buyCurrency)){
			buyCurrency = "usdt";
		}

		if(StringUtils.isNullOrEmpty(outputCurrency)){
			outputCurrency = "usdt";
		}
		
		String name_en = request.getParameter("name_en");
		String name_cn = request.getParameter("name_cn");
		String test = request.getParameter("test");
		String on_sale = request.getParameter("on_sale");
		String login_safeword = request.getParameter("login_safeword");
		String computing_power_unit = request.getParameter("computing_power_unit");
		// 体验矿机日利率范围
		String daily_rate_start = request.getParameter("daily_rate_start");
		String daily_rate_end = request.getParameter("daily_rate_end");
		
//		ModelAndView model = new ModelAndView();
//		model.addObject("id", id);
//		model.addObject("name", name);
//		model.addObject("name_en", name_en);
//		model.addObject("name_cn", name_cn);
//		model.addObject("cycle_close", cycle_close);
//		model.addObject("daily_rate", daily_rate);
//		model.addObject("investment_min", investment_min);
//		model.addObject("investment_max", investment_max);
//		model.addObject("show_daily_rate", show_daily_rate);
//		model.addObject("test", test);
//		model.addObject("on_sale", on_sale);
//		model.addObject("algorithm", algorithm);
//		model.addObject("computing_power", computing_power);
//		model.addObject("computing_power_unit", computing_power_unit);
//		model.addObject("power", power);
//		model.addObject("product_factory", product_factory);
//		model.addObject("product_size", product_size);
//		model.addObject("weight", weight);
//		model.addObject("work_temperature_min", work_temperature_min);
//		model.addObject("work_temperature_max", work_temperature_max);
//		model.addObject("work_humidity_min", work_humidity_min);
//		model.addObject("work_humidity_max", work_humidity_max);
//		model.addObject("internet", internet);
		
		Miner miner = this.minerService.findById(id);
		try {
			error = verificationUpdate(name, show_daily_rate, daily_rate, cycle_close, investment_min, investment_max,
					algorithm, computing_power, power, product_factory,
					product_size, weight, work_temperature_min, work_temperature_max, work_humidity_min,
					work_humidity_max, internet);
			if (!StringUtils.isNullOrEmpty(error)) {
//				model.addObject("error", error);
//				model.setViewName("miner_update");
				return Result.failed(error);
			}
			String username = SecurityUtils.getSysUser().getUsername();
//			SysUser sec = this.secUserService.getByUserName(username);
//			User sec = this.secUserService.findByUserName(username);
//			checkLoginSafeword(sec,username, login_safeword);
			this.secUserService.checkSafeWord(login_safeword);
			
			miner.setName(name);
			miner.setName_en(name_en);
			miner.setName_cn(name_cn);
//			miner.setCycle(this.cycle);
			miner.setDaily_rate(Double.valueOf(daily_rate));
			miner.setInvestment_min(Double.valueOf(investment_min));
			miner.setInvestment_max(investment_max == null ? 0 : Double.valueOf(investment_max));
			miner.setOn_sale(on_sale);
			miner.setShow_daily_rate(Double.valueOf(show_daily_rate));
//			miner.setState(this.state);
			
			// 设置体验矿机日利率范围
			if(daily_rate_start != null && !daily_rate_start.isEmpty()) {
				miner.setDaily_rate_start(Double.valueOf(daily_rate_start));
			}
			if(daily_rate_end != null && !daily_rate_end.isEmpty()) {
				miner.setDaily_rate_end(Double.valueOf(daily_rate_end));
			}
			
			//基础参数
			miner.setAlgorithm(algorithm);
			miner.setComputing_power(Double.valueOf(computing_power));
			miner.setComputing_power_unit(computing_power_unit);
			miner.setPower(Double.valueOf(power));
			miner.setProduct_factory(product_factory);
			miner.setProduct_size(product_size);
			miner.setWeight(Double.valueOf(weight));
			miner.setWork_temperature_min(Double.valueOf(work_temperature_min));
			miner.setWork_temperature_max(Double.valueOf(work_temperature_max));
			miner.setWork_humidity_min(Double.valueOf(work_humidity_min));
			miner.setWork_humidity_max(Double.valueOf(work_humidity_max));
			miner.setInternet(internet);
			miner.setCycle_close(Integer.valueOf(cycle_close));
			miner.setBuy_currency(buyCurrency.toLowerCase());
			miner.setOutput_currency(outputCurrency.toLowerCase());
			
			this.minerService.updateA(miner);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);

			log.setUsername(username);
			log.setOperator(username);
			String ip = ServletUtil.getIp(request);
			log.setLog("手动修改矿机配置,ip:["+ip+"]");
			logService.save(log);

//			model.addObject("message", "操作成功");
//			model.setViewName("redirect:/" + action + "list.action");
			return Result.ok(miner);
		} catch (BusinessException e) {
//			model.addObject("error", e.getMessage());
//			model.setViewName("miner_update");
			return Result.failed(e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
//			model.addObject("error", "程序错误");
//			model.setViewName("miner_update");
			return Result.failed("程序错误");
//			return model;
		}
	}
	
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(User secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafePassword();

		String safeword_md5 = passwordEncoder.encode(loginSafeword);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

//	public String toDelete() {
//		try {
//
//			this.minerService.delete(this.id);
//			this.message = "操作成功";
//			return list();
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return list();
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			this.error = "程序错误";
//			return list();
//		}
//	}
}
