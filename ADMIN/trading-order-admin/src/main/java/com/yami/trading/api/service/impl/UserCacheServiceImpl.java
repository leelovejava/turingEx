package com.yami.trading.api.service.impl;

import com.yami.trading.api.service.UserCacheService;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.user.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户缓存操作类
 */
@Service
public class UserCacheServiceImpl implements UserCacheService {
    @Resource
    UserService userService;

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    public boolean updateUser(User user) {
        return userService.updateById(user);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    @Override
    public User currentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        User user = userService.getById(userId);
        if (!user.isEnabled()) {
            throw new YamiShopBindException("用户已锁定");
        }
        return user;
    }
}
