package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.HighLevelAuthRecord;
import com.yami.trading.bean.model.RealNameAuthRecord;
import com.yami.trading.bean.user.dto.HighLevelAuthRecordDto;
import com.yami.trading.dao.user.HighLevelAuthRecordMapper;
import com.yami.trading.service.HighLevelAuthRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighLevelAuthRecordServiceImpl  extends ServiceImpl<HighLevelAuthRecordMapper, HighLevelAuthRecord> implements HighLevelAuthRecordService {
    @Override
    public HighLevelAuthRecord findByUserId(String userId) {
        return getOne(Wrappers.<HighLevelAuthRecord>query().lambda().eq(HighLevelAuthRecord::getUserId, userId));
    }

    @Override
    public Page<HighLevelAuthRecordDto> pageRecord(Page page,String rolename, String status, String userName,List<String> userIds) {

        return baseMapper.listRecord(page,rolename,status,userName,userIds);
    }

    @Override
    public long waitCount(List<String> userIds) {
       LambdaQueryWrapper<HighLevelAuthRecord> lambdaQueryWrapper= Wrappers.<HighLevelAuthRecord>query().lambda().eq(HighLevelAuthRecord::getStatus,1);
       if (CollectionUtil.isNotEmpty(userIds)){
           lambdaQueryWrapper.in(HighLevelAuthRecord::getUserId,userIds);
       }
        return  count(lambdaQueryWrapper);
    }


}
