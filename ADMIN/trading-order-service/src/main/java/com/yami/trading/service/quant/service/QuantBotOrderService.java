package com.yami.trading.service.quant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.quant.QuantBotOrder;

public interface QuantBotOrderService extends IService<QuantBotOrder> {

	/**
	 * 创建机器人订单
	 */
	QuantBotOrder createBotOrder(QuantBotOrder order);
}
