package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: 44219
 * @since: 2026/10/28 11:40
 * @description:none
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // 和工厂对象链接
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // key 使用字符串序列化器，Redis 中可直接看到 status 这样的键名
        // 设置序列化器 : java —> redis 中会把数据进行序列化处理
        // 此处未给value设置序列化器 set("javaKey","卢本伟") -> redis中value = ��t	卢本伟
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // value 也按字符串保存，店铺状态会存为 "0" 或 "1"
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
