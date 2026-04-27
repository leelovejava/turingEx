package com.yami.trading.admin.controller.ipo.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class NewSharesConfigListModel extends PageRequest {
    @ApiModelProperty("发行市场 1 待上市 2 已上市 ")
    private  int ipoStatus;

    @ApiModelProperty("状态  1 未开始 2 开放中  3 已结束")
    private  int status;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;


    @ApiModelProperty("产品代码（数字）")
    private  String productCode;
}
