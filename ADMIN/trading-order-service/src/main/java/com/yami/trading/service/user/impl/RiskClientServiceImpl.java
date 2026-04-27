package com.yami.trading.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.dao.user.RiskClientMapper;
import com.yami.trading.service.user.RiskClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RiskClientServiceImpl extends ServiceImpl<RiskClientMapper, RiskClient> implements RiskClientService {

    public Page<RiskClient> pageListRiskClient(int status, int pageNum, int pageSize) {
        if (pageNum <= 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }

        QueryWrapper<RiskClient> queryWrapper = new QueryWrapper<RiskClient>();
        if (status >= 0) {
            queryWrapper.eq("status", status);
        }

        Page<RiskClient> pageInfo = new Page(pageNum, pageSize);
        return this.getBaseMapper().selectPage(pageInfo, queryWrapper);
    }

    public RiskClient getRiskClient(String clientKey, String clientType, String type) {
        if (StrUtil.isBlank(clientKey) || StrUtil.isBlank(clientType) || StrUtil.isBlank(type)) {
            throw new BusinessException("缺失必须参数");
        }

        QueryWrapper<RiskClient> queryWrapper = new QueryWrapper<RiskClient>();
        queryWrapper.eq("client_key", clientKey.trim());
        queryWrapper.eq("client_type", clientType.trim());
        queryWrapper.eq("type", type.trim());

        return this.getBaseMapper().selectOne(queryWrapper);
    }

}
