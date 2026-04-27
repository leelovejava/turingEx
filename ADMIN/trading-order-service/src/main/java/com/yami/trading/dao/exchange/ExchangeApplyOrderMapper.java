package com.yami.trading.dao.exchange;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeApplyOrderDto;
import com.yami.trading.bean.exchange.dto.TodayTransactionDto;
import com.yami.trading.common.util.DateUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExchangeApplyOrderMapper extends BaseMapper<ExchangeApplyOrder> {


    Page<ExchangeApplyOrderDto> listPage(Page page, @Param("rolename")  String rolename,
                                         @Param("userName") String userName,
                                         @Param("orderNo") String orderNo,
                                         @Param("state") String state,
                                         @Param("offset") String offset,
                                         @Param("symbolType") String symbolType,
                                         @Param("userCode") String userCode,
                                         @Param("symbol") String symbol,
                                         @Param("userIds") List<String> userIds

    );

            List<TodayTransactionDto>  getTodayTransaction(@Param("userId") String userId,
                                             @Param("state")  String state,
                                                   @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime,
                                                   @Param("symbols")  List<String> symbols );
}
