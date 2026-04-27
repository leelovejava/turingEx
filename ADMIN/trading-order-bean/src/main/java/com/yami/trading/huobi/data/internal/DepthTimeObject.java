package com.yami.trading.huobi.data.internal;


import com.yami.trading.bean.data.domain.Depth;
import lombok.Data;

import java.io.Serializable;

@Data
public class DepthTimeObject implements Serializable {
	private static final long serialVersionUID = -6508793344391115053L;
	private Depth depth;
}
