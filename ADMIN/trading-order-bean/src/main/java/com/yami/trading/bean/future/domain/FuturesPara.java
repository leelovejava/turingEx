package com.yami.trading.bean.future.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 交割合约管理Entity
 * @author lucas
 * @version 2023-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_futures_para")
public class FuturesPara extends BaseEntity {
	public final static String TIMENUM_SECOND = "second";
	public final static String TIMENUM_MINUTE = "minute";
	public final static String TIMENUM_HOUR = "hour";
	public final static String TIMENUM_DAY = "day";

	public enum TIMENUM {
		second(TIMENUM_SECOND, "秒"), minute(TIMENUM_MINUTE, "分"), hour(TIMENUM_HOUR, "时"), day(TIMENUM_DAY, "天");

		private String timenum;
		private String cn;

		TIMENUM(String timenum, String cn) {
			this.timenum = timenum;
			this.cn = cn;
		}

		public String getCn() {
			return this.cn;
		}

		public String getTimenum() {
			return timenum;
		}

		public static String getCnByTimeunit(String timeunit){

			TIMENUM[] values = TIMENUM.values();
			for(TIMENUM value : values){
				if(value.getTimenum().equalsIgnoreCase(timeunit)){
					return value.getCn();
				}
			}
			return "";
		}

	}
	private static final long serialVersionUID = 1L;

	/**
     * SYMBOL
     */
	@ApiModelProperty("币种")
	private String symbol;
	/**
     * TIMENUM
     */
	@ApiModelProperty("时间")
	private Integer timenum;
	@ApiModelProperty("时间字符串")
	@TableField(exist = false)
	private String timenumStr;
	/**
     * TIMEUNIT
     */
	@ApiModelProperty("时间单位")
	private String timeunit;
	/**
     * UNIT_AMOUNT
     */
	@ApiModelProperty(" 最低购买金额")
	private Double unitAmount;
	/**
     * UNIT_MAX_AMOUNT
     */
	@ApiModelProperty(" 每手最高价格")
	private BigDecimal unitMaxAmount;
	/**
     * PROFIT_RATIO
     */
	@ApiModelProperty(" 浮动最小收益率")
	private BigDecimal profitRatio;
	/**
     * UNIT_FEE
     */
	@ApiModelProperty(" 手续费(%)")
	private BigDecimal unitFee;
	/**
     * PROFIT_RATIO_MAX
     */
	@ApiModelProperty(" 浮动最大收益率")
	private BigDecimal profitRatioMax;

	@ApiModelProperty("收益率基数，例如：10000，1000，1000整数类型，默认10000")
	private Integer profitRatioCardinality;
	//ALTER TABLE `t_futures_para`
	//ADD COLUMN `profit_ratio_cardinality`  int(11) NULL DEFAULT 10000 COMMENT '比率基数，例如：10000，1000，1000整数类型，默认10000' AFTER `profit_ratio`;

	 @TableField(exist = false)
	private String profitRatioFront;
	@TableField(exist = false)
	 private BigDecimal buyMin;
	@TableField(exist = false)
	 private BigDecimal buyMax;
}
