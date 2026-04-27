package com.yami.trading.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.common.util.RedisUtil;

import java.util.List;

public interface RiskClientService extends IService<RiskClient> {

    public Page<RiskClient> pageListRiskClient(int status, int pageNum, int pageSize);

    RiskClient getRiskClient(String clientKey, String clientType, String type);

}
