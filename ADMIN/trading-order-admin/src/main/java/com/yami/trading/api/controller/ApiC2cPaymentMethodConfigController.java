package com.yami.trading.api.controller;

import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cTranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
public class ApiC2cPaymentMethodConfigController {
    private static final List<String> ALLOWED_CRYPTO_METHODS = Arrays.asList("USDT", "USDC", "BTC", "ETH");
    private static final String DEFAULT_PARAM_NAME = "Wallet Address";

    @Autowired
    C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    C2cTranslateService c2cTranslateService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    private String normalizeMethodName(String methodName) {
        return methodName == null ? "" : methodName.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isAllowedCryptoMethod(String methodName) {
        return ALLOWED_CRYPTO_METHODS.contains(normalizeMethodName(methodName));
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
        Map<String, C2cPaymentMethodConfig> finalMap = new LinkedHashMap<>();

        for (String methodName : ALLOWED_CRYPTO_METHODS) {
            C2cPaymentMethodConfig config = null;
            for (C2cPaymentMethodConfig realConfig : realMap.values()) {
                if (realConfig != null && methodName.equals(normalizeMethodName(realConfig.getMethodName()))) {
                    config = realConfig;
                    break;
                }
            }
            if (config == null) {
                config = buildVirtualCryptoConfig(methodName);
            }
            finalMap.put(config.getUuid(), config);
        }

        return finalMap;
    }

    private C2cPaymentMethodConfig resolveConfig(String idOrMethodName) {
        C2cPaymentMethodConfig config = this.c2cPaymentMethodConfigService.getById(idOrMethodName);
        if (config != null && config.getType() == 2 && isAllowedCryptoMethod(config.getMethodName())) {
            return config;
        }
        return buildVirtualCryptoConfig(idOrMethodName);
    }

    @RequestMapping("/api/c2cPaymentMethodConfig!list.action")
    public Object list(HttpServletRequest request) {
        String language = request.getParameter("language");
        log.info("c2cPaymentMethodConfig:{}", language);
        Map<String, C2cPaymentMethodConfig> methodConfigMap = getAllowedCryptoConfigMap();
        Map<String, String> retPayMap = new LinkedHashMap<>();
        for (Map.Entry<String, C2cPaymentMethodConfig> entry : methodConfigMap.entrySet()) {
            String id = entry.getKey();
            C2cPaymentMethodConfig configEntity = entry.getValue();

            String payMethodNameLangKey = "pay.param." + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + configEntity.getUuid();
            C2cTranslate trans = this.c2cTranslateService.get(id, payMethodNameLangKey, language);
            if (trans != null && StringUtils.isNullOrEmpty(trans.getTranslate())) {
                retPayMap.put(id, trans.getTranslate());
            } else {
                retPayMap.put(id, configEntity.getMethodName());
            }
        }

        return Result.succeed(retPayMap);
    }

    @RequestMapping("/api/c2cPaymentMethodConfig!get.action")
    public Object get(HttpServletRequest request) {
        String id = request.getParameter("id");
        String language = request.getParameter("language");
        C2cPaymentMethodConfig config = resolveConfig(id);
        if (config == null) {
            throw new BusinessException("支付方式模板不存在");
        }
        if (StringUtils.isNotEmpty(config.getMethodImg())) {
            String path = awsS3OSSFileService.getUrl(config.getMethodImg());
            config.setMethodImg(path);
        }

        C2cPaymentMethodConfig payConfig = this.c2cTranslateService.translatePmc(config, language);
        if (payConfig == null) {
            payConfig = config;
        }
        if (StringUtils.isEmpty(payConfig.getParamName1())) {
            payConfig.setParamName1(DEFAULT_PARAM_NAME);
        }
        if (StringUtils.isEmpty(payConfig.getMethodName())) {
            payConfig.setMethodName(config.getMethodName());
        }
        return Result.succeed(payConfig);
    }
}
