# 交易订单服务 (trading-order-admin)

## 目录

- [合约交易（永续合约）](#合约交易永续合约)
  - [订单结构](#订单结构)
  - [开仓流程](#开仓流程)
  - [平仓流程](#平仓流程)
  - [收益判定逻辑](#收益判定逻辑)
  - [预设结果功能](#预设结果功能)
- [杠杆交易](#杠杆交易)
- [跟单交易](#跟单交易)
- [借贷业务](#借贷业务)
  - [借贷申请单](#借贷申请单)
  - [质押借币](#质押借币)
- [量化/机器人交易](#量化机器人交易)
  - [下单机器人](#下单机器人)
  - [机器人订单执行](#机器人订单执行)

---

## 合约交易（永续合约）

### 订单结构

| 订单类型 | 类名 | 说明 |
|---------|------|------|
| 委托订单 | `ContractApplyOrder` | 用户提交的开仓/平仓申请 |
| 持仓订单 | `ContractOrder` | 成功开仓后的持仓记录 |

**代码位置**：
- 委托订单：`trading-order-bean/src/main/java/com/yami/trading/bean/contract/domain/ContractApplyOrder.java`
- 持仓订单：`trading-order-bean/src/main/java/com/yami/trading/bean/contract/domain/ContractOrder.java`

**关键字段说明**：

| 字段 | 说明 |
|------|------|
| `direction` | 交易方向：`0`=买涨，`1`=买跌 |
| `leverRate` | 杠杆倍数 |
| `volume` | 当前持仓数量 |
| `volumeOpen` | 开仓数量 |
| `tradeAvgPrice` | 开仓成交均价 |
| `pips` | 最小波动点（如 0.01） |
| `pipsAmount` | 最小波动点对应金额（如 1） |
| `stopPriceProfit` | 止盈价格 |
| `stopPriceLoss` | 止损价格 |
| `optionPreResult` | 期权预设结果：`-1`=亏损，`0`=未设置，`1`=盈利 |

---

### 开仓流程

**代码位置**：`trading-order-service/src/main/java/com/yami/trading/service/contract/ContractOrderService.java`

**方法**：`saveOpen(ContractApplyOrder applyOrder, Realtime realtime)` （第 688-821 行）

**流程说明**：

```
用户提交委托订单
       ↓
  校验品种信息
       ↓
  生成订单号（格式：yyMMddHHmmss + 8位随机数）
       ↓
  创建持仓订单，设置各项字段
       ↓
  保存到数据库和Redis缓存
       ↓
  更新用户合约资产（总资产、总保证金、总未实现盈利）
       ↓
  处理跟单逻辑（交易员开仓，通知跟随者）
       ↓
  发送开仓提示消息
```

**关键代码片段**：

```java
// 生成订单号：格式=年月日时分秒(12位)+8位随机数
String orderNo = com.yami.trading.common.util.DateUtil.formatDate(new Date(), "yyMMddHHmmss") + RandomUtil.getRandomNum(8);
order.setOrderNo(orderNo);

// 设置开仓成交均价（使用当前实时价格）
order.setTradeAvgPrice(BigDecimal.valueOf(realtime.getClose()));

// 复制订单级别的期权预设结果（-1=亏损，0=未设置，1=盈利）
order.setOptionPreResult(applyOrder.getOptionPreResult());
```

---

### 平仓流程

**代码位置**：`trading-order-service/src/main/java/com/yami/trading/service/contract/ContractOrderService.java`

**方法**：`saveClose(ContractApplyOrder applyOrder, Realtime realtime, String order_no)` （第 823-890 行）

**流程说明**：

```
用户提交平仓委托
       ↓
  查询持仓订单，校验状态
       ↓
  确定实际平仓数量（不能超过持仓量）
       ↓
  调用 settle() 计算收益
       ↓
  更新钱包余额，记录资金流水
       ↓
  更新订单状态
       ↓
  处理跟单平仓逻辑
       ↓
  删除订单提示
```

---

### 收益结算

**代码位置**：`trading-order-service/src/main/java/com/yami/trading/service/contract/ContractOrderService.java`

**方法**：`settle(ContractOrder order, BigDecimal volume)` （第 615-686 行）

**计算公式**：

```
退还金额 = 当前保证金 + 收益
profit = order.getDeposit() + originProfit
```

**关键代码片段**：

```java
// 从缓存获取该订单的实时收益数据
ContractOrderProfit cacheProfit = getCacheProfit(order.getUuid());

// 计算退还金额：当前保证金 + 收益
BigDecimal profit = order.getDeposit().add(originProfit);

// 更新订单的累计平仓金额
order.setAmountClose(order.getAmountClose().add(profit));

// 如果持仓数量减到0，标记订单为已平仓
if (order.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
    order.setState(ContractOrder.STATE_CREATED);
    order.setCloseTime(DateUtil.currentSeconds());
}
```

---

### 收益判定逻辑

**代码位置**：`trading-order-service/src/main/java/com/yami/trading/service/contract/ContractOrderCalculationServiceImpl.java`

**方法**：`saveCalculation(String order_no)` （第 56-111 行）

**触发时机**：由定时任务 `ContractOrderCalculationJob` 每 **3秒** 执行一次

**判定规则**：

#### 1. 买涨（direction = "buy"）

| 条件 | 结果 |
|------|------|
| 当前价格 ≥ 开仓价 + 最小波动点 | 盈利（平多） |
| 当前价格 ≤ 开仓价 - 最小波动点 | 亏损（爆仓） |

#### 2. 买跌（direction = "sell")

| 条件 | 结果 |
|------|------|
| 当前价格 ≤ 开仓价 - 最小波动点 | 盈利（平空） |
| 当前价格 ≥ 开仓价 + 最小波动点 | 亏损（爆仓） |

**关键代码片段**：

```java
// 计算涨跌触发价格
BigDecimal add = order.getTradeAvgPrice().add(order.getPips());      // 开仓价 + 最小波动点
BigDecimal subtract = order.getTradeAvgPrice().subtract(order.getPips()); // 开仓价 - 最小波动点

if (ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
    // 买涨
    if (close.compareTo(add) >= 0) {
        settle(order, "profit", close);  // 盈利
    }
    if (close.compareTo(subtract) <= 0) {
        settle(order, "loss", close);    // 亏损
    }
} else {
    // 买跌
    if (close.compareTo(subtract) <= 0) {
        settle(order, "profit", close);  // 盈利
    }
    if (close.compareTo(add) >= 0) {
        settle(order, "loss", close);    // 亏损
    }
}
```

---

### 预设结果功能

#### 功能说明

预设结果功能允许通过后台设置，强制指定订单的结算结果，跳过实际价格判定逻辑。

**优先级**：订单级别 > 用户级别

| 级别 | 字段位置 | 说明 |
|------|---------|------|
| 订单级别 | `ContractOrder.optionPreResult` | 针对单个订单的预设 |
| 用户级别 | `User.optionPreResult` | 针对用户的全局预设 |

**optionPreResult 值说明**：

| 值 | 含义 |
|----|------|
| `-1` | 亏损 |
| `0` | 未设置（使用正常价格判定） |
| `1` | 盈利 |

#### 代码修改位置

1. **实体类添加字段**：
   - `ContractOrder`：`trading-order-bean/.../contract/domain/ContractOrder.java`
   - `ContractApplyOrder`：`trading-order-bean/.../contract/domain/ContractApplyOrder.java`
   - `User`：`trading-order-bean/.../model/User.java`

2. **开仓时复制字段**：
   - `ContractOrderService.saveOpen()`：第 711-712 行

   ```java
   // 复制订单级别的期权预设结果（-1=亏损，0=未设置，1=盈利）
   order.setOptionPreResult(applyOrder.getOptionPreResult());
   ```

3. **收益判定逻辑**：
   - `ContractOrderCalculationServiceImpl.saveCalculation()`：第 56-111 行

   ```java
   // 第三步：判断预设结果（优先级：订单级别 > 用户级别）
   Integer preResult = null;

   // 首先检查订单级别是否设置了预设结果
   if (order.getOptionPreResult() != null && order.getOptionPreResult() != 0) {
       preResult = order.getOptionPreResult();
   } else {
       // 如果订单未设置，则检查用户级别是否设置了预设结果
       User user = userService.findByUserId(order.getPartyId());
       if (user != null && user.getOptionPreResult() != null && user.getOptionPreResult() != 0) {
           preResult = user.getOptionPreResult();
       }
   }

   // 第四步：根据预设结果或实际价格进行结算
   if (preResult != null) {
       if (preResult == 1) {
           settle(order, "profit", close);
       } else if (preResult == -1) {
           settle(order, "loss", close);
       }
   } else {
       // 没有设置预设结果，使用原始的涨跌判断逻辑
       ...
   }
   ```

---

## 杠杆交易

### 订单结构

**类名**：`ExchangeLeverOrder`

**代码位置**：`trading-order-bean/src/main/java/com/yami/trading/bean/exchangelever/ExchangeLeverOrder.java`

**关键字段**：

| 字段 | 说明 |
|------|------|
| `direction` | 交易方向：`buy`=买涨（做多），`sell`=买跌（做空） |
| `volumeOpen` | 开仓数量 |
| `tradeAvgPrice` | 开仓均价 |

### 收益判定

**代码位置**：`trading-order-service/src/main/java/com/yami/trading/service/impl/ExchangeLeverProfitServiceImpl.java`

**方法**：`settle(ExchangeLeverOrder order, double amount)` （第 295-310 行）

**计算公式**：

```java
// 买涨盈利 = (当前价格 * 开仓数量) - (开仓均价 * 开仓数量)
profit = (realtime.getClose() * order.getVolumeOpen()) - (order.getTradeAvgPrice() * order.getVolumeOpen());
```

---

## 跟单交易

### 核心类

| 类名 | 说明 |
|------|------|
| `Trader` | 交易员信息 |
| `TraderFollowUser` | 跟随者关系 |
| `TraderFollowUserOrder` | 跟随者订单映射 |

**代码位置**：
- `trading-order-bean/src/main/java/com/yami/trading/bean/trader/domain/`

### 跟单流程

**开仓跟随**：
- 交易员开仓后，系统自动通知所有跟随者
- 代码位置：`TraderFollowUserOrderService.traderOpen()`

**平仓跟随**：
- 交易员平仓后，跟随者订单自动平仓
- 代码位置：`TraderFollowUserOrderService.traderClose()`

---

## 借贷业务

### 借贷申请单

#### 实体类

**类名**：`SimpleLoanOrder`

**代码位置**：`trading-order-bean/src/main/java/com/yami/trading/bean/loan/SimpleLoanOrder.java`

**表名**：`t_simple_loan_order`

**关键字段说明**：

| 字段 | 说明 |
|------|------|
| `partyId` | 用户ID |
| `symbol` | 借贷币种 |
| `quota` | 借贷额度 |
| `state` | 审核状态：`1`=审批中，`2`=待还款，`3`=审批失败，`4`=已逾期，`5`=已还款 |
| `term` | 借贷期限(天) |
| `dailyRate` | 日利率(浮点数，百分比) |
| `repayment` | 还款方式：`1`=到期还本息，`2`=到期还本金，`3`=到期还利息 |
| `lendingInstitution` | 放款机构ID |
| `lendingName` | 放款机构名称 |

#### 借贷参数配置

**类名**：`LoanParam`

**代码位置**：`trading-order-bean/src/main/java/com/yami/trading/bean/loan/LoanParam.java`

**表名**：`t_loan_param`

**关键字段说明**：

| 字段 | 说明 | 示例 |
|------|------|------|
| `term` | 借贷期限(天) | `1,3,7,30,90,180` |
| `max_quota` | 借贷最大额度(按币种) | `USDT:1000,ETH:800,BTC:300` |
| `min_quota` | 借贷最小额度(按币种) | `USDT:100,ETH:80,BTC:30` |
| `repay_cycle` | 还款周期(天) | `3,7,30,60,90,180,360` |
| `daily_rate` | 日利率(百分比) | `0.0003,0.0006,0.001` |
| `repayment` | 还款方式 | `1:到期还本息,2:到期还本金,3:到期还利息` |

#### 服务实现

**接口**：`LoanService`

**实现类**：`LoanServiceImpl`

**代码位置**：
- `trading-order-admin/src/main/java/com/yami/trading/admin/controller/loan/LoanService.java`
- `trading-order-admin/src/main/java/com/yami/trading/admin/controller/loan/internal/LoanServiceImpl.java`

**核心方法**：

| 方法 | 说明 |
|------|------|
| `addLoanOrder()` | 添加借贷申请单 |
| `modLoanOrder()` | 修改借贷申请单 |
| `updateLoanOrderState()` | 修改借贷申请单状态 |
| `pagedQuery()` | 获取借贷申请单列表 |
| `getLoanParamList()` | 获取借贷参数配置列表 |

**状态字典**：

| 状态值 | 含义 |
|--------|------|
| `1` | 未审（审批中） |
| `2` | 待还款 |
| `3` | 驳回（审批失败） |
| `4` | 已逾期 |
| `5` | 已还款 |

---

### 质押借币

#### 核心类

**类名**：`LoanOrder`

**代码位置**：`trading-order-admin/src/main/java/com/yami/trading/admin/controller/loanOrder/`

**关键常量**：`LoanConstants`

**强平率阈值**：`PLEDGE_RATE_CLOSEOUT`（质押率超过此值则触发强平）

#### 强平计算任务

**类名**：`LoanCloseoutJob`

**代码位置**：`trading-order-admin/src/main/java/com/yami/trading/admin/controller/loanOrder/job/LoanCloseoutJob.java`

**执行方式**：独立线程无限循环，每 **30秒** 遍历一次

**强平逻辑**：

```
遍历所有质押借币订单
       ↓
  计算质押率 = 质押物价值 / 债务价值
       ↓
  如果质押率 >= 强平阈值
       ↓
  触发强平，更新订单状态
```

**关键代码片段**：

```java
// 计算质押率
Map<String, Double> rateMap = loanOrderService.calculatePledgeRate(
    order.getPledgeCurrency(),
    order.getDebt_amount(),
    order.getPledge_amount()
);

// 达到强平率
if (rateMap != null && rateMap.get("pledgeRate") >= LoanConstants.PLEDGE_RATE_CLOSEOUT) {
    loanOrderService.updateCloseout(order, rateMap.get("pledgeRate"));
}
```

#### 利息计算任务

**类名**：`LoanInterestJob`

**代码位置**：`trading-order-admin/src/main/java/com/yami/trading/admin/controller/loanOrder/job/LoanInterestJob.java`

**执行频率**：`@Scheduled(fixedDelay = 300000, initialDelay = 5000)` 每 **5分钟** 执行一次

**利息计算逻辑**：

```
分页查询待计算利息的订单（状态为待还款）
       ↓
  每页300条，逐页处理
       ↓
  更新订单利息
```

---

## 量化/机器人交易

### 下单机器人

#### 机器人实体

**类名**：`Robot`

**代码位置**：`trading-order-bean/src/main/java/com/yami/trading/bean/robot/domain/Robot.java`

**表名**：`t_robot`

**关键字段说明**：

| 字段 | 说明 |
|------|------|
| `symbol` | 交易对 |
| `status` | 状态：`1`=正常，`0`=无效 |
| `step` | 价格变化步长 |
| `maxmunInterval` | 最大下单间隔(秒) |
| `minmunInterval` | 最小下单间隔(秒) |
| `maxmunNum` | 最大下单数量 |
| `minmunNum` | 最小下单数量 |
| `money` | 机器人资金 |
| `priceDecimals` | 价格精度 |
| `numDecimals` | 数量精度 |
| `maxumPriceDiff` | 买卖最高差价 |
| `buyNum` | 买单数量 |
| `sellNum` | 卖单数量 |
| `highFrequency` | 高频量比 |
| `lowFrequency` | 低频量比 |
| `weight` | 权重 |

#### 机器人订单

**类名**：`RobotOrder`

**代码位置**：`trading-order-bean/src/main/java/com/yami/trading/bean/robot/domain/RobotOrder.java`

**表名**：`t_robot_order`

**关键字段说明**：

| 字段 | 说明 |
|------|------|
| `uid` | 用户id |
| `symbol` | 交易对 |
| `projectName` | 委托量 |
| `orderQuantity` | 挂单量(USDT) |
| `turnover` | 成交量(USDT) |
| `orderType` | 订单类型：`1`=现价单，`2`=市价单 |
| `direction` | 方向：`1`=买，`2`=卖 |
| `price` | 挂单价格 |
| `status` | 订单状态：`1`=挂单中，`2`=已成交 |

#### 机器人管理

**Controller**：`RobotController`

**代码位置**：`trading-order-admin/src/main/java/com/yami/trading/admin/controller/robot/RobotController.java`

**接口说明**：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/etf/robot/list` | GET | 查询下单机器人列表 |
| `/etf/robot/queryById` | GET | 根据ID获取机器人数据 |
| `/etf/robot/save` | POST | 保存下单机器人 |
| `/etf/robot/delete` | DELETE | 删除下单机器人 |

---

### 机器人订单执行

**定时任务类**：`RobotOrderTask`

**代码位置**：`trading-order-admin/src/main/java/com/yami/trading/admin/task/robot/RobotOrderTask.java`

**执行频率**：每 **20秒** 执行一次（`@Scheduled(cron = "*/20 * * * * ?")`）

**执行逻辑**：

```
查询所有挂单中的订单（status=1）
       ↓
  获取订单对应的实时价格
       ↓
  判断是否触发成交条件：
    - 买单(direction=1)：当前价格 <= 挂单价格 → 成交
    - 卖单(direction=2)：当前价格 >= 挂单价格 → 成交
       ↓
  更新订单状态为已成交，记录成交量
```

**关键代码片段**：

```java
@Scheduled(cron = "*/20 * * * * ?")
public void updateRobotOrder() {
    robotOrderService.query().eq("status", 1).list().forEach(robotOrder -> {
        Realtime realtime = marketService.queryRealtime(robotOrder.getSymbol());
        if (realtime != null && realtime.getTs() != 0) {
            // 买单：价格低于或等于挂单价格时成交
            if (robotOrder.getDirection() == 1 && realtime.getClose() <= robotOrder.getPrice()) {
                robotOrder.setStatus(2);
                robotOrder.setTurnover(robotOrder.getOrderQuantity());
                robotOrderService.updateById(robotOrder);
            }
            // 卖单：价格高于或等于挂单价格时成交
            else if (robotOrder.getDirection() == 2 && realtime.getClose() >= robotOrder.getPrice()) {
                robotOrder.setStatus(2);
                robotOrder.setTurnover(robotOrder.getOrderQuantity());
                robotOrderService.updateById(robotOrder);
            }
        }
    });
}
```

---

## 定时任务

| 任务类 | 执行频率 | 说明 |
|--------|---------|------|
| `ContractApplyOrderHandleJob` | 3秒 | 处理委托订单（开仓/平仓） |
| `ContractOrderCalculationJob` | 3秒 | 收益判定和结算 |
| `LoanCloseoutJob` | 30秒 | 质押借币强平检测 |
| `LoanInterestJob` | 5分钟 | 质押借币利息计算 |
| `RobotOrderTask` | 20秒 | 机器人订单成交检测 |

**代码位置**：`trading-order-huobi/src/main/java/com/yami/trading/huobi/task/contract/`

---

## 系统参数

系统参数表：`t_syspara`

| Key | 说明 | 默认值 |
|-----|------|--------|
| `order_close_line` | 平仓线 | 1.1 |
| `order_close_line_type` | 平仓方式：1=全仓，2=单个持仓 | 1 |

**代码位置**：`InitHandle.java` （第 158-159 行）

```java
// 系统启动时，从系统参数表中读取配置覆盖硬编码预设
contractOrderCalculationService.setOrder_close_line(
    this.sysparaService.find("order_close_line").getBigDecimal());
```
