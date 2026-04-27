package com.yami.trading.admin.controller.trader;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.trading.api.service.UserCacheService;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.model.WalletTransferLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.*;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.WalletLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("normal/follow/wallet")
@Api(tags = "跟单钱包")
@Slf4j
public class AdminFollowWalletController {
    @Autowired
    ChannelBlockchainService channelBlockchainService;
    @Autowired
    RechargeBlockchainOrderService rechargeBlockchainOrderService;
    @Autowired
    WalletService walletService;
    @Autowired
    FollowWalletService followWalletService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserCacheService userCacheService;
    @Autowired
    WithdrawService withdrawService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    DataService dataService;
    @Autowired
    TipService tipService;
    @Autowired
    ItemService itemService;
    @Autowired
    WalletLogService walletLogService;

    @Autowired
    WalletTransferLogService walletTransferLogService;


    /**
     * 资金划转历史记录
     */
    @GetMapping("list.action")
    @ApiOperation("资金划转历史记录")
    public Result list(HttpServletRequest request) {
        String partyId = SecurityUtils.getCurrentUserId();
        String type = request.getParameter("type"); // 0-现货账户转跟单账户，1-跟单账户转现货账户
        LambdaQueryWrapper<WalletTransferLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletTransferLog::getPartyId, partyId);
        if(!StringUtils.isEmptyString(type)) {
            lambdaQueryWrapper.eq(WalletTransferLog::getCategory, type);
        }
        List<WalletTransferLog> walletTransferLogs = walletTransferLogService.list(lambdaQueryWrapper);
        return Result.succeed(walletTransferLogs);
    }
}
