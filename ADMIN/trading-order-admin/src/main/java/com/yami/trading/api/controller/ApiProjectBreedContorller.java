package com.yami.trading.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.ItemVisitFacade;
import com.yami.trading.api.model.GetConstituentStockListModel;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.purchasing.ProjectBreed;
import com.yami.trading.bean.purchasing.ProjectVariety;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.purchasing.ProjectBreedService;
import com.yami.trading.service.purchasing.ProjectVarietyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/projectBreed")
@Slf4j
@Api(tags = "ETF总类-api")
public class ApiProjectBreedContorller {

    @Autowired
    DataService dataService;
    @Autowired
    ProjectBreedService projectBreedService;

    @Autowired
    ProjectVarietyService projectVarietyService;

    @Autowired
    private ItemVisitFacade itemVisitFacade;

    @ApiOperation(value = "列表")
    @GetMapping("getAll")
    public Result<Page<ProjectBreed>> list(PageRequest request) {
        Page page = new Page(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ProjectBreed> lambdaQueryWrapper = Wrappers.<ProjectBreed>query().lambda();
        projectBreedService.page(page, lambdaQueryWrapper);
        return Result.ok(page);
    }

    @ApiOperation(value = "获取成份股列表")
    @GetMapping("getConstituentStockList")
    public Result<List<ProjectVariety>> getConstituentStockList(GetConstituentStockListModel  model) {
        List<ProjectVariety> list = projectVarietyService.list(Wrappers.<ProjectVariety>query().lambda().eq(ProjectVariety::getTransactionPairsSymbol, model.getSymbol()));
        try {
            itemVisitFacade.updateOrInsertVisit(model.getSymbol());
        }catch (Exception e){
            log.error("updateOrInsertVisit {} 异常", model.getSymbol(), e);
        }
         for (ProjectVariety projectVariety : list) {
             Realtime realtime = null;

             // 此处的时间戳如何处理？ TODO
             List<Realtime> realtimes = dataService.realtime(projectVariety.getRelatedStockSymbol());
             if (CollectionUtil.isNotEmpty(realtimes)) {
                 realtime = realtimes.get(0);
             }
             log.info(realtime+"======");
             projectVariety.setRealtime(realtime);
         }

        return Result.ok(list);
    }
}
