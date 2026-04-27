package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_c2c_payment_method_config")
public class C2cPaymentMethodConfig {
    private static final long serialVersionUID = -5097468546071632477L;

    @TableId(type = IdType.ASSIGN_UUID)
    private  String uuid;

    /**
     * 1 银行卡  2 c2c
     */
    private int type;

    /**
     * 支付方式类型：0其它/1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款
     * 参考枚举类型： PayMethodTypeEnum
     */
    private int methodType;

    /**
     * 支付方式名称
     */
    private String methodName;

    /**
     * 支付方式图片
     */
    private String methodImg;

    /**
     * 参数名1（配关键数据：如微信账号、支付宝账号、银行卡号、虚拟货币地址等等）
     */
    private String paramName1;

    /**
     * 参数名2
     */
    private String paramName2;

    /**
     * 参数名3
     */
    private String paramName3;

    /**
     * 参数名4
     */
    private String paramName4;

    /**
     * 参数名5
     */
    private String paramName5;

    /**
     * 参数名6
     */
    private String paramName6;

    /**
     * 参数名7
     */
    private String paramName7;

    /**
     * 参数名8
     */
    private String paramName8;

    /**
     * 参数名9
     */
    private String paramName9;

    /**
     * 参数名10
     */
    private String paramName10;

    /**
     * 参数名11
     */
    private String paramName11;

    /**
     * 参数名12
     */
    private String paramName12;

    /**
     * 参数名13
     */
    private String paramName13;

    /**
     * 参数名14
     */
    private String paramName14;

    /**
     * 参数名15
     */
    private String paramName15;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 支付方式类型名称
     */
    @TableField(exist = false)
    private String methodTypeName;

}
