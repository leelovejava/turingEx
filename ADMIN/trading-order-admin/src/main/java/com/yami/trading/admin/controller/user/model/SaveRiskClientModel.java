package com.yami.trading.admin.controller.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SaveRiskClientModel {
    /**
     * userId 和 ip 二选一
     */
    @ApiModelProperty("用户Code")
    private String userCode;

    /**
     * userId 和 ip 二选一
     */
    @ApiModelProperty("IP，支持*号占位符")
    private String ip;

    @ApiModelProperty("类型：badnetwork, black")
    private String type;

    /**
     * 不填时间代表不限时间，数据库用固定值： 2023-01-01 00:00:00 取代
     */
    @ApiModelProperty("风控开始时间")
    private String beginTime = "2023-01-01 00:00:00";

    /**
     * 不填时间代表不限时间，数据库用固定值： 2023-01-01 00:00:00 取代
     */
    @ApiModelProperty("风控截止时间")
    private String endTime = "2023-01-01 00:00:00";

    @ApiModelProperty("备注")
    private String remark;

    private int status = -1;

}
