package com.yami.trading.bean.exchange;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_exchange_lever")
public class ExchangeLever  extends UUIDEntity {


    private double leverRate;

    private String  symbol;
}
