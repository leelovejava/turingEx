package com.yami.trading.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class MoneylogUpdateModel {

    @ApiModelProperty("账变id")
    private  String  uuid;


    @ApiModelProperty("是否显示，1为显示 0为不显示")
    private String show;

}
