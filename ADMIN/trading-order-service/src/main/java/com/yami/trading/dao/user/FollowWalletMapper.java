package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.Wallet;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FollowWalletMapper extends BaseMapper<FollowWallet> {

    BigDecimal sumMoney(@Param("children")  List<String> children);

}
