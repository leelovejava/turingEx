package com.yami.trading.api.controller.ipo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.IdModel;
import com.yami.trading.api.controller.ipo.dto.ProspectusDto;
import com.yami.trading.api.controller.ipo.model.GetProspectusModel;
import com.yami.trading.api.controller.ipo.model.ListListingAndlistedModel;
import com.yami.trading.api.controller.ipo.model.NewSharesConfigModel;
import com.yami.trading.bean.ipo.*;
import com.yami.trading.bean.ipo.dto.NewSharesConfigDto;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.ipo.ProspectusService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import com.yami.trading.service.item.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/newSharesConfig")
@Api(tags = "新股认购")
@Slf4j
public class ApiNewSharesConfigContorller {
    @Autowired
    NewSharesConfigService newSharesConfigService;
    @Autowired
    ProspectusService prospectusService;
    @Autowired
    DataService dataService;
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    UserPromiseRecordService userPromiseRecordService;
    @Autowired
    ItemService itemService;

    @Autowired
    private SpiderService spiderService;
    @ApiOperation(value = "新上市列表")
    @GetMapping("newIssueList")
    public Result<List<XueQiuNewStocks>> newIssue(@RequestParam(required = false) String type) {
        List<XueQiuNewStocks> xueQiuNewStocks = spiderService.fetchNewStocks();
        if(CollectionUtil.isEmpty(xueQiuNewStocks)){
            return Result.succeed(xueQiuNewStocks);
        }
        for (XueQiuNewStocks xueQiuNewStock : xueQiuNewStocks) {
            String symbol = xueQiuNewStock.getSymbol();
            Item bySymbol = itemService.findBySymbol(symbol);
            if(bySymbol != null && StringUtils.isNotEmpty(bySymbol.getName())){
                xueQiuNewStock.setName(bySymbol.getName());
                xueQiuNewStock.setType(bySymbol.getType());
            }
        }

        return Result.succeed(xueQiuNewStocks);






    }
    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<List<NewSharesConfig>> list(@Valid NewSharesConfigModel model, HttpServletRequest request) {
        Page<NewSharesConfig> page = new Page(model.getCurrent(), model.getSize());
        LambdaQueryWrapper<NewSharesConfig> lambdaQueryWrapper = Wrappers.<NewSharesConfig>query()
                .lambda().eq(NewSharesConfig::getStatus, 2);

        if (StrUtil.isNotEmpty(model.getType())){
            List<String> symbols = itemService.findDBByType(model.getType()).stream().map(Item::getSymbol).collect(Collectors.toList());
            symbols.add("-1");
            log.info("新股认购"+JSONUtil.toJsonStr(symbols));
            lambdaQueryWrapper.in(NewSharesConfig::getProductCode,symbols);
        }

        lambdaQueryWrapper.orderByAsc(NewSharesConfig::getWeight);
        newSharesConfigService.page(page, lambdaQueryWrapper);
        List<String> producntCode = new ArrayList<>();
        for (NewSharesConfig newSharesConfig : page.getRecords()) {
            newSharesConfig.setPriceDifference(newSharesConfig.getUnderwritingPrice()
                    .subtract(newSharesConfig.getMarketPrice()));
            producntCode.add(newSharesConfig.getProductCode());
            newSharesConfig.setPriceDifferenceValue(newSharesConfig.getUnderwritingPrice()
                    .divide(newSharesConfig.getMarketPrice(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        }
        List<ApplyNewSharesOrder> applyNewSharesOrders = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(producntCode)) {
            applyNewSharesOrders = applyNewSharesOrderService.list(Wrappers.<ApplyNewSharesOrder>query().lambda()
                    .in(ApplyNewSharesOrder::getSymbolCode, producntCode)
                    .eq(ApplyNewSharesOrder::getStatus, 2)
                    .eq(ApplyNewSharesOrder::getUserId, SecurityUtils.getCurrentUserId()));
        }
        Map<String, ApplyNewSharesOrder> map = new HashMap<>();
        for (ApplyNewSharesOrder order : applyNewSharesOrders) {
            map.put(order.getSymbolCode(), order);
        }
        for (NewSharesConfig newSharesConfig : page.getRecords()) {
            Item item = itemService.findBySymbol(newSharesConfig.getProductCode());
            if (item != null) {
                newSharesConfig.setName(item.getName());
            }
            ApplyNewSharesOrder order = map.get(newSharesConfig.getProductCode());
            newSharesConfig.setShareStatus(map.containsKey(newSharesConfig.getProductCode()) ? 2 : 1);
            Date now =new Date();
            Date startDate = newSharesConfig.getStartSubscribeDate();
            Date endDate =newSharesConfig.getEndSubscribeDate();
            if (newSharesConfig.getShareStatus() == 1) {
                if (now.getTime()<startDate.getTime()) {
                    newSharesConfig.setShareStatus(0);
                }
                if (now.getTime()<startDate.getTime() ||now.getTime() > endDate.getTime()) {
                    newSharesConfig.setShareStatus(0);
                }
            }
            if (newSharesConfig.getShareStatus() == 2) {
                if (now.getTime() > endDate.getTime()) {
                    newSharesConfig.setShareStatus(0);
                }
            }
            if (newSharesConfig.getShareStatus() == 2) {
                if (order.getSubscribedCount()>0){
                    if (order.getUserPromiseCount() == order.getSubscribedCount()) {
                        newSharesConfig.setShareStatus(0);
                    }
                }
            }
        }
        Result result=  Result.succeed(page.getRecords());
        result.setTotal(page.getTotal());
        return result;
    }

    @ApiOperation(value = "详情接口")
    @PostMapping("getDesc")
    public Result<NewSharesConfigDto> list(@Valid IdModel model, HttpServletRequest request) {
        NewSharesConfig newSharesConfig = newSharesConfigService.getById(model.getId());
        NewSharesConfigDto dto = new NewSharesConfigDto();
        BeanUtils.copyProperties(newSharesConfig, dto);
        dto.setPriceDifference(newSharesConfig.getUnderwritingPrice()
                .subtract(newSharesConfig.getMarketPrice()));
        dto.setPriceDifferenceValue(newSharesConfig.getUnderwritingPrice()
                .divide(newSharesConfig.getMarketPrice(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        List<ApplyNewSharesOrder> applyNewSharesOrders = applyNewSharesOrderService.list(Wrappers.<ApplyNewSharesOrder>query().lambda()
                .eq(ApplyNewSharesOrder::getSymbolCode, newSharesConfig.getProductCode()).eq(ApplyNewSharesOrder::getStatus, 2).eq(ApplyNewSharesOrder::getUserId, SecurityUtils.getCurrentUserId()));
        List<UserPromiseRecord> userPromiseRecords = userPromiseRecordService.findByProductCodeAndUserId(newSharesConfig.getProductCode(), SecurityUtils.getCurrentUserId());
        if (CollectionUtil.isNotEmpty(applyNewSharesOrders)) {
            dto.setShareStatus(2);
            dto.setRequiredNumber(applyNewSharesOrders.get(0).getRequiredNumber());
            dto.setOrderNo(applyNewSharesOrders.get(0).getOrderNo());
            dto.setWinningNumber(applyNewSharesOrders.get(0).getWinningNumber());
        } else {
            dto.setShareStatus(1);
            dto.setRequiredNumber(new BigDecimal(0));
        }
        if (CollectionUtil.isNotEmpty(userPromiseRecords)) {
            dto.setUserPromiseCount(applyNewSharesOrders.get(0).getUserPromiseCount()-userPromiseRecords.size());
            int sumNumber = 0;
            for (UserPromiseRecord record : userPromiseRecords) {
                sumNumber += record.getDeductNumber().intValue();
            }
            if (CollectionUtil.isNotEmpty(applyNewSharesOrders)) {
                dto.setResiduePromiseNumber(applyNewSharesOrders.get(0).getWinningNumber().subtract(new BigDecimal(sumNumber)).intValue());
            }
        } else {
            if (CollectionUtil.isNotEmpty(applyNewSharesOrders)) {
                dto.setUserPromiseCount(applyNewSharesOrders.get(0).getUserPromiseCount());
                dto.setResiduePromiseNumber(applyNewSharesOrders.get(0).getWinningNumber().intValue());
            }
        }
//        String language = request.getParameter("language");
        Item item = itemService.findBySymbol(newSharesConfig.getProductCode());
        if (item != null) {
            dto.setName(item.getName());
        }
        return Result.succeed(dto);
    }

    @ApiOperation(value = "获取招股书")
    @PostMapping("getProspectus")
    public Result<List<ProspectusDto>> getProspectus(@Valid GetProspectusModel pageRequest,
                                                     HttpServletRequest request) {
        Page<Prospectus> page = new Page(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<Prospectus> lambdaQueryWrapper = Wrappers.<Prospectus>query()
                .lambda();
        if (StrUtil.isNotEmpty(pageRequest.getType())){
            List<String> symbols = itemService.findDBByType(pageRequest.getType()).stream().map(Item::getSymbol).collect(Collectors.toList());
            symbols.add("-1");
            lambdaQueryWrapper.in(Prospectus::getProductCode,symbols);
        }
        List<OrderItem> orders = new ArrayList<>();
        orders.add(OrderItem.desc("create_time"));
        page.setOrders(orders);
        prospectusService.page(page,lambdaQueryWrapper);
//        String language = request.getParameter("language");
        List<ProspectusDto> list = new ArrayList<>();
        for (Prospectus prospectus : page.getRecords()) {
            ProspectusDto prospectusDto = new ProspectusDto();
            BeanUtils.copyProperties(prospectus, prospectusDto);
            Item item = itemService.findBySymbol(prospectusDto.getProductCode());
            if (item != null) {
                prospectusDto.setProductName(item.getName());
            }
            list.add(prospectusDto);
        }
        return Result.succeed(list);
    }

    @ApiOperation(value = "待上市列表 已上市列表")
    @PostMapping("listListingAndlisted")
    public Result<List<NewSharesConfig>> listPendingListing(@Valid ListListingAndlistedModel model,
                                                            HttpServletRequest request) {
        Page<NewSharesConfig> page = new Page(model.getCurrent(), model.getSize());
        LambdaQueryWrapper<NewSharesConfig> lambdaQueryWrapper = Wrappers.<NewSharesConfig>query()
                .lambda().eq(NewSharesConfig::getIpoStatus, model.getIpoStatus());
        lambdaQueryWrapper.orderByDesc(NewSharesConfig::getWeight);
        if (StrUtil.isNotEmpty(model.getType())){
            List<String> symbols = itemService.findDBByType(model.getType()).stream().map(Item::getSymbol).collect(Collectors.toList());
            symbols.add("-1");
            lambdaQueryWrapper.in(NewSharesConfig::getProductCode,symbols);
        }
//        String language = request.getParameter("language");
        newSharesConfigService.page(page, lambdaQueryWrapper);
        for (NewSharesConfig newSharesConfig : page.getRecords()) {
            Item item = itemService.findBySymbol(newSharesConfig.getProductCode());
            if (item != null) {
                newSharesConfig.setName( item.getName());
            }
        }
        return Result.succeed(page.getRecords());
    }
}
