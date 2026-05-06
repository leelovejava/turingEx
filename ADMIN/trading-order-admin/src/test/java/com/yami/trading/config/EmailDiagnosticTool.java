package com.yami.trading.config;

import com.yami.trading.common.manager.email.EmailMessage;
import com.yami.trading.common.manager.email.EmailMessageQueue;
import com.yami.trading.service.EmailSendService;
import com.yami.trading.service.InternalEmailSenderService;
import com.yami.trading.service.impl.EmailSendServiceImpl;
import com.yami.trading.service.impl.InternalEmailSenderServiceImpl;
import com.yami.trading.common.util.ApplicationUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 邮箱发送功能诊断工具
 *
 * 这是一个不依赖Spring的简单测试，用于快速定位邮件发送失败的原因
 */
public class EmailDiagnosticTool {

    private static final Logger logger = LoggerFactory.getLogger(EmailDiagnosticTool.class);

    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;
    private String emailFrom;

    public EmailDiagnosticTool() {
        // 读取邮件配置
        loadEmailConfig();
    }

    /**
     * 加载邮件配置
     */
    private void loadEmailConfig() {
        logger.info("========== 加载邮件配置 ==========");

        try {
            // 使用System.getProperty模拟配置读取
            // 你可以直接在这里配置，或者通过环境变量设置
            emailHost = System.getProperty("email.host", "smtp.share-email.com");
            emailPort = System.getProperty("email.port", "587");
            emailUsername = System.getProperty("email.username", "support@helixcapital.net");
            emailPassword = System.getProperty("email.password", "6Q$NG6Ik$gJaiLzj");
            emailFrom = System.getProperty("email.from", "smtp.share-email.com");

            logger.info("email.host: {}", emailHost);
            logger.info("email.port: {}", emailPort);
            logger.info("email.username: {}", emailUsername);
            logger.info("email.password: {}", maskPassword(emailPassword));
            logger.info("email.from: {}", emailFrom);

            // 校验配置
            validateConfig();

        } catch (Exception e) {
            logger.error("加载配置失败: ", e);
        }
    }

    /**
     * 隐藏密码敏感信息
     */
    private String maskPassword(String password) {
        if (password == null || password.length() < 3) {
            return "******";
        }
        return password.charAt(0) + "******" + password.charAt(password.length() - 1);
    }

    /**
     * 校验配置
     */
    private boolean validateConfig() {
        logger.info("========== 配置校验 ==========");

        boolean valid = true;

        if (emailHost == null || emailHost.isBlank()) {
            logger.error("❌ email.host 未配置");
            valid = false;
        } else {
            logger.info("✓ email.host 已配置: {}", emailHost);
        }

        if (emailPort == null || emailPort.isBlank()) {
            logger.error("❌ email.port 未配置");
            valid = false;
        } else {
            logger.info("✓ email.port 已配置: {}", emailPort);
        }

        if (emailUsername == null || emailUsername.isBlank()) {
            logger.error("❌ email.username 未配置");
            valid = false;
        } else {
            logger.info("✓ email.username 已配置");
        }

        if (emailPassword == null || emailPassword.isBlank()) {
            logger.error("❌ email.password 未配置");
            valid = false;
        } else {
            logger.info("✓ email.password 已配置");
        }

        if (emailFrom == null || emailFrom.isBlank()) {
            logger.error("❌ email.from 未配置");
            valid = false;
        } else {
            logger.info("✓ email.from 已配置");
        }

        return valid;
    }

    /**
     * 测试1: 检查JavaMail是否可用
     */
    @Test
    public void testJavaMailAvailable() {
        logger.info("========== 测试1: 检查JavaMail是否可用 ==========");
        try {
            Class.forName("javax.mail.Session");
            Class.forName("javax.mail.Transport");
            Class.forName("javax.mail.internet.MimeMessage");
            logger.info("✓ JavaMail API 可用");
        } catch (ClassNotFoundException e) {
            logger.error("❌ JavaMail API 未找到，缺少依赖: ", e);
            fail("JavaMail API 缺失");
        }
    }

    /**
     * 测试2: 测试SMTP连接（使用Java原生代码）
     */
    @Test
    public void testSmtpConnection() {
        logger.info("========== 测试2: 测试SMTP连接 ==========");

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", emailHost);
            props.put("mail.smtp.port", emailPort);
            props.put("mail.smtp.auth", "true");

            if ("587".equals(emailPort)) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.starttls.protocols", "TLSv1.2");
                logger.info("使用 STARTTLS (端口 587)");
            } else if ("465".equals(emailPort)) {
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.port", emailPort);
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                logger.info("使用 SSL (端口 465)");
            } else {
                logger.info("使用标准连接 (端口 {})", emailPort);
            }

            logger.info("正在连接到 SMTP 服务器: {}:{}", emailHost, emailPort);

            javax.mail.Session session = javax.mail.Session.getInstance(props);
            session.setDebug(true);

            javax.mail.Transport transport = session.getTransport("smtp");
            transport.connect(emailHost, Integer.parseInt(emailPort), emailUsername, emailPassword);

            logger.info("✓ SMTP 连接成功！");
            transport.close();
            logger.info("✓ 连接已关闭");

        } catch (Exception e) {
            logger.error("❌ SMTP 连接失败: ", e);
            analyzeSmtpError(e);
            fail("SMTP 连接失败");
        }
    }

    /**
     * 测试3: 使用 InternalEmailSenderServiceImpl 发送邮件
     */
    @Test
    public void testInternalEmailSender() {
        logger.info("========== 测试3: 使用 InternalEmailSenderServiceImpl 发送 ==========");

        try {
            InternalEmailSenderServiceImpl emailSender = new InternalEmailSenderServiceImpl();

            // 设置配置
            // Note: 由于 InternalEmailSenderServiceImpl 使用 ApplicationUtil 读取配置，
            // 我们需要先确认配置是否正确加载

            // 创建测试邮件
            EmailMessage message = new EmailMessage(
                "test@example.com",
                "【诊断测试】邮件发送测试 " + System.currentTimeMillis(),
                "这是一封诊断测试邮件，如果收到说明邮件发送功能正常！",
                null,
                null,
                null,
                null
            );

            logger.info("正在发送邮件...");
            logger.info("收件人: {}", message.getTomail());
            logger.info("主题: {}", message.getSubject());
            logger.info("内容: {}", message.getContent());

            // 发送邮件
            emailSender.send(message);

            logger.info("✓ 邮件发送调用完成（请检查是否实际发送成功）");

        } catch (Exception e) {
            logger.error("❌ 邮件发送失败: ", e);
            analyzeSmtpError(e);
            fail("邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 测试4: 使用 EmailSendService（队列方式）
     */
    @Test
    public void testEmailSendService() {
        logger.info("========== 测试4: 使用 EmailSendService（队列方式） ==========");

        try {
            EmailSendService emailService = new EmailSendServiceImpl();

            logger.info("队列操作前大小: {}", EmailMessageQueue.size());

            String testEmail = "queue_test@example.com";
            String subject = "【队列测试】 " + System.currentTimeMillis();
            String content = "测试队列发送功能";

            emailService.sendEmail(testEmail, subject, content);

            int queueSize = EmailMessageQueue.size();
            logger.info("队列操作后大小: {}", queueSize);

            if (queueSize > 0) {
                logger.info("✓ 邮件已加入队列");
            } else {
                logger.error("❌ 邮件未加入队列");
            }

            // 检查队列中的邮件
            EmailMessage queuedMessage = EmailMessageQueue.poll();
            if (queuedMessage != null) {
                logger.info("✓ 从队列中取出邮件:");
                logger.info("  - 收件人: {}", queuedMessage.getTomail());
                logger.info("  - 主题: {}", queuedMessage.getSubject());
                logger.info("  - 内容: {}", queuedMessage.getContent());
            }

        } catch (Exception e) {
            logger.error("❌ EmailSendService 测试失败: ", e);
            fail("测试失败: " + e.getMessage());
        }
    }

    /**
     * 分析SMTP错误
     */
    private void analyzeSmtpError(Exception e) {
        String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        String className = e.getClass().getName();

        logger.error("");
        logger.error("========== 错误分析 ==========");
        logger.error("异常类型: {}", className);
        logger.error("错误信息: {}", e.getMessage());

        logger.error("");
        logger.error("【可能原因及解决方案】");

        if (className.contains("AuthenticationFailedException") || errorMsg.contains("authentication")) {
            logger.error("1. 认证失败");
            logger.error("   - 检查 email.username 是否正确");
            logger.error("   - 检查 email.password 是否正确");
            logger.error("   - 某些邮箱需要使用「应用专用密码」而不是普通密码");
            logger.error("   - 检查邮箱是否开启了SMTP功能");
        } else if (errorMsg.contains("connection refused") || errorMsg.contains("connect")) {
            logger.error("2. 连接被拒绝");
            logger.error("   - 检查 email.host 是否正确（当前: " + emailHost + "）");
            logger.error("   - 检查 email.port 是否正确（当前: " + emailPort + "）");
            logger.error("   - 检查防火墙是否阻止了SMTP端口");
            logger.error("   - 尝试使用 ping " + emailHost + " 检查网络连接");
        } else if (errorMsg.contains("timeout")) {
            logger.error("3. 连接超时");
            logger.error("   - 网络连接不稳定");
            logger.error("   - SMTP服务器响应慢或不可用");
            logger.error("   - 检查端口是否被防火墙阻止");
        } else if (errorMsg.contains("ssl") || errorMsg.contains("tls") || errorMsg.contains("starttls")) {
            logger.error("4. SSL/TLS加密问题");
            logger.error("   - 检查端口与加密方式是否匹配:");
            logger.error("     - 端口 587 应该使用 STARTTLS");
            logger.error("     - 端口 465 应该使用 SSL");
            logger.error("   - 尝试在 587 和 465 之间切换");
            logger.error("   - 检查 Java 版本是否支持 TLS 1.2+");
        } else if (errorMsg.contains("certificate") || errorMsg.contains("untrusted")) {
            logger.error("5. 证书问题");
            logger.error("   - SMTP服务器证书不可信");
            logger.error("   - 可能需要添加信任证书到 Java keystore");
        } else {
            logger.error("6. 其他错误");
            logger.error("   - 请查看完整异常堆栈");
        }

        logger.error("");
        logger.error("【配置检查清单】");
        logger.error("  [ ] email.host 配置正确");
        logger.error("  [ ] email.port 配置正确（587/465）");
        logger.error("  [ ] email.username 配置正确");
        logger.error("  [ ] email.password 配置正确");
        logger.error("  [ ] 邮箱账号已开启 SMTP 功能");
        logger.error("  [ ] 防火墙未阻止 SMTP 端口");
        logger.error("  [ ] 网络连接正常");
    }

    /**
     * 运行完整诊断
     */
    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("       邮箱发送功能诊断工具");
        logger.info("========================================");
        logger.info("");

        // 可以在这里直接设置配置（可选）
        // System.setProperty("email.host", "smtp.example.com");
        // System.setProperty("email.port", "587");
        // System.setProperty("email.username", "your@email.com");
        // System.setProperty("email.password", "yourpassword");

        EmailDiagnosticTool tool = new EmailDiagnosticTool();

        // 运行测试
        try {
            tool.testJavaMailAvailable();
            tool.testSmtpConnection();
            tool.testInternalEmailSender();
            tool.testEmailSendService();

            logger.info("");
            logger.info("========================================");
            logger.info("       所有诊断测试完成！");
            logger.info("========================================");
        } catch (Exception e) {
            logger.error("诊断过程中发生异常: ", e);
        }
    }
}
