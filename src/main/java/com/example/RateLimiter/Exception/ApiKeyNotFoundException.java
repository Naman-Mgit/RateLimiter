package com.example.RateLimiter.Exception;

public class ApiKeyNotFoundException extends  RuntimeException {
    public  ApiKeyNotFoundException(String message){
         super(message);
    }

}
