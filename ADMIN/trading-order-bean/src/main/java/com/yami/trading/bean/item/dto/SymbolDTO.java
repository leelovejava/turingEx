package com.yami.trading.bean.item.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 永续合约
 * @author lucas
 * @version 2023-03-10
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = false)
public class SymbolDTO {

	private static final long serialVersionUID = 1L;

	private String uuid;

	/**
	 * 币种全称
	 */
	private String symbolFullName;

	/**
	 * 币种名称
	 */
	@NotBlank(message = "名称不能为空")
	@ApiModelProperty("币种名称")
	private String name;


	private String enName;
	/**
	 * 代码
	 */
	@ApiModelProperty("代码")
	@NotBlank(message = "代码不能为空")
	private String symbol;

	/**
	 * 数据源编码
	 */
	private String symbolData;

	/**
	 * 小数位精度
	 */
	@NotNull
	@Min(value = 0, message = "精度必须大于等于0")
	@ApiModelProperty("精度")
	private Integer decimals;

	private List<ItemLeverageDTO> levels;

	@ApiModelProperty("是否置顶")
	private String isTop;

	@ApiModelProperty("类型")
	private String type;

	@ApiModelProperty("类别")
	private String category;

	/**
	 * 成交金额
	 */
	@ApiModelProperty("成交金额")
	private Double amount;

	/**
	 * 成交额量
	 */
	@ApiModelProperty("成交量")
	private Double volume;

	/**
	 * 涨跌幅
	 */
	@TableField(exist = false)
	@ApiModelProperty("涨跌幅")
	private Double changeRatio;

	/**
	 * 涨跌幅
	 */
	@TableField(exist = false)
	@ApiModelProperty("涨跌额")
	private Double chg;

	/**
	 * 时间戳
	 */
	@ApiModelProperty("时间戳")
	@TableField(exist = false)
	private Long ts;

	/**
	 * 时间戳
	 */
	@ApiModelProperty("时间戳")
	@TableField(exist = false)
	private Long current_time;

	/**
	 * 最新价
	 */
	@ApiModelProperty("当前价格")
	@TableField(exist = false)
	private Double close;
}
