package com.yami.trading.service.ipo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.model.MoneyLog;

public interface NewSharesConfigService extends IService<NewSharesConfig> {

    NewSharesConfig  getByProductCode(String productCode);

    void saveNewSharesConfig(NewSharesConfig config,String enName);
}
