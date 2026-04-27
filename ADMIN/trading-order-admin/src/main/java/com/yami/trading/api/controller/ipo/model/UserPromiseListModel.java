package com.yami.trading.api.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class UserPromiseListModel extends PageRequest {

    private String symbolType;
}
