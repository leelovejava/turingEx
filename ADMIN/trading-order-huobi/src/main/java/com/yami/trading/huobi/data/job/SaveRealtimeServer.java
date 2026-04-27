package com.yami.trading.huobi.data.job;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.MD5;
import com.yami.trading.common.util.ApplicationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.internal.DataDBService;
import javax.annotation.Resource;

@Component
@Slf4j
public class SaveRealtimeServer implements Runnable {
	
	@Resource
	private DataDBService dataDBService;
	
	public void start() {
		new Thread(this, "SaveRealtimeServer").start();
		log.info("启动SaveRealtimeServer！");
	}

	@Override
	public void run() {
		while (true) {
			try {
				int size = RealtimeQueue.size();
				List<Realtime> list = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					Realtime item = RealtimeQueue.poll();
					item.setUuid(ApplicationUtil.getCurrentTimeUUID());
					item.setTableIndex(mod(item.getSymbol()));
					list.add(item);
				}

				if (list.size() > 0) {
					Map<Integer, List<Realtime>> realtimePartition = list.stream().collect(Collectors.groupingBy(Realtime::getTableIndex));
					for (int i : realtimePartition.keySet()) {
						List<Realtime> partList = realtimePartition.get(i);
						if (CollectionUtil.isNotEmpty(partList)) {
							long start = System.currentTimeMillis();
							log.info("入庫realtime_{} 开始", i);
							dataDBService.saveBatch(partList);
							log.info("入庫realtime_{} 结束，入库 {}条， 耗时:{} ms", i, partList.size(), (System.currentTimeMillis() - start));
						}
					}
				}
			} catch (Throwable e) {
				log.error("SaveRealtimeServer run fail ->{}", e.getMessage());
			} finally {
				ThreadUtils.sleep(5* 60 * 1000);
			}
		}
	}

	public static int mod(String str) {
		BigInteger bigInt = new BigInteger(1, MD5.create().digest(str));
		return bigInt.mod(BigInteger.TEN).abs().intValue();
	}
}
