package com.yami.trading.service.ipo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.Prospectus;
import com.yami.trading.dao.ipo.NewSharesConfigMapper;
import com.yami.trading.dao.ipo.ProspectusMapper;
import com.yami.trading.service.ipo.ProspectusService;
import org.springframework.stereotype.Service;

@Service
public class ProspectusServiceImpl   extends ServiceImpl<ProspectusMapper, Prospectus> implements ProspectusService {
}
