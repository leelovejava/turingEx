package com.yami.trading.service.worker.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.worker.domain.WorkerOrder;
import com.yami.trading.bean.worker.domain.WorkerOrderContent;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.dao.worker.WorkerOrderContentMapper;
import com.yami.trading.dao.worker.WorkerOrderMapper;
import com.yami.trading.service.worker.WorkerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class WorkerOrderServiceImpl extends ServiceImpl<WorkerOrderMapper, WorkerOrder> implements WorkerOrderService {

    @Autowired
    private WorkerOrderContentMapper workerOrderContentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkerOrder createOrder(String memberId, String account, String title, String content) {
        if (memberId == null) {
            throw new YamiShopBindException("user id is required");
        }
        if (StrUtil.isBlank(title)) {
            throw new YamiShopBindException("title is required");
        }
        Date now = new Date();
        WorkerOrder order = new WorkerOrder();
        order.setTitle(title.trim());
        order.setWorkOrderStatus(String.valueOf(WorkerOrder.STATUS_PROCESSING));
        order.setWorkOrderSn(buildOrderSn());
        order.setAccount(account);
        order.setMemberId(memberId);
        order.setLocalCreateTime(now);
        order.setCreateTime(now);
        this.save(order);

        if (StrUtil.isNotBlank(content)) {
            WorkerOrderContent first = new WorkerOrderContent();
            first.setWorkerOrderId(order.getId());
            first.setContent(content.trim());
            first.setMemberId(memberId);
            first.setAccount(account);
            first.setReplyRoleType(WorkerOrderContent.ROLE_USER);
            first.setLocalCreateTime(now);
            first.setCreateTime(now);
            workerOrderContentMapper.insert(first);
        }
        return order;
    }

    @Override
    public Page<WorkerOrder> userList(String memberId, Integer status, long current, long size) {
        Page<WorkerOrder> page = new Page<>(current, size);
        page.addOrder(OrderItem.desc("create_time"));
        LambdaQueryWrapper<WorkerOrder> query = new LambdaQueryWrapper<WorkerOrder>()
                .eq(WorkerOrder::getMemberId, memberId)
                .eq(status != null, WorkerOrder::getWorkOrderStatus, String.valueOf(status));
        return this.page(page, query);
    }

    @Override
    public Page<WorkerOrder> adminList(String workOrderSn, Integer status, Long memberId, long current, long size) {
        Page<WorkerOrder> page = new Page<>(current, size);
        page.addOrder(OrderItem.desc("create_time"));
        LambdaQueryWrapper<WorkerOrder> query = new LambdaQueryWrapper<WorkerOrder>()
                .like(StrUtil.isNotBlank(workOrderSn), WorkerOrder::getWorkOrderSn, workOrderSn)
                .eq(status != null, WorkerOrder::getWorkOrderStatus, String.valueOf(status))
                .eq(memberId != null, WorkerOrder::getMemberId, memberId);
        return this.page(page, query);
    }

    @Override
    public Map<String, Object> detail(Long orderId) {
        WorkerOrder order = this.getById(orderId);
        if (order == null) {
            throw new YamiShopBindException("work order not found");
        }
        List<WorkerOrderContent> contentList = workerOrderContentMapper.selectList(
                new LambdaQueryWrapper<WorkerOrderContent>()
                        .eq(WorkerOrderContent::getWorkerOrderId, orderId)
                        .orderByAsc(WorkerOrderContent::getCreateTime)
        );
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("contents", contentList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReply(Long orderId, String memberId, String account, String content) {
        WorkerOrder order = checkReplyAllowed(orderId);
        if (!order.getMemberId().equals(memberId)) {
            throw new YamiShopBindException("no permission");
        }
        insertReply(orderId, memberId, account, content, WorkerOrderContent.ROLE_USER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminReply(Long orderId, String adminAccount, String content) {
        checkReplyAllowed(orderId);
        insertReply(orderId, null, adminAccount, content, WorkerOrderContent.ROLE_SYSTEM);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long orderId) {
        WorkerOrder order = this.getById(orderId);
        if (order == null) {
            throw new YamiShopBindException("work order not found");
        }
        order.setWorkOrderStatus(String.valueOf(WorkerOrder.STATUS_FINISHED));
        this.updateById(order);
    }

    private WorkerOrder checkReplyAllowed(Long orderId) {
        WorkerOrder order = this.getById(orderId);
        if (order == null) {
            throw new YamiShopBindException("work order not found");
        }
        if (!String.valueOf(WorkerOrder.STATUS_PROCESSING).equals(order.getWorkOrderStatus())) {
            throw new YamiShopBindException("work order is finished");
        }
        return order;
    }

    private void insertReply(Long orderId, String memberId, String account, String content, int roleType) {
        if (StrUtil.isBlank(content)) {
            throw new YamiShopBindException("content is required");
        }
        Date now = new Date();
        WorkerOrderContent reply = new WorkerOrderContent();
        reply.setWorkerOrderId(orderId);
        reply.setContent(content.trim());
        reply.setMemberId(memberId);
        reply.setAccount(account);
        reply.setReplyRoleType(roleType);
        reply.setLocalCreateTime(now);
        reply.setCreateTime(now);
        workerOrderContentMapper.insert(reply);
    }

    private String buildOrderSn() {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return ts + rand;
    }
}
