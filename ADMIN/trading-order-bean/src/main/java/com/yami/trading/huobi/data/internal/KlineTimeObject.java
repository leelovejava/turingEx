package com.yami.trading.huobi.data.internal;


import com.yami.trading.bean.data.domain.Kline;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class KlineTimeObject implements Serializable {
	private static final long serialVersionUID = -5777137609729197999L;
	private List<Kline> kline = new ArrayList<>();
}
