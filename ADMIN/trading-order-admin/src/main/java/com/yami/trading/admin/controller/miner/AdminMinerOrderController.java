package com.yami.trading.admin.controller.miner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.common.util.*;
import com.yami.trading.service.miner.job.MinerOrderProfitJob;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.quant.QuantPreIncome;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.security.common.util.SecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yami.trading.service.miner.service.AdminMinerOrderService;
import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.miner.service.MinerService;
import com.yami.trading.service.quant.service.QuantPreIncomeService;


/**
 * 管理后台-矿机订单页面
 *
 */
@RestController
@CrossOrigin
public class AdminMinerOrderController {

	private Logger logger = LogManager.getLogger(AdminMinerOrderController.class);
	
	@Autowired
	protected AdminMinerOrderService adminMinerOrderService;
	@Autowired
	protected MinerOrderService minerOrderService;
	@Autowired
	protected MinerService minerService;
	@Autowired
	protected MinerOrderProfitJob minerOrderProfitJob;

	@Autowired
	private PermissionFacade permissionFacade;
	@Autowired
	private QuantPreIncomeService quantPreIncomeService;
	
	protected Map<String, Object> session = new HashMap<>();
	
	private final String action = "normal/adminMinerOrderAction!";
	
	@RequestMapping(action + "list.action")
	public Result list(HttpServletRequest request) {

//		this.checkAndSetPageNo(request.getParameter("pageNo"));
		String name_para = request.getParameter("name_para");
		String miner_para = request.getParameter("miner_para");
		String status_para = request.getParameter("status_para");
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");

		int pageNo = Integer.parseInt(request.getParameter("current"));
		int pageSize = Integer.parseInt(request.getParameter("size"));

		List<String> children = permissionFacade.getOwnerUserIds();

		//&current=1&size=10
		
//		String partyId = getLoginPartyId();
		String partyId = null;//SecurityUtils.getCurrentSysUserId();
		List<Miner> findAll = this.minerService.findAll();
		Map<String, String> miner_name_map = new LinkedHashMap<String, String>();
		for (Miner miner : findAll) {
			miner_name_map.put(miner.getUuid(), miner.getName());
		}

		Page page = this.adminMinerOrderService.pagedQuery(pageNo, pageSize, name_para, miner_para,
				status_para, children, order_no_para, rolename_para);
//		ModelAndView model = new ModelAndView();
//		model.addObject("pageNo", pageNo);
//		model.addObject("pageSize", pageSize);
//		model.addObject("page", page);
//		model.addObject("name_para", name_para);
//		model.addObject("miner_para", miner_para);
//		model.addObject("status_para", status_para);
//		model.addObject("order_no_para", order_no_para);
//		model.addObject("rolename_para", rolename_para);
//
//		model.addObject("miner_name_map", miner_name_map);
//		model.addObject("message", message);
//		model.addObject("error", error);
//		model.setViewName("miner_order_list");
		return Result.ok(page);
	}

	/**
	 * 矿机订单收益分页
	 */
	@RequestMapping(action + "incomeList.action")
	public Result incomeList(HttpServletRequest request) {
		String quantOrderId = request.getParameter("quantOrderId");
		int pageNo = Integer.parseInt(request.getParameter("current"));
		int pageSize = Integer.parseInt(request.getParameter("size"));

		Page<QuantPreIncome> page = new Page<>(pageNo, pageSize);
		QueryWrapper<QuantPreIncome> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("quant_order_id", quantOrderId);
		queryWrapper.orderByDesc("start_time");
		Page<QuantPreIncome> result = quantPreIncomeService.page(page, queryWrapper);
		return Result.ok(result);
	}

	/**
	 * 后台赎回
	 */
	@RequestMapping(action + "close.action")
	public Result closOrder(HttpServletRequest request) {
		String message = "";
		String error = "";
		Map<String,Object> map = new HashMap<>();
		try {
			String order_no = request.getParameter("order_no");
			System.out.println("order_no => "+order_no);
			MinerOrder order = minerOrderService.findByOrder_no(order_no);
			System.out.println("order.getMiner_id() => "+order.getMiner_id());
			Miner miner = minerService.findById(order.getMiner_id());

			CloseDelayThread lockDelayThread = new CloseDelayThread(order_no, minerOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
			return Result.failed(error);
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
			return Result.failed(error);
		}
//		ModelAndView model = new ModelAndView();
//		model.addObject("message", message);
//		model.addObject("error", error);
//		model.setViewName("redirect:/" + action + "list.action");
		return Result.succeed(map);
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 *
	 */
	public class CloseDelayThread implements Runnable {
		private String orderNo;
		private MinerOrderService minerOrderService;

		public void run() {
			try {
				while (true) {
					/**
					 * 提前赎回理财产品需要支付违约金
					 */
					MinerOrder order = minerOrderService.findByOrder_no(orderNo);

					Miner miner = minerService.findById(order.getMiner_id());
					
					// 体验矿机不支持提前赎回
					if ("Y".equals(miner.getTest())) {
						throw new BusinessException("体验矿机不支持提前赎回");
					}

					Date date_now = new Date();// 取时间
					double last_days = daysBetween(order.getCreate_time(), date_now);
					if ("1".equals(order.getState()) && last_days >= miner.getCycle_close()) {
						/**
						 * 扣除违约金
						 */
						double default_money = 0d;// 不计违约金
						order.setState("2");
						order.setProfit(Arith.sub(order.getProfit(), default_money));
						this.minerOrderService.saveClose(order);
					}
					/**
					 * 处理完退出
					 */
					break;
				}

			} catch (BusinessException e) {
				logger.error("error:", e);
			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public CloseDelayThread(String orderNo, MinerOrderService minerOrderService) {
			this.orderNo = orderNo;
			this.minerOrderService = minerOrderService;
		}

	}

	@RequestMapping(action + "toAddOrder.action")
	public Result toAddOrder(HttpServletRequest request) {
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);

		List<Miner> findAll = this.minerService.findAll();
		Map<String, String> miner_name_map = new LinkedHashMap<String, String>();
		List<Miner> miner_list = new LinkedList<Miner>();
		for (Miner miner : findAll) {
			miner_name_map.put(miner.getUuid(), miner.getName());
			miner_list.add(miner);
		}
		Map<String,Object> map = new HashMap<>();
		map.put("miner_name_map", miner_name_map);
		map.put("miner_list", miner_list);
		map.put("session_token", session_token);
		return Result.ok(map);
	}

	@RequestMapping(action + "addOrder.action")
	public Result addOrder(HttpServletRequest request) {

		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			String para_uid = request.getParameter("para_uid");
			// 购买金额
			String para_amount = request.getParameter("para_amount");
			// 矿机id
			String para_minerid = request.getParameter("para_minerid");
			
//			Object object = this.session.get("session_token");
//			this.session.remove("session_token");
//			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
//					|| (!session_token.equals((String) object))) {
//				return Result.failed("token");
//			}

			error = verifyAddOrder(para_uid, para_amount, para_minerid);
			if (!StringUtils.isNullOrEmpty(error)) {
//				model.addObject("error", error);
//				model.setViewName("miner_order_add");
				return Result.failed(error);
			}

			Object object = new Object();
			synchronized (object) {
				String username = SecurityUtils.getSysUser().getUsername();
				adminMinerOrderService.addOrder(para_uid, Double.valueOf(para_amount), para_minerid, username);
				ThreadUtils.sleep(100);
			}
//			model.addObject("message", "操作成功");
//			model.setViewName("redirect:/" + action + "list.action");
			return Result.ok("操作成功");
		} catch (BusinessException e) {
//			model.addObject("error", e.getMessage());
			return Result.failed(e.getMessage());
		} catch (Exception e) {
			logger.error("error ", e);
//			model.addObject("error", "程序错误");
//			model.setViewName("miner_order_add");
			return Result.failed("程序错误");
		}
	}

	protected String verifyAddOrder(String para_uid, String para_amount, String para_minerid) {
		if (StringUtils.isEmptyString(para_uid)) {
			return "请输入用户uid";
		}
			
		if (StringUtils.isNullOrEmpty(para_amount) 
				|| !StringUtils.isDouble(para_amount) 
				|| Double.valueOf(para_amount)< 0) {
			return "购买金额不能小于0";
		}
		
		if (StringUtils.isEmptyString(para_minerid)) {
			return "请选择要购买的矿机";
		}
		return null;
	}
	
	/**
	 * 修改单条收益记录
	 */
	@RequestMapping(action + "updateIncome.action")
	public Result updateIncome(HttpServletRequest request) {
		String id = request.getParameter("id");
		String income = request.getParameter("income");
		try {
			if (StringUtils.isEmptyString(id) || StringUtils.isEmptyString(income)) {
				return Result.failed("参数错误");
			}
			QuantPreIncome record = quantPreIncomeService.getById(Integer.parseInt(id));
			if (record == null) {
				return Result.failed("记录不存在");
			}
			record.setIncome(Double.parseDouble(income));
			quantPreIncomeService.updateById(record);
			return Result.ok("操作成功");
		} catch (Exception e) {
			logger.error("updateIncome error", e);
			return Result.failed("程序错误");
		}
	}

	@RequestMapping(action + "addProfit.action")
	public Result addProfit(HttpServletRequest request) {
		String message = "";
		String error = "";
		String system_time = request.getParameter("system_time");
		try {
			String username = SecurityUtils.getSysUser().getUsername();
			if (!"root".equals(username) && !"admin".equals(username)) {
				throw new BusinessException("权限不足");
			}
			if (StringUtils.isEmptyString(system_time)) {
				throw new BusinessException("请填入系统时间");
			}

			Date newTime = DateTimeTools.readQueryTime(system_time, DateUtils.NORMAL_DATE_FORMAT, null);
			//JobDelayThread thread = new JobDelayThread(DateUtils.toDate(system_time, DateUtils.NORMAL_DATE_FORMAT), minerOrderProfitJob);
			JobDelayThread thread = new JobDelayThread(newTime, minerOrderProfitJob);
			Thread t = new Thread(thread);
			t.start();
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
//			System.out.println("error = " + error);
			return Result.failed(e.getMessage());
		} catch (Exception e) {
			logger.error("error ", e);
//			System.out.println("error " + e);
			error = "程序错误";
			return Result.failed(e.getMessage());
		}
//		ModelAndView model = new ModelAndView();
		Map<String,Object> map = new HashMap<>();
		map.put("message", message);
		map.put("error", error);
		return Result.ok(map);
	}

	
	public class JobDelayThread implements Runnable {
		private MinerOrderProfitJob minerOrderProfitJob;
		private Date systemTime;
		public void run() {
			minerOrderProfitJob.handleData(systemTime);
		}

		public JobDelayThread(Date systemTime, MinerOrderProfitJob minerOrderProfitJob) {
			this.systemTime = systemTime;
			this.minerOrderProfitJob = minerOrderProfitJob;
		}

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

}
