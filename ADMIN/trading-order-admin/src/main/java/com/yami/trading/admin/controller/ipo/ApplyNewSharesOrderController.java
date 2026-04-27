package com.yami.trading.admin.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.ipo.model.*;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.dto.ApplyNewSharesOrderSharesDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("applyNewSharesOrder")
@Api(tags = "新股申购")
@Slf4j
public class ApplyNewSharesOrderController {
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    NewSharesConfigService newSharesConfigService;
    @Autowired
    UserService userService;

    @Autowired
    PermissionFacade permissionFacade;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<ApplyNewSharesOrderSharesDto>> list(@RequestBody @Valid ApplyNewSharesOrderListModel request) {
        Page page = new Page(request.getCurrent(), request.getSize());

        List<String> children = permissionFacade.getOwnerUserIds();

        applyNewSharesOrderService.pageData(page,request.getOrderNo(),request.getSymbolCode(),
                request.getSymbolName(),request.getUserName(),request.getStatus() , children);
        return Result.succeed(page);
    }
//
//    @ApiOperation(value = "新增")
//    @PostMapping("add")
//    public Result add(@RequestBody @Valid ApplyNewSharesOrderModel model) {
//        User user = userService.findUserByUserCode(model.getUserCode());
//        if (user == null) {
//            throw new BusinessException("用户不存在!");
//        }
//        NewSharesConfig newSharesConfig = newSharesConfigService.getByProductCode(model.getProductCode());
//        ApplyNewSharesOrder applyNewSharesOrder = new ApplyNewSharesOrder();
//        applyNewSharesOrder.setUserId(user.getUserId());
//        applyNewSharesOrder.setSymbolName(newSharesConfig.getProductName());
//        applyNewSharesOrder.setSymbolCode(newSharesConfig.getProductCode());
//        applyNewSharesOrder.setSubPrice(model.getSubPrice());
//        applyNewSharesOrder.setSubNumber(model.getSubNumber());
//        applyNewSharesOrder.setStatus(model.getStatus());
//        applyNewSharesOrder.setWinningNumber(model.getWinningNumber());
//        applyNewSharesOrder.setRequiredNumber(model.getRequiredNumber());
//        applyNewSharesOrder.setRequiredSubscribe(model.getRequiredSubscribe());
//        applyNewSharesOrder.setSubscribedCount(model.getSubscribedCount());
//        applyNewSharesOrder.setSubscribedAmount(model.getSubscribedAmount());
//        applyNewSharesOrder.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
//        applyNewSharesOrderService.save(applyNewSharesOrder);
//        return Result.succeed();
//    }

    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result update(@RequestBody @Valid ApplyNewSharesOrderUpdateModel model) {
        ApplyNewSharesOrder applyNewSharesOrder = applyNewSharesOrderService.findByOrderNo(model.getOrderNo());
        if (applyNewSharesOrder == null) {
            throw  new BusinessException("新股申购订单不存在!");
        }
        if (model.getWinningNumber().doubleValue()>applyNewSharesOrder.getSubNumber().doubleValue()){
            throw  new BusinessException("中签数量不能大于申购数量!");
        }
        applyNewSharesOrder.setSubPrice(model.getSubPrice());
        applyNewSharesOrder.setSubNumber(model.getSubNumber());
        applyNewSharesOrder.setWinningNumber(model.getWinningNumber());
        applyNewSharesOrder.setRequiredNumber(model.getWinningNumber().multiply(model.getSubPrice()));
        applyNewSharesOrder.setRequiredSubscribe(model.getRequiredSubscribe());
        applyNewSharesOrder.setSubscribedCount(model.getSubscribedCount());
        applyNewSharesOrder.setSubscribedAmount(model.getSubscribedAmount());
        applyNewSharesOrder.setUserPromiseCount(model.getUserPromiseCount());
        applyNewSharesOrderService.updateById(applyNewSharesOrder);
        return Result.succeed();
    }

    @ApiOperation(value = "批量改签")
    @PostMapping("batchRescheduling")
    public  Result batchRescheduling(@RequestBody @Valid  BatchReschedulingModel model){
        List<String> orderNos=model.getOrderNo();
        if (CollectionUtil.isEmpty(orderNos)){
            throw new BusinessException("请选择申购订单记录");
        }
        List<ApplyNewSharesOrder> list= applyNewSharesOrderService.findByOrderNo(orderNos);
        for (ApplyNewSharesOrder order:list){
            order.setStatus(2);
            order.setWinningNumber(order.getSubNumber());
            order.setRequiredNumber(order.getSubNumber().multiply(order.getSubPrice()));
            order.setUserPromiseCount(1);
        }
        applyNewSharesOrderService.updateBatchById(list);
        return  Result.succeed();
    }
    @ApiOperation(value = "批量公布")
    @PostMapping("batchPublish")
    public  Result batchPublish(@RequestBody @Valid  BatchPublishModel model){
        List<String> orderNos=model.getOrderNo();
        if (CollectionUtil.isEmpty(orderNos)){
            throw new BusinessException("请选择申购订单记录");
        }
        List<ApplyNewSharesOrder> list= applyNewSharesOrderService.findByOrderNo(orderNos);
        for (ApplyNewSharesOrder order:list){
            if (order.getStatus()!= 1){
                throw  new BusinessException("申购订单已公布中签!");
            }
            if (order.getStatus()==2){
                order.setWinningNumber(order.getSubNumber());
                order.setRequiredNumber(order.getSubNumber().multiply(order.getSubPrice()));
            }
            order.setStatus(model.getStatus());
        }
        applyNewSharesOrderService.updateBatchById(list);
        return  Result.succeed();
    }
}
