package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.TraderListModel;
import com.yami.trading.admin.model.trader.TraderModel;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.trader.AdminTraderService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易员管理")
public class AdminTraderController {

	private static Log logger = LogFactory.getLog(AdminTraderController.class);

	@Autowired
	private AdminTraderService adminTraderService;
	
	@Autowired
	private UserService userService;
	
	private final String action = "/normal/adminTrader!";

	@RequestMapping(action + "list.action")
	public Result list(TraderListModel model, Page page) {
		
		page = this.adminTraderService.pagedQuery(page, model.getName_para(), model.getUsername_para());
		
		for (Map<String, Object> data : (List<Map<String, Object>>) (page.getRecords())) {
			data.put("profit_ratio", Arith.mul(Arith.add(Double.parseDouble(data.get("profit_ratio").toString()),
					Double.parseDouble(data.get("deviation_profit_ratio").toString())), 100));
			data.put("profit_share_ratio",
					Arith.mul(Double.parseDouble(data.get("profit_share_ratio").toString()), 100));
			data.put("follower_now", Arith.add(Double.parseDouble(data.get("follower_now").toString()),
					Double.parseDouble(data.get("deviation_follower_now").toString())));
			data.put("follower_sum", Arith.add(Double.parseDouble(data.get("follower_sum").toString()),
					Double.parseDouble(data.get("deviation_follower_sum").toString())));
			data.put("profit", Arith.add(Double.parseDouble(data.get("profit").toString()),
					Double.parseDouble(data.get("deviation_profit").toString())));
		}
		
		HashMap<String,Object> resultDict=new HashMap<String,Object>();
		resultDict.put("pageSize", page.getSize());
		resultDict.put("pageNo", page.getCurrent());
		resultDict.put("page", page);
		
		return Result.succeed(resultDict, "获取数据成功");
	}

//	@RequestMapping(action + "toAdd.action")
//	public String toAdd() {
//		return "add";
//	}

	

	@PostMapping(action + "add.action")
	public Result add(@RequestBody TraderModel model) {
		String name = model.getName();
		String img = model.getImg();
		String create_time = model.getCreateTime();
		String symbols = model.getSymbols();
		String usercode = model.getUsercode();

		int order_profit = model.getOrderProfit();

		int deviation_order_profit = model.getDeviationOrderProfit();
		

		int order_loss = model.getOrderLoss();

		int deviation_order_loss = model.getDeviationOrderLoss();

		double week_3_order_amount = model.getWeek3OrderAmount();

		double deviation_week_3_order_amount = model.getDeviationWeek3OrderAmount();

		int week_3_order_profit = model.getWeek3OrderProfit();

		int deviation_week_3_order_profit = model.getDeviationWeek3OrderProfit();

		int week_3_order_sum = model.getWeek3OrderSum();
		

		int deviation_week_3_order_sum = model.getDeviationWeek3OrderSum();
		

		double order_amount = model.getOrderAmount();

		double deviation_order_amount = model.getDeviationOrderAmount();

		int follower_sum = model.getFollowerSum();

		int deviation_follower_sum = model.getDeviationFollowerSum();

		int follower_now = model.getFollowerNow();

		int deviation_follower_now = model.getDeviationFollowerNow();

		double profit_share_ratio = model.getProfitShareRatio();

		int follower_max = model.getFollowerMax();

		int follow_volumn_min = model.getFollowVolumnMin();

		double week_3_profit = model.getWeek3Profit();

		double week_3_profit_ratio = model.getWeek3ProfitRatio();

		double profit = model.getProfit();

		double profit_ratio = model.getProfitRatio();

		double deviation_week_3_profit = model.getDeviationWeek3Profit();

		double deviation_week_3_profit_ratio = model.getDeviationWeek3ProfitRatio();

		double deviation_profit = model.getDeviationProfit();

		double deviation_profit_ratio = model.getDeviationProfitRatio();
		
		String state = model.getState();
		
		String remarks = model.getRemarks();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {

			String error = verification(name, img, create_time, symbols, order_profit, deviation_order_profit, order_loss
					, deviation_order_loss, week_3_order_amount, deviation_week_3_order_amount, week_3_order_profit, deviation_week_3_order_profit,
					week_3_order_sum, deviation_week_3_order_sum, order_amount, deviation_order_amount, follower_sum, deviation_follower_sum,
					follower_now, deviation_follower_now, profit_share_ratio, follower_max, follow_volumn_min);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			User party = userService.findUserByUserCode(usercode);
			if (party == null) {
				error = "UID不存在!";
				throw new BusinessException(error);
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
				error = "试用用户无法添加";
				throw new BusinessException(error);
			}
			if (adminTraderService.findByPartyId(party.getUserId()) != null) {
				error = "交易员已存在!";
				throw new BusinessException(error);
			}
			Trader trader = new Trader();
			trader.setUuid(ApplicationUtil.getCurrentTimeUUID());
			trader.setPartyId(party.getUserId());
			trader.setName(name);
			trader.setRemarks(remarks);
			trader.setSymbols(symbols);
			/**
			 * 统计数据
			 */
			trader.setWeek3Profit(week_3_profit);
			trader.setWeek3OrderAmount(week_3_order_amount);
			trader.setWeek3ProfitRatio(Arith.div(week_3_profit_ratio, 100));
			trader.setWeek3OrderProfit(week_3_order_profit);
			trader.setWeek3OrderSum(week_3_order_sum);
			trader.setOrderAmount(order_amount);
			trader.setProfit(profit);
			trader.setProfitRatio(Arith.div(profit_ratio, 100));
			trader.setOrderProfit(order_profit);
			trader.setOrderLoss(order_loss);
			trader.setOrderSum((int) Arith.add(order_loss, order_profit));
			trader.setFollowerSum(follower_sum);
			trader.setFollowerNow(follower_now);
			/**
			 * 偏差值Deviation_w
			 */
			trader.setDeviationWeek3Profit(deviation_week_3_profit);
			trader.setDeviationWeek3OrderAmount(deviation_week_3_order_amount);
			trader.setDeviationWeek3ProfitRatio(Arith.div(deviation_week_3_profit_ratio, 100));
			trader.setDeviationWeek3OrderProfit(deviation_week_3_order_profit);
			trader.setDeviationWeek3OrderSum(deviation_week_3_order_sum);
			trader.setDeviationOrderAmount(deviation_order_amount);
			trader.setDeviationProfit(deviation_profit);
			trader.setDeviationProfitRatio(Arith.div(deviation_profit_ratio, 100));
			trader.setDeviationOrderProfit(deviation_order_profit);
			trader.setDeviationOrderLoss(deviation_order_loss);
			trader.setDeviationOrderSum((int) Arith.add(deviation_order_loss, deviation_order_profit));
			trader.setDeviationFollowerSum(deviation_follower_sum);
			trader.setDeviationFollowerNow(deviation_follower_now);

			trader.setProfitShareRatio(Arith.div(profit_share_ratio, 100));
			trader.setState(state);
			trader.setFollowerMax(follower_max);
			trader.setCreateTime(sdf.parse(create_time));
			trader.setImg(img);
			trader.setFollowVolumnMin(follow_volumn_min);
			trader.setChecked(1);

			adminTraderService.save(trader);
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			return Result.failed("1", t.getMessage());
		}
		return Result.succeed(null, "添加成功!");
	}

//	private String verificationUpdate() {
//		if (StringUtils.isEmptyString(this.name))
//			return "请输入交易员名称";
//		if (StringUtils.isEmptyString(this.img))
//			return "请上传交易员头像";
//		if (this.create_time == null)
//			return "请输入入驻时�?";
////			if (StringUtils.isEmptyString(this.remarks))
////				return "请输入交易员�?�?";
//		if (StringUtils.isEmptyString(this.symbols))
//			return "请输入带币品�?";
//		if (this.profit_share_ratio < 0.0D)
//			return "利润分成比例不能小于0";
//
//		if (this.follower_max <= 0)
//			return "此次跟单�?多跟随人数不能小于等�?0";
//		if (StringUtils.isEmptyString(this.img))
//			return "请上传头�?";
//
////			if (Arith.add(this.profit,this.deviation_profit) < 0.0D) 
////			return "累计收益加偏差�?�不能小�?0";
////		if (Arith.add(this.profit_ratio,this.deviation_profit_ratio) < 0.0D) 
////			return "累计收益率加偏差值不能小�?0";
//		if (Arith.add(this.order_profit, this.deviation_order_profit) < 0)
//			return "累计盈利笔数加偏差�?�不能小�?0";
//		if (Arith.add(this.order_loss, this.deviation_order_loss) < 0)
//			return "累计亏损笔数加偏差�?�不能小�?0";
////		if (Arith.add(this.week_3_profit,this.deviation_week_3_profit) < 0.0D) 
////			return "�?3周收益加偏差值不能小�?0";
//		if (Arith.add(this.week_3_order_amount, this.deviation_week_3_order_amount) < 0.0D)
//			return "�?3周累计金额加偏差值不能小�?0";
////		if (Arith.add(this.week_3_profit_ratio,this.deviation_week_3_profit_ratio) < 0.0D) 
////			return "�?3周收益率加偏差�?�不能小�?0";
//		if (Arith.add(this.week_3_order_profit, this.deviation_week_3_order_profit) < 0)
//			return "�?3周盈利笔数加偏差值不能小�?0";
//		if (Arith.add(this.week_3_order_sum, this.deviation_week_3_order_sum) < 0)
//			return "�?3周交易笔数加偏差值不能小�?0";
//		if (Arith.add(this.order_amount, this.deviation_order_amount) < 0.0D)
//			return "累计金额加偏差�?�不能小�?0";
//		if (Arith.add(this.follower_sum, this.deviation_follower_sum) < 0)
//			return "累计跟随加偏差�?�人数不能小�?0";
//
//		if (Arith.add(this.follower_now, this.deviation_follower_now) < 0)
//			return "当前跟随人数加偏差�?�不能小�?0";
//
//		if (this.follower_max < Arith.add(this.follower_now, this.deviation_follower_now))
//			return "此次跟单�?多跟随人数不能小于当前跟随人数加偏差�?";
//
////			if (this.daily_rate < 0.0D) {
////				return "日利率不能小�?0";
////			}
////			
//		if (this.follow_volumn_min < 0)
//			return "�?小跟单张数不能小�?0";
//		return null;
//	}

	@RequestMapping(action + "toUpdate.action")
	public Result toUpdate(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		
		Trader trader = adminTraderService.findById(id);

		User party = userService.findByUserId(trader.getPartyId());
		if(party!=null) {
			trader.setUserCode(party.getUserCode());
		}
		trader.setProfitRatio(Arith.mul(trader.getProfitRatio(), 100));
		trader.setProfitShareRatio(Arith.mul(trader.getProfitShareRatio(), 100));
		trader.setWeek3ProfitRatio(Arith.mul(trader.getWeek3ProfitRatio(), 100));
		trader.setDeviationProfitRatio(Arith.mul(trader.getDeviationWeek3ProfitRatio(), 100));
		trader.setDeviationProfitRatio(Arith.mul(trader.getDeviationProfitRatio(), 100));
		String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + trader.getImg();
		trader.setPath(path);


//		this.name = trader.getName();
//		this.remarks = trader.getRemarks();
//		this.symbols = trader.getSymbols();
//		this.profit = trader.getProfit();
//		this.profit_ratio = Arith.mul(trader.getProfit_ratio(), 100);
//		this.order_profit = trader.getOrder_profit();
//		this.order_loss = trader.getOrder_loss();
//
//		this.follower_sum = trader.getFollower_sum();
//		this.profit_share_ratio = Arith.mul(trader.getProfit_share_ratio(), 100);
//		this.follower_max = trader.getFollower_max();
//		this.follower_now = trader.getFollower_now();
//
//		this.week_3_profit = trader.getWeek_3_profit();
//		this.week_3_order_amount = trader.getWeek_3_order_amount();
//		this.week_3_profit_ratio = Arith.mul(trader.getWeek_3_profit_ratio(), 100);
//		this.week_3_order_profit = trader.getWeek_3_order_profit();
//		this.week_3_order_sum = trader.getWeek_3_order_sum();
//		this.order_amount = trader.getOrder_amount();
//
//		/**
//		 * 偏差值Deviation_w
//		 */
//		this.deviation_week_3_profit = trader.getDeviation_week_3_profit();
//		this.deviation_week_3_order_amount = trader.getDeviation_week_3_order_amount();
//		this.deviation_week_3_profit_ratio = Arith.mul(trader.getDeviation_week_3_profit_ratio(), 100);
//		this.deviation_week_3_order_profit = trader.getDeviation_week_3_order_profit();
//		this.deviation_week_3_order_sum = trader.getDeviation_week_3_order_sum();
//		this.deviation_order_amount = trader.getDeviation_order_amount();
//		this.deviation_profit = trader.getDeviation_profit();
//		this.deviation_profit_ratio = Arith.mul(trader.getDeviation_profit_ratio(), 100);
//		this.deviation_order_profit = trader.getDeviation_order_profit();
//		this.deviation_order_loss = trader.getDeviation_order_loss();
//		this.deviation_follower_sum = trader.getDeviation_follower_sum();
//		this.deviation_follower_now = trader.getDeviation_follower_now();
//
//		this.img = trader.getImg();
//		this.state = trader.getState();
//		this.create_time = DateUtils.toDate(trader.getCreate_time().toString(), DateUtils.DF_yyyyMMdd);
//		this.follow_volumn_min = trader.getFollow_volumn_min();

		return Result.succeed(trader, "获取数据成功!");
	}

	@PostMapping(action + "update.action")
	public Result update(@RequestBody TraderModel model) {
		String id = model.getUuid();

		String name = model.getName();
		String img = model.getImg();
		String create_time = model.getCreateTime();
		String symbols = model.getSymbols();
		String usercode = model.getUsercode();

		int order_profit = model.getOrderProfit();

		int deviation_order_profit = model.getDeviationOrderProfit();


		int order_loss = model.getOrderLoss();

		int deviation_order_loss = model.getDeviationOrderLoss();

		double week_3_order_amount = model.getWeek3OrderAmount();

		double deviation_week_3_order_amount = model.getDeviationWeek3OrderAmount();

		int week_3_order_profit = model.getWeek3OrderProfit();

		int deviation_week_3_order_profit = model.getDeviationWeek3OrderProfit();

		int week_3_order_sum = model.getWeek3OrderSum();

		int deviation_week_3_order_sum = model.getDeviationWeek3OrderSum();

		double order_amount = model.getOrderAmount();

		double deviation_order_amount = model.getDeviationOrderAmount();

		int follower_sum = model.getFollowerSum();

		int deviation_follower_sum = model.getDeviationFollowerSum();

		int follower_now = model.getFollowerNow();

		int deviation_follower_now = model.getDeviationFollowerNow();

		double profit_share_ratio = model.getProfitShareRatio();

		int follower_max = model.getFollowerMax();

		int follow_volumn_min = model.getFollowVolumnMin();

		double week_3_profit = model.getWeek3Profit();

		double week_3_profit_ratio = model.getWeek3ProfitRatio();

		double profit = model.getProfit();

		double profit_ratio = model.getProfitRatio();

		double deviation_week_3_profit = model.getDeviationWeek3Profit();

		double deviation_week_3_profit_ratio = model.getDeviationWeek3ProfitRatio();

		double deviation_profit = model.getDeviationProfit();

		double deviation_profit_ratio = model.getDeviationProfitRatio();

		String state = model.getState();

		String remarks = model.getRemarks();
		
		Trader trader = adminTraderService.findById(id);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			String error = verification(name, img, create_time, symbols, order_profit, deviation_order_profit, order_loss
					, deviation_order_loss, week_3_order_amount, deviation_week_3_order_amount, week_3_order_profit, deviation_week_3_order_profit,
					week_3_order_sum, deviation_week_3_order_sum, order_amount, deviation_order_amount, follower_sum, deviation_follower_sum,
					follower_now, deviation_follower_now, profit_share_ratio, follower_max, follow_volumn_min);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			trader.setName(name);
			trader.setRemarks(remarks);
			trader.setSymbols(symbols);
			/**
			 * 统计数据
			 */
			trader.setWeek3Profit(week_3_profit);
			trader.setWeek3OrderAmount(week_3_order_amount);
			trader.setWeek3ProfitRatio(Arith.div(week_3_profit_ratio, 100));
			trader.setWeek3OrderProfit(week_3_order_profit);
			trader.setWeek3OrderSum(week_3_order_sum);
			trader.setOrderAmount(order_amount);
			trader.setProfit(profit);
			trader.setProfitRatio(Arith.div(profit_ratio, 100));
			trader.setOrderProfit(order_profit);
			trader.setOrderLoss(order_loss);
			trader.setOrderSum((int) Arith.add(order_loss, order_profit));
			trader.setFollowerSum(follower_sum);
			trader.setFollowerNow(follower_now);

			/**
			 * 偏差值Deviation_w
			 */
			trader.setDeviationWeek3Profit(deviation_week_3_profit);
			trader.setDeviationWeek3OrderAmount(deviation_week_3_order_amount);
			trader.setDeviationWeek3ProfitRatio(Arith.div(deviation_week_3_profit_ratio, 100));
			trader.setDeviationWeek3OrderProfit(deviation_week_3_order_profit);
			trader.setDeviationWeek3OrderSum(deviation_week_3_order_sum);
			trader.setDeviationOrderAmount(deviation_order_amount);
			trader.setDeviationProfit(deviation_profit);
			trader.setDeviationProfitRatio(Arith.div(deviation_profit_ratio, 100));
			trader.setDeviationOrderProfit(deviation_order_profit);
			trader.setDeviationOrderLoss(deviation_order_loss);
			trader.setDeviationOrderSum((int) Arith.add(deviation_order_loss, deviation_order_profit));
			trader.setDeviationFollowerSum(deviation_follower_sum);
			trader.setDeviationFollowerNow(deviation_follower_now);

			trader.setState(state);
			trader.setFollowerMax(follower_max);
			trader.setProfitShareRatio(Arith.div(profit_share_ratio, 100));
			trader.setCreateTime(sdf.parse(create_time));
			trader.setImg(img);
			trader.setFollowVolumnMin(follow_volumn_min);

			adminTraderService.update(trader);
			return Result.succeed(null,"更新成功!");
		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return "update";
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
//			logger.error("update error ", t);
//			this.error = "程序错误";
//			return "update";
			return Result.failed("1", t.getMessage());
		}
	}

	@RequestMapping(action + "check.action")
	public Result check(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		String check = request.getParameter("check");
		try {
			if(StringUtils.isEmptyString(check)) {
				return Result.failed("1","审核参数不能为空");
			}
			Trader trader = adminTraderService.findById(id);
			if(null == trader) {
				return Result.failed("1","该记录不存在");
			}
			if(Integer.parseInt(check) == trader.getChecked()) {
				return Result.failed("1","该记录审核状跟提交状态一致");
			}
            trader.setChecked(Integer.parseInt(check));
			adminTraderService.update(trader);
			return Result.succeed(null,"审核成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}

	@RequestMapping(action + "delete.action")
	public Result toDelete(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		
		try {
			Trader trader = adminTraderService.findById(id);
			if(null == trader) {
				return Result.failed("1", "交易员不存在");
			}
			adminTraderService.delete(id);
			return Result.succeed(null,"删除成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}
	
	private String verification(String name, String img, String create_time, String symbols, int order_profit, int deviation_order_profit, int order_loss
			, int deviation_order_loss, double week_3_order_amount, double deviation_week_3_order_amount, double week_3_order_profit, double deviation_week_3_order_profit,
			int week_3_order_sum, int deviation_week_3_order_sum, double order_amount, double deviation_order_amount, int follower_sum, int deviation_follower_sum,
			int follower_now, int deviation_follower_now, double profit_share_ratio, int follower_max, int follow_volumn_min) {
		
		
		if (StringUtils.isEmptyString(name))
			return "请输入交易员名称";
		if (StringUtils.isEmptyString(img))
			return "请上传交易员头像";
		if (create_time == null)
			return "请输入入驻时间";
//		if (StringUtils.isEmptyString(this.remarks))
//			return "请输入交易员简介";
		if (StringUtils.isEmptyString(symbols))
			return "请输入带币品种";
//		if (Arith.add(this.profit,this.deviation_profit) < 0.0D) 
//			return "累计收益加偏差值不能小于0";
//		if (Arith.add(this.profit_ratio,this.deviation_profit_ratio) < 0.0D) 
//			return "累计收益率加偏差值不能小于0";
		if (Arith.add(order_profit, deviation_order_profit) < 0)
			return "累计盈利笔数加偏差值不能小于0";
		if (Arith.add(order_loss, deviation_order_loss) < 0)
			return "累计亏损笔数加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit,this.deviation_week_3_profit) < 0.0D) 
//			return "近3周收益加偏差值不能小于0";
		if (Arith.add(week_3_order_amount, deviation_week_3_order_amount) < 0.0D)
			return "近3周累计金额加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit_ratio,this.deviation_week_3_profit_ratio) < 0.0D) 
//			return "近3周收益率加偏差值不能小于0";
		if (Arith.add(week_3_order_profit, deviation_week_3_order_profit) < 0)
			return "近3周盈利笔数加偏差值不能小于0";
		if (Arith.add(week_3_order_sum, deviation_week_3_order_sum) < 0)
			return "近3周交易笔数加偏差值不能小于0";
		if (Arith.add(order_amount, deviation_order_amount) < 0.0D)
			return "累计金额加偏差值不能小于0";
		if (Arith.add(follower_sum, deviation_follower_sum) < 0)
			return "累计跟随加偏差值人数不能小于0";
		if (Arith.add(follower_now, deviation_follower_now) < 0)
			return "当前跟随人数加偏差值不能小于0";
		if (profit_share_ratio < 0.0D)
			return "利润分成比例不能小于0";
		if (follower_max <= 0)
			return "此次跟单最多跟随人数不能小于等于0";
		if (StringUtils.isEmptyString(img))
			return "请上传头像";
		if (follower_max < Arith.add(follower_now, deviation_follower_now))
			return "此次跟单最多跟随人数不能小于当前跟随人数加偏差值";
		if (follow_volumn_min < 0)
			return "最小跟单张数不能小于0";
		return null;
	}

}
