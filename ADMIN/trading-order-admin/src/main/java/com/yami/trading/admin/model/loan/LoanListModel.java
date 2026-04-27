package com.yami.trading.admin.model.loan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class LoanListModel extends PageRequest {
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("账号类型 ")
    private String roleName;
    @ApiModelProperty("用户名 uuid")
    private String userName;
    @ApiModelProperty("订单状态: 1计息中;2已结清;3强平结清;4已逾期")
    private String state;
}
