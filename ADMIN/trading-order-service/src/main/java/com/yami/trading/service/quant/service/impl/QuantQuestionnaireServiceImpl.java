package com.yami.trading.service.quant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.quant.QuantQuestionnaire;
import com.yami.trading.dao.quant.QuantQuestionnaireMapper;
import com.yami.trading.service.quant.service.QuantQuestionnaireService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class QuantQuestionnaireServiceImpl extends ServiceImpl<QuantQuestionnaireMapper, QuantQuestionnaire> implements QuantQuestionnaireService {

    @Override
    public IPage<QuantQuestionnaire> listPage(Page<QuantQuestionnaire> page, String userCode, String status) {
        QueryWrapper<QuantQuestionnaire> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userCode)) {
            queryWrapper.like("user_code", userCode);
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        IPage<QuantQuestionnaire> result = this.page(page, queryWrapper);
        result.getRecords().forEach(this::fillQuestions);
        return result;
    }

    @Override
    public QuantQuestionnaire fillQuestions(QuantQuestionnaire questionnaire) {
        if (questionnaire == null) {
            return null;
        }
        questionnaire.setQuestion1Question("是否有过加密货币交易经验");
        questionnaire.setQuestion2Question("每日可用于交易的时间");
        questionnaire.setQuestion3Question("对波动和回撤的承受能力");
        questionnaire.setQuestion4Question("当前财务状况舒适区");
        questionnaire.setQuestion5Question("期望的周收益率");
        return questionnaire;
    }
}
