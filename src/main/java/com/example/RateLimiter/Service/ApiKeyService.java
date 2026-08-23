package com.example.RateLimiter.Service;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Repository.ApiKeyRepository;
import com.example.RateLimiter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    public ApiKeyService(
            ApiKeyRepository apiKeyRepository,
            UserRepository userRepository
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }

    public ApiKey createApiKey(Long userId) {

        // 1. Find user in database
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // 2. Generate a random API key
        String keyValue = UUID.randomUUID().toString();

        // 3. Create API key object
        ApiKey apiKey = new ApiKey(keyValue, user);

        // 4. Save it in MySQL
        return apiKeyRepository.save(apiKey);
    }


    public boolean isValidApiKey(String keyValue) {

        return apiKeyRepository
                .findByKeyValue(keyValue)
                .isPresent();
    }


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
}