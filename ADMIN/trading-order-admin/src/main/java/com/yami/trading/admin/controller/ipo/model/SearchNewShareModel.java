package com.yami.trading.admin.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class SearchNewShareModel  extends PageRequest {
    private  String symbol;
}
