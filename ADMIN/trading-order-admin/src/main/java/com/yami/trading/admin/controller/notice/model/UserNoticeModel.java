package com.yami.trading.admin.controller.notice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class UserNoticeModel {
    @ApiModelProperty("修改传id 新增不传")
    private String id;
    @ApiModelProperty("资金密码")
    private String loginSafeword;
    @ApiModelProperty("标题")
    @NotBlank
    private String title;
    @ApiModelProperty("内容")
    @NotBlank
    private String content;
    @ApiModelProperty("通知类型")
    private String noticeType;
    @ApiModelProperty("用户UID（空是所有用户）")
    private String userCode;
}
