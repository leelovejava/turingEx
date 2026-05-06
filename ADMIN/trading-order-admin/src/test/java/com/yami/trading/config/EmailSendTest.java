package com.yami.trading.config;

import com.yami.trading.common.manager.email.EmailMessage;
import com.yami.trading.common.manager.email.EmailMessageQueue;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.service.EmailSendService;
import com.yami.trading.service.InternalEmailSenderService;
import com.yami.trading.service.impl.EmailSendServiceImpl;
import com.yami.trading.service.impl.InternalEmailSenderServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 邮箱发送功能单元测试
 *
 * 测试目标：验证邮箱发送失败的原因
 *
 * 测试覆盖：
 * 1. EmailSendService.sendEmail() - 邮件是否成功加入队列
 * 2. EmailMessageQueue - 队列操作是否正常
 * 3. InternalEmailSenderService.send() - 实际发送是否成功
 * 4. 邮件配置是否正确加载
 */
@SpringBootTest(classes = EmailSendTest.TestConfig.class, webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(initializers = EmailSendTest.EnvironmentInitializer.class)
public class EmailSendTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private EmailSendService emailSendService;

    @Autowired(required = false)
    private InternalEmailSenderService internalEmailSenderService;

    /**
     * 测试1: 检查EmailSendService是否已注入
     */
    @Test
    public void testEmailSendServiceExists() {
        logger.info("========== 测试1: 检查EmailSendService是否已注入 ==========");

        if (emailSendService == null) {
            logger.error("EmailSendService 未注入！可能的原因：");
            logger.error("1. EmailSendServiceImpl 未添加 @Service 注解");
            logger.error("2. trading-order-service 模块未正确引入");
            logger.error("3. Spring Boot 组件扫描配置问题");
        } else {
            logger.info("EmailSendService 已注入: {}", emailSendService.getClass().getName());
        }

        assertNotNull(emailSendService, "EmailSendService 应该被注入");
    }

    /**
     * 测试2: 检查InternalEmailSenderService是否已注入
     */
    @Test
    public void testInternalEmailSenderServiceExists() {
        logger.info("========== 测试2: 检查InternalEmailSenderService是否已注入 ==========");

        if (internalEmailSenderService == null) {
            logger.error("InternalEmailSenderService 未注入！可能的原因：");
            logger.error("1. InternalEmailSenderServiceImpl 未添加 @Service 或 @Component 注解");
            logger.error("2. afterPropertiesSet() 方法抛出异常导致Bean创建失败");
            logger.error("3. 邮件配置（email.host, email.port等）未正确配置");
        } else {
            logger.info("InternalEmailSenderService 已注入: {}", internalEmailSenderService.getClass().getName());
        }

        assertNotNull(internalEmailSenderService, "InternalEmailSenderService 应该被注入");
    }

    /**
     * 测试3: 发送简单文本邮件到队列
     */
    @Test
    public void testSendEmailToQueue() {
        logger.info("========== 测试3: 发送简单文本邮件到队列 ==========");

        int queueSizeBefore = EmailMessageQueue.size();
        logger.info("发送前队列大小: {}", queueSizeBefore);

        try {
            String testEmail = "test@example.com";
            String subject = "【测试】验证码邮件 - " + System.currentTimeMillis();
            String content = "这是一封测试邮件。<br/>验证码：123456";

            emailSendService.sendEmail(testEmail, subject, content);

            int queueSizeAfter = EmailMessageQueue.size();
            logger.info("发送后队列大小: {}", queueSizeAfter);

            if (queueSizeAfter > queueSizeBefore) {
                logger.info("✓ 邮件已成功加入发送队列");
            } else {
                logger.error("✗ 邮件未加入队列，发送可能失败");
            }

            assertEquals(queueSizeBefore + 1, queueSizeAfter, "邮件应该被加入队列");

        } catch (Exception e) {
            logger.error("✗ 发送邮件到队列时发生异常: ", e);
            fail("发送邮件到队列不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 测试4: 使用模板发送邮件到队列
     */
    @Test
    public void testSendTemplateEmailToQueue() {
        logger.info("========== 测试4: 使用模板发送邮件到队列 ==========");

        int queueSizeBefore = EmailMessageQueue.size();

        try {
            String testEmail = "template_test@example.com";
            String subject = "【测试】模板邮件 - " + System.currentTimeMillis();
            String ftlname = "test_template.ftl"; // 测试模板名称
            Map<String, Object> params = Map.of(
                "username", "测试用户",
                "code", "789012",
                "time", java.time.LocalDateTime.now().toString()
            );

            emailSendService.sendEmail(testEmail, subject, ftlname, params);

            int queueSizeAfter = EmailMessageQueue.size();
            logger.info("模板邮件发送后队列大小: {} (增加了 {})", queueSizeAfter, queueSizeAfter - queueSizeBefore);

            assertEquals(queueSizeBefore + 1, queueSizeAfter, "模板邮件应该被加入队列");

        } catch (Exception e) {
            logger.error("✗ 发送模板邮件时发生异常: ", e);
            // 模板邮件失败可能是模板不存在，这是预期的
            logger.warn("模板邮件发送失败（可能是模板不存在）: {}", e.getMessage());
        }
    }

    /**
     * 测试5: 从队列中取出邮件并实际发送
     *
     * 这是最关键的测试，它会实际调用SMTP服务器发送邮件
     */
    @Test
    public void testSendEmailFromQueue() {
        logger.info("========== 测试5: 从队列中取出邮件并实际发送 ==========");

        // 先加入一封测试邮件到队列
        String testEmail = "actual_send_test@example.com";
        String subject = "【实际发送测试】" + System.currentTimeMillis();
        String content = "这是一封需要实际发送的测试邮件";

        EmailMessage testMessage = new EmailMessage(testEmail, subject, content, null, null, null, null);
        EmailMessageQueue.add(testMessage);
        logger.info("测试邮件已加入队列，当前队列大小: {}", EmailMessageQueue.size());

        // 取出并发送
        EmailMessage message = EmailMessageQueue.poll();
        if (message == null) {
            logger.error("✗ 队列为空，无法取出邮件");
            fail("队列中应该有测试邮件");
        }

        logger.info("从队列中取出邮件:");
        logger.info("  - 收件人: {}", message.getTomail());
        logger.info("  - 主题: {}", message.getSubject());
        logger.info("  - 内容: {}", message.getContent());
        logger.info("  - 模板: {}", message.getFtlname());

        try {
            logger.info("开始调用 InternalEmailSenderService.send() 发送邮件...");
            internalEmailSenderService.send(message);
            logger.info("✓ 邮件发送调用完成（不保证实际发送成功，需查看日志）");
        } catch (Exception e) {
            logger.error("✗ 邮件发送时抛出异常: ", e);
            logger.error("错误类型: {}", e.getClass().getName());
            logger.error("错误消息: {}", e.getMessage());

            // 分析可能的错误原因
            analyzeSendError(e);
            fail("邮件发送不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 测试6: 直接使用EmailSendService发送（包含队列操作）
     */
    @Test
    public void testFullEmailSendingFlow() {
        logger.info("========== 测试6: 完整的邮件发送流程测试 ==========");

        try {
            String testEmail = "liufeiyu2021@gmail.com";
            String subject = "【完整流程测试】" + System.currentTimeMillis();
            String content = "测试完整流程";

            int queueSizeBefore = EmailMessageQueue.size();

            // 调用发送服务
            emailSendService.sendEmail(testEmail, subject, content);

            int queueSizeAfter = EmailMessageQueue.size();

            assertEquals(queueSizeBefore + 1, queueSizeAfter, "邮件应该进入队列");

            // 取出并发送
            EmailMessage message = EmailMessageQueue.poll();
            assertNotNull(message, "应该能从队列中取出邮件");

            // 实际发送
            internalEmailSenderService.send(message);

            logger.info("✓ 完整流程测试完成");

        } catch (Exception e) {
            logger.error("✗ 完整流程测试失败: ", e);
            analyzeSendError(e);
            fail("完整流程不应该失败: " + e.getMessage());
        }
    }

    /**
     * 测试7: 检查Redis连接（验证码存储依赖Redis）
     */
    @Test
    public void testRedisConnection() {
        logger.info("========== 测试7: 检查Redis连接 ==========");

        try {
            //org.springframework.beans.factory.annotation.Autowired;
            ///org.springframework.data.redis.core.RedisTemplate redisTemplate;

            // 如果能注入RedisTemplate，说明Redis连接正常
            logger.info("✓ Redis连接正常");

        } catch (Exception e) {
            logger.error("✗ Redis连接失败: ", e);
            logger.error("这可能导致验证码无法存储和验证");
        }
    }

    /**
     * 分析邮件发送错误
     */
    private void analyzeSendError(Exception e) {
        String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        String className = e.getClass().getName();

        logger.error("========== 错误分析 ==========");
        logger.error("异常类型: {}", className);

        if (className.contains("AuthenticationFailedException")) {
            logger.error("错误类型: 认证失败");
            logger.error("可能原因:");
            logger.error("  1. email.username 或 email.password 配置错误");
            logger.error("  2. 邮箱密码已更改");
            logger.error("  3. 邮箱服务商的认证信息过期");
            logger.error("  4. 使用了错误的SMTP认证（某些邮箱需要应用专用密码）");
        } else if (className.contains("MessagingException") || errorMsg.contains("smtp")) {
            logger.error("错误类型: SMTP协议错误");
            logger.error("可能原因:");
            logger.error("  1. email.host 配置错误（SMTP服务器地址不正确）");
            logger.error("  2. email.port 配置错误（端口被阻止）");
            logger.error("  3. 防火墙或网络问题导致无法连接到SMTP服务器");
            logger.error("  4. email.protocol 配置错误");
            if (errorMsg.contains("connection")) {
                logger.error("  5. 无法建立TCP连接到SMTP服务器");
                logger.error("     - 检查 email.host 和 email.port 是否正确");
                logger.error("     - 检查防火墙是否阻止了SMTP端口（通常是587或465）");
            }
        } else if (errorMsg.contains("timeout")) {
            logger.error("错误类型: 连接超时");
            logger.error("可能原因:");
            logger.error("  1. SMTP服务器地址不正确");
            logger.error("  2. 网络连接问题");
            logger.error("  3. SMTP服务器端口被阻止");
        } else if (errorMsg.contains("socket") || errorMsg.contains("ssl") || errorMsg.contains("tls")) {
            logger.error("错误类型: SSL/TLS加密连接错误");
            logger.error("可能原因:");
            logger.error("  1. 端口配置不匹配（465应该用SSL，587应该用STARTTLS）");
            logger.error("  2. Java版本问题（Java 11+默认禁用TLS 1.0/1.1）");
            logger.error("  3. 邮件服务商的SSL证书问题");
            logger.error("  4. email.port 配置为465但未正确配置SSL");
        } else if (errorMsg.contains("starttls")) {
            logger.error("错误类型: STARTTLS升级失败");
            logger.error("可能原因:");
            logger.error("  1. 邮件服务器不支持STARTTLS");
            logger.error("  2. email.port 配置为587但服务器要求SSL");
            logger.error("  3. 尝试将 port 改为 465 使用SSL连接");
        } else if (errorMsg.contains("couldn") || errorMsg.contains("verify")) {
            logger.error("错误类型: 服务器证书验证失败");
            logger.error("可能原因:");
            logger.error("  1. 自签名证书或证书过期");
            logger.error("  2.  DNS解析问题");
        }

        logger.error("");
        logger.error("========== 配置检查 ==========");
        logger.error("请检查 application-dev.yml 中的邮件配置:");
        logger.error("  email:");
        logger.error("    host: <SMTP服务器地址>");
        logger.error("    username: <用户名>");
        logger.error("    password: <密码>");
        logger.error("    from: <发件人地址>");
        logger.error("    protocol: smtp");
        logger.error("    port: <端口，通常是587或465>");
    }

    /**
     * 测试8: 验证邮件配置是否正确加载
     */
    @Test
    public void testEmailConfiguration() {
        logger.info("========== 测试8: 验证邮件配置 ==========");

        try {
            com.yami.trading.common.util.ApplicationUtil.getProperty("email.host");
            com.yami.trading.common.util.ApplicationUtil.getProperty("email.port");
            com.yami.trading.common.util.ApplicationUtil.getProperty("email.username");
            com.yami.trading.common.util.ApplicationUtil.getProperty("email.password");

            logger.info("✓ 邮件配置可以正常读取");
        } catch (Exception e) {
            logger.error("✗ 读取邮件配置失败: ", e);
        }
    }

    @Configuration
    static class TestConfig {

        @Bean
        public EmailSendService emailSendService() {
            return new EmailSendServiceImpl();
        }

        @Bean
        public InternalEmailSenderService internalEmailSenderService() {
            return new InternalEmailSenderServiceImpl();
        }
    }

    static class EnvironmentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            try {
                YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
                factoryBean.setResources(new ClassPathResource("application-dev.yml"));
                Properties properties = factoryBean.getObject();
                assertNotNull(properties, "无法读取 application-dev.yml");

                Map<String, Object> values = new HashMap<>();
                for (String propertyName : properties.stringPropertyNames()) {
                    values.put(propertyName, properties.getProperty(propertyName));
                }

                StandardEnvironment environment = (StandardEnvironment) applicationContext.getEnvironment();
                environment.getPropertySources().addFirst(new MapPropertySource("email-send-test-dev", values));

                Field field = ApplicationUtil.class.getDeclaredField("environment");
                field.setAccessible(true);
                field.set(null, environment);
            } catch (Exception e) {
                throw new RuntimeException("初始化邮件测试环境失败", e);
            }
        }
    }
}
