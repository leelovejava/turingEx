package com.yami.trading.admin.controller.notice.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserNoticeListModel extends PageRequest {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("用户UID")
    private String userCode;

    @ApiModelProperty("通知类型")
    private String noticeType;
}
