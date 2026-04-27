package com.yami.trading.service.miner.service.impl;

import java.util.List;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.miner.service.AdminMinerOrderService;
import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.miner.service.MinerService;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.miner.MinerOrderMapper;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminMinerOrderServiceImpl extends ServiceImpl<MinerOrderMapper, MinerOrder> implements AdminMinerOrderService {
//	protected PagedQueryDao pagedQueryDao;
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected MinerOrderService minerOrderService;
	@Autowired
	protected MinerService minerService;
	@Autowired
	protected UserService partyService;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String miner_para, String status_para,
						   List<String> children, String orderNo, String rolename_para) {

//		Map<String, Object> parameters = new HashMap<>();
//		StringBuffer queryString = new StringBuffer(
//				" SELECT minerOrder.UUID id,minerOrder.ORDER_NO order_no  ,minerOrder.MINER_ID minerId  , ");
//		queryString.append(" minerOrder.AMOUNT amount,minerOrder.CREATE_TIME create_time,minerOrder.BASE_COMPUTE_AMOUNT base_compute_amount, ");
//		queryString.append(" minerOrder.EARN_TIME earn_time,minerOrder.STOP_TIME stop_time,minerOrder.PROFIT profit, ");
//		queryString.append(" minerOrder.STATE state,minerOrder.CLOSE_TIME close_time,minerOrder.DEFAULT_MONEY default_money, ");
//		queryString.append(" party.user_name username,party.user_code usercode,party.role_name rolename, ");
//		queryString.append(" miner.NAME miner_name,miner.NAME_EN miner_name_en ");
//		queryString.append(" FROM T_MINER_ORDER minerOrder   ");
//		queryString.append(" LEFT JOIN tz_user party ON minerOrder.party_id = party.user_id  ");
//		queryString.append(" LEFT JOIN T_MINER miner ON miner.UUID = minerOrder.MINER_ID ");
//
//		queryString.append(" WHERE 1 = 1 ");
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			List children = this.userRecomService.findChildren(partyId);
//			if (children.size() == 0) {
//				return new Page();
//			}
//			queryString.append(" and minerOrder.PARTY_ID in (:children) ");
//			parameters.put("children", children);
//		}


//		if (!StringUtils.isNullOrEmpty(miner_para)) {
//			queryString.append(
//					" and miner.UUID=:miner_para  ");
//			parameters.put("miner_para", miner_para);
//		}

//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append("AND (party.user_name like:username OR party.user_code like:username ) ");
//			parameters.put("username", "%" + name_para + "%");
//		}

//		if (!StringUtils.isNullOrEmpty(status_para)) {
//			String status = status_para;
//			if("0".equals(status)) {
//				status ="'0','2'";
//			}else {
//				status ="'"+status+"'";
//			}
//			queryString.append(" and  minerOrder.STATE  in ("+status+") ");
//		}
//		if (!StringUtils.isNullOrEmpty(orderNo)) {
//			queryString.append(" and minerOrder.ORDER_NO = :orderNo  ");
//			parameters.put("orderNo", orderNo);
//
//		}
//		if (!StringUtils.isNullOrEmpty(rolename_para)) {
//			queryString.append(" and   party.role_name =:rolename");
//			parameters.put("rolename", rolename_para);
//		}
//		if("0".equals(status_para)) {
//			queryString.append(" order by minerOrder.CLOSE_TIME desc ");
//		}else {
//			queryString.append(" order by minerOrder.CREATE_TIME desc ");
//		}
		System.out.println("****************************************");

		String status = null;
		if (!StringUtils.isNullOrEmpty(status_para)) {
//			status = status_para;
			if("0".equals(status_para)) {
				status ="0";
			}else if("1".equals(status_para)){
				status ="1";
			}else if("3".equals(status_para)){
				status =null;
			}
		}

		Page page = new Page(pageNo,pageSize);
		this.baseMapper.pagedQueryAdmin(page,children,miner_para,status,orderNo,rolename_para,name_para);
		

//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		return page;
		return page;
	}
	
	public void addOrder(String uid,double amount,String minerId,String operator_username) {
		Miner miner = this.minerService.findById(minerId);
		if(null == miner) {
			throw new BusinessException("矿机不存在");
		}

		User party = partyService.findUserByUserCode(uid);
		if(null==party) {
			throw new BusinessException("购买用户不存在");
		}else {
			if(!(Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())
			||Constants.SECURITY_ROLE_GUEST.equals(party.getRoleName()))) {
				throw new BusinessException("该用户并非正式用户或演示用户，无法购买");
			}
		}
		
		MinerOrder order = new MinerOrder();
		order.setPartyId(party.getUserId());
		order.setMinerId(minerId);
		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		order.setAmount(amount);
		order.setState("1");
		this.minerOrderService.saveCreateByManage(order, operator_username);
	}


	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setMinerOrderService(MinerOrderService minerOrderService) {
		this.minerOrderService = minerOrderService;
	}

	public void setMinerService(MinerService minerService) {
		this.minerService = minerService;
	}

}
