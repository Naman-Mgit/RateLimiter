package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Service.ApiKeyService;
import com.example.RateLimiter.Service.RateLimiterService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final RateLimiterService rateLimiterService;
    private final ApiKeyService apiKeyService;

    public ApiController(
            RateLimiterService rateLimiterService,
            ApiKeyService apiKeyService
    ) {
        this.rateLimiterService = rateLimiterService;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("/api/resource")
    public ResponseEntity<String> getResource(
            @RequestHeader(value = "X-API-KEY", required = false)
            String apiKey
    ) {

        System.out.println("========== RESOURCE CONTROLLER REACHED ==========");
        // 1. Check whether API key is present
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity
                    .status(401)
                    .body("API key is missing");
        }

        // 2. Validate API key from MySQL
        if (!apiKeyService.isValidApiKey(apiKey)) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid API key");
        }
        apiKeyService.updateLastUsed(apiKey);

       
        // 3. Apply rate limiting directly using API key
        boolean allowed = rateLimiterService.allowRequest(apiKey);

        if (!allowed) {
            int remainingTokens =
                    rateLimiterService.getRemainingTokens(apiKey);

            return ResponseEntity
                    .status(429)
                    .header(
                            "X-RateLimit-Limit",
                            String.valueOf(
                                    rateLimiterService.getBucketCapacity()
                            )
                    )
                    .header(
                            "X-RateLimit-Remaining",
                            String.valueOf(remainingTokens)
                    )
                    .header(
                            "Retry-After",
                            "1"
                    )
                    .body(
                            "Rate limit exceeded. Please wait for tokens to refill."
                    );
        }

        // 4. Get remaining tokens for this API key
        int remainingTokens =
                rateLimiterService.getRemainingTokens(apiKey);

        return ResponseEntity.ok()
                .header(
                        "X-RateLimit-Limit",
                        String.valueOf(
                                rateLimiterService.getBucketCapacity()
                        )
                )
                .header(
                        "X-RateLimit-Remaining",
                        String.valueOf(remainingTokens)
                )
                .body(
                        "Protected resource accessed successfully!"
                );
    }
}