package com.yami.trading.admin.controller.quant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.quant.model.QuantQuestionnaireSaveModel;
import com.yami.trading.admin.controller.quant.model.QuantQuestionnaireStatusModel;
import com.yami.trading.bean.quant.QuantQuestionnaire;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.service.quant.service.QuantQuestionnaireService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "AI量化问卷")
@Validated
@RestController
@CrossOrigin
@RequestMapping("normal/adminQuantQuestionnaireAction!")
public class AdminQuantQuestionnaireController {

    @Autowired
    private QuantQuestionnaireService quantQuestionnaireService;

    @ApiOperation("问卷列表")
    @GetMapping("list.action")
    public Result<IPage<QuantQuestionnaire>> list(Page<QuantQuestionnaire> page,
                                                   @RequestParam(required = false) String userCode,
                                                   @RequestParam(required = false) String status) {
        return Result.ok(quantQuestionnaireService.listPage(page, userCode, status));
    }

    @ApiOperation("问卷详情")
    @GetMapping("detail.action")
    public Result<QuantQuestionnaire> detail(@RequestParam String uuid) {
        QuantQuestionnaire questionnaire = quantQuestionnaireService.getById(uuid);
        if (questionnaire == null) {
            return Result.failed("问卷不存在");
        }
        return Result.ok(quantQuestionnaireService.fillQuestions(questionnaire));
    }

    @ApiOperation("提交问卷")
    @PostMapping("save.action")
    public Result<String> save(@Valid @RequestBody QuantQuestionnaireSaveModel model) {
        QuantQuestionnaire questionnaire = new QuantQuestionnaire();
        BeanUtils.copyProperties(model, questionnaire);
        questionnaire.setStatus("N");
        questionnaire.setAuditRemark("");
        quantQuestionnaireService.save(questionnaire);
        return Result.ok("提交成功");
    }

    @ApiOperation("审核问卷")
    @PostMapping("updateStatus.action")
    public Result<String> updateStatus(@Valid @RequestBody QuantQuestionnaireStatusModel model) {
        if (!"N".equalsIgnoreCase(model.getStatus())
                && !"PASS".equalsIgnoreCase(model.getStatus())
                && !"NOPASS".equalsIgnoreCase(model.getStatus())) {
            throw new YamiShopBindException("状态仅支持 N/PASS/NOPASS");
        }

        QuantQuestionnaire questionnaire = quantQuestionnaireService.getById(model.getUuid());
        if (questionnaire == null) {
            return Result.failed("问卷不存在");
        }

        questionnaire.setStatus(model.getStatus().toUpperCase());
        questionnaire.setAuditRemark(model.getAuditRemark());
        quantQuestionnaireService.updateById(questionnaire);
        return Result.ok("操作成功");
    }
}
