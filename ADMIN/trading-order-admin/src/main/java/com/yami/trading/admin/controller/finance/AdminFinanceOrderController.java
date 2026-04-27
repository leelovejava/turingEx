package com.yami.trading.admin.controller.finance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.service.finance.job.FinanceOrder1DayJob;
import com.yami.trading.service.finance.service.AdminFinanceOrderService;
import com.yami.trading.service.finance.service.FinanceOrderLock;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.finance.service.FinanceService;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;

import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
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
 * 理财产品订单
 */
@RestController
@CrossOrigin
public class AdminFinanceOrderController {

	private Logger logger = LogManager.getLogger(AdminFinanceOrderController.class);

	@Autowired
	protected AdminFinanceOrderService adminFinanceOrderService;
	@Autowired
	protected FinanceOrderService financeOrderService;
	@Autowired
	protected FinanceService financeService;
	@Autowired
	protected FinanceOrder1DayJob financeOrder1DayJob;
	@Autowired
	protected LogService logService;
	@Autowired
	protected UserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	private PermissionFacade permissionFacade;

	private final String action = "normal/adminFinanceOrderAction!";

	/**
	 * 获取 理财产品订单 列表
	 */
	@RequestMapping(action + "list.action")
	public Result list(HttpServletRequest request) {
		String pageNo = request.getParameter("current");
		String pageSize = request.getParameter("size");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String finance_para = request.getParameter("finance_para");
		String status_para = request.getParameter("status_para");
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");
		String closeTime = request.getParameter("closeTime");
		String createTime = request.getParameter("createTime");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("finance_order_list");

		List<String> children = permissionFacade.getOwnerUserIds();

		String partyId = "";
		Page page = null;
		try {

			if("3".equals(status_para)){
				status_para = null;
			}

//			this.checkAndSetPageNo(pageNo);



//			String partyId = this.getLoginPartyId();

			page = this.adminFinanceOrderService.pagedQuery(Integer.parseInt(pageNo), Integer.parseInt(pageSize), name_para, finance_para,
					status_para, children, order_no_para, rolename_para,createTime ,closeTime);

			List<Map> list = page.getRecords();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
			return Result.failed(e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
//			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return Result.failed("程序错误");
		}

		Map<String,Object> map = new HashMap<>();

		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		map.put("page", page);
		map.put("message", message);
		map.put("error", error);
		map.put("name_para", name_para);
		map.put("finance_para", finance_para);
		map.put("status_para", status_para);
		map.put("order_no_para", order_no_para);
		map.put("rolename_para", rolename_para);
		return Result.ok(map);
	}
	
	/**
	 * 后台赎回
	 */
	@RequestMapping(action + "close.action")
	public Result close(HttpServletRequest request) {
		String id = request.getParameter("id");

//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {

			FinanceOrder order = this.financeOrderService.findById(id);
			
			CloseDelayThread lockDelayThread = new CloseDelayThread(id, order.getOrderNo(), this.financeService, this.financeOrderService);
			
			Thread t = new Thread(lockDelayThread);
			t.start();

			User sec_user = this.secUserService.findUserByUserCode(order.getPartyId().toString());

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(sec_user.getUserName());
			log.setUserId(sec_user.getUserId());
			log.setOperator(SecurityUtils.getSysUser().getUsername());
			log.setLog("手动赎回理财订单,订单号：[" + order.getOrderNo() + "],ip:[" + this.getIp() + "]");
			logService.save(log);

		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
			return Result.failed(e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
//			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//			return modelAndView;
			return Result.failed(t.getMessage());
		}

//		modelAndView.addObject("message", "操作成功");
		return Result.ok("操作成功");
	}
	
	/**
	 * 利息重计
	 */
	@RequestMapping(action + "addProfit.action")
	public Result addProfit(HttpServletRequest request) {
		String system_time = request.getParameter("system_time");

//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			String username = SecurityUtils.getSysUser().getUsername();
			if (!"root".equals(username) && !"admin".equals(username)) {
				throw new BusinessException("权限不足");
			}
			
			if (StringUtils.isEmpty(system_time)) {
				throw new BusinessException("请填入系统时间");
			}

			Date localTime = DateTimeTools.readQueryTime(system_time, DateUtils.NORMAL_DATE_FORMAT, null);
			//JobDelayThread thread = new JobDelayThread(DateUtils.toDate(system_time, DateUtils.NORMAL_DATE_FORMAT), this.financeOrder1DayJob);
			JobDelayThread thread = new JobDelayThread(localTime, this.financeOrder1DayJob);

			Thread t = new Thread(thread);
			t.start();

		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
			return Result.failed(e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
//			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//			return modelAndView;
			return Result.failed(t.getMessage());
		}

//		modelAndView.addObject("message", "操作成功");
//		return modelAndView;
		return Result.ok("操作成功");
	}

	public static int daysBetween(Date smdate, Date bdate) throws ParseException {		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));		
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 */
	public class CloseDelayThread implements Runnable {
		private String id;
		private String order_no;
		private FinanceService financeService;
		private FinanceOrderService financeOrderService;

		public void run() {
			
			try {
				
				while (true) {
					
					if (FinanceOrderLock.add(order_no)) {
						
						// 提前赎回理财产品需要支付违约金
						FinanceOrder order = this.financeOrderService.findById(id);
						Finance finance = this.financeService.findById(order.getFinanceId());
						
						if ("1".equals(order.getState())) {
							
							// 取时间
							Date date_now = new Date();
							
							// 扣除违约金
							double last_days = daysBetween(date_now, order.getStopTime());
							if (last_days <= 0) {
								last_days = 1;
							}
							
							double default_ratio = Arith.mul(finance.getDefaultRatio(), 0.01);
							default_ratio = Arith.mul(default_ratio, last_days);
							double breach_amount = Arith.mul(order.getAmount(), default_ratio);
							order.setProfit(Arith.sub(0, breach_amount));
							order.setState("2");

							this.financeOrderService.saveClose(order);
						}
						
						// 处理完退出
						break;
					}
					
					ThreadUtils.sleep(500);
				}

			} catch (Exception e) {
				logger.error("error:", e);
			} finally {
				FinanceOrderLock.remove(order_no);
			}
		}

		public CloseDelayThread(String id, String order_no, FinanceService financeService, FinanceOrderService financeOrderService) {
			this.id = id;
			this.order_no = order_no;
			this.financeService = financeService;
			this.financeOrderService = financeOrderService;
		}
		
	}

	public class JobDelayThread implements Runnable {
		private FinanceOrder1DayJob financeOrder1DayJob;
		private Date systemTime;

		public void run() {
			this.financeOrder1DayJob.handleData(systemTime);
		}

		public JobDelayThread(Date systemTime, FinanceOrder1DayJob financeOrder1DayJob) {
			this.systemTime = systemTime;
			this.financeOrder1DayJob = financeOrder1DayJob;
		}

	}

	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String ip = request.getHeader("X-Forwarded-For");
		if (com.yami.trading.common.util.StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (com.yami.trading.common.util.StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

}
