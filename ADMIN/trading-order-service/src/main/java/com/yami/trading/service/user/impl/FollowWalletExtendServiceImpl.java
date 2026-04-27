package com.yami.trading.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.FollowWalletExtend;
import com.yami.trading.dao.FollowWalletExtendMapper;
import com.yami.trading.service.user.FollowWalletExtendService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class FollowWalletExtendServiceImpl extends ServiceImpl<FollowWalletExtendMapper, FollowWalletExtend> implements FollowWalletExtendService {

    @Override
    public List<FollowWalletExtend> findByUserIdAndWallettype(String userId, String wallettype) {
        return list(Wrappers.<FollowWalletExtend>query().lambda().eq(FollowWalletExtend::getPartyId,userId).eq(FollowWalletExtend::getWallettype,wallettype));
    }

    @Override
    public List<FollowWalletExtend> findByUserIdAndWallettype(String partyId, List<String> list_symbol) {
        return list(Wrappers.<FollowWalletExtend>query().lambda().eq(FollowWalletExtend::getPartyId,partyId).in(FollowWalletExtend::getWallettype,list_symbol));
    }

    @Override
    public List<FollowWalletExtend> findByUserId(Serializable partyId) {
        return list(Wrappers.<FollowWalletExtend>query().lambda().eq(FollowWalletExtend::getPartyId,partyId));
    }
}
