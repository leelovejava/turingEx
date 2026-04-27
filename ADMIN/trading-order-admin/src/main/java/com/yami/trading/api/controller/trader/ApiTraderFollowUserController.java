package com.yami.trading.api.controller.trader;

import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.trader.TraderFollowUserService;
import com.yami.trading.service.trader.TraderService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户准备跟随交易员api接口
 */
@RestController
@CrossOrigin
@RequestMapping()
public class ApiTraderFollowUserController {
	private static final long serialVersionUID = 623416500874018208L;
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(ApiTraderFollowUserController.class);
	
	@Autowired
	private TraderFollowUserService traderFollowUserService;
	
	@Autowired
	private TraderService traderService;

	@Autowired
	private UserService userService;

//	private String trader_id;
//
//	private String trader_name;
//
//	/**
//	 * 跟随购买品种 symbol
//	 */
//	private String symbol;
//	/**
//	 * 跟单固定张数/固定比例---选择 1,固定张数�?2，固定比�?
//	 */
//	private String follow_type;
//
//	/**
//	 * 跟单张数或比�?---具体�?
//	 */
//	private double volume;
//	/**
//	 * �?大持仓张�?
//	 */
//	private double volume_max;
//	/**
//	 * 止盈百分�?
//	 */
//	private double stop_profit;
//	/**
//	 * 止损百分�?
//	 */
//	private double stop_loss;
	
	private final String action = "/api/traderFollowUser!";

	/**
	 * 用户跟随交易员
	 */
	@RequestMapping(action + "save.action")
	public Object saveCreate(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String follow_type = request.getParameter("follow_type");
		String stop_loss = request.getParameter("stop_loss");
		String stop_profit = request.getParameter("stop_profit");
		String symbol = request.getParameter("symbol");
		String volume = request.getParameter("volume");
		String volume_max = request.getParameter("volume_max");
		String trader_id = request.getParameter("trader_id");

		String partyId = SecurityUtils.getCurrentUserId();
		try {

//			Object object = this.sessionTokenService.cacheGet(session_token);
//			this.sessionTokenService.del(session_token);
//			if ((object == null) || (!this.getLoginPartyId().equals((String) object))) {
//				resultObject.setCode("1");
//				resultObject.setMsg("请稍后再试");
//				return resultObject;
//			}

			User user = userService.getById(partyId);

//			if (!party.getKyc_authority()) {
//				resultObject.setCode("401");
//				resultObject.setMsg(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
			TraderFollowUser entity = new TraderFollowUser();
			entity.setPartyId(partyId);
			entity.setUsername(user.getUserName());
			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数量，固定比例
			 */
			entity.setFollowType(follow_type);
			entity.setStopLoss(Double.parseDouble(stop_loss));
			entity.setStopProfit(Double.parseDouble(stop_profit));
			entity.setSymbol(symbol);
			entity.setVolume(Double.parseDouble(volume));
			entity.setVolumeMax(Double.parseDouble(volume_max));
			/**
			 * 状态 是否还在跟随状随 1,跟随，取消跟随
			 */
			entity.setState("1");

			this.traderFollowUserService.save(entity, trader_id);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode(e.getSign() + "");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {

		}

		return resultObject;
	}

	@RequestMapping(action + "choose.action")
	public Object chooseDays(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String daysSetting = request.getParameter("daysSetting");
		String partyId = SecurityUtils.getCurrentUserId();
		User user = userService.findByUserId(partyId);
		user.setDaysSetting(daysSetting);

		userService.updateById(user);

		resultObject.setCode("0");
		resultObject.setMsg("设置成功!");

		return resultObject;
	}

	@RequestMapping(action + "changeFollow.action")
	public Object changeFollow(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String follow_type = request.getParameter("follow_type");
		String stop_loss = request.getParameter("stop_loss");
		String stop_profit = request.getParameter("stop_profit");
		String symbol = request.getParameter("symbol");
		String volume = request.getParameter("volume");
		String volume_max = request.getParameter("volume_max");
		String trader_id = request.getParameter("trader_id");

		String partyId = SecurityUtils.getCurrentUserId();
		try {

//			Object object = this.sessionTokenService.cacheGet(session_token);
//			this.sessionTokenService.del(session_token);
//			if ((object == null) || (!this.getLoginPartyId().equals((String) object))) {
//				resultObject.setCode("1");
//				resultObject.setMsg("请稍后再试");
//				return resultObject;
//			}

			User user = userService.getById(partyId);
//			if (!party.getKyc_authority()) {
//				resultObject.setCode("401");
//				resultObject.setMsg(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
			Trader trader = this.traderService.findById(trader_id);
			TraderFollowUser entity = this.traderFollowUserService.findByPartyIdAndTrader_partyId(partyId,
					trader.getPartyId());
			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数�?2，固定比�?
			 */
			entity.setFollowType(follow_type);
			entity.setStopLoss(Double.parseDouble(stop_loss));
			entity.setStopProfit(Double.parseDouble(stop_profit));
			entity.setSymbol(symbol);
			entity.setVolume(Double.parseDouble(volume));
			entity.setVolumeMax(Double.parseDouble(volume_max));
			/**
			 * 状�?? 是否还在跟随状�?? 1,跟随�?2，取消跟�?
			 */
			entity.setState("1");

			this.traderFollowUserService.update(entity);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode(e.getSign() + "");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {

		}

		return resultObject;
	}

	/**
	 * 取消跟随
	 */
	@RequestMapping(action + "cancelFollow.action")
	public Object cancelFollow(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String trader_id = request.getParameter("trader_id");

		String partyId = SecurityUtils.getCurrentUserId();
		try {
			Trader trader = traderService.findById(trader_id);
			TraderFollowUser traderFollowUser = this.traderFollowUserService.findByPartyIdAndTrader_partyId(partyId,
					trader.getPartyId().toString());
			if (traderFollowUser != null) {
				this.traderFollowUserService.deleteCancel(traderFollowUser.getUuid());
			}

			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode(e.getSign() + "");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {

		}

		return resultObject;
	}

}
