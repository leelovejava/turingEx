package com.yami.trading.service.exchange;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.exchange.ExchangeLever;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;

import java.util.List;

public interface ExchangeLeverService  extends IService<ExchangeLever> {


    public List<ExchangeLever> findBySymbol(String symbol);
}
