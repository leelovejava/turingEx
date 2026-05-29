package com.yami.trading.dao.miner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.miner.MinerOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface MinerOrderMapper extends BaseMapper<MinerOrder> {
    Page pagedQuery(Page page, @Param("name_para") String name_para);

    Page pagedQuery1(Page page, @Param("partyId") String partyId,@Param("state") String state);

    Page pagedQueryComputeOrder(Page page);


    Page pagedQueryAdmin(Page page, @Param("children") List children,
                         @Param("miner_para") String miner_para,
                         @Param("status_para") String status_para,
                         @Param("orderNo") String orderNo,
                         @Param("rolename_para") String rolename_para,
                         @Param("name_para") String name_para);

    @Select("SELECT * FROM t_miner_order WHERE order_no = #{orderNo} FOR UPDATE")
    MinerOrder selectByOrderNoForUpdate(@Param("orderNo") String orderNo);

    @Update("UPDATE t_miner_order " +
            "SET state = #{targetState}, close_time = #{closeTime}, compute_day = #{computeDay} " +
            "WHERE order_no = #{orderNo} AND state = '1'")
    int closeIfRunning(@Param("orderNo") String orderNo,
                       @Param("targetState") String targetState,
                       @Param("computeDay") Date computeDay,
                       @Param("closeTime") Date closeTime);
}
