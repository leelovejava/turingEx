package com.yami.trading.api.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class NewSharesConfigModel  extends PageRequest {

    private  String  type;
}
