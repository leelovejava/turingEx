package com.yami.trading.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.FollowMoneyLog;

import java.util.Date;
import java.util.List;

public interface FollowMoneyLogService extends IService<FollowMoneyLog> {

    Page pageMoneyLog(String userCode, Page page, String rolename, Date startTime,
                      Date endTime,
                      String userName,String log,String category,
                      List<String> userIds);

    /**
     * 单条数据
     */
    FollowMoneyLog getMoneyLogByUserId(String userId);


    FollowMoneyLog getMoneyLogByFirstRecharge(String userId);

}
