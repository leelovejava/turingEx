package com.yami.trading.dao.exchangelever;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ExchangeLeverOrderMapper extends BaseMapper<ExchangeLeverOrder> {

    Page<ExchangeLeverOrderDto> listPage(Page page,
                                         @Param("roleName") String roleName,
                                         @Param("status") String status,
                                         @Param("userName") String userName,
                                         @Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime,
                                         @Param("orderNo") String orderNo,
                                         @Param("userIds") List<String> userIds);

}




