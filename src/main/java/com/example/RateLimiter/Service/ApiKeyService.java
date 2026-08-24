package com.example.RateLimiter.Service;

import com.example.RateLimiter.Exception.ApiKeyLimitExceededException;
import com.example.RateLimiter.Exception.ApiKeyNotFoundException;
import com.example.RateLimiter.Exception.UserNotFoundException;
import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Repository.ApiKeyRepository;
import com.example.RateLimiter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ApiKey createApiKey(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        long existingKeyCount =
                apiKeyRepository.countByUserId(userId);

        if (existingKeyCount >= MAX_API_KEYS_PER_USER) {
            throw new ApiKeyLimitExceededException(
                    "Maximum of 2 API keys allowed per user"
            );
        }

        String keyValue = UUID.randomUUID().toString();

        ApiKey apiKey = new ApiKey(keyValue, user);

        return apiKeyRepository.save(apiKey);
    }


    public boolean isValidApiKey(String keyValue) {
        return apiKeyRepository.findByKeyValue(keyValue).isPresent();
    }


    public List<ApiKey> getApiKeysByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return apiKeyRepository.findByUser(user);
    }


    public void revokeApiKey(Long apiKeyId) {

        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() ->
                        new ApiKeyNotFoundException("API key not found"));

        apiKeyRepository.delete(apiKey);
    }
}