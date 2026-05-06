package com.yami.trading.bean.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class UserDataDto implements Serializable {
    /***
     * 用户名（钱包地址）
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * UID
     */
    @ApiModelProperty("userCode")
    private String userCode;

    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String rolename;

    /**
     * USDT账户余额
     */
    @ApiModelProperty("注册 时间")
    private Date createTime;

    @ApiModelProperty("注册时间")
    private long createTimeTs;

    @ApiModelProperty("最后登录时间")
    private Date userLasttime;

    @ApiModelProperty("最后登录时间")
    private long userLasttimeTs;

    @ApiModelProperty("最后登录IP")
    private String  userRegip;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("最后登录IP")
    private String  userLastip;

    @ApiModelProperty("在线状态 ")
    private boolean online;

    @ApiModelProperty("推荐人")
    private String recomUserName;

    @ApiModelProperty("userid")
    private  String userId;

    private int status;

    @ApiModelProperty("是否业务锁定")
    private boolean enabled;

    @ApiModelProperty("登录权限")
    private boolean loginAuthority;

    /**
     * 提现权限
     */
    @ApiModelProperty("提现权限")
    private  boolean withdrawAuthority;

    /**
     * 用户邮箱
     */
    private String userMail;

    /**
     * 邮箱绑定
     */
    private boolean mailBind = false;

    /**
     * 手机号码
     */
    private String userMobile;

    /**
     * 手机绑定
     */
    private boolean userMobileBind = false;

    private int userLevel = 0;
    /**
     * 实名认证
     */
    private boolean realNameAuthority = false;

    /**
     * 借贷状态 1正常 2禁止
     */
    private Integer loanStatus;

    /**
     * 可贷金额(借贷)
     */
    private java.math.BigDecimal loanCanAmount;

    /**
     * 是否老客户 1老客户 2新客户
     */
    private Integer isOldUser;

    /**
     * 已贷金额(借贷)
     */
    private java.math.BigDecimal loanAlreadyAmount;

    /**
     * 购买量化机器状态 1正常 2禁止
     */
    private Integer createRobotStatus;

    /**
     * 提币状态 1正常 2禁止
     */
    private Integer txState;

    /**
     * 期权(杠杆交易)预设结果: -1.亏损,0.未设置,1.盈利
     */
    private Integer optionPreResult;

}
