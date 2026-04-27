package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletMapper extends BaseMapper<Wallet> {

    BigDecimal sumMoney(@Param("children")  List<String> children);

}
