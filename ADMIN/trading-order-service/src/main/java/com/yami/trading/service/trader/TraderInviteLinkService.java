package com.yami.trading.service.trader;

import com.yami.trading.bean.trader.domain.TraderInviteLink;

import java.util.List;

/**
 * 交易跟单利息设置
 */
public interface TraderInviteLinkService {

	List<TraderInviteLink> list();

	int save(TraderInviteLink traderInviteLink);

	int update(TraderInviteLink traderInviteLink);

	TraderInviteLink selectById(String uuid);

	int delete(String uuid);

    TraderInviteLink findByTraderIdAndStatus(String userId, int status);
}
