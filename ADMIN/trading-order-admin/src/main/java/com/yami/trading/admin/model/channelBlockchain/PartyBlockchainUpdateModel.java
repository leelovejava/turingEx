
package com.yami.trading.admin.model.channelBlockchain;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class PartyBlockchainUpdateModel  {

    private  String id;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("链名")
    private  String chainName;

    @ApiModelProperty("币种")
    private  String coinSymbol;


}
