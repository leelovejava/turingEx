package com.yami.trading.admin.controller.c2c;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.c2c.model.BankCardOrderModel;
import com.yami.trading.admin.controller.c2c.model.C2cOrderPayModel;
import com.yami.trading.admin.controller.c2c.model.GetOrderPaymentsModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.ManualReleaseModel;
import com.yami.trading.admin.model.OrderCancelModel;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.PageRequest;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.C2cOrderLock;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cOrderService;
import com.yami.trading.service.chat.otc.OtcOnlineChatMessageService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("bankCardOrder")
@Api(tags = "银行卡订单")
@Slf4j
public class BankCardController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    C2cOrderService c2cOrderService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @Autowired
    C2cAdvertService c2cAdvertService;

    @Autowired
    OtcOnlineChatMessageService otcOnlineChatMessageService;

    @Autowired
    PermissionFacade permissionFacade;

    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<?> list(@RequestBody @Valid BankCardOrderModel model) {

        Page page = new Page(model.getCurrent(), model.getSize());
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(model.getDirection())) {
            list.add(model.getDirection());
        } else {
            list.add("recharge");
            list.add("withdraw");
        }
        c2cOrderService.pagedBankCardOrderQuery(page, list
                , model.getStatus(), model.getUser_code(),
                model.getRolename(), model.getOrder_no(), permissionFacade.getOwnerUserIds());
        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        List<String> nos = new ArrayList<String>();
        for (Map<String, Object> map : (List<Map<String, Object>>) page.getRecords()) {
            nos.add(map.get("order_no").toString());
            String methodType = String.valueOf((Integer) map.get("method_type"));
            if (map.containsKey("img")) {
                if (map.get("img") != null) {
                    map.put("img", awsS3OSSFileService.getUrl(map.get("img").toString()));
                }
            }
            if (map.containsKey("method_img")) {
                if (map.get("method_img") != null) {
                    map.put("method_img", awsS3OSSFileService.getUrl(map.get("method_img").toString()));
                }
            }
            if (map.containsKey("qrcode")) {
                if (map.get("qrcode") != null) {
                    if ((!map.get("qrcode").toString().startsWith("http"))) {
                        map.put("qrcode", awsS3OSSFileService.getUrl(map.get("qrcode").toString()));
                    }
                }
            }

            map.put("method_type_name", pmtMap.containsKey(methodType) ? pmtMap.get(methodType) : methodType);
        }
        String order_nos = String.join(",", nos);
        Map<String, Integer> unreadMsgs = this.otcOnlineChatMessageService.unreadMsgs(nos);
        log.info(JSONUtil.toJsonStr(unreadMsgs));
        for (Map<String, Object> map : (List<Map<String, Object>>) page.getRecords()) {
            String orderNo = map.get("order_no").toString();
            if (unreadMsgs.containsKey(orderNo)) {
                map.put("unread_msg", unreadMsgs.get(orderNo));
            }
            if (null == map.get("rolename")) {
                map.put("roleNameDesc", "");
            } else {
                String roleName = map.get("rolename").toString();
                map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
            }
            if (map.get("amount") != null) {
                map.put("amount", Double.valueOf(map.get("amount").toString()).doubleValue() -
                        Double.valueOf(map.get("coin_amount_fee").toString()).doubleValue());
            }


            // 如果这些 time 字段返回的是 Date 类型，则无需强制做时区处理 TODO
            // create_time handle_time cancel_time pay_time close_time
            Date curCreateTime = (Date) map.get("create_time");
            log.info("-------> BankCardController.list createTime: " + curCreateTime);
        }

        return Result.ok(page);
    }

    @ApiOperation(value = "手动放行")
    @PostMapping("orderPass")
    public Result<?> manualRelease(@RequestBody @Valid ManualReleaseModel model) {
        String order_no = model.getOrderNo();
        C2cOrder order = this.c2cOrderService.get(order_no);
        if (null == order) {
            throw new YamiShopBindException("订单不存在");
        }
        boolean lock = false;
        try {
            if (!C2cOrderLock.add(order_no)) {
                throw new YamiShopBindException("系统繁忙，请稍后重试");
            }
            lock = true;
            sysUserService.checkSafeWord(model.getSafeword());
            c2cOrderService.manualRelease(order, SecurityUtils.getSysUser().getUsername());
            ThreadUtils.sleep(100);
        } catch (Exception e) {
            log.error("银行卡手动放行异常", e);
            return Result.failed(e.getMessage());
        } finally {
            if (lock) {
                C2cOrderLock.remove(order_no);
            }
        }
        return Result.ok(null);
    }

    @ApiOperation(value = "取消订单")
    @PostMapping("orderCancel")
    public Result<?> orderCancel(@RequestBody @Valid OrderCancelModel model) {

        C2cOrder order = this.c2cOrderService.get(model.getOrderNo());
        if (null == order) {
            throw new YamiShopBindException("订单不存在");
        }
        order.setRemark(model.getReason());
        this.c2cOrderService.saveOrderCancel(order, "manager");
        User user = userService.getById(order.getPartyId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_BANK_CARD_RW);
        log.setUsername(user.getUserName());
        log.setUserId(order.getPartyId());
        log.setOperator(SecurityUtils.getSysUser().getUsername());
        log.setLog("银行卡订单取消，订单号[" + order.getOrderNo() + "]");
        log.setCreateTime(new Date());
        logService.save(log);
        return Result.ok(null);
    }

    /**
     * 手动转账
     */
    @PostMapping("orderPay")
    @ApiOperation("手动转账")
    public Result orderPay(@RequestBody @Valid C2cOrderPayModel model) {

        boolean lock = false;
        try {
            if (!C2cOrderLock.add(model.getOrder_no())) {
                throw new YamiShopBindException("系统繁忙，请稍后重试");
            }
            lock = true;

            if (StringUtils.isNullOrEmpty(model.getPayment_method_id_order_pay())) {
                throw new YamiShopBindException("请选择订单支付方式");

            }

            sysUserService.checkSafeWord(model.getSafeword());
            c2cOrderService.saveOrderPay(model.getOrder_no(), model.getSafeword(), SecurityUtils.getSysUser().getUsername(), model.getPayment_method_id_order_pay());
            C2cOrder c2cOrder = c2cOrderService.get(model.getOrder_no());
            User orderUser = userService.getById(c2cOrder.getPartyId());
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_BANK_CARD_RW);
            log.setUsername(orderUser.getUserName());
            log.setUserId(orderUser.getUserId());
            log.setOperator(SecurityUtils.getSysUser().getUsername());
            log.setLog("银行卡提现订单转账完成");
            log.setCreateTime(new Date());
            logService.save(log);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        } finally {
            if (lock) {
                C2cOrderLock.remove(model.getOrder_no());
            }
        }
        return Result.succeed();
    }

    /**
     * 获取 银行卡订单的支付方式
     */
    @GetMapping("getOrderPayments")
    @ApiOperation("获取 银行卡订单的支付方式")
    public Result getOrderPayments(@Valid GetOrderPaymentsModel request) {
        List<C2cPaymentMethod> methodList = c2cOrderService.getOrderPayments(request.getOrder_no(), false);
        return Result.succeed(methodList);
    }

}
