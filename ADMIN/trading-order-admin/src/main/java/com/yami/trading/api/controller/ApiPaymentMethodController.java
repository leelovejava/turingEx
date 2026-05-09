package com.yami.trading.api.controller;

import cn.hutool.core.util.StrUtil;
import com.qiniu.util.StringUtils;
import com.yami.trading.api.model.AddPaymentMethodModel;
import com.yami.trading.api.model.IdModel;
import com.yami.trading.api.model.PaymentMethodModel;
import com.yami.trading.api.service.UserCacheService;
import com.yami.trading.bean.constans.UserConstants;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cPaymentMethodService;
import com.yami.trading.service.c2c.C2cTranslateService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
@RequestMapping("api/paymentMethod")
@Api(tags = "Payment method")
public class ApiPaymentMethodController {
    /**
     * 允许支付方式
     */
    private static final List<String> ALLOWED_CRYPTO_METHODS = Arrays.asList("USDT", "USDC", "BTC", "ETH");
    private static final Set<String> ALLOWED_CRYPTO_METHOD_SET = new HashSet<>(ALLOWED_CRYPTO_METHODS);
    private static final String DEFAULT_PARAM_NAME = "Wallet Address";

    @Autowired
    private C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    private C2cPaymentMethodService c2cPaymentMethodService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private LogService logService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private C2cTranslateService c2cTranslateService;
    @Autowired
    private C2cAdvertService c2cAdvertService;

    private String normalizeMethodName(String methodName) {
        return methodName == null ? "" : methodName.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isAllowedCryptoMethod(String methodName) {
        return ALLOWED_CRYPTO_METHOD_SET.contains(normalizeMethodName(methodName));
    }

    private boolean isChinese(String language) {
        return "zh-CN".equals(language);
    }

    private void throwLocalized(String language, String zhMessage, String enMessage) {
        throw new YamiShopBindException(isChinese(language) ? zhMessage : enMessage);
    }

    private void validateRemark(String language, String remark) {
        if (remark != null && remark.length() > 100) {
            throwLocalized(language, "备注长度大于100", "Note length greater than 100");
        }
    }

    private void validateParamLength(String language, String paramName, String paramValue, int maxLength, int paramIndex) {
        if (StrUtil.isNotEmpty(paramValue) && paramValue.length() > maxLength) {
            if (isChinese(language)) {
                throw new YamiShopBindException(paramName + "长度大于" + maxLength);
            }
            throw new YamiShopBindException("Parameter value " + paramIndex + " length greater than " + maxLength);
        }
    }

    private C2cPaymentMethodConfig buildVirtualCryptoConfig(String methodName) {
        String normalizedName = normalizeMethodName(methodName);
        if (!isAllowedCryptoMethod(normalizedName)) {
            return null;
        }
        C2cPaymentMethodConfig config = new C2cPaymentMethodConfig();
        config.setUuid(normalizedName);
        config.setType(2);
        config.setMethodType(2);
        config.setMethodName(normalizedName);
        config.setMethodImg("");
        config.setParamName1(DEFAULT_PARAM_NAME);
        return config;
    }

    private Map<String, C2cPaymentMethodConfig> getAllowedCryptoConfigMap() {
        Map<String, C2cPaymentMethodConfig> realMap = this.c2cPaymentMethodConfigService.getC2cPMethodConfigMap();
        Map<String, C2cPaymentMethodConfig> methodNameMap = new HashMap<>();
        for (C2cPaymentMethodConfig config : realMap.values()) {
            if (config != null && isAllowedCryptoMethod(config.getMethodName())) {
                methodNameMap.put(normalizeMethodName(config.getMethodName()), config);
            }
        }

        Map<String, C2cPaymentMethodConfig> finalMap = new LinkedHashMap<>();
        for (String methodName : ALLOWED_CRYPTO_METHODS) {
            C2cPaymentMethodConfig config = methodNameMap.get(methodName);
            if (config == null) {
                config = buildVirtualCryptoConfig(methodName);
            }
            finalMap.put(config.getUuid(), config);
        }
        return finalMap;
    }

    private C2cPaymentMethodConfig resolvePaymentMethodConfig(String idOrMethodName) {
        C2cPaymentMethodConfig config = c2cPaymentMethodConfigService.getById(idOrMethodName);
        if (config != null && config.getType() == 2 && isAllowedCryptoMethod(config.getMethodName())) {
            return config;
        }
        return buildVirtualCryptoConfig(idOrMethodName);
    }

    /**
     * api/api/paymentMethod/list
     *
     * @param paymentMethodModel
     * @return
     */
    @ApiOperation(value = "Get payment method list")
    @RequestMapping("list")
    public Result<Map<String, String>> getPaymentMethod(@Valid PaymentMethodModel paymentMethodModel) {
        String language = paymentMethodModel.getLanguage();
        Map<String, C2cPaymentMethodConfig> methodConfigMap = getAllowedCryptoConfigMap();
        Map<String, String> retPayMap = new LinkedHashMap<>();
        for (Map.Entry<String, C2cPaymentMethodConfig> entry : methodConfigMap.entrySet()) {
            String id = entry.getKey();
            C2cPaymentMethodConfig configEntity = entry.getValue();
            String payMethodNameLangKey = "pay.param." + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + configEntity.getUuid();
            C2cTranslate trans = c2cTranslateService.get(id, payMethodNameLangKey, language);
            if (trans != null && StrUtil.isNotBlank(trans.getTranslate())) {
                retPayMap.put(id, trans.getTranslate());
            } else {
                retPayMap.put(id, configEntity.getMethodName());
            }
        }
        return Result.succeed(retPayMap);
    }

    @ApiOperation(value = "Add payment method")
    @RequestMapping("add")
    public Result<?> add(@Valid AddPaymentMethodModel model, @RequestParam(required = false) String language) {
        if (StringUtils.isNullOrEmpty(model.getMethod_config_id())) {
            throwLocalized(language, "支付方式模板不正确", "Payment method template is incorrect");
        }
        if (StringUtils.isNullOrEmpty(model.getReal_name())) {
            throwLocalized(language, "真实姓名必填", "Real name required");
        }
        if (StringUtils.isNullOrEmpty(model.getParam_value1())) {
            throwLocalized(language, "参数1必填", "Parameter value 1 is required");
        }
        if (model.getReal_name().length() > 20) {
            throwLocalized(language, "真实姓名长度大于20", "Real name length greater than 20");
        }
        validateRemark(language, model.getRemark());

        C2cPaymentMethodConfig methodConfig = resolvePaymentMethodConfig(model.getMethod_config_id());
        if (methodConfig == null) {
            throwLocalized(language, "支付方式模板不存在", "Payment method template does not exist");
        }
        if (!isAllowedCryptoMethod(methodConfig.getMethodName())) {
            throwLocalized(language, "支付方式模板不正确", "Payment method template is incorrect");
        }

        validateParamLength(language, methodConfig.getParamName1(), model.getParam_value1(), 80, 1);
        validateParamLength(language, methodConfig.getParamName2(), model.getParam_value2(), 80, 2);

        String partyId = SecurityUtils.getCurrentUserId();
        if (partyId == null) {
            throwLocalized(language, "请重新登录", "please login again");
        }

        Map<String, C2cPaymentMethod> methodMap = c2cPaymentMethodService.getByPartyId(partyId);
        Object obj = this.sysparaService.find("c2c_payment_method_count_max");
        if (obj != null && methodMap.size() >= Integer.parseInt(this.sysparaService.find("c2c_payment_method_count_max").getSvalue())) {
            throwLocalized(language, "支付方式数量已达上限", "The number of payment methods has reached the upper limit");
        }

        C2cPaymentMethod method = new C2cPaymentMethod();
        method.setPartyId(partyId);
        method.setMethodConfigId(methodConfig.getUuid());
        method.setMethodType(methodConfig.getMethodType());
        method.setType(2);
        method.setMethodName(methodConfig.getMethodName());
        method.setMethodImg(methodConfig.getMethodImg());
        method.setRealName(model.getReal_name());
        method.setParamName1(StrUtil.isNotBlank(methodConfig.getParamName1()) ? methodConfig.getParamName1() : DEFAULT_PARAM_NAME);
        method.setParamValue1(model.getParam_value1());
        method.setParamName2(methodConfig.getParamName2());
        method.setParamValue2(model.getParam_value2());
        method.setParamName3(methodConfig.getParamName3());
        method.setParamValue3(model.getParam_value3());
        method.setParamName4(methodConfig.getParamName4());
        method.setParamValue4(model.getParam_value4());
        method.setParamName5(methodConfig.getParamName5());
        method.setParamValue5(model.getParam_value5());
        method.setParamName6(methodConfig.getParamName6());
        method.setParamValue6(model.getParam_value6());
        method.setParamName7(methodConfig.getParamName7());
        method.setParamValue7(model.getParam_value7());
        method.setParamName8(methodConfig.getParamName8());
        method.setParamValue8(model.getParam_value8());
        method.setParamName9(methodConfig.getParamName9());
        method.setParamValue9(model.getParam_value9());
        method.setParamName10(methodConfig.getParamName10());
        method.setParamValue10(model.getParam_value10());
        method.setParamName11(methodConfig.getParamName11());
        method.setParamValue11(model.getParam_value11());
        method.setParamName12(methodConfig.getParamName12());
        method.setParamValue12(model.getParam_value12());
        method.setParamName13(methodConfig.getParamName13());
        method.setParamValue13(model.getParam_value13());
        method.setParamName14(methodConfig.getParamName14());
        method.setParamValue14(model.getParam_value14());
        method.setParamName15(methodConfig.getParamName15());
        method.setParamValue15(model.getParam_value15());
        method.setQrcode(model.getQrcode());
        method.setRemark(model.getRemark());

        String id = c2cPaymentMethodService.saveData(method);
        C2cPaymentMethod methodSaved = c2cPaymentMethodService.getC2cPaymentMethod(id);
        if (methodSaved != null) {
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_ID + methodSaved.getUuid(), methodSaved);
            Map<String, C2cPaymentMethod> map = (Map<String, C2cPaymentMethod>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_PARTY_ID + methodSaved.getPartyId());
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(methodSaved.getUuid(), methodSaved);
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_PARTY_ID + methodSaved.getPartyId(), map);
            Map<String, String> map1 = (Map<String, String>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_ID_TYPE);
            if (map1 == null) {
                map1 = new ConcurrentHashMap<>();
            }
            map1.put(methodSaved.getUuid(), String.valueOf(methodSaved.getMethodType()));
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_ID_TYPE + methodSaved.getUuid(), map1);
        }

        String log = MessageFormat.format(
                "ip:{0}, add payment method, id:{1}, partyId:{2}, methodConfigId:{3}, methodType:{4}, methodName:{5}",
                IPHelper.getIpAddr(), method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName());
        User user = userCacheService.currentUser();
        logService.saveLog(user, log, UserConstants.LOG_CATEGORY_C2C);
        return Result.succeed(null);
    }

    @ApiOperation(value = "Update payment method")
    @RequestMapping("update")
    public Result<?> update(@Valid AddPaymentMethodModel model, @RequestParam(required = false) String language) {
        if (StringUtils.isNullOrEmpty(model.getReal_name())) {
            throwLocalized(language, "真实姓名必填", "Real name required");
        }
        if (StringUtils.isNullOrEmpty(model.getParam_value1())) {
            throwLocalized(language, "参数1必填", "Parameter value 1 is required");
        }
        if (model.getReal_name().length() > 20) {
            throwLocalized(language, "真实姓名长度大于20", "Real name length greater than 20");
        }
        validateRemark(language, model.getRemark());

        C2cPaymentMethod method = this.c2cPaymentMethodService.getById(model.getId());
        if (method == null) {
            throwLocalized(language, "支付方式不存在", "Payment method does not exist");
        }
        if (method.getType() != 2 || !isAllowedCryptoMethod(method.getMethodName())) {
            throwLocalized(language, "支付方式不正确", "Payment method is incorrect");
        }

        String partyId = SecurityUtils.getCurrentUserId();
        if (!method.getPartyId().equals(partyId)) {
            throwLocalized(language, "支付方式不匹配该用户", "The payment method does not match this user");
        }

        String log = MessageFormat.format(
                "ip:{0}, update payment method, id:{1}, partyId:{2}, methodConfigId:{3}, methodType:{4}, methodName:{5}",
                IPHelper.getIpAddr(), method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName());

        method.setRealName(model.getReal_name());
        method.setParamValue1(model.getParam_value1());
        method.setParamValue2(model.getParam_value2());
        method.setParamValue3(model.getParam_value3());
        method.setParamValue4(model.getParam_value4());
        method.setParamValue5(model.getParam_value5());
        method.setParamValue6(model.getParam_value6());
        method.setParamValue7(model.getParam_value7());
        method.setParamValue8(model.getParam_value8());
        method.setParamValue9(model.getParam_value9());
        method.setParamValue10(model.getParam_value10());
        method.setParamValue11(model.getParam_value11());
        method.setParamValue12(model.getParam_value12());
        method.setParamValue13(model.getParam_value13());
        method.setParamValue14(model.getParam_value14());
        method.setParamValue15(model.getParam_value15());
        method.setQrcode(model.getQrcode());
        method.setRemark(model.getRemark());

        boolean state = this.c2cPaymentMethodService.updateById(method);
        if (state) {
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_ID + method.getUuid(), method);
            Map<String, C2cPaymentMethod> map = (Map<String, C2cPaymentMethod>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_PARTY_ID + method.getPartyId());
            if (map == null) {
                map = new ConcurrentHashMap<>();
            } else {
                map.remove(method.getUuid());
            }
            map.put(method.getUuid(), method);
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_PARTY_ID + method.getPartyId(), map);
            Map<String, String> map1 = (Map<String, String>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_ID_TYPE);
            if (map1 == null) {
                map1 = new ConcurrentHashMap<>();
            } else {
                map1.remove(method.getUuid());
            }
            map1.put(method.getUuid(), String.valueOf(method.getMethodType()));
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_ID_TYPE, map1);
        }

        User user = userCacheService.currentUser();
        logService.saveLog(user, log, UserConstants.LOG_CATEGORY_C2C);
        return Result.succeed(null);
    }

    @RequestMapping("myList")
    @ApiOperation("Get my payment method list")
    public Result<List<C2cPaymentMethod>> list(@RequestParam String language) {
        List<C2cPaymentMethod> list = new ArrayList<>();
        Map<String, C2cPaymentMethod> map = this.c2cPaymentMethodService.getByC2CPartyId(SecurityUtils.getCurrentUserId());
        if (map != null && !map.isEmpty()) {
            for (C2cPaymentMethod method : map.values()) {
                if (method == null || !isAllowedCryptoMethod(method.getMethodName())) {
                    continue;
                }
                if (com.yami.trading.common.util.StringUtils.isNotEmpty(method.getMethodImg())) {
                    String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + method.getMethodImg();
                    method.setMethodImg(path);
                }
                list.add(this.c2cTranslateService.translatePm(method, language));
            }
        }

        return Result.succeed(list);
    }

    @ApiOperation(value = "Delete my payment method")
    @RequestMapping("deletepPymentMethod")
    public Result<List<C2cPaymentMethod>> deletepPymentMethod(@Valid IdModel model) {
        c2cPaymentMethodService.removeById(model.getId());
        return Result.succeed(null);
    }
}
