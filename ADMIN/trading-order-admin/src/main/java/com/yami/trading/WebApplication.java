package com.yami.trading;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.yami.trading.service.impl.InternalEmailSenderServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
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
@EnableMethodCache(basePackages = "com.yami.trading")
public class WebApplication extends SpringBootServletInitializer {

    @PostConstruct
    void init() {
        // 设置JVM默认时区为美国东部时间（New York）
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
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
