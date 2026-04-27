package com.yami.trading.bean.data.query;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class QueryRealtimeDTO implements Serializable {
	private static final long serialVersionUID = 6008419088288211150L;

	private String symbol;

	private Long ts;

	private String name;

	private String enName;

	private Double open;

	private Double high;

	private Double low;

	private Double close;

	private Double amount;

	private Double volume;

	private Double changeRatio;

	private Double netChange;

	private String currentTime;

	private String sorted;

	private Double chg;

	private String category;

	private String type;

}
