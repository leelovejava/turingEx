package com.yami.trading.init;

import com.yami.trading.init.cache.LoadNewContractApplyOrderCache;
import com.yami.trading.init.cache.LoadRiskClientCache;
import com.yami.trading.init.cache.LoadTranslateCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author caster
 * @since 2023/11/18
 */
@Service
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {//, Ordered
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
    public int getOrder() {
	    return Ordered.HIGHEST_PRECEDENCE;
    }
	
	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	private LoadNewContractApplyOrderCache loadNewContractApplyOrderCache;

	@Autowired
	private LoadRiskClientCache loadRiskClientCache;

	@Autowired
	private LoadTranslateCache loadTranslateCache;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			loadNewContractApplyOrderCache.loadData();
		} catch (Exception e) {
			logger.error("loadNewContractApplyOrderCache 处理报错: ", e);
		}

		try {
			loadRiskClientCache.loadData();
		} catch (Exception e) {
			logger.error("loadRiskClientCache 处理报错: ", e);
		}

		try {
			loadTranslateCache.loadData();
		} catch (Exception e) {
			logger.error("loadTranslateCache 处理报错: ", e);
		}

	}
	
}