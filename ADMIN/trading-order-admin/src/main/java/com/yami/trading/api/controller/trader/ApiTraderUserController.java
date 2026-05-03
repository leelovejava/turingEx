package com.yami.trading.api.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.bean.trader.domain.TraderUser;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.trader.TraderFollowUserOrderService;
import com.yami.trading.service.trader.TraderFollowUserService;
import com.yami.trading.service.trader.TraderService;
import com.yami.trading.service.trader.TraderUserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户api接口，累计数据
 */
@RestController
@CrossOrigin
@RequestMapping()
public class ApiTraderUserController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3217562997540384508L;
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(ApiTraderUserController.class);
	
	@Autowired
	private TraderService traderService;
	
	@Autowired
	private TraderUserService traderUserService;
	
	@Autowired
	private TraderFollowUserService traderFollowUserService;
	
	@Autowired
	private TraderFollowUserOrderService traderFollowUserOrderService;
	
	@Autowired
	private ContractOrderService contractOrderService;
	
//	private String id;
//	private String orderBy_type;
//	private String symbol;

	@Autowired
	private ItemService itemService;

	/**
	 * 查询类型 orders 我的跟单 ，trader 我的交易
	 */
//	private String type;
	/**
	 * 我的跟单查询类型： orders 当前委托，hisorders 历史委托
	 */
//	private String type_order;

//	private String name;

//	private int page_no;
	
	private final String action = "/api/traderUser!";

	@RequestMapping(action + "home.action")
	public Object home() {
		ResultObject resultObject = new ResultObject();
		String partyId = SecurityUtils.getCurrentUserId();
		Map<String, Object> map = new HashMap<>();
		int followNumber = 0;
		BigDecimal followAmount = BigDecimal.ZERO;
		BigDecimal followProfit = BigDecimal.ZERO;
		double totalProfit = 0d;
		// 正在进行中的跟单
		List<ContractOrder> contractOrderServiceList = contractOrderService.selectContractOrderByUserIdAndFollowAndState(partyId, ContractOrder.ORDER_FOLLOW, ContractOrder.STATE_SUBMITTED);
		TraderUser traderUser = traderUserService.saveTraderUserByPartyId(partyId);
		if(null != contractOrderServiceList) {
			followNumber = contractOrderServiceList.size(); // 跟单中的订单数
			followAmount = contractOrderServiceList.stream().map(ContractOrder::getDeposit).reduce(BigDecimal.ZERO, BigDecimal::add); // 跟单USDT
			followProfit = contractOrderServiceList.stream().map(ContractOrder::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		if(null != traderUser) {
			totalProfit = traderUser.getProfit();
		}
		map.put("followNumber", followNumber);
		map.put("followAmount", followAmount);
		map.put("followProfit", followProfit);
		map.put("totalProfit", totalProfit);
		resultObject.setCode("0");
		resultObject.setMsg("请求成功");
		resultObject.setData(map);
		return resultObject;
	}

	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String type = request.getParameter("type");
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
			Page<T> page = new Page<>(1, 1000000);

			String partyId = SecurityUtils.getCurrentUserId();

			TraderUser data = traderUserService.saveTraderUserByPartyId(partyId);

			resultObject.setData(bulidData(data, type, page));
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

	private Map<String, Object> bulidData(TraderUser entity, String type, Page page) throws ParseException {

		List<Map<String, Object>> trader_order = new ArrayList<Map<String, Object>>();
		List<TraderFollowUser> follow_users = new ArrayList<TraderFollowUser>();

		List<Map<String, Object>> follow_traders = new ArrayList<Map<String, Object>>();
		follow_users = traderFollowUserService.findByPartyId(entity.getPartyId());
		double folllow_trader_num = 0;
		if (follow_users != null) {
			folllow_trader_num = follow_users.size();
		}

		/**
		 * 跟随的交易员
		 */
		if ("trader".equals(type)) {
			if (follow_users != null) {
				for (TraderFollowUser user : follow_users) {
					Trader trader = traderService.findByPartyId(user.getTraderPartyId());
					Item item = itemService.findBySymbol(trader.getSymbols());
					Map<String, Object> follow_trader = new HashMap<String, Object>();
					follow_trader.put("profit", user.getProfit());
					follow_trader.put("profitRation", BigDecimal.valueOf(user.getProfit()).divide(BigDecimal.valueOf(user.getAmountSum()), RoundingMode.HALF_UP));
					follow_trader.put("amountSum", user.getAmountSum());
					follow_trader.put("username", trader.getName());
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + trader.getImg();
					follow_trader.put("img", path);
					follow_trader.put("id", trader.getUuid());
					follow_trader.put("followState", "1");
					follow_trader.put("followType", user.getFollowType());
					follow_trader.put("volume", user.getVolume());
					follow_trader.put("volumeMax", user.getVolumeMax());
					follow_trader.put("followNow", trader.getFollowerNow());
					follow_trader.put("followMax", trader.getFollowerMax());
					follow_trader.put("symbols", item.getName());
					follow_traders.add(follow_trader);
				}
			}
		}
		if ("orders".equals(type)) {
			trader_order = this.traderFollowUserOrderService.getPaged(page, entity.getPartyId(), ContractOrder.STATE_SUBMITTED);
		} else if("hisorders".equals(type)) {
			trader_order = this.traderFollowUserOrderService.getPaged(page, entity.getPartyId(), ContractOrder.STATE_CREATED);
		}

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("orders", trader_order);
		map.put("traders", follow_traders);
		map.put("folllow_trader_num", folllow_trader_num);

		map.put("id", entity.getUuid());

		map.put("name", entity.getName());
		map.put("profit", entity.getProfit());
		map.put("amount_sum", entity.getAmountSum());
		Date date_now = new Date();// 取时间
		int days = daysBetween(entity.getCreateTime(), date_now);
		if (days < 0) {
			days = 0;
		}
		map.put("create_days", days);

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
