package com.example.RateLimiter.Service;

import com.example.RateLimiter.Model.TokenBucket;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {

    private static final int BUCKET_CAPACITY = 5;

    // 1 token added every second
    private static final double REFILL_RATE = 1.0;

    // Each API key gets its own bucket
    private final Map<String, TokenBucket> apiKeyBuckets =
            new HashMap<>();

    public synchronized boolean allowRequest(String apiKey) {

        apiKeyBuckets.putIfAbsent(
                apiKey,
                new TokenBucket(
                        BUCKET_CAPACITY,
                        REFILL_RATE
                )
        );

        TokenBucket bucket = apiKeyBuckets.get(apiKey);

        return bucket.tryConsume();
    }

    public int getRemainingTokens(String apiKey) {

        TokenBucket bucket = apiKeyBuckets.get(apiKey);

        if (bucket == null) {
            return BUCKET_CAPACITY;
        }

        return bucket.getRemainingToken();
    }
}