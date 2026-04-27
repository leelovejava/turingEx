package com.yami.trading.service.miner.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AdminMinerParaService {
	public Page pagedQuery(int pageNo, int pageSize, String miner_id);
}
