package com.example.RateLimiter.Service;

import com.example.RateLimiter.Model.TokenBucket;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {

     private  static  final  int BUCKET_CAPACITY=5;

     //1 token will get added every second

     private  static  final double REFILL_RATE=1.0;

    private final Map<String, TokenBucket> userBuckets =
            new HashMap<>();

    public synchronized boolean allowRequest(String userId){
       userBuckets.putIfAbsent(userId,new TokenBucket(BUCKET_CAPACITY,REFILL_RATE));

       TokenBucket bucket = userBuckets.get(userId);

       return bucket.tryConsume();
    }

    public int getRemainingRequests(String userId){

        TokenBucket bucket = userBuckets.get(userId);

        if (bucket == null) {
            return BUCKET_CAPACITY;
        }

        return bucket.getRemainingToken();
    }
}
