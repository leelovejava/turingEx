package com.yami.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.common.manager.sms.SmsMessage;
import com.yami.trading.common.manager.sms.SmsMessageQueue;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.InternalSmsSenderService;

/**
 * 短信服务类，负责从短信消息队列取出短信消息并发送
 */
public class SmsServer implements InitializingBean, Runnable {
	
	@Autowired
	private InternalSmsSenderService internalSmsSenderService;

	private static final Logger logger = LoggerFactory.getLogger(SmsServer.class);

	/**
	 * 服务运行： 1. 从消息队列获取message 2.调用currentProvider发送短信
	 */
	public void run() {
		while (true) {
			try {
				SmsMessage  item = SmsMessageQueue.poll();
				if (null!=item) {
					internalSmsSenderService.send(item);
				} else {
					ThreadUtils.sleep(500);
				}
			} catch (Throwable e) {
				logger.error("SmsServer taskExecutor.execute() fail", e);
			}
		}
	}

	public void afterPropertiesSet() throws Exception {
		logger.info("启动短信(Smsbao)服务！");
    	ThreadPool.getFixedTaskExecutor(1,"SmsbaoServer").execute(this);
	}
}
