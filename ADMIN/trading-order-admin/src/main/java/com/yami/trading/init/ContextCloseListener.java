package com.yami.trading.init;

import com.yami.trading.admin.controller.loanOrder.job.LoanCloseoutJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired(required = false)
	private LoanCloseoutJob loanCloseoutJob;

	@Override
    public void onApplicationEvent(ContextClosedEvent event) {
		logger.info("Application context is closing, stopping LoanCloseoutJob...");
		if (loanCloseoutJob != null) {
			loanCloseoutJob.stop();
		}
    }
}
