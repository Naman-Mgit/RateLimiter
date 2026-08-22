package com.example.RateLimiter.Service;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    int requestCount=0;

    private final int maxRequest=5;

    public boolean allowRequest(){
        if(requestCount < maxRequest){
            requestCount++;
            return  true;
        }
        return false;
    }

    public int getRemainingRequest(){
        return maxRequest-requestCount;
    }
}
