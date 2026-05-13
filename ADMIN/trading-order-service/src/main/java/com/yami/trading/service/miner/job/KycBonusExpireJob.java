package com.yami.trading.service.miner.job;

import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 实名体验金过期定时任务
 * 每天凌晨1:05执行，检查发放超过7天仍未使用的体验金，自动取消冻结
 */
@Component
@Slf4j
public class KycBonusExpireJob {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private MoneyLogService moneyLogService;

    /**
     * 每10分钟执行一次（启动后延迟10秒首次执行）
     * 逻辑：查询 kyc_bonus_time 超过7天且 kyc_bonus_amount > 0 的用户，解冻其USDT冻结余额
     */
    @Scheduled(fixedDelay = 600000, initialDelay = 10000)
    public void expireKycBonus() {
        // 计算7天前的时间点，作为过期判断基准
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date expireTime = cal.getTime();

        // 查询所有体验金已过期（发放时间超过7天）且尚未消费的用户
        List<User> users = userService.listExpiredKycBonus(expireTime);
        log.info("体验金过期任务开始，待处理用户数：{}", users.size());

        for (User user : users) {
            try {
                double amount = user.getKycBonusAmount();

                // 从USDT扩展钱包冻结余额中扣除体验金（amount=0表示可用余额不变，frozenAmount=-amount表示解冻）
                walletService.updateExtend(user.getUserId(), WalletConstants.WALLET_USDT, 0, -amount);

                // 记录资金日志
                MoneyLog moneyLog = new MoneyLog();
                moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
                moneyLog.setAmount(BigDecimal.valueOf(-amount));
                moneyLog.setLog("实名体验金7天未使用，自动取消冻结");
                moneyLog.setUserId(user.getUserId());
                moneyLog.setWalletType(WalletConstants.WALLET_USDT);
                moneyLog.setContentType(WalletConstants.MONEYLOG_CONTENT_KYC_BONUS_EXPIRE);
                moneyLogService.save(moneyLog);

                // 清零用户体验金记录，防止重复处理
                user.setKycBonusAmount(0.0);
                user.setKycBonusTime(null);
                userService.updateById(user);

                log.info("体验金过期处理成功, userId={}, amount={}", user.getUserId(), amount);
            } catch (Exception e) {
                log.error("体验金过期处理失败, userId={}", user.getUserId(), e);
            }
        }

        log.info("体验金过期任务结束");
    }
}
