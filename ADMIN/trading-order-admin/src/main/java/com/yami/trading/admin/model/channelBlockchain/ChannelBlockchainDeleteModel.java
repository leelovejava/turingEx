
package com.yami.trading.admin.model.channelBlockchain;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ChannelBlockchainDeleteModel  {



    private String id;

    @ApiModelProperty("资金密码")
    private  String  safeword;


    @ApiModelProperty("超级谷歌验证码")
    private  String  superGoogleAuthCode;



}
