package com.yami.trading.admin.controller.quant.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QuantQuestionnaireStatusModel {

    @NotBlank(message = "uuid不能为空")
    private String uuid;

    @NotBlank(message = "状态不能为空")
    private String status;

    private String auditRemark;
}
