package com.yami.trading.admin.controller.ipo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class NewSharesConfigModel {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("产品代码（字母）")
    private String productName;
    @ApiModelProperty("产品代码（数字）")
    private String productCode;
    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;
    @ApiModelProperty("承销价")
    private BigDecimal underwritingPrice;
    @ApiModelProperty("总申购数")
    private int subscribeTotalNumber;
    @ApiModelProperty("已申购数")
    private int appliedSubscribeNumber;
    @ApiModelProperty("抽签日期 2023-03-22 00:00:00")
    private Date drawDate;
    @ApiModelProperty("开放申购日期 2023-03-22 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startSubscribeDate;
    @ApiModelProperty("截止申购日期 2023-03-22 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endSubscribeDate;
    @ApiModelProperty("发劵日期 2023-03-22 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issuanceDate;
    @ApiModelProperty("权重")
    private int weight;
    @ApiModelProperty("状态  1 未开始 2 开放中  3 已结束")
    private int status;
    @ApiModelProperty("默认额度")
    private  BigDecimal defaultLimit;

    private  int ipoStatus;

    @ApiModelProperty("锁定时间 天")
    private  int lockDay;
    /**
     * 股票类型
     */
    private  String type;
}
