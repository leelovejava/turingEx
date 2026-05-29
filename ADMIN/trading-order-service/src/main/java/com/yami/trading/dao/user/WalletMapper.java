package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletMapper extends BaseMapper<Wallet> {

    BigDecimal sumMoney(@Param("children")  List<String> children);

    @Update("UPDATE tz_wallet " +
            "SET money = COALESCE(money, 0) + #{amount}, " +
            "lock_money = GREATEST(COALESCE(lock_money, 0), 0) + #{lockAmount}, " +
            "freeze_money = GREATEST(COALESCE(freeze_money, 0), 0) + #{freezeAmount}, " +
            "version = COALESCE(version, 0) + 1 " +
            "WHERE user_id = #{partyId} " +
            "AND COALESCE(money, 0) + #{amount} >= 0 " +
            "AND GREATEST(COALESCE(lock_money, 0), 0) + #{lockAmount} >= 0 " +
            "AND GREATEST(COALESCE(freeze_money, 0), 0) + #{freezeAmount} >= 0")
    int updateBalanceDelta(@Param("partyId") String partyId,
                           @Param("amount") BigDecimal amount,
                           @Param("lockAmount") BigDecimal lockAmount,
                           @Param("freezeAmount") BigDecimal freezeAmount);

}
