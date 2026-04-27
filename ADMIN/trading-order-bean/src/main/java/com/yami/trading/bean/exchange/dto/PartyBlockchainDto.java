package com.yami.trading.bean.exchange.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PartyBlockchainDto {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("uid")
    private String  userCode;

    /**
     *
     */
    @ApiModelProperty("账号类型")
    private  String roleName;

    /**
     * 链名
     */
    @ApiModelProperty("链名")
    private  String chainName;

    @ApiModelProperty("币种")
    private  String coinSymbol;

    @ApiModelProperty("地址")
    private  String address;
}
