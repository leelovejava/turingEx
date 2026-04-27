package com.yami.trading.bean.item.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yami.trading.common.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品杠杠倍数DTO
 *
 * @author lucas
 * @version 2023-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ItemLeverageDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * ITEM_ID
     */
    private String itemId;
    /**
     * LEVER_RATE
     */
    private String leverRate;
}
