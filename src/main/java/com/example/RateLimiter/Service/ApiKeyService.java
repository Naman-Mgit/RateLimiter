package com.example.RateLimiter.Service;

import com.example.RateLimiter.Exception.ApiKeyLimitExceededException;
import com.example.RateLimiter.Exception.ApiKeyNotFoundException;
import com.example.RateLimiter.Exception.UserNotFoundException;
import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Repository.ApiKeyRepository;
import com.example.RateLimiter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void updateLastUsed(String keyValue) {

        ApiKey apiKey = apiKeyRepository
                .findByKeyValue(keyValue)
                .orElseThrow(() ->
                        new ApiKeyNotFoundException(
                                "API key not found"
                        )
                );

        apiKey.setLastUsedAt(LocalDateTime.now());

        apiKeyRepository.save(apiKey);
    }
    public boolean isValidApiKey(String keyValue) {
        ApiKey apiKey = apiKeyRepository
                .findByKeyValue(keyValue)
                .orElse(null);

        if (apiKey == null) {
            return false;
        }

        // Check whether manually revoked
        if (!apiKey.isActive()) {
            return false;
        }

        // Check expiration
        if (apiKey.getExpiresAt() != null &&
                apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {

            // Automatically deactivate expired key
            apiKey.setActive(false);

            apiKeyRepository.save(apiKey);

            return false;
        }

        return true;
    }


    public List<ApiKey> getApiKeysByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return apiKeyRepository.findByUser(user);
    }


    public void revokeApiKey(
            Long apiKeyId,
            Long userId
    ) {

        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() ->
                        new ApiKeyNotFoundException(
                                "API key not found"
                        )
                );

        if (!apiKey.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not allowed to revoke this API key"
            );
        }

        apiKey.setActive(false);

        apiKeyRepository.save(apiKey);
    }
}