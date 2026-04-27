package com.yami.trading.dao.miner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.miner.MinerPara;
import org.apache.ibatis.annotations.Param;

public interface MinerParaMapper extends BaseMapper<MinerPara> {
    Page pagedQuery(Page page, @Param("name_para") String name_para);
}
