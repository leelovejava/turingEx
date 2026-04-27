package com.yami.trading.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.admin.dto.HomeViewDto;
import com.yami.trading.admin.dto.WaitCountDto;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.user.dto.UserBenefitsDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.service.*;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("home")
@Api(tags = "首页")
@Slf4j
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    UserDataService userDataService;

    @Autowired
    WalletService walletService;


    @Autowired
    RealNameAuthRecordService realNameAuthRecordService;

    @Autowired
    HighLevelAuthRecordService highLevelAuthRecordService;

    @Autowired
    RechargeBlockchainOrderService rechargeBlockchainOrderService;



    @Autowired
    WithdrawService withdrawService;

    @Autowired
    private PermissionFacade permissionFacade;
    @ApiOperation(value = "今日数据统计")
    @PostMapping("view")
    public Result<HomeViewDto> view(){
        HomeViewDto homeViewDto=new HomeViewDto();
        List<String> userIds= permissionFacade.getOwnerUserIds();
        if (CollectionUtil.isNotEmpty(userIds)){
            homeViewDto.setAllUserCount(userService.count(Wrappers.<User>query().lambda().in(User::getUserId,userIds)));
        }
        else {
            homeViewDto.setAllUserCount(userService.count());
        }
        homeViewDto.setTodayUserCount(userService.countToDay(userIds));
        Date date=new Date();
        log.info(DateUtil.formatDate(date,"yyyy-MM-dd HH:mm:ss"));

        log.info(DateUtil.formatDate(DateUtil.minDate(date),"yyyy-MM-dd HH:mm:ss"));
        log.info(DateUtil.formatDate(DateUtil.maxDate(date),"yyyy-MM-dd HH:mm:ss"));

        UserBenefitsDto userBenefitsDto= userDataService.daySumData(DateUtil.minDate(date),DateUtil.maxDate(date),userIds);
        homeViewDto.setSumUsdtAmount(walletService.sumMoney(userIds));
        homeViewDto.setTodayRechargeUserCount(userDataService.countTodayRechargeUser(userIds));
        if (userBenefitsDto!=null){
            homeViewDto.setRecharge(userBenefitsDto.getRechargeUsdt());
            homeViewDto.setWithdraw(userBenefitsDto.getWithdraw());
            homeViewDto.setBalanceAmount(userBenefitsDto.getWithdraw().subtract(userBenefitsDto.getRechargeUsdt()));
            homeViewDto.setTodayTotleIncome(userBenefitsDto.getTotleIncome());
        }

        UserBenefitsDto sumUserBenefitsDto= userDataService.daySumData(null,null,userIds);
        if (sumUserBenefitsDto!=null){
            homeViewDto.setSumRecharge(sumUserBenefitsDto.getRechargeUsdt());
            homeViewDto.setSumWithdraw(sumUserBenefitsDto.getWithdraw());
            homeViewDto.setTotleIncome(sumUserBenefitsDto.getTotleIncome());
        }
        String today = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
        Map<String, Object> daySumData = userDataService.daySumDataOld(today, userIds);
        Map<String, Object> allSumData = userDataService.daySumDataOld(null, userIds);

        homeViewDto.setTodayTotleIncome(new BigDecimal(daySumData.getOrDefault("totle_income", 0).toString()).setScale(2, BigDecimal.ROUND_FLOOR));
        homeViewDto.setTotleIncome(new BigDecimal(allSumData.getOrDefault("totle_income", 0).toString()).setScale(2, BigDecimal.ROUND_FLOOR));
        return  Result.ok(homeViewDto);
    }


    @ApiOperation(value = "待处理统计数据")
    @PostMapping("waitCount")
    public Result<WaitCountDto> waitCount(){
        WaitCountDto waitCountDto=new WaitCountDto();
        List<String> userIds= permissionFacade.getOwnerUserIds();

        waitCountDto.setHighLevelAuthCount(highLevelAuthRecordService.waitCount(userIds));
        waitCountDto.setRealNameAuthCount(realNameAuthRecordService.waitCount(userIds));
        waitCountDto.setWithdrawCount(withdrawService.waitCount(userIds));
        waitCountDto.setRechargeCount(rechargeBlockchainOrderService.waitCount(userIds));
        return  Result.ok(waitCountDto);
    }



}
