package com.yami.trading.huobi.data.job;

import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import com.yami.trading.bean.data.domain.Realtime;

@Slf4j
public class RealtimeQueue {
	
	private static ConcurrentLinkedQueue<Realtime> WORKING_EVENTS = new ConcurrentLinkedQueue<>();

	public static void add(Realtime item) {
		Assert.notNull(item, "The item must not be null.");
		try {
			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			log.error("RealtimeQueue add fail -> {}", e.getMessage());
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static Realtime poll() {
		Realtime item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			log.error("RealtimeQueue poll() fail  -> {}", e.getMessage());
		}
		return item;
	}
}
