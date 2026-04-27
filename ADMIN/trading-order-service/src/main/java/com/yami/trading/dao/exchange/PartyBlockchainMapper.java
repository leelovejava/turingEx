package com.yami.trading.dao.exchange;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.exchange.PartyBlockchain;
import com.yami.trading.bean.exchange.dto.PartyBlockchainDto;
import org.apache.ibatis.annotations.Param;

public interface PartyBlockchainMapper extends BaseMapper<PartyBlockchain> {
    Page<PartyBlockchainDto> pageData(Page page,
                                      @Param("address") String address,
                                      @Param("userName") String userName,
                                      @Param("roleName") String roleName);

}
