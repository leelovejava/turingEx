package com.yami.trading.common.constants;

import cn.hutool.core.util.StrUtil;

/**
 * 参数名1 ... 参数名15
 *
 */
public enum PayTemplateParamEnum {
    // 此类模板参数对应的多语言 langKey 组装规则： pay.param.{paramCode}.{payId};
    METHOD_NAME("method_name", "支付方式名称"),
    PARAM_1("param_name1", "参数名1"),
    PARAM_2("param_name2", "参数名2"),
    PARAM_3("param_name3", "参数名3"),
    PARAM_4("param_name4", "参数名4"),
    PARAM_5("param_name5", "参数名5"),
    PARAM_6("param_name6", "参数名6"),
    PARAM_7("param_name7", "参数名7"),
    PARAM_8("param_name8", "参数名8"),
    PARAM_9("param_name9", "参数名9"),
    PARAM_10("param_name10", "参数名10"),
    PARAM_11("param_name11", "参数名11"),
    PARAM_12("param_name12", "参数名12"),
    PARAM_13("param_name13", "参数名13"),
    PARAM_14("param_name14", "参数名14"),
    PARAM_15("param_name15", "参数名15"),

    ;

    private String code;

    private String name;

    private PayTemplateParamEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayTemplateParamEnum codeOf(String inputCode) {
        PayTemplateParamEnum[] values = PayTemplateParamEnum.values();
        for (PayTemplateParamEnum one : values) {
            if (one.getCode().equalsIgnoreCase(inputCode)) {
                return one;
            }
        }

        return null;
    }

    public static PayTemplateParamEnum nameOf(String inputName) {
        if (StrUtil.isBlank(inputName)) {
            return null;
        }
        PayTemplateParamEnum[] values = PayTemplateParamEnum.values();
        for (PayTemplateParamEnum one : values) {
            if (one.getName().equalsIgnoreCase(inputName)) {
                return one;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
