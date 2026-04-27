package com.yami.trading.admin.model.c2c;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class C2cPaymentMethodConfigListModel  extends PageRequest {
    /**
     * 参考枚举类型： PayMethodTypeEnum
     */
    @ApiModelProperty("* 支付方式类型：1银行卡/2虚拟货币/3微信/4支付宝/5PayPal/6西联汇款/7SWIFT国际汇款/ 8其它")
    private String methodType;

    /**
     * 支付方式名称
     */
    @ApiModelProperty("支付方式名称")
    private String methodName;

    private  int type;
}
