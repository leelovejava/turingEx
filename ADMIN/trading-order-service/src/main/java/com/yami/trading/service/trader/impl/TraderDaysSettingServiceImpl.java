package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.bean.trader.domain.TraderDaysSetting;
import com.yami.trading.dao.trader.TraderDaysSettingMapper;
import com.yami.trading.service.trader.TraderDaysSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TraderDaysSettingServiceImpl implements TraderDaysSettingService {

    @Resource
    private TraderDaysSettingMapper traderDaysSettingMapper;

    @Override
    public List<TraderDaysSetting> list() {
        return traderDaysSettingMapper.selectList(Wrappers.<TraderDaysSetting>lambdaQuery().orderByAsc(TraderDaysSetting::getDays));
    }

    @Override
    public int save(TraderDaysSetting traderDaysSetting) {
        return traderDaysSettingMapper.insert(traderDaysSetting);
    }

    @Override
    public int update(TraderDaysSetting traderDaysSetting) {
        return traderDaysSettingMapper.updateById(traderDaysSetting);
    }

    @Override
    public TraderDaysSetting selectById(String uuid) {
        return traderDaysSettingMapper.selectById(uuid);
    }

    @Override
    public int delete(String uuid) {
        return traderDaysSettingMapper.deleteById(uuid);
    }
}
