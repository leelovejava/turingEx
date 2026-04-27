package com.yami.trading.dao.miner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.miner.Miner;
import org.apache.ibatis.annotations.Param;

public interface MinerMapper extends BaseMapper<Miner> {
    Page pagedQuery(Page page, @Param("name_para") String name_para);
}
