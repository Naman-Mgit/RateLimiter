package com.example.RateLimiter.Model;

public class TokenBucket {

    private  final int capacity;

    private  final  double refillrate;

    private double tokens;

    private  long lastRefillTime;

    public  TokenBucket(int capacity,double refillrate){
        this.capacity=capacity;
        this.refillrate=refillrate;

        this.tokens=capacity;
        this.lastRefillTime=System.currentTimeMillis();
    }

    public synchronized  boolean tryConsume(){
        refill();

        if(tokens >= 1){
             tokens--;
             return true;
        }
        return false;
    }
    private  void refill(){
        long currentTime=System.currentTimeMillis();

        long timePassed=currentTime-lastRefillTime;

        double tokensToAdd=(timePassed/1000.0)*refillrate;

        if(tokensToAdd > 0){
             tokens=Math.min(capacity,tokens+tokensToAdd);

             lastRefillTime=currentTime;
        }

    }
    public synchronized int getRemainingToken(){
        refill();

        return (int) tokens;
    }

}

