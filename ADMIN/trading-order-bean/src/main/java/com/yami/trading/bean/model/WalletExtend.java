package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

@Data
@TableName("t_wallet_extend")
public class WalletExtend extends UUIDEntity {

    private static final long serialVersionUID = -926374250240199976L;

    private String partyId;

    private String wallettype;

    /**
     * 金额
     */
    private double amount;

    /**
     * 锁定金额
     */
    private double lockAmount;

    /**
     * 冻结金额
     */
    private double freezeAmount;

    private String name;

    @Version
    private int version;
}
