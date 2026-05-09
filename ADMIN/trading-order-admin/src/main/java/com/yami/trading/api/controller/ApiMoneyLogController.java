package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.MoneyLogService;
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
public class ApiMoneyLogController {
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @RequestMapping("api/moneylog!list.action")
    public Result list(HttpServletRequest request) throws IOException {
        String partyId = SecurityUtils.getCurrentUserId();
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
        Page<MoneyLog> page = new Page<>(pageNo, 20);
        LambdaQueryWrapper<MoneyLog> lambdaQueryWrapper = Wrappers.<MoneyLog>query().lambda().eq(MoneyLog::getUserId, partyId);
        // 不显示不可见的
        lambdaQueryWrapper.ne(MoneyLog::getShow, "0");
        if (StringUtils.isNotEmpty(category)){
            if(category.equals(Constants.MONEYLOG_CATEGORY_EXCHANGE)){
                lambdaQueryWrapper.and((wrapper)->{
                    wrapper.eq(MoneyLog::getCategory, category)
                            .or().eq(MoneyLog::getCategory, Item.forex)
                            .or().eq(MoneyLog::getCategory, Item.indices)
                            .or().eq(MoneyLog::getCategory, Item.commodities)
                            .or().eq(MoneyLog::getCategory, Item.cryptos)
                            .or().eq(MoneyLog::getCategory, Item.US_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.HK_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.SG_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.UK_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.TW_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.A_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.JP_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.INDIA_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.DE_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.BZ_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.INDIA_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.INDIA_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.CATEGORY_GLOBAL)
                            .or().eq(MoneyLog::getCategory, Item.CATEGORY_GOLD)
                            .or().eq(MoneyLog::getCategory, Item.CATEGORY_AI)
                            .or().eq(MoneyLog::getCategory, Item.CATEGORY_ENERGY)
                            .or().eq(MoneyLog::getCategory, Item.CAD_STOCKS)
                            .or().eq(MoneyLog::getCategory, Item.FR_STOCKS)
                    ;
                });
            }else{
                lambdaQueryWrapper.eq(MoneyLog::getCategory, category);
            }
        }

        if (StringUtils.isNotEmpty(contentType)){
            if(contentType.equals(Constants.MONEYLOG_CATEGORY_RECHARGE)){
                lambdaQueryWrapper.and((wrapper)->{
                    wrapper.eq(MoneyLog::getContentType, contentType).or().eq(MoneyLog::getContentType, Constants.MONEYLOG_CATEGORY_BANK_CARD_RECHARGE);
                });
            }else if(contentType.equals(Constants.MONEYLOG_CATEGORY_WITHDRAW)){
                lambdaQueryWrapper.and((wrapper)->{
                    wrapper.eq(MoneyLog::getContentType, contentType).or().eq(MoneyLog::getContentType, Constants.MONEYLOG_CATEGORY_BANK_CARD_WITHDRAW);
                });
            }else{
                lambdaQueryWrapper.eq(MoneyLog::getContentType, contentType);
            }
        }
        if (StringUtils.isNotEmpty(symbolType)){
            lambdaQueryWrapper.in(MoneyLog::getSymbol,symbols);
        }
        lambdaQueryWrapper.orderByDesc(MoneyLog::getCreateTime);
        moneyLogService.page(page, lambdaQueryWrapper);
        for (MoneyLog log : page.getRecords()) {
            log.setContent_type(log.getContentType());
            log.setWallet_type(log.getWalletType());
            log.setAmount_after(log.getAmountAfter().setScale(4, RoundingMode.FLOOR));
            log.setAmount_before(log.getAmountBefore().setScale(4, RoundingMode.FLOOR));
            log.setAmountAfter(log.getAmountAfter().setScale(4, RoundingMode.FLOOR));
            log.setAmountBefore(log.getAmountBefore().setScale(4, RoundingMode.FLOOR));
            if (Constants.MONEYLOG_CONTENT_FINANCE_PROFIT.equals(log.getContentType())) {
                log.setAmount(log.getAmount().add(new BigDecimal(amount)).setScale(4, RoundingMode.FLOOR));

                // 时区转换
                Date showCreateTime = DateTimeTools.transferToShowTime(log.getCreateTime());
                log.setCreateTimeStr(DateUtils.format(showCreateTime, DateUtils.DF_yyyyMMddHHmmss));
            }
        }

        return Result.succeed(page.getRecords());
    }

}
