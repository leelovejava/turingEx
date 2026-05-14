package com.yami.trading.service.finance.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class FinanceOrderCreateRecomServiceImpl implements FinanceOrderCreateRecomService {
	@Autowired
	protected FinanceOrderService financeOrderService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected UserService partyService;
	@Autowired
	protected WalletService walletService;
	@Autowired
	protected MoneyLogService moneyLogService;
	/**
	 * 计算推荐人收益
	 */
	protected Map<String, Double> cacheRecomProfit = new ConcurrentHashMap<String, Double>();

	/**
	 * 计算前一日购买产品的订单
	 */
	public void computeRecom() {
		String finance_buy_bonus_parameters = sysparaService.find("finance_buy_bonus_parameters").getSvalue();
		if (StringUtils.isEmptyString(finance_buy_bonus_parameters)) {
			return;
		}
		cacheRecomProfit.clear();
		int pageSize = 1000;
		int pageNo = 1;
		String date = DateUtils.getDateStr(DateUtils.addDate(new Date(), -1));
		while (true) {
			Page page = financeOrderService.pagedQueryByDate(pageNo, pageSize, date);
			List<FinanceOrder> list = (List<FinanceOrder>) page.getRecords();
			if (list.isEmpty()) {
				break;
			}
			for (FinanceOrder order : list) {
				handleCacheRecom(order, finance_buy_bonus_parameters);
			}
			pageNo++;
		}
		saveRecomProfit();
	}

	/**
	 * 购买推荐奖励
	 * 
	 * @param entity
	 */
	public void handleCacheRecom(FinanceOrder entity, String finance_buy_bonus_parameters) {
		List<UserRecom> list_parents = this.userRecomService.getParents((String)entity.getPartyId());

		if (CollectionUtils.isNotEmpty(list_parents)) {
			String[] finance_buy_bonus_array = finance_buy_bonus_parameters.split(",");
			int loop = 0;
			int loopMax = finance_buy_bonus_array.length;
			for (int i = 0; i < list_parents.size(); i++) {
				if (loop >= loopMax) {
					break;
				}
				User party_parent = this.partyService.cacheUserBy(list_parents.get(i).getRecomUserId());
				if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRoleName())) {
					continue;
				}
				loop++;
				double pip_amount = Double.valueOf(finance_buy_bonus_array[loop - 1]);
				double get_money = Arith.mul(entity.getAmount(), pip_amount);

				if (cacheRecomProfit.containsKey(party_parent.getUserId())) {
					cacheRecomProfit.put(party_parent.getUserId(),
							Arith.add(cacheRecomProfit.get(party_parent.getUserId()), get_money));
				} else {
					cacheRecomProfit.put(party_parent.getUserId(), get_money);
				}
			}

		}
	}

	public void saveRecomProfit() {
		if (cacheRecomProfit.isEmpty()) {
			return;
		}
		for (Entry<String, Double> entry : cacheRecomProfit.entrySet()) {
			Wallet wallet_parent = walletService.saveWalletByPartyId(entry.getKey());
			BigDecimal amount_before_parent = wallet_parent.getMoney();
			walletService.update(wallet_parent.getUserId(), entry.getValue());
			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneyLog.setAmountBefore(amount_before_parent);
			moneyLog.setAmount(BigDecimal.valueOf(entry.getValue()));
			moneyLog.setAmountAfter(BigDecimal.valueOf(Arith.add(wallet_parent.getMoney(), BigDecimal.valueOf(entry.getValue()))));
			// 下级购买理财佣金奖励
			moneyLog.setLog("Subordinate finance purchase commission reward");
			moneyLog.setUserId(entry.getKey());
			moneyLog.setWalletType(Constants.WALLET);
			moneyLog.setContentType(WalletConstants.MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT);
			moneyLogService.save(moneyLog);
		}
	}

	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
		this.financeOrderService = financeOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setPartyService(UserService partyService) {
		this.partyService = partyService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setCacheRecomProfit(Map<String, Double> cacheRecomProfit) {
		this.cacheRecomProfit = cacheRecomProfit;
	}

}
