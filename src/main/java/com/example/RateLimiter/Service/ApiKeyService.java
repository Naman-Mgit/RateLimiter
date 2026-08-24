package com.example.RateLimiter.Service;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Repository.ApiKeyRepository;
import com.example.RateLimiter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    private static final int MAX_API_KEYS_PER_USER = 2;

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    public ApiKeyService(
            ApiKeyRepository apiKeyRepository,
            UserRepository userRepository
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }


    // CREATE API KEY
    public ApiKey createApiKey(Long userId) {

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Count existing API keys
        long existingKeyCount =
                apiKeyRepository.countByUserId(userId);

        // Check limit
        if (existingKeyCount >= MAX_API_KEYS_PER_USER) {
            throw new RuntimeException(
                    "Maximum of " + MAX_API_KEYS_PER_USER
                            + " API keys allowed per user"
            );
        }

        // Generate random key
        String keyValue = UUID.randomUUID().toString();

        // Create API key
        ApiKey apiKey = new ApiKey(keyValue, user);

        // Save to database
        return apiKeyRepository.save(apiKey);
    }


    // VALIDATE API KEY
    public boolean isValidApiKey(String keyValue) {

        return apiKeyRepository
                .findByKeyValue(keyValue)
                .isPresent();
    }


    // GET USER ID FROM API KEY
    public String getUserIdFromApiKey(String keyValue) {

        Optional<ApiKey> apiKey =
                apiKeyRepository.findByKeyValue(keyValue);

        if (apiKey.isEmpty()) {
            return null;
        }

        return apiKey.get()
                .getUser()
                .getId()
                .toString();
    }


    // GET ALL API KEYS OF A USER
    public List<ApiKey> getApiKeysByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return apiKeyRepository.findByUser(user);
    }


    // REVOKE API KEY
    public void revokeApiKey(Long apiKeyId) {

        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() ->
                        new RuntimeException("API key not found")
                );

        apiKeyRepository.delete(apiKey);
    }
}