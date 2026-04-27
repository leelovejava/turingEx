package com.yami.trading.bean.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class UserDto  implements Serializable {

    private String userId;
    /***
     * 用户名（钱包地址）
     */
    @ApiModelProperty("用户名")
    private  String userName;

    @ApiModelProperty("邮箱")
    private String userMail;

    @ApiModelProperty("手机号")
    private String userMobile;

    /**
     * 真实姓名
     */

    private String realName;
    /**
     * UID
     */
    @ApiModelProperty("userCode")
    private  String userCode;
    /**
     * USDT账户余额
     */
    @ApiModelProperty("USDT账户余额")
    private  BigDecimal money;
    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String rolename;
    /**
     * 提现限制流水
     */
    @ApiModelProperty("提现限制流水")
    private BigDecimal withdrawLimitAmount;
    /**
     * 用户当前流水
     */

    @ApiModelProperty("用户当前流水")
    private  BigDecimal withdrawLimitNowAmount;

    @ApiModelProperty("注册时间")
    private Date createTime;//create_time  createTimeTs


}
