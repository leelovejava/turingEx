package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户跟随交易员详情表
 */
@Data
@TableName("T_TRADER_FOLLOW_USER_ORDER")
@Slf4j
public class TraderFollowUserOrder extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;
	
	@TableField("PARTY_ID")
	private String partyId;

	/**
	 * 交易员partyId TRADER_PARTYID
	 */
	@TableField("TRADER_PARTY_ID")
	private String traderPartyId;


	/**
	 * 用户合约持仓单号订单号 USER_ORDER_NO
	 */
	@TableField("USER_ORDER_NO")
	private String userOrderNo;
	/**
	 * 当前订单张数
	 */
	@TableField("VOLUME")
	private double volume;
	
	/**
	 * 跟随的交易员合约持仓单号 TRADER_ORDER_NO
	 */
	@TableField("TRADER_ORDER_NO")
	private String traderOrderNo;

	/**
	 * 状态。submitted 已提交（持仓），canceled 已撤销， created 完成（平仓）
	 */
	@TableField("STATE")
	private String state = "submitted";

}
