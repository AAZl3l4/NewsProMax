package com.AAZl3l4.common.configuration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //设置redis key的序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); //设置redis hashkey的序列化器
        // value的序列化器不需要设置 获取时反序列化就改回来了
        redisTemplate.setConnectionFactory(connectionFactory);  //设置redis的连接工厂对象
        return redisTemplate;
    }
}