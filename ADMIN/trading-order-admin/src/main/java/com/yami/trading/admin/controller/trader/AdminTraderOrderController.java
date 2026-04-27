package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.TraderOrderListModel;
import com.yami.trading.admin.model.trader.TraderOrderModel;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderOrder;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.trader.AdminTraderOrderService;
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
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易员订单管理")
public class AdminTraderOrderController {
	private static Log logger = LogFactory.getLog(AdminTraderOrderController.class);
	
	Map<String,String> item_map = new HashMap<String,String>();

	@Autowired
	private AdminTraderService adminTraderService;
	
	@Autowired
	private AdminTraderOrderService adminTraderOrderService;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private UserService userService;
	
	private final String action = "/normal/adminTraderOrder!";

	@RequestMapping(action + "list.action")
	public Result list(TraderOrderListModel model, Page page) {

		page = this.adminTraderOrderService.pagedQuery(page, model.getName_para(), model.getUsername_para(), model.getRolename_para());

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
	public Result add(@RequestBody TraderOrderModel model) {
		String usercode = model.getUsercode();
		
		String symbol = model.getSymbol();
		String profit = model.getProfit();
		String change_ratio = model.getChangeRatio();
		String close_avg_price = model.getCloseAvgPrice();
		String trade_avg_price = model.getTradeAvgPrice();
		String close_time = model.getCloseTime();
		String create_time = model.getCreateTime();
		String direction = model.getDirection();
		String lever_rate = model.getLeverRate();
		String state = model.getState();
		String volume_open = model.getVolumeOpen();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {

			String error = verification(usercode, symbol, profit, change_ratio, close_avg_price, trade_avg_price, close_time, create_time, direction, lever_rate, state, volume_open);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			User party = userService.findUserByUserCode(usercode);
			if (party == null ) {
				error = "交易员UID不存在!";
				throw new BusinessException(error);
			}
			
			Trader trader = this.adminTraderService.findByPartyId(party.getUserId());
			if (trader == null) {
				error = "交易员不存在!";
				throw new BusinessException(error);
			}

			TraderOrder entity = new TraderOrder();
			entity.setPartyId(party.getUserId());
		
			entity.setSymbol(symbol);
			entity.setProfit(Double.parseDouble(profit));
			entity.setChangeRatio(Double.parseDouble(change_ratio));
			entity.setCloseAvgPrice(Double.parseDouble(close_avg_price));
			entity.setTradeAvgPrice(Double.parseDouble(trade_avg_price));
			entity.setCloseTime(sdf.parse(close_time));
			entity.setCreateTime(sdf.parse(create_time));
			entity.setDirection(direction);
			entity.setLeverRate(Double.parseDouble(lever_rate));
			entity.setProfit(Double.parseDouble(profit));
			entity.setState(state);
			entity.setVolumeOpen(Double.parseDouble(volume_open));
			

			this.adminTraderOrderService.save(entity);
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("error ", t);
			return Result.failed("1", t.getMessage());
		}
		return Result.succeed("添加成功!", "0");
	}

//	private String verificationUpdate() {
//		if (StringUtils.isEmptyString(this.usercode))
//			return "请输入交易员UID";
//		if (StringUtils.isEmptyString(this.symbol))
//			return "请输入品�?";
//		if (this.create_time == null)
//			return "请输入开仓时�?";
//		if (this.close_time == null)
//			return "请输入平仓时�?";
//
//		if (this.lever_rate <= 0 || this.lever_rate %1 != 0)
//			return "杠杆倍数不能小于等于0,并且不能有小�?";
//		if (this.volume_open <= 0 || this.volume_open %1 != 0)
//			return "委托数量不能小于等于0,并且不能有小�?";
//		if (this.trade_avg_price < 0)
//			return "买入价格不能小于0";
//		if (this.close_avg_price < 0)
//			return "平常价格不能小于0";
//		return null;
//	}

	@RequestMapping(action + "toUpdate.action")
	public Result toUpdate(HttpServletRequest request) {

		String id = request.getParameter("uuid");
		
		TraderOrder entity = adminTraderOrderService.findById(id);
		User user = userService.findByUserId(entity.getPartyId());
		entity.setUsercode(user.getUserCode());
//		User party = userService.cacheUserBy(entity.getPartyId().toString());
		
//		this.usercode = party.getUsercode();
//		
//		this.symbol = entity.getSymbol();
//		this.profit = entity.getProfit();
//		this.change_ratio = entity.getChange_ratio();
//		this.close_avg_price = entity.getClose_avg_price();
//		this.trade_avg_price = entity.getTrade_avg_price();
//		this.close_time = entity.getClose_time();
//		this.create_time = entity.getCreate_time();
//		this.direction = entity.getDirection();
//		this.lever_rate = entity.getLever_rate();
//		this.profit = entity.getProfit();
//		this.state = entity.getState();
//		this.volume_open = entity.getVolume_open();
//
//		return "update";
		return Result.succeed(entity, "获取数据成功!");
	}

	@RequestMapping(action + "update.action")
	public Result update(@RequestBody TraderOrderModel model) {
		String id = model.getUuid();
		String usercode = model.getUsercode();

		String symbol = model.getSymbol();
		String profit = model.getProfit();
		String change_ratio = model.getChangeRatio();
		String close_avg_price = model.getCloseAvgPrice();
		String trade_avg_price = model.getTradeAvgPrice();
		String close_time = model.getCloseTime();
		String create_time = model.getCreateTime();
		String direction = model.getDirection();
		String lever_rate = model.getLeverRate();
		String state = model.getState();
		String volume_open = model.getVolumeOpen();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String error = verification(usercode, symbol, profit, change_ratio, close_avg_price, trade_avg_price, close_time, create_time, direction, lever_rate, state, volume_open);
			if (!StringUtils.isNullOrEmpty(error)) throw new BusinessException(error);

			TraderOrder entity = adminTraderOrderService.findById(id);
			
			
			entity.setSymbol(symbol);
			entity.setProfit(Double.parseDouble(profit));
			entity.setChangeRatio(Double.parseDouble(change_ratio));
			entity.setCloseAvgPrice(Double.parseDouble(close_avg_price));
			entity.setTradeAvgPrice(Double.parseDouble(trade_avg_price));
			entity.setCloseTime(sdf.parse(close_time));
			entity.setCreateTime(sdf.parse(create_time));
			entity.setDirection(direction);
			entity.setLeverRate(Double.parseDouble(lever_rate));
			entity.setProfit(Double.parseDouble(profit));
			entity.setState(state);
			entity.setVolumeOpen(Double.parseDouble(volume_open));

			this.adminTraderOrderService.update(entity);
			return Result.succeed("更新成功!", "0");
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

			this.adminTraderOrderService.delete(id);
			return Result.succeed("删除成功!");
		} catch (BusinessException e) {
			return Result.failed("1", e.getMessage());
		} catch (Throwable t) {
			logger.error("update error ", t);
			return Result.failed("1", t.getMessage());
		}
	}

	
	private String verification(String usercode, String symbol, String profit, String change_ratio, String close_avg_price, String trade_avg_price, 
			String close_time, String create_time, String direction, String lever_rate, String state, String volume_open) {
		
		if (StringUtils.isEmptyString(usercode))
			return "请输入交易员UID";
		if (StringUtils.isEmptyString(symbol))
			return "请输入品种";
		if (create_time == null)
			return "请输入开仓时间";
		if (close_time == null)
			return "请输入平仓时间";

		if (Double.parseDouble(lever_rate) <= 0 || Double.parseDouble(lever_rate) %1 != 0)
			return "杠杆倍数不能小于等于0,并且不能有小数";
		if (Double.parseDouble(volume_open) <= 0 || Double.parseDouble(volume_open) %1 != 0)
			return "委托数量不能小于等于0,并且不能有数";
		if (Double.parseDouble(trade_avg_price) < 0)
			return "买入价格不能小于0";
		if (Double.parseDouble(close_avg_price) < 0)
			return "平常价格不能小于0";

		return null;
	}


	public Map<String, String> getItem_map() {
		List<Item> items = this.itemService.cacheGetAll();
		if(items != null) {
			for(Item item :items) {
				this.item_map.put(item.getSymbol().toString(), item.getName().toString());
				
			}
		}
		return item_map;
	}

	public void setItem_map(Map<String, String> item_map) {
		this.item_map = item_map;
	}
	
	
}
