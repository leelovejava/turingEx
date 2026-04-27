package com.yami.trading.service.ipo.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.XueQiuNewStocks;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.dao.ipo.NewSharesConfigMapper;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewSharesConfigServiceImpl extends ServiceImpl<NewSharesConfigMapper, NewSharesConfig> implements NewSharesConfigService {
    @Autowired
    ItemService itemService;
    public static final String NEW_STOCKS = "new_stocks";

    @Override
    public NewSharesConfig getByProductCode(String productCode) {
        return getOne(Wrappers.<NewSharesConfig>query().lambda().eq(NewSharesConfig::getProductCode, productCode));
    }

    @Override
    @Transactional
    public void saveNewSharesConfig(NewSharesConfig config, String enName) {
        String symbol = config.getProductCode();
        symbol = symbol.replace("SZ", symbol).replace("SH", "");
        if ("SZ-stocks".equals(config.getType())) {
            symbol = "SZ" + symbol;
        }
        if ("SH-stocks".equals(config.getType())) {
            symbol = "SH" + symbol;
        }
        Item item = itemService.findBySymbol(symbol);
        if (item == null) {
            String type = config.getType();
            if ("SZ-stocks".equals(config.getType()) || "SH-stocks".equals(config.getType())) {
                type = "A-stocks";
                config.setProductCode(symbol);
                config.setType(type);
            }
            Item template = itemService.findByType(type).stream()
                    .filter(i -> !Item.indices.equalsIgnoreCase(i.getType())).findFirst().orElse(null);
            item = BeanUtil.copyProperties(template, Item.class);
            item.setUuid(null);
            item.setSymbol(symbol);
            item.setRemarks(symbol);
            item.setSymbolData(symbol);
            item.setName(config.getName());
            item.setAdjustmentValue(0);
            item.setSymbolFullName(config.getName());
            item.setCategory(NEW_STOCKS);
            item.setEnName(enName);
            item.setType(config.getType());
            item.setCrawlStatus(null);
            itemService.save(item);
        }
        NewSharesConfig newSharesConfig = getByProductCode(config.getProductCode());
        if (newSharesConfig != null) {
            throw new BusinessException("请勿重复新增股票!");
        }
        save(config);
    }
}