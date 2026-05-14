package com.yami.trading.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 *
 * 实名认证审核
 */
@Data
@ApiModel
public class RealNameExamineModel {


    @ApiModelProperty("审核状态: 1 审核通过 2 驳回")
    @Min(1)
    @Max(2)
    private  int type;
    
    
    private  String content;
    

    @NotBlank
    private  String id;
}
