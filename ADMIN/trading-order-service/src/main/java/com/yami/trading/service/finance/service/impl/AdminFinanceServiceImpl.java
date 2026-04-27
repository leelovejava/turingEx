package com.yami.trading.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yami.trading.service.finance.service.AdminFinanceService;
import com.yami.trading.bean.finance.Finance;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.dao.finance.FinanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminFinanceServiceImpl extends ServiceImpl<FinanceMapper, Finance>  implements AdminFinanceService {

	public Page pagedQuery(int pageNo, int pageSize, String name_para) {
		Page page=new Page(pageNo,pageSize);
		return baseMapper.pagedQuery(page,name_para);
	}

//	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
//		this.pagedQueryDao = pagedQueryDao;
//	}
}