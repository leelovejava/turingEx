package com.yami.trading.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.FollowWalletExtend;

import java.io.Serializable;
import java.util.List;

public interface FollowWalletExtendService extends IService<FollowWalletExtend> {

    List<FollowWalletExtend> findByUserIdAndWallettype(String userId, String wallettype);

    List<FollowWalletExtend> findByUserIdAndWallettype(String partyId, List<String> list_symbol);

    List<FollowWalletExtend> findByUserId(Serializable partyId);
}
