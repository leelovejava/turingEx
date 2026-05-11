package com.yami.trading.huobi.data.internal;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.huobi.data.AdjustmentValueCache;
import com.yami.trading.huobi.data.model.AdjustmentValue;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 行情调整值服务实现类
 * 
 * 该类负责处理行情控制的核心业务逻辑,主要功能包括:
 * 1. 设置行情调整值(支持即时生效和延时生效两种模式)
 * 2. 安全检查:限制单次调整幅度不超过10%
 * 3. 持久化调整值到数据库
 * 4. 获取当前调整值和待生效的延时调整值
 * 
 * 行情控制流程:
 * 前端设置 → 本服务处理 → 保存到缓存 → AbstractGetDataJob应用调整值
 * 
 * 缓存说明:
 * - AdjustmentValueCache: 静态内存缓存,保存当前调整值和延时调整值
 * - 数据库: 持久化保存调整值,系统重启时从数据库加载
 * 
 * @author System
 */
@Slf4j
@Component
public class AdjustmentValueServiceImpl implements AdjustmentValueService {
    private static final ScheduledExecutorService CLEAR_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final Map<String, ScheduledFuture<?>> CLEAR_TASKS = new ConcurrentHashMap<>();

    /**
     * 数据服务,用于获取实时行情数据
     */
    @Autowired
    @Qualifier("dataService")
    private DataService dataService;

    /**
     * 交易品种服务,用于获取和更新品种信息
     */
    @Autowired
    private ItemService itemService;

    /**
     * 设置行情调整值
     * 
     * 这是行情控制的入口方法,根据生效时间选择不同的处理方式:
     * - second <= 0: 即时生效模式,调整值立即应用
     * - second > 0: 延时生效模式,调整值在指定时间内分批应用
     * 
     * 安全机制:
     * - 调整值为0时直接返回,不做任何处理
     * - 单次调整幅度不能超过当前价格的10%,否则抛出异常
     * 
     * 执行流程:
     * 1. 快速检查,调整值为0直接返回
     * 2. 安全检查:调整幅度不超过10%
     * 3. 根据生效时间选择处理模式
     * 4. 保存调整值到缓存
     * 5. 持久化调整值到数据库
     * 
     * @param symbol 交易品种代码
     * @param value 调整值(正数表示加价,负数表示降价)
     * @param second 生效时间(秒),0表示即时生效
     * @throws YamiShopBindException 当调整幅度超过10%时抛出异常
     */
    @Override
    public void adjust(String symbol, double value, double second) {
        // ========== 步骤1:快速检查,调整值为0直接返回 ==========
        if (value == 0) {
            AdjustmentValueCache.getCurrentValue().put(symbol, 0D);
            AdjustmentValueCache.getDelayValue().remove(symbol);
            Item item = this.itemService.findBySymbol(symbol);
            if (item != null && (item.getAdjustmentValue() != 0D || item.getAdjustmentDurationSecond() != 0D)) {
                item.setAdjustmentValue(0D);
                item.setAdjustmentDurationSecond(0D);
                itemService.saveOrUpdate(item);
            }
            return;
        }

        // ========== 步骤2:安全检查,调整幅度不超过10% ==========
        Realtime realtime = dataService.realtime(symbol).get(0);
        BigDecimal newPrice = BigDecimal.valueOf(realtime.getClose());
        BigDecimal plus = BigDecimal.valueOf(Math.abs(value));
        // 计算调整幅度: |调整值| / 当前价格
        if (plus.divide(newPrice, 2, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.1")) > 0) {
            throw new YamiShopBindException("调整偏差过大，超过10%");
        }

        // ========== 步骤3:根据生效时间选择处理模式 ==========
        if (second <= 0) {
            // ========== 即时生效模式 ==========
            // 获取当前已有的调整值
            Double currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);
            
            // 累加新的调整值到缓存
            if (currentValue == null || currentValue == 0) {
                // 之前没有调整值,直接设置新值
                AdjustmentValueCache.getCurrentValue().put(symbol, value);
            } else {
                // 之前已有调整值,累加新值
                AdjustmentValueCache.getCurrentValue().put(symbol, Arith.add(currentValue, value));
            }

            // 【重要】立即调整当前实时价格,避免因为数据还没拉取导致价格不对
            realtime.setClose(Arith.add(realtime.getClose(), value));

            // ========== 持久化调整值到数据库 ==========
            Item item = this.itemService.findBySymbol(symbol);
            if (item.getAdjustmentValue() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
                item.setAdjustmentValue(AdjustmentValueCache.getCurrentValue().get(symbol));
                itemService.saveOrUpdate(item);
            }
        } else {
            // ========== 延时生效模式 ==========
            // 创建延时调整值对象
            AdjustmentValue adjustmentValue = new AdjustmentValue();
            adjustmentValue.setSymbol(symbol);
            adjustmentValue.setValue(value);
            adjustmentValue.setSecond(second);
            
            // 保存到延时缓存,由 AbstractGetDataJob 分批应用
            AdjustmentValueCache.getDelayValue().put(symbol, adjustmentValue);
        }
    }

    @Override
    public void adjust(String symbol, double value, double second, Double durationSecond) {
        adjust(symbol, value, second);
        Item item = this.itemService.findBySymbol(symbol);
        if (item != null) {
            item.setAdjustmentDurationSecond(durationSecond == null ? 0D : durationSecond);
            itemService.saveOrUpdate(item);
        }
        if (second > 0 && durationSecond != null && durationSecond > 0) {
            // 插针模式：等插针分批应用完毕后再开始计时，由 handleRealTimeList 触发
            AdjustmentValueCache.getPendingDurationSecond().put(symbol, durationSecond);
        } else {
            scheduleClear(symbol, durationSecond);
        }
    }

    public void scheduleClear(String symbol, Double durationSecond) {
        if (durationSecond == null || durationSecond <= 0) {
            ScheduledFuture<?> old = CLEAR_TASKS.remove(symbol);
            if (old != null) {
                old.cancel(false);
            }
            return;
        }
        ScheduledFuture<?> old = CLEAR_TASKS.remove(symbol);
        if (old != null) {
            old.cancel(false);
        }
        long delayMs = (long) (durationSecond * 1000);
        log.info("[scheduleClear] 品种={}, 持续时间={}秒, 将在{}ms后清除调整值", symbol, durationSecond, delayMs);
        ScheduledFuture<?> task = CLEAR_EXECUTOR.schedule(() -> {
            try {
                log.info("[scheduleClear] 品种={}, 定时器触发, 开始清除调整值", symbol);
                AdjustmentValueCache.getCurrentValue().put(symbol, 0D);
                AdjustmentValueCache.getDelayValue().remove(symbol);
                Item item = this.itemService.findBySymbol(symbol);
                if (item != null) {
                    item.setAdjustmentValue(0D);
                    item.setAdjustmentDurationSecond(0D);
                    itemService.saveOrUpdate(item);
                }
                log.info("[scheduleClear] 品种={}, 调整值已清除", symbol);
            } catch (Exception e) {
                log.error("[scheduleClear] 品种={}, 清除调整值异常", symbol, e);
            } finally {
                CLEAR_TASKS.remove(symbol);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
        CLEAR_TASKS.put(symbol, task);
    }

    /**
     * 获取指定品种的当前调整值
     * <p>
     * 该方法用于查询当前已生效的调整值,注意:
     * - 返回值为null表示该品种没有设置调整值
     * - 返回值为0表示调整值已清零
     * - 调整值是累加的,每次调用adjust都会累加新值
     *
     * @param symbol 交易品种代码
     * @return 当前调整值,如果没有设置则返回null
     */
    @Override
    public double getCurrentValue(String symbol) {
        // 从缓存获取当前调整值
        return AdjustmentValueCache.getCurrentValue().get(symbol);
    }

    /**
     * 获取指定品种的延时调整值
     * 
     * 该方法用于查询待生效的延时调整值,返回的对象包含:
     * - symbol: 交易品种代码
     * - value: 剩余待应用的调整值
     * - second: 剩余生效时间(秒)
     * 
     * 延时调整值由 AbstractGetDataJob.handleRealTimeList() 分批应用,
     * 每次采集应用一小部分,直到调整值全部应用完毕。
     * 
     * @param symbol 交易品种代码
     * @return 延时调整值对象,如果没有待生效的调整值则返回null
     */
    @Override
    public AdjustmentValue getDelayValue2(String symbol) {
        // 从缓存获取延时调整值
        return AdjustmentValueCache.getDelayValue().get(symbol);
    }

    /**
     * 设置数据服务(用于单元测试或依赖注入)
     * 
     * @param dataService 数据服务实例
     */
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * 设置交易品种服务(用于单元测试或依赖注入)
     * 
     * @param itemService 交易品种服务实例
     */
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

}
