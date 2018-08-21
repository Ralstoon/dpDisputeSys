package com.seu.config;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.seu.common.RedisConstant;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RedisCacheConfig
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/21 13:46
 * @Version 1.0
 **/

@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {
//    @Value("${redis-timeout}")
//    private Long redisTimeout;

    //缓存管理器
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间RedisConstant.EXPIRE
        cacheManager.setDefaultExpiration(RedisConstant.EXPIRE);
        Map<String,Long> expiresMap=new HashMap<>();
        expiresMap.put("constantData",(long)RedisConstant.EXPIRE);
        cacheManager.setExpires(expiresMap);
        return cacheManager;
    }



}
