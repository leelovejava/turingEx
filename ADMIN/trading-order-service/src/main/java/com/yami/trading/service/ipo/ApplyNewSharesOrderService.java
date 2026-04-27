package com.yami.trading.service.ipo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.ipo.dto.SumSpotStockDto;

import java.math.BigDecimal;
import java.util.List;

public interface ApplyNewSharesOrderService extends IService<ApplyNewSharesOrder> {





    void apply(BigDecimal amount,String userId,String code);

    ApplyNewSharesOrder findByOrderNo(String orderNo);

    public List<ApplyNewSharesOrder> findByStatus(String productCode,int status);


    List<ApplyNewSharesOrder> findByOrderNo(List<String> orderNo);

    Page pageData(Page page, String orderNo, String symbolCode, String symbolName, String userName, int status , List<String> children);

    List<SumSpotStockDto> sumSpotStock(String userId);

    BigDecimal sumWinningNumberByUserId(String userId, String symbolCode);
}
