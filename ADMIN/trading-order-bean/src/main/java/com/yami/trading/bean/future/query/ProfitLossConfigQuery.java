package com.yami.trading.bean.future.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 交割合约DTO
 *
 * @author lucas
 * @version 2023-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProfitLossConfigQuery {

    private static final long serialVersionUID = 1L;

    private String namePara;

    private List<String> children;

    private String userName;

    private String symbolType;
}
