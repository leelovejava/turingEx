package com.yami.trading.bean.chat.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lucas
 */
@Data
@ApiModel("消息发送请求体")
public class ChatSendRespModel {
    // 需要确认下是否需要修改为 Date 类型
    @ApiModelProperty("发送时间")
    private String sendTimeStamp;

    @ApiModelProperty("消息ID")
    private String chatId;

    // 需要确认下是否需要修改为 Date 类型
    @ApiModelProperty("更新时间")
    private Date updatetime;
}
