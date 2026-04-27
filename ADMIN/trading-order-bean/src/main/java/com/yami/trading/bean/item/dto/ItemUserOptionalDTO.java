package com.yami.trading.bean.item.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户自选DTO
 *
 * @author lucas
 * @version 2023-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ApiModel
public class ItemUserOptionalDTO implements Serializable {

    private static final long serialVersionUID = -8050403186613426946L;
    private String partyId;

    @ApiModelProperty("币对")
    private String symbol;

    @ApiModelProperty("币对名词")
    private String name;

    @ApiModelProperty("法币币种")
    private String currency;

    private String remarks;

    @JSONField(name = "volume_ratio")
    private Double volumeRatio;

    @JSONField(name = "change_ratio")
    private Double changeRatio;

    @JSONField(name = "turnover_rate")
    private Double turnoverRate;

    @ApiModelProperty("最新价")
    private Double close;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String type;
}
