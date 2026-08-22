package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Service.ApiKeyService;
import com.example.RateLimiter.Service.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private  final RateLimiterService rateLimiterService;
    private  final ApiKeyService apiKeyService;


    public  ApiController(RateLimiterService rateLimiterService,ApiKeyService apiKeyService){
         this.rateLimiterService=rateLimiterService;
         this.apiKeyService=apiKeyService;
    }

    @GetMapping("/api/resource")
    public ResponseEntity<String> getResource(@RequestHeader(value="X-API-KEY",required = false) String apiKey){

        if(apiKey == null || !apiKeyService.isValidApiKey(apiKey)){
            return ResponseEntity
                    .status(401)
                    .body("Invalid or missing API key");
        }

        String userId=apiKeyService.getUserIdFromApiKey(apiKey);

        boolean allowed = rateLimiterService.allowRequest(userId);

        if(!allowed){
            return ResponseEntity
                    .status(429)
                    .body("Rate limit exceeded. Please wait for tokens to refill.");
        }

        int remainingTokens = rateLimiterService.getRemainingRequests(userId);

        return ResponseEntity.ok(
                "Protected resource accessed successfully! "
                        + "User: " + userId
                        + ", Remaining tokens: " + remainingTokens
        );
    }

}
