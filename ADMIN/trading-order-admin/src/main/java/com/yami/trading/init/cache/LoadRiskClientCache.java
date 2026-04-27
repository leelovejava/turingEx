package com.yami.trading.init.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.service.user.RiskClientService;
import com.yami.trading.security.common.util.RiskClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统启动时，加载风控配置信息到内存
 *
 * @author caster
 * @since 2023/12/04
 **/
@Service
@Slf4j
public class LoadRiskClientCache {
    @Autowired
    private RiskClientService riskClientService;

    public void loadData() {
        log.info("------> LoadRiskClientCache.loadData 开始将风控配置数据加载到缓存...");
        int pageNum = 1;
        int pageSize = 100;
        try {
            Map<String, RiskClient> allRiskClientMap = new ConcurrentHashMap<>();
            while (true) {
                Page<RiskClient> pageData = riskClientService.pageListRiskClient(1, pageNum, pageSize);
                List<RiskClient> pageList = pageData.getRecords();
                if (CollectionUtil.isEmpty(pageList)) {

                    break;
                }

                for (RiskClient oneRiskClient : pageList) {
                    if (oneRiskClient.getStatus() != 1) {
                        continue;
                    }
                    allRiskClientMap.put(oneRiskClient.getType() + ":" + oneRiskClient.getClientType() + ":" + oneRiskClient.getClientKey(), oneRiskClient);
                }

                pageNum++;
            }

            RiskClientUtil.initCache(allRiskClientMap);
            log.info("------> LoadRiskClientCache.loadData 完成风控配置的加载");
        } catch (Exception e) {
            log.error("------> LoadRiskClientCache.loadData 执行 loadData 方法报错: ", e);
        }
    }
}
