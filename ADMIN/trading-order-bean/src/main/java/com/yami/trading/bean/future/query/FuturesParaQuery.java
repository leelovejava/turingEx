package com.yami.trading.bean.future.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 交割合约管理DTO
 * @author lucas
 * @version 2023-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FuturesParaQuery {

	private static final long serialVersionUID = 1L;
	private String symbol;
}
