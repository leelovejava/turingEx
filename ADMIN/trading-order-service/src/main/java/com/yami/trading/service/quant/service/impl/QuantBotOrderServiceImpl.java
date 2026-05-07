package com.yami.trading.service.quant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.quant.QuantBotOrder;
import com.yami.trading.dao.quant.QuantBotOrderMapper;
import com.yami.trading.service.quant.service.QuantBotOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuantBotOrderServiceImpl extends ServiceImpl<QuantBotOrderMapper, QuantBotOrder> implements QuantBotOrderService {

	@Override
	public QuantBotOrder createBotOrder(QuantBotOrder order) {
		this.save(order);
		return order;
	}
}
