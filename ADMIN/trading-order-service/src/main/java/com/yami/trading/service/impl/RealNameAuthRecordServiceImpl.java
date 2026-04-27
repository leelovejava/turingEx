package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.HighLevelAuthRecord;
import com.yami.trading.bean.model.RealNameAuthRecord;
import com.yami.trading.dao.user.RealNameAuthRecordMapper;
import com.yami.trading.service.RealNameAuthRecordService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealNameAuthRecordServiceImpl extends ServiceImpl<RealNameAuthRecordMapper, RealNameAuthRecord> implements RealNameAuthRecordService {
    @Override
    public RealNameAuthRecord getByUserId(String userId) {
        return getOne(Wrappers.<RealNameAuthRecord>query().lambda().eq(RealNameAuthRecord::getUserId, userId));
    }
    public List<RealNameAuthRecord> findByStatus(int status) {
        return list(Wrappers.<RealNameAuthRecord>query().lambda().eq(RealNameAuthRecord::getStatus, status));
    }
    @Override
    public Page pageRecord(Page page, String rolename, String idNumber, String status,String userCode,@Param("userIds")  List<String> userIds) {
        return baseMapper.pageRecord(page,rolename,idNumber,status,userCode,userIds);
    }

    @Override
    public boolean isPass(String userId) {
        RealNameAuthRecord kyc = getByUserId(userId);
        if (null == kyc)
            return Boolean.FALSE;
        return kyc.getStatus() == 2;
    }

    @Override
    public long waitCount(List<String> userIds) {

      LambdaQueryWrapper<RealNameAuthRecord>  lambdaQueryWrapper= Wrappers.<RealNameAuthRecord>query().lambda().eq(RealNameAuthRecord::getStatus,1);
        if (CollectionUtil.isNotEmpty(userIds)){
            lambdaQueryWrapper.in(RealNameAuthRecord::getUserId,userIds);
        }
        return  count(lambdaQueryWrapper);
    }

}
