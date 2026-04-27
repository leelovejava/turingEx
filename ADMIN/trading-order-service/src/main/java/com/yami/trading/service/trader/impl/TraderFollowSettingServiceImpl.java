package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.bean.trader.domain.TraderFollowSetting;
import com.yami.trading.dao.trader.TraderFollowSettingMapper;
import com.yami.trading.service.trader.TraderFollowSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TraderFollowSettingServiceImpl implements TraderFollowSettingService {

    @Resource
    TraderFollowSettingMapper traderFollowSettingMapper;

    @Override
    public TraderFollowSetting findByPartyId(String partyId) {
        return traderFollowSettingMapper.selectOne(Wrappers.<TraderFollowSetting>lambdaQuery().eq(TraderFollowSetting::getPartyId, partyId));
    }

    @Override
    public TraderFollowSetting findById(String id) {
        return traderFollowSettingMapper.selectById(id);
    }

    @Override
    public int update(TraderFollowSetting traderFollowSetting) {
        return traderFollowSettingMapper.updateById(traderFollowSetting);
    }
}
