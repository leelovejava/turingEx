package com.yami.trading.api.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.api.controller.ipo.model.UserPromiseListModel;
import com.yami.trading.bean.ipo.dto.PromiseTopDto;
import com.yami.trading.api.controller.ipo.model.ApplyPromiseModel;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.ipo.dto.UserPromiseListDto;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/userPromise")
@Api(tags = "用户认购")
@Slf4j
public class ApiUserPromiseRecordController {
    @Autowired
    UserPromiseRecordService userPromiseRecordService;
    @Autowired
    NewSharesConfigService newSharesConfigService;
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    SysparaService sysparaService;

    @Autowired
    ItemService itemService;

    @ApiOperation("列表")
    @PostMapping("list")
    public Result<List<UserPromiseListDto>> list(UserPromiseListModel pageRequest, HttpServletRequest request) {
        List<String> symbols=new ArrayList<>();
        if (StrUtil.isNotEmpty(pageRequest.getSymbolType())){
            symbols = itemService.findDBByType(pageRequest.getSymbolType()).stream().map(Item::getSymbol).collect(Collectors.toList());
            symbols.add("-1");
        }
        Page<UserPromiseListDto> page = new Page(pageRequest.getCurrent(), pageRequest.getSize());
        userPromiseRecordService.pageUserPromiseData(page, SecurityUtils.getCurrentUserId(),symbols);

        for (UserPromiseListDto dto: page.getRecords()){
            Item item = itemService.findBySymbol(dto.getProductCode());
            if (item != null) {
                dto.setName(item.getName());
            }
        }
        Result result= Result.succeed(page.getRecords());
        result.setTotal(page.getTotal());
        return result;
    }

    @ApiOperation("top 数据")
    @PostMapping("getTop")
    public Result<PromiseTopDto> getTop() {
        PromiseTopDto promiseTopDto = userPromiseRecordService.topData(SecurityUtils.getCurrentUserId());
        if (promiseTopDto==null){
            promiseTopDto=new PromiseTopDto();
        }
        promiseTopDto.setAvailableLimit(promiseTopDto.getWinningQuota()-promiseTopDto.getSubscriptionLimit());
        return Result.succeed(promiseTopDto);
    }

    @ApiOperation("认缴")
    @PostMapping("applyPromise")
    public Result applyPromise(ApplyPromiseModel model) {

        if (StrUtil.isEmpty(model.getOrderNo())) {
            throw new BusinessException("申购订单号不能为空");
        }
        if (model.getDeductNumber() == null) {
            throw new BusinessException("认缴金额(股)不能为空");
        }
        if (model.getDeductNumber().doubleValue() <= 0) {
            throw new BusinessException("认缴金额(股)必须大于0");
        }
        ApplyNewSharesOrder order = applyNewSharesOrderService.findByOrderNo(model.getOrderNo());
        if (order==null){
            throw new BusinessException("申购订单号不存在");
        }
        NewSharesConfig newSharesConfig = newSharesConfigService.getByProductCode(order.getSymbolCode());
        if (newSharesConfig == null) {
            throw new BusinessException("新股不存在!");
        }
       List<UserPromiseRecord> userPromiseRecords=  userPromiseRecordService.findByProductCodeAndUserId(order.getSymbolCode(),SecurityUtils.getCurrentUserId());
        int count=0;
        if (CollectionUtil.isNotEmpty(userPromiseRecords)){
            count=userPromiseRecords.size();
        }
        count++;
        if (CollectionUtil.isNotEmpty(userPromiseRecords)){
            if (count>order.getUserPromiseCount()){
                throw new BusinessException("超过认购次数");
            }
        }
        UserPromiseRecord userPromiseRecord = new UserPromiseRecord();
        userPromiseRecord.setUserId(SecurityUtils.getUser().getUserId());
        userPromiseRecord.setDeductNumber(model.getDeductNumber());
        userPromiseRecord.setDeductUsdt(order.getSubPrice().multiply(userPromiseRecord.getDeductNumber()));
        userPromiseRecord.setStatus(1);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,newSharesConfig.getLockDay());
        userPromiseRecord.setLockEndTime(calendar.getTime());
        userPromiseRecord.setNewSharesConfigId(newSharesConfig.getUuid());
        userPromiseRecordService.applyPromise(newSharesConfig, userPromiseRecord, order);
        return Result.succeed();
    }

}
