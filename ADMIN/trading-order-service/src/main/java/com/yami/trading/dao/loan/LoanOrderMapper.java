package com.yami.trading.dao.loan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.loanOrder.LoanOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoanOrderMapper extends BaseMapper<LoanOrder> {

    Page pagedQuery(Page page
            , @Param("userParam") String userParam
            , @Param("orderNo") String orderNo
            , @Param("state") String state
            , @Param("rolename") String rolename,
             @Param("children") List<String> children);
}
