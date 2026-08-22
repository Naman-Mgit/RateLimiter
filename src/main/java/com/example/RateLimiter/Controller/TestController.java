package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Service.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final RateLimiterService rateLimiterService;

    public TestController(RateLimiterService rateLimiterService){
         this.rateLimiterService=rateLimiterService;
    }

    @GetMapping("/api/hello")

    public ResponseEntity<String> hello(){
        boolean allowed=rateLimiterService.allowRequest();

        if(!allowed){
            return  ResponseEntity.status(429).body("Rate limit exceeded ! Try again later");
        }

        int remaining= rateLimiterService.getRemainingRequest();
        return ResponseEntity.ok("Hello ! Request Allowed. Remaining Request: "+remaining);
    }

}
