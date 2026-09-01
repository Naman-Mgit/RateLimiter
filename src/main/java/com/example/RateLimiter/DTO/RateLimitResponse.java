package com.example.RateLimiter.DTO;

public class RateLimitResponse {

    private String message;
    private int remainingTokens;
    private int capacity;

    public RateLimitResponse(
            String message,
            int remainingTokens,
            int capacity
    ) {
        this.message = message;
        this.remainingTokens = remainingTokens;
        this.capacity = capacity;
    }

    public String getMessage() {
        return message;
    }

    public int getRemainingTokens() {
        return remainingTokens;
    }

    public int getCapacity() {
        return capacity;
    }
}