package com.yami.trading;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.service.impl.InternalEmailSenderServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author lgh
 */
@EnableRetry
@EnableScheduling
@SpringBootApplication
@ComponentScan("com.yami.trading")
@EnableMethodCache(basePackages = "com.yami.trading")
public class WebApplication extends SpringBootServletInitializer {

    @PostConstruct
    void init() {
        // 配置时区
        String timeZone = ApplicationUtil.getProperty("config.timezone.record", "GMT+8");
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebApplication.class);
    }

    @Bean
    public InternalEmailSenderServiceImpl internalEmailSenderService(){
        return  new InternalEmailSenderServiceImpl();
    }

    @Bean
    public EmailServer emailServer(){
        return  new EmailServer();
    }

    @Bean
    public SmsServer  smsServer(){
        return  new SmsServer();
    }
}
