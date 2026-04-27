package com.yami.trading.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TraderFollowUserListModel {

    @ApiModelProperty("用户名")
    private String name_para;

    @ApiModelProperty("用户名")
    private String username_para;


}
