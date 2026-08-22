package com.example.RateLimiter.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiKeyService {

    private final Map<String, String> apiKeys = new HashMap<>();

    public ApiKeyService() {
        // Temporary hardcoded API keys
        apiKeys.put("naman-secret-key", "naman");
        apiKeys.put("rahul-secret-key", "rahul");
        apiKeys.put("test-secret-key", "test-user");
    }

    public String getUserIdFromApiKey(String apiKey) {
        return apiKeys.get(apiKey);
    }

    public boolean isValidApiKey(String apiKey) {
        return apiKeys.containsKey(apiKey);
    }

}
