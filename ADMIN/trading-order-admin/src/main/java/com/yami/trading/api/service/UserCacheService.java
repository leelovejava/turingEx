package com.yami.trading.api.service;

import com.yami.trading.bean.model.User;

public interface UserCacheService {

    User currentUser();
    boolean updateUser(User user);

}
