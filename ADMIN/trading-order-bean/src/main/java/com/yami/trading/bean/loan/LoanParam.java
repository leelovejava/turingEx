package com.yami.trading.bean.loan;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author JORGE
 * @description 借贷参数实体类
 * 每个实例代表一个配置项,配置项中每个复合字段值使用英文逗号分割值
 */
@TableName("t_loan_param")
@Data
public class LoanParam implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -1590224831484585648L;

	@Setter
	@Getter
	private int uuid;

	/**
	 * 借贷期限(天)
	 * exam:1,3,7,30,90,180
	 */
	private String term;
	
	/**
	 * 借贷最大额度
	 * exam:USDT:1000,ETH:800,BTC:300
	 */
	private String max_quota;
	
	/**
	 * 借贷最小额度
	 * exam:USDT:100,ETH:80,BTC:30
	 */
	private String min_quota;
	
	/**
	 * 还款周期(天)
	 * exam:3,7,30,60,90,180,360
	 */
	private String repay_cycle;
	
	/**
	 * 日利率(浮点数,百分比)
	 * exam:0.0003,0.0006,0.001
	 */
	private String daily_rate;
	
	/**
	 * 还款方式
	 * exam:1:到期还本息,2:到期还本金,3:到期还利息
	 */
	private String repayment;
	
	/**
	 * 放款机构ID
	 */
	private String lending_institution;
	
	/**
	 * 放款机构名称
	 */
//	@TableField("LENDING_NAME")
	private String lending_name;
	
}
