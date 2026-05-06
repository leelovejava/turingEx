package com.yami.trading.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.yami.trading.common.manager.email.EmailMessage;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.InternalEmailSenderService;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 内部邮件发送服务实现类
 *
 * 本类实现邮件发送功能，支持普通文本邮件和HTML模板邮件，同时支持附件发送。
 *
 * 功能特性：
 * 1. 支持普通文本邮件和FreeMarker模板邮件
 * 2. 支持邮件附件（解决附件名中文乱码问题）
 * 3. 支持HTML格式邮件内容
 * 4. 配置从YAML文件读取，灵活可切换
 *
 * 邮件配置（从application-*.yml读取）：
 * - email.host: SMTP服务器地址
 * - email.port: SMTP端口（默认587）
 * - email.protocol: 邮件协议（默认smtp）
 * - email.username: SMTP认证用户名
 * - email.password: SMTP认证密码
 * - email.from: 发件人邮箱地址
 *
 * SMTP协议说明：
 * - 端口25: 标准SMTP端口（已较少使用，通常被拦截）
 * - 端口465: 使用SSL加密的SMTP（deprecated，但仍广泛使用）
 * - 端口587: 使用STARTTLS加密的SMTP（推荐，更安全）
 *
 * 使用示例：
 * EmailMessage message = new EmailMessage();
 * message.setTomail("receiver@example.com");
 * message.setSubject("测试邮件");
 * message.setContent("这是一封测试邮件");
 * emailSenderService.send(message);
 *
 * 模板邮件示例：
 * EmailMessage message = new EmailMessage();
 * message.setTomail("receiver@example.com");
 * message.setSubject("订单确认");
 * message.setFtlname("order_confirm.ftl");  // 指定模板文件
 * Map<String, Object> params = new HashMap<>();
 * params.put("username", "张三");
 * params.put("orderNo", "ORDER123456");
 * message.setMap(params);
 * emailSenderService.send(message);
 *
 * Spring Boot集成说明：
 * - 本类实现InitializingBean接口，在bean初始化完成后自动配置
 * - 需要确保ApplicationUtil能正确读取email.*配置
 * - 模板文件应放置在classpath:email/ftl/目录下
 */
public class InternalEmailSenderServiceImpl implements InternalEmailSenderService, InitializingBean {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(InternalEmailSenderServiceImpl.class);

    /**
     * JavaMail邮件发送器
     * - 用于创建和发送MimeMessage
     * - 配置包括：SMTP服务器、端口、认证信息等
     */
    private JavaMailSenderImpl mailSender;

    /**
     * 简单邮件消息（用于设置发件人等基本信息）
     * - 虽然实际发送使用MimeMessage，但发件人from从此处获取
     */
    private SimpleMailMessage mailMessage;

    /**
     * FreeMarker模板配置器
     * - 用于解析和渲染FreeMarker模板
     * - 模板文件通常为.ftl格式，放置在classpath:email/ftl/目录下
     * - 支持在模板中使用占位符，如 ${username}、${orderNo}
     */
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 初始化邮件发送器配置
     *
     * 本方法在bean创建后自动调用，负责初始化：
     * 1. JavaMailSenderImpl邮件发送器
     * 2. SimpleMailMessage发件人信息
     * 3. FreeMarkerConfigurer模板配置
     *
     * 配置读取流程：
     * 1. 从ApplicationUtil读取YAML中的email配置
     * 2. 设置SMTP服务器参数
     * 3. 根据端口决定是否启用SSL
     * 4. 初始化FreeMarker模板配置
     *
     * 端口与连接方式：
     * - 端口465: 使用SSL加密连接，需要设置socketFactory
     * - 端口587: 使用STARTTLS加密（STARTTLS是明文连接升级为加密连接）
     * - 其他端口: 标准明文连接（或服务器自定义）
     *
     * STARTTLS vs SSL的区别：
     * - SSL: 建立连接时立即加密（connect -> encrypted）
     * - STARTTLS: 先建立明文连接，然后升级为加密（connect -> STARTTLS -> encrypted）
     *
     * 常见邮件服务商配置参考：
     * | 服务商        | SMTP服务器                   | 端口  | 加密方式 |
     * |---------------|------------------------------|-------|---------|
     * | Mailhostbox  | us3.smtp.mailhostbox.com    | 587   | STARTTLS|
     * | Gmail        | smtp.gmail.com              | 465/587| SSL/STARTTLS|
     * | QQ邮箱        | smtp.qq.com                 | 465   | SSL     |
     * | 163邮箱       | smtp.163.com                | 465   | SSL     |
     */
    @Override
    public void afterPropertiesSet() {
        // ========== 第1步：初始化JavaMailSenderImpl ==========
        // JavaMailSenderImpl是Spring提供的邮件发送实现类
        // 支持简单邮件和MIME邮件的发送
        mailSender = new JavaMailSenderImpl();

        // ========== 第2步：设置SMTP服务器基础信息 ==========
        // 从YAML配置读取SMTP服务器认证信息
        // ApplicationUtil.getProperty()方法用于读取application-*.yml中的配置
        // 配置路径对应email.*，如email.host对应配置文件中的email.host
        mailSender.setUsername(ApplicationUtil.getProperty("email.username"));
        mailSender.setPassword(ApplicationUtil.getProperty("email.password"));
        mailSender.setHost(ApplicationUtil.getProperty("email.host"));

        // ========== 第3步：设置端口和协议 ==========
        // 端口默认587（STARTTLS），这是目前最常用的SMTP端口
        // 如果配置文件中没有设置port，则使用默认值587
        String port = ApplicationUtil.getProperty("email.port", "587");
        mailSender.setPort(Integer.parseInt(port));


        String protocol = ApplicationUtil.getProperty("email.protocol", "ssl");

        // JavaMail协议名称转换：ssl -> smtps
        // JavaMail没有"ssl"协议，需要转换为"smtps"（SSL加密的SMTP）
        String mailProtocol = "ssl".equals(protocol) ? "smtps" : protocol;
        mailSender.setProtocol(mailProtocol);

        // ========== 第4步：配置JavaMail Properties ==========
        // JavaMail使用Properties配置SMTP连接的各种参数
        // 这些参数会影响SMTP连接的建立过程
        Properties javaMailProperties = new Properties();

        // 设置SMTP端口（必须与mailSender.setPort一致）
        // 注意：这里设置的是SMTP命令端口，不是SSL监听端口
        javaMailProperties.setProperty("mail.smtp.port", port);

        // 启用STARTTLS加密
        // STARTTLS是一种将明文SMTP连接升级为加密连接的方式
        // 当服务器支持STARTTLS时，客户端发送STARTTLS命令，服务器响应OK
        // 然后整个连接切换到TLS/SSL加密模式
        // 注意：启用STARTTLS后，端口通常为587
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        
        // ========== Java 11+ TLS协议兼容配置 ==========
        // Java 11+ 默认禁用了 TLS 1.0 和 TLS 1.1
        // 设置安全证书(强制使用 TLS 1.2 解决兼容性问题)
        javaMailProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        javaMailProperties.setProperty("mail.smtp.starttls.protocols", "TLSv1.2");

        // 启用SMTP认证
        // 大多数SMTP服务器要求客户端认证才能发送邮件
        // 认证信息通过username和password提供
        javaMailProperties.setProperty("mail.smtp.auth", "true");

        // 开启debug调试
        ///javaMailProperties.setProperty("mail.debug", "true");

        // 设置SMTP服务器地址（必须与mailSender.setHost一致）
        // JavaMail会根据这个地址建立TCP连接
        javaMailProperties.setProperty("mail.smtp.host", ApplicationUtil.getProperty("email.host"));

        // ========== 第5步：根据端口配置SSL ==========
        // SSL配置说明：
        // - 端口465: 传统SMTP over SSL，连接建立时即使用SSL加密
        // - 端口587: 通常使用STARTTLS，连接建立后升级为加密
        // - 这里我们根据端口判断是否启用SSL socketFactory
        if("ssl".equals(protocol)) {
            // 使用 smtps 协议时，属性前缀是 mail.smtps.*
            javaMailProperties.setProperty("mail.smtps.port", port);
            javaMailProperties.setProperty("mail.smtps.host", ApplicationUtil.getProperty("email.host"));
            javaMailProperties.setProperty("mail.smtps.auth", "true");
            javaMailProperties.setProperty("mail.smtps.ssl.protocols", "TLSv1.2");
            javaMailProperties.setProperty("mail.smtps.socketFactory.port", port);
            javaMailProperties.setProperty("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // 禁用 mail.smtp.* 属性的干扰
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "false");
        } else if("smtp".equals(protocol)) {
            // 启用STARTTLS加密
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        }

        // 将Properties设置到mailSender
        // 这些属性会在建立SMTP连接时被使用
        mailSender.setJavaMailProperties(javaMailProperties);

        // ========== 第6步：初始化SimpleMailMessage ==========
        // SimpleMailMessage用于存储简单邮件的元数据
        // 这里主要设置发件人地址（from）
        // 虽然实际发送使用MimeMessage，但from字段从此处获取
        mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(ApplicationUtil.getProperty("email.from"));

        // ========== 第7步：初始化FreeMarker模板配置 ==========
        // FreeMarkerConfigurer用于配置FreeMarker模板引擎
        // 模板引擎可以将.ftl模板文件与数据结合，生成最终的HTML内容
        freeMarkerConfigurer = new FreeMarkerConfigurer();

        // 设置模板文件加载路径
        // classpath:email/ftl 表示从classpath的email/ftl目录加载模板
        // 例如：src/main/resources/email/ftl/order_confirm.ftl
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:email/ftl");

        // 配置FreeMarker的运行参数
        Properties settings = new Properties();

        // 模板更新检测延迟（秒）
        // 设置为1800秒（30分钟），表示模板文件加载后30分钟内不会检测更新
        // 生产环境建议设置较长的时间以提高性能
        // 开发环境可以设置为较短时间以便修改模板后立即生效
        settings.setProperty("template_update_delay", "1800");

        // 默认字符编码
        // 设置模板文件的默认编码为UTF-8
        // 确保中文模板文件能够正确解析
        settings.setProperty("default_encoding", "UTF-8");

        // 默认区域设置
        // 影响数字、日期等本地化格式的显示
        settings.setProperty("locale", "zh_CN");

        // 将配置应用到FreeMarkerConfigurer
        freeMarkerConfigurer.setFreemarkerSettings(settings);
    }

    /**
     * 发送邮件
     *
     * 本方法是邮件发送的核心入口，支持三种邮件类型：
     * 1. 普通文本邮件：只需设置content
     * 2. HTML邮件：设置ftlname使用FreeMarker模板
     * 3. 带附件邮件：设置file和filename参数
     *
     * @param emailMessage 邮件消息对象，包含以下字段：
     *                    - tomail: 收件人邮箱地址（必填）
     *                    - subject: 邮件主题/标题（必填）
     *                    - content: 邮件正文内容（与ftlname二选一）
     *                    - ftlname: FreeMarker模板文件名（与content二选一）
     *                    - map: 模板参数Map，用于填充模板中的占位符
     *                    - file: 附件文件路径（可选）
     *                    - filename: 附件显示名称（可选）
     *
     * 处理流程：
     * 1. 创建MimeMessage（支持复杂邮件格式的邮件对象）
     * 2. 创建MimeMessageHelper（辅助构建MimeMessage的工具类）
     * 3. 设置收件人、发件人、发送时间、主题
     * 4. 根据是否有模板生成邮件内容
     * 5. 如果有附件，添加附件（解决中文乱码问题）
     * 6. 发送邮件
     *
     * 附件中文乱码解决方案：
     * - 使用MimeUtility.encodeWord()方法编码附件文件名
     * - 该方法会根据文件名内容选择合适的编码（UTF-8或RFC 2047格式）
     * - 示例：filename = "测试文件.pdf" -> "=?UTF-8?B?5rWL6K+V5a2m5ZCrLnBkZg==?="
     *
     * 错误处理：
     * - MessagingException: 邮件消息构建或发送异常
     * - UnsupportedEncodingException: 字符编码不支持异常
     * - 异常会被捕获并记录到日志，但不会向上抛出
     *
     * 日志说明：
     * - 成功时记录邮件内容到info日志
     * - 失败时记录异常堆栈到error日志
     */
    @Override
    public void send(EmailMessage emailMessage) {
        try {
            // 第1步：创建MimeMessage
            // MimeMessage是JavaMail中表示MIME格式邮件的消息类
            // 与SimpleMailMessage不同，MimeMessage支持附件、HTML、多部分邮件等复杂功能
            MimeMessage mailMsg = this.mailSender.createMimeMessage();

            // 第2步：创建MimeMessageHelper
            // MimeMessageHelper是构建MimeMessage的辅助工具类
            // 第二个参数true表示启用了多部分邮件（用于添加附件）
            // 第三个参数"UTF-8"表示邮件内容的默认编码
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMsg, true, "UTF-8");

            // 第3步：设置邮件基本信息
            // 收件人地址
            messageHelper.setTo(emailMessage.getTomail());

            // 发件人地址
            // 从之前初始化的SimpleMailMessage中获取，保持一致性
            messageHelper.setFrom(this.mailMessage.getFrom());

            // 发送时间
            messageHelper.setSentDate(new Date());

            // 邮件主题/标题
            messageHelper.setSubject(emailMessage.getSubject());

            // 第4步：生成邮件内容
            // 根据是否指定了模板文件决定内容生成方式
            if (StringUtils.isNullOrEmpty(emailMessage.getFtlname())) {
                // 没有指定模板，使用普通文本内容
                // setText的第二个参数false表示这是纯文本，不是HTML
                // 如果想发送HTML邮件，应设置为true
                messageHelper.setText(emailMessage.getContent());
            } else {
                // 指定了模板文件，使用FreeMarker生成HTML内容
                // 第二个参数true表示这是HTML格式的邮件
                // getMailText方法会解析.ftl模板并将map中的数据填充到模板中
                messageHelper.setText(this.getMailText(emailMessage.getFtlname(), emailMessage.getMap()), true);
            }

            // 记录邮件内容日志（便于排查问题）
            logger.info("邮件发送内容:" + emailMessage.getContent());

            // 第5步：添加附件
            // 只有当file参数不为null时才添加附件
            // 附件可以是任意类型的文件
            if (emailMessage.getFile() != null) {
                // 创建文件系统资源对象
                // FileSystemResource用于读取服务器本地文件
                FileSystemResource rarfile = new FileSystemResource(emailMessage.getFile());

                // 添加附件的方法：
                // - addAttachment: 作为附件添加，邮件显示附件图标
                // - addInline: 作为内联资源添加，邮件正文中可以直接引用
                // 这里使用addAttachment作为附件形式

                // 使用MimeUtility.encodeWord解决附件名中文乱码问题
                // MimeUtility.encodeWord会自动选择合适的编码方式
                // 对于纯ASCII字符不进行编码
                // 对于非ASCII字符使用RFC 2047格式编码（=?charset?B?base64?=?）
                messageHelper.addAttachment(MimeUtility.encodeWord(emailMessage.getFilename()), rarfile);
            }

            // 第6步：发送邮件
            // send方法会调用SMTP服务器发送邮件
            // 如果连接失败或认证失败，会抛出MessagingException
            this.mailSender.send(mailMsg);

        } catch (MessagingException e) {
            // 邮件消息构建或发送异常
            // 可能的原因：
            // - 无法连接到SMTP服务器
            // - 认证失败（用户名或密码错误）
            // - 收件人地址格式错误
            // - 邮件内容过大超过服务器限制
            logger.error("邮件发送异常:", e);
        } catch (UnsupportedEncodingException e) {
            // 字符编码异常
            // 可能的原因：
            // - 附件文件名包含不支持的字符集
            // - 邮件内容编码与声明的编码不符
            logger.error("邮件发送异常:", e);
        } catch (Exception e) {
            logger.error("邮件发送异常:", e);
        }
    }

    /**
     * 使用FreeMarker模板生成邮件内容
     *
     * 本方法将FreeMarker模板文件与数据模型结合，生成最终的HTML字符串。
     *
     * 模板工作原理：
     * 1. FreeMarker读取.ftl模板文件
     * 2. 模板中包含占位符，如 ${username}、${orderNo}
     * 3. FreeMarker用map中的数据替换占位符
     * 4. 最终生成包含实际数据的HTML字符串
     *
     * 模板语法示例（order_confirm.ftl）：
     * <html>
     * <body>
     *     <h1>尊敬的 ${username}，您好！</h1>
     *     <p>您的订单 ${orderNo} 已确认。</p>
     *     <p>订单金额：${amount} USD</p>
     *     <p>下单时间：${createTime}</p>
     * </body>
     * </html>
     *
     * @param ftlname 模板文件名（不含路径）
     *                应放置在 classpath:email/ftl/ 目录下
     *                例如：ftlname = "order_confirm.ftl"
     * @param map 数据模型Map，key为模板中的变量名，value为实际数据
     *            例如：map.put("username", "张三")
     *            模板中使用：${username}
     * @return 渲染后的HTML字符串
     *         如果渲染失败，返回空字符串""
     *
     * 异常处理：
     * - IOException: 模板文件读取失败
     * - TemplateException: 模板语法错误或数据填充失败
     * - 异常会被捕获并打印堆栈，但不会向上抛出
     *
     * 性能考虑：
     * - FreeMarker会在内存中缓存已解析的模板
     * - template_update_delay控制缓存更新时间
     * - 生产环境建议保持较长更新间隔
     */
    private String getMailText(String ftlname, Map<String, Object> map) {
        // 初始化返回字符串
        String html = "";

        try {
            // 第1步：获取模板实例
            // getConfiguration()返回FreeMarker的配置对象
            // getTemplate()根据文件名加载模板
            // FreeMarker会自动在之前设置的templateLoaderPath下查找文件
            Template tpl = this.freeMarkerConfigurer.getConfiguration().getTemplate(ftlname);

            // 第2步：渲染模板
            // processTemplateIntoString方法：
            // - 第一个参数：模板对象
            // - 第二个参数：数据模型（Map）
            // - 返回值：渲染后的字符串
            html = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);

        } catch (IOException e) {
            // 模板文件读取失败
            // 可能原因：
            // - 模板文件不存在
            // - 模板路径配置错误
            // - 文件读取权限不足
            e.printStackTrace();
        } catch (TemplateException e) {
            // 模板处理失败
            // 可能原因：
            // - 模板语法错误
            // - Map中缺少模板需要的变量
            // - 变量类型不匹配（如期望字符串但传入数字）
            e.printStackTrace();
        }

        // 返回渲染后的HTML内容
        // 如果渲染过程中发生异常，此时html仍为空字符串""
        return html;
    }
}
