package com.yami.trading.admin.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserPromiseListModel  extends PageRequest {

    private String userName;


    private String roleName;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;

    @ApiModelProperty("产品代码（数字）")
    private  String productCode;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("1. 状态  1。待确认 2 已认缴")
    private  Integer status;
}
