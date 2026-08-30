package com.example.RateLimiter.DTO;

import java.time.LocalDateTime;

public class ApiKeyResponse {

    private Long id;
    private String keyValue;
    private Long userId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;

    public ApiKeyResponse(
            Long id,
            String keyValue,
            Long userId,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime lastUsedAt
    ) {
        this.id = id;
        this.keyValue = keyValue;
        this.userId = userId;
        this.active = active;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
}