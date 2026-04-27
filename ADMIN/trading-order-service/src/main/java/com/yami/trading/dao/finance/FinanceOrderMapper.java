package com.yami.trading.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.finance.FinanceOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FinanceOrderMapper extends BaseMapper<FinanceOrder> {

    Page pagedQuery(Page page, @Param("partyId") String partyId, @Param("state") String state);

    Page pagedQueryByDate(Page page, @Param("date") String date);

    Page pagedQuery1(Page page, @Param("name_para") String name_para, @Param("finance_para") String finance_para, @Param("status_para") String status_para,
                     @Param("children") List<String> children, @Param("orderNo") String orderNo, @Param("rolename_para") String rolename_para,
                     @Param("createTime") String createTime, @Param("closeTime") String closeTime);

    // public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
    // public Page pagedQueryByDate(int pageNo, int pageSize, String date) {
//    public Page pagedQuery(int pageNo, int pageSize, String name_para, String finance_para, String status_para,
//                           String partyId, String orderNo, String rolename_para) {

}
