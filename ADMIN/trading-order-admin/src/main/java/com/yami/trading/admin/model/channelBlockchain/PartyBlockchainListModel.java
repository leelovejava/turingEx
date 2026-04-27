package com.yami.trading.admin.model.channelBlockchain;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

@ApiModel
@Data
public class PartyBlockchainListModel extends PageRequest {
    @ApiModelProperty("充值地址")
    private String address;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("角色 ")
    private String roleName;
}
