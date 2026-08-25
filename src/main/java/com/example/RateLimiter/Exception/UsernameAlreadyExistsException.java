package com.example.RateLimiter.Exception;

public class UsernameAlreadyExistsException extends  RuntimeException{
    public UsernameAlreadyExistsException(String message){
         super(message);
    }
}
