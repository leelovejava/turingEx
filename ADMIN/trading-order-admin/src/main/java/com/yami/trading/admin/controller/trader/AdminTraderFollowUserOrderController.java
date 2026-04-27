package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.TraderFollowUserOrderListModel;
import com.yami.trading.common.domain.Result;
import com.yami.trading.service.trader.AdminTraderFollowUserOrderService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;


@RestController
@CrossOrigin
@RequestMapping()
@Api(tags = "交易员跟随者订单管理")
public class AdminTraderFollowUserOrderController {

	private static Log logger = LogFactory.getLog(AdminTraderFollowUserOrderController.class);

	@Autowired
	private AdminTraderFollowUserOrderService adminTraderFollowUserOrderService;
	
	@Autowired
	private UserService userService;

//	/**
//	 * 查询参数 交易员名�?
//	 */
//	private String name_para;
//	/**
//	 * 用户�?
//	 */
//	private String username_para;
//	
//	private String rolename_para;
//
//	/**
//	 * 添加用户类型 '1':'真实用户','2':'虚假用户'
//	 */
//	private String user_type;
//	
//	private String username;
//
//	private String id;

	private final String action = "/normal/adminTraderFollowUserOrder!";

	@RequestMapping(action + "list.action")
	public Result list(TraderFollowUserOrderListModel model, Page page) {
		
		page = adminTraderFollowUserOrderService.pagedQuery(page, model.getName_para(), model.getUsername_para(), model.getRolename_para());

		HashMap<String,Object> resultDict=new HashMap<String,Object>();
		resultDict.put("pageSize", page.getSize());
		resultDict.put("pageNo", page.getCurrent());
		resultDict.put("page", page);
		
		return Result.succeed(resultDict, "获取数据成功");
	}

}
