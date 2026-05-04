package com.yami.trading.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.UserOld;

/**
 * 老客户服务接口
 */
public interface UserOldService extends IService<UserOld> {

    /**
     * 判断是否为老客户
     *
     * @param email 邮箱
     * @param phone 手机号
     * @return true-是老客户，false-不是老客户
     */
    boolean isOldUser(String email, String phone);

    /**
     * 根据邮箱或手机号查询是否存在老客户
     *
     * @param email 邮箱
     * @param phone 手机号
     * @return 是否存在
     */
    int countByEmailOrPhone(String email, String phone);
}