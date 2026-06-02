package com.yami.trading.admin.controller.miner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yami.trading.bean.miner.Miner;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yami.trading.service.miner.job.MinerOrderExpireJob;
import com.yami.trading.service.miner.job.MinerOrderProfitJob;
import com.yami.trading.service.miner.service.MinerService;

/**
 * 矿机产品
 *
 */
@RestController
@CrossOrigin
public class MinerController {

	private Logger logger = LogManager.getLogger(MinerController.class);
	
	@Autowired
	protected MinerService minerService;

	@Autowired
	protected MinerOrderProfitJob minerOrderProfitJob;

	@Autowired
	protected MinerOrderExpireJob minerOrderExpireJob;

	private final String action = "api/miner!";

	/**
	 * 矿机产品列表
	 */
	@RequestMapping(action + "list.action")
	public Object list() throws IOException {

		ResultObject resultObject = new ResultObject();
		try {
			List<Miner> data = minerService.findAllState_1();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					result.add(minerService.getBindOne(data.get(i)));
				}
			}
			resultObject.setData(result);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
			logger.error("BusinessException:", e);
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 矿机产品详情
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		try {
			String id = request.getParameter("id");
			Miner data = minerService.findById(id);
			resultObject.setData(minerService.getBindOne(data));
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
			logger.error("BusinessException:", e);
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;
	}

	/**
	 * 手动触发矿机收益计算任务
	 */
	@RequestMapping(action + "runProfitJob.action")
	public Object runProfitJob() {
		ResultObject resultObject = new ResultObject();
		try {
			logger.info("手动触发矿机收益计算任务");
			minerOrderProfitJob.taskJob();
			resultObject.setCode("0");
			resultObject.setMsg("矿机收益计算任务执行成功");
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("任务执行失败: " + e.getMessage());
			logger.error("手动触发矿机收益计算任务失败:", e);
		}
		return resultObject;
	}

	/**
	 * 手动触发到期订单关闭任务
	 */
	@RequestMapping(action + "runExpireJob.action")
	public Object runExpireJob() {
		ResultObject resultObject = new ResultObject();
		try {
			logger.info("手动触发到期订单关闭任务");
			minerOrderExpireJob.closeExpiredOrders();
			resultObject.setCode("0");
			resultObject.setMsg("到期订单关闭任务执行成功");
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("任务执行失败: " + e.getMessage());
			logger.error("手动触发到期订单关闭任务失败:", e);
		}
		return resultObject;
	}
}
