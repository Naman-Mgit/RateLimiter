package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Service.ApiKeyService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }


    // CREATE API KEY
    @PostMapping
    public ResponseEntity<ApiKey> createApiKey(
            @RequestParam Long userId
    ) {

        ApiKey apiKey =
                apiKeyService.createApiKey(userId);

        return ResponseEntity.ok(apiKey);
    }


    // GET ALL API KEYS FOR A USER
    @GetMapping
    public ResponseEntity<List<ApiKey>> getApiKeys(
            @RequestParam Long userId
    ) {

        List<ApiKey> apiKeys =
                apiKeyService.getApiKeysByUser(userId);

        return ResponseEntity.ok(apiKeys);
    }


    // REVOKE AN API KEY
    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<String> revokeApiKey(
            @PathVariable Long apiKeyId
    ) {

        apiKeyService.revokeApiKey(apiKeyId);

        return ResponseEntity.ok("API key revoked successfully");
    }
}