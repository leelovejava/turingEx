package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.c2c.C2cAdvert;
import com.yami.trading.bean.c2c.C2cUser;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.rate.domain.ExchangeRate;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cTranslateService;
import com.yami.trading.service.c2c.C2cUserService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * C2C广告
 */
@RestController
@CrossOrigin
@Slf4j
public class ApiC2cAdvertController {
    @Autowired
    private C2cAdvertService c2cAdvertService;
    @Autowired
    private C2cUserService c2cUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;
    @Autowired
    private C2cTranslateService c2cTranslateService;

    /**
     * 获取 支付币种（法币） 列表
     */
    @RequestMapping("/api/c2cAdvert!currency.action")
    public Result currency() {
        Map<String, String> pmtMap = c2cAdvertService.getC2cSyspara("c2c_advert_currency");
        List<Map<String, Object>> data = new ArrayList<>();
        List<ExchangeRate> exchangeRateList = exchangeRateService.findBy(Constants.OUT_OR_IN_DEFAULT);
        for (ExchangeRate er : exchangeRateList) {
            if (pmtMap.keySet().contains(er.getCurrency())) {
                Map<String, Object> erMap = new HashMap<String, Object>();
                erMap.put("out_or_in", er.getOutOrIn());
                erMap.put("rate", er.getRata());
                erMap.put("currency", er.getCurrency());
                erMap.put("name", er.getName());
                erMap.put("currency_symbol", er.getCurrencySymbol());
                data.add(erMap);
            }
        }

        return Result.succeed(data);
    }

    /**
     * 获取 上架币种 列表
     */
    @RequestMapping("/api/c2cAdvert!symbol.action")
    public Result symbol() {
        Map<String, String> asMap =c2cAdvertService.getC2cSyspara("c2c_advert_symbol");
        Map<String, String> data = new HashMap<>();
        List<Item> itemList = itemService.cacheGetAll();
        data.put("usdt", "USDT");
        for (Item item : itemList) {
            if (asMap.keySet().contains(item.getSymbol().toUpperCase())) {
                data.put(item.getSymbol(), item.getSymbol().toUpperCase());
            }
        }
        return Result.succeed(data);
    }

    /**
     * 获取 广告 列表
     * <p>
     * direction 买卖方式：buy买/sell卖
     * currency 支付币种（大写CNY、USD、CAD等）
     * symbol 上架币种（大写USDT、BTC、ETH）
     * method_type 支付方式类型：0其它/1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款
     * amount 支付金额：必须小于剩余派单金额
     */
    @RequestMapping("/api/c2cAdvert!list.action")
    public Result list(HttpServletRequest request) {
        String page_no = request.getParameter("page_no");
        String direction = request.getParameter("direction");
        String currency = request.getParameter("currency");
        String symbol = request.getParameter("symbol");
        String method_type = request.getParameter("method_type");
        String amount = request.getParameter("amount");
        String language = request.getParameter("language");

        String error = this.verif(direction, currency, symbol, method_type, amount);
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        if (StringUtils.isNullOrEmpty(page_no)) {
            page_no = "1";
        }
        if (!StringUtils.isInteger(page_no)) {
            throw new YamiShopBindException("页码不是整数");
        }
        if (Integer.valueOf(page_no).intValue() <= 0) {
            throw new YamiShopBindException("页码不能小于等于0");
        }
        int page_no_int = Integer.valueOf(page_no).intValue();
        String partyId = SecurityUtils.getUser().getUserId();
         String c2cUserId = "";
        User party = userService.getById(partyId);
        if (null == party) {
            throw new YamiShopBindException("用户不存在");
        }
        if (Arrays.asList(1, 2).contains(party.getC2cUserType())) {
            C2cUser c2cUser = c2cUserService.getByPartyId(partyId);
            if (null != c2cUser) {
                c2cUserId = c2cUser.getUuid();
            }
        }
        Page page = c2cAdvertService.pagedQuery(page_no_int, 20, c2cUserId, direction, currency, symbol, amount, 1, 0, false);

        if (null == page) {
            return Result.succeed(new ArrayList<Map<String, Object>>());
        } else {
            List<Map<String, Object>> dataResult = new ArrayList<Map<String, Object>>();
            if (!StringUtils.isEmptyString(method_type)) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) page.getRecords();
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> map = data.get(i);
                    Object pay_type = map.get("pay_type");
                    if (null == pay_type) {
                        continue;
                    }
                    // 获取 Map<支付方式模板ID，支付方式模板类型>
                    Map<String, String> methodConfigIdTypeMap =c2cPaymentMethodConfigService.getMethodConfigIdTypeMap();
                    String[] payTypes = pay_type.toString().split(",");
                    for (String type : payTypes) {
                        if (method_type.equals(methodConfigIdTypeMap.get(type))) {
                            dataResult.add(map);
                            break;
                        }
                    }
                }
            } else {
                dataResult = (List<Map<String, Object>>) page.getRecords();
            }
            for (int i = 0; i < dataResult.size(); i++) {
                Map<String, Object> mso = dataResult.get(i);
                if (null != mso) {
                    int thirtyDaysOrder = 0;
                    int thirtyDaysOrderBase = 0;
                    double thirtyDaysOrderRatio = 0D;
                    double thirtyDaysOrderRatioBase = 0D;
                    if (null != mso.get("thirty_days_order") && StringUtils.isNotEmpty(mso.get("thirty_days_order").toString())) {
                        thirtyDaysOrder = Integer.valueOf(mso.get("thirty_days_order").toString()).intValue();
                    }
                    if (null != mso.get("thirty_days_order_base") && StringUtils.isNotEmpty(mso.get("thirty_days_order_base").toString())) {
                        thirtyDaysOrderBase = Integer.valueOf(mso.get("thirty_days_order_base").toString()).intValue();
                    }
                    if (null != mso.get("thirty_days_order_ratio") && StringUtils.isNotEmpty(mso.get("thirty_days_order_ratio").toString())) {
                        thirtyDaysOrderRatio = Double.valueOf(mso.get("thirty_days_order_ratio").toString()).doubleValue();
                    }
                    if (null != mso.get("thirty_days_order_ratio_base") && StringUtils.isNotEmpty(mso.get("thirty_days_order_ratio_base").toString())) {
                        thirtyDaysOrderRatioBase = Double.valueOf(mso.get("thirty_days_order_ratio_base").toString()).doubleValue();
                    }
                    mso.put("thirty_days_order", thirtyDaysOrderBase + thirtyDaysOrder);
                    mso.put("thirty_days_order_ratio", 0 != thirtyDaysOrderRatioBase ? thirtyDaysOrderRatioBase : thirtyDaysOrderRatio);
                    if (null != mso.get("head_img") && StringUtils.isNotEmpty(mso.get("head_img").toString())) {
                        String path = awsS3OSSFileService.getUrl(mso.get("head_img").toString());
                        mso.put("head_img", path);
                    }
                    if (mso.get("pay_type") != null && StringUtils.isNotEmpty(mso.get("pay_type").toString())) {
                        List<String> pay_type = new LinkedList<String>();
                        String[] types = mso.get("pay_type").toString().split(",");
                        for (String type : types) {
                            C2cPaymentMethodConfig method = c2cPaymentMethodConfigService.getById(type.trim());
                            if (null != method) {
                                String payParamLangKeyPrefix = "pay.param.";
                                String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + method.getUuid();

                                C2cTranslate trans = c2cTranslateService.get(method.getUuid(), payMethodNameLangKey, language);
                                if (null != trans) {
                                    pay_type.add(trans.getTranslate());
                                } else {
                                    pay_type.add(method.getMethodName());
                                }
                            }
                        }
                        mso.put("pay_type_name", String.join(",", pay_type));
                    }
                }
            }
            return  Result.succeed(dataResult);
        }
    }

    /**
     * 获取 广告 详情
     * id C2C广告ID
     */
    @RequestMapping("/api/c2cAdvert!get.action")
    public Result get(HttpServletRequest request) {

        String id = request.getParameter("id");
        String language = request.getParameter("language");
        if (StringUtils.isEmptyString(id)) {
            throw new YamiShopBindException("C2C广告id不正确");
        }
        C2cAdvert c2cAdvert = c2cAdvertService.getById(id);
        if (null == c2cAdvert) {
            throw new YamiShopBindException("广告不存在");
        }
        C2cUser c2cUser = this.c2cUserService.getById(c2cAdvert.getC2cUserId());
        if (null == c2cUser) {
            throw new YamiShopBindException("承兑商不存在");
        }
        User c2cParty = userService.getById(c2cUser.getC2cUserPartyId());
        if (null == c2cParty) {
            throw new YamiShopBindException("承兑商的用户信息不存在");
        }
        Map<String, String> data = new HashMap<>();
        data.put("id", c2cAdvert.getUuid());
        data.put("direction", c2cAdvert.getDirection());
        data.put("currency", c2cAdvert.getCurrency());
        data.put("symbol", c2cAdvert.getSymbol());
        data.put("pay_rate", String.valueOf(c2cAdvert.getPayRate()));
        data.put("pay_type", c2cAdvert.getPayType());
        data.put("symbol_value", String.valueOf(c2cAdvert.getSymbolValue()));
        data.put("coin_amount", String.valueOf(c2cAdvert.getCoinAmount()));
        data.put("investment_min", String.valueOf(c2cAdvert.getInvestmentMin()));
        data.put("investment_max", String.valueOf(c2cAdvert.getInvestmentMax()));
        data.put("deposit", String.valueOf(c2cAdvert.getDeposit()));
        data.put("deposit_open", String.valueOf(c2cAdvert.getDepositOpen()));
        data.put("on_sale", String.valueOf(c2cAdvert.getOnSale()));
        data.put("sort_index", String.valueOf(c2cAdvert.getSortIndex()));
        data.put("expire_time", String.valueOf(c2cAdvert.getExpireTime()));
        data.put("transaction_terms", c2cAdvert.getTransactionTerms());
        data.put("order_msg", c2cAdvert.getOrderMsg());
        data.put("remark", c2cAdvert.getRemark());
//        data.put("create_time", c2cAdvert.getCreateTime());
//        data.put("update_time", c2cAdvert.getUpdateTime());
        data.put("c2c_user_id", c2cUser.getUuid());
        data.put("nick_name", c2cUser.getNickName());
        data.put("c2c_user_type", String.valueOf(c2cUser.getC2cUserType()));
        data.put("c2c_user_code", c2cUser.getC2cUserCode());
        data.put("party_id", c2cParty.getUserId());
        data.put("user_code", c2cParty.getUserCode());
        data.put("user_name", c2cParty.getUserName());
        if (StringUtils.isNotEmpty(c2cUser.getHeadImg())) {
            String path = awsS3OSSFileService.getUrl(c2cUser.getHeadImg());
            data.put("head_img", path);
        } else {
            data.put("head_img", "");
        }
        if (data.get("pay_type") != null && StringUtils.isNotEmpty(data.get("pay_type").toString())) {
            List<String> pay_type = new LinkedList<String>();
            String[] types = data.get("pay_type").toString().split(",");
            for (String type : types) {
                C2cPaymentMethodConfig method = this.c2cPaymentMethodConfigService.get(type.trim());
                if (null != method) {
                    String payParamLangKeyPrefix = "pay.param.";
                    String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + method.getUuid();

                    C2cTranslate trans = c2cTranslateService.get(method.getUuid(), payMethodNameLangKey, language);
                    if (null != trans) {
                        pay_type.add(trans.getTranslate());
                    } else {
                        pay_type.add(method.getMethodName());
                    }
                }
            }
            data.put("pay_type_name", String.join(",", pay_type));
        }
        return Result.succeed(data);
    }

    private String verif(String direction, String currency, String symbol, String method_type, String amount) {
        if (!StringUtils.isEmptyString(direction) && !Arrays.asList("buy", "sell").contains(direction)) {
            return "买卖方式不正确";
        }
        Map<String, String> currencyMap = this.c2cAdvertService.getCurrencyMap();
        Map<String, String> symbolMap = this.c2cAdvertService.getSymbolMap();
        if (!StringUtils.isEmptyString(currency) && null != currencyMap && !currencyMap.containsKey(currency)) {
            return "支付币种不正确";
        }
        if (!StringUtils.isEmptyString(symbol) && null != symbolMap && !symbolMap.containsKey(symbol)) {
            return "上架币种不正确";
        }
        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        if (!StringUtils.isEmptyString(method_type) && !pmtMap.keySet().contains(method_type)) {
            return "支付方式类型不正确";
        }
        if (!StringUtils.isEmptyString(amount) && (!StringUtils.isDouble(amount) || Double.valueOf(amount).doubleValue() < 0)) {
            return "支付金额不正确";
        }
        return null;
    }
}
