package com.yami.trading.service.quant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.quant.QuantQuestionnaire;

public interface QuantQuestionnaireService extends IService<QuantQuestionnaire> {

    IPage<QuantQuestionnaire> listPage(Page<QuantQuestionnaire> page, String userCode, String status);

    QuantQuestionnaire fillQuestions(QuantQuestionnaire questionnaire);

    QuantQuestionnaire getByUserId(String userId);
}
