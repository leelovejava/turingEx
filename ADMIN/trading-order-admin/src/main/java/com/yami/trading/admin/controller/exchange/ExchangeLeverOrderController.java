package com.yami.trading.admin.controller.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.exchange.model.ExchangeLeverOrderModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.exchange.dto.ExchangeApplyOrderDto;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("exchangeLeverOrder")
@Api(tags = "币币交易历史订单")
public class ExchangeLeverOrderController {


    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;

    @Autowired
    PermissionFacade permissionFacade;
    @ApiModelProperty("币币交易订单 列表查询")
    @PostMapping("list")
    public Result<Page<ExchangeLeverOrderDto>> list(@RequestBody @Valid ExchangeLeverOrderModel request){
        Page<ExchangeLeverOrderDto> page=new Page<>(request.getCurrent(),request.getSize());
        if (request.getStartTime() == null && request.getEndTime() != null) {
            request.setEndTime(DateUtil.maxDate(request.getEndTime()));
            request.setStartTime(DateUtil.minDate(request.getEndTime()));
        }
        if (request.getStartTime() != null && request.getEndTime() == null) {
            request.setEndTime(DateUtil.maxDate(request.getStartTime()));
            request.setStartTime(DateUtil.minDate(request.getStartTime()));
        }
        exchangeLeverOrderService.listPage(page,
                request.getRoleName(),
                request.getStatus(),
                request.getUserName(),
                request.getStartTime(),
                request.getEndTime(),
                request.getOrderNo(),permissionFacade.getOwnerUserIds());
        return  Result.succeed(page);
    }
}
