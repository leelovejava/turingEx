package com.yami.trading.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.ChannelBlockchain;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.dao.user.ChannelBlockchainMapper;
import com.yami.trading.service.ChannelBlockchainService;
import com.yami.trading.service.system.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChannelBlockchainServiceImpl  extends ServiceImpl<ChannelBlockchainMapper, ChannelBlockchain> implements ChannelBlockchainService {

    @Autowired
    private    LogService logService;

    @Override
    public ChannelBlockchain findByNameAndCoinAndAdd(String blockchain_name, String coin, String address) {
        List<ChannelBlockchain> list = new ArrayList<ChannelBlockchain>();
        if (StringUtils.isEmpty(address)) {

            list=list(Wrappers.<ChannelBlockchain>query().lambda()
                    .eq(ChannelBlockchain::getBlockchainName,blockchain_name).eq(ChannelBlockchain::getCoin,coin));
        } else {
            list=list(Wrappers.<ChannelBlockchain>query().lambda().
                    eq(ChannelBlockchain::getBlockchainName,blockchain_name).eq(ChannelBlockchain::getCoin,coin).eq(ChannelBlockchain::getAddress,address));
        }
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<ChannelBlockchain> findByCoin(String coin) {
        return list(Wrappers.<ChannelBlockchain>query().lambda().eq(ChannelBlockchain::getCoin,coin));
    }

    @Override
    public List<ChannelBlockchain> findByCoinAndType(String coin, String addressType) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChannelBlockchain> wrapper =
                Wrappers.<ChannelBlockchain>query().lambda()
                        .eq(ChannelBlockchain::getAddressType, addressType);
        if (!StringUtils.isEmpty(coin)) {
            wrapper.eq(ChannelBlockchain::getCoin, coin);
        }
        return list(wrapper);
    }


}
