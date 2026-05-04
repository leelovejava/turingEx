package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.model.UserOld;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 老客户表Mapper接口
 */
public interface UserOldMapper extends BaseMapper<UserOld> {

    /**
     * 根据邮箱或手机号查询是否存在老客户
     *
     * @param email 邮箱
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) FROM tz_user_old WHERE email = #{email} OR phone = #{phone} OR phone_all = #{phone}")
    int countByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

    /**
     * 判断是否为老客户
     *
     * @param email 邮箱
     * @param phone 手机号
     * @return true-是老客户，false-不是老客户
     */
    default boolean isOldUser(String email, String phone) {
        int count = countByEmailOrPhone(email, phone);
        return count > 0;
    }
}