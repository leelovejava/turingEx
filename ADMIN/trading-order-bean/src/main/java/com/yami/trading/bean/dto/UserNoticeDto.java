package com.yami.trading.bean.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class UserNoticeDto {

    private String id;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("状态：1未读 2已读")
    private Integer status;

    @ApiModelProperty("通知类型")
    private String noticeType;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("用户UID")
    private String userCode;

    @ApiModelProperty("用户名")
    private String userName;
}
