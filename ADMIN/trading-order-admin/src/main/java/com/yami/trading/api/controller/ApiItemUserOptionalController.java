package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.bean.item.domain.ItemUserOptional;
import com.yami.trading.bean.item.dto.ItemUserOptionalDTO;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.item.ItemUserOptionalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户自选Controller
 *
 * @author lucas
 * @version 2023-03-10
 */
@Api(tags = "h5用户自选")
@RestController
@CrossOrigin
@Slf4j
@RequestMapping
public class ApiItemUserOptionalController {

    @Autowired
    private ItemUserOptionalService itemUserOptionalService;

    @ApiOperation("返回用户自选币种的列表")
    @GetMapping("/api/itemUserOptional!list.action")
    public Result<List<ItemUserOptionalDTO>> list(@RequestParam(required = false) String symbol) {
        String partyId = SecurityUtils.getCurrentUserId();
        List<ItemUserOptionalDTO> models = itemUserOptionalService.getItemUserOptionals(symbol, partyId);
        return Result.ok(models);
    }

    @ApiOperation("加入自选")
    @GetMapping("/api/itemUserOptional!add.action")
    public Result<String> add(@RequestParam String symbol) {
        String loginPartyId = SecurityUtils.getCurrentUserId();
        boolean lock = false;
        try {
            if (ItemLock.add(loginPartyId)) {
                lock = true;
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("party_id", loginPartyId);
                queryWrapper.eq("symbol", symbol);
                long count = itemUserOptionalService.count(queryWrapper);
                if (count > 0) {
                    // 当前已经加入过自选
                    throw new YamiShopBindException("Already added to favorites");
                }
                ItemUserOptional entity = new ItemUserOptional();
                entity.setPartyId(loginPartyId);
                entity.setSymbol(symbol);
                itemUserOptionalService.save(entity);
            } else {
                // 请稍后再试
                throw new YamiShopBindException("Please try again later");
            }
        } catch (Exception e) {
            log.error("保存自选失败", e);
            // 保存自选失败
            throw new YamiShopBindException("Failed to save favorite: " + e.getMessage());
        } finally {
            if (lock) {
                ThreadUtils.sleep(50);
                ItemLock.remove(loginPartyId);
            }
        }
        return Result.succeed("保存成功");
    }

    @ApiOperation("删除自选币种")
    @GetMapping("/api/itemUserOptional!delete.action")
    public Result<String> delete(@RequestParam String symbol) {
        String loginPartyId = SecurityUtils.getCurrentUserId();
        boolean lock = false;
        try {
            if (ItemLock.add(loginPartyId)) {
                lock = true;
                QueryWrapper<ItemUserOptional> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("party_id", loginPartyId);
                queryWrapper.eq("symbol", symbol);
                itemUserOptionalService.remove(queryWrapper);
            } else {
                // 请稍后再试
                throw new YamiShopBindException("Please try again later");
            }
        } catch (Exception e) {
            log.error("删除失败", e);
            // 删除失败
            throw new YamiShopBindException("Failed to delete");
        } finally {
            if (lock) {
                ThreadUtils.sleep(50);
                ItemLock.remove(loginPartyId);
            }
        }
        return Result.ok("删除自选币种成功");
    }

    @ApiOperation("查询是否已加入自选")
    @GetMapping("/api/itemUserOptional!getItemOptionalStatus.action")
    public Result<Map<String, Object>> getItemOptionalStatus(@RequestParam String symbol) {
        String loginPartyId = SecurityUtils.getCurrentUserId();
        QueryWrapper<ItemUserOptional> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", loginPartyId);
        queryWrapper.eq("symbol", symbol);
        long count = itemUserOptionalService.count(queryWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("status", count > 0 ? "1" : "0");
        return Result.ok(data);
    }
}
