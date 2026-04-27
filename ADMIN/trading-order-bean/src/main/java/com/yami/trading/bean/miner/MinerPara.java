package com.yami.trading.bean.miner;


import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 矿机配置参数
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_miner_para")
public class MinerPara extends UUIDEntity {

	private static final long serialVersionUID = 1639941028310043811L;

	/**
	 * 矿机id
	 */
	private String miner_id;
	/**
	 * 购买周期
	 */
	private int cycle;

	/**
	 * 购买价格
	 */
	private double amount;

	public int getCycle() {
		return cycle;
	}

	public double getAmount() {
		return amount;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMiner_id() {
		return miner_id;
	}

	public void setMiner_id(String miner_id) {
		this.miner_id = miner_id;
	}

	
	
	


	
	
}
