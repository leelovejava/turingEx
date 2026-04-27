package com.yami.trading.common.config;

import com.yami.trading.common.serializer.redis.KryoRedisSerializer;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * redis
 *
 * @author
 */
@EnableCaching
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisCacheConfig2 {
    private String host;
    private int port;
    private String password;

    @Bean(name = "redisTemplate2")
    public RedisTemplate<String, Object> redisTemplate2() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(RedisPassword.of(password));
        configuration.setDatabase(6);
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(genericObjectPoolConfig);
        builder.commandTimeout(Duration.ofSeconds(15));
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                configuration, builder.build()
        );
        connectionFactory.afterPropertiesSet();
        return createRedisTemplate(connectionFactory);
    }

    private RedisTemplate createRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate redisTemplate = new RedisTemplate();
        KryoRedisSerializer kryoRedisSerializer = new KryoRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(kryoRedisSerializer);
        redisTemplate.setHashValueSerializer(kryoRedisSerializer);
        redisTemplate.setEnableTransactionSupport(false);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
