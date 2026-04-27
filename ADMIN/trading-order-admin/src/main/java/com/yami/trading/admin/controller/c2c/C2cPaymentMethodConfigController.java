package com.yami.trading.admin.controller.c2c;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.dto.c2c.C2cPaymentMethodConfigListDto;
import com.yami.trading.admin.dto.c2c.C2cPaymentMethodTypeDto;
import com.yami.trading.admin.dto.c2c.GetC2cPaymentMethodConfigDto;
import com.yami.trading.admin.dto.c2c.PmtLangDto;
import com.yami.trading.admin.model.SaveTranslateModel;
import com.yami.trading.admin.model.c2c.*;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PayMethodTypeEnum;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cTranslateService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
@RequestMapping("paymentMethodConfig")
@Api(tags = "C2C支付方式模板")
public class C2cPaymentMethodConfigController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    C2cAdvertService c2cAdvertService;
    @Autowired
    LogService logService;
    @Autowired
    C2cTranslateService c2cTranslateService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<C2cPaymentMethodConfigListDto>> list(@RequestBody @Valid C2cPaymentMethodConfigListModel request) {
        Page<C2cPaymentMethodConfig> page = new Page(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<C2cPaymentMethodConfig>
                queryWrapper = Wrappers.<C2cPaymentMethodConfig>query().lambda();
        if (StringUtils.isNotEmpty(request.getMethodName())) {
            queryWrapper.like(C2cPaymentMethodConfig::getMethodName, request.getMethodName());
        }
        queryWrapper.eq(C2cPaymentMethodConfig::getType, request.getType());
        if (StringUtils.isNotEmpty(request.getMethodType())) {
            queryWrapper.eq(C2cPaymentMethodConfig::getMethodType, request.getMethodType());
        }
        queryWrapper.orderByDesc(C2cPaymentMethodConfig::getCreateTime);
        c2cPaymentMethodConfigService.page(page, queryWrapper);
        Page<C2cPaymentMethodConfigListDto> paymentMethodConfigListDtoPage = new Page<>();
        paymentMethodConfigListDtoPage.setTotal(page.getTotal());
        Map<String, String> map = c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        List<C2cPaymentMethodConfigListDto> list = new ArrayList<>();
        for (C2cPaymentMethodConfig paymentMethodConfig : page.getRecords()) {
            C2cPaymentMethodConfigListDto c2cPaymentMethodConfigListDto = new C2cPaymentMethodConfigListDto();
            BeanUtils.copyProperties(paymentMethodConfig, c2cPaymentMethodConfigListDto);
            c2cPaymentMethodConfigListDto.setMethodType(paymentMethodConfig.getMethodType() + "");
            c2cPaymentMethodConfigListDto.setMethodTypeName(map.get(c2cPaymentMethodConfigListDto.getMethodType()));
            //awsS3OSSFileService.getUrl(paymentMethodConfig.getMethodImg())
            //awsS3OSSFileService.getUrl(paymentMethodConfig.getMethodImg())
            c2cPaymentMethodConfigListDto.setMethodImgUrl(awsS3OSSFileService.getUrl(paymentMethodConfig.getMethodImg()));
            c2cPaymentMethodConfigListDto.setMethodImg(paymentMethodConfig.getMethodImg());
            list.add(c2cPaymentMethodConfigListDto);
        }
        paymentMethodConfigListDtoPage.setRecords(list);
        return Result.ok(paymentMethodConfigListDtoPage);
    }

    @ApiOperation(value = "获取支付方式类型")
    @GetMapping("getC2cPaymentMethodType")
    public Result<?> getC2cPaymentMethodType() {
        Map<String, String> map = c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        List<C2cPaymentMethodTypeDto> list = new ArrayList<>();
        for (String key : map.keySet()) {
            C2cPaymentMethodTypeDto c2cPaymentMethodTypeDto = new C2cPaymentMethodTypeDto();
            c2cPaymentMethodTypeDto.setId(key);
            c2cPaymentMethodTypeDto.setName(map.get(key));
            list.add(c2cPaymentMethodTypeDto);
        }
        return Result.ok(list);
    }

    /**
     * 获取 C2C支付方式模板 接口
     */
    @GetMapping("getDesc")
    @ApiOperation("获取 C2C支付方式模板 接口")
    public Result<GetC2cPaymentMethodConfigDto> getDesc(@RequestParam String id) {
        // 此处存储了支付模板各个模板字段的自定义中文名称值
        C2cPaymentMethodConfig cfg = this.c2cPaymentMethodConfigService.getById(id);
        if (null == cfg) {
            throw new YamiShopBindException("支付方式模板不存在");
        }
        Map<String, String> map = c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        GetC2cPaymentMethodConfigDto dto = new GetC2cPaymentMethodConfigDto();
        dto.setMethodType(String.valueOf(cfg.getMethodType()));
        // 例如：工商银行卡
        dto.setMethodName(cfg.getMethodName());
        // 例如：银行卡
        dto.setMethodTypeName(map.get(dto.getMethodType()));
        dto.setMethodImg(cfg.getMethodImg());
        dto.setMethodImgUrl(awsS3OSSFileService.getUrl(cfg.getMethodImg()));
        dto.setParamName1(cfg.getParamName1());
        dto.setParamName2(cfg.getParamName2());
        dto.setParamName3(cfg.getParamName3());
        dto.setParamName4(cfg.getParamName4());
        dto.setParamName5(cfg.getParamName5());
        dto.setParamName6(cfg.getParamName6());
        dto.setParamName7(cfg.getParamName7());
        dto.setParamName8(cfg.getParamName8());
        dto.setParamName9(cfg.getParamName9());
        dto.setParamName10(cfg.getParamName10());
        dto.setParamName11(cfg.getParamName11());
        dto.setParamName12(cfg.getParamName12());
        dto.setParamName13(cfg.getParamName13());
        dto.setParamName14(cfg.getParamName14());
        dto.setParamName15(cfg.getParamName15());

        String multiLangKeyPrefix = "pay.param.";
        String methodNameLangKey = multiLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + id;
        dto.setMethodNameLangTrans(StringUtils.isNotEmpty(cfg.getMethodName()) ? c2cTranslateService.getTranslate(cfg.getUuid(), methodNameLangKey) : "");

        String param1LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_1.getCode() + "." + id;
        dto.setParamName1LangTrans(StringUtils.isNotEmpty(cfg.getParamName1()) ? c2cTranslateService.getTranslate(cfg.getUuid(), param1LangKey) : "");

        String param2LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_2.getCode() + "." + id;
        dto.setParamName2LangTrans(StringUtils.isNotEmpty(cfg.getParamName2()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param2LangKey) : "");

        String param3LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_3.getCode() + "." + id;
        dto.setParamName3LangTrans(StringUtils.isNotEmpty(cfg.getParamName3()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param3LangKey) : "");

        String param4LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_4.getCode() + "." + id;
        dto.setParamName4LangTrans(StringUtils.isNotEmpty(cfg.getParamName4()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param4LangKey) : "");

        String param5LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_5.getCode() + "." + id;
        dto.setParamName5LangTrans(StringUtils.isNotEmpty(cfg.getParamName5()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param5LangKey) : "");

        String param6LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_6.getCode() + "." + id;
        dto.setParamName6LangTrans(StringUtils.isNotEmpty(cfg.getParamName6()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param6LangKey) : "");

        String param7LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_7.getCode() + "." + id;
        dto.setParamName7LangTrans(StringUtils.isNotEmpty(cfg.getParamName7()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param7LangKey) : "");

        String param8LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_8.getCode() + "." + id;
        dto.setParamName8LangTrans(StringUtils.isNotEmpty(cfg.getParamName8()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param8LangKey) : "");

        String param9LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_9.getCode() + "." + id;
        dto.setParamName9LangTrans(StringUtils.isNotEmpty(cfg.getParamName9()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param9LangKey) : "");

        String param10LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_10.getCode() + "." + id;
        dto.setParamName10LangTrans(StringUtils.isNotEmpty(cfg.getParamName10()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param10LangKey) : "");

        String param11LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_11.getCode() + "." + id;
        dto.setParamName11LangTrans(StringUtils.isNotEmpty(cfg.getParamName11()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param11LangKey) : "");

        String param12LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_12.getCode() + "." + id;
        dto.setParamName12LangTrans(StringUtils.isNotEmpty(cfg.getParamName12()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param12LangKey) : "");

        String param13LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_13.getCode() + "." + id;
        dto.setParamName13LangTrans(StringUtils.isNotEmpty(cfg.getParamName13()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param13LangKey) : "");

        String param14LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_14.getCode() + "." + id;
        dto.setParamName14LangTrans(StringUtils.isNotEmpty(cfg.getParamName14()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param14LangKey) : "");

        String param15LangKey = multiLangKeyPrefix + PayTemplateParamEnum.PARAM_15.getCode() + "." + id;
        dto.setParamName15LangTrans(StringUtils.isNotEmpty(cfg.getParamName15()) ? this.c2cTranslateService.getTranslate(cfg.getUuid(), param15LangKey) : "");

        dto.setLanguageIntro(this.c2cAdvertService.getLanguageIntro());
        dto.setMethodTypeIntro(this.c2cAdvertService.getMethodTypeIntro());
        return Result.ok(dto);
    }

    @ApiOperation(value = "新增支付方式模板")
    @PostMapping("add")
    public Result<?> add(@RequestBody  C2cPaymentMethodConfigModel config) {
//        String method_type = config.getMethodType();
//        String method_name = config.getMethodName();
//        String method_img = config.getMethodImg();
//        String param_name1 = config.getParamName1();
//        String param_name2 = config.getParamName2();
//        String param_name3 = config.getParamName3();
//        String param_name4 = config.getParamName4();
//        String param_name5 = config.getParamName5();
//        String param_name6 = config.getParamName6();
//        String param_name7 = config.getParamName7();
//        String param_name8 = config.getParamName8();
//        String param_name9 = config.getParamName9();
//        String param_name10 = config.getParamName10();
//        String param_name11 = config.getParamName11();
//        String param_name12 = config.getParamName12();
//        String param_name13 = config.getParamName13();
//        String param_name14 = config.getParamName14();
//        String param_name15 = config.getParamName15();

        Date now = new Date();
        C2cPaymentMethodConfig cfg = new C2cPaymentMethodConfig();
        cfg.setMethodType(Integer.valueOf(config.getMethodType()));
        cfg.setMethodName(config.getMethodName());
        cfg.setMethodImg(config.getMethodImg());
        cfg.setParamName1(config.getParamName1());
        cfg.setParamName2(config.getParamName2());
        cfg.setParamName3(config.getParamName3());
        cfg.setParamName4(config.getParamName4());
        cfg.setParamName5(config.getParamName5());
        cfg.setParamName6(config.getParamName6());
        cfg.setParamName7(config.getParamName7());
        cfg.setParamName8(config.getParamName8());
        cfg.setParamName9(config.getParamName9());
        cfg.setParamName10(config.getParamName10());
        cfg.setParamName11(config.getParamName11());
        cfg.setParamName12(config.getParamName12());
        cfg.setParamName13(config.getParamName13());
        cfg.setParamName14(config.getParamName14());
        cfg.setParamName15(config.getParamName15());
        cfg.setCreateTime(now);
        cfg.setUpdateTime(now);
        cfg.setType(config.getType());
        c2cPaymentMethodConfigService.add(cfg);

        // 本方法仅用于创建支付模板的初始记录，自定义字段仅使用一种语言来表述，例如中文。后续需要在配置多语言的编辑操作中另外配置自定义字段的多语言

        String log = MessageFormat.format("新增支付方式模板,id:{0},支付方式类型:{1},支付方式名称:{2},支付方式图片:{3},参数名1:{4},参数名2:{5},参数名3:{6}",
                cfg.getUuid(), cfg.getMethodType(), cfg.getMethodName(), cfg.getMethodImg(),
                cfg.getParamName1(), cfg.getParamName2(), cfg.getParamName3());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(SecurityUtils.getSysUser().getUsername());
        dbLog.setUsername(SecurityUtils.getSysUser().getUsername());
        dbLog.setUserId(SecurityUtils.getSysUser().getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(new Date());
        logService.save(dbLog);
        return Result.ok(null);
    }

    public String verif(String method_type, String method_name, String method_img, String param_name1, String login_safeword) {
        if (StringUtils.isEmpty(method_type)) {
            return "支付方式类型必填";
        }
        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        if (!pmtMap.keySet().contains(method_type)) {
            return "支付方式类型错误";
        }
        if (StringUtils.isEmpty(method_name)) {
            return "支付方式名必填";
        }
        if (StringUtils.isEmpty(method_img)) {
            return "支付方式图片必填";
        }
        if (StringUtils.isEmpty(param_name1)) {
            return "参数名1必填";
        }
        if (StringUtils.isEmpty(login_safeword)) {
            return "请填写资金密码";
        }
        return "";
    }

    @ApiOperation(value = "更新自定义支付模板的字段")
    @PostMapping("update")
    public Result<?> update(@RequestBody C2cPaymentMethodConfigUpdateModel config) {
        String id = config.getId();
        String method_type = config.getMethodType();
        // 自定义的支付方式
        String method_name = config.getMethodName();
        String method_img = config.getMethodImg();
        // 自定义的支付模板字段（不是多语言）
        String param_name1 = config.getParamName1();
        String param_name2 = config.getParamName2();
        String param_name3 = config.getParamName3();
        String param_name4 = config.getParamName4();
        String param_name5 = config.getParamName5();
        String param_name6 = config.getParamName6();
        String param_name7 = config.getParamName7();
        String param_name8 = config.getParamName8();
        String param_name9 = config.getParamName9();
        String param_name10 = config.getParamName10();
        String param_name11 = config.getParamName11();
        String param_name12 = config.getParamName12();
        String param_name13 = config.getParamName13();
        String param_name14 = config.getParamName14();
        String param_name15 = config.getParamName15();
        String error = this.verif(method_type, method_name, method_img, param_name1, config.getLoginSafeword());
        if (!StringUtils.isEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        sysUserService.checkSafeWord(config.getLoginSafeword());
        C2cPaymentMethodConfig cfg = this.c2cPaymentMethodConfigService.getById(id);
        if (null == cfg) {
            throw new YamiShopBindException("支付方式模板不存在");
        }
        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr()
                        + ",更新支付方式模板,id:{0},原支付方式类型:{1},原支付方式名称:{2},原支付方式图片:{3},原参数名1:{4},原参数名2:{5},原参数名3:{6},原参数名4:{7},原参数名5:{8},"
                        + "原参数名6:{9},原参数名7:{10},原参数名8:{11},原参数名9:{12},原参数名10:{13},原参数名11:{14},原参数名12:{15},原参数名13:{16},原参数名14:{17},原参数名15:{18},原创建时间:{19},原更新时间:{20}",
                cfg.getUuid(), cfg.getMethodType(), cfg.getMethodName(), cfg.getMethodImg(), cfg.getParamName1(), cfg.getParamName2(), cfg.getParamName3(), cfg.getParamName4(),
                cfg.getParamName5(), cfg.getParamName6(), cfg.getParamName7(), cfg.getParamName8(), cfg.getParamName9(), cfg.getParamName10(), cfg.getParamName11(), cfg.getParamName12(),
                cfg.getParamName13(), cfg.getParamName14(), cfg.getParamName15(), cfg.getCreateTime(), cfg.getUpdateTime());

        Date now = new Date();
        cfg.setMethodType(Integer.valueOf(method_type));
        cfg.setMethodName(method_name);
        cfg.setMethodImg(method_img);
        cfg.setParamName1(param_name1);
        cfg.setParamName2(param_name2);
        cfg.setParamName3(param_name3);
        cfg.setParamName4(param_name4);
        cfg.setParamName5(param_name5);
        cfg.setParamName6(param_name6);
        cfg.setParamName7(param_name7);
        cfg.setParamName8(param_name8);
        cfg.setParamName9(param_name9);
        cfg.setParamName10(param_name10);
        cfg.setParamName11(param_name11);
        cfg.setParamName12(param_name12);
        cfg.setParamName13(param_name13);
        cfg.setParamName14(param_name14);
        cfg.setParamName15(param_name15);
        cfg.setUpdateTime(now);
        boolean state = this.c2cPaymentMethodConfigService.updateById(cfg);
        // 可以考虑在此处同步刷新一下模板自定义字段多语言记录里的 content 值

        if (state) {
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID + cfg.getUuid(), cfg);
            Map<String, String> map1 = (Map<String, String>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE);
            if (null == map1) {
                map1 = new ConcurrentHashMap<String, String>();
            } else {
                map1.remove(cfg.getUuid());
            }
            map1.put(cfg.getUuid(), String.valueOf(cfg.getMethodType()));
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE, map1);
        }

        log += MessageFormat.format(",新支付方式类型:{0},新支付方式名称:{1},新支付方式图片:{2},新参数名1:{3},新参数名2:{4},新参数名3:{5},新参数名4:{6},新参数名5:{7},"
                        + "新参数名6:{8},新参数名7:{9},新参数名8:{10},新参数名9:{11},新参数名10:{12},新参数名11:{13},新参数名12:{14},新参数名13:{15},新参数名14:{16},新参数名15:{17},新创建时间:{18},新更新时间:{29}",
                cfg.getMethodType(), cfg.getMethodName(), cfg.getMethodImg(), cfg.getParamName1(), cfg.getParamName2(), cfg.getParamName3(), cfg.getParamName4(),
                cfg.getParamName5(), cfg.getParamName6(), cfg.getParamName7(), cfg.getParamName8(), cfg.getParamName9(), cfg.getParamName10(), cfg.getParamName11(), cfg.getParamName12(),
                cfg.getParamName13(), cfg.getParamName14(), cfg.getParamName15(), cfg.getCreateTime(), cfg.getUpdateTime());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(SecurityUtils.getSysUser().getUsername());
        dbLog.setUsername(SecurityUtils.getSysUser().getUsername());
        dbLog.setUserId(SecurityUtils.getSysUser().getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(now);
        logService.save(dbLog);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除")
    @PostMapping("delete")
    public Result<?> delete(@RequestBody @Valid C2cPaymentMethodConfigDeleteModel model) {
        sysUserService.checkSafeWord(model.getLoginSafeword());
        C2cPaymentMethodConfig cfg = this.c2cPaymentMethodConfigService.getById(model.getId());

        this.c2cPaymentMethodConfigService.delete(model.getId());
        // 可以考虑将多语言配置记录也给删除 TODO

        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr()
                        + ",删除支付方式模板,id:{0},支付方式类型:{1},支付方式名称:{2},支付方式图片:{3},参数名1:{4},参数名2:{5},参数名3:{6},参数名4:{7},参数名5:{8},"
                        + "参数名6:{9},参数名7:{10},参数名8:{11},参数名9:{12},参数名10:{13},参数名11:{14},参数名12:{15},参数名13:{16},参数名14:{17},参数名15:{18},创建时间:{19},更新时间:{20}",
                cfg.getUuid(), cfg.getMethodType(), cfg.getMethodName(), cfg.getMethodImg(), cfg.getParamName1(), cfg.getParamName2(), cfg.getParamName3(), cfg.getParamName4(),
                cfg.getParamName5(), cfg.getParamName6(), cfg.getParamName7(), cfg.getParamName8(), cfg.getParamName9(), cfg.getParamName10(), cfg.getParamName11(), cfg.getParamName12(),
                cfg.getParamName13(), cfg.getParamName14(), cfg.getParamName15(), cfg.getCreateTime(), cfg.getUpdateTime());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(SecurityUtils.getSysUser().getUsername());
        dbLog.setUsername(SecurityUtils.getSysUser().getUsername());
        dbLog.setUserId(SecurityUtils.getSysUser().getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(new Date());
        logService.save(dbLog);
        return Result.ok(null);
    }

    /**
     * C2C支付方式类型 多语言配置 页面
     */
    @GetMapping("getPmtLang")
    @ApiOperation("支付方式类型 多语言配置页面")
    public Result<PmtLangDto> getPmtLang() {
        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        PmtLangDto pmtLangDto = new PmtLangDto();
        List<PmtLangDto.TransTypeNameDto> list = new ArrayList<>();
        for (String typeId : pmtMap.keySet()) {
            String name = pmtMap.get(typeId);
            PayMethodTypeEnum payType = PayMethodTypeEnum.codeOf(Integer.parseInt(typeId));

            PmtLangDto.TransTypeNameDto transTypeNameDto = new PmtLangDto.TransTypeNameDto();
            if (null != payType) {
                transTypeNameDto.setTypeId(typeId);
                transTypeNameDto.setTransTypeId("trans_" + typeId);
                transTypeNameDto.setName(name);

                String langKey = "pay.type." + payType.getCode();
                transTypeNameDto.setTrans(StringUtils.isNotEmpty(name) ? this.c2cTranslateService.getTranslate(langKey) : "");
            } else {
                transTypeNameDto.setTypeId(typeId);
                transTypeNameDto.setTransTypeId("trans_" + typeId);
                transTypeNameDto.setName(name);
                transTypeNameDto.setTrans("");
            }
            list.add(transTypeNameDto);
        }

        pmtLangDto.setMethodTypeList(list);
        pmtLangDto.setLanguageIntro(this.c2cAdvertService.getLanguageIntro());
        pmtLangDto.setMethodTypeIntro(this.c2cAdvertService.getMethodTypeIntro());
        return Result.ok(pmtLangDto);
    }

    /**
     *
     */
    @ApiOperation("添加或修改 支付方式模板 多语言")
    @PostMapping("saveTranslate")
    public Result saveTranslate(@RequestBody @Valid SaveTranslateModel model) {
        String content_name = model.getContentName();
        String content = model.getContent();
        String lang_trans = model.getLangTrans();
        if (StringUtils.isEmptyString(content_name)) {
            throw new YamiShopBindException("内容名称不能为空");
        }
        if (StringUtils.isEmptyString(content)) {
            throw new YamiShopBindException("要翻译的内容不能为空");
        }
        if (StringUtils.isEmptyString(lang_trans)) {
            throw new YamiShopBindException("翻译不能为空");
        }

        PayTemplateParamEnum paramType = PayTemplateParamEnum.nameOf(content_name);
        if (paramType == null) {
            throw new YamiShopBindException("不支持的模板参数");
        }

        try {
            // 此处的 name 值比较固定，是约定的几种支付方式名称，例如：银行卡，虚拟货币等
            // trans 即是某种支付名称的多语言叫法
            String multiLangKey = "pay.param." + paramType.getCode() + "." + model.getId();
            this.c2cTranslateService.saveTranslate(model.getId(), multiLangKey, lang_trans, content);

            String log = MessageFormat.format("ip:" + IPHelper.getIpAddr() + ",添加或修改支付方式模板多语言,内容名称{0},要翻译的内容:{1},翻译:{2}", content_name, content, lang_trans);
            Log dbLog = new Log();
            dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
            dbLog.setOperator(SecurityUtils.getSysUser().getUsername());
            dbLog.setUsername(SecurityUtils.getSysUser().getUsername());
            dbLog.setUserId(SecurityUtils.getSysUser().getUserId() + "");
            dbLog.setLog(log);
            dbLog.setCreateTime(new Date());
            logService.save(dbLog);
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new BusinessException("输入内容格式不对!");
        }
        return Result.ok(null);
    }

    /**
     * 添加或修改 支付方式类型多语言
     */
    @PostMapping("savePmtTranslate")
    @ApiOperation("添加或修改 支付方式类型多语言")
    public Result<?> savePmtTranslate(@RequestBody @Valid SavePmtTranslateModel model) {
        String name = model.getName();
        String trans = model.getTrans();
        if (StrUtil.isBlank(name)) {
            throw new YamiShopBindException("要翻译的内容不能为空");
        }
        if (StrUtil.isBlank(trans)) {
            throw new YamiShopBindException("翻译不能为空");
        }

        PayMethodTypeEnum payMethodType = PayMethodTypeEnum.nameOf(name);
        if (payMethodType == null) {
            throw new YamiShopBindException("不支持的支付类型");
        }

        try {
            // 此处的 name 值比较固定，是约定的几种支付方式名称，例如：银行卡，虚拟货币等
            // trans 即是某种支付名称的多语言叫法
            String multiLangKey = "pay.type." + payMethodType.getCode();
            c2cTranslateService.saveTranslate("0", multiLangKey, trans, name);

            String log = MessageFormat.format("ip:" + IPHelper.getIpAddr() + ",添加或修改支付方式类型多语言,要翻译的内容:{0},翻译:{1}", name, trans);
            Log dbLog = new Log();
            dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
            dbLog.setOperator(SecurityUtils.getSysUser().getUsername());
            dbLog.setUsername(SecurityUtils.getSysUser().getUsername());
            dbLog.setUserId(SecurityUtils.getSysUser().getUserId() + "");
            dbLog.setLog(log);
            dbLog.setCreateTime(new Date());
            logService.save(dbLog);
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new BusinessException("输入内容格式不对!");
        }

        return Result.ok(null);
    }
}
