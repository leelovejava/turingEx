package com.yami.trading.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.domain.ItemSummary;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.lang.LangUtils;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.item.ItemSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简况Controller
 *
 * @author lucas
 * @version 2023-05-01
 */
@Api(tags = "h5简况")
@RestController
@CrossOrigin
@RequestMapping(value = "api/item/itemSummary")
public class ItemSummaryController {
    @Autowired
    private ItemSummaryService itemSummaryService;
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "根据symbol获取简况数据")
    @GetMapping("get")
    public Result<ItemSummary> queryById(@RequestParam String symbol) {
        Item bySymbol = itemService.findBySymbol(symbol);
        if (bySymbol == null) {
            return Result.failed("币对不存在");
        }
        ItemSummary orNewOne = itemSummaryService.getOrNewOne(symbol);
        orNewOne.setSymbolName(bySymbol.getName());
        if (LangUtils.isTWItem()) {
            String jsonString = JSON.toJSONString(orNewOne);
            String traditionalChinese = HanLP.convertToTraditionalChinese(jsonString);
            ItemSummary cn = JSONObject.parseObject(traditionalChinese, ItemSummary.class);
            return Result.ok(cn);
        }
        if (LangUtils.isEnItem()) {
            ItemSummary en = itemSummaryService.getOneByLang(symbol, "en");
            if (en == null) {
                return Result.ok(orNewOne);
            }
            en.setSymbolName(bySymbol.getName());
            return Result.ok(en);
        }

        return Result.ok(orNewOne);
    }


}
