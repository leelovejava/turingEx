package com.yami.trading.bean.ipo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_prospectus")
public class Prospectus  extends BaseEntity {
    /**
     *
     * 募集资金
     */
    private BigDecimal raisedFunds;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;


    @ApiModelProperty("产品代码（数字）")
    private  String productCode;

    /**
     * 配置id
     */
    private  String newSharesConfigId;
}
