package com.yami.trading.admin.controller.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.exchange.model.ExchangeLeverApplyOrderListModel;
import com.yami.trading.admin.controller.exchange.model.ExchangeLeverOrderModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.service.exchange.ExchangeLeverApplyOrderService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("exchangeLeverApplyOrder")
@Api(tags = "币币杠杆委托单")
public class ExchangeLeverApplyOrderController {

    @Autowired
    ExchangeLeverApplyOrderService  exchangeLeverApplyOrderService;

    @Autowired
    PermissionFacade permissionFacade;
    @ApiModelProperty("币币交易订单 列表查询")
    @PostMapping("list")
    public Result<Page<ExchangeLeverOrderDto>> list(@RequestBody @Valid ExchangeLeverApplyOrderListModel request){
        Page<ExchangeLeverOrderDto> page=new Page<>(request.getCurrent(),request.getSize());
        exchangeLeverApplyOrderService.listPage(page,
                request.getRoleName(),
                request.getStatus(),
                request.getUserName(),
                request.getOrderNo(),permissionFacade.getOwnerUserIds());
        return  Result.succeed(page);
    }
}
