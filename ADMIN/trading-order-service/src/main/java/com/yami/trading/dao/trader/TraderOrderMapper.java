package com.yami.trading.dao.trader;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.trading.bean.trader.domain.TraderOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TraderOrderMapper extends BaseMapper<TraderOrder> {
    List<Map<String, Object>> listDatas(@Param("pageNo") long pageNo, @Param("pageSize") long pageSize, @Param("partyId") String partyId);

    List<Map<String, Object>> listMDatas(@Param("pageNo") long pageNo, @Param("pageSize") long pageSize, @Param("name") String name, @Param("rolename") String rolename, @Param("username") String username);
}
