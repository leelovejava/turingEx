package com.yami.trading.service.trader;

import com.yami.trading.bean.trader.domain.TraderDaysSetting;

import java.util.List;

/**
 * 交易跟单利息设置
 */
public interface TraderDaysSettingService {

	List<TraderDaysSetting> list();

	int save(TraderDaysSetting traderDaysSetting);

	int update(TraderDaysSetting traderDaysSetting);

	TraderDaysSetting selectById(String uuid);

	int delete(String uuid);
}
