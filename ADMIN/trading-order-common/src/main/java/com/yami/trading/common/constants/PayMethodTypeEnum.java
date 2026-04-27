package com.yami.trading.common.constants;

import cn.hutool.core.util.StrUtil;

/**
 * 支付方式类型：0其它/1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款
 *
 */
public enum PayMethodTypeEnum {
    // 此类支付大类对应的多语言 langKey 组装规则： pay.type.{code}
    OTHER(0, "其它"),
    BANK_CARD(1, "银行卡"),
    VIRTUAL_CURRENCY(2, "虚拟货币"),
    WX_PAY(3, "微信"),
    ALI_PAY(4, "支付宝"),
    PAYPAL(5, "PayPal"),
    WESTERN_UNION(6, "西联汇款"),
    SWIFT(7, "SWIFT国际汇款"),
    ;

    private int code;

    private String name;

    private PayMethodTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayMethodTypeEnum codeOf(int inputCode) {
        PayMethodTypeEnum[] values = PayMethodTypeEnum.values();
        for (PayMethodTypeEnum one : values) {
            if (one.getCode() == inputCode) {
                return one;
            }
        }

        return null;
    }

    public static PayMethodTypeEnum nameOf(String inputName) {
        if (StrUtil.isBlank(inputName)) {
            //return PayMethodTypeEnum.OTHER;
            return null;
        }
        PayMethodTypeEnum[] values = PayMethodTypeEnum.values();
        for (PayMethodTypeEnum one : values) {
            if (one.getName().equalsIgnoreCase(inputName)) {
                return one;
            }
        }

        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
