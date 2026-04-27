package com.yami.trading.bean.chat.query.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lucas
 */
@Data
@ApiModel("在线聊天")
public class ChatUserInfoRespModel {

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("partyId")
    private String partyId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户code")
    private String userCode;

    // 需要确认下是否需要修改为 Date 类型
    @ApiModelProperty("最近一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty("创建时间")
    private Date createtime;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("登入ip")
    private String loginIp;
}
