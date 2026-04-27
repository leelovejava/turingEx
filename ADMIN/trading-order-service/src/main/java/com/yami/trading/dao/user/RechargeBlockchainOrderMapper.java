package com.yami.trading.dao.user;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.RechargeBlockchainOrder;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RechargeBlockchainOrderMapper   extends BaseMapper<RechargeBlockchainOrder> {


    Page listRecord(Page page, @Param("roleName")  String roleName,
                    @Param("orderNo") String orderNo,
                    @Param("userName") String userName,@Param("startTime") Date startTime,
                    @Param("endTime") Date endTime,
                    @Param("status") String status,
                    @Param("userIds") List<String> userIds);
}
