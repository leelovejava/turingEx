package com.yami.trading.bean.c2c.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class C2cPaymentMethodDto {


    private  String  uuid;


    private  String userCode;


    private  String usrName;

    /**
     * 支付方式模板ID
     */
    @ApiModelProperty("支付方式模板ID")
    private String methodConfigId;

    /**
     * 支付方式类型：0其它/1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款
     * 参考枚举类型： PayMethodTypeEnum
     */
    @ApiModelProperty("支付方式类型：0其它/1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款")
    private int methodType;

    /**
     * 支付方式类型名称
     */
    @TableField(exist = false)
    private String methodTypeName;

    /**
     * 支付方式名称
     */
    @ApiModelProperty("支付方式名称")
    private String methodName;

    /**
     * 支付方式图片
     */
    @ApiModelProperty("支付方式图片")
    private String methodImg;

    private String methodImgUrl;


    /**
     * 承兑商真实姓名
     */
    @ApiModelProperty("承兑商真实姓名")
    private String realName;

    /**
     * 参数名1
     */
    private String paramName1;

    /**
     * 参数值1
     */
    private String paramValue1;

    /**
     * 参数名2
     */
    private String paramName2;

    /**
     * 参数值2
     */
    private String paramValue2;

    /**
     * 参数名3
     */
    private String paramName3;

    /**
     * 参数值3
     */
    private String paramValue3;

    /**
     * 参数名4
     */
    private String paramName4;

    /**
     * 参数值4
     */
    private String paramValue4;

    /**
     * 参数名5
     */
    private String paramName5;

    /**
     * 参数值5
     */
    private String paramValue5;

    /**
     * 参数名6
     */
    private String paramName6;

    /**
     * 参数值6
     */
    private String paramValue6;

    /**
     * 参数名7
     */
    private String paramName7;

    /**
     * 参数值7
     */
    private String paramValue7;

    /**
     * 参数名8
     */
    private String paramName8;

    /**
     * 参数值8
     */
    private String paramValue8;

    /**
     * 参数名9
     */
    private String paramName9;

    /**
     * 参数值9
     */
    private String paramValue9;

    /**
     * 参数名10
     */
    private String paramName10;

    /**
     * 参数值10
     */
    private String paramValue10;

    /**
     * 参数名11
     */
    private String paramName11;

    /**
     * 参数值11
     */
    private String paramValue11;

    /**
     * 参数名12
     */
    private String paramName12;

    /**
     * 参数值12
     */
    private String paramValue12;

    /**
     * 参数名13
     */
    private String paramName13;

    /**
     * 参数值13
     */
    private String paramValue13;

    /**
     * 参数名14
     */
    private String paramName14;

    /**
     * 参数值14
     */
    private String paramValue14;

    /**
     * 参数名15
     */
    private String paramName15;

    /**
     * 参数值15
     */
    private String paramValue15;

    /**
     * 支付二维码
     */
    @ApiModelProperty("支付二维码")
    private String qrcode;


    @ApiModelProperty("支付二维码")
    private String qrcodeImg;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;


    @ApiModelProperty("用户id")
    private  String userId;


    @ApiModelProperty("用户名")
    private  String userName;
}
