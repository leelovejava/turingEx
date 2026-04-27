package com.yami.trading.service.c2c.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PayMethodTypeEnum;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.util.Json;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.c2c.C2cTranslateMapper;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cPaymentMethodService;
import com.yami.trading.service.c2c.C2cTranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class C2cTranslateServiceImpl extends ServiceImpl<C2cTranslateMapper,C2cTranslate>  implements C2cTranslateService {


    @Autowired
    private RedisTemplate  redisTemplate;

    @Autowired
    private C2cAdvertService c2cAdvertService;

    @Autowired
    C2cPaymentMethodService c2cPaymentMethodService;

    @Override
    public C2cTranslate get(String langKey, String language) {
        return get("0", langKey, language);
    }

    public C2cTranslate get(String id, String langKey, String language) {
        //Object json = redisTemplate.opsForValue().get(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + id + StringUtils.stringToUnicode(content) + language);
        Object json = redisTemplate.opsForValue().get(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + langKey + ":" + language);
        if (json == null) {
            C2cTranslate entity = getFromDb(langKey, language);
            if (entity == null) {
                return null;
            }

            entity.setLangKey(langKey);
            redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + langKey + ":" + entity.getLanguage(), Json.toJsonString(entity));
            return entity;
        }

        // return new Gson().fromJson(json.toString(), C2cTranslate.class);
        return Json.parseObject(json.toString(), C2cTranslate.class);
    }

    public C2cTranslate getFromDb(String langKey, String language) {
        if (StrUtil.isBlank(langKey)) {
            return null;
        }

        LambdaQueryWrapper query = null;
        if (StrUtil.isBlank(langKey)) {
            query = Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getLangKey, langKey.trim());
        } else {
            query = Wrappers.<C2cTranslate>query().lambda()
                    .eq(C2cTranslate::getLangKey, langKey.trim())
                    .eq(C2cTranslate::getLanguage, language.trim());
        }

        List<C2cTranslate> list = this.getBaseMapper().selectList(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public void saveC2cTranslate(C2cTranslate entity, String id) {
        save(entity);
        //redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + id + StringUtils.stringToUnicode(entity.getContent()) + entity.getLanguage(), new Gson().toJson(entity));
        redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + entity.getLangKey() + ":" + entity.getLanguage(), Json.toJsonString(entity));
    }

    public void update(C2cTranslate entity) {
        updateById(entity);
        if (null != entity) {
            //redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + StringUtils.stringToUnicode(entity.getContent()) + entity.getLanguage(), new Gson().toJson(entity));
            redisTemplate.opsForValue().set(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + entity.getLangKey() + ":" + entity.getLanguage(), Json.toJsonString(entity));
        }
    }

    @Override
    public void delete(String langKey, String language) {
        delete("0", langKey, language);
    }

    public void delete(String id, String langKey, String language) {
        C2cTranslate entity = this.get(id, langKey, language);
        if (entity != null) {
           removeById(entity);
			////this.redisHandler.remove(RedisKeys.C2C_TRANSLATE_ID + entity.getId().toString());
            //redisTemplate.delete(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + id + StringUtils.stringToUnicode(entity.getContent()) + entity.getLanguage());
            redisTemplate.delete(RedisKeys.C2C_TRANSLATE_CONTENT_LANGUAGE + langKey + ":" + entity.getLanguage());
        }
    }

    /**
     * 刷新中文名称为: content 值的多语言记录，所有多语言记录都是 content 值的不同语言表达方式
     *
     * @param id
     * @param langKey : 最新逻辑下变成了一个固定的多语言 key
     * @param langTransStr
     */
    public void saveTranslate(String id, String langKey, String langTransStr, String content) {
        if (StrUtil.isBlank(langKey) || StrUtil.isBlank(langTransStr)) {
            return;
        }
        if (StrUtil.isBlank(id) || id.equalsIgnoreCase("null")) {
            id = "0";
        }

        Date now = new Date();
        Map<String, String> langMap = Constants.LANGUAGE;
        for (String lang : langMap.keySet()) {
            C2cTranslate trans = this.get(id, langKey, lang);
            if (trans != null) {
                //delete(id, trans.getContent(), lang);
                delete(id, langKey, lang);
            }
        }

        // 前面 for 循环中已经删除过一次了，此处又进行批量删除一次 TODO
        // 对于 content 相同的所有记录都删除了
        remove(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getLangKey, langKey));
//        if (StringUtils.isNotEmpty(id)) {
//            remove(Wrappers.<C2cTranslate>query().lambda()
//                    .eq(C2cTranslate::getPaymentMethodId, id)
//                    .eq(C2cTranslate::getContent, langKey));
//        } else {
//            // 前面 for 循环中已经删除过一次了，此处又进行批量删除一次 TODO
//            // 对于 content 相同的所有记录都删除了
//            remove(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getContent, content));
//        }
        String[] ltStrArr = langTransStr.split("&&");
        for (int i = 0; i < ltStrArr.length; i++) {
            String ltStr = ltStrArr[i];
            if (StringUtils.isEmptyString(ltStr)) {
                continue;
            }

            String[] lt = ltStr.split("##");
            String language = lt[0];
            String translate = lt[1];
            if (StringUtils.isEmptyString(language) || StringUtils.isEmptyString(translate)) {
                continue;
            }

            C2cTranslate trans = this.get(id, langKey, language.trim());
            if (null == trans) {
                // 重建多语言记录和对应的缓存
                C2cTranslate ct = new C2cTranslate();
                ct.setLanguage(language);
                ct.setLangKey(langKey);
                ct.setContent(content);
                ct.setTranslate(translate);
                ct.setCreateTime(now);
                ct.setUpdateTime(now);
                ct.setPaymentMethodId(id);
//                if (StrUtil.isBlank(id) || id.equalsIgnoreCase("null")) {
//                    ct.setPaymentMethodId("");
//                } else {
//                    ct.setPaymentMethodId(id);
//                }

                saveC2cTranslate(ct, id);
            } else {
                // 前面都已经删除过了，正常情况下此处代码路径不会执行
                trans.setLangKey(langKey);
                trans.setContent(content);
                trans.setTranslate(translate);
                trans.setUpdateTime(now);
                update(trans);
            }
        }
    }

    /**
     * 复现出指定支付模板下指定自定义参数的多语言配置信息
     *
     * @param id
     * @param langKey
     * @return
     */
    @Override
    public String getTranslate(String id, String langKey) {
        String langTransStr = "";

        Map<String, String> langMap = Constants.LANGUAGE;
        // 提取这个支付模板记录下的所有多语言参数字段列表
        List<C2cTranslate> c2cPaymentMethods = list(Wrappers.<C2cTranslate>query().lambda().eq(C2cTranslate::getPaymentMethodId, id));

        Map<String,C2cTranslate> map = new HashMap<>();
        for (C2cTranslate c2cTranslate : c2cPaymentMethods) {
            //map.put(c2cTranslate.getContent() + c2cTranslate.getLanguage(), c2cTranslate);
            map.put(c2cTranslate.getLangKey() + ":" + c2cTranslate.getLanguage(), c2cTranslate);
        }
        for (String lang : langMap.keySet()) {
            // 从缓存中提取该语言类型，该字段对应的一条多语言记录
            C2cTranslate trans = this.get(id, langKey, lang);
            if (trans == null) {
                // 如果缓存中没有对应的记录，就从数据库中提取
                trans = map.get(langKey + ":" + lang);
            }
            if (StringUtils.isEmptyString(langTransStr)) {
                if (null != trans) {
                    langTransStr = lang + "##" + trans.getTranslate();
                }
            } else {
                if (null != trans) {
                    langTransStr += "&&" + lang + "##" + trans.getTranslate();
                }
            }
        }

        return langTransStr;
    }

    public String getTranslate(String langKey) {
        return getTranslate("0", langKey);
    }

    public C2cPaymentMethodConfig translatePmc(C2cPaymentMethodConfig config, String language) {
        Map<String, String> data = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        String name = data.get(String.valueOf(config.getMethodType()));
        PayMethodTypeEnum payType = PayMethodTypeEnum.codeOf(config.getMethodType());
        if (null != payType) {
            String payTypeLangKey = "pay.type." + config.getMethodType();
            C2cTranslate trans = this.get(payTypeLangKey, language);
            if (null != trans) {
                config.setMethodTypeName(trans.getTranslate());
            }
        }

        // pay.param.{paramCode}.{payId};
        String payParamLangKeyPrefix = "pay.param.";
        String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + config.getUuid();
        if (StringUtils.isNotEmpty(config.getMethodName())) {
            C2cTranslate trans = this.get(payMethodNameLangKey, language);
            if (null != trans) {
                config.setMethodName(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName1())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName1(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName2())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName2(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName3())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_3.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName3(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName4())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_4.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName4(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName5())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_5.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName5(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName6())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_6.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName6(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName7())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_7.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName7(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName8())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_8.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName8(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName9())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_9.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName9(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName10())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_10.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName10(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName11())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_11.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName11(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName12())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_12.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName12(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName13())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_13.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName13(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName14())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_14.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName14(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(config.getParamName15())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_15.getCode() + "." + config.getUuid();
            C2cTranslate trans = this.get(config.getUuid(), paramLangKey, language);
            if (null != trans) {
                config.setParamName15(trans.getTranslate());
            }
        }

        return config;
    }

    public C2cPaymentMethod translatePm(C2cPaymentMethod cpm, String language) {
        Map<String, String> data = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        String name = data.get(String.valueOf(cpm.getMethodType()));
        PayMethodTypeEnum payType = PayMethodTypeEnum.codeOf(cpm.getMethodType());
        if (null != payType) {
            String payTypeLangKey = "pay.type." + cpm.getMethodType();
            C2cTranslate trans = this.get(payTypeLangKey, language);
            if (null != trans) {
                cpm.setMethodTypeName(trans.getTranslate());
            }
        }

        // pay.param.{paramCode}.{payId};
        String payParamLangKeyPrefix = "pay.param.";
        String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + cpm.getMethodConfigId();
        if (StringUtils.isNotEmpty(cpm.getMethodName())) {
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), payMethodNameLangKey, language);
            if (null != trans) {
                cpm.setMethodName(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName1())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName1(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName2())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName2(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName3())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_3.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName3(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName4())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_4.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName4(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName5())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_5.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName5(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName6())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_6.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName6(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName7())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_7.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName7(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName8())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_8.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName8(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName9())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_9.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName9(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName10())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_10.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName10(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName11())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_11.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName11(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName12())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_12.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName12(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName13())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_13.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName13(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName14())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_14.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName14(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(cpm.getParamName15())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_15.getCode() + "." + cpm.getMethodConfigId();
            C2cTranslate trans = this.get(cpm.getMethodConfigId(), paramLangKey, language);
            if (null != trans) {
                cpm.setParamName15(trans.getTranslate());
            }
        }

        return cpm;
    }

//    public C2cOrder translateOrder(C2cOrder order, String language) {
//
//        Map<String, String> data = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
//        String name = data.get(String.valueOf(order.getMethodType()));
//        if (null != name) {
//            C2cTranslate trans = this.get(name, language);
//            if (null != trans) {
//                order.setMethodTypeName(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getMethodName())) {
//            C2cTranslate trans = this.get(order.getMethodName(), language);
//            if (null != trans) {
//                order.setMethodName(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName1())) {
//            C2cTranslate trans = this.get(order.getParamName1(), language);
//            if (null != trans) {
//                order.setParamName1(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName2())) {
//            C2cTranslate trans = this.get(order.getParamName2(), language);
//            if (null != trans) {
//                order.setParamName2(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName3())) {
//            C2cTranslate trans = this.get(order.getParamName3(), language);
//            if (null != trans) {
//                order.setParamName3(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName4())) {
//            C2cTranslate trans = this.get(order.getParamName4(), language);
//            if (null != trans) {
//                order.setParamName4(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName5())) {
//            C2cTranslate trans = this.get(order.getParamName5(), language);
//            if (null != trans) {
//                order.setParamName5(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName6())) {
//            C2cTranslate trans = this.get(order.getParamName6(), language);
//            if (null != trans) {
//                order.setParamName6(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName7())) {
//            C2cTranslate trans = this.get(order.getParamName7(), language);
//            if (null != trans) {
//                order.setParamName7(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName8())) {
//            C2cTranslate trans = this.get(order.getParamName8(), language);
//            if (null != trans) {
//                order.setParamName8(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName9())) {
//            C2cTranslate trans = this.get(order.getParamName9(), language);
//            if (null != trans) {
//                order.setParamName9(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName10())) {
//            C2cTranslate trans = this.get(order.getParamName10(), language);
//            if (null != trans) {
//                order.setParamName10(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName11())) {
//            C2cTranslate trans = this.get(order.getParamName11(), language);
//            if (null != trans) {
//                order.setParamName11(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName12())) {
//            C2cTranslate trans = this.get(order.getParamName12(), language);
//            if (null != trans) {
//                order.setParamName12(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName13())) {
//            C2cTranslate trans = this.get(order.getParamName13(), language);
//            if (null != trans) {
//                order.setParamName13(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName14())) {
//            C2cTranslate trans = this.get(order.getParamName14(), language);
//            if (null != trans) {
//                order.setParamName14(trans.getTranslate());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(order.getParamName15())) {
//            C2cTranslate trans = this.get(order.getParamName15(), language);
//            if (null != trans) {
//                order.setParamName15(trans.getTranslate());
//            }
//        }
//
//        return order;
//    }

    /*
     * 获取 支付方式类型名称列表
     */
    public List<String> getAllPaymentMethodTypeName() {
        List<String> nameList = new ArrayList<String>();
        Map<String, String> data = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        for (String id : data.keySet()) {
            String name = data.get(id);
            if (null != name) {
                nameList.add(name);
            }
        }

        return nameList;
    }

    public void translateOrder(Map order, String language) {
        String paymentConfigId = "0";
        if (order.containsKey("method_name")) {
            String method_name = order.getOrDefault("method_name","").toString();
            String payment_method_id = order.getOrDefault("payment_method_id","").toString();

            if (StringUtils.isNotEmpty(payment_method_id)) {
                C2cPaymentMethod payMethodEntity = c2cPaymentMethodService.get(payment_method_id);
                if (payMethodEntity == null) {
                    return;
                }

                paymentConfigId = payMethodEntity.getMethodConfigId();
                String payParamLangKeyPrefix = "pay.param.";
                String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + paymentConfigId;

                C2cTranslate trans = this.get(paymentConfigId, payMethodNameLangKey, language);
                if (trans == null) {
                    List<C2cTranslate> c2cTranslate=list(Wrappers.<C2cTranslate>query().lambda()
                            .eq(C2cTranslate::getLanguage, language)
                            .eq(C2cTranslate::getLangKey, payMethodNameLangKey));
                    if (CollectionUtil.isNotEmpty(c2cTranslate)) {
                        trans = c2cTranslate.get(0);
                    }
                }

                if (null != trans) {
                    order.put("method_name", trans.getTranslate());
                }
            }
        }

        String payParamLangKeyPrefix = "pay.param.";
        if (order.containsKey("param_name1")) {
            String param_name = order.getOrDefault("param_name1","").toString();
            if (StringUtils.isNotEmpty(param_name)) {
                String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + paymentConfigId;
                C2cTranslate trans = this.get(paymentConfigId, paramLangKey, language);
                if (null != trans) {
                    order.put("param_name1", trans.getTranslate());
                }
            }
        }

        if (   order.containsKey("param_name2")){
            String  param_name = order.getOrDefault("param_name2","").toString();
            if (StringUtils.isNotEmpty(param_name)) {
                String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + paymentConfigId;
                C2cTranslate trans = this.get(paymentConfigId, paramLangKey, language);
                if (null != trans) {
                    order.put("param_name2", trans.getTranslate());
                }
            }
        }
    }

    public C2cOrder translateOrder(C2cOrder order, String language) {
        String payConfigId = "0";
        String payment_method_id = order.getPaymentMethodId();
        C2cPaymentMethod payMethodEntity = c2cPaymentMethodService.get(payment_method_id);
        if (payMethodEntity != null) {
            payConfigId = payMethodEntity.getMethodConfigId();
        }

        if (StringUtils.isNotEmpty(order.getMethodName())) {
            String payParamLangKeyPrefix = "pay.param.";
            String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + payConfigId;

            C2cTranslate trans = this.get(payConfigId, payMethodNameLangKey, language);
            if (null != trans) {
                order.setMethodName(trans.getTranslate());
            }
        }

        String payParamLangKeyPrefix = "pay.param.";
        if (StringUtils.isNotEmpty(order.getParamName1())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            // C2cTranslate trans = this.get(order.getParamName1(), language);
            if (null != trans) {
                order.setParamName1(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName2())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName2(), language);
            if (null != trans) {
                order.setParamName2(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName3())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_3.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName3(), language);
            if (null != trans) {
                order.setParamName3(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName4())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_4.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName4(), language);
            if (null != trans) {
                order.setParamName4(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName5())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_5.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName5(), language);
            if (null != trans) {
                order.setParamName5(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName6())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_6.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName6(), language);
            if (null != trans) {
                order.setParamName6(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName7())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_7.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName7(), language);
            if (null != trans) {
                order.setParamName7(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName8())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_8.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName8(), language);
            if (null != trans) {
                order.setParamName8(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName9())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_9.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName9(), language);
            if (null != trans) {
                order.setParamName9(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName10())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_10.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName10(), language);
            if (null != trans) {
                order.setParamName10(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName11())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_11.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName11(), language);
            if (null != trans) {
                order.setParamName11(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName12())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_12.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName12(), language);
            if (null != trans) {
                order.setParamName12(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName13())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_13.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName13(), language);
            if (null != trans) {
                order.setParamName13(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName14())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_14.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName14(), language);
            if (null != trans) {
                order.setParamName14(trans.getTranslate());
            }
        }

        if (StringUtils.isNotEmpty(order.getParamName15())) {
            String paramLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.PARAM_15.getCode() + "." + payConfigId;
            C2cTranslate trans = this.get(payConfigId, paramLangKey, language);
            //C2cTranslate trans = this.get(order.getParamName15(), language);
            if (null != trans) {
                order.setParamName15(trans.getTranslate());
            }
        }

        return order;
    }
}
