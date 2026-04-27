package com.yami.trading.dao.trader;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TraderFollowUserMapper extends BaseMapper<TraderFollowUser> {
    List<Map<String, Object>> listDatas(@Param("pageNo") long pageNo, @Param("pageSize") long pageSize, @Param("name") String name, @Param("username") String username);
}
