package com.yami.trading.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.model.WalletExtend;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface WalletExtendMapper extends BaseMapper<WalletExtend> {
    @Update("UPDATE t_wallet_extend " +
            "SET amount = COALESCE(amount, 0) + #{amount}, " +
            "lock_amount = GREATEST(COALESCE(lock_amount, 0), 0) + #{lockAmount}, " +
            "freeze_amount = GREATEST(COALESCE(freeze_amount, 0), 0) + #{freezeAmount}, " +
            "version = COALESCE(version, 0) + 1 " +
            "WHERE party_id = #{partyId} " +
            "AND wallettype = #{walletType} " +
            "AND COALESCE(amount, 0) + #{amount} >= 0 " +
            "AND GREATEST(COALESCE(lock_amount, 0), 0) + #{lockAmount} >= 0 " +
            "AND GREATEST(COALESCE(freeze_amount, 0), 0) + #{freezeAmount} >= 0")
    int updateBalanceDelta(@Param("partyId") String partyId,
                           @Param("walletType") String walletType,
                           @Param("amount") double amount,
                           @Param("lockAmount") double lockAmount,
                           @Param("freezeAmount") double freezeAmount);
}
