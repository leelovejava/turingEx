package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.TraderUserListModel;
import com.yami.trading.admin.model.trader.TraderUserModel;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.TraderUser;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.trader.AdminTraderUserService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.HashMap;


@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易员用户管理")
public class AdminTraderUserController {
	private static Log logger = LogFactory.getLog(AdminTraderUserController.class);

	@Autowired
	private AdminTraderUserService adminTraderUserService;
	
	@Autowired
	private UserService userService;

//	/**
//	 * 查询参数
//	 */
//	private String name_para;
//
//	private String username_para;
//
//	private String rolename_para;
//
//	/**
//	 * 修改参数
//	 */
//
//	/**
//	 * 用户Uid
//	 */
//	private String usercode;
//
//	private String id;
//
//	private String name;
//	
//	private String error;
//	
//	/**
//	 * 累计金额
//	 */
//	private double amount_sum;
//
//	/**
//	 * 累计收益
//	 */
//	private double profit;
//
//	/**
//	 * 入驻时间----CREATE_TIME
//	 */
//	private Date create_time;

	private final String action = "/normal/adminTraderUser!";
	
	@RequestMapping(action + "list.action")
	public Result list(TraderUserListModel model, Page page) {

		page = this.adminTraderUserService.pagedQuery(page, model.getName_para(), model.getUsername_para());
//		for (Map<String, Object> data : (List<Map<String, Object>>) (page.getElements())) {
//			data.put("profit_ratio", Arith.mul(Double.parseDouble(data.get("profit_ratio").toString()), 100));
//			data.put("profit_share_ratio",
//					Arith.mul(Double.parseDouble(data.get("profit_share_ratio").toString()), 100));
//
//		}
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

	

	@RequestMapping(action + "add.action")
	public Result add(@RequestBody TraderUserModel model) {
		String usercode = model.getUsercode();
		
		String name = model.getName();
		String amount_sum = model.getAmountSum();
		String profit = model.getProfit();
		String create_time = model.getCreateTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {

			String error = verification(usercode, name, amount_sum, profit, create_time);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			User party = userService.findUserByUserCode(usercode);
			if (party == null) {
				error = "UID不存在!";
				throw new BusinessException(error);
			}

			TraderUser trader_user = new TraderUser();
			trader_user.setPartyId(party.getUserId());
			trader_user.setName(name);
			trader_user.setAmountSum(Double.parseDouble(amount_sum));
			trader_user.setProfit(Double.parseDouble(profit));
			trader_user.setCreateTime(sdf.parse(create_time));

			this.adminTraderUserService.save(trader_user);
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("AdminTraderUserAction.add error ", t);
			return Result.failed("1", t.getMessage());
		}
		return Result.succeed("添加成功!");
	}

//	private String verificationUpdate(String usercode, String name, String amount_sum, String profit, String create_time) {
//		if (create_time == null)
//			return "请输入入驻时间";
//		if (Double.parseDouble(amount_sum) < 0.0D)
//			return "累计金额不能小于0";
//		if (Double.parseDouble(profit) < 0.0D)
//			return "累计收益不能小于0";
//		if (StringUtils.isEmptyString(name))
//			return "请输入名称";
//
//		return null;
//	}

	
	@RequestMapping(action + "toUpdate.action")
	public Result toUpdate(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		
		TraderUser trader_user = adminTraderUserService.findById(id);
		User user = userService.findByUserId(trader_user.getPartyId());
		trader_user.setUserCode(user.getUserCode());

//		this.name = trader_user.getName();
//		this.amount_sum = trader_user.getAmount_sum();
//		this.profit = trader_user.getProfit();
//		this.create_time = trader_user.getCreate_time();
//
//		return "update";
		return Result.succeed(trader_user, "获取数据成功!");
	}

	
	@RequestMapping(action + "update.action")
	public Result update(@RequestBody TraderUserModel model) {
		String id = model.getUuid();
		String usercode = model.getUsercode();

		String name = model.getName();
		String amount_sum = model.getAmountSum();
		String profit = model.getProfit();
		String create_time = model.getCreateTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		TraderUser trader_user = this.adminTraderUserService.findById(id);
		try {
			String error = verification(usercode, name, amount_sum, profit, create_time);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			trader_user.setName(name);
			trader_user.setAmountSum(Double.parseDouble(amount_sum));
			trader_user.setProfit(Double.parseDouble(profit));
			trader_user.setCreateTime(sdf.parse(create_time));

			this.adminTraderUserService.update(trader_user);
			return Result.succeed( "更新成功!");
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

			this.adminTraderUserService.delete(id);
			return Result.succeed("删除成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}

	private String verification(String usercode, String name, String amount_sum, String profit, String create_time) {
		if (create_time == null)
			return "请输入入驻时间";
		if (Double.parseDouble(amount_sum) < 0.0D)
			return "累计金额不能小于0";
		if (Double.parseDouble(profit) < 0.0D)
			return "累计收益不能小于0";
		if (StringUtils.isEmptyString(name))
			return "请输入名称";

		return null;
	}

}
