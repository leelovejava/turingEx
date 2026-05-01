package com.yami.trading.admin.controller.worker.model;

import com.yami.trading.common.domain.PageRequest;
import lombok.Data;

@Data
public class WorkerOrderListModel extends PageRequest {
    private String workOrderSn;
    private Integer status;
    private Long memberId;
}
