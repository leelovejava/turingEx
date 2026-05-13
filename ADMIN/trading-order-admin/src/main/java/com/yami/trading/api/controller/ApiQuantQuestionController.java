package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.api.model.QuantQuestionInsertModel;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.quant.QuantQuestionnaire;
import com.yami.trading.common.domain.Result;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.quant.service.QuantQuestionnaireService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/quant/question")
@Api(tags = "AI量化问卷")
@Validated
public class ApiQuantQuestionController {

    @Autowired
    private QuantQuestionnaireService quantQuestionnaireService;

    @Autowired
    private UserService userService;

    @ApiOperation("是否提交过AI量化问卷")
    @GetMapping("/get")
    public Result<Map<String, Object>> get() {
        String userId = SecurityUtils.getCurrentUserId();
        QueryWrapper<QuantQuestionnaire> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("create_time").last("limit 1");
        QuantQuestionnaire one = quantQuestionnaireService.getOne(qw, false);

        Map<String, Object> data = new HashMap<>();
        data.put("exist", one != null);
        if (one != null) {
            data.put("status", one.getStatus());
        }
        return Result.succeed(data);
    }

    @ApiOperation("提交AI量化问卷")
    @PostMapping("/insert")
    public Result<Void> insert(@Valid QuantQuestionInsertModel model) {
        String userId = SecurityUtils.getCurrentUserId();
        User user = userService.getById(userId);

        QuantQuestionnaire q = new QuantQuestionnaire();
        q.setUserId(userId);
        if (user != null) {
            q.setUserCode(user.getUserCode());
            q.setUserMail(user.getUserMail());
        }
        q.setQuestion1Answer(model.getQuestion1Answer());
        q.setQuestion2Answer(model.getQuestion2Answer());
        q.setQuestion3Answer(model.getQuestion3Answer());
        q.setQuestion4Answer(model.getQuestion4Answer());
        q.setQuestion5Answer(model.getQuestion5Answer());
        q.setStatus("N");
        q.setAuditRemark("");

        quantQuestionnaireService.save(q);
        return Result.succeed(null);
    }
}
