package com.yami.trading.admin.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.ipo.dto.NameValueDto;
import com.yami.trading.admin.controller.ipo.model.*;
import com.yami.trading.admin.facade.MachineTranslationService;
import com.yami.trading.admin.model.IdModel;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.domain.BaseEntity;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.data.RealtimeService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import com.yami.trading.service.item.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("newSharesConfig")
@Api(tags = "新股发布")
@Slf4j
public class NewSharesConfigController {


    @Autowired
    NewSharesConfigService newSharesConfigService;


    @Autowired
    ItemService itemService;

    @Autowired
    UserPromiseRecordService userPromiseRecordService;

    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    private MachineTranslationService translationService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result list(@RequestBody @Valid NewSharesConfigListModel request) {
        Page page = new Page(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<NewSharesConfig>
                lambdaQueryWrapper = Wrappers.<NewSharesConfig>query().lambda();
        if (request.getStatus() > 0) {
            lambdaQueryWrapper.eq(NewSharesConfig::getStatus, request.getStatus());
        }
        if (request.getIpoStatus() > 0) {
            lambdaQueryWrapper.eq(NewSharesConfig::getIpoStatus, request.getIpoStatus());
        }
        if (StrUtil.isNotEmpty(request.getProductCode())) {
            lambdaQueryWrapper.like(NewSharesConfig::getProductCode, request.getProductCode());
        }
        if (StrUtil.isNotEmpty(request.getProductName())) {
            lambdaQueryWrapper.like(NewSharesConfig::getProductName, request.getProductName());
        }

        if (StrUtil.isNotEmpty(request.getName())) {
            lambdaQueryWrapper.like(NewSharesConfig::getName, request.getName());
        }
        lambdaQueryWrapper.orderByDesc(BaseEntity::getCreateTime);
        newSharesConfigService.page(page, lambdaQueryWrapper);
        return Result.succeed(page);
    }

    @ApiOperation(value = "新增")
    @PostMapping("add")
    public Result add(@RequestBody @Valid NewSharesConfigModel model) {
        NewSharesConfig config = new NewSharesConfig();
        BeanUtils.copyProperties(model, config);
        config.setProductCode(model.getProductName());
        config.setDrawDate(model.getStartSubscribeDate());
        newSharesConfigService.saveNewSharesConfig(config, translationService.translate(model.getName()));
        return Result.succeed();
    }


    @ApiOperation(value = "获取股票类型")
    @GetMapping("getSharesTypeList")
    public Result<List<NameValueDto>> getSharesTypeList() {
        List<NameValueDto> list = new ArrayList<>();
        list.add(new NameValueDto("美股", "US-stocks"));
        list.add(new NameValueDto("港股", "HK-stocks"));
        list.add(new NameValueDto("深市", "SZ-stocks"));
        list.add(new NameValueDto("沪市", "SH-stocks"));
        return Result.succeed(list);
    }


    @ApiOperation(value = "搜索股票信息")
    @PostMapping("searchNewShare")
    public Result<List<Item>> searchNewShare(@RequestBody @Valid SearchNewShareModel model) {
        Page<Item> page = new Page<Item>(model.getCurrent(), model.getSize());
        LambdaQueryWrapper<Item> queryWrapper = Wrappers.<Item>query().lambda().like(Item::getSymbol, model.getSymbol());
        itemService.page(page, queryWrapper);
        return Result.succeed(page.getRecords());
    }


    @ApiOperation(value = "获取新股信息")
    @PostMapping("getNewShare")
    public Result<Item> getNewShare(@RequestBody @Valid GetNewShareModel model) {
        Item item = itemService.findBySymbol(model.getProductCode());
        if (item == null) {
            throw new BusinessException("未查询到股票数据!");
        }
        return Result.succeed(item);
    }

    @ApiOperation(value = "批量发布")
    @PostMapping("batchPublishing")
    public Result batchPublishing(@RequestBody @Valid IdModel model) {
        List<String> ids = StrUtil.split(model.getId(), ',');
        if (CollectionUtil.isEmpty(ids)) {
            throw new BusinessException("id不能为空");
        }

        List<NewSharesConfig> list = newSharesConfigService.listByIds(ids);
        for (NewSharesConfig newSharesConfig : list) {
            newSharesConfig.setStatus(2);
        }
        newSharesConfigService.updateBatchById(list);
        return Result.succeed();
    }


    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result update(@RequestBody @Valid NewSharesConfigUpdateModel model) {
        NewSharesConfig config = new NewSharesConfig();
        config.setUuid(model.getId());
        BeanUtils.copyProperties(model, config);
        config.setProductCode(model.getProductName());
        config.setDrawDate(model.getStartSubscribeDate());
        String symbol = config.getProductCode();
        symbol = symbol.replace("SZ", "").replace("SH", "");
        if ("SZ-stocks".equals(config.getType())) {
            symbol = "SZ" + symbol;
        }
        if ("SH-stocks".equals(config.getType())) {
            symbol = "SH" + symbol;
        }
        Item item = itemService.findBySymbol(symbol);
//        if (item==null){
//            throw  new BusinessException("未查询到股票数据!");
//        }
        if (item != null) {
            config.setName(item.getSourceName());
        }
        newSharesConfigService.updateById(config);
        return Result.succeed();
    }

    @ApiOperation(value = "删除")
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid IdModel model) {
        List<String> list = StrUtil.split(model.getId(), ",");
        for (String id : list) {
            NewSharesConfig newSharesConfig = newSharesConfigService.getById(id);
            if (newSharesConfig == null) {
                throw new BusinessException("选择记录不存在!");
            }
            List<ApplyNewSharesOrder> applyNewSharesOrders = applyNewSharesOrderService.findByStatus(newSharesConfig.getProductCode(), 1);
            if (CollectionUtil.isNotEmpty(applyNewSharesOrders)) {
                throw new BusinessException("存在申购订单,禁止删除!");
            }
            List<UserPromiseRecord> userPromiseRecords = userPromiseRecordService.findByProductCode(newSharesConfig.getProductCode());
            if (CollectionUtil.isNotEmpty(userPromiseRecords)) {
                throw new BusinessException("现股存在待卖出股票,禁止删除!");
            }
            newSharesConfigService.removeById(id);
        }
        return Result.succeed();
    }
}
