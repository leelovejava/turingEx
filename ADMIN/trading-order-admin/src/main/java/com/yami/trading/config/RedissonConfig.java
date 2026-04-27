package com.yami.trading.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class RedissonConfig {
    @Value("${spring.redis.redisson.config}")
    private Resource configFile;
    @Value("${spring.redis.redisson.config-spider}")
    private Resource configFile8;
    @Bean(destroyMethod = "shutdown")
    @Primary
    public RedissonClient redissonClientDb() throws IOException {
        Config config = Config.fromYAML(configFile.getInputStream());
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClientSpider() throws IOException {
        Config config = Config.fromYAML(configFile8.getInputStream());
        return Redisson.create(config);
    }
}