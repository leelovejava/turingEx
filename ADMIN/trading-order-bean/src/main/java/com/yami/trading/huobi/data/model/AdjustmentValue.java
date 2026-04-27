package com.yami.trading.huobi.data.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修正值
 */
@Data
public class AdjustmentValue implements Serializable {
	private static final long serialVersionUID = 2896031576741063236L;
	private String symbol;
	/**
	 * 修正值
	 */
	private double value;
	/**
	 * 延长时间，秒
	 */
	private double second;
}
