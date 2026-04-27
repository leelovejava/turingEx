package com.yami.trading.init.cache;

import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.service.contract.ContractApplyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统启动时，将合约单的时间戳加载到缓存，提升后台定时轮询任务的效率，减少数据库压力
 *
 * @author caster
 * @since 2023/11/23
 **/
@Service
@Slf4j
public class LoadNewContractApplyOrderCache {
    @Autowired
    private ContractApplyOrderService contractApplyOrderService;

    public void loadData() {
        try {
            List<ContractApplyOrder> allNewOrderList = contractApplyOrderService.findSubmitted();
            for (ContractApplyOrder oneOrder : allNewOrderList) {
                RedisUtil.sadd(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, oneOrder.getUuid());
            }
        } catch (Exception e) {
            log.error("------> LoadNewContractApplyOrderCache.loadData 执行 loadData 方法报错: ", e);
        }
    }
}
