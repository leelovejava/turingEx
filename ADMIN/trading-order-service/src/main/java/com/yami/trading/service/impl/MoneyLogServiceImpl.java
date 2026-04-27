package com.yami.trading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.dao.user.MoneyLogMapper;
import com.yami.trading.service.MoneyLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class MoneyLogServiceImpl extends ServiceImpl<MoneyLogMapper, MoneyLog> implements MoneyLogService {
    @Override
    public Page pageMoneyLog(String userCode, Page page, String rolename, Date startTime,
                             Date endTime,
                             String userName,String log,String category,
                            List<String> userIds) {
        page.setOptimizeCountSql(false);
        return baseMapper.pageMoneyLog(page, userCode, rolename, startTime, endTime, userName,log,category,
                userIds);
    }

    /**
     * 单条记录
     */
    @Override
    public MoneyLog getMoneyLogByUserId(String userId) {
        QueryWrapper<MoneyLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // String startDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        String endDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";
        // queryWrapper.ge("create_time", startDate);
        queryWrapper.lt("create_time", endDate);
        queryWrapper.orderByDesc("create_time");
        List<MoneyLog> list = baseMapper.selectList(queryWrapper);
        if (null == list || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 首次充值记录
     */
    @Override
    public MoneyLog getMoneyLogByFirstRecharge(String userId) {
        QueryWrapper<MoneyLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("log", userId);
        queryWrapper.eq("content_type", Constants.MONEYLOG_CONTENT_FIRST_RECHARGE);
        // LocalDate yesterday = LocalDate.now().minusDays(1);
        // String startDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        // String endDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";
        // queryWrapper.ge("create_time", startDate);
        // queryWrapper.lt("create_time", endDate);
        queryWrapper.orderByDesc("create_time");
        List<MoneyLog> list = baseMapper.selectList(queryWrapper);
        if (null == list || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

}


