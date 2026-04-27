package com.yami.trading.service.exchange.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.exchange.ExchangeLever;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.dao.exchangelever.ExchangeLeverMapper;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import com.yami.trading.service.exchange.ExchangeLeverService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeLeverServiceImpl extends ServiceImpl<ExchangeLeverMapper, ExchangeLever> implements ExchangeLeverService {
    @Override
    public List<ExchangeLever> findBySymbol(String symbol) {

        List<ExchangeLever> list = list(Wrappers.<ExchangeLever>query().lambda().eq(ExchangeLever::getSymbol, symbol));
        return list;
    }

}
