package com.yami.trading.common.core;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.Ordered;

/**
 * @author JORGE
 * @description 抽象应用监听器
 */
public abstract class AbstractApplicationListener implements SpringApplicationRunListener,Ordered{

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
