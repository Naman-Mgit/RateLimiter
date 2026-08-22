package com.example.RateLimiter.Model;

//Store rate limiting information for single user counter is not shared
public class UserRateLimit {

    private int requestCount;
    private long windowStartTime;

    public UserRateLimit() {
        this.requestCount = 0;
        this.windowStartTime = System.currentTimeMillis();
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void reset(long currentTime) {
        this.requestCount = 0;
        this.windowStartTime = currentTime;
    }

    public long getWindowStartTime() {
        return windowStartTime;
    }

}
