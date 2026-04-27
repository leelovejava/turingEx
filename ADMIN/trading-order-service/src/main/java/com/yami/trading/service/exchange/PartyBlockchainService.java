package com.yami.trading.service.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.exchange.PartyBlockchain;
import com.yami.trading.bean.exchange.dto.PartyBlockchainDto;
import com.yami.trading.bean.model.ChannelBlockchain;

import java.util.List;

public interface  PartyBlockchainService extends IService<PartyBlockchain> {
    List<PartyBlockchain> findByUserName(String userName);

    public List<PartyBlockchain> findByUserNameAndCoinSymbol(String userName,String coinSymbol);

    Page<PartyBlockchainDto> pageList(Page<ChannelBlockchain> page,
                                      String address,
                                      String userName,
                                      String roleName);
}
