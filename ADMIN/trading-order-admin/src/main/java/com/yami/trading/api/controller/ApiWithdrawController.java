package com.yami.trading.api.controller;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.api.util.ServletUtil;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Withdraw;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.GoogleAuthenticator;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.IdentifyingCodeTimeWindowService;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WithdrawService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.service.user.WalletLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提现
 */
@RestController
@CrossOrigin
@RequestMapping("api/withdraw")
@Api(tags = "提现")
@Slf4j
public class ApiWithdrawController implements InitializingBean {
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionTokenService sessionTokenService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    protected WalletLogService walletLogService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.printf(passwordEncoder.encode("000000"));
    }

    /**
     * 首次进入页面，传递session_token
     */
    @GetMapping("withdrawOpen")
    @ApiOperation("首次进入页面，传递session_token")
    public Result withdrawOpen() {
        String partyId = SecurityUtils.getCurrentUserId();
        String session_token = this.sessionTokenService.savePut(partyId);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("session_token", session_token);
        // 返回给前端用户绑定的验证方式（谷歌、短信、邮箱）
        List<Map<String, String>> typeList = userService.getTypeListById(partyId);
        data.put("verifcode_type_list", typeList);
        return Result.succeed(data);
    }

    /**
     * 提现申请
     * <p>
     * safeword 资金密码
     * amount 提现金额
     * from 客户转出地址
     * currency 货币 CNY USD
     * channel 渠道 USDT,BTC,ETH
     * verifcode_type 验证方式
     * verifcode_value 验证的值
     */
    @ApiOperation("提现申请")
    @PostMapping("apply")
    public Result apply(HttpServletRequest request, String session_token, String safeword,
                        String amount, String from, String currency,
                        String channel, String language, String verifcode_type, String verifcode_value) {
        String partyId = SecurityUtils.getCurrentUserId();
        String error = this.verif(amount);
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        double amount_double = Double.valueOf(amount).doubleValue();
        // 万能密码 000000 跳过验证码验证
        if (!"000000".equals(safeword) && StringUtils.isNotEmpty(verifcode_type)) {
            userService.checkCode(partyId, verifcode_type, verifcode_value);
        }
        Object object = this.sessionTokenService.cacheGet(session_token);
        this.sessionTokenService.del(session_token);
//        if (null == object || !SecurityUtils.getCurrentUserId().equals((String) object)) {
//            throw new YamiShopBindException("请稍后再试");
//        }
        Withdraw withdraw = new Withdraw();
        withdraw.setUserId(partyId);
        withdraw.setVolume(new BigDecimal(amount_double));
        withdraw.setAddress(from);
        withdraw.setCurrency(currency);
        withdraw.setTx("");
        withdraw.setDeviceIp(ServletUtil.getIp(request));
        // 保存
        this.withdrawService.saveApply(withdraw, channel, null, language);
        return Result.succeed(null);
    }

    /**
     * 提现订单详情
     * <p>
     * order_no 订单号
     */
    @ApiOperation("提现订单详情")
    @GetMapping("get")
    public Result get(@RequestParam String order_no) throws IOException {
        Withdraw withdraw = this.withdrawService.findByOrderNo(order_no);
        if (withdraw == null) {
// 订单不存在
            throw new YamiShopBindException("Order does not exist");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_no", withdraw.getOrderNo());
        map.put("volume", withdraw.getVolume());
        map.put("amount", withdraw.getAmount());
        map.put("create_time", withdraw.getCreateTime());
        map.put("to", withdraw.getAddress());
        map.put("fee", withdraw.getAmountFee().doubleValue());
        map.put("coin_blockchain", withdraw.getMethod());
        map.put("coin",
                withdraw.getMethod().indexOf("BTC") != -1 || withdraw.getMethod().indexOf("ETH") != -1
                        ? withdraw.getMethod()
                        : "USDT");
        map.put("state", withdraw.getStatus());
        map.put("tx", withdraw.getTx());
        map.put("failure_msg", withdraw.getFailureMsg());
        return Result.succeed(map);
    }

    /**
     * 提现记录
     */
    @GetMapping("list")
    @ApiOperation("提现记录")
    public Result list(@RequestParam String page_no) {
        if (StringUtils.isNullOrEmpty(page_no)) {
            page_no = "1";
        }
        if (!StringUtils.isInteger(page_no)) {
// 页码不是整数
            throw new YamiShopBindException("Page number must be an integer");
        }
        if (Integer.valueOf(page_no).intValue() <= 0) {
// 页码不能小于等于0
            throw new YamiShopBindException("Page number must be greater than 0");
        }
        int page_no_int = Integer.valueOf(page_no).intValue();
        List<Map<String, Object>> data = this.walletLogService.pagedQueryWithdraw(page_no_int, 10, SecurityUtils.getCurrentUserId(), "1").getRecords();
        for (Map<String, Object> log : data) {
            if (null == log.get("coin") || !StringUtils.isNotEmpty(log.get("coin").toString())) {
                log.put("coin", Constants.WALLET);
            } else {
                log.put("coin", log.get("coin").toString().toUpperCase());
            }

            // createtime createTimeTs TODO
            String createtime = (String)log.get("createtime");
            Integer createTimeTs = (Integer) log.get("createTimeTs");
            if (StrUtil.isNotBlank(createtime)) {
                Date oriCreateTime = DateTimeTools.readQueryTime(createtime, "yyyy-MM-dd HH:mm:ss", ZoneId.systemDefault());
                log.put("createtime", oriCreateTime);
            }
            if (createTimeTs != null && createTimeTs > 0) {
                long showCreateTimeTs = DateTimeTools.transferShowTimeToClientTime(createTimeTs.longValue());
                log.put("createTimeTs", showCreateTimeTs);
            }
        }

        return Result.succeed(data);
    }

    /**
     * 提现手续费
     * <p>
     * channel 渠道 USDT,OTC
     * amount 提币数量
     */
    @GetMapping("fee")
    @ApiOperation("提现手续费")
    public Result fee(String channel, String amount) {
        String error = this.verif(amount);
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        double amount_double = Double.valueOf(amount).doubleValue();
        Map<String, Object> map = new HashMap<String, Object>();
        DecimalFormat df = new DecimalFormat("#.########");
        double fee = 0;
        channel = StringUtils.isEmptyString(channel) ? "USDT" : channel;
        if (channel.indexOf("BTC") != -1 || channel.indexOf("ETH") != -1) {
            map.put("withdraw_fee_type", "rate");
            fee = this.withdrawService.getOtherChannelWithdrawFee(amount_double);
        } else {
            // 手续费(USDT)
            // 提现手续费类型,fixed是单笔固定金额，rate是百分比，part是分段
            String withdraw_fee_type = this.sysparaService.find("withdraw_fee_type").getSvalue();
            // fixed单笔固定金额 和 rate百分比 的手续费数值
            double withdraw_fee = Double.valueOf(this.sysparaService.find("withdraw_fee").getSvalue());
            if ("fixed".equals(withdraw_fee_type)) {
                fee = withdraw_fee;
            }
            if ("rate".equals(withdraw_fee_type)) {
                withdraw_fee = Arith.div(withdraw_fee, 100);
                fee = Arith.mul(amount_double, withdraw_fee);
            }
            if ("part".equals(withdraw_fee_type)) {
                // 提现手续费part分段的值
                String withdraw_fee_part = this.sysparaService.find("withdraw_fee_part").getSvalue();
                String[] withdraw_fee_parts = withdraw_fee_part.split(",");
                for (int i = 0; i < withdraw_fee_parts.length; i++) {
                    double part_amount = Double.valueOf(withdraw_fee_parts[i]);
                    double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
                    if (amount_double <= part_amount) {
                        fee = part_fee;
                        break;
                    }
                    i++;
                }
            }
            map.put("withdraw_fee_type", withdraw_fee_type);
        }

        double volume_last = Arith.sub(amount_double, fee);
        if (volume_last < 0) {
            volume_last = 0;
        }
        map.put("fee", fee);
        map.put("volume_last", df.format(volume_last));
        return Result.succeed(map);
    }

    /**
     * 提现限额
     * <p>
     * channel 渠道 USDT,OTC
     */
    @GetMapping("limit")
    @ApiOperation("提现限额")
    public Result limit(@RequestParam(value = "channel") String channel) {
        return Result.succeed(withdrawService.getWithdrawLimitBySymbol(channel, true));
    }

    private String verif(String amount) {
        if (StringUtils.isNullOrEmpty(amount)) {
            // 提币数量必填
            return "Withdrawal amount is required";
        }
        if (!StringUtils.isDouble(amount)) {
            // 提币数量输入错误，请输入浮点数
            return "Withdrawal amount input error, please enter a decimal number";
        }
        if (Double.valueOf(amount).doubleValue() <= 0) {
            // 提币数量不能小于等于0
            return "Withdrawal amount cannot be less than or equal to 0";
        }

        return null;
    }
}
