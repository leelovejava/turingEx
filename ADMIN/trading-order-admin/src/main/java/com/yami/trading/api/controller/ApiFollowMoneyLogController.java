package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.FollowMoneyLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.FollowMoneyLogService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Slf4j
public class ApiFollowMoneyLogController {
    @Autowired
    FollowMoneyLogService followMoneyLogService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @RequestMapping("api/followmoneylog!list.action")
    public Result list(HttpServletRequest request) throws IOException {
        String partyId = SecurityUtils.getUser().getUserId();
        String page_no = request.getParameter("page_no");
        String symbolType = request.getParameter("symbolType");
        if (StringUtils.isNullOrEmpty(page_no)
                || !StringUtils.isInteger(page_no) || Integer.valueOf(page_no) <= 0) {
            page_no = "1";
        }
        double amount = 0;
        int pageNo = Integer.valueOf(page_no);
        String category = request.getParameter("category");
        String contentType = request.getParameter("contentType");
        if (symbolType==null){
            symbolType="";
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        Page<FollowMoneyLog> page = new Page<>(pageNo, 20);
        LambdaQueryWrapper<FollowMoneyLog> lambdaQueryWrapper = Wrappers.<FollowMoneyLog>query().lambda().eq(FollowMoneyLog::getUserId, partyId);
        // 不显示不可见的
        lambdaQueryWrapper.ne(FollowMoneyLog::getShow, "0");
        if (StringUtils.isNotEmpty(category)){
            lambdaQueryWrapper.eq(FollowMoneyLog::getCategory, category);
        }
        if (StringUtils.isNotEmpty(contentType)){
            lambdaQueryWrapper.eq(FollowMoneyLog::getContentType, contentType);
        }
        if (StringUtils.isNotEmpty(symbolType)){
            lambdaQueryWrapper.in(FollowMoneyLog::getSymbol,symbols);
        }
        lambdaQueryWrapper.orderByDesc(FollowMoneyLog::getCreateTime);
        followMoneyLogService.page(page, lambdaQueryWrapper);
        for (FollowMoneyLog log : page.getRecords()) {
            log.setContent_type(log.getContentType());
            log.setWallet_type(log.getWalletType());
            log.setAmount_after(log.getAmountAfter().setScale(4, RoundingMode.FLOOR));
            log.setAmount_before(log.getAmountBefore().setScale(4, RoundingMode.FLOOR));
            if (Constants.MONEYLOG_CONTENT_FINANCE_PROFIT.equals(log.getContentType())) {
                log.setAmount(log.getAmount().add(new BigDecimal(amount)));

                // 时区转换
                Date showCreateTime = DateTimeTools.transferToShowTime(log.getCreateTime());
                log.setCreateTimeStr(DateUtils.format(showCreateTime, DateUtils.DF_yyyyMMddHHmmss));
            }
        }

        return Result.succeed(page.getRecords());
    }

}
