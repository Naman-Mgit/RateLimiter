package com.example.RateLimiter.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RateLimiterService {

    private static final int BUCKET_CAPACITY = 5;

    // 1 token added every second
    private static final double REFILL_RATE = 1.0;

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized boolean allowRequest(String apiKey) {

        String redisKey = "rate_limit:" + apiKey;

        // Get bucket data from Redis
        Map<Object, Object> bucketData =
                redisTemplate.opsForHash().entries(redisKey);

        double tokens;
        long lastRefillTime;

        // First request for this API key
        if (bucketData.isEmpty()) {

            tokens = BUCKET_CAPACITY;
            lastRefillTime = System.currentTimeMillis();

        } else {

            tokens = Double.parseDouble(
                    bucketData.get("tokens").toString()
            );

            lastRefillTime = Long.parseLong(
                    bucketData.get("lastRefillTime").toString()
            );
        }

        // Refill tokens
        long currentTime = System.currentTimeMillis();

        long timePassed = currentTime - lastRefillTime;

        double tokensToAdd =
                (timePassed / 1000.0) * REFILL_RATE;

        if (tokensToAdd > 0) {

            tokens = Math.min(
                    BUCKET_CAPACITY,
                    tokens + tokensToAdd
            );

            lastRefillTime = currentTime;
        }

        // Try to consume one token
        boolean allowed = false;

        if (tokens >= 1) {

            tokens--;
            allowed = true;
        }

        // Save updated state back to Redis
        redisTemplate.opsForHash().putAll(
                redisKey,
                Map.of(
                        "tokens", String.valueOf(tokens),
                        "lastRefillTime",
                        String.valueOf(lastRefillTime)
                )
        );

        return allowed;
    }


    public int getRemainingTokens(String apiKey) {

        String redisKey = "rate_limit:" + apiKey;

        Object tokens =
                redisTemplate.opsForHash()
                        .get(redisKey, "tokens");

        // No bucket yet → full capacity
        if (tokens == null) {
            return BUCKET_CAPACITY;
        }

        return (int) Double.parseDouble(
                tokens.toString()
        );
    }
}