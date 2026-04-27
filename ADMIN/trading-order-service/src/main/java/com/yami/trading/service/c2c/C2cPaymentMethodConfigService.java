package com.yami.trading.service.c2c;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.ChannelBlockchain;

import java.util.Map;

public interface C2cPaymentMethodConfigService  extends IService<C2cPaymentMethodConfig> {
    Map<String, String> getMethodConfigMap();

    Map<String, C2cPaymentMethodConfig> getBankCardPMethodConfigMap();

    Map<String, C2cPaymentMethodConfig> getC2cPMethodConfigMap();

    C2cPaymentMethodConfig add(C2cPaymentMethodConfig cfg);

    Map<String, String> getMethodConfigIdTypeMap();

    void delete(String id);

    C2cPaymentMethodConfig get(String id);

}
