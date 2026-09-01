package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Service.ApiKeyService;
import com.example.RateLimiter.Service.RateLimiterService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.RateLimiter.DTO.RateLimitResponse;

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
    public ResponseEntity<RateLimitResponse> getResource(
            @RequestHeader(value = "X-API-KEY", required = false)
            String apiKey
    ) {
        // 1. Check API key exists
        if (apiKey == null || apiKey.isBlank()) {

            return ResponseEntity
                    .status(401)
                    .body(
                            new RateLimitResponse(
                                    "API key is missing",
                                    0,
                                    5
                            )
                    );
        }

        // 2. Validate API key
        if (!apiKeyService.isValidApiKey(apiKey)) {

            return ResponseEntity
                    .status(401)
                    .body(
                            new RateLimitResponse(
                                    "Invalid API key",
                                    0,
                                    5
                            )
                    );
        }

        // 3. Rate limiting
        boolean allowed =
                rateLimiterService.allowRequest(apiKey);

        // 4. Request rejected
        if (!allowed) {

            return ResponseEntity
                    .status(429)
                    .body(
                            new RateLimitResponse(
                                    "Rate limit exceeded. Please wait for tokens to refill.",
                                    0,
                                    5
                            )
                    );
        }

        // 5. Get remaining tokens
        int remainingTokens =
                rateLimiterService.getRemainingTokens(apiKey);

        return ResponseEntity.ok(
                new RateLimitResponse(
                        "Protected resource accessed successfully!",
                        remainingTokens,
                        5
                )
        );
     

    }
}