package com.yami.trading.admin.controller.worker.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WorkerOrderFinishModel {
    @NotNull
    private Long orderId;
}
