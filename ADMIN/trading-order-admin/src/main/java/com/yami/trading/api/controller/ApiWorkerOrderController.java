package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.worker.domain.WorkerOrder;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.user.UserService;
import com.yami.trading.service.worker.WorkerOrderService;
import com.yami.trading.api.service.UserCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@Api(tags = "用户工单")
public class ApiWorkerOrderController {
    private static final Pattern NUMERIC = Pattern.compile("^\\d+$");

    private final String action = "/api/workerOrder!";

    @Autowired
    private WorkerOrderService workerOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCacheService userCacheService;

    @ApiOperation("发起工单")
    @PostMapping(action + "create.action")
    public Result<WorkerOrder> create(@RequestParam String title, @RequestParam(required = false) String content) {
        String memberId = currentMemberId();
        String account = currentAccount(memberId);
        WorkerOrder order = workerOrderService.createOrder(memberId, account, title, content);
        return Result.ok(order);
    }

    @ApiOperation("我的工单")
    @GetMapping(action + "list.action")
    public Result<Page<WorkerOrder>> list(@RequestParam(value = "page_no", defaultValue = "1") Long pageNo,
                                          @RequestParam(value = "page_size", defaultValue = "10") Long pageSize,
                                          @RequestParam(required = false) Integer status) {
        String memberId = currentMemberId();
        Page<WorkerOrder> page = workerOrderService.userList(memberId, status, pageNo, pageSize);
        return Result.ok(page);
    }

    @ApiOperation("工单详情")
    @GetMapping(action + "detail.action")
    public Result<Map<String, Object>> detail(@RequestParam Long order_id) {
        String memberId = currentMemberId();
        Map<String, Object> detail = workerOrderService.detail(order_id);
        WorkerOrder order = (WorkerOrder) detail.get("order");
        if (order == null || !memberId.equals(order.getMemberId())) {
            throw new YamiShopBindException("no permission");
        }
        return Result.ok(detail);
    }

    @ApiOperation("用户回复")
    @PostMapping(action + "reply.action")
    public Result<?> reply(@RequestParam Long order_id, @RequestParam String content) {
        String memberId = currentMemberId();
        String account = currentAccount(memberId);
        workerOrderService.userReply(order_id, memberId, account, content);
        return Result.ok(null);
    }

    private String currentMemberId() {
        User user = userCacheService.currentUser();
        return user.getUserId();
    }



    private String currentAccount(String memberId) {
        User user = userService.getById(String.valueOf(memberId));
        if (user == null) {
            return null;
        }
        if (user.getUserCode() != null && !user.getUserCode().isEmpty()) {
            return user.getUserCode();
        }
        if (user.getUserMobile() != null && !user.getUserMobile().isEmpty()) {
            return user.getUserMobile();
        }
        return user.getUserName();
    }
}
