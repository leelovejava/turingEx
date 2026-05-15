package com.yami.trading.service.worker;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.worker.domain.WorkerOrder;

import java.util.List;
import java.util.Map;

public interface WorkerOrderService extends IService<WorkerOrder> {

    WorkerOrder createOrder(String memberId, String account, String title, String content);

    Page<WorkerOrder> userList(String memberId, Integer status, long current, long size);

    Page<WorkerOrder> adminList(String workOrderSn, Integer status, Long memberId, long current, long size, List<String> children);

    Map<String, Object> detail(Long orderId);

    void userReply(Long orderId, String memberId, String account, String content);

    void adminReply(Long orderId, String adminAccount, String content);

    void finish(Long orderId);
}
