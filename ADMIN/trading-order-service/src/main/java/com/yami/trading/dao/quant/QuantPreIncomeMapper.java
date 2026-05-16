package com.yami.trading.dao.quant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.quant.QuantPreIncome;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface QuantPreIncomeMapper extends BaseMapper<QuantPreIncome> {

    @Select("SELECT COALESCE(SUM(income), 0) FROM t_quant_pre_income WHERE quant_order_id = #{quantOrderId} AND status = 1")
    double sumIncomeByOrderId(@Param("quantOrderId") String quantOrderId);
}
