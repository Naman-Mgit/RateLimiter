package com.example.RateLimiter.Exception;

public class ApiKeyLimitExceededException extends  RuntimeException{

    public ApiKeyLimitExceededException(String message) {
        super(message);
    }
}
