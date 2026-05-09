package com.yami.trading.huobi.data.job;


import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.Arith;
import com.yami.trading.huobi.data.AdjustmentValueCache;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DataDBService;
import com.yami.trading.huobi.data.model.AdjustmentValue;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 行情数据采集任务抽象基类
 * 
 * 该类是行情数据采集的核心基类,定义了数据采集和行情控制的通用逻辑。
 * 子类需要实现具体的数据获取逻辑。
 * 
 * 主要功能:
 * 1. 从数据源获取实时行情数据
 * 2. 应用行情调整值(支持即时生效和延时生效两种模式)
 * 3. 更新价格缓存(最高价、最低价)
 * 4. 异步保存行情数据到数据库
 * 
 * 行情控制流程:
 * 1. 获取交易品种的当前调整值和延时调整值
 * 2. 处理延时调整值(分批逐步应用)
 * 3. 将调整值应用到行情价格(收盘价、买价、卖价)
 * 4. 保存处理后的行情数据
 * 
 * @author System
 */
@Slf4j
public abstract class AbstractGetDataJob implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 首次启动标识,用于系统启动时的特殊处理逻辑
     */
    public static volatile boolean first = true;
    
    /**
     * 数据接口调用间隔时长(毫秒)
     * 例如: 3000表示每3秒采集一次数据
     */
    protected int interval;
    
    /**
     * 系统参数服务,用于获取系统配置参数
     */
    @Autowired
    protected SysparaService sysparaService;
    
    /**
     * 数据库服务,用于异步保存行情数据到数据库
     */
    @Autowired
    protected DataDBService dataDBService;
    
    /**
     * 火币数据服务,用于从火币交易所或爬虫获取行情数据
     */
    @Autowired
    protected HobiDataService hobiDataService;
    
    /**
     * 交易品种服务,用于获取品种信息(如小数位数、符号转换等)
     */
    @Autowired
    protected ItemService itemService;

    /**
     * 启动数据采集任务
     * 创建一个新的线程并运行该任务
     */
    public void start() {
        new Thread(this, getName()).start();
    }

    /**
     * 任务执行方法,由子类实现具体的数据采集逻辑
     * 子类应该在这个方法中实现:
     * 1. while(true) 无限循环
     * 2. 调用 realtimeHandle() 获取和处理行情
     * 3. Thread.sleep(interval) 间隔等待
     */
    @Override
    public abstract void run();

    /**
     * 获取任务名称
     * 
     * @return 任务名称字符串,用于线程命名和日志标识
     */
    public abstract String getName();

    /**
     * 处理实时行情数据
     * 由子类实现,负责从交易所获取行情数据
     * 
     * @param symbols 交易品种代码字符串,多个品种用逗号分隔
     */
    public abstract void realtimeHandle(String symbols);

    /**
     * 处理实时行情数据列表(行情控制核心方法)
     * 
     * 执行流程:
     * 1. 标准化交易品种代码
     * 2. 获取当前调整值和延时调整值
     * 3. 处理延时生效的调整值(分批逐步应用)
     * 4. 应用当前调整值到行情价格(收盘价、买价、卖价)
     * 5. 保存处理后的行情数据
     * 
     * 延时调整值的应用逻辑:
     * - 计算分批次数: 总时长(秒) * 1000 / 采集间隔(毫秒)
     * - 每次采集应用一小部分调整值
     * - 剩余调整值和剩余时间更新到延时缓存
     * - 直到调整值全部应用完毕
     * 
     * @param realtimeList 实时行情数据列表
     */
    public void handleRealTimeList(List<Realtime> realtimeList) {
        // 遍历每个行情数据进行处理
        for (Realtime realtime : realtimeList) {
            try {
                // ========== 步骤1:获取标准化的交易品种信息 ==========
                String symbol = realtime.getSymbol();
                // 将外部符号转换为内部标准符号
                symbol = itemService.getSymbolByRemarks(symbol);
                // 获取该品种的小数位数,用于价格精度控制
                Integer decimal = itemService.getDecimal(symbol);
                // 获取交易品种详细信息
                Item item = this.itemService.findBySymbol(symbol);
                
                // ========== 步骤2:获取调整值 ==========
                // 获取当前已生效的调整值
                Double currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);
                // 获取待生效的延时调整值
                AdjustmentValue delayValue = AdjustmentValueCache.getDelayValue().get(symbol);

                // ========== 步骤3:处理延时生效的调整值 ==========
                if (delayValue != null) {
                    // 计算需要分几次调整完成: 总时长(秒) * 1000 / 采集间隔(毫秒)
                    int frequency = (int) Arith.div(Arith.mul(delayValue.getSecond(), 1000.0D), this.interval);

                    // 如果只需要一次或更少,则立即应用剩余的全部调整值
                    if (frequency <= 1) {
                        // 更新当前调整值
                        if (currentValue == null) {
                            AdjustmentValueCache.getCurrentValue().put(symbol, delayValue.getValue());
                        } else {
                            AdjustmentValueCache.getCurrentValue().put(symbol, delayValue.getValue() + currentValue);
                        }

                        // 更新数据库中的调整值
                        if (item.getAdjustmentValue() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
                            item.setAdjustmentValue(AdjustmentValueCache.getCurrentValue().get(symbol));
                            itemService.saveOrUpdate(item);
                        }
                        
                        // 从延时缓存中移除该调整值,表示已完成
                        AdjustmentValueCache.getDelayValue().remove(symbol);
                    } else {
                        // ========== 分批调整:计算本次应应用的调整值 ==========
                        double currentValueFrequency = BigDecimal.valueOf(delayValue.getValue() / frequency)
                                .setScale(decimal, RoundingMode.HALF_UP).doubleValue();
                        
                        // 更新当前调整值
                        if (currentValue == null) {
                            AdjustmentValueCache.getCurrentValue().put(symbol, currentValueFrequency);
                        } else {
                            AdjustmentValueCache.getCurrentValue().put(symbol, currentValue + currentValueFrequency);
                        }

                        // 减少延时调整值的剩余量
                        delayValue.setValue(delayValue.getValue() - currentValueFrequency);
                        // 减少剩余时间
                        delayValue.setSecond(Arith.sub(delayValue.getSecond(), Arith.div(this.interval, 1000.0D)));
                        // 更新延时缓存
                        AdjustmentValueCache.getDelayValue().put(symbol, delayValue);

                        // 更新数据库中的调整值
                        if (item.getAdjustmentValue() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
                            item.setAdjustmentValue(AdjustmentValueCache.getCurrentValue().get(symbol));
                            itemService.saveOrUpdate(item);
                        }
                    }
                }

                // ========== 步骤4:应用当前调整值到行情价格 ==========
                currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);
                
                if (currentValue != null && currentValue != 0) {
                    // 行情控制已开启：记录应用前价格，便于核对是否生效
                    double beforeClose = realtime.getClose();
                    double beforeAsk = realtime.getAsk();
                    double beforeBid = realtime.getBid();
                    // 同时调整三个关键价格: 收盘价、买价、卖价
                    realtime.setClose(BigDecimal.valueOf(realtime.getClose() + currentValue)
                            .setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setAsk(BigDecimal.valueOf(realtime.getAsk() + currentValue)
                            .setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setBid(BigDecimal.valueOf(realtime.getBid() + currentValue)
                            .setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    logger.info("[MarketAdjust] 品种={}, 行情控制=开启, 调整值={}, 收盘价:{}->{}, 卖价:{}->{}, 买价:{}->{}",
                            symbol, currentValue, beforeClose, realtime.getClose(), beforeAsk, realtime.getAsk(), beforeBid, realtime.getBid());
                } else {
                    // 行情控制未开启：输出当前值及是否存在延时生效任务
                    AdjustmentValue delayValueNow = AdjustmentValueCache.getDelayValue().get(symbol);
                    if (delayValueNow != null) {
                        logger.info("[MarketAdjust] 品种={}, 行情控制=未开启, 调整值={}, 存在延时任务={}",
                                symbol, currentValue, true);
                    }
                }

                // ========== 步骤5:保存处理后的行情数据 ==========
                try {
                    // 缓存中最新一条Realtime数据
                    saveData(realtime, symbol, item);
                } catch (Exception e) {
                    logger.error("---> AbstractGetDataJob.handleRealTimeList debug 位置4, realtime -> symbol:{}, error: ", realtime.getSymbol(), e);
                    throw e;
                }
            } catch (Exception e) {
                log.error("数据采集失败 {}:[]", realtime.getSymbol(), JSONObject.toJSON(realtime), e);
            }
        }
    }

    /**
     * 保存行情数据到缓存和数据库
     * 
     * 主要功能:
     * 1. 处理时间戳格式(10位秒级转换为13位毫秒级)
     * 2. 更新最高价缓存
     * 3. 更新最低价缓存(只接受大于0的价格)
     * 4. 异步保存行情数据到数据库
     * 
     * @param realtime 实时行情数据
     * @param symbol 交易品种代码
     * @param item 交易品种信息
     */
    private void saveData(Realtime realtime, String symbol, Item item) {
        // 获取缓存中的最高价和最低价
        Double high = DataCache.getRealtimeHigh(symbol);
        Double low = DataCache.getRealtimeLow(symbol);
        
        // 处理时间戳:如果是10位秒级时间戳,转换为13位毫秒级
        if (realtime.getTs().toString().length() <= 10) {
            realtime.setTs(Long.valueOf(realtime.getTs() + "000"));
        }
        
        // 设置交易品种名称
        realtime.setName(item.getName());
        
        // 更新最高价缓存
        if (high == null || realtime.getHigh() > high) {
            // 刷新内存中的 high
            DataCache.putRealtimeHigh(symbol, realtime.getHigh());
        }
        
        // 更新最低价缓存(只接受大于0的价格)
        if ((low == null || realtime.getLow() < low) && realtime.getLow() > 0) {
            // 刷新内存中的 low，并且只使用值大于 0 的 low
            DataCache.putRealtimeLow(symbol, realtime.getLow());
        }
        
        // 异步保存行情数据到数据库
        this.dataDBService.saveAsyn(realtime);
    }

}
