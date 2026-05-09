package com.yami.trading.service.contract;

import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.ContractRedisKeys;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 合约订单计算服务实现类
 * 
 * 该类负责合约交易的核心计算逻辑,主要功能包括:
 * 1. 实时计算订单盈亏
 * 2. 止盈止损触发判断
 * 3. 强制平仓逻辑(全仓/单个持仓)
 * 4. 支持预设结果功能(可控盈亏)
 * 5. 计算今日盈亏和总盈亏
 * 
 * 核心概念:
 * - 最小波动点(pips): 价格变动的最小单位
 * - 点值(pipsAmount): 每波动一个点对应的金额
 * - 止盈价(stopPriceProfit): 到达此价格自动获利平仓
 * - 止损价(stopPriceLoss): 到达此价格自动止损平仓
 * - 强制平仓(force close): 保证金不足时系统强制平仓
 */
@Slf4j
@Service
public class ContractOrderCalculationServiceImpl implements ContractOrderCalculationService {
    
    /**
     * 交易品种服务,用于获取品种信息
     */
    @Autowired
    private ItemService itemService;
    
    /**
     * 平仓线比例,默认为110%
     * 计算公式: 订金价值 / 亏损金额 = 110%
     * 当亏损达到此比例时触发强制平仓
     */
    public BigDecimal order_close_line = new BigDecimal("1.1");
    
    /**
     * 平仓方式:
     * 1 = 全仓模式: 计算所有持仓的总盈亏,不足时全仓强平
     * 2 = 单个持仓模式: 只计算单个持仓的盈亏,单独强平
     */
    public int order_close_line_type = 1;
    
    /**
     * 合约订单服务,用于订单CRUD操作
     */
    @Autowired
    private ContractOrderService contractOrderService;
    
    /**
     * 数据服务,用于获取实时行情数据
     */
    @Qualifier("dataService")
    @Autowired
    @Lazy
    private DataService dataService;
    
    /**
     * 钱包服务,用于查询用户余额
     */
    @Autowired
    private WalletService walletService;
    
    /**
     * 系统参数服务,用于获取系统配置
     */
    @Resource
    private SysparaService sysparaService;
    
    /**
     * 用户服务,用于查询用户信息
     */
    @Autowired
    private UserService userService;
    
    /**
     * 日志记录器
     */
    private Logger logger = LogManager.getLogger(ContractOrderCalculationServiceImpl.class);

    /**
     * 保存订单计算结果
     * 
     * 这是订单结算的核心方法,执行流程如下:
     * 1. 从Redis缓存获取订单信息,缓存未命中则从数据库查询
     * 2. 检查订单状态,非持仓状态直接返回
     * 3. 获取实时行情价格
     * 4. 判断是否有预设结果(订单级别优先级高于用户级别)
     * 5. 根据预设结果或实际价格进行盈亏结算
     * 6. 触发止盈止损或强制平仓逻辑
     * 
     * 预设结果说明:
     * - 1 = 强制盈利
     * - -1 = 强制亏损
     * - 0 = 未设置,使用真实价格
     * 
     * @param order_no 订单号
     */
    @Override
    public void saveCalculation(String order_no) {
        try {
            // ========== 第一步：从Redis缓存中获取订单信息 ==========
            ContractOrder order = contractOrderService.findByOrderNoRedis(order_no);

            // 如果Redis中没有，则从数据库查询并重新缓存
            if (order == null) {
                ContractOrder byOrderNo = contractOrderService.findByOrderNo(order_no);
                if (byOrderNo != null) {
                    RedisUtil.set(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrderNo(), order);
                }
            }

            // 检查订单状态，如果不是持仓状态则直接返回
            if (!ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
                /**
                 * 状态已改变，退出处理
                 */
                return;
            }

            // ========== 第二步：获取实时价格数据 ==========
            List<Realtime> list = this.dataService.realtime(order.getSymbol());
            if (list.isEmpty()) {
                return;
            }
            Realtime realtime = list.get(0);

            // 获取当前收盘价
            BigDecimal close = BigDecimal.valueOf(realtime.getClose());

            // 计算涨跌触发价格:
            // add = 开仓价 + 最小波动点(买涨盈利、买跌亏损的触发点)
            // subtract = 开仓价 - 最小波动点(买跌盈利、买涨亏损的触发点)
            BigDecimal add = order.getTradeAvgPrice().add(order.getPips());
            BigDecimal subtract = order.getTradeAvgPrice().subtract(order.getPips());

            // ========== 第三步：判断预设结果(优先级:订单级别 > 用户级别) ==========
            Integer preResult = null;

            // 首先检查订单级别是否设置了预设结果(-1=亏损,1=盈利,0=未设置)
            if (order.getOptionPreResult() != null && order.getOptionPreResult() != 0) {
                preResult = order.getOptionPreResult();
            } else {
                // 如果订单未设置，则检查用户级别是否设置了预设结果
                User user = userService.findByUserId(order.getPartyId());
                if (user != null && user.getOptionPreResult() != null && user.getOptionPreResult() != 0) {
                    preResult = user.getOptionPreResult();
                }
            }

            // ========== 第四步：根据预设结果或实际价格进行结算 ==========
            if (preResult != null) {
                // 使用预设结果进行结算(可控盈亏模式)
                if (preResult == 1) {
                    // 预设结果为盈利，直接结算盈利
                    settle(order, "profit", close);
                } else if (preResult == -1) {
                    // 预设结果为亏损，直接结算亏损
                    settle(order, "loss", close);
                }
            } else {
                // 没有设置预设结果，使用原始的涨跌判断逻辑(真实市场模式)
                if (ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
                    /*
                     * 0 买涨:
                     * 如果当前价格 >= 开仓价+最小波动点，则盈利
                     * 如果当前价格 <= 开仓价-最小波动点，则亏损
                     */
                    if (close.compareTo(add) >= 0) {
                        settle(order, "profit", close);
                    }

                    if (close.compareTo(subtract) <= 0) {
                        settle(order, "loss", close);
                    }

                } else {
                    /*
                     * 1 买跌:
                     * 如果当前价格 <= 开仓价-最小波动点，则盈利
                     * 如果当前价格 >= 开仓价+最小波动点，则亏损
                     */
                    if (close.compareTo(subtract) <= 0) {
                        settle(order, "profit", close);
                    }
                    if (close.compareTo(add) >= 0) {
                        settle(order, "loss", close);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("ContractOrderCalculatio run fail", e);
        }

    }

    /**
     * 检查订单的最小波动点(pips)是否有效
     * 
     * @param order 合约订单
     * @return true 如果 pips 有效(非空且非零), false 否则
     */
    private boolean isValidPips(ContractOrder order) {
        if (order.getPips() == null || order.getPips().compareTo(BigDecimal.ZERO) == 0) {
            log.error("Invalid pips: null or zero, orderNo={}, symbol={}",
                      order.getOrderNo(), order.getSymbol());
            return false;
        }
        return true;
    }

    /**
     * 计算订单的全部盈亏(从开仓到现在)
     * 
     * 计算公式:
     * 偏差点数 = |当前价格 - 开仓价| / 最小波动点
     * 盈亏金额 = 点值 × 偏差点数 × 手数
     * 
     * @param order 合约订单
     * @return 盈亏金额,正数表示盈利,负数表示亏损
     */
    @Override
    public BigDecimal calculateAllProfit(ContractOrder order) {
        List<Realtime> list = this.dataService.realtime(order.getSymbol());
        if (list.size() == 0) {
            log.error("{} 没有获取到实时价格", order.getSymbol());
            return BigDecimal.ZERO;
        }
        
        // 防御性检查：pips不能为零
        if (!isValidPips(order)) {
            return BigDecimal.ZERO;
        }
        
        Realtime realtime = list.get(0);
        BigDecimal close = BigDecimal.valueOf(realtime.getClose());
        
        // 计算偏差点数: |当前价格 - 开仓价| / 最小波动点
        BigDecimal point = close.subtract(order.getTradeAvgPrice()).abs().divide(order.getPips(), 10, RoundingMode.HALF_UP);
        
        // 根据偏差点数和手数算出盈亏金额: 点值 × 偏差点数 × 手数
        BigDecimal amount = order.getPipsAmount().multiply(point).multiply(order.getVolume());
        
        // 根据买卖方向确定盈亏正负
        if (order.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_BUY)) {
            return amount;  // 买涨:价格上涨盈利
        } else {
            return amount.negate();  // 买跌:价格下跌盈利(取反)
        }

    }

    /**
     * 计算今日盈亏
     * 
     * 如果订单是今天开仓的,从开仓价计算
     * 如果订单是之前开仓的,从今日开盘价计算
     * 
     * @param order 合约订单
     * @param zoneId 时区
     * @return 今日盈亏金额
     */
    @Override
    public BigDecimal calculateTodayProfit(ContractOrder order, ZoneId zoneId) {
        List<Realtime> list = this.dataService.realtime(order.getSymbol());
        if (list.size() == 0) {
            log.error("{} 没有获取到实时价格", order.getSymbol());
            return BigDecimal.ZERO;
        }
        
        // 防御性检查：pips不能为零
        if (!isValidPips(order)) {
            return BigDecimal.ZERO;
        }
        
        Realtime realtime = list.get(0);
        BigDecimal close = BigDecimal.valueOf(realtime.getClose());
        
        // 偏差点位
        BigDecimal point;
        if (isTimestampFromToday(order.getCreateTimeTs(), zoneId)) {
            // 今天开仓的订单:从开仓价计算
            point = close.subtract(order.getTradeAvgPrice()).abs().divide(order.getPips(), 10, RoundingMode.HALF_UP);
        } else {
            // 之前开仓的订单:从今日开盘价计算
            point = close.subtract(BigDecimal.valueOf(realtime.getOpen())).abs().divide(order.getPips(), 10, RoundingMode.HALF_UP);
        }
        
        /*
         * 根据偏差点数和手数算出盈亏金额
         */
        BigDecimal amount = order.getPipsAmount().multiply(point).multiply(order.getVolume());
        if (order.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_BUY)) {
            return amount;
        } else {
            return amount.negate();
        }

    }

    /**
     * 判断时间戳是否是今天
     * 
     * @param timestamp 时间戳(秒)
     * @param zoneId 时区
     * @return true表示是今天
     */
    public static boolean isTimestampFromToday(long timestamp, ZoneId zoneId) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDate timestampDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return timestampDate.isEqual(today);
    }
    

    /**
     * 订单盈亏结算
     * 
     * 这是订单结算的核心方法,主要功能:
     * 1. 计算盈亏金额
     * 2. 更新订单盈亏和平仓价
     * 3. 检查止盈止损是否触发
     * 4. 检查强制平仓条件
     * 
     * @param order 合约订单
     * @param profit_loss 盈亏类型: "profit"盈利, "loss"亏损
     * @param currentPrice 当前价格
     */
    public void settle(ContractOrder order, String profit_loss, BigDecimal currentPrice) {

        /**
         * 计算偏差点数: |当前价格 - 开仓价| / 最小波动点
         */
        // 防御性检查：pips不能为零，否则会导致除零异常
        if (order.getPips() == null || order.getPips().compareTo(BigDecimal.ZERO) == 0) {
            log.error("settle failed: pips is zero or null, orderNo={}, symbol={}",
                      order.getOrderNo(), order.getSymbol());
            return;
        }
        
        BigDecimal point = currentPrice.subtract(order.getTradeAvgPrice()).abs().divide(order.getPips(), 10, RoundingMode.HALF_UP);
        
        /*
         * 根据偏差点数和手数算出盈亏金额: 点值 × 偏差点数 × 手数
         */
        BigDecimal amount = order.getPipsAmount().multiply(point).multiply(order.getVolume());
        
        // 检查是否是标准合约模式
        Syspara syspara = sysparaService.find("u_standard_contract");
        if (ObjectUtils.isNotEmpty(syspara)) {
            if ("1".equals(syspara.getSvalue())) {
                // 标准合约模式:考虑杠杆率
                if (ObjectUtils.isNotEmpty(order.getLeverRate())) {
                    BigDecimal pipsAmount = order.getPipsAmount().multiply(point);
                    BigDecimal volume = order.getVolume().divide(order.getLeverRate(),10, RoundingMode.HALF_UP);
                    amount = pipsAmount.multiply(volume);
                }
                amount = order.getPips().multiply(point).multiply(order.getVolume());
            }
        }

        // 设置盈亏金额
        if ("profit".equals(profit_loss)) {
            /**
             * 盈利:正数
             */
            order.setProfit(amount);
        } else if ("loss".equals(profit_loss)) {
            /**
             * 亏损:负数
             */
            order.setProfit(amount.negate());
        }
        
        /**
         * 多次平仓价格不对,后续修复
         */
        order.setCloseAvgPrice(currentPrice);
        this.contractOrderService.updateByIdBuffer(order);

        // ========== 止盈检查 ==========
        /**
         * 止盈价
         */
        BigDecimal profitStop = order.getStopPriceProfit();
        if (profitStop != null && profitStop.compareTo(BigDecimal.ZERO) > 0 && ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
            /*
             * 买涨:当前价格 >= 止盈价,触发止盈
             */
            if (currentPrice.compareTo(profitStop) >= 0) {
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());
                return;
            }
        } else if (profitStop != null && profitStop.compareTo(BigDecimal.ZERO) > 0
                && ContractOrder.DIRECTION_SELL.equals(order.getDirection())) {
            /**
             * 买跌:当前价格 <= 止盈价,触发止盈
             */
            if (currentPrice.compareTo(profitStop) <= 0) {
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());
                return;
            }
        }

        // ========== 止损检查 ==========
        /**
         * 止亏线
         */
        BigDecimal loss_stop = order.getStopPriceLoss();

        if (loss_stop != null && loss_stop.compareTo(BigDecimal.ZERO) > 0 && ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
            /*
             * 买涨:当前价格 <= 止损价,触发止损
             */
            if (currentPrice.compareTo(loss_stop) <= 0) {
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());
                return;

            }
        } else if (loss_stop != null && loss_stop.compareTo(BigDecimal.ZERO) > 0 && ContractOrder.DIRECTION_SELL.equals(order.getDirection())) {
            /**
             * 买跌:当前价格 >= 止损价,触发止损
             */

            if (currentPrice.compareTo(loss_stop) >= 0) {
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());
                return;
            }
        }

        // ========== 强制平仓检查 ==========
        BigDecimal profit1 = contractOrderService.getCacheProfit(order.getUuid()).getProfit();
        
        if (order_close_line_type == 1) {
            /**
             * 全仓模式:计算所有持仓的总盈亏
             */
            BigDecimal profit = BigDecimal.ZERO;

            Wallet wallet = this.walletService.findByUserId(order.getPartyId().toString());
            
            // 计算所有除自己以外的profit
            BigDecimal profitExptThis = profit.subtract(profit1).subtract(order.getDeposit());
            
            /**
             * 全仓强平公式:
             * profitAll + wallet <= 0
             * 即:总盈亏 + 钱包余额 <= 0 时触发强平
             * 
             * 推导过程:
             * p1 + Σ(p2~pn) + deposit <= wallet - Σ(p2~pn)
             * (currentPrice - tradeAvg) * pipAmount * volume / pips + deposit <= wallet - Σ(p2~pn)
             */
            BigDecimal left = wallet.getMoney().negate().subtract(profitExptThis).subtract(order.getDeposit());
            BigDecimal overLine = (left.multiply(order.getPips()).divide(order.getPipsAmount(), 10, RoundingMode.HALF_UP)
                    .divide(order.getVolume(), 10, RoundingMode.HALF_UP));
            Integer decimal = itemService.getDecimal(order.getSymbol());
            BigDecimal forceClose = BigDecimal.ZERO;
            
            // 计算强制平仓价格
            // 买多:从买价跌多少
            if (order.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_BUY)) {
                forceClose = order.getTradeAvgPrice().add(overLine).setScale(decimal, RoundingMode.HALF_UP);
            } 
            // 买跌:涨到多少
            else {
                forceClose = order.getTradeAvgPrice().subtract(overLine).setScale(decimal, RoundingMode.HALF_UP);
            }
            
            // 强制平仓价不能小于0
            if (forceClose.compareTo(BigDecimal.ZERO) < 0) {
                forceClose = BigDecimal.ZERO;
            }
            
            // 更新订单的强制平仓价
            order.setForceClosePrice(forceClose.toPlainString());
            this.contractOrderService.updateByIdBuffer(order);
            
            // 重新计算用户所有持仓的盈亏
            List<ContractOrder> list = contractOrderService.findSubmitted(order.getPartyId(), null, null, null, null, null);            
            for(ContractOrder contractOrder :list) {
                if(ContractOrder.STATE_SUBMITTED.equals(contractOrder.getState())){
                    contractOrderService.wrapProfit(contractOrder);
                }
            }
            
            // 累加所有持仓的盈亏+订金
            for (int i = 0; i < list.size(); i++) {
                ContractOrder close_line = list.get(i);            
                profit = profit.add(close_line.getProfit().add(close_line.getDeposit()));
            }

            // 检查是否触发全仓强平:总盈亏+钱包余额 <= 0
            if (profit.add(wallet.getMoney()).compareTo(BigDecimal.ZERO) <= 0) {
                /**
                 * 触发全仓强平
                 */
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());

            }
        } else {
            // ========== 单个持仓强平模式 ==========
            BigDecimal divide = order.getDeposit().divide(profit1.abs(), 10, RoundingMode.HALF_UP);
            
            // 检查:亏损时 且 订金/亏损 <= 平仓线比例
            if (profit1.compareTo(BigDecimal.ZERO) < 0 && divide.compareTo(order_close_line.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP)) <= 0) {
                /**
                 * 低于系统默认平仓线，进行强平
                 */
                this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrderNo());
                return;
            }
        }
    }
    
    /**
     * 测试方法:验证强平逻辑
     */
    public static void main(String[] args) { 
        BigDecimal profit = new BigDecimal("-241000");
        Wallet wallet = new Wallet();
        wallet.setMoney(new BigDecimal("240000"));
        if (profit.add(wallet.getMoney()).compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("进来了");
        }else {
            System.out.println("没进来");
        }
        
    }

    /**
     * 设置平仓线比例
     * 
     * @param order_close_line 平仓线比例
     */
    @Override
    public void setOrder_close_line(BigDecimal order_close_line) {
        this.order_close_line = order_close_line;
    }

    /**
     * 设置平仓方式
     * 
     * @param order_close_line_type 平仓方式:1=全仓,2=单个持仓
     */
    @Override
    public void setOrder_close_line_type(int order_close_line_type) {
        this.order_close_line_type = order_close_line_type;
    }

}
