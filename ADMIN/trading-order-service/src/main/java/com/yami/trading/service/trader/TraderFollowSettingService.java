package com.yami.trading.service.trader;

import com.yami.trading.bean.trader.domain.TraderFollowSetting;

public interface TraderFollowSettingService {
    TraderFollowSetting findByPartyId(String partyId);

    TraderFollowSetting findById(String id);

    int update(TraderFollowSetting traderFollowSetting);
}
