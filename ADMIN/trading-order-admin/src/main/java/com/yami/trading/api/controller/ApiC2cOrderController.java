package com.yami.trading.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.c2c.C2cAdvert;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cOrderService;
import com.yami.trading.service.c2c.C2cPaymentMethodService;
import com.yami.trading.service.c2c.C2cTranslateService;
import com.yami.trading.service.chat.otc.OtcOnlineChatMessageService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
@Slf4j
public class ApiC2cOrderController {
    @Autowired
    private C2cOrderService c2cOrderService;
    @Autowired
    SessionTokenService sessionTokenService;
    @Autowired
    C2cAdvertService c2cAdvertService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    C2cPaymentMethodService c2cPaymentMethodService;
    @Autowired
    C2cTranslateService c2cTranslateService;
    @Autowired
    PasswordEncoder  passwordEncoder;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @Autowired
    OtcOnlineChatMessageService otcOnlineChatMessageService;

    /**
     * 首次进入下单页面，传递session_token
     */
    @RequestMapping("/api/c2cOrder!order_open.action")
    public Object order_open(HttpServletRequest request) {
        String currency = request.getParameter("currency");
        if (StringUtils.isEmptyString(currency)) {
            throw new YamiShopBindException("支付币种不正确");
        }
        String partyId = SecurityUtils.getCurrentUserId();
        String session_token = sessionTokenService.savePut(partyId);
        Map<String, String> allPrice = c2cAdvertService.getAllSymbolPrice(currency);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("session_token", session_token);
        data.put("all_price", allPrice);
        return Result.succeed(data);
    }

    private String verifOpen(String direction, String currency, String symbol, String order_type) {
        if (StringUtils.isEmptyString(direction) || !Arrays.asList("buy", "sell").contains(direction)) {
            return "买卖方式不正确";
        }
        Map<String, String> currencyMap = this.c2cAdvertService.getCurrencyMap();
        Map<String, String> symbolMap = this.c2cAdvertService.getSymbolMap();
        if (null == currencyMap || !currencyMap.containsKey(currency)) {
            return "支付币种不正确";
        }
        if (null == symbolMap || !symbolMap.containsKey(symbol)) {
            return "上架币种不正确";
        }
        if (StringUtils.isEmptyString(order_type) || !Arrays.asList("by_amount", "by_num").contains(order_type)) {
            throw new BusinessException("订单类型不正确");
        }

        return null;
    }

    /**
     * 快捷区下单：购买、出售，提交订单
     * <p>
     * safe_password 资金密码
     * payment_method_id 用户选择的自己的支付方式ID
     * order_type 订单类型：by_amount按支付金额/by_num按币种数量
     * amount 支付金额
     * coin_amount 币种数量
     * direction 买卖方式：buy买/sell卖
     * currency 支付币种
     * symbol 上架币种
     * remark 备注
     */
    @RequestMapping("/api/c2cOrder!open_quick_apply.action")
    public Object open_quick_apply(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
        String payment_method_id = request.getParameter("payment_method_id");
        String order_type = request.getParameter("order_type");
        String amount = request.getParameter("amount");
        String coin_amount = request.getParameter("coin_amount");
        String direction = request.getParameter("direction");
        String currency = request.getParameter("currency");
        String symbol = request.getParameter("symbol");
        String remark = request.getParameter("remark");
        String c2c_advert_id = request.getParameter("c2c_advert_id");
        String partyId = SecurityUtils.getCurrentUserId();
        User party = userService.getById(partyId);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
            throw new BusinessException("无权限");
        }
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
        if (null == object || !partyId.equals((String) object)) {
            throw new BusinessException("请稍后再试");
        }
        if (!party.isEnabled()) {
            throw new BusinessException("用户已锁定");
        }
        String error = this.verifOpen(direction, currency, symbol, order_type);
        if (StringUtils.isNotEmpty(error)) {
            throw new BusinessException(error);
        }
        if (StringUtils.isEmptyString(c2c_advert_id)) {
            throw new BusinessException("广告不存在");
        }
        if (C2cOrder.ORDER_TYPE_BY_AMOUNT.equals(order_type)) {
            // 按支付金额支付
            if (StringUtils.isEmptyString(amount)
                    || !StringUtils.isDouble(amount)
                    || Double.valueOf(amount).doubleValue() <= 0) {
                throw new BusinessException("支付金额不正确");
            }
          //  coin_amount = "0";
        } else {
            // 按币种数量支付
            if (StringUtils.isEmptyString(coin_amount)
                    || !StringUtils.isDouble(coin_amount)
                    || Double.valueOf(coin_amount).doubleValue() <= 0) {
                throw new BusinessException("币种数量不正确");
            }
            amount = "0";
        }
        // C2C用户未结束订单最大数量
        Long nofinishOrderCount = this.c2cOrderService.getNofinishOrderCount(partyId, direction);
        Object obj2 = this.sysparaService.find("c2c_nofinish_order_count_max");
        if (null != obj2) {
            if (nofinishOrderCount >= Long.valueOf(this.sysparaService.find("c2c_nofinish_order_count_max").getSvalue()).longValue()) {
                throw new BusinessException("用户未结束订单数量已达上限");
            }
        }
        // C2C用户下单是否需要基础认证（true:是，false:否）
        Object obj = this.sysparaService.find("c2c_order_need_kyc");
        if (null != obj) {
            if (!party.isRealNameAuthority()
                    && "true".equals(this.sysparaService.find("c2c_order_need_kyc").getSvalue())) {
                throw new YamiShopBindException("401", "未实名认证，是否认证？");
            }
        }
        // C2C每日订单取消最大次数
        int orderCancelDayTimes = 0;
        Map<String, Integer> map = (Map<String, Integer>) redisTemplate.opsForValue().get(RedisKeys.C2C_ORDER_CANCEL_DAY_TIMES);
        if (null != map && null != map.get(partyId)) {
            orderCancelDayTimes = map.get(partyId);
        }
        Object obj1 = this.sysparaService.find("c2c_order_cancel_day_times");
        if (null != obj1) {
            if (orderCancelDayTimes >= Integer.valueOf(this.sysparaService.find("c2c_order_cancel_day_times").getSvalue()).intValue()) {
                throw new BusinessException("今日取消订单次数太多了，请明日再试");
            }
        }
        C2cOrder c2cOrder = new C2cOrder();
        c2cOrder.setPartyId(partyId);
        c2cOrder.setPaymentMethodId(payment_method_id);
        c2cOrder.setOrderType(order_type);
        c2cOrder.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        // 0未付款
        c2cOrder.setState("0");
        c2cOrder.setAmount(StringUtils.isEmptyString(amount) ? 0 : Double.valueOf(amount).doubleValue());
        c2cOrder.setCoinAmount(StringUtils.isEmptyString(coin_amount) ? 0 : Double.valueOf(coin_amount).doubleValue());
        c2cOrder.setDirection(direction);
        c2cOrder.setCurrency(currency);
        c2cOrder.setSymbol(symbol);
        c2cOrder.setRemark(remark);
        c2cOrder.setC2cAdvertId(c2c_advert_id);
        this.c2cOrderService.saveOpenQuickApply(c2cOrder);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("order_no", c2cOrder.getOrderNo());
        return Result.succeed(data);
    }

    /**
     * 快捷区下单：获取最优价格的广告
     * <p>
     * order_type 订单类型：by_amount按支付金额/by_num按币种数量
     * amount 支付金额
     * coin_amount 币种数量
     * direction 买卖方式：buy买/sell卖
     * currency 支付币种
     * symbol 上架币种
     */
    @RequestMapping("/api/c2cOrder!get_best_price_advert.action")
    public Result get_best_price_advert(HttpServletRequest request) {
        String order_type = request.getParameter("order_type");
        String amount = request.getParameter("amount");
        String coin_amount = request.getParameter("coin_amount");
        String direction = request.getParameter("direction");
        String currency = request.getParameter("currency");
        String symbol = request.getParameter("symbol");
        String language = request.getParameter("language");
        String partyId = SecurityUtils.getCurrentUserId();
        User party = userService.getById(partyId);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
            throw new BusinessException("无权限");
        }
        if (!party.isEnabled()) {
            throw new BusinessException("用户已锁定");
        }
        String error = verifOpen(direction, currency, symbol, order_type);
        if (StringUtils.isNotEmpty(error)) {
            throw new BusinessException(error);
        }
        if (C2cOrder.ORDER_TYPE_BY_AMOUNT.equals(order_type)) {
            // 按支付金额支付
            if (StringUtils.isEmptyString(amount) || !StringUtils.isDouble(amount) || Double.valueOf(amount).doubleValue() <= 0) {
                throw new BusinessException("支付金额不正确");
            }
            coin_amount = "0";
        } else {
            // 按币种数量支付
            if (StringUtils.isEmptyString(coin_amount) || !StringUtils.isDouble(coin_amount) || Double.valueOf(coin_amount).doubleValue() <= 0) {
                throw new BusinessException("币种数量不正确");
            }
            amount = "0";
        }
        double amount_double = StringUtils.isEmptyString(amount) ? 0 : Double.valueOf(amount).doubleValue();
        double coin_amount_double = StringUtils.isEmptyString(coin_amount) ? 0 : Double.valueOf(coin_amount).doubleValue();
        Map<String, C2cPaymentMethod> cpmMap;
        List<C2cPaymentMethod> cpmList = new ArrayList<>();
        // 获取用户所有支付方式  id做key
        cpmMap = this.c2cPaymentMethodService.getByPartyId(partyId);
        if (null == cpmMap) {
            cpmMap = new HashMap<>();
        }
        for (String id : cpmMap.keySet()) {
            C2cPaymentMethod cpm = cpmMap.get(id);
            if (null != cpm) {
                cpmList.add(cpm);
            }
        }
        Map<String, Map<String, Object>> retMap=new HashMap<>();
        if (C2cAdvert.DIRECTION_BUY.equals(direction)) {
            retMap= c2cAdvertService.getBestPriceAdvert(currency, symbol, direction, order_type, amount_double, coin_amount_double);
        } else if (C2cAdvert.DIRECTION_SELL.equals(direction)) {
            retMap= c2cAdvertService.getBestPriceAdvert(currency, symbol, direction, order_type, amount_double, coin_amount_double, cpmList);
        }
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        for (String pmi : retMap.keySet()) {
            Map<String, Object> map = retMap.get(pmi);
            if (null != map) {
                Object method_name = map.get("method_name");
                Object payment_method_id = map.get("payment_method_id");

                if (null != method_name
                        && StringUtils.isNotEmpty(method_name.toString())
                        && payment_method_id != null
                        && StrUtil.isNotBlank(payment_method_id.toString())) {
                    // 批量查询优化 TODO
                    C2cPaymentMethod payMethodEntity = c2cPaymentMethodService.get(payment_method_id.toString());
                    if (payMethodEntity != null) {
                        String paymentConfigId = payMethodEntity.getMethodConfigId();
                        String payParamLangKeyPrefix = "pay.param.";
                        String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + paymentConfigId;

                        C2cTranslate trans = c2cTranslateService.get(paymentConfigId, payMethodNameLangKey, language);
                        if (null != trans) {
                            map.put("method_name", trans.getTranslate());
                        }
                    }
                }

                if (null!=map.get("method_img_path")) {
                    map.put("method_img_path", awsS3OSSFileService.getUrl(map.get("method_img_path").toString()));
                }

                retList.add(map);
            }
        }

        return Result.succeed(retList);
    }

    /**
     * 自选区下单：购买、出售
     * <p>
     * safe_password 资金密码
     * c2c_advert_id 广告id
     * payment_method_id 支付方式ID：购买为承兑商收款方式ID，出售为用户收款方式ID
     * order_type 订单类型：by_amount按支付金额/by_num按币种数量
     * amount 支付金额
     * coin_amount 币种数量
     * remark 备注
     */
    @RequestMapping("/api/c2cOrder!open.action")
    public Object open(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
//		String safe_password = request.getParameter("safe_password");
        String c2c_advert_id = request.getParameter("c2c_advert_id");
        String payment_method_id = request.getParameter("payment_method_id");
        String order_type = request.getParameter("order_type");
        String direction = request.getParameter("direction");
        String amount = request.getParameter("amount");
        String coin_amount = request.getParameter("coin_amount");
        String remark = request.getParameter("remark");
        String partyId = SecurityUtils.getCurrentUserId();
        Result resultObject = new Result();
        boolean lock = false;
        User party = null;
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        try {
            party = userService.getById(partyId);
            log.error("用户" + direction + "开始当前用户uid:" + party.getUserCode() + "当前用户名:" + party.getUserName() + "生成的订单号:" + orderNo);
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
                throw new YamiShopBindException("无权限");
            }
            if (!C2cLock.add(partyId)) {
                throw new YamiShopBindException("Please try again later");
            }
            lock = true;
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.del(session_token);
            if (null == object || !partyId.equals(object)) {
                throw new YamiShopBindException("请稍后再试");
            }
            if (!party.isEnabled()) {
                return Result.succeed("用户已锁定");
            }
            // C2C用户未结束订单最大数量
            Long nofinishOrderCount = c2cOrderService.getNofinishOrderCount(partyId);
            String c2c_sell_only_one = sysparaService.find("c2c_sell_only_one").getSvalue();
            if (StringUtils.isNotEmpty(c2c_sell_only_one) && "1".equals(c2c_sell_only_one)) {
                if (nofinishOrderCount >= 1) {
                    throw new YamiShopBindException("提交失败，当前有未处理订单");
                }
                Long.valueOf(c2c_sell_only_one).longValue();
            }
            Object obj2 = this.sysparaService.find("c2c_nofinish_order_count_max");
            if (null != obj2) {
                if (nofinishOrderCount >= Long.valueOf(this.sysparaService.find("c2c_nofinish_order_count_max").getSvalue()).longValue()) {
                    throw new YamiShopBindException("用户未结束订单数量已达上限");
                }
            }
            // C2C用户下单是否需要基础认证（true:是，false:否）
            Object obj = this.sysparaService.find("c2c_order_need_kyc");
            if (null != obj) {
                if (!party.isRealNameAuthority() && "true".equals(this.sysparaService.find("c2c_order_need_kyc").getSvalue())) {
                    return Result.succeed("未实名认证，是否认证？");
                }
            }
            // C2C每日订单取消最大次数
            int orderCancelDayTimes = 0;
            Map<String, Integer> map = (Map<String, Integer>) redisTemplate.opsForValue().get(RedisKeys.C2C_ORDER_CANCEL_DAY_TIMES);
            if (null != map && null != map.get(partyId)) {
                orderCancelDayTimes = map.get(partyId);
            }
            Object obj1 = this.sysparaService.find("c2c_order_cancel_day_times");
            if (null != obj1) {
                if (orderCancelDayTimes >= Integer.valueOf(this.sysparaService.find("c2c_order_cancel_day_times").getSvalue()).intValue()) {
                    throw new YamiShopBindException("今日取消订单次数太多了，请明日再试");
                }
            }
            C2cAdvert c2cAdvert = this.c2cAdvertService.getById(c2c_advert_id);
            if (null == c2cAdvert) {
                throw new YamiShopBindException("广告不存在");
            }
            C2cPaymentMethod method = c2cPaymentMethodService.get(payment_method_id);
            if (null == method) {
                throw new YamiShopBindException("支付方式不存在");
            }
            if (StringUtils.isEmptyString(order_type) || !Arrays.asList("by_amount", "by_num").contains(order_type)) {
                throw new YamiShopBindException("订单类型不正确");
            }
            if (C2cOrder.ORDER_TYPE_BY_AMOUNT.equals(order_type)) {
                // 按支付金额支付
                if (StringUtils.isEmptyString(amount) || !StringUtils.isDouble(amount) || Double.valueOf(amount).doubleValue() <= 0) {
                    throw new YamiShopBindException("支付金额不正确");
                }
                coin_amount = "0";
            } else {
                // 按币种数量支付
                if (StringUtils.isEmptyString(coin_amount) || !StringUtils.isDouble(coin_amount) || Double.valueOf(coin_amount).doubleValue() <= 0) {
                    throw new YamiShopBindException("币种数量不正确");
                }
                amount = "0";
            }
            Map<String, Object> data = new HashMap<String, Object>();
            C2cOrder c2cOrder = new C2cOrder();
            c2cOrder.setPartyId(partyId);
            c2cOrder.setC2cUserId(c2cAdvert.getC2cUserId());
            c2cOrder.setC2cAdvertId(c2c_advert_id);
            c2cOrder.setPaymentMethodId(payment_method_id);
            c2cOrder.setOrderType(order_type);
            c2cOrder.setOrderNo(orderNo);
            // 0未付款
            c2cOrder.setState("0");
            c2cOrder.setAmount(StringUtils.isEmptyString(amount) ? 0 : Double.valueOf(amount).doubleValue());
            c2cOrder.setCoinAmount(StringUtils.isEmptyString(coin_amount) ? 0 : Double.valueOf(coin_amount).doubleValue());
            c2cOrder.setRemark(remark);
            String remarks = "name:" + party.getUserName() + "code:" + party.getUserCode() + "direction:" + direction + "orderNo:" + orderNo;
            this.c2cOrderService.saveOpen(c2cOrder, remarks);
            data.put("order_no", c2cOrder.getOrderNo());
            resultObject.setData(data);
        } catch (BusinessException e) {
            log.error("用户" + direction + "执行异常1当前用户uid:" + party.getUserCode() + "当前用户名:" + party.getUserName() + "生成的订单号:" + orderNo);
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
            log.error("error:" + e.getMessage());
        }
        catch (YamiShopBindException e) {
            log.error("用户" + direction + "执行异常1当前用户uid:" + party.getUserCode() + "当前用户名:" + party.getUserName() + "生成的订单号:" + orderNo);
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
            log.error("error:" + e.getMessage());
        }
        catch (Exception t) {
            log.error("用户" + direction + "执行异常2当前用户uid:" + party.getUserCode() + "当前用户名:" + party.getUserName() + "生成的订单号:" + orderNo);
            resultObject.setCode(1);
            resultObject.setMsg("程序错误");
            t.printStackTrace();
        } finally {
            if (lock) {
                C2cLock.remove(partyId);
            }
        }
        return resultObject;
    }

    /**
     * 支付完成
     */
    @RequestMapping("/api/c2cOrder!pay_finish.action")
    public Object pay_finish(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");
        String safe_password = request.getParameter("safe_password");
        Result resultObject=new Result();
        boolean lock = false;
        try {
            String partyId = SecurityUtils.getCurrentUserId();
            User party = userService.getById(partyId);
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
                throw new BusinessException("无权限");
            }
            if (! passwordEncoder.matches(safe_password,party.getSafePassword())) {
                throw new BusinessException("资金密码错误");
            }
            if (!party.isEnabled()) {
                 throw  new BusinessException("用户已锁定");
            }
            C2cOrder order = this.c2cOrderService.get(order_no);
            if (null == order) {
                throw new BusinessException("订单不存在");
            }
            if (partyId.equals(order.getPartyId())) {
                // 用户操作
                if (!"buy".equals(order.getDirection())) {
                    throw new BusinessException("用户不能支付卖单");
                }
            } else if (partyId.equals(order.getC2cUserPartyId())) {
                // 承兑商操作
                if (!"sell".equals(order.getDirection())) {
                    throw new BusinessException("承兑商不能支付买单");
                }
            } else {
                throw new BusinessException("无权限");
            }
            if (!"0".equals(order.getState())) {
                throw new BusinessException("订单不是未付款状态");
            }
            if (!C2cOrderLock.add(order_no)) {
                throw new BusinessException(1, "请稍后再试");
            }
            lock = true;
            order.setState("1");
            order.setPayTime(new Date());
            this.c2cOrderService.updateById(order);
        } catch (BusinessException e) {
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode(1);
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                C2cOrderLock.remove(order_no);
            }
        }
        return resultObject;
    }

    /**
     * 订单放行
     */
    @RequestMapping("/api/c2cOrder!order_pass.action")
    public Object order_pass(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");
        String safe_password = request.getParameter("safe_password");
        Result resultObject = new Result();
        boolean lock = false;
        try {
            String partyId =SecurityUtils.getCurrentUserId();
            if (null == partyId) {
                throw new BusinessException("请重新登录");
            }
            User party =userService.getById(partyId);
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
                throw new BusinessException("无权限");
            }
            if (!C2cOrderLock.add(order_no)) {
                throw new BusinessException(1, "请稍后再试");
            }
            lock = true;
            if (!passwordEncoder.matches(safe_password, party.getSafePassword())) {
                throw new BusinessException("资金密码错误");
            }
            if (!party.isEnabled()) {
                resultObject.setCode(506);
                resultObject.setMsg("用户已锁定");
                return resultObject;
            }
            C2cOrder order = this.c2cOrderService.get(order_no);
            if (null == order || !order.getPartyId().equals(SecurityUtils.getCurrentUserId())) {
                throw new BusinessException("订单不存在");
            }
            if ("0".equals(order.getState())) {
                throw new BusinessException("待付款的订单无法放行");
            }
            if ("3".equals(order.getState())) {
                throw new BusinessException("订单已完成，无法放行");
            }
            if ("4".equals(order.getState())) {
                throw new BusinessException("订单已取消，无法放行");
            }
            this.c2cOrderService.saveOrderPass(order);
        } catch (BusinessException e) {
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode(1);
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        } finally {
            if (lock) {
                ThreadUtils.sleep(100);
                C2cOrderLock.remove(order_no);
            }
        }
        return resultObject;
    }

    /**
     * 取消订单
     */
    @RequestMapping("/api/c2cOrder!order_cancel.action")
    public Object order_cancel(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");
        String remark = request.getParameter("remark");
        Result resultObject = new Result();
        boolean lock = false;
        try {
            String partyId = SecurityUtils.getCurrentUserId();
            User party =userService.getById(partyId);
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRoleName())) {
                throw new BusinessException("无权限");
            }
            if (!party.isEnabled()) {
                resultObject.setCode(506);
                resultObject.setMsg("用户已锁定");
                return resultObject;
            }
            C2cOrder order = this.c2cOrderService.get(order_no);
            if (null == order || !order.getPartyId().equals(SecurityUtils.getCurrentUserId())) {
                throw new BusinessException("订单不存在");
            }
            if (!C2cOrderLock.add(order_no)) {
                throw new BusinessException(1, "请稍后再试");
            }
            lock = true;
            if (StringUtils.isEmptyString(remark)) {
                throw new BusinessException("请选择取消理由");
            }
            order.setRemark("订单号:"+order.getOrderNo()+"用户手动取消订单:"+remark);
            // 用户不能取消卖单，承兑商不能取消买单
            if (partyId.equals(order.getPartyId())) {
                // 用户操作
                if (!"buy".equals(order.getDirection())) {
                    throw new BusinessException("用户不能取消卖单");
                }

                this.c2cOrderService.saveOrderCancel(order, "user");

            } else if (partyId.equals(order.getC2cUserPartyId())) {
                // 承兑商操作
                if (!"sell".equals(order.getDirection())) {
                    throw new BusinessException("承兑商不能取消买单");
                }
                this.c2cOrderService.saveOrderCancel(order, "c2c_user");
            } else {
                throw new BusinessException("无权限");
            }

        } catch (Exception e) {
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
        }finally {
            if (lock) {
                ThreadUtils.sleep(100);
                C2cOrderLock.remove(order_no);
            }
        }
        return resultObject;
    }

    /**
     * 获取 用户订单 列表
     */
    @RequestMapping("/api/c2cOrder!list.action")
    public Result list(HttpServletRequest request) {
        String page_no = request.getParameter("page_no");
        String direction = request.getParameter("direction");
        String state = request.getParameter("state");
        Result resultObject = new Result();
        String language = request.getParameter("language");
        try {
            String partyId = SecurityUtils.getCurrentUserId();
            if (StringUtils.isNullOrEmpty(page_no)) {
                page_no = "1";
            }
            if (!StringUtils.isInteger(page_no)) {
                throw new BusinessException("页码不是整数");
            }
            if (Integer.valueOf(page_no).intValue() <= 0) {
                throw new BusinessException("页码不能小于等于0");
            }
            int page_no_int = Integer.valueOf(page_no).intValue();
            if (StringUtils.isNotEmpty(direction) && !Arrays.asList("buy", "sell").contains(direction)) {
                throw new BusinessException("买卖方式不正确");
            }
            if (StringUtils.isNotEmpty(state) && !Arrays.asList("0", "1", "2", "3", "4", "5").contains(state)) {
                throw new BusinessException("订单状态不正确");
            }
            List<String> directions=new ArrayList<>();
            if (!StringUtils.isNotEmpty(direction)){
                directions.add("buy");
                directions.add("sell");
            }
            else {
                directions.add(direction);
            }
            Page page = this.c2cOrderService.pagedQuery(page_no_int,20, directions, state, partyId, false);
            if (null == page) {
                resultObject.setCode(0);
                resultObject.setData(new ArrayList<Map<String, Object>>());
            } else {
                for (Map<String, Object> map : (List<Map<String, Object>>) page.getRecords()) {
                    if (null == map.get("c2c_user_head_img")) {
                        map.put("c2c_user_head_img", "");
                    } else {
                        String headImg = map.get("c2c_user_head_img").toString();
                        map.put("c2c_user_head_img", awsS3OSSFileService.getUrl(headImg));
                    }
                    c2cTranslateService.translateOrder(map, language);
                }
                List<String> nos = new ArrayList<String>();
                for (Map<String, Object> map : (List<Map<String, Object>>) page.getRecords()) {
                    nos.add(map.get("order_no").toString());
                }
                Map<String, Integer> unreadMsgs = this.otcOnlineChatMessageService.unreadMsgsApi(nos);
                for (Map<String, Object> map : (List<Map<String, Object>>) page.getRecords()) {
                    String orderNo = map.get("order_no").toString();
                    if (unreadMsgs.containsKey(orderNo)) {
                        map.put("unread_msg", unreadMsgs.get(orderNo));
                    }

                    String curCreateTimeStr = (String)map.get("create_time");
                    if (StrUtil.isNotBlank(curCreateTimeStr)) {
                        Date curCreateTime = DateUtils.toDate(curCreateTimeStr, DateUtils.DF_yyyyMMddHHmmss);
                        //Date curShowCreatTime = DateTimeTools.transferToShowTime(curCreateTime);

                        map.put("create_time", curCreateTime);
                    }
                }

                resultObject.setCode(0);
                resultObject.setData(page.getRecords());
            }
        } catch (BusinessException e) {
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode(1);
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 获取 订单 详情
     */
    @RequestMapping("/api/c2cOrder!get.action")
    public Object get(HttpServletRequest request) {
        String order_no = request.getParameter("order_no");
        String language = request.getParameter("language");
        Result resultObject = new Result();
        try {
            C2cOrder c2cOrder = this.c2cOrderService.get(order_no);
            if (null == c2cOrder) {
                throw new BusinessException("订单不存在");
            }
            List<String> nos = new ArrayList<String>();
            nos.add(c2cOrder.getOrderNo());
            Map<String, Integer> unreadMsgs = this.otcOnlineChatMessageService.unreadMsgsApi(nos);
            Integer unreadMsg = unreadMsgs.get(c2cOrder.getOrderNo());
            if (null == unreadMsg) {
                c2cOrder.setUnreadMsg(0);
            } else {
                c2cOrder.setUnreadMsg(unreadMsg.intValue());
            }
            if (StringUtils.isNotEmpty(c2cOrder.getC2cUserHeadImg())) {
                String path =awsS3OSSFileService.getUrl(c2cOrder.getC2cUserHeadImg());
                c2cOrder.setC2cUserHeadImg(path);
            }
            if (StringUtils.isNotEmpty(c2cOrder.getMethodImg())) {
                String path =awsS3OSSFileService.getUrl(c2cOrder.getMethodImg());
                c2cOrder.setMethodImg(path);
            }

            // 多语言
            c2cOrder = this.c2cTranslateService.translateOrder(c2cOrder, language);

            long nowTimestamp = (new Date()).getTime();
            long createTimestamp = c2cOrder.getCreateTime().getTime();
            long autoCancelSeconds = 0;
            long remainSeconds = 0;

            if ("0".equals(c2cOrder.getState())) {
                autoCancelSeconds = c2cOrder.getExpireTime() * 60 * 1000 - (nowTimestamp - createTimestamp);
                remainSeconds = c2cOrder.getExpireTime() * 60 * 1000 * 2 - (nowTimestamp - createTimestamp);
            } else if ("1".equals(c2cOrder.getState())) {
                long payTimestamp = c2cOrder.getPayTime().getTime();
                autoCancelSeconds = c2cOrder.getExpireTime() * 60 * 1000 - (nowTimestamp - createTimestamp);
                remainSeconds = c2cOrder.getExpireTime() * 60 * 1000 - (nowTimestamp - payTimestamp);
            }

            c2cOrder.setAutoCancelTimeRemain((int) (autoCancelSeconds <= 0 ? 0 : autoCancelSeconds / 1000));
            c2cOrder.setExpireTimeRemain((int) (remainSeconds <= 0 ? 0 : remainSeconds / 1000));
            c2cOrder.setFailureMsg(c2cOrder.getRemark());
            resultObject.setData(c2cOrder);
        } catch (BusinessException e) {
            resultObject.setCode(1);
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode(1);
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }

        return resultObject;
    }
}
