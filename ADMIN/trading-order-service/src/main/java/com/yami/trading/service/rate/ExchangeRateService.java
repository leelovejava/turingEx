package com.yami.trading.service.rate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.rate.domain.ExchangeRate;
import com.yami.trading.dao.rate.ExchangeRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 汇率管理Service
 *
 * @author lucas
 * @version 2023-03-28
 */
@Service
@Transactional
public class ExchangeRateService extends ServiceImpl<ExchangeRateMapper, ExchangeRate> {

    @Autowired
    RedisTemplate redisTemplate;


    public ExchangeRate findBy(String out_or_in, String currency) {

        return getOne(Wrappers.<ExchangeRate>query().lambda()
                .eq(ExchangeRate::getOutOrIn, out_or_in)
                .eq(ExchangeRate::getCurrency, currency));
    }

    public List<ExchangeRate> findBy(String out_or_in) {
        List<ExchangeRate> list = list(Wrappers.<ExchangeRate>query().lambda().eq(ExchangeRate::getOutOrIn, out_or_in));
        return list;

    }

    /**
     * 根据股票类型获取对应法币金额
     *
     * @param usdt 钱包usdt余额
     * @param type 股票类型
     * @return currency
     */
    public BigDecimal getCurrencyByType(BigDecimal usdt, String type) {
        if (StringUtils.isEmpty(type)) {
            type = Item.UK_STOCKS;
        }
        ExchangeRate rate = getOne(new LambdaQueryWrapper<ExchangeRate>().like(ExchangeRate::getType, type));
        if (rate != null) {
            BigDecimal rata = rate.getRata();
            usdt = usdt.multiply(rata).setScale(4, RoundingMode.FLOOR);
        }
        return usdt;
    }

    /**
     * 根据股票类型获取对应USDT金额
     *
     * @param currency 币种价值
     * @param type     股票类型
     * @return currency
     */
    public BigDecimal getUsdtByType(BigDecimal currency, String type) {
        if (StringUtils.isEmpty(type)) {
            type = Item.US_STOCKS;
        }
        if (type.contains("A") && !Item.A_STOCKS.equalsIgnoreCase(type)) {
            type = type.replace("A", "");
        }
        ExchangeRate rate = getOne(new LambdaQueryWrapper<ExchangeRate>().like(ExchangeRate::getType, type));
        if (rate != null) {
            BigDecimal rata = rate.getRata();
            currency = currency.divide(rata, 4, RoundingMode.FLOOR);
        }
        return currency;
    }

}
