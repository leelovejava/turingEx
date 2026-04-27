package com.yami.trading.admin.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.ipo.model.BatchSubscriptionModel;
import com.yami.trading.admin.controller.ipo.model.UserPromiseListModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("userPromise")
@Api(tags = "认购记录")
@Slf4j
public class UserPromiseRecordController {
    @Autowired
    UserPromiseRecordService userPromiseRecordService;
    @Autowired
    NewSharesConfigService newSharesConfigService;
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;

    @Autowired
    private PermissionFacade permissionFacade;

    @ApiOperation("列表")
    @PostMapping("list")
    public Result list(@RequestBody @Valid UserPromiseListModel  request) {

        List<String> children = permissionFacade.getOwnerUserIds();

        Page page = new Page(request.getCurrent(), request.getSize());
        userPromiseRecordService.pagePromiseData(page,request.getUserName(),request.getProductName(),
                request.getRoleName(),request.getProductCode(),request.getName(),request.getStatus(),children);
        return Result.succeed(page);
    }
    @ApiOperation(value = "批量认缴")
    @PostMapping("batchSubscription")
    public  Result batchSubscription(@RequestBody @Valid BatchSubscriptionModel model){
        List<String> orderNos= model.getIds();
        if (CollectionUtil.isEmpty(orderNos)){
            throw new BusinessException("请选择认缴订单记录");
        }
        List<UserPromiseRecord> list= userPromiseRecordService.listByIds(orderNos);
        if (CollectionUtil.isEmpty(list)){
            throw new BusinessException("请选择认缴订单记录");
        }
        for(UserPromiseRecord userPromiseRecord:list){
            userPromiseRecord.setStatus(2);
        }
        userPromiseRecordService.updateBatchById(list);
        return  Result.succeed();
    }
}
