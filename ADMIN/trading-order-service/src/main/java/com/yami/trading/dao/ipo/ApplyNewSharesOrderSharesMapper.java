package com.yami.trading.dao.ipo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.dto.ApplyNewSharesOrderSharesDto;
import com.yami.trading.bean.ipo.dto.SumSpotStockDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ApplyNewSharesOrderSharesMapper extends BaseMapper<ApplyNewSharesOrder> {
    Page<ApplyNewSharesOrderSharesDto> pageData(Page page,
                                                @Param("orderNo") String orderNo,
                                                @Param("symbolCode")  String symbolCode,
                                                @Param("symbolName") String symbolName,
                                                @Param("userName") String userName,
                                                @Param("status") int status,
                                                @Param("children") List<String> children);

    List<SumSpotStockDto> sumSpotStock(String userId);

    BigDecimal sumWinningNumberByUserId( @Param("userId")  String userId,   @Param("symbolCode")  String symbolCode);
}
