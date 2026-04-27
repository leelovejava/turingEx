package com.yami.trading.admin.model.channelBlockchain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ChannelBlockchainUpdateModel {

    private  String id;

    /**
     * 币种名称 BTC ETH USDT
     */
    @ApiModelProperty("币种名称")
    private String coin;
    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    private String blockchainName;


    @ApiModelProperty("地址")
    private  String address;


    @ApiModelProperty("资金密码")
    private  String  safeword;


    @ApiModelProperty("超级谷歌验证码")
    private  String  superGoogleAuthCode;
}
