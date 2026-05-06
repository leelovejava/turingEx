package com.yami.trading.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UpdateUserModel {

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("登录权限")
    private boolean loginAuthority;

    @ApiModelProperty("提现权限")
    private boolean withdrawAuthority;

    @ApiModelProperty("是否业务锁定")
    private boolean enabled;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("用户等级")
    private int userLevel;
    /**
     * 实名认证
     */
    @ApiModelProperty("实名认证")
    private Boolean realNameAuthority;

    /**
     * 借贷状态 1正常 2禁止
     */
    @ApiModelProperty("借贷状态 1正常 2禁止")
    private Integer loanStatus;

    /**
     * 可贷金额(借贷)
     */
    @ApiModelProperty("可贷金额(借贷)")
    private java.math.BigDecimal loanCanAmount;

    /**
     * 是否老客户 1老客户 2新客户
     */
    @ApiModelProperty("是否老客户 1老客户 2新客户")
    private Integer isOldUser;

    /**
     * 已贷金额(借贷)
     */
    @ApiModelProperty("已贷金额(借贷)")
    private java.math.BigDecimal loanAlreadyAmount;

    /**
     * 购买量化机器状态 1正常 2禁止
     */
    @ApiModelProperty("购买量化机器状态 1正常 2禁止")
    private Integer createRobotStatus;

    /**
     * 提币状态 1正常 2禁止
     */
    @ApiModelProperty("提币状态 1正常 2禁止")
    private Integer txState;

    /**
     * 期权(杠杆交易)预设结果: -1.亏损,0.未设置,1.盈利
     */
    @ApiModelProperty("期权(杠杆交易)预设结果: -1.亏损,0.未设置,1.盈利")
    private Integer optionPreResult;

}
