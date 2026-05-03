package com.yami.trading.api.controller.trader;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderFollowSetting;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.bean.trader.domain.TraderInviteLink;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.trader.*;
import com.yami.trading.service.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;



/**
 * 交易员api接口
 */
@RestController
@CrossOrigin
@RequestMapping()
public class ApiTraderController {
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(ApiTraderController.class);

	@Autowired
	private TraderService traderService;

	@Autowired
	private TraderUserService traderUserService;

	@Autowired
	private TraderFollowUserService traderFollowUserService;

	@Autowired
	private TraderOrderService traderOrderService;

	@Autowired
	private ContractOrderService contractOrderService;

	@Autowired
	private UserService userService;

	@Autowired
	private TraderFollowSettingService traderFollowSettingService;

	@Autowired
	private TraderInviteLinkService traderInviteLinkService;

	/**
	 * 查询类型 orders 当前委托数 ，hisorders 历史委托数 ，user 跟随数
	 */
//	private String type;

	private final String action = "/api/trader!";

	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		String name = request.getParameter("name");
		String state = request.getParameter("state");
//		String orderBy_type = request.getParameter("orderBy_type");
		String page_no = request.getParameter("page_no");
		try {
			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (!StringUtils.isInteger(page_no)) {
// 页码不是整数
				throw new YamiShopBindException("Page number must be an integer");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
// 页码不能小于等于0
				throw new YamiShopBindException("Page number must be greater than 0");
			}
			Page<Trader> page = new Page<>(1, 1000000);

			data = traderService.getPaged(page, name, state);
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					Map<String, Object> map = data.get(i);
					String partyId = SecurityUtils.getCurrentUserId();
					if (partyId != null) {
						TraderFollowUser user = this.traderFollowUserService.findByPartyIdAndTrader_partyId(partyId, map.get("partyId").toString());
						if (user != null) {
							/**
							 * 1跟随 2未跟随
							 */
							map.put("follow_state", "1");
							map.remove("partyId");
						} else {
							map.put("follow_state", "2");
							map.remove("partyId");
						}

					} else {
						map.put("follow_state", "2");
						map.remove("partyId");
					}
				}

			}

			// List<Trader> data = traderService.findAllState_1();

//			resultObject.setData(bulidData(data));
			resultObject.setData(data);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;

	}

	/**
	 * 查询是否是交易员
	 * @param request
	 * @return
	 */
	@RequestMapping(action + "istrader.action")
	public Object istrader(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String id = request.getParameter("id");

		Trader data = null;
		if(StringUtils.isNotEmpty(id)) {
			data = traderService.findById(id);
		} else{
			String partyId = SecurityUtils.getCurrentUserId();
			data = traderService.findByPartyId(partyId);
		}
		if(null == data) {
			resultObject.setCode("1");
			resultObject.setData(false);
			return resultObject;
		}

		resultObject.setData(true);
		resultObject.setCode("0");
		return resultObject;
	}

	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		String symbol = request.getParameter("symbol");
		String page_no = request.getParameter("page_no");
		try {
			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (!StringUtils.isInteger(page_no)) {
// 页码不是整数
				throw new YamiShopBindException("Page number must be an integer");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
// 页码不能小于等于0
				throw new YamiShopBindException("Page number must be greater than 0");
			}
			Page<Trader> page = new Page<>(1, 1000000);
			Trader data = null;
			if(StringUtils.isNotEmpty(id)) {
				data = traderService.findById(id);
			} else{
				String partyId = SecurityUtils.getCurrentUserId();
				data = traderService.findByPartyId(partyId);
			}
			if(null == data) {
				resultObject.setCode("1");
				resultObject.setMsg("交易员不存在");
				return resultObject;
			}

			Map<String, Object> retData = bulidData(data, type, symbol, page);
			resultObject.setData(retData);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;
	}

	@RequestMapping(action + "generate.action")
	public Object generate(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String profit_share_ratio = request.getParameter("profit_share_ratio");
		String ids = request.getParameter("ids");
		String level = request.getParameter("level");

		if(StringUtils.isEmptyString(profit_share_ratio)) {
			resultObject.setCode("1");
			resultObject.setMsg("佣金分成比例不能为空");
			return resultObject;
		}

		String partyId = SecurityUtils.getCurrentUserId();

		TraderInviteLink traderInviteLink = traderInviteLinkService.findByTraderIdAndStatus(partyId, 1);
		if(null == traderInviteLink) {
			traderInviteLink = new TraderInviteLink();
			String uuid = ApplicationUtil.getCurrentTimeUUID();
			traderInviteLink.setUuid(uuid);
			traderInviteLink.setTraderId(partyId);
			traderInviteLink.setProfitShareRatio(Double.parseDouble(profit_share_ratio));
			traderInviteLink.setIds(ids);
			traderInviteLink.setLevel(Integer.parseInt(level));
			traderInviteLink.setStatus(1);
			String link = "https://sjiepc.com/syn/#/followUpStrategy/index?type=share&id=" + uuid;
			traderInviteLink.setLink(link);
			traderInviteLinkService.save(traderInviteLink);
			resultObject.setData(link);
		} else {
			resultObject.setCode("1");
			resultObject.setMsg("该用户已生成过邀请链接!");
			return resultObject;
		}
		resultObject.setCode("0");
		resultObject.setMsg("生成邀请链接成功!");

		return resultObject;
	}

	@RequestMapping(action + "getInviteLink.action")
	public Object getInviteLink(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();

		resultObject.setCode("0");
		resultObject.setMsg("获取数据成功!");

		TraderInviteLink traderInviteLink = traderInviteLinkService.findByTraderIdAndStatus(partyId, 1);
		if(null != traderInviteLink) {
			long remain = getRemainTime(traderInviteLink.getCreateTimeTs());
			if (remain <= 0) {
				traderInviteLink.setStatus(0);
				traderInviteLinkService.update(traderInviteLink);
			} else {
				resultObject.setData(traderInviteLink);
			}
		}

		return resultObject;
	}

	@RequestMapping(action + "follow.action")
	public Object follow(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String id = request.getParameter("id");
		String usercode = request.getParameter("usercode");
		TraderInviteLink traderInviteLink = traderInviteLinkService.selectById(id);

		long createTime = traderInviteLink.getCreateTimeTs();

		long remain = getRemainTime(createTime);

		if(0==traderInviteLink.getStatus() || remain <= 0) { // 链接已失效
			resultObject.setCode("1");
			resultObject.setMsg("跟单失败!");
			return resultObject;
		}

		User user = userService.findUserByUserCode(usercode);
		User trader = userService.findByUserId(traderInviteLink.getTraderId());

		TraderFollowUser traderFollowUser = new TraderFollowUser();
		traderFollowUser.setPartyId(user.getUserId());
		traderFollowUser.setUserCode(usercode);
		traderFollowUser.setUsername(user.getUserName());
		traderFollowUser.setTraderPartyId(trader.getUserId());
		traderFollowUser.setFollowType("1");
		traderFollowUser.setState("1");

		traderFollowUserService.save(traderFollowUser);

		resultObject.setCode("0");
		resultObject.setMsg("跟单成功!");
		return resultObject;
	}

	@RequestMapping(action + "expire.action")
	public Object expire(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String id = request.getParameter("id");
		TraderInviteLink traderInviteLink = traderInviteLinkService.selectById(id);
		long remain = 0L;
		resultObject.setCode("0");
		resultObject.setMsg("获取数据成功!");
		if(0==traderInviteLink.getStatus()) {
			resultObject.setData(remain);
			return resultObject;
		}
		long createTime = traderInviteLink.getCreateTimeTs();

		remain = getRemainTime(createTime);

		if(remain <= 0) { // 链接已失效
			traderInviteLink.setStatus(0);
			remain = 0L;
			traderInviteLinkService.update(traderInviteLink);
		}

		resultObject.setData(remain);
		return resultObject;
	}

	private long getRemainTime(long createTime) {
		long remain = 0L;
		Instant instant = Instant.now();
		long now = instant.getEpochSecond();
		remain = 3600 - (now - createTime);
		return remain;
	}

	@RequestMapping(action + "apply.action")
	public Object apply(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();

		String symbols = request.getParameter("symbols");
		String name = request.getParameter("name");

		String follow_volumn_min_param = request.getParameter("follow_volumn_min");
		int follow_volumn_min = StringUtils.isEmptyString(follow_volumn_min_param)?0:Integer.parseInt(follow_volumn_min_param);

		String state = "1";
		String img = request.getParameter("img");

		try {
			User party = userService.findByUserId(partyId);

			if (party == null) {
				throw new BusinessException("用户ID不存在!");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
				throw new BusinessException("试用用户无法添加!");
			}
			Trader exist = traderService.findByPartyId(partyId);
			if (exist != null) {
				throw new BusinessException("交易员已存在!");

			}
			Trader trader = new Trader();
			trader.setUuid(ApplicationUtil.getCurrentTimeUUID());
			trader.setPartyId(partyId);
			trader.setName(StringUtils.isEmptyString(name)?party.getUserName():name);
			trader.setSymbols(symbols);

			trader.setState(state);
			trader.setCreateTime(new Date());
			trader.setImg(img);
			trader.setFollowVolumnMin(follow_volumn_min);
			trader.setChecked(0);

			traderService.save(trader);



			resultObject.setCode("0");
			resultObject.setMsg("操作成功");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			resultObject.setCode("1");
			resultObject.setMsg(t.getMessage());
		}
		return resultObject;

	}

	/**
	 * 显示带单设置
	 * @param request
	 * @return
	 */
	@RequestMapping("showfollowsetting.action")
	public Object show_follow_setting(HttpServletRequest request){
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();

		TraderFollowSetting traderFollowSetting = traderFollowSettingService.findByPartyId(partyId);

		resultObject.setCode("0");
		resultObject.setMsg("设置成功");
		resultObject.setData(traderFollowSetting);
		return resultObject;
	}

	/**
	 * 跟单设置
	 * @param request
	 * @return
	 */
	@RequestMapping("followsetting.action")
	public Object follow_setting(HttpServletRequest request){
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();

		User user = userService.findByUserId(partyId);

		String days_setting= request.getParameter("days_setting");

		if(StringUtils.isEmptyString(days_setting)) {
			resultObject.setCode("1");
			resultObject.setMsg("借款天数不能为空");
			return resultObject;
		}

		String rate = request.getParameter("rate");

		if(StringUtils.isEmptyString(rate)) {
			resultObject.setCode("1");
			resultObject.setMsg("带单佣金比例不能为空");
			return resultObject;
		}

		if(!StringUtils.isDouble(rate)) {
			resultObject.setCode("1");
			resultObject.setMsg("带单佣金比例格式不正确");
			return resultObject;
		}

		if(null != traderFollowSettingService.findByPartyId(partyId)) {
			resultObject.setCode("1");
			resultObject.setMsg("带单设置已存在");
			return resultObject;
		}

		TraderFollowSetting traderFollowSetting = new TraderFollowSetting();
		traderFollowSetting.setPartyId(partyId);
		traderFollowSetting.setUsername(user.getUserName());
		traderFollowSetting.setDaysSetting(days_setting);
		traderFollowSetting.setRate(Double.parseDouble(rate));

		resultObject.setCode("0");
		resultObject.setMsg("设置成功");
		return resultObject;
	}

	/**
	 * 跟单设置
	 * @param request
	 * @return
	 */
	@RequestMapping("updatefollowsetting.action")
	public Object update_follow_setting(HttpServletRequest request){
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();

		String id = request.getParameter("id");

		String days_setting= request.getParameter("days_setting");

		if(StringUtils.isEmptyString(id)) {
			resultObject.setCode("1");
			resultObject.setMsg("更改记录ID不能为空");
			return resultObject;
		}

		if(StringUtils.isEmptyString(days_setting)) {
			resultObject.setCode("1");
			resultObject.setMsg("借款天数不能为空");
			return resultObject;
		}

		String rate = request.getParameter("rate");

		if(StringUtils.isEmptyString(rate)) {
			resultObject.setCode("1");
			resultObject.setMsg("带单佣金比例不能为空");
			return resultObject;
		}

		if(!StringUtils.isDouble(rate)) {
			resultObject.setCode("1");
			resultObject.setMsg("带单佣金比例格式不正确");
			return resultObject;
		}

		TraderFollowSetting traderFollowSetting = traderFollowSettingService.findById(id);

		if(null == traderFollowSetting) {
			resultObject.setCode("1");
			resultObject.setMsg("记录不存在");
			return resultObject;
		}


		traderFollowSetting.setDaysSetting(days_setting);
		traderFollowSetting.setRate(Double.parseDouble(rate));

		traderFollowSettingService.update(traderFollowSetting);

		resultObject.setCode("0");
		resultObject.setMsg("设置成功");
		return resultObject;
	}

	private String verification(String name, String img, String symbols, int order_profit, int deviation_order_profit, int order_loss,
								int deviation_order_loss, double week_3_order_amount, double deviation_week_3_order_amount, int week_3_order_profit,
								int deviation_week_3_order_profit, int week_3_order_sum, int deviation_week_3_order_sum, double order_amount, double deviation_order_amount,
								int follower_sum, int deviation_follower_sum, int follower_now, int deviation_follower_now, double profit_share_ratio, int follower_max, double follow_volumn_min) {
		if (StringUtils.isEmptyString(name))
			// 请输入交易员名称
			return "Please enter trader name";
		if (StringUtils.isEmptyString(img))
			// 请上传交易员头像
			return "Please upload trader avatar";

		if (StringUtils.isEmptyString(symbols))
			// 请输入带币品种
			return "Please enter trading symbol";
		if (Arith.add(order_profit, deviation_order_profit) < 0)
			// 累计盈利笔数加偏差值不能小于0
			return "Cumulative profit orders plus deviation cannot be less than 0";
		if (Arith.add(order_loss, deviation_order_loss) < 0)
			// 累计亏损笔数加偏差值不能小于0
			return "Cumulative loss orders plus deviation cannot be less than 0";
		if (Arith.add(week_3_order_amount, deviation_week_3_order_amount) < 0.0D)
			// 近3周累计金额加偏差值不能小于0
			return "Recent 3-week cumulative amount plus deviation cannot be less than 0";
		if (Arith.add(week_3_order_profit, deviation_week_3_order_profit) < 0)
			// 近3周盈利笔数加偏差值不能小于0
			return "Recent 3-week profit orders plus deviation cannot be less than 0";
		if (Arith.add(week_3_order_sum, deviation_week_3_order_sum) < 0)
			// 近3周交易笔数加偏差值不能小于0
			return "Recent 3-week trading orders plus deviation cannot be less than 0";
		if (Arith.add(order_amount, deviation_order_amount) < 0.0D)
			// 累计金额加偏差值不能小于0
			return "Cumulative amount plus deviation cannot be less than 0";
		if (Arith.add(follower_sum, deviation_follower_sum) < 0)
			// 累计跟随加偏差值人数不能小于0
			return "Cumulative followers plus deviation cannot be less than 0";
		if (Arith.add(follower_now, deviation_follower_now) < 0)
			// 当前跟随人数加偏差值不能小于0
			return "Current followers plus deviation cannot be less than 0";
		if (profit_share_ratio < 0.0D)
			// 利润分成比例不能小于0
			return "Profit sharing ratio cannot be less than 0";
		if (follower_max <= 0)
			// 此次跟单最多跟随人数不能小于等于0
			return "Maximum followers for this copy trading cannot be less than or equal to 0";
		if (follower_max < Arith.add(follower_now, deviation_follower_now))
			// 此次跟单最多跟随人数不能小于当前跟随人数加偏差值
			return "Maximum followers cannot be less than current followers plus deviation";
		if (follow_volumn_min < 0)
			// 最小跟单张数不能小于0
			return "Minimum copy volume cannot be less than 0";
		return null;
	}

	private Map<String, Object> bulidData(Trader entity, String type, String symbol, Page page) throws ParseException {
		DecimalFormat df2 = new DecimalFormat("#.##");
		List<Map<String, Object>> trader_order = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> trader_user = new ArrayList<Map<String, Object>>();
		/**
		 * 跟随用户
		 */
		if ("user".equals(type)) {
			trader_user = traderFollowUserService.getPaged(page, entity.getPartyId(), null);
//			if (trader_user != null) {
//				for (Map<String, Object> follow_user : trader_user) {
//					String username = follow_user.get("name").toString();
//					char[] username_char = username.toCharArray();
//					StringBuffer sb = new StringBuffer();
//					if (username_char.length > 6) {
//						for (int i = 0; i < username_char.length; i++) {
//							String aa = "";
//							if (i < 6) {
//								aa = String.valueOf(username_char[i]).replaceAll(String.valueOf(username_char[i]), "*");
//							} else {
//								aa = String.valueOf(username_char[i]);
//							}
//							sb.append(aa);
//						}
//					} else {
//						for (int i = 0; i < username_char.length; i++) {
//							String aa = "";
//							if (i < username_char.length) {
//								aa = String.valueOf(username_char[i]).replaceAll(String.valueOf(username_char[i]), "*");
//							} else {
//								aa = String.valueOf(username_char[i]);
//							}
//							sb.append(aa);
//						}
//					}
//					follow_user.put("name", sb);
//				}
//			}
		} else if("orders".equals(type)){
			/**
			 * 查询类型 orders 当前委托 ，hisorders 历史委托 ，user 跟随者
			 */
			trader_order = this.contractOrderService.getPaged(Long.valueOf(page.getCurrent()).intValue(), Long.valueOf(page.getSize()).intValue(), entity.getPartyId(), symbol, type, null);
		} else if("hisorders".equals(type)) {
			trader_order = this.traderOrderService.getPaged(page, entity.getPartyId());
		}
		if (trader_order != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map<String, Object> order : trader_order) {
				if (null != order.get("create_time")) {
					// 确认下数据库中提取出来的值类型是 string， 还是 Date TODO
					logger.info("------> ApiTraderController.bulidData 时间值类型为:" + order.get("create_time"));
					Date curCreateTime = DateUtils.toDate(sdf.format(order.get("create_time")), DateUtils.DF_yyyyMMddHHmmss);
					Date showCreateTime = DateTimeTools.transferToShowTime(curCreateTime);
					order.put("create_time", DateUtils.format(showCreateTime,"MM-dd HH:mm:ss"));
				} else {
					order.put("create_time", "");
				}

				if (order.get("close_time") != null && !"".equals(order.get("close_time"))) {
					Date curCloseTime = DateUtils.toDate(sdf.format(order.get("close_time")), DateUtils.DF_yyyyMMddHHmmss);
					Date showCloseTime = DateTimeTools.transferToShowTime(curCloseTime);
					order.put("close_time", DateUtils.format(showCloseTime,"MM-dd HH:mm:ss"));
				} else {
					order.put("close_time", "");
				}

				if (order.get("create_time_ts") != null
						&& StrUtil.isNotBlank(order.get("create_time_ts").toString())
						&& !Objects.equals(order.get("create_time_ts").toString(), "0")) {
					String oneValue = order.get("create_time_ts").toString();
					Long timestamp = Long.parseLong(oneValue);
					long showTimestamp = DateTimeTools.transferShowTimeToClientTime(timestamp);
					order.put("create_time_ts", String.valueOf(showTimestamp));
				}
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + entity.getImg();
		map.put("img", path);
		map.put("trader_order", trader_order);
		map.put("trader_user", trader_user);
		map.put("id", entity.getUuid());

		map.put("name", entity.getName());
		map.put("remarks", entity.getRemarks());
		/**
		 * 累计金额order_amount
		 */
		map.put("order_amount", df2.format(Arith.add(entity.getOrderAmount(), entity.getDeviationOrderAmount())));

//		map.put("symbol_name", "BTC/USDT;ETH/USDT");
		map.put("profit", df2.format(Arith.add(entity.getProfit(), entity.getDeviationProfit())));

		map.put("order_profit", (int) Arith.add(entity.getOrderProfit(), entity.getDeviationOrderProfit()));

		map.put("order_loss", (int) Arith.add(entity.getOrderLoss(), entity.getDeviationOrderLoss()));
		map.put("order_sum", (int) Arith.add(Arith.add(entity.getOrderProfit(), entity.getOrderLoss()),
				Arith.add(entity.getDeviationOrderProfit(), entity.getDeviationOrderLoss())));
		map.put("follower_sum", (int) Arith.add(entity.getFollowerSum(), entity.getDeviationFollowerSum()));

		map.put("follower_now", (int) Arith.add(entity.getFollowerNow(), entity.getDeviationFollowerNow()));
		/**
		 * 累计收益率
		 */
		map.put("profit_ratio", df2.format(Arith.add(Arith.mul(entity.getDeviationProfitRatio(), 100),
				Arith.mul(entity.getProfitRatio(), 100))));

		map.put("profit_share_ratio", Arith.mul(entity.getProfitShareRatio(), 100));
		map.put("follower_max", entity.getFollowerMax());
		Date date_now = new Date();// 取时单
		int days = daysBetween(entity.getCreateTime(), date_now);
		if (days < 0) {
			days = 0;
		}
		map.put("create_days", days);
		map.put("week_3_profit_ratio", Arith.add(entity.getWeek3ProfitRatio(), entity.getDeviationWeek3ProfitRatio()));
		map.put("week_3_profit", Arith.add(entity.getWeek3Profit(), entity.getDeviationWeek3Profit()));
		if(BigDecimal.valueOf(Arith.add(entity.getWeek3OrderSum(), entity.getDeviationWeek3OrderSum())).compareTo(BigDecimal.ZERO) > 0) {
			map.put("week_3_order_profit", Arith.div(Arith.add(entity.getWeek3OrderProfit(), entity.getDeviationWeek3OrderProfit()), Arith.add(entity.getWeek3OrderSum(), entity.getDeviationWeek3OrderSum())));
		} else {
			map.put("week_3_order_profit", "");
		}

		String partyId = SecurityUtils.getCurrentUserId();
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			session_token = sessionTokenService.savePut(partyId);
//			map.put("session_token", session_token);
//		}

		if (partyId != null) {
			TraderFollowUser user = this.traderFollowUserService
					.findByPartyIdAndTrader_partyId(partyId, entity.getPartyId());
			if (user != null) {

				map.put("follow_volume", user.getVolume());
				map.put("follow_volume_max", user.getVolumeMax());
				/**
				 * 跟单固定张数/固定比例---选择 1,固定张数，固定比例
				 */
				map.put("follow_type", user.getFollowType());
				map.put("follow_state", "1");
			} else {

				map.put("follow_state", "2");
			}

		} else {
			map.put("follow_state", "2");
			map.remove("partyId");
		}
		map.put("follow_volumn_min", entity.getFollowVolumnMin());
		return map;

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
