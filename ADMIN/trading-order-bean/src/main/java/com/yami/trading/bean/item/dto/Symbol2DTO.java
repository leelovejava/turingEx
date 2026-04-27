package com.yami.trading.bean.item.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 永续合约
 *
 * @author lucas
 * @version 2023-03-10
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = false)
public class Symbol2DTO extends SymbolDTO {

    @ApiModelProperty(value = "是否添加自选")
    private boolean hasAddGlobal;
}
