package com.yami.trading.service.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeLeverApplyOrderDto;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExchangeLeverApplyOrderService extends IService<ExchangeLeverApplyOrder> {



    Page<ExchangeLeverApplyOrderDto> listPage(Page page, String roleName,
                                              String status,
                                              String userName,
                                              String orderNo,
                                              List<String> userIds);

    public void saveCancel(String partyId, String order_no);
    public void saveCreate(ExchangeLeverApplyOrder order);
    public ExchangeLeverApplyOrder findByOrderNo(String order_no);

    public void saveOpen(ExchangeLeverApplyOrder applyOrder, Realtime realtime);
    public ExchangeLeverApplyOrder saveClose(ExchangeLeverApplyOrder applyOrder, Realtime realtime, String order_no);


    /**
     * 所有未处理状态的委托单
     */
    public List<ExchangeLeverApplyOrder> findSubmitted();
    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type);

    void saveCloseSymbol(ExchangeLeverApplyOrder applyOrder);
}
