package com.yami.trading.admin.controller.finance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yami.trading.bean.finance.Finance;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.service.finance.service.FinanceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 理财产品
 *
 */
@RestController
@CrossOrigin
public class FinanceController {

	private Logger logger = LogManager.getLogger(FinanceController.class);
	
	@Autowired
	protected FinanceService financeService;
	
	private final String action = "/api/finance!";

	/**
	 * 理财产品列表
	 */
	@RequestMapping(action + "list.action")
	public Object list() {
		
		ResultObject resultObject = new ResultObject();

		try {
			
			List<Finance> data = this.financeService.findAllState_1();
			resultObject.setData(bulidData(data));
			resultObject.setCode("0");
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 理财产品详情
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {
		String id = request.getParameter("id");
		
		ResultObject resultObject = new ResultObject();
		
		try {
			
			Finance data = this.financeService.findById(id);
			if (!StringUtils.isNullOrEmpty(data.getImg())) {
				String path = data.getImg();
				data.setImg(path);
			}
			
			resultObject.setData(data);
			resultObject.setCode("0");
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	protected List<Map<String, Object>> bulidData(List<Finance> finances) throws ParseException {

		List<Map<String, Object>> result_finances = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < finances.size(); i++) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			Finance finance = finances.get(i);

			System.out.println(finance);
			
			String path = finance.getImg();
			map.put("img", path);
			map.put("id", finance.getUuid());

			map.put("name", finance.getName());
			map.put("name_en", finance.getNameEn());
			map.put("name_cn", finance.getNameCn());
			map.put("name_kn", finance.getNameKn());
			map.put("name_jn", finance.getNameJn());
			map.put("cycle", finance.getCycle());
			map.put("daily_rate", finance.getDailyRate() + " ~ " + finance.getDailyRateMax());
			map.put("daily_rate_max", finance.getDailyRateMax());
			map.put("today_rate", finance.getTodayRate());
			map.put("default_ratio", finance.getDefaultRatio());
			map.put("investment_min", finance.getInvestmentMin());
			map.put("investment_max", finance.getInvestmentMax());
			map.put("state", finance.getState());
			map.put("buyCurrency", finance.getBuyCurrency());
			map.put("outputCurrency", finance.getOutputCurrency());

			result_finances.add(map);
		}

		return result_finances;
	}

	private Map<String, Object> bulidData(Finance finance) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		
		String path = finance.getImg();
		map.put("img", path);
		map.put("id", finance.getUuid());

		map.put("name", finance.getName());
		map.put("name_en", finance.getNameEn());
		map.put("name_cn", finance.getNameCn());
		map.put("name_kn", finance.getNameKn());
		map.put("name_jn", finance.getNameJn());
		map.put("cycle", finance.getCycle());
		map.put("daily_rate", finance.getDailyRate());
		map.put("daily_rate_max", finance.getDailyRateMax());
		map.put("today_rate", finance.getTodayRate());
		map.put("default_ratio", finance.getDefaultRatio());
		map.put("investment_min", finance.getInvestmentMin());
		map.put("investment_max", finance.getInvestmentMax());
		map.put("state", finance.getState());
		map.put("buyCurrency", finance.getBuyCurrency());
		map.put("outputCurrency", finance.getOutputCurrency());

		return map;
	}
	
}
