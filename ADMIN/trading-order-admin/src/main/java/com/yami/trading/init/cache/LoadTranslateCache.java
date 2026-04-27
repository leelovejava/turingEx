package com.yami.trading.init.cache;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.common.constants.PayMethodTypeEnum;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.util.Json;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cTranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * C2cTranslate 表加字段后，迁移旧记录，根据规则为新加字段填充值；
 * 所有盘口迁移完后，本服务可以关闭掉。
 *
 * @author caster
 * @since 2023/11/18
 **/
@Service
@Slf4j
public class LoadTranslateCache {
    @Autowired
    C2cPaymentMethodConfigService c2cPaymentMethodConfigService;

    @Autowired
    C2cTranslateService c2cTranslateService;

    @Autowired
    private RedisTemplate redisTemplate;

    public void loadData() {
        try {
            transferOldData();
        } catch (Exception e) {
            log.error("------> LoadTranslateCache.loadData 执行 transferOldData 方法报错: ", e);
        }

        QueryWrapper<C2cTranslate> allTranslateRecordWrapper = new QueryWrapper<C2cTranslate>();
        List<C2cTranslate> allTranslateRecordList = c2cTranslateService.list(allTranslateRecordWrapper);

        for (C2cTranslate oneTranslateEntity : allTranslateRecordList) {
            redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + oneTranslateEntity.getLangKey() +
                    ":" + oneTranslateEntity.getLanguage(), Json.toJsonString(oneTranslateEntity));
        }
    }

    /**
     * 翻译功能改造，t_c2c_translate 表添加新字段：lang_key 后，对旧数据做迁移
     */
    private void transferOldData() {
        // 1. 迁移支付模板自定义参数多语言
        QueryWrapper<C2cPaymentMethodConfig> allPayConfigWrapper = new QueryWrapper<C2cPaymentMethodConfig>();
        List<C2cPaymentMethodConfig> allPayConfigList = c2cPaymentMethodConfigService.list(allPayConfigWrapper);

        String payParamLangKeyPrefix = "pay.param.";
        for (C2cPaymentMethodConfig oneConfigEntity : allPayConfigList) {
            List<C2cTranslate> c2cPaymentMethods = c2cTranslateService.list(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getPaymentMethodId, oneConfigEntity.getUuid()));
            for (C2cTranslate oneTranslate : c2cPaymentMethods) {
                if (StrUtil.isNotBlank(oneTranslate.getLangKey())) {
                    // 已做过迁移的，跳过，必要情况下可以强制再迁移一次
                    continue;
                }

                if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getMethodName())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName1())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName2())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName3())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_3.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName4())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_4.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName5())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_5.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName6())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_6.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName7())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_7.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName8())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_8.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName9())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_9.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName10())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_10.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName11())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_11.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName12())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_12.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName13())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_13.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName14())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_14.getCode() + "." + oneConfigEntity.getUuid());
                } else if (oneTranslate.getContent().equalsIgnoreCase(oneConfigEntity.getParamName15())) {
                    oneTranslate.setLangKey(payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_15.getCode() + "." + oneConfigEntity.getUuid());
                } else {
                    log.error("-------> LoadTranslateCache.transferOldData translate 当前记录:{} 的 content 值不正确:{}", oneTranslate.getUuid(), oneTranslate.getContent());
                    continue;
                }

                c2cTranslateService.update(oneTranslate);
            }
        }

        // 2. 迁移支付方式多语言
        String payTypeLangKeyPrefix = "pay.type.";
        List<C2cTranslate> list1 = c2cTranslateService.list(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getPaymentMethodId, "0"));
        List<C2cTranslate> list2 = c2cTranslateService.list(Wrappers.<C2cTranslate>query().lambda().isNull(C2cTranslate::getPaymentMethodId));
        List<C2cTranslate> list3 = c2cTranslateService.list(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getPaymentMethodId, ""));

        List<C2cTranslate> payTypeTranslateList = new ArrayList<>();
        payTypeTranslateList.addAll(list1);
        payTypeTranslateList.addAll(list2);
        payTypeTranslateList.addAll(list3);

        for (C2cTranslate oneTranslate : payTypeTranslateList) {
            if (StrUtil.isNotBlank(oneTranslate.getLangKey())) {
                // 已做过迁移的，跳过，必要情况下可以强制再迁移一次
                continue;
            }

            PayMethodTypeEnum payType = PayMethodTypeEnum.nameOf(oneTranslate.getContent());
            if (payType == null) {
                log.error("-------> LoadTranslateCache.transferOldData translate 当前记录:{} 的 content 值不正确:{}", oneTranslate.getUuid(), oneTranslate.getContent());
                continue;
            }

            oneTranslate.setLangKey(payTypeLangKeyPrefix + payType.getCode());
            oneTranslate.setPaymentMethodId("0");

            c2cTranslateService.update(oneTranslate);
        }
    }
}
