package com.yami.trading.huobi.data.internal;

import com.yami.trading.bean.data.domain.Trend;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrendTimeObject implements Serializable {
	private static final long serialVersionUID = -4078190280148061255L;
	private List<Trend> trend;
}
