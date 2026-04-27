package com.yami.trading.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

/**
 * @author caster
 * @since 2023/11/18
 */
@Service
public class ContextCloseListener implements ApplicationListener<ContextClosedEvent> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 停止 springboot 时调用了一次
	 */
	@Override
    public void onApplicationEvent(ContextClosedEvent event) {
		// 关闭所有的线程池，回收其他资源

    }
}
