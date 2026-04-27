package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.util.Date;

/**
 * 资金划转记录
 *
 */
@Data
@TableName("t_wallet_transfer_log")
public class WalletTransferLog extends UUIDEntity {
	private static final long serialVersionUID = 5914244062518608589L;

	private String partyId;

	private String orderNo;

	private double amount = 0.0D;

	// 日志类型，见Constants
	private String category;

	private int status;

	// 创建时间
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	/**
	 * 创建日期
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createTimeTs;

	@TableField(exist = false)
	private String createTimeStr;

	/**
	 * 交易币种
	 */
	private String wallettype;

}
