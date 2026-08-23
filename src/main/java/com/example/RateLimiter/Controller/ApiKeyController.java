package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Service.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    public ResponseEntity<ApiKey> createApiKey(
            @RequestParam Long userId
    ) {

        ApiKey apiKey =
                apiKeyService.createApiKey(userId);

        return ResponseEntity.ok(apiKey);
    }
}