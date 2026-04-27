package com.yami.trading.admin.controller.item;
import com.yami.trading.bean.item.domain.ItemSummary;
import com.yami.trading.common.domain.Result;
import javax.validation.Valid;

import com.yami.trading.common.exception.YamiShopBindException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yami.trading.bean.item.dto.ItemSummaryDTO;
import com.yami.trading.service.item.ItemSummaryService;


/**
 * 简况Controller
 * @author lucas
 * @version 2023-05-01
 */

@Api(tags ="简况")
@RestController
@CrossOrigin
@RequestMapping(value = "/item/itemSummary")
public class AdminItemSummaryController {

	@Autowired
	private ItemSummaryService itemSummaryService;
	/**
	 * 根据Id获取简况数据
	 */
	@ApiOperation(value = "根据symbol获取简况数据")
	@GetMapping("queryBySymbol")
	public Result<ItemSummary> queryById(String symbol) {
		return Result.ok (itemSummaryService.getOrNewOne ( symbol ) );
	}

	/**
	 * 保存简况
	 */
	@ApiOperation(value = "修改简况")
	@PostMapping("save")
	public  Result <String> save(@Valid @RequestBody ItemSummary itemSummaryDTO) {
		String uuid = itemSummaryDTO.getUuid();
		ItemSummaryDTO byId = itemSummaryService.findById(uuid);
		if(byId == null){
			throw  new YamiShopBindException("简况不存在");
		}
		//新增或编辑表单保存
		itemSummaryService.updateById (itemSummaryDTO);
        return Result.ok ( "保存简况成功" );
	}


}
