package com.yami.trading.service.miner.web;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerPara;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.miner.service.AdminMinerParaService;
import com.yami.trading.service.miner.service.MinerParaService;
import com.yami.trading.service.miner.service.MinerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AdminMinerParaAction {
	
	private static Log logger = LogFactory.getLog(AdminMinerParaAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2606134812043922161L;

	private AdminMinerParaService adminMinerParaService;
	private MinerParaService minerParaService;
	private MinerService minerService;
	
	/**
	 * id
	 */
	private String id;
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
	
	private String miner_name;
	
	public String list() {
		int pageNo = 1;
		int pageSize = 30;
		Miner miner = minerService.findById(miner_id);
		miner_name = miner.getName();
		Page page = adminMinerParaService.pagedQuery(pageNo, pageSize, miner_id);
		return "list";
	}
	public String toAdd() {
		return "add";
	}
	public String toUpdate() {
		MinerPara minerPara = minerParaService.findById(id);
		try {
			if(minerPara==null) {
				throw new BusinessException("参数不存在，刷新重试");
			}
			miner_id = minerPara.getMiner_id();
			cycle = minerPara.getCycle();
			amount = minerPara.getAmount();
			return "update";
		} catch (BusinessException e) {
//			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error("update error ", t);
//			this.error = "程序错误";
			return list();
		}
	}
	
	public String update() {
		MinerPara minerPara = minerParaService.findById(id);
		try {
			minerPara.setCycle(cycle);
			minerPara.setAmount(amount);
			minerParaService.update(minerPara);
//			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
//			this.error = e.getMessage();
			return toUpdate();
		} catch (Throwable t) {
			logger.error("update error ", t);
//			this.error = "程序错误";
			return toUpdate();
		}
	}
	public String add() {
		try {
			MinerPara minerPara  = new MinerPara();
			minerPara.setCycle(cycle);
			minerPara.setAmount(amount);
			minerPara.setMiner_id(miner_id);
//			minerParaService.save(minerPara);
//			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
//			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("update error ", t);
//			this.error = "程序错误";
			return toAdd();
		}
	}
	
	public String getMiner_id() {
		return miner_id;
	}
	public int getCycle() {
		return cycle;
	}
	public double getAmount() {
		return amount;
	}
	public void setMiner_id(String miner_id) {
		this.miner_id = miner_id;
	}
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setAdminMinerParaService(AdminMinerParaService adminMinerParaService) {
		this.adminMinerParaService = adminMinerParaService;
	}
	public void setMinerParaService(MinerParaService minerParaService) {
		this.minerParaService = minerParaService;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMinerService(MinerService minerService) {
		this.minerService = minerService;
	}
	public String getMiner_name() {
		return miner_name;
	}
	public void setMiner_name(String miner_name) {
		this.miner_name = miner_name;
	}
	
	
	
}
