package com.yami.trading.huobi.data.job;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 虚拟货币数据采集任务类
 * 
 * 该类负责采集虚拟货币(加密货币)的实时行情数据,继承自 AbstractGetDataJob。
 * 
 * 主要功能:
 * 1. 从数据库查询所有虚拟货币品种
 * 2. 调用火币数据服务获取实时行情
 * 3. 将行情数据交由父类处理(应用调整值、保存数据)
 * 4. 循环执行,每3秒采集一次
 * 
 * 数据采集流程:
 * 1. 查询数据库获取所有虚拟货币品种
 * 2. 构建币种符号字符串(去除usdt后缀)
 * 3. 调用 realtimeCryptos() 获取实时行情
 * 4. 调用父类 handleRealTimeList() 处理行情数据
 * 5. 等待3秒后重复执行
 * 
 * @author System
 */
@Slf4j
@Component
public class CryptosGetDataJob extends AbstractGetDataJob {

    /**
     * 首次启动标识,用于初始化采集间隔
     */
    public static volatile boolean first = true;
    
    /**
     * 日志记录器
     */
    private static Logger logger = LoggerFactory.getLogger(CryptosGetDataJob.class);

    /**
     * 系统参数服务,用于获取系统配置
     */
    @Autowired
    private SysparaService sysparaService;
    
    /**
     * 火币数据服务,用于从火币交易所或爬虫获取虚拟货币行情数据
     */
    @Autowired
    private HobiDataService hobiDataService;
    
    /**
     * 交易品种服务,用于查询虚拟货币品种列表
     */
    @Autowired
    private ItemService itemService;

    /**
     * 启动虚拟货币数据采集任务
     * 创建一个新的线程并运行该任务
     */
    @Override
    public void start() {
        log.debug("[CryptoJob] ========== CryptosGetDataJob 启动 ==========");
        new Thread(this, "CryptosGetDataJob").start();
        log.debug("[CryptoJob] ========== CryptosGetDataJob 线程已创建 ==========");
    }

    /**
     * 任务执行主循环
     * 
     * 执行流程:
     * 1. 首次执行时初始化采集间隔(3000毫秒=3秒)
     * 2. 进入无限循环
     * 3. 查询数据库获取所有虚拟货币品种
     * 4. 构建币种符号字符串
     * 5. 调用 realtimeHandle() 获取和处理行情数据
     * 6. 等待指定间隔后继续下一次采集
     * 
     * 异常处理:
     * - 采集过程中出现异常会被捕获并记录日志
     * - 无论是否异常,都会等待指定间隔后继续循环
     */
    @Override
    public void run() {
        log.debug("[CryptoJob] ========== CryptosGetDataJob.run() 开始执行 ==========");

        // 首次执行时初始化采集间隔
        if (first) {
            /**
             * 数据采集间隔时长(毫秒)
             * 默认设置为3000毫秒(3秒)
             */
            this.interval = 3000;
            log.debug("[CryptoJob] 首次执行, 设置采集间隔: {}ms", this.interval);
            first = false;
        }
        
        log.debug("[CryptoJob] 进入数据采集循环...");
        
        // 无限循环采集数据
        while (true) {
            log.debug("[CryptoJob] >>> 循环执行中, 当前时间: {}", System.currentTimeMillis());
            try {
                log.debug("[CryptoJob] --- 查询数据库获取加密货币列表...");
                // 查询所有虚拟货币品种
                List<Item> byType = itemService.findByType(Item.cryptos);
                log.debug("[CryptoJob] --- 查询到 {} 个加密货币", byType.size());
                
                // 构建币种符号字符串,去除usdt后缀
                // 例如: BTCUSDT -> BTC, ETHUSDT -> ETH
                String symbols = byType.stream()
                        .map(i -> i.getRemarks().replace("usdt", ""))
                        .collect(Collectors.joining(","));
                log.debug("[CryptoJob] 开始采集加密货币数据, 币种数量: {}, symbols: {}", byType.size(), symbols);
                
                // 处理实时行情数据
                this.realtimeHandle(symbols);
            } catch (Exception e) {
                // 记录异常日志但不中断循环
                logger.error("[CryptoJob] 采集加密货币数据失败", e);
            } finally {
                // 无论是否异常,都等待指定间隔
                log.debug("[CryptoJob] <<< 等待 {}ms 后继续采集...", this.interval);
                ThreadUtils.sleep(this.interval);
            }
        }

    }

    /**
     * 获取任务名称
     * 
     * @return 任务名称字符串,用于日志标识
     */
    @Override
    public String getName() {
        return "虚拟货币数据采集";
    }

    /**
     * 处理虚拟货币实时行情数据
     * 
     * 执行流程:
     * 1. 调用火币数据服务获取虚拟货币实时行情
     * 2. 将行情数据交由父类处理(应用调整值、保存数据)
     * 
     * @param symbols 币种符号字符串,多个币种用逗号分隔(不含usdt后缀)
     */
    @Override
    public void realtimeHandle(String symbols) {
        // 从火币数据服务获取虚拟货币实时行情
        List<Realtime> realtimeList = this.hobiDataService.realtimeCryptos(symbols);
        // 调用父类方法处理行情数据(应用调整值、保存到缓存和数据库)
        super.handleRealTimeList(realtimeList);
    }


}
