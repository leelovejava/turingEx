package com.yami.trading.bean.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class RiskClientInfoDto implements Serializable {

    private String id;

    private String clientKey;

    private String clientName;

    private String clientType;

    private String type;

    private String beginTime;

    private String endTime;

    private int status;

    @ApiModelProperty("记录创建时间")
    private Date createTime;

    /**
     * 最后处理时间
     */
    private Date lastOperaTime;

    private String remark;

}
