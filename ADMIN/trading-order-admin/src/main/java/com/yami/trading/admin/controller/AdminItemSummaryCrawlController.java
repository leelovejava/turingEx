package com.yami.trading.admin.controller;

import com.yami.trading.bean.item.domain.ItemSummary;
import com.yami.trading.bean.item.dto.ItemSummaryDTO;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.huobi.task.summary.SummaryCrawl;
import com.yami.trading.service.item.ItemSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 简况Controller
 * @author lucas
 * @version 2023-05-01
 */

@Api(tags ="简况")
@RestController
@CrossOrigin
@RequestMapping(value = "/item/itemSummary")
public class AdminItemSummaryCrawlController {

	@Autowired
	private ItemSummaryService itemSummaryService;

	@Autowired
	private SummaryCrawl summaryCrawl;


	@ApiOperation(value = "根据symbol获取简况数据")
	@GetMapping("crawl")
	public Result<String> crawl(String symbol) {
		summaryCrawl.crawl();
		return Result.ok("success");
	}


}
