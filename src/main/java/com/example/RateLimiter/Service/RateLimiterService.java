package com.example.RateLimiter.Service;

import com.example.RateLimiter.Model.UserRateLimit;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {

    int requestCount=0;

    private static final int maxRequest=5;

    // 1 minute in ms
    private static final long WINDOW_SIZE = 60 * 1000;

    private  final Map<String, UserRateLimit> userLimits=new HashMap<>();

    private long windowStartTime = System.currentTimeMillis();

    public boolean allowRequest(String userId){
        long currentTime = System.currentTimeMillis();


        userLimits.putIfAbsent(userId, new UserRateLimit());

        UserRateLimit userRateLimit = userLimits.get(userId);

        if (currentTime - userRateLimit.getWindowStartTime() >= WINDOW_SIZE) {

            userRateLimit.reset(currentTime);

            System.out.println("Rate limit reset for user: " + userId);
        }

        if(userRateLimit.getRequestCount() < maxRequest){
             userRateLimit.incrementRequestCount();
             return true;
        }

        return false;
    }

    public int getRemainingRequest(String userId){


        UserRateLimit userRateLimit = userLimits.get(userId);



        // If the window has expired, technically all requests are available
        if (userRateLimit == null) {
            return maxRequest;
        }

        return maxRequest-userRateLimit.getRequestCount();
    }
}
