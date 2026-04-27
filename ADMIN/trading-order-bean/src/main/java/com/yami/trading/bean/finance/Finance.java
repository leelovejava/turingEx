package com.yami.trading.bean.finance;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_finance")
public class Finance extends UUIDEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1639941028310043811L;
	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 产品名称英文
	 */
//	@TableField("`name_en`")
	private String nameEn;
	/**
	 * 产品名称繁体
	 */
	private String nameCn;
	/**
	 * 产品名称 韩语
	 */
	private String nameKn;
	/**
	 * 产品名称 日语
	 */
	private String nameJn;

	/**
	 * 产品图片
	 */
	private String img;
	/**
	 * 周期-天数
	 */
	private int cycle;

	/**
	 * 日利率最低(%)
	 */
	private double dailyRate;
	/**
	 * 日利率最高(%)
	 */
	private double dailyRateMax;
	/**
	 * 今日利率(%)
	 * 
	 */
	private double todayRate;

	/**
	 * 违约结算比例(%)
	 */
	private double defaultRatio;

	/**
	 * 投资金额区间(USDT)
	 */
	private double investmentMin;
	/**
	 * 投资金额区间(USDT)
	 */
	private double investmentMax;

	/**
	 * 状态。0 停用， 1 启用
	 */
	private String state = "0";

	/**
	 * 理财购买币种
	 */
	private String buyCurrency = "usdt";
	
	/**
	 * 理财购买币种
	 */
	private String outputCurrency = "usdt";

}
