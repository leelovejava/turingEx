package com.yami.trading.service.finance.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.finance.service.AdminFinanceOrderService;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.finance.FinanceOrderMapper;
import com.yami.trading.service.user.UserRecomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminFinanceOrderServiceImpl extends ServiceImpl<FinanceOrderMapper, FinanceOrder> implements AdminFinanceOrderService {
//	private PagedQueryDao pagedQueryDao;
	@Autowired
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String finance_para, String status_para,
						   List<String> children, String orderNo, String rolename_para , String createTime , String closeTime) {

//		Map<String, Object> parameters = new HashMap<>();
//		StringBuffer queryString = new StringBuffer(
//				" SELECT financeOrder.UUID id,financeOrder.ORDER_NO order_no  ,financeOrder.FINANCE_ID financeId  , ");
//		queryString.append(" financeOrder.AMOUNT amount,financeOrder.CREATE_TIME create_time, ");
//		queryString.append("  financeOrder.CLOSE_TIME close_time,financeOrder.PROFIT profit, ");
//		queryString.append(" financeOrder.STATE state, ");
//		queryString.append(" party.user_name username,party.user_code usercode,party.role_name rolename, ");
//		queryString.append(" finance.NAME finance_name,finance.NAME_EN finance_name_en, ");
//		queryString.append(" finance.NAME_KN finance_name_kn,finance.NAME_JN finance_name_jn ");
//		queryString.append(" FROM T_FINANCE_ORDER financeOrder   ");
//		queryString.append(" LEFT JOIN  party ON financeOrder.PARTY_ID = party.UUID  ");
//		queryString.append(" LEFT JOIN T_FINANCE finance ON finance.UUID = financeOrder.FINANCE_ID ");
//
//		queryString.append(" WHERE 1 = 1 ");
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			queryString.append(" and  financeOrder.PARTY_ID in (:partyId) ");
//			parameters.put("partyId", children);
//		}
//
//		if (!StringUtils.isNullOrEmpty(finance_para)) {
//			queryString.append(
//					" and  (finance.NAME=:finance_para or finance.NAME_EN =:finance_para or finance.NAME_CN =:finance_para) ");
//			parameters.put("finance_para", finance_para);
//		}
//
//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append("AND (party.user_name like:username OR party.user_code like:username ) ");
//			parameters.put("username", "%" + name_para + "%");
//		}
//		if (!StringUtils.isNullOrEmpty(status_para)) {
//			queryString.append(" and  financeOrder.STATE  =:status_para ");
//			parameters.put("status_para", status_para);
//		}
//		if (!StringUtils.isNullOrEmpty(orderNo)) {
//			queryString.append(" and financeOrder.ORDER_NO = :orderNo  ");
//			parameters.put("orderNo", orderNo);
//
//		}
//		if (!StringUtils.isNullOrEmpty(rolename_para)) {
//			queryString.append(" and   party.role_name =:rolename");
//			parameters.put("rolename", rolename_para);
//		}
//
//		queryString.append(" order by financeOrder.CREATE_TIME desc ");

//		Page page = null;
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		return page;

		Page page=new Page(pageNo,pageSize);
		return baseMapper.pagedQuery1(page,name_para,finance_para,status_para, children, orderNo, rolename_para , createTime , closeTime);

	}


}
