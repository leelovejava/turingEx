package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.FollowMoneyLog;
import com.yami.trading.bean.user.dto.SumBSalanceDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FollowMoneyLogMapper extends BaseMapper<FollowMoneyLog> {


   Page pageMoneyLog(Page page, @Param("userCode") String userId,
                     @Param("rolename")  String rolename, @Param("startTime") Date startTime,
                     @Param("endTime") Date endTime,
                     @Param("userName") String userName,
                     @Param("log") String log,@Param("category") String category,
                     @Param("userIds") List<String> userIds);

   SumBSalanceDto sumBSalance(@Param("category") String category,
                              @Param("contentType") String contentType,
                              @Param("userId") String userId,
                              @Param("nowTime") Date now);//2222
}
