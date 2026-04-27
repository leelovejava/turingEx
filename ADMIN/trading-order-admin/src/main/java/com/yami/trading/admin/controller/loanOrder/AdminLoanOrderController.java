package com.yami.trading.admin.controller.loanOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 质押借币 后台页面
 *
 */
@RestController
@CrossOrigin
public class AdminLoanOrderController {

	private Logger logger = LoggerFactory.getLogger(AdminLoanOrderController.class);
	
	@Autowired
	LoanOrderService loanOrderService;

	@Autowired
	LoanRelationOrderService loanRelationOrderService;

	@Autowired
	PermissionFacade permissionFacade;
	
	private final String action = "/normal/adminLoanOrder!";
	
	/**
	 * 质押借币订单
	 */
	@RequestMapping(action + "list.action")
	public Result list(HttpServletRequest request) {
		String pageNoStr = request.getParameter("current");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String orderNo = request.getParameter("orderNo");
		String roleName = request.getParameter("roleName");
		String userParam = request.getParameter("userParam");
		String state = request.getParameter("state");
		//size
		int pageSize = Integer.parseInt(request.getParameter("size"));
		
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("loan_list");

		int pageNo=1;
		Page page = null;
		// int pageSize=300;
		try {
			pageNo = Integer.parseInt(request.getParameter("current"));

			List<String> children = permissionFacade.getOwnerUserIds();

			page = loanOrderService.pagedQueryAdmin(pageNo, pageSize, userParam, orderNo, roleName, state , children);
		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
//			return modelAndView;
			return Result.failed(e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
//			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//			return modelAndView;
			return Result.failed("程序错误");
		}

		Map<String,Object> map = new HashMap<>();
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		map.put("page", page);
		map.put("message", message);
		map.put("error", error);
		map.put("userParam", userParam);
		return Result.ok(map);
	}
	
	@RequestMapping(action + "queryLoanRelation.action")
	public String queryLoanRelation(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String id = request.getParameter("id");
			resultMap.put("code", 0);
			resultMap.put("loanRelation", loanRelationOrderService.queryLoanRelation(id));
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}

		return JSON.toJSONString(resultMap);
	}
	
}
