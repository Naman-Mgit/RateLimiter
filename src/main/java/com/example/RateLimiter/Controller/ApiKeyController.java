package com.example.RateLimiter.Controller;

import com.example.RateLimiter.DTO.ApiKeyResponse;
import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Service.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createApiKey(
            @RequestParam Long userId
    ) {
        ApiKey apiKey = apiKeyService.createApiKey(userId);

        return ResponseEntity.ok(convertToResponse(apiKey));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getApiKeys(
            @RequestParam Long userId
    ) {
        List<ApiKey> apiKeys =
                apiKeyService.getApiKeysByUser(userId);

        List<ApiKeyResponse> response = new ArrayList<>();

        for (ApiKey apiKey : apiKeys) {

            ApiKeyResponse dto =
                    convertToResponse(apiKey);

            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<String> revokeApiKey(
            @PathVariable Long apiKeyId
    ) {
        apiKeyService.revokeApiKey(apiKeyId);

        return ResponseEntity.ok("API key revoked successfully");
    }

    private ApiKeyResponse convertToResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getKeyValue(),
                apiKey.getUser().getId()
        );
    }
}