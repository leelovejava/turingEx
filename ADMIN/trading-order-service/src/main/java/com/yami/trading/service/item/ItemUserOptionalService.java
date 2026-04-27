package com.yami.trading.service.item;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.domain.ItemUserOptional;
import com.yami.trading.bean.item.dto.ItemUserOptionalDTO;
import com.yami.trading.bean.item.mapstruct.ItemUserOptionalWrapper;
import com.yami.trading.dao.item.ItemUserOptionalMapper;
import com.yami.trading.service.data.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户自选Service
 * @author lucas
 * @version 2023-03-10
 */
@Service
@Transactional
@Slf4j
public class ItemUserOptionalService extends ServiceImpl<ItemUserOptionalMapper, ItemUserOptional> {
    @Autowired
    private ItemUserOptionalWrapper wrapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    @Qualifier("dataService")
    private DataService dataService;
    /**
     * 查询我的自选的币对列表
     * @param partyId
     * @return
     */
    public List<String> getOptionalSymbols(String partyId){
        QueryWrapper<ItemUserOptional> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PARTY_ID", partyId);
        queryWrapper.select("SYMBOL");
        return list(queryWrapper).stream().map(ItemUserOptional::getSymbol).collect(Collectors.toList());
    }

    public List<ItemUserOptional> getOptionalItems(String partyId){
        QueryWrapper<ItemUserOptional> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PARTY_ID", partyId);
        queryWrapper.select("SYMBOL");
        return list(queryWrapper);
    }
    public List<ItemUserOptionalDTO> getItemUserOptionals(@RequestParam(required = false) String symbol, String partyId) {
        QueryWrapper<ItemUserOptional> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PARTY_ID", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol),"symbol", symbol);
        List<ItemUserOptionalDTO> models = wrapper.toDTO(list(queryWrapper));
        for(ItemUserOptionalDTO dto: models){
            Item bySymbol = itemService.findBySymbol(dto.getSymbol());
            dto.setName(bySymbol.getName());
            List<Realtime> realtimes = dataService.realtime(dto.getSymbol());
            if(!CollectionUtil.isEmpty(realtimes)){
                Realtime realtime = realtimes.get(0);
                dto.setClose(realtime.getClose());
                dto.setChangeRatio(realtime.getChangeRatio());
                dto.setTurnoverRate(realtime.getTurnoverRate());
                dto.setVolumeRatio(realtime.getVolumeRatio());
                dto.setType(bySymbol.getType());
            }else{
                log.error("{} 实时价格是空", bySymbol.getSymbol());
            }
        }
        return models;
    }

}
