package com.yami.trading.admin.model.c2c;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class SavePmtTranslateModel {

    @ApiModelProperty("类型名称: 银行卡")
    @NotBlank
    private  String name;

    // 格式： en##YHK&&zh-CN##银行卡
    @ApiModelProperty("类型 多语言")
    @NotBlank
    private  String trans;
}
