package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Service.RedisTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    private final RedisTestService redisTestService;

    public RedisTestController(
            RedisTestService redisTestService
    ) {
        this.redisTestService = redisTestService;
    }

    @GetMapping("/test")
    public String testRedis() {
        return redisTestService.testRedis();
    }
}