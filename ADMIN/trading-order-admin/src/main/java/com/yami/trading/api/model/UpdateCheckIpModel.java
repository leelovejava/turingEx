package com.yami.trading.api.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class UpdateCheckIpModel {

    private  long gooleAuthCode;
}
