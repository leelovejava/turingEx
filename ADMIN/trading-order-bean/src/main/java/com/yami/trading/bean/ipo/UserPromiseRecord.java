package com.yami.trading.bean.ipo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_user_promise_record")
public class UserPromiseRecord extends BaseEntity {

    @ApiModelProperty("扣除usdt")
    private BigDecimal deductUsdt;

    @ApiModelProperty("扣除股数")
    private BigDecimal deductNumber;
    private String userId;
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("产品代码（字母）")
    private String productName;

    @ApiModelProperty("产品代码（数字）")
    private String productCode;

    private String orderNo;

    @ApiModelProperty(" 1。 待确认  2 已认缴")
    private int status;

    /**
     * 配置id
     */
    private  String newSharesConfigId;
    /**
     * 锁定截止日期
     */
    private Date lockEndTime;

    private int showFlag;


}
