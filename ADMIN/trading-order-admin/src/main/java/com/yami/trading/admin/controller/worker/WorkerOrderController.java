package com.yami.trading.admin.controller.worker;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.worker.model.WorkerOrderFinishModel;
import com.yami.trading.admin.controller.worker.model.WorkerOrderListModel;
import com.yami.trading.admin.controller.worker.model.WorkerOrderReplyModel;
import com.yami.trading.bean.worker.domain.WorkerOrder;
import com.yami.trading.common.domain.Result;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.worker.WorkerOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("workerOrder")
@Api(tags = "工单管理")
public class WorkerOrderController {

    @Autowired
    private WorkerOrderService workerOrderService;

    @ApiOperation("工单分页")
    @PostMapping("list")
    public Result<Page<WorkerOrder>> list(@RequestBody @Valid WorkerOrderListModel model) {
        Page<WorkerOrder> page = workerOrderService.adminList(
                model.getWorkOrderSn(),
                model.getStatus(),
                model.getMemberId(),
                model.getCurrent(),
                model.getSize()
        );
        return Result.ok(page);
    }

    @ApiOperation("工单详情")
    @GetMapping("detail")
    public Result<Map<String, Object>> detail(@RequestParam Long orderId) {
        return Result.ok(workerOrderService.detail(orderId));
    }

    @ApiOperation("后台回复")
    @PostMapping("reply")
    public Result<?> reply(@RequestBody @Valid WorkerOrderReplyModel model) {
        String admin = SecurityUtils.getSysUser() == null ? "admin" : SecurityUtils.getSysUser().getUsername();
        workerOrderService.adminReply(model.getOrderId(), admin, model.getContent());
        return Result.ok(null);
    }

    @ApiOperation("结束工单")
    @PostMapping("finish")
    public Result<?> finish(@RequestBody @Valid WorkerOrderFinishModel model) {
        workerOrderService.finish(model.getOrderId());
        return Result.ok(null);
    }
}
