package com.yami.trading.admin.model.channelBlockchain;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ChannelBlockchainListModel extends PageRequest {



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
}
