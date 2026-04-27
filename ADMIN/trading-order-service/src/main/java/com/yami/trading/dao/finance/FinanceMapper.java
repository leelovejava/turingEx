package com.yami.trading.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.finance.Finance;
import org.apache.ibatis.annotations.Param;

public interface FinanceMapper extends BaseMapper<Finance> {
    Page pagedQuery(Page page, @Param("name_para") String name_para);
}
