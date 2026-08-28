package com.example.RateLimiter.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RateLimiterService {

    private static final int BUCKET_CAPACITY = 5;

    private static final double REFILL_RATE = 1.0;

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<List> rateLimiterScript;

    public RateLimiterService(
            StringRedisTemplate redisTemplate,
            DefaultRedisScript<List> rateLimiterScript
    ) {
        this.redisTemplate = redisTemplate;
        this.rateLimiterScript = rateLimiterScript;
    }

    public boolean allowRequest(String apiKey) {

        String redisKey = "rate_limit:" + apiKey;

        long currentTime = System.currentTimeMillis();

        List result = redisTemplate.execute(
                rateLimiterScript,

                Collections.singletonList(redisKey),

                String.valueOf(BUCKET_CAPACITY),
                String.valueOf(REFILL_RATE),
                String.valueOf(currentTime)
        );

        if (result == null || result.isEmpty()) {
            return false;
        }

        Long allowed = (Long) result.get(0);

        return allowed == 1;
    }


    public int getRemainingTokens(String apiKey) {

        String redisKey = "rate_limit:" + apiKey;

        Object tokens =
                redisTemplate.opsForHash()
                        .get(redisKey, "tokens");

        if (tokens == null) {
            return BUCKET_CAPACITY;
        }

        return (int) Double.parseDouble(
                tokens.toString()
        );
    }
}