package com.yami.trading.api.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * AI量化的问卷调查
 */
@Data
public class QuantQuestionInsertModel {

    @NotBlank(message = "question1Answer不能为空")
    private String question1Answer;

    @NotBlank(message = "question2Answer不能为空")
    private String question2Answer;

    @NotBlank(message = "question3Answer不能为空")
    private String question3Answer;

    @NotBlank(message = "question4Answer不能为空")
    private String question4Answer;

    @NotBlank(message = "question5Answer不能为空")
    private String question5Answer;
}
