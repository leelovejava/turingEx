package com.yami.trading;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.common.manager.email.EmailMessage;
import com.yami.trading.common.manager.email.EmailMessageQueue;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.InternalEmailSenderService;

/**
 * 邮件服务类，负责从短信消息队列取出短信消息并发送
 */
public class EmailServer implements Runnable, InitializingBean {

    @Autowired
    private InternalEmailSenderService internalEmailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(EmailServer.class);

    /**
     * 服务运行：
     * 1. 从消息队列获取message
     * 2.调用currentProvider发送短信
     */
    public void run() {
        while (true) {
            try {
                EmailMessage item = EmailMessageQueue.poll();
                if (null != item) {
                    logger.info("邮寄地址：" + item.getTomail() + "内容:" + item.getContent());
                    internalEmailSenderService.send(item);
                } else {
                    ThreadUtils.sleep(50);
                }
            } catch (Throwable e) {
                logger.error("EmailServer taskExecutor.execute() fail", e);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        logger.info("启动邮件发送服务！");
        ThreadPool.getFixedTaskExecutor(1, "EmailServer").execute(this);
    }

    public void setInternalEmailSenderService(InternalEmailSenderService internalEmailSenderService) {
        this.internalEmailSenderService = internalEmailSenderService;
    }
}
