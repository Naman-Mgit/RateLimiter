package com.example.RateLimiter.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public RedisTestService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public String testRedis() {

        redisTemplate.opsForValue().set(
                "test-key",
                "Hello from Spring Boot"
        );

        return redisTemplate.opsForValue().get(
                "test-key"
        );
    }
}