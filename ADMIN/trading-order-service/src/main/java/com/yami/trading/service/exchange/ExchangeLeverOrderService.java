package com.yami.trading.service.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.model.FollowWallet;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExchangeLeverOrderService extends IService<ExchangeLeverOrder> {

    Page<ExchangeLeverOrderDto> listPage(Page page,String roleName,
                                         String status,
                                         String userName,
                                         Date startTime,
                                         Date endTime,String orderNo,
                                         List<String> userIds);
    public ExchangeLeverOrder saveClose(String partyId, String order_no);

    public ExchangeLeverOrder findByOrderNo(String order_no);
    public Map<String, Object> bulidOne(ExchangeLeverOrder order,double money);
    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type);
    /**
         * 持仓单
         */
    public List<ExchangeLeverOrder> findSubmitted(String partyId, String symbol, String direction);

    /**
     * 所有持仓单
     */
    public List<ExchangeLeverOrder> findSubmitted();

    double sumVolume(String currentUserId,String symbol);

    ExchangeLeverOrder findBySymbol(String partyId,String symbol);

}
