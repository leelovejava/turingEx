package com.yami.trading.admin.controller.exchange.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class ExchangeLeverOrderModel  extends PageRequest {

    @ApiModelProperty("账号类型 ")
    private  String  roleName;

    @ApiModelProperty("状态。submitted 已提交（持仓）， created 完成（平仓）")
    private  String status;

    @ApiModelProperty("uuid")
    private  String userName;



    @ApiModelProperty("截止申购日期 2023-03-22 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束日期 2023-03-22 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    private  String orderNo;






}
