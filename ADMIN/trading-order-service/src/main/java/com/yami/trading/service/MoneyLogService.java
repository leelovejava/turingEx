package com.yami.trading.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.MoneyLog;

import java.util.Date;
import java.util.List;

public interface MoneyLogService extends IService<MoneyLog> {

    Page pageMoneyLog(String userCode, Page page, String rolename, Date startTime,
                      Date endTime,
                      String userName,String log,String category,
                      List<String> userIds);

    /**
     * 单条数据
     */
    MoneyLog getMoneyLogByUserId(String userId);


    MoneyLog getMoneyLogByFirstRecharge(String userId);

}
