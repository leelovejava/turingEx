package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.TraderFollowUserListModel;
import com.yami.trading.admin.model.trader.TraderFollowUserModel;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.trader.AdminTraderFollowUserService;
import com.yami.trading.service.trader.AdminTraderService;
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
import java.util.HashMap;


@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易员跟随者管理")
public class AdminTraderFollowUserController {

	private static Log logger = LogFactory.getLog(AdminTraderFollowUserController.class);

	@Autowired
	private AdminTraderService adminTraderService;
	
	@Autowired
	private AdminTraderFollowUserService adminTraderFollowUserService;
	
	@Autowired
	private UserService userService;
	
	private final String action = "/normal/adminTraderFollowUser!";

	@RequestMapping(action + "list.action")
	public Result list(TraderFollowUserListModel model, Page page) {
		
		page = this.adminTraderFollowUserService.pagedQuery(page, model.getName_para(), model.getUsername_para());

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
	public Result add(@RequestBody TraderFollowUserModel model) {
		String usercode = model.getUsercode();
		String user_type = model.getUserType();
		String trader_usercode = model.getTraderUsercode();
		String username = model.getUsername();
		String follow_type = model.getFollowType();
		String stop_loss = model.getStopLoss();
		String stop_profit = model.getStopProfit();
		String symbol = model.getSymbol();
		String volume = model.getVolume();
		String profit = model.getProfit();
		String amount_sum = model.getAmountSum();
		String volume_max = model.getVolumeMax();
		
		try {

			String error = verification(usercode, trader_usercode, username, follow_type, stop_loss, stop_profit, symbol, volume, profit, amount_sum, volume_max);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			User party = userService.findUserByUserCode(usercode);
			if (party == null && "1".equals(user_type)) {
				error = "用户UID不存在!";
				throw new BusinessException(error);
			}
			User trader_party = userService.findUserByUserCode(trader_usercode);
			if (trader_party == null) {
				error = "交易员不存在!";
				throw new BusinessException(error);
			}
			Trader trader = adminTraderService.findByPartyId(trader_party.getUserId());
			if (trader == null) {
				error = "交易员不存在!";
				throw new BusinessException(error);
			}

			TraderFollowUser entity = new TraderFollowUser();
			entity.setUuid(ApplicationUtil.getCurrentTimeUUID());
			if (party == null) {
				entity.setPartyId("");
				entity.setUsername(username);
			} else {
				entity.setPartyId(party.getUserId());
				entity.setUsername(party.getUserName());
			}
			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数�?2，固定比�?
			 */

			entity.setFollowType(follow_type);
			entity.setStopLoss(Double.parseDouble(stop_loss));
			entity.setStopProfit(Double.parseDouble(stop_profit));
			entity.setSymbol(symbol);
			entity.setVolume(Double.parseDouble(volume));
			entity.setProfit(Double.parseDouble(profit));
			entity.setAmountSum(Double.parseDouble(amount_sum));

			entity.setVolumeMax(Double.parseDouble(volume_max));
			/**
			 * 状�?? 是否还在跟随状�?? 1,跟随�?2，取消跟�?
			 */
			entity.setState("1");

			this.adminTraderFollowUserService.save(entity, trader.getUuid());
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("error ", t);
			return Result.failed("1", t.getMessage());
		}
		return Result.succeed(null, "添加成功!");
	}

//	private String verificationUpdate() {
//
//		if (this.volume < 0)
//			return "跟单张数或比例不能小�?0";
//		if (this.volume_max < 0)
//			return "�?大持仓张数不能小�?0";
//		if (this.profit < 0)
//			return "累计跟单收益不能小于0";
//		if (this.amount_sum < 0)
//			return "累计跟单本金不能小于0";
//		return null;
//	}

	@RequestMapping(action + "toUpdate.action")
	public Result toUpdate(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		
		
		TraderFollowUser entity = adminTraderFollowUserService.findById(id);

		if(null == entity) {
			return Result.failed("记录不存在");
		}

		User trader = userService.findByUserId(entity.getTraderPartyId());
		User user = userService.findByUserId(entity.getPartyId());
		if(null != trader) {
			entity.setTraderUserCode(trader.getUserCode());
		}
		if(null != user) {
			entity.setUserCode(user.getUserCode());
		}
//		Party party = this.partyService.cachePartyBy(entity.getTrader_partyId().toString(), true);
//		this.trader_username = trader.getName();
//		this.trader_usercode = party.getUsercode();
//		this.username = entity.getUsername();
//
//		this.follow_type = entity.getFollow_type();
//		this.stop_loss = entity.getStop_loss();
//		this.stop_profit = entity.getStop_profit();
//		this.symbol = entity.getSymbol();
//		this.volume = entity.getVolume();
//		this.volume_max = entity.getVolume_max();
//		this.profit = entity.getProfit();
//		this.amount_sum = entity.getAmount_sum();
//
//		return "update";
		return Result.succeed(entity, "获取数据成功!");
	}

	@RequestMapping(action + "update.action")
	public Result update(@RequestBody TraderFollowUserModel model) {
		String id = model.getUuid();
		
		String usercode = model.getUsercode();
		String trader_usercode = model.getTraderUsercode();
		String username = model.getUsername();
		String stop_loss = model.getStopLoss();
		String stop_profit = model.getStopProfit();

		String follow_type = model.getFollowType();
		String symbol = model.getSymbol();
		String volume = model.getVolume();
		String profit = model.getProfit();
		String amount_sum = model.getAmountSum();
		String volume_max = model.getVolumeMax();
		
		try {
			String error = verification(usercode, trader_usercode, username, follow_type, stop_loss, stop_profit, symbol, volume, profit, amount_sum, volume_max);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			TraderFollowUser entity = adminTraderFollowUserService.findById(id);

			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数，固定比例
			 */
			entity.setUserCode(usercode);
			entity.setTraderUserCode(trader_usercode);
			entity.setUsername(username);
			entity.setStopProfit(Double.parseDouble(stop_profit));
			entity.setStopLoss(Double.parseDouble(stop_loss));
			entity.setFollowType(follow_type);
			entity.setSymbol(symbol);
			entity.setVolume(Double.parseDouble(volume));
			entity.setVolumeMax(Double.parseDouble(volume_max));
			entity.setProfit(Double.parseDouble(profit));
			entity.setAmountSum(Double.parseDouble(amount_sum));

			/**
			 * 状态 是否还在跟随状态 1,跟随，取消跟随
			 */
			entity.setState("1");

			this.adminTraderFollowUserService.update(entity);
			return Result.succeed("更新成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}

	@RequestMapping(action + "cancel.action")
	public Result cancel(HttpServletRequest request) {
		String id = request.getParameter("uuid");

		try {
			this.adminTraderFollowUserService.delete(id);
			return Result.succeed("操作成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}

	/**
	 * 取消跟随
	 * @param request
	 * @return
	 */
	@RequestMapping(action + "follow.action")
	public Result follow(HttpServletRequest request) {
		String id = request.getParameter("uuid");
		String state = request.getParameter("state");
		
		try {
			TraderFollowUser traderFollowUser = adminTraderFollowUserService.findById(id);
			traderFollowUser.setState(state);
			adminTraderFollowUserService.update(traderFollowUser);
			return Result.succeed("操作成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}
	
	private String verification(String usercode, String trader_usercode, String username, String follow_type, String stop_loss, String stop_profit, String symbol,
								String volume, String profit, String amount_sum, String volume_max) {
		if (StringUtils.isEmptyString(trader_usercode))
			return "请输入交易员UID";
		if (StringUtils.isEmptyString(usercode))
			return "请输入用户UID";

		if(!StringUtils.hasLength(stop_loss)) {
			return "止损百分率不能为空";
		}
		
		if(!StringUtils.isDouble(stop_loss)) {
			return "止损百分率输入不正确";
		}
		
		if(!StringUtils.hasLength(stop_profit)) {
			return "止盈百分率不能为空";
		}
		
		if(!StringUtils.isDouble(stop_profit)) {
			return "止盈百分率输入不正确";
		}
		
		if(!StringUtils.hasLength(volume)) {
			return "跟单张数或比例不能为空";
		}
		
		if(!StringUtils.isDouble(volume)) {
			return "跟单张数或比例输入不正确";
		}
		
		if (Double.parseDouble(volume) < 0)
			return "跟单张数或比例不能小于0";
		
		if(!StringUtils.hasLength(volume_max)) {
			return "最大持仓张数不能为空";
		}
		
		if(!StringUtils.isDouble(volume_max)) {
			return "最大持仓张数输入不正确";
		}
		if (Double.parseDouble(volume_max) < 0)
			return "最大持仓张数不能小于0";
		
		if(!StringUtils.hasLength(profit)) {
			return "累计跟单收益不能为空";
		}
		
		if(!StringUtils.isDouble(profit)) {
			return "累计跟单收益输入不正确";
		}
		if (Double.parseDouble(profit) < 0)
			return "累计跟单收益不能小于0";
		
		if(!StringUtils.hasLength(amount_sum)) {
			return "累计跟单本金不能为空";
		}
		
		if(!StringUtils.isDouble(amount_sum)) {
			return "累计跟单本金输入不正确";
		}
		if (Double.parseDouble(amount_sum) < 0)
			return "累计跟单本金不能小于0";

		return null;
	}

}
