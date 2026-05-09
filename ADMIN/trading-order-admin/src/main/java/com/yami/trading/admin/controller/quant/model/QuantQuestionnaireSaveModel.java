package com.yami.trading.admin.controller.quant.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QuantQuestionnaireSaveModel {

    private String userId;

    private String userCode;

    private String userMail;

    @NotBlank(message = "问题1答案不能为空")
    private String question1Answer;

    @NotBlank(message = "问题2答案不能为空")
    private String question2Answer;

    @NotBlank(message = "问题3答案不能为空")
    private String question3Answer;

    @NotBlank(message = "问题4答案不能为空")
    private String question4Answer;

    @NotBlank(message = "问题5答案不能为空")
    private String question5Answer;
}
