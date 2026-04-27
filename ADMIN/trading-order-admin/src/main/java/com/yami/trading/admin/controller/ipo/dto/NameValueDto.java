package com.yami.trading.admin.controller.ipo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class NameValueDto {

    private  String value;

    private  String name;

    public NameValueDto(String name,String value){
        this.name=name;
        this.value=value;
    }
}
