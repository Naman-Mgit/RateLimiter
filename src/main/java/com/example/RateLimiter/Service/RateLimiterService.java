package com.example.RateLimiter.Service;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    int requestCount=0;

    private final int maxRequest=5;

    // 1 minute in ms
    private static final long WINDOW_SIZE = 60 * 1000;

    private long windowStartTime = System.currentTimeMillis();

    public boolean allowRequest(){
        long currentTime = System.currentTimeMillis();

        if (currentTime - windowStartTime >= WINDOW_SIZE) {

            // Start a new window
            requestCount = 0;
            windowStartTime = currentTime;

            System.out.println("Rate limit window reset!");
        }
        if(requestCount < maxRequest){
            requestCount++;
            return  true;
        }
        return false;
    }

    public int getRemainingRequest(){
        long currentTime = System.currentTimeMillis();

        // If the window has expired, technically all requests are available
        if (currentTime - windowStartTime >= WINDOW_SIZE) {
            return maxRequest;
        }
        return maxRequest-requestCount;
    }
}
