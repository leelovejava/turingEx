package com.yami.trading.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.mapper.TzUserOldMapper;
import com.yami.trading.bean.model.TzUserOld;
import com.yami.trading.service.user.TzUserOldService;
import org.springframework.stereotype.Service;

/**
 * 老客户服务实现类
 */
@Service
public class TzUserOldServiceImpl extends ServiceImpl<TzUserOldMapper, TzUserOld> implements TzUserOldService {

    @Override
    public boolean isOldUser(String email, String phone) {
        int count = countByEmailOrPhone(email, phone);
        return count > 0;
    }

    @Override
    public int countByEmailOrPhone(String email, String phone) {
        return baseMapper.countByEmailOrPhone(email, phone);
    }
}