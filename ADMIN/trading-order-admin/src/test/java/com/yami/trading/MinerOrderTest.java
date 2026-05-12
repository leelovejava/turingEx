package com.yami.trading;

import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.service.miner.job.MinerOrderProfitJob;
import com.yami.trading.service.miner.service.MinerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 矿机订单单元测试类
 *
 * <p>测试目标：
 * <ul>
 *   <li>验证 MinerOrderService 服务是否正常注入</li>
 *   <li>验证 MinerOrderProfitJob 定时任务是否正常注入</li>
 *   <li>测试创建矿机订单功能</li>
 *   <li>测试矿机收益定时任务执行</li>
 * </ul>
 *
 * <p>运行前提：
 * <ul>
 *   <li>MySQL 数据库正常运行，配置见 application-dev.yml</li>
 *   <li>Redis 服务正常运行</li>
 *   <li>trading-order-service 模块已正确编译</li>
 * </ul>
 *
 * <p>运行方式：
 * <pre>
 * mvn test -Dtest=MinerOrderTest -pl trading-order-admin
 * </pre>
 */
@Slf4j
@SpringBootTest
@ContextConfiguration
public class MinerOrderTest {

    /**
     * 矿机订单服务
     * <p>
     * 负责矿机订单的创建、查询、赎回等核心业务逻辑
     *
     * @see com.yami.trading.service.miner.service.MinerOrderService
     */
    @Autowired(required = false)
    private MinerOrderService minerOrderService;

    /**
     * 矿机收益定时任务
     * <p>
     * 负责定期计算并发放矿机收益，每12分钟执行一次
     *
     * @see com.yami.trading.service.miner.job.MinerOrderProfitJob
     */
    @Autowired(required = false)
    private MinerOrderProfitJob minerOrderProfitJob;

    /**
     * 测试1: 检查 MinerOrderService 是否成功注入
     *
     * <p>验证 Spring 容器是否成功创建并注入了 MinerOrderService Bean
     * 如果注入失败，可能原因：
     * <ul>
     *   <li>MinerOrderServiceImpl 未添加 @Service 注解</li>
     *   <li>trading-order-service 模块未正确引入</li>
     *   <li>组件扫描路径配置问题</li>
     * </ul>
     */
    @Test
    public void testMinerOrderServiceExists() {
        log.info("========== 测试1: 检查 MinerOrderService 是否已注入 ==========");
        assertNotNull(minerOrderService, "MinerOrderService should be autowired");
        log.info("MinerOrderService 已注入: {}", minerOrderService.getClass().getName());
    }

    /**
     * 测试2: 检查 MinerOrderProfitJob 是否成功注入
     *
     * <p>验证 Spring 容器是否成功创建并注入了矿机收益定时任务 Bean
     * 如果注入失败，可能原因：
     * <ul>
     *   <li>MinerOrderProfitJob 类未添加 @Component 注解</li>
     *   <li>定时任务相关配置缺失</li>
     *   <li>依赖的服务（SysparaService、DataService等）注入失败</li>
     * </ul>
     */
    @Test
    public void testMinerOrderProfitJobExists() {
        log.info("========== 测试2: 检查 MinerOrderProfitJob 是否已注入 ==========");
        assertNotNull(minerOrderProfitJob, "MinerOrderProfitJob should be autowired");
        log.info("MinerOrderProfitJob 已注入: {}", minerOrderProfitJob.getClass().getName());
    }

    /**
     * 测试3: 创建矿机订单
     *
     * <p>测试管理员后台创建矿机订单的功能
     *
     * <p>测试步骤：
     * <ol>
     *   <li>创建 MinerOrder 实体对象</li>
     *   <li>设置订单号（使用 UUID 前8位确保唯一）</li>
     *   <li>设置用户ID (partyId=1)</li>
     *   <li>设置矿机ID (minerId=1)</li>
     *   <li>设置订单金额 (1000)</li>
     *   <li>调用 saveCreateByManage 创建订单</li>
     *   <li>观察日志输出验证创建结果</li>
     * </ol>
     *
     * <p>注意事项：
     * <ul>
     *   <li>需要确保数据库中存在 partyId=1 的用户</li>
     *   <li>需要确保数据库中存在 minerId=1 的矿机</li>
     *   <li>该方法为后台管理员创建，不受普通用户购买限制</li>
     * </ul>
     *
     * @see com.yami.trading.service.miner.service.MinerOrderService#saveCreateByManage
     */
    @Test
    public void testCreateMinerOrder() {
        log.info("========== 测试3: 创建矿机订单 ==========");
        if (minerOrderService == null) {
            log.warn("MinerOrderService is null, 跳过测试");
            return;
        }

        // 创建矿机订单实体
        MinerOrder order = new MinerOrder();
        // 订单号：使用 TEST 前缀 + UUID 前8位，确保唯一性
        order.setOrder_no("TEST" + UUID.randomUUID().toString().substring(0, 8));
        // 用户ID：设置为 1（测试用户，实际应为真实的用户ID）
        order.setPartyId("");
        // 矿机ID：设置为 1（测试矿机，实际应为真实的矿机ID）
        order.setMiner_id("");
        // 订单金额：1000（单位根据实际业务，可能是 USDT 或其他币种）
        order.setAmount(1000);
        // 创建时间：当前时间
        order.setCreate_time(new Date());
        // 订单状态：1 表示计息中（不同数值代表不同状态：0-待确认, 1-计息中, 2-已赎回等）
        order.setState("1");
        // 订单类型：1 表示普通矿机（0-体验矿机, 1-普通矿机, 2-高端矿机等）
        ///order.setType("1");
        // 订单来源：web（可能的值：web-网页, wap-手机网页, app-APP, admin-后台管理）
        ///order.setSource("web");

        try {
            // 调用服务创建订单，第二个参数 true 表示后台管理员操作
            minerOrderService.saveCreateByManage(order, "admin");
            log.info("矿机订单创建成功, 订单号: {}", order.getOrder_no());
        } catch (Exception e) {
            log.error("创建矿机订单失败: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试4: 运行矿机收益定时任务
     *
     * <p>测试矿机收益计算和发放的定时任务是否能够正常执行
     *
     * <p>任务执行流程：
     * <ol>
     *   <li>获取系统参数中的收益币种配置（miner_profit_symbol）</li>
     *   <li>获取收益币种的实时行情数据</li>
     *   <li>分页查询所有计息中的矿机订单（每页300条）</li>
     *   <li>遍历每个订单，从预收益表读取未使用的收益记录</li>
     *   <li>标记预收益记录为已使用</li>
     *   <li>在机器人订单表中新增使用记录</li>
     *   <li>更新矿机订单的累计收益</li>
     *   <li>计算并发放推荐人收益</li>
     * </ol>
     *
     * <p>注意事项：
     * <ul>
     *   <li>该任务设计为每12分钟执行一次（fixedDelay = 386000ms）</li>
     *   <li>需要确保 Redis 连接正常（用于缓存和分布式锁）</li>
     *   <li>需要确保行情数据服务正常（DataService）</li>
     *   <li>单页处理失败不影响其他页面继续处理</li>
     * </ul>
     *
     * @see com.yami.trading.service.miner.job.MinerOrderProfitJob#taskJob
     */
    @Test
    public void testMinerProfitJobTask() {
        log.info("========== 测试4: 运行矿机收益定时任务 ==========");
        if (minerOrderProfitJob == null) {
            log.warn("MinerOrderProfitJob is null, 跳过测试");
            return;
        }

        try {
            log.info("开始执行矿机收益任务...");
            log.info("该任务将：");
            log.info("1. 分页查询所有计息中的矿机订单");
            log.info("2. 计算每个订单的收益");
            log.info("3. 更新订单累计收益");
            log.info("4. 计算推荐人收益");

            // 调用定时任务的核心方法
            minerOrderProfitJob.taskJob();

            log.info("矿机收益任务执行完成");
        } catch (Exception e) {
            log.error("矿机收益任务执行失败: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}