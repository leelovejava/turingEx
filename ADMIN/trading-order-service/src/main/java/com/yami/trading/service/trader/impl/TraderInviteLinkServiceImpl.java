package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.bean.trader.domain.TraderInviteLink;
import com.yami.trading.dao.trader.TraderInviteLinkMapper;
import com.yami.trading.service.trader.TraderInviteLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TraderInviteLinkServiceImpl implements TraderInviteLinkService {

    @Resource
    private TraderInviteLinkMapper traderInviteLinkMapper;

    @Override
    public List<TraderInviteLink> list() {
        return traderInviteLinkMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public int save(TraderInviteLink traderInviteLink) {
        return traderInviteLinkMapper.insert(traderInviteLink);
    }

    @Override
    public int update(TraderInviteLink traderInviteLink) {
        return traderInviteLinkMapper.updateById(traderInviteLink);
    }

    @Override
    public TraderInviteLink selectById(String uuid) {
        return traderInviteLinkMapper.selectById(uuid);
    }

    @Override
    public int delete(String uuid) {
        return traderInviteLinkMapper.deleteById(uuid);
    }

    @Override
    public TraderInviteLink findByTraderIdAndStatus(String userId, int status) {
        return traderInviteLinkMapper.selectOne(Wrappers.<TraderInviteLink>lambdaQuery().eq(TraderInviteLink::getTraderId, userId).eq(TraderInviteLink::getStatus, status));
    }
}
