package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.UserData;
import com.yami.trading.bean.user.dto.UserBenefitsDto;
import com.yami.trading.bean.user.dto.UserDataWithdrawLimitDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserDataMapper extends BaseMapper<UserData> {

    Page  listUserGenefits(Page page, @Param("startTime") Date startTime,
                           @Param("endTime") Date endTime,@Param("userName") String userName,
                           @Param("children")  List<String> children);

    UserBenefitsDto daySumData(@Param("startTime") Date startTime,
                               @Param("endTime") Date endTime, @Param("userIds")List<String> userIds);

    long sumExchangeAmount( @Param("userId") String userId);

    List<UserDataWithdrawLimitDto> withdrawLimit(@Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime, @Param("userIds")List<String> userIds);

    Page userAll(Page page,@Param("startTime") Date startTime, @Param("endTime")  Date endTime,@Param("userIds") List<String> userIds);

    Map sumAll(@Param("startTime") Date startTime, @Param("endTime")  Date endTime,@Param("userIds") List<String> userIds);
}
