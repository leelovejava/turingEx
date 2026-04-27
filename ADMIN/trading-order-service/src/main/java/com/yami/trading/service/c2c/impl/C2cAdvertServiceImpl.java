package com.yami.trading.service.c2c.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.c2c.C2cAdvert;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.bean.c2c.C2cUser;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.rate.domain.ExchangeRate;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.c2c.C2cAdvertMapper;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cPaymentMethodService;
import com.yami.trading.service.c2c.C2cUserService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class C2cAdvertServiceImpl extends ServiceImpl<C2cAdvertMapper, C2cAdvert> implements C2cAdvertService {
    @Autowired
    SysparaService sysparaService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ExchangeRateService exchangeRateService;
    @Autowired
    ItemService itemService;
    @Autowired
    DataService dataService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;


    @Autowired
    C2cPaymentMethodService  c2cPaymentMethodService;

    @Autowired
    C2cUserService c2cUserService;
    /**
     * 获取 上架币种Map
     */
    @Override
    public Map<String, String> getSymbolMap() {
        // 获取 C2C上架币种配置
        Map<String, String> symMap = this.getC2cSyspara("c2c_advert_symbol");
        if (null == symMap) {
            symMap = new HashMap<String, String>();
        }
        Map<String, String> symbolMap = new HashMap<String, String>();
        List<Item> itemList = this.itemService.cacheGetAll();
        if (symMap.keySet().contains("USDT")) {
            symbolMap.put("usdt", "USDT");
        }
        for (Item item : itemList) {
            if (symMap.keySet().contains(item.getSymbol().toUpperCase())) {
                symbolMap.put(item.getSymbol(), item.getSymbol().toUpperCase());
                symbolMap.put(item.getSymbol().toUpperCase(), item.getSymbol().toUpperCase());
            }
        }
        return symbolMap;
    }

    @Override
    public List<C2cAdvert> getByC2cUserId(String c2c_user_id) {
        return list(Wrappers.<C2cAdvert>query().lambda().eq(C2cAdvert::getC2cUserId, c2c_user_id));
    }

    @Override
    public Page pagedQuery(long pageNo, long pageSize, String c2cUserCode, String c2cUserType, String userCode, String direction, String currency, String symbol) {
        Page page = new Page(pageNo, pageSize);
        return baseMapper.pagedQuery(page, c2cUserCode, c2cUserType, userCode, direction, currency, symbol);
    }

    @Override
    public void updateAdvert(C2cAdvert entity) {
        updateById(entity);
        C2cAdvert oldEntity = (C2cAdvert) redisTemplate.opsForValue().get(RedisKeys.C2C_ADVERT_ID + entity.getUuid().toString());
        String updateSql = "UPDATE T_C2C_ADVERT SET C2C_USER_ID=?, DIRECTION=?, CURRENCY=?, SYMBOL=?, SYMBOL_CLOSE=?, PAY_RATE=?, PAY_TYPE=?, SYMBOL_VALUE=?, COIN_AMOUNT=?, INVESTMENT_MIN=?, INVESTMENT_MAX=?, DEPOSIT=?, DEPOSIT_OPEN=?, ON_SALE=?, CLOSED=?, SORT_INDEX=?, EXPIRE_TIME=?, TRANSACTION_TERMS=?, ORDER_MSG=?, REMARK=?, UPDATE_TIME=? WHERE UUID=?";
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_ID + entity.getUuid().toString(), entity);
        // 承兑商的广告
        Map<String, C2cAdvert> map = (Map<String, C2cAdvert>) redisTemplate.opsForValue().get(RedisKeys.C2C_ADVERT_C2C_USER_ID + entity.getC2cUserId());
        if (null == map) {
            map = new ConcurrentHashMap<String, C2cAdvert>();
        }
        map.put(entity.getUuid().toString(), entity);
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_C2C_USER_ID + entity.getC2cUserId(), map);
        // 广告上架币种单价
        if (null != oldEntity && (!oldEntity.getCurrency().equals(entity.getCurrency())
                || !oldEntity.getSymbol().equals(entity.getSymbol())
                || !oldEntity.getDirection().equals(entity.getDirection()))) {
            // 删除之前的
            Map<String, Double> mapPriceOld = (Map<String, Double>) redisTemplate.opsForValue().get(
                    RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + oldEntity.getCurrency() + oldEntity.getSymbol() + oldEntity.getDirection());
            if (null != mapPriceOld) {
                redisTemplate.delete(RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + oldEntity.getCurrency() + oldEntity.getSymbol() + oldEntity.getDirection());
            }
        }
        Map<String, Double> mapPrice = (Map<String, Double>) redisTemplate.opsForValue().get(
                RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + entity.getCurrency() + entity.getSymbol() + entity.getDirection());
        if (null == mapPrice) {
            mapPrice = new ConcurrentHashMap<String, Double>();
        }
        mapPrice.put(entity.getUuid().toString(), entity.getSymbolValue());
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + entity.getCurrency() + entity.getSymbol() + entity.getDirection(), mapPrice);
    }

    @Override
    public void saveAdvert(C2cAdvert entity) {
        save(entity);
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_ID + entity.getUuid().toString(), entity);
        // 承兑商的广告
        Map<String, C2cAdvert> map = (Map<String, C2cAdvert>) redisTemplate.opsForValue().get(RedisKeys.C2C_ADVERT_C2C_USER_ID + entity.getC2cUserId());
        if (null == map) {
            map = new ConcurrentHashMap<String, C2cAdvert>();
        }
        map.put(entity.getUuid().toString(), entity);
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_C2C_USER_ID + entity.getC2cUserId(), map);
        // 广告上架币种单价
        Map<String, Double> mapPrice = (Map<String, Double>) redisTemplate.opsForValue().get(
                RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + entity.getCurrency() + entity.getSymbol() + entity.getDirection());
        if (null == mapPrice) {
            mapPrice = new ConcurrentHashMap<String, Double>();
        }
        mapPrice.put(entity.getUuid().toString(), entity.getSymbolValue());
        redisTemplate.opsForValue().set(RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + entity.getCurrency() + entity.getSymbol() + entity.getDirection(), mapPrice);
    }

    /**
     * 计算广告参数
     */
    @Override
    public Map<String, Object> getComputeValue(double deposit_total, String currency, String symbol, double coin_amount, double symbol_value) {
        Map<String, Object> result = new HashMap<>();
        double symbol_close = 0d;
        if (symbol.equals("usdt")) {
            symbol_close = 1;
        } else {
            List<Realtime> list = this.dataService.realtime(symbol);
            if (0 == list.size()) {
                log.error("行情获取异常,币种：" + symbol);
                throw new YamiShopBindException("行情获取异常，请重试");
            }
            Realtime realtime = list.get(0);
            symbol_close = realtime.getClose();
        }
        if (0 == symbol_close) {
            throw new YamiShopBindException("行情获取异常，请重试");
        }
        ExchangeRate ex = this.exchangeRateService.findBy(Constants.OUT_OR_IN_DEFAULT, currency);
        // 支付比率=支付币种汇率*上架币种实时行情价/币种单价；例如，支付比率95%，1USDT=7.3CNY*1*95%=6.935CNY
        double payRate = Arith.mul(Arith.div(Arith.mul(ex.getRata().doubleValue(), symbol_close), symbol_value), 100);
        // 广告保证金=交易币种数量*上架币种实时行情价
        double depositOpen = Arith.mul(coin_amount, symbol_close, 10);
        // 支付币种市价=支付币种汇率*上架币种实时行情价；
        double price = Arith.mul(ex.getRata().doubleValue(), symbol_close, 10);
        // 交易币种数量=广告派单额度/上架币种实时行情价
        double coinAmountMax = Arith.div(deposit_total, symbol_close, 10);
        // 最小支付金额
        double investmentMinLimit = 0;
        // C2C承兑商广告：单笔订单最低限额不能低于（支付币种金额折算成USDT）
        Object obj = this.sysparaService.find("c2c_user_advert_investment_min_limit");
        if (null != obj) {
            String c2c_user_advert_investment_min_limit = this.sysparaService.find("c2c_user_advert_investment_min_limit").getSvalue();
            if (!StringUtils.isEmptyString(c2c_user_advert_investment_min_limit)) {
                double limit_usdt = Double.valueOf(c2c_user_advert_investment_min_limit).doubleValue();
                investmentMinLimit = Arith.mul(Arith.div(limit_usdt, symbol_close, 10), symbol_value, 10);
            }
        }
        // 最大支付金额
        double investmentMaxLimit = Arith.mul(coinAmountMax, symbol_value);
        DecimalFormat df = new DecimalFormat("#.########");
        result.put("pay_rate", (int) payRate);
        result.put("deposit_open", df.format(new Double(depositOpen)));
        result.put("all_deposit", df.format(Arith.sub(deposit_total, depositOpen)));
        result.put("symbol_close", df.format(new Double(symbol_close)));
        result.put("price", df.format(new Double(price)));
        result.put("coin_amount_max", df.format(new Double(coinAmountMax)));
        result.put("investment_min_limit", df.format(new Double(investmentMinLimit)));
        result.put("investment_max_limit", df.format(new Double(investmentMaxLimit)));
        return result;
    }

    @Override
    public Page pagedQuery(int page_no, int page_size, String c2c_user_id, String direction, String currency, String symbol, String amount, Integer on_sale, Integer closed, boolean is_c2c_user) {
        Page page = new Page(page_no, page_size);
        double amount_double = 0;
        if (StringUtils.isNotEmpty(amount)) {
            amount_double = Double.valueOf(amount).doubleValue();
        }
        if (is_c2c_user) {
            baseMapper.pagedQueryC2cUser(page, c2c_user_id, direction, currency, symbol,
                    amount_double, on_sale, closed);
        } else {
            baseMapper.pagedQueryNotC2cUser(page, c2c_user_id, direction, currency, symbol,
                    amount_double, on_sale, closed);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        // 币种默认保留8位
        DecimalFormat dfCoin = new DecimalFormat("#.########");
        for (Map<String, Object> data : (List<Map<String, Object>>) page.getRecords()) {
            data.put("symbol_value", df.format(data.get("symbol_value")));
            data.put("coin_amount", dfCoin.format(data.get("coin_amount")));
        }
        return page;
    }

    @Override
    public Map<String, String> getCurrencyMap() {
        // 获取 C2C支付币种配置
        Map<String, String> curMap = this.getC2cSyspara("c2c_advert_currency");
        if (null == curMap) {
            curMap = new HashMap<>();
        }
        Map<String, String> currencyMap = new HashMap<String, String>();
        List<ExchangeRate> exchangeRateList = this.exchangeRateService.findBy(Constants.OUT_OR_IN_DEFAULT);
        for (ExchangeRate er : exchangeRateList) {
            if (curMap.keySet().contains(er.getCurrency())) {
                currencyMap.put(er.getCurrency(), String.format("%s(%s)", er.getCurrency(), er.getName()));
            }
        }
        return currencyMap;
    }

    @Override
    public Map<String, Map<String, Object>> getBestPriceAdvert(String currency, String symbol, String direction, String order_type, double amount, double coinAmount) {
        Map<String, Map<String, Object>> resMap = new HashMap<>();
        // 支付方式模板 对应的最低最高价格
        Map<String, String> advertIdMap = new HashMap<String, String>();
        Map<String, Double> minPriceMap = new HashMap<String, Double>();
        List<C2cAdvert> list = list(Wrappers.<C2cAdvert>query().lambda()
                .eq(C2cAdvert::getCurrency, currency)
                .eq(C2cAdvert::getSymbol, symbol)
                .eq(C2cAdvert::getDirection, direction));

        Map<String, Double> mapPrice = new HashMap<>();
        for (C2cAdvert c2cAdvert : list) {
            mapPrice.put(c2cAdvert.getUuid().toString(), c2cAdvert.getSymbolValue());
        }
        for (String id : mapPrice.keySet()) {
            C2cAdvert advert = getById(id);
            if (0 == advert.getOnSale()) {
                continue;
            }
            if (1 == advert.getClosed()) {
                continue;
            }
            if (C2cOrder.ORDER_TYPE_BY_AMOUNT.equals(order_type)) {
                // 按支付金额支付
                if (amount < advert.getInvestmentMin()
                        || amount > advert.getInvestmentMax()
                        || advert.getCoinAmount() < Arith.div(amount, advert.getSymbolValue())) {
                    continue;
                }
            } else {
                // 按币种数量支付
                double amount1 = Arith.mul(coinAmount, advert.getSymbolValue());
                if (amount1 < advert.getInvestmentMin()
                        || amount1 > advert.getInvestmentMax()
                        || advert.getCoinAmount() < coinAmount) {
                    continue;
                }
            }
            String payType = advert.getPayType();
            if (StringUtils.isEmptyString(payType)) {
                continue;
            }
            String[] payTypeArr = payType.split(",");
            for (int i = 0; i < payTypeArr.length; i++) {
                String mci = payTypeArr[i];
                // 买币
                Double nowMin = minPriceMap.get(mci);
                if (null == nowMin) {
                    minPriceMap.put(mci, advert.getSymbolValue());
                    advertIdMap.put(mci, advert.getUuid().toString());
                } else {
                    if (nowMin.doubleValue() > advert.getSymbolValue()) {
                        minPriceMap.put(mci, advert.getSymbolValue());
                        advertIdMap.put(mci, advert.getUuid().toString());
                    }
                }
            }
        }
        for (String advertId : advertIdMap.keySet()) {
            for (C2cAdvert c2cAdvert : list) {
                if (c2cAdvert.getUuid().equals(advertIdMap.get(advertId))) {
                    String payType = c2cAdvert.getPayType();
                    if (StringUtils.isEmptyString(payType)) {
                        continue;
                    }
                    String[] payTypeArr = payType.split(",");
                    for (int i = 0; i < payTypeArr.length; i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Double minPrice = minPriceMap.get(payTypeArr[i]);
                        C2cUser c2cUser = c2cUserService.getById(c2cAdvert.getC2cUserId());
                        if (c2cUser != null) {
                            Map<String, C2cPaymentMethod> c2cPaymentMethodMap = c2cPaymentMethodService.getByPartyId(c2cUser.getC2cUserPartyId());
                            for (String id : c2cPaymentMethodMap.keySet()) {
                                C2cPaymentMethod c2cPaymentMethod = c2cPaymentMethodMap.get(id);
                                if (c2cPaymentMethod.getMethodConfigId().equals(payTypeArr[i])) {
                                    map.put("id", c2cPaymentMethod.getUuid().toString());
                                    map.put("payment_method_id", c2cPaymentMethod.getUuid().toString());
                                    map.put("method_name", c2cPaymentMethod.getMethodName());
                                    map.put("method_img", awsS3OSSFileService.getUrl(c2cPaymentMethod.getMethodImg()));
                                    map.put("method_img_path", c2cPaymentMethod.getMethodImg());
                                    map.put("advert_id", advertIdMap.get(advertId));
                                    map.put("advert_price", minPrice);
                                    resMap.put(payTypeArr[i], map);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

        return resMap;
    }


        /*
     * 获取 用户所有支付方式  最优价格广告
     */
    @Override
    public Map<String, Map<String, Object>> getBestPriceAdvert(String currency,
                                                               String symbol,
                                                               String direction,
                                                               String order_type,
                                                               double amount,
                                                               double coinAmount,
                                                               List<C2cPaymentMethod> cpmList) {
        Map<String, Map<String, Object>> retMap = new HashMap<>();
        Set<String> methodConfigIdSet = new HashSet<>();
        for (int i = 0; i < cpmList.size(); i++) {
            C2cPaymentMethod cpm = cpmList.get(i);
            if (null != cpm) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cpm.getUuid());
                map.put("payment_method_id", cpm.getUuid());
                map.put("method_name", cpm.getMethodName());
                map.put("method_img", cpm.getMethodImg());
                map.put("method_img_path", cpm.getMethodImg());
                map.put("advert_id", "");
                map.put("advert_price", 0D);
                retMap.put(cpm.getUuid(), map);
                methodConfigIdSet.add(cpm.getMethodConfigId());
            }
        }
        // 支付方式模板 对应的最低最高价格
        Map<String, String> advertIdMap = new HashMap<>();
        Map<String, Double> minPriceMap = new HashMap<>();
        Map<String, Double> maxPriceMap = new HashMap<>();
        List<C2cAdvert> list = list(Wrappers.<C2cAdvert>query().lambda()
                .eq(C2cAdvert::getCurrency, currency)
                .eq(C2cAdvert::getSymbol, symbol)
                .eq(C2cAdvert::getDirection, direction));

        Map<String, Double> mapPrice = new HashMap<>();
        for (C2cAdvert c2cAdvert : list) {
            mapPrice.put(c2cAdvert.getUuid(), c2cAdvert.getSymbolValue());
        }
        // Map<String, Double> mapPrice = (Map<String, Double>)redisTemplate.opsForValue().get(RedisKeys.C2C_ADVERT_CURRENCY_SYMBOL_DIRECTION + currency + symbol + direction);
        if (null == mapPrice || 0 == mapPrice.size()) {
            return retMap;
        } else {
            for (String id : mapPrice.keySet()) {
                C2cAdvert advert = getById(id);
                if (0 == advert.getOnSale()) {
                    continue;
                }
                if (1 == advert.getClosed()) {
                    continue;
                }
                if (C2cOrder.ORDER_TYPE_BY_AMOUNT.equals(order_type)) {
                    // 按支付金额支付
                    if (amount < advert.getInvestmentMin()
                            || amount > advert.getInvestmentMax()
                            || advert.getCoinAmount() < Arith.div(amount, advert.getSymbolValue())) {
                        continue;
                    }
                } else {
                    // 按币种数量支付
                    double amount1 = Arith.mul(coinAmount, advert.getSymbolValue());
                    if (amount1 < advert.getInvestmentMin()
                            || amount1 > advert.getInvestmentMax()
                            || advert.getCoinAmount() < coinAmount) {
                        continue;
                    }
                }
                String payType = advert.getPayType();
                if (StringUtils.isEmptyString(payType)) {
                    continue;
                }
                String[] payTypeArr = payType.split(",");
                for (int i = 0; i < payTypeArr.length; i++) {
                    String mci = payTypeArr[i];
                    if (methodConfigIdSet.contains(mci)) {
                        if (C2cAdvert.DIRECTION_BUY.equals(direction)) {
                            // 买币
                            Double nowMin = minPriceMap.get(mci);
                            if (null == nowMin) {
                                minPriceMap.put(mci, advert.getSymbolValue());
                                advertIdMap.put(mci, advert.getUuid().toString());
                            } else {
                                if (nowMin.doubleValue() > advert.getSymbolValue()) {
                                    minPriceMap.put(mci, advert.getSymbolValue());
                                    advertIdMap.put(mci, advert.getUuid().toString());
                                }
                            }
                        } else if (C2cAdvert.DIRECTION_SELL.equals(direction)) {
                            // 卖币
                            Double nowMax = maxPriceMap.get(mci);
                            if (null == nowMax) {
                                maxPriceMap.put(mci, advert.getSymbolValue());
                                advertIdMap.put(mci, advert.getUuid().toString());
                            } else {
                                if (nowMax.doubleValue() < advert.getSymbolValue()) {
                                    maxPriceMap.put(mci, advert.getSymbolValue());
                                    advertIdMap.put(mci, advert.getUuid().toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < cpmList.size(); i++) {
            C2cPaymentMethod cpm = cpmList.get(i);
            if (null != cpm) {
                Map<String, Object> map = retMap.get(cpm.getUuid().toString());
                if (C2cAdvert.DIRECTION_BUY.equals(direction)) {
                    // 买币
                    Double minPrice = minPriceMap.get(cpm.getMethodConfigId());
                    if (null != minPrice) {
                        map.put("advert_id", advertIdMap.get(cpm.getMethodConfigId()));
                        map.put("advert_price", minPrice);
                    }
                } else if (C2cAdvert.DIRECTION_SELL.equals(direction)) {
                    // 卖币
                    Double maxPrice = maxPriceMap.get(cpm.getMethodConfigId());
                    if (null != maxPrice) {
                        map.put("advert_id", advertIdMap.get(cpm.getMethodConfigId()));
                        map.put("advert_price", maxPrice);
                    }
                }
                retMap.put(cpm.getUuid(), map);
            }
        }

        Map<String, Map<String, Object>> resMap = new HashMap<>();
        for (String id : retMap.keySet()) {
            Map<String, Object> map = retMap.get(id);
            if (null != map
                    && null != map.get("advert_id")
                    && null != map.get("advert_price")
                    && StringUtils.isNotEmpty(map.get("advert_id").toString())
                    && 0 != ((Double) map.get("advert_price")).doubleValue()) {
                resMap.put(id, map);
            }
        }

        return resMap;
    }

    /*
     * 获取 C2C支付方式类型、C2C支付币种配置、C2C上架币种配置、C2C广告支付时效
     */
    @Override
    public Map<String, String> getC2cSyspara(String syspara) {
        if ("c2c_payment_method_type".equals(syspara)) {
            // C2C支付方式类型
            Map<String, String> pmtMap = new HashMap<String, String>();
            Syspara obj = this.sysparaService.find("c2c_payment_method_type");
            if (null != obj) {
                String pmtStr = obj.getSvalue();
                String[] pmtArray = pmtStr.split("&&");
                for (int i = 0; i < pmtArray.length; i++) {
                    String[] pmt = pmtArray[i].split("##");
                    pmtMap.put(pmt[0], pmt[1]);
                }
                return pmtMap;
            }
        } else if ("c2c_advert_currency".equals(syspara)) {
            // C2C支付币种配置
            Map<String, String> acMap = new HashMap<String, String>();
            Syspara obj = this.sysparaService.find("c2c_advert_currency");
            if (null != obj) {
                String acStr = obj.getSvalue();
                String[] acArray = acStr.split("&&");
                for (int i = 0; i < acArray.length; i++) {
                    String[] ac = acArray[i].split("##");
                    acMap.put(ac[0], ac[1]);
                }
                return acMap;
            }
        } else if ("c2c_advert_symbol".equals(syspara)) {
            // C2C上架币种配置
            Map<String, String> asMap = new HashMap<String, String>();
            Syspara obj = this.sysparaService.find("c2c_advert_symbol");
            if (null != obj) {
                String asStr = obj.getSvalue();
                String[] asArray = asStr.split("##");
                for (int i = 0; i < asArray.length; i++) {
                    asMap.put(asArray[i], asArray[i]);
                }
                return asMap;
            }
        } else if ("c2c_advert_expire_time".equals(syspara)) {
            // C2C广告支付时效
            Map<String, String> aetMap = new HashMap<String, String>();
            Syspara obj = this.sysparaService.find("c2c_advert_expire_time");
            if (null != obj) {
                String aetStr = obj.getSvalue();
                String[] aetArray = aetStr.split("&&");
                for (int i = 0; i < aetArray.length; i++) {
                    String[] aet = aetArray[i].split("##");
                    aetMap.put(aet[0], aet[1]);
                }
                return aetMap;
            }
        }

        return new HashMap<String, String>();
    }

    /**
     * 获取所有上架币种单价
     */
    public Map<String, String> getAllSymbolPrice(String currency) {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> allPrice = new HashMap<String, String>();
        ExchangeRate ex = this.exchangeRateService.findBy(Constants.OUT_OR_IN_DEFAULT, currency);
        Map<String, String> allSymbol = this.getC2cSyspara("c2c_advert_symbol");
        if (null == allSymbol || 0 == allSymbol.size()) {
            return new HashMap<String, String>();
        }
        for (String symbol : allSymbol.keySet()) {
            symbol = itemService.getCleanSymbol(symbol);
            double symbol_close = 0d;
            if (symbol.equals("usdt")) {
                symbol_close = 1;
            } else {
                List<Realtime> list = this.dataService.realtime(symbol);
                if (0 == list.size()) {
                    continue;
                }
                Realtime realtime = list.get(0);
                symbol_close = realtime.getClose();
            }
            if (0 == symbol_close) {
                continue;
            }
            // 支付币种市价=支付币种汇率*上架币种实时行情价；
            double price = Arith.mul(ex.getRata().doubleValue(), symbol_close);
            allPrice.put(symbol, df.format(new Double(price)));
        }
        return allPrice;
    }

    /*
     * 获取 语种说明
     */
    @Override
    public String getLanguageIntro() {
        Map<String, String> langMap = Constants.LANGUAGE;
        String retStr = "";
        for (String lang : langMap.keySet()) {
            String langName = langMap.get(lang);
            if (StringUtils.isEmptyString(retStr)) {
                retStr = lang + " " + langName;
            } else {
                retStr += "; " + lang + " " + langName;
            }
        }
        return retStr + ";";
    }

    /*
     * 获取 支付方式类型说明
     */
    @Override
    public String getMethodTypeIntro() {
        Map<String, String> pmtMap = this.getC2cSyspara("c2c_payment_method_type");
        String retStr = "";
        for (String pmt : pmtMap.keySet()) {
            String pmtName = pmtMap.get(pmt);
            if (StringUtils.isEmptyString(retStr)) {
                retStr = pmt + " " + pmtName;
            } else {
                retStr += "; " + pmt + " " + pmtName;
            }
        }
        return retStr + ";";
    }
}
